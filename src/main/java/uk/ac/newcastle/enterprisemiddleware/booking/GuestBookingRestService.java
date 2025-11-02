//Part 2

package uk.ac.newcastle.enterprisemiddleware.booking;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.customer.CustomerService;
import uk.ac.newcastle.enterprisemiddleware.hotel.Hotel;
import uk.ac.newcastle.enterprisemiddleware.hotel.HotelRepository;
import uk.ac.newcastle.enterprisemiddleware.util.RestServiceException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction; // Import the JTA UserTransaction
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Path("/guestbookings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GuestBookingRestService {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    CustomerService customerService;

    @Inject
    BookingService bookingService;

    @Inject
    HotelRepository hotelRepository; // We need this to re-hydrate the Hotel object

    @Inject
    UserTransaction userTransaction; // Inject the JTA UserTransaction

    @POST
    @Operation(summary = "Create a new Customer and Booking in a single transaction",
            description = "Creates a new Customer and a new Booking from the supplied JSON object. " +
                    "If either operation fails, the entire transaction is rolled back.")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Guest booking created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Booking.class))),
            @APIResponse(responseCode = "400", description = "Invalid data supplied (e.g., validation failure)"),
            @APIResponse(responseCode = "409", description = "Data conflicts with existing records (e.g., email or booking)"),
            @APIResponse(responseCode = "500", description = "An unexpected server error occurred")
    })
    public Response createGuestBooking(
            @Parameter(description = "JSON representation of GuestBooking object", required = true)
            GuestBooking guestBooking) {

        if (guestBooking == null || guestBooking.getCustomer() == null || guestBooking.getBooking() == null) {
            throw new RestServiceException("Bad Request: Invalid GuestBooking data provided", Response.Status.BAD_REQUEST);
        }

        try {
            // 1. Begin the manual transaction
            userTransaction.begin();

            // 2. Create the Customer
            Customer customer = guestBooking.getCustomer();
            customerService.createCustomer(customer); // This will persist the customer
            log.info("GuestBooking: Created customer " + customer.getId());

            // 3. Prepare the Booking
            Booking booking = guestBooking.getBooking();

            // 3a. Re-hydrate the Hotel object (like in BookingRestService)
            if (booking.getHotel() == null || booking.getHotel().getId() == null) {
                throw new ValidationException("Hotel ID must be provided");
            }
            Hotel hotel = hotelRepository.findById(booking.getHotel().getId());
            if (hotel == null) {
                throw new ValidationException("Invalid Hotel ID: No hotel found");
            }
            booking.setHotel(hotel);

            // 3b. Set the newly created Customer on the Booking
            booking.setCustomer(customer);

            // 4. Create the Booking
            bookingService.createBooking(booking); // This will persist the booking
            log.info("GuestBooking: Created booking " + booking.getId());

            // 5. If everything succeeded, commit the transaction
            userTransaction.commit();

            return Response.status(Response.Status.CREATED).entity(booking).build();

        } catch (ConstraintViolationException e) {
            // Handle bean validation issues
            rollbackTransaction(); // Rollback on failure
            Map<String, String> responseObj = new HashMap<>();
            for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request: Validation failed", responseObj, Response.Status.BAD_REQUEST, e);

        } catch (ValidationException e) {
            // Handle our custom validation (e.g., email exists, booking exists, hotel/customer not found)
            rollbackTransaction(); // Rollback on failure
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("validation", e.getMessage());
            throw new RestServiceException("Bad Request: Invalid data", responseObj, Response.Status.BAD_REQUEST, e);

        } catch (Exception e) {
            // Handle all other exceptions (including transaction commit failure)
            rollbackTransaction(); // Rollback on failure
            log.severe("Error during guest booking: " + e.getMessage());
            throw new RestServiceException(e);
        }
    }

    /**
     * Helper method to safely roll back the transaction.
     */
    private void rollbackTransaction() {
        try {
            if (userTransaction != null) {
                userTransaction.rollback();
                log.info("Transaction rolled back.");
            }
        } catch (SystemException se) {
            log.severe("Failed to roll back transaction: " + se.getMessage());
        }
    }
}
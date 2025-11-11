package uk.ac.newcastle.enterprisemiddleware.booking;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import uk.ac.newcastle.enterprisemiddleware.util.RestServiceException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

//Part 2

import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.customer.CustomerRepository;
import uk.ac.newcastle.enterprisemiddleware.hotel.Hotel;
import uk.ac.newcastle.enterprisemiddleware.hotel.HotelRepository;

// End of part 2

@Path("/bookings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BookingRestService {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    BookingService bookingService;

    @Inject
    CustomerRepository customerRepository;

    @Inject
    HotelRepository hotelRepository;

    @GET
    @Operation(summary = "Fetch all Bookings for a Customer", description = "Returns a JSON array of Booking objects, filtered by customerId.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Bookings retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Booking[].class))),
            @APIResponse(responseCode = "400", description = "customerId query parameter is required")
    })
    public Response getBookings(@Parameter(description = "ID of the Customer to filter bookings for", required = true) @QueryParam("customerId") Long customerId) {

        if (customerId == null) {
            throw new RestServiceException("customerId query parameter is required", Response.Status.BAD_REQUEST);
        }

        log.info("GET /bookings requested for customerId " + customerId);
        List<Booking> bookings = bookingService.findBookingsByCustomerId(customerId);
        log.info("GET /bookings completed");
        return Response.ok(bookings).build();
    }

    @POST
    @Operation(summary = "Create a new Booking", description = "Creates a new Booking from the supplied JSON object.")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Booking created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Booking.class))),
            @APIResponse(responseCode = "400", description = "Invalid Booking data supplied (e.g., date in past, invalid customer/hotel)"),
            @APIResponse(responseCode = "409", description = "A booking for this hotel and date already exists"),
            @APIResponse(responseCode = "500", description = "An unexpected server error occurred")
    })
    public Response createBooking(
            @Parameter(description = "JSON representation of Booking object to be added", required = true)
            Booking booking) {

        log.info("POST /bookings requested");

        if (booking == null) {
            throw new RestServiceException("Bad Request: No booking data provided", Response.Status.BAD_REQUEST);
        }
        // Part 2 Rehydration logic
        // Incoming JSON will have customer/hotel with only ID.
        try {

            booking.setId(null);

            if (booking.getCustomer() == null || booking.getCustomer().getId() == null) {
                throw new ValidationException("Customer ID must be provided");
            }
            Customer customer = customerRepository.findById(booking.getCustomer().getId());
            if (customer == null) {
                throw new ValidationException("Invalid Customer ID: No customer found");
            }
            booking.setCustomer(customer);

            if (booking.getHotel() == null || booking.getHotel().getId() == null) {
                throw new ValidationException("Hotel ID must be provided");
            }
            Hotel hotel = hotelRepository.findById(booking.getHotel().getId());
            if (hotel == null) {
                throw new ValidationException("Invalid Hotel ID: No hotel found");
            }
            booking.setHotel(hotel);

            Booking createdBooking = bookingService.createBooking(booking);
            log.info("POST /bookings successful for booking: " + createdBooking.getId());
            return Response.status(Response.Status.CREATED).entity(createdBooking).build();

        }
        // End of part 2
        catch (ConstraintViolationException e) {
            Map<String, String> responseObj = new HashMap<>();
            for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request: Validation failed", responseObj, Response.Status.BAD_REQUEST, e);

        } catch (UniqueBookingException e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("booking", e.getMessage());
            throw new RestServiceException("Conflict: Booking exists", responseObj, Response.Status.CONFLICT, e);

        } catch (ValidationException e) {
            // This catches our custom Customer/Hotel existence checks
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("validation", e.getMessage());
            throw new RestServiceException("Bad Request: Invalid data", responseObj, Response.Status.BAD_REQUEST, e);

        } catch (Exception e) {
            log.severe("Error creating booking: " + e.getMessage());
            throw new RestServiceException(e);
        }
    }

    @DELETE
    @Path("/{id:[0-9]+}")
    @Operation(summary = "Delete a Booking", description = "Deletes a Booking by its ID.")
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "Booking deleted successfully"),
            @APIResponse(responseCode = "404", description = "Booking not found"),
            @APIResponse(responseCode = "500", description = "An unexpected server error occurred")
    })
    public Response deleteBooking(
            @Parameter(description = "ID of the Booking to delete", required = true)
            @PathParam("id") Long id) {

        log.info("DELETE /bookings/" + id + " requested");

        Booking deletedBooking;
        try {
            // This try/catch is only for *unexpected* service-layer errors
            deletedBooking = bookingService.deleteBooking(id);
        } catch (Exception e) {
            log.severe("Error deleting booking: " + e.getMessage());
            throw new RestServiceException(e);
        }

        // Handle the "not found" case *outside* the try block.
        if (deletedBooking == null) {
            // This will be caught by the ExceptionMapper and turned into a 404
            throw new RestServiceException("No booking found with ID " + id, Response.Status.NOT_FOUND);
        }

        log.info("DELETE /bookings/" + id + " successful");
        return Response.noContent().build();
    }
}
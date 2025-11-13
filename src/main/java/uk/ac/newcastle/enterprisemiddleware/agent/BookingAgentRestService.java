package uk.ac.newcastle.enterprisemiddleware.agent;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import uk.ac.newcastle.enterprisemiddleware.agent.client.FlightClient;
import uk.ac.newcastle.enterprisemiddleware.agent.client.HotelClient;
import uk.ac.newcastle.enterprisemiddleware.agent.client.TaxiClient;
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.customer.CustomerService;
import uk.ac.newcastle.enterprisemiddleware.util.GlobalBookingUtils;
import uk.ac.newcastle.enterprisemiddleware.util.RestServiceException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * @author Swapnil Sagar
 * */
@Path("/agent")
@ApplicationScoped
public class BookingAgentRestService {

    @Inject
    Logger logger;

    @Inject
    @RestClient
    TaxiClient taxiClient;

    @Inject
    @RestClient
    FlightClient flightClient;

    @Inject
    @RestClient
    HotelClient hotelClient;

    @Inject
    GlobalBookingService globalBookingService;

    @Inject
    CustomerService customerService;

    @GET
    @Path("/")
    public Response retrieveAllBookings(){
        return Response.ok(globalBookingService.getAllBookings()).build();
    }

    @POST
    @Path("/booking")
    @Transactional
    public Response booking(@Valid GlobalBookingRequest request) {
        String globalBookingId = GlobalBookingUtils.getBookingId();
        GlobalBooking booking = GlobalBookingUtils.getBookingDetails(globalBookingId,
                request.getFutureDate(), customerService.getCustomerById(request.getCustomerID()));
        Map<String, Boolean> bookingStatus = new HashMap<>(Map.of(
                "flight", false,
                "hotel", false,
                "taxi", false
        ));
        try {
            Response flightResponse = attemptBooking("flight", () ->
                    flightClient.book(GlobalBookingUtils.getFlightBookingRequest(globalBookingId, request)), bookingStatus);
            Response hotelResponse = attemptBooking("hotel", () ->
                    hotelClient.book(GlobalBookingUtils.getHotelBookingRequest(globalBookingId, request)), bookingStatus);
            Response taxiResponse = attemptBooking("taxi", () ->
                    taxiClient.bookTaxi(GlobalBookingUtils.getTaxiBookingRequest(globalBookingId, request)), bookingStatus);
            GlobalBookingUtils.updateBookingDetails(booking, flightResponse, hotelResponse, taxiResponse);
            booking.setStatus(GlobalStatus.SUCCESS);
            if(isRollBackRequired(bookingStatus)) {
                rollback(globalBookingId, bookingStatus);
            } else {
                globalBookingService.createBooking(booking);
            }
            return Response.ok(booking).build();
        } catch (Exception ex) {
            logger.warning("Booking failed: " + ex.getMessage());
            booking.setStatus(GlobalStatus.FAILED);
            globalBookingService.updateBooking(booking);
            rollback(globalBookingId, bookingStatus);
            return Response.serverError().entity(booking).build();
        }
    }

    private boolean isRollBackRequired(Map<String, Boolean> bookingStatus) {
        for(Boolean wasBooked: bookingStatus.values()){
            if (!wasBooked) {
                return true;
            }
        }
        return false;
    }

    /** Helper for safe booking attempts */
    private Response attemptBooking(String service, Supplier<Response> action, Map<String, Boolean> statusMap) {
        Response response = action.get();
        if (response.getStatus() != 200) {
            throw new RestServiceException(service + " booking failed");
        }
        statusMap.put(service, true);
        return response;
    }

    /** Rollback any successful bookings */
    private void rollback(String globalBookingId, Map<String, Boolean> status) {
        rollbackIf(status.get("taxi"), "Taxi", () -> taxiClient.delete(globalBookingId));
        rollbackIf(status.get("hotel"), "Hotel", () -> hotelClient.delete(globalBookingId));
        rollbackIf(status.get("flight"), "Flight", () -> flightClient.delete(globalBookingId));
    }

    /** Safe rollback with logging */
    private void rollbackIf(boolean wasBooked, String serviceName, Runnable rollbackAction) {
        if (!wasBooked) return;
        try {
            rollbackAction.run();
            logger.info(serviceName + " booking rolled back successfully");
        } catch (Exception e) {
            logger.warning(serviceName + " rollback failed: " + e.getMessage());
        }
    }

    @POST
    @Path("/guest-booking")
    @Transactional
    public Response guestBooking(@Valid GuestBookingRequest request) {
        Customer customer = GlobalBookingUtils.getCustomer(request);
        try {
            customerService.createCustomer(customer);
            Response response = booking(GlobalBookingUtils.getBookingRequest(request, customer.getId()));
            if(response.getStatus() != 200){
                safeDeleteCustomer(customer.getId());
                return Response.serverError()
                        .entity(response.getEntity())
                        .build();
            }
            return Response.ok().build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Helper to safely delete a customer and log any failure */
    private void safeDeleteCustomer(Long customerId) {
        try {
            customerService.delete(customerId);
            logger.info("Rolled back guest customer with ID: " + customerId);
        } catch (Exception e) {
            logger.warning("Failed to rollback guest customer with ID " + customerId + ": " + e.getMessage());
        }
    }

    @DELETE
    @Path("/cancel/{globalBookingId}")
    public Response cancelBooking(@PathParam("globalBookingId") String globalBookingId) {
        GlobalBooking booking = globalBookingService.getBookingById(globalBookingId);
        if (booking == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Booking not found: " + globalBookingId)
                    .build();
        }

        try {
            safeDelete("Flight", () -> flightClient.delete(globalBookingId));
            safeDelete("Hotel", () -> hotelClient.delete(globalBookingId));
            safeDelete("Taxi", () -> taxiClient.delete(globalBookingId));

            booking.setStatus(GlobalStatus.CANCELLED);
            globalBookingService.deleteBooking(booking.getId());

            return Response.ok(booking).build();
        } catch (Exception e) {
//            booking.setStatus(GlobalStatus.FAILED);
//            globalBookingService.updateBooking(booking);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Cancellation failed: " + e.getMessage())
                    .build();
        }
    }

    private void safeDelete(String serviceName, Runnable deleteAction) {
        try {
            deleteAction.run();
            logger.info(serviceName + " booking deleted successfully");
        } catch (Exception e) {
            logger.warning(serviceName + " deletion failed: " + e.getMessage());
        }
    }

    @DELETE
    @Path("/{customerId}")
    @Operation(description = "Delete Customer from the database and corresponding bookings from all the services.")
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "Customer deleted successfully."),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    @Transactional
    public Response deleteCustomer(@PathParam("customerId") Long customerId){
        List<String> bookingIds = globalBookingService.getBookingIdByCustomerId(customerId);
        for(String bookingId: bookingIds) {
            cancelBooking(bookingId);
        }
        customerService.delete(customerId);
        return Response.noContent().build();
    }
}

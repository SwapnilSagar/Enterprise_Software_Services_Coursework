package uk.ac.newcastle.enterprisemiddleware.travelagent;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import uk.ac.newcastle.enterprisemiddleware.travelagent.client.Taxi2Client;
import uk.ac.newcastle.enterprisemiddleware.travelagent.client.HotelClient;
import uk.ac.newcastle.enterprisemiddleware.travelagent.client.TaxiClient;
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.customer.CustomerService;
import uk.ac.newcastle.enterprisemiddleware.util.GlobalBookingUtils;
import uk.ac.newcastle.enterprisemiddleware.util.RestServiceException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
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
public class TravelAgentRestService {

    @Inject
    @Named("logger")
    Logger logger;

    @Inject
    @RestClient
    TaxiClient taxiClient;

    @Inject
    @RestClient
    Taxi2Client taxi2Client;



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
        Map<String, Object> bookingStatus = new HashMap<>(Map.of(
                "taxi2", false,
                "hotel", false,
                "taxi", false
        ));
        try {
            String taxi2Response = attemptBooking("taxi2", () ->
                    taxi2Client.bookTaxi2(GlobalBookingUtils.getTaxi2BookingRequest(globalBookingId, request)), bookingStatus);
            String hotelResponse = attemptBooking("hotel", () ->
                    hotelClient.bookHotel(GlobalBookingUtils.getHotelBookingRequest(globalBookingId, request)), bookingStatus);
            String taxiResponse = attemptBooking("taxi", () ->
                    taxiClient.bookTaxi(GlobalBookingUtils.getTaxiBookingRequest(globalBookingId, request)), bookingStatus);
            GlobalBookingUtils.updateBookingDetails(booking, taxi2Response, hotelResponse, taxiResponse);
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
            return Response.serverError().entity(bookingStatus).build();
        }
    }

    private boolean isRollBackRequired(Map<String, Object> bookingStatus) {
        for(Object wasBooked: bookingStatus.values()){
            if (wasBooked instanceof Boolean && !((Boolean) wasBooked)) {
                return true;
            }
        }
        return false;
    }



    /** Helper for safe booking attempts */
    private String attemptBooking(String service, Supplier<Response> action, Map<String, Object> statusMap) {
        Response response;
        String entity;
        try {
            response = action.get();
            entity = response.readEntity(String.class);
            logger.info("Response from " + service + ": " + entity);
        } catch (Exception e) {
            // This catches connection errors
            logger.warning(service + " booking threw exception: " + e.getMessage());
            statusMap.put(service + "BookingFailed", e.getMessage());
            throw new RestServiceException(service + " booking failed", e);
        }

        int status = response.getStatus();
        if (status < 200 || status >= 300) {
            statusMap.put(service + "BookingFailed", entity);
            throw new RestServiceException(service + " booking failed with status " + status);
        }
        statusMap.put(service, true);
        return entity;
    }



    /** Rollback any successful bookings */
    private void rollback(String globalBookingId, Map<String, Object> status) {
        rollbackIf((Boolean) status.get("taxi"), "Taxi", () -> taxiClient.delete(globalBookingId));
        rollbackIf((Boolean) status.get("hotel"), "Hotel", () -> hotelClient.delete(globalBookingId));
        rollbackIf((Boolean) status.get("taxi2"), "taxi2", () -> taxi2Client.delete(globalBookingId));
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
            if(response.getStatus() != 200) {
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
    @Transactional
    public Response cancelBooking(@PathParam("globalBookingId") String globalBookingId) {
        GlobalBooking booking = globalBookingService.getBookingById(globalBookingId);
        if (booking == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Booking not found: " + globalBookingId)
                    .build();
        }

        try {
            safeDelete("taxi2", () -> taxi2Client.delete(globalBookingId));
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

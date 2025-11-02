package uk.ac.newcastle.enterprisemiddleware.travelagent;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import uk.ac.newcastle.enterprisemiddleware.util.RestServiceException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ValidationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Path("/agent")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TravelAgentRestService {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    TravelAgentService travelAgentService;

    @GET
    @Path("/bookings")
    @Operation(summary = "Fetch all aggregate bookings for a Customer")
    public Response getBookings(@Parameter(required = true) @QueryParam("customerId") Long customerId) {
        if (customerId == null) {
            throw new RestServiceException("customerId query parameter is required", Response.Status.BAD_REQUEST);
        }
        List<TravelAgentBooking> bookings = travelAgentService.findByCustomerId(customerId);
        return Response.ok(bookings).build();
    }

    @POST
    @Path("/bookings")
    @Operation(summary = "Create a new aggregate travel booking")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Aggregate booking created"),
            @APIResponse(responseCode = "400", description = "Invalid request data"),
            @APIResponse(responseCode = "500", description = "Booking failed (partial bookings rolled back)")
    })
    public Response createTravelAgentBooking(TravelAgentRequest request) {

        if (request == null || request.getCustomerId() == null || request.getHotelBooking() == null ||
                request.getTaxiBooking() == null || request.getFlightBooking() == null) {
            throw new RestServiceException("Invalid booking request. All fields are required.", Response.Status.BAD_REQUEST);
        }

        try {
            TravelAgentBooking booking = travelAgentService.createTravelAgentBooking(
                    request.getHotelBooking(),
                    request.getTaxiBooking(),
                    request.getFlightBooking(),
                    request.getCustomerId()
            );
            return Response.status(Response.Status.CREATED).entity(booking).build();

        } catch (ValidationException e) {

            throw new RestServiceException(
                    "Bad Request: Validation failed",
                    Map.of("validation", e.getMessage()),
                    Response.Status.BAD_REQUEST
            );
        } catch (WebApplicationException e) {

            throw new RestServiceException(
                    "Booking failed",
                    Map.of("error", e.getMessage()),
                    Response.Status.INTERNAL_SERVER_ERROR
            );
        } catch (Exception e) {
            throw new RestServiceException(e);
        }
    }
}
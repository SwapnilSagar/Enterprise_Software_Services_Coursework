package uk.ac.newcastle.enterprisemiddleware.hotel;

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
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Path("/hotels")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class HotelRestService {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    HotelService hotelService;

    @GET
    @Operation(summary = "Fetch all Hotels", description = "Returns a JSON array of all stored Hotel objects.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Hotels retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Hotel[].class)))
    })
    public Response getAllHotels() {
        log.info("GET /hotels requested");
        List<Hotel> hotels = hotelService.getAllHotels();
        log.info("GET /hotels completed");
        return Response.ok(hotels).build();
    }

    @POST
    @Operation(summary = "Create a new Hotel", description = "Creates a new Hotel from the supplied JSON object.")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Hotel created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Hotel.class))),
            @APIResponse(responseCode = "400", description = "Invalid Hotel data supplied (e.g., validation failure)"),
            @APIResponse(responseCode = "409", description = "Hotel with this phone number already exists"),
            @APIResponse(responseCode = "500", description = "An unexpected server error occurred")
    })
    public Response createHotel(
            @Parameter(description = "JSON representation of Hotel object to be added", required = true)
            Hotel hotel) {

        log.info("POST /hotels requested with hotel: " + hotel.getName());

        if (hotel == null) {
            throw new RestServiceException("Bad Request: No hotel data provided", Response.Status.BAD_REQUEST);
        }

        try {
            hotel.setId(null);
            Hotel createdHotel = hotelService.createHotel(hotel);
            log.info("POST /hotels successful for hotel: " + createdHotel.getName());
            return Response.status(Response.Status.CREATED).entity(createdHotel).build();

        } catch (ConstraintViolationException e) {
            // Handle bean validation issues
            Map<String, String> responseObj = new HashMap<>();
            for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request: Validation failed", responseObj, Response.Status.BAD_REQUEST, e);

        } catch (UniquePhoneException e) {
            // Handle the unique constraint violation
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("phoneNumber", "A hotel with this phone number already exists");
            throw new RestServiceException("Conflict: Phone number exists", responseObj, Response.Status.CONFLICT, e);

        } catch (Exception e) {
            // Handle generic exceptions
            log.severe("Error creating hotel: " + e.getMessage());
            throw new RestServiceException(e);
        }
    }


    //Part 2
    @DELETE
    @Path("/{id:[0-9]+}")
    @Operation(summary = "Delete a Hotel", description = "Deletes a Hotel by its ID. This will also delete all bookings for this hotel.")
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "Hotel deleted successfully"),
            @APIResponse(responseCode = "404", description = "Hotel not found"),
            @APIResponse(responseCode = "500", description = "An unexpected server error occurred")
    })
    public Response deleteHotel(
            @Parameter(description = "ID of the Hotel to delete", required = true)
            @PathParam("id") Long id) {

        log.info("DELETE /hotels/" + id + " requested");

        try {
            Hotel hotel = hotelService.deleteHotel(id);
            if (hotel == null) {
                throw new RestServiceException("No hotel found with ID " + id, Response.Status.NOT_FOUND);
            }
            log.info("DELETE /hotels/" + id + " successful");
            return Response.noContent().build();

        } catch (Exception e) {
            log.severe("Error deleting hotel: " + e.getMessage());
            throw new RestServiceException(e);
        }
    }
    //End of part 2
}
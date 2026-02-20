package uk.ac.newcastle.enterprisemiddleware.hotel;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import uk.ac.newcastle.enterprisemiddleware.DTO.HotelDTO;
import uk.ac.newcastle.enterprisemiddleware.util.RestServiceException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Swapnil Sagar
 * */
@Path("/hotel")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class HotelRestService
{

    @Inject
    @Named("logger")
    Logger logger;

    @Inject
    HotelService hotelService;


    @GET
    @Path("/{id:[0-9]+}")
    @Operation(summary = "Fetch Hotel detail", description = "Returns JSON of stored Hotel Object using their ID.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Hotel found"),
            @APIResponse(responseCode = "404", description = "Hotel with given ID not found")
    })
    public Response retrieveHotelById(@Parameter(description = "ID of the Hotel to be fetched", required = true)
                                         @PathParam("id") long id) {
        Hotel hotel = hotelService.getHotelById(id);
        if (hotel == null)
        {
            // If no hotel found, return 404
            throw new HotelNotFoundException("No hotel with ID " + id + " was found!");
        }
        logger.info("Id:"+ id +" . hotel: "+ hotel.toString());
        return Response.ok(HotelMapper.toDTO(hotel)).build();
    }

    @GET
    @Path("/bookings")
    @Operation(summary = "Fetch all Hotel with Booking details",
            description = "Returns JSON array of all stored Hotel Booking Objects.")
    public Response retrieveAllHotelBookingInfo()
    {
        List<Hotel> hotelList = hotelService.getAllHotelInfo();
        List<HotelDTO> hotelDTOS = hotelList.stream()
                .map(HotelMapper::toDTO)
                .collect(Collectors.toList());
        return Response.ok(hotelDTOS).build();

    }



    @POST
    @Operation(description = "Add new Hotel to the database")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Hotel created successfully.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = HotelDTO.class))), // Return DTO
            @APIResponse(responseCode = "400", description = "Invalid Hotel data supplied"),
            @APIResponse(responseCode = "409", description = "Hotel with this phone number already exists"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    @Transactional
    public Response createHotel(@Parameter(description = "", required = true)
                                   @Valid Hotel hotel){
        if (hotel == null)
        {
            throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
        }

        Response.ResponseBuilder builder;
        try {
            // Clear the ID if accidentally set
            hotel.setId(null);
            hotel.setBookings(null);
            hotelService.createHotel(hotel);
            builder = Response.status(Response.Status.CREATED).entity(hotel);
        }
        catch (ConstraintViolationException e) {
            // Handle bean validation issues
            Map<String, String> responseObj = new HashMap<>();
            for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request: Validation failed", responseObj, Response.Status.BAD_REQUEST, e);

        }
        catch (PersistenceException e) {
            // Handle unique constraint violation (phone)
            if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
                Map<String, String> responseObj = new HashMap<>();
                if (hotelService.findByPhoneNumber(hotel.getPhoneNumber()) != null) {
                    responseObj.put("phoneNumber", "A hotel with this phone number already exists");
                    throw new RestServiceException("Conflict: Phone number exists", responseObj, Response.Status.CONFLICT, e);
                }
            }
            // Handle other generic persistence exceptions
            throw new RestServiceException(e);
        }
        catch (Exception e) {
            // Handle generic exceptions
            throw new RestServiceException(e);
        }
        logger.info("createHotel completed = " + hotel);
        return builder.build();
    }

    @DELETE
    @Path("/{id:[0-9]+}")
    @Operation(description = "Delete a Hotel from the database via local hotel ID")
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "Hotel successfully deleted"),
            @APIResponse(responseCode = "404", description = "Hotel with given ID not found"),
            @APIResponse(responseCode = "500", description = "Unexpected error occurred")
    })
    @Transactional

    public Response deleteHotel(@Parameter(description = "Id of Hotel to be removed", required = true)
                                    @Schema(minimum = "0")
                                    @PathParam("id") Long id){
        try {
            boolean deleted = hotelService.deleteHotel(id);;

            if (!deleted) {
                // Booking not found, return 404
                throw new RestServiceException(
                        "No Hotel with ID " + id + " was found!",
                        Response.Status.NOT_FOUND
                );
            }


            // Successfully deleted, return 204 No Content
            return Response.noContent().build();

        } catch (Exception e) {
            // Handle generic exceptions
            throw new RestServiceException("Unexpected error occurred while deleting booking", e);

        }
    }
}

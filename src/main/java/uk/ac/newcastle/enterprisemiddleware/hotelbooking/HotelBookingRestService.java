package uk.ac.newcastle.enterprisemiddleware.hotelbooking;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import uk.ac.newcastle.enterprisemiddleware.DTO.HotelBookingDTO;
import uk.ac.newcastle.enterprisemiddleware.customer.CustomerRestService;
import uk.ac.newcastle.enterprisemiddleware.DTO.HotelDTO;
import uk.ac.newcastle.enterprisemiddleware.hotel.HotelMapper;
import uk.ac.newcastle.enterprisemiddleware.hotel.Hotel;
import uk.ac.newcastle.enterprisemiddleware.hotel.HotelNotFoundException;
import uk.ac.newcastle.enterprisemiddleware.hotel.HotelRestService;
import uk.ac.newcastle.enterprisemiddleware.hotelbooking.exception.BookingDateConflictException;
import uk.ac.newcastle.enterprisemiddleware.hotelbooking.exception.InvalidBookingDateException;
import uk.ac.newcastle.enterprisemiddleware.util.RestServiceException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author Swapnil Sagar
 *
 * */
@Path("/hotel-booking")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)

public class HotelBookingRestService
{

    @Inject
    @Named("logger")
    Logger logger;

    @Inject
    HotelBookingService hotelBookingService;

    @Inject
    CustomerRestService customerRestService;

    @Inject
    HotelRestService hotelRestService;

    @POST
    @Path("/")
    @Operation(description = "Add new Booking to the database")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Booking created successfully."),
            @APIResponse(responseCode = "400", description = "Invalid booking request"),
            @APIResponse(responseCode = "409", description = "Booking already exists for this hotel and date"),
            @APIResponse(responseCode = "500", description = "Unexpected error occurred")
    })
    @Transactional
    public Response createBooking(@Parameter(description = "Booking request data", required = true)
                                      HotelBookingRequest request) throws Exception {
        if (request == null) {
            throw new RestServiceException("Invalid booking request", Response.Status.BAD_REQUEST);
        }

        try {
            // Convert request DTO to entity
            HotelBooking hotelBooking = createBookingReq(request);
            if (hotelBooking == null) {
                throw new RestServiceException("Booking could not be created", Response.Status.BAD_REQUEST);
            }
            // Set initial status before persisting
            hotelBooking.setStatus(Status.SUCCESS);
            // Persist booking
            HotelBooking createdBooking = hotelBookingService.createBooking(hotelBooking);
            logger.info("createdBooking : " + createdBooking.toString());
            // Return 201 Created with the created booking
            return Response.status(Response.Status.CREATED)
                    .entity(HotelBookingMapper.toDTO(createdBooking))
                    .build();
        }
        catch (HotelNotFoundException ex){
            throw new RestServiceException("Bad Request",
                    Map.of("InvalidHotelID", "Hotel id = " + request.getHotelId() + " does not exist"),
                    Response.Status.BAD_REQUEST, ex);
        }
        catch (BookingDateConflictException ex){
            throw new RestServiceException("Bad Request",
                    Map.of("HotelBookingDateConflict", "Hotel booking already exists on this date"),
                    Response.Status.BAD_REQUEST, ex);
        }
        catch (InvalidBookingDateException ex){
            throw new RestServiceException("Bad Request",
                    Map.of("InvalidHotelBookingDate", "Hotel booking date is invalid"),
                    Response.Status.BAD_REQUEST, ex);
        }
        catch (RestServiceException e) {
            throw e;
        }
        catch (Exception e) {
            // Wrap any unexpected errors
            throw new RestServiceException("Failed to create booking", e);
        }
    }

    private HotelBooking createBookingReq(HotelBookingRequest request) {
        HotelBooking hotelBooking = new HotelBooking();
        hotelBooking.setBookingDate(request.getBookingDate());
        hotelBooking.setGlobalBookingId(request.getGlobalBookingId());
        Response response = hotelRestService.retrieveHotelById(request.getHotelId());
        if (response.getStatus() == 200 && response.hasEntity()) {
            Hotel hotel = HotelMapper.toHotel(response.readEntity(HotelDTO.class));
            if (hotel == null) return null;
            hotelBooking.setHotel(hotel);
        } else {
            throw new RestServiceException(
                    " No Hotel with ID " + request.getHotelId() + " was found! ",
                    Response.Status.NOT_FOUND
            );
        }
        logger.info("hotelBooking:" + hotelBooking.toString());
        return hotelBooking;
    }

    @GET
    @Path("/")
    @Operation(summary = "Fetch all bookings")
    public Response getAllBookings() {
        List<HotelBookingDTO> bookingDTOS = hotelBookingService.getAllBookings()
                .stream()
                .map(HotelBookingMapper::toDTO)
                .collect(Collectors.toList());
        return Response.ok(bookingDTOS).build();
    }

    @DELETE
    @Path("/{bookingId:[0-9]+}")
    @Operation(description = "Delete a Hotel-Booking from the database via local hotel booking ID")
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "Booking successfully deleted"),
            @APIResponse(responseCode = "404", description = "Booking with given ID not found"),
            @APIResponse(responseCode = "500", description = "Unexpected error occurred")
    })
    @Transactional
    public Response deleteBookingRecord(
            @Parameter(description = "Id of Booking to be deleted", required = true)
            @Schema(minimum = "0")
            @PathParam("bookingId") long bookingId) {

        try {
            boolean deleted = hotelBookingService.deleteBookingRecord(bookingId);

            if (!deleted) {
                // Booking not found, return 404
                throw new RestServiceException(
                        "No HotelBooking with ID " + bookingId + " was found!",
                        Response.Status.NOT_FOUND
                );
            }

            // Successfully deleted, return 204 No Content
            return Response.noContent().build();

        }
        catch (Exception e) {
            // Handle generic exceptions
            throw new RestServiceException("Unexpected error occurred while deleting booking", e);
        }
    }

    @DELETE
    @Path("/{globalBookingId}")
    @Operation(description = "Delete a Hotel-Booking from the database via Global booking ID")
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "Booking successfully deleted"),
            @APIResponse(responseCode = "404", description = "Booking with given ID not found"),
            @APIResponse(responseCode = "500", description = "Unexpected error occurred")
    })
    @Transactional
    public Response deleteBookingRecord(
            @Parameter(description = "Global Id of Booking to be deleted", required = true)
            @Schema(minimum = "0")
            @PathParam("globalBookingId") String globalBookingId) {

        logger.info("DELETE /bookings/" + globalBookingId + " requested");
        try {
            hotelBookingService.deleteByGlobalBookingId(globalBookingId);
            return Response.noContent().build();

        }
        catch (EntityNotFoundException e) {
            throw new RestServiceException(e.getMessage(), Response.Status.NOT_FOUND);
        }
        catch (Exception e) {
            throw new RestServiceException("Unexpected error occurred while deleting booking", e);
        }
    }
}

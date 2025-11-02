package uk.ac.newcastle.enterprisemiddleware.travelagent;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import uk.ac.newcastle.enterprisemiddleware.booking.Booking;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * REST Client for the Hotel Service
 */
@Path("/bookings")
@RegisterRestClient(configKey = "hotel-api")
public interface HotelServiceClient {

    @POST
    Booking createBooking(Booking booking);

    @DELETE
    @Path("/{id:[0-9]+}")
    void deleteBooking(@PathParam("id") Long id);
}
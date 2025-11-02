package uk.ac.newcastle.enterprisemiddleware.travelagent;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import uk.ac.newcastle.enterprisemiddleware.booking.Booking; // Re-using your Booking class

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * REST Client for the (assumed) Taxi Service
 */
@Path("/bookings")
@RegisterRestClient(configKey = "taxi-api")
public interface TaxiServiceClient {

    @POST
    Booking createBooking(Booking booking);

    @DELETE
    @Path("/{id:[0-9]+}")
    void deleteBooking(@PathParam("id") Long id);
}
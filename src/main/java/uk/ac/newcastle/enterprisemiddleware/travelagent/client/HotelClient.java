package uk.ac.newcastle.enterprisemiddleware.travelagent.client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import uk.ac.newcastle.enterprisemiddleware.hotelbooking.HotelBookingRequest;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * @author Swapnil Sagar
 *
 *
 * */
// FIX #8 — Removed hardcoded baseUri from @RegisterRestClient.
// The URL is already configured in application.properties via quarkus.rest-client.hotel-api.url.
// Having it in two places risks a mismatch between environments (dev vs deployed).
@RegisterRestClient(configKey = "hotel-api")
@Path("/hotel-booking")

public interface HotelClient {

    @POST
    @Path("/")
    Response bookHotel(HotelBookingRequest request);

    @DELETE
    @Path("/{id}")
    Response delete(String id);
}

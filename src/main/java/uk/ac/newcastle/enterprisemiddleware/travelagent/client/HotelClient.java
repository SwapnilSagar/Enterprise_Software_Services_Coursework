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
@RegisterRestClient(baseUri = "https://csc-8104-swapnil-sagar-swapnilsagar-dev.apps.rm1.0a51.p1.openshiftapps.com/")
@Path("/hotel-booking")

public interface HotelClient {

    @POST
    @Path("/")
    Response bookHotel(HotelBookingRequest request);

    @DELETE
    @Path("/{id}")
    Response delete(String id);
}

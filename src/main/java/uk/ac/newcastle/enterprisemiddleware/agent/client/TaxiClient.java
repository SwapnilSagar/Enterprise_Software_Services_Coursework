package uk.ac.newcastle.enterprisemiddleware.agent.client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import uk.ac.newcastle.enterprisemiddleware.hotelbooking.HotelBookingRequest;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * @author Swapnil Sagar
 * */
@RegisterRestClient(baseUri = "https://csc-8104-mayank-kunwar-crt-9690097516-dev.apps.rm3.7wse.p1.openshiftapps.com/")
@Path("/taxi")
public interface TaxiClient {

    @POST
    @Path("/book")
    Response bookTaxi(HotelBookingRequest request);

    @DELETE
    @Path("/{id}")
    Response delete(String id);
}

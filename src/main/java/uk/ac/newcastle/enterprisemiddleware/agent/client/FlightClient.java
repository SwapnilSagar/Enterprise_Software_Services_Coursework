package uk.ac.newcastle.enterprisemiddleware.agent.client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import uk.ac.newcastle.enterprisemiddleware.agent.client.request.FlightBookingRequest;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * @author Mayank Kunwar
 * */
@RegisterRestClient(baseUri = "https://csc-8104-mayank-kunwar-crt-9690097516-dev.apps.rm3.7wse.p1.openshiftapps.com/")
@Path("/flight")
public interface FlightClient {

    @POST
    @Path("/book")
    Response book(FlightBookingRequest request);

    @DELETE
    @Path("/{id}")
    Response delete(String id);
}

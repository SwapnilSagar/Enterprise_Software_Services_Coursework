package uk.ac.newcastle.enterprisemiddleware.travelagent.client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import uk.ac.newcastle.enterprisemiddleware.travelagent.client.request.TaxiBookingRequest;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 *
 *
 * @author Swapnil Sagar
 *
 *
 * */
@RegisterRestClient(baseUri = "https://csc-8104-mayank-kunwar-crt-9690097516-dev.apps.rm3.7wse.p1.openshiftapps.com/")
@Path("/taxi-booking")
public interface TaxiClient {



    @POST
    @Path("/")
    Response bookTaxi(TaxiBookingRequest request);



    @DELETE
    @Path("/{id}")
    Response delete(String id);
}

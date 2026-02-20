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
 * */

@RegisterRestClient(baseUri = "https://csc-8104-deepal-thakur-deepalthakur-dev.apps.rm1.0a51.p1.openshiftapps.com/")
@Path("/TaxiBooking")
public interface Taxi2Client {

    @POST
    @Path("/")
    Response bookTaxi2(TaxiBookingRequest request);



    @DELETE
    @Path("/{id}")
    Response delete(String id);
}

package uk.ac.newcastle.enterprisemiddleware.customer;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import uk.ac.newcastle.enterprisemiddleware.dto.customer.CustomerDTO;
import uk.ac.newcastle.enterprisemiddleware.dto.customer.CustomerMapper;
import uk.ac.newcastle.enterprisemiddleware.util.RestServiceException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Swapnil Sagar
 */
@Path("/customers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CustomerRestService {

    @Inject
    @Named("logger")
    Logger logger;

    @Inject
    CustomerService customerService;

    @GET
    @Path("/{id}")
    @Operation(summary = "Fetch Customer detail", description = "Returns JSON of stored Consumer Object using their ID.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Customer found"),
            @APIResponse(responseCode = "404", description = "Customer with given ID not found")
    })
    public Response retrieveCustomerById(@Parameter(description = "ID of the Customer to be fetched", required = true)
                                             @PathParam("id") long id) {
        Customer customer = customerService.getCustomerById(id);
        if (customer == null) {
            // If no customer found, return 404
            throw new RestServiceException("No Customer with ID " + id + " was found!", Response.Status.NOT_FOUND);
        }
        logger.info("Id:"+ id +" . Customer: "+ customer.toString());
        return Response.ok(CustomerMapper.toDTO(customer)).build();
    }

    @GET
    @Path("/bookings")
    @Operation(summary = "Fetch all Customer with Booking details",
            description = "Returns JSON array of all stored Consumer Booking Objects.")
    public Response retrieveAllCustomersBookingInfo() {
        List<Customer> customers = customerService.getAllCustomersInfo();
        List<CustomerDTO> customerDTOs = customers.stream()
                .map(CustomerMapper::toDTO)
                .collect(Collectors.toList());
        return Response.ok(customerDTOs).build();
    }

    @POST
    @Operation(description = "Add new Customer to the database")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Customer created successfully."),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    @Transactional
    public Response createCustomer(@Parameter(description = "", required = true)
                                       @Valid Customer customer){
        if (customer == null) {
            throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
        }
        Response.ResponseBuilder builder;
        try {
            // Clear the ID if accidentally set
            customer.setId(null);
            customer.setBookings(null);
            customerService.createCustomer(customer);
            builder = Response.status(Response.Status.CREATED).entity(customer);
        } catch (Exception e) {
            // Handle generic exceptions
            throw new RestServiceException(e);
        }
        logger.info("createCustomer completed = " + customer);
        return builder.build();
    }
}

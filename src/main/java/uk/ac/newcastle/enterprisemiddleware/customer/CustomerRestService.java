package uk.ac.newcastle.enterprisemiddleware.customer;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import uk.ac.newcastle.enterprisemiddleware.util.RestServiceException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    @Path("/{id:[0-9]+}")
    @Operation(summary = "Fetch Customer detail", description = "Returns JSON of stored Consumer Object using their ID.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Customer found"),
            @APIResponse(responseCode = "404", description = "Customer with given ID not found")
    })
    public Response retrieveCustomerById(@Parameter(description = "ID of the Customer to be fetched", required = true)
                                             @PathParam("id") long id)

    {
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
    public Response retrieveAllCustomersBookingInfo()
    {
        List<Customer> customers = customerService.getAllCustomersInfo();
        List<CustomerPayload> customerPayloads = customers.stream()
                .map(CustomerMapper::toDTO)
                .collect(Collectors.toList());
        return Response.ok(customerPayloads).build();

    }

    @POST
    @Operation(description = "Add new Customer to the database")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Customer created successfully.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CustomerPayload.class))), // Return DTO
            @APIResponse(responseCode = "400", description = "Invalid Customer data supplied"),
            @APIResponse(responseCode = "409", description = "Customer with this email already exists"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    @Transactional
    public Response createCustomer(@Parameter(description = "", required = true)
                                       @Valid Customer customer)
    {
        if (customer == null)
        {
            throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
        }
        Response.ResponseBuilder builder;
        try
        {
            // Clear the ID if accidentally set
            customer.setId(null);
            customer.setBookings(null);
            customerService.createCustomer(customer);
            builder = Response.status(Response.Status.CREATED).entity(customer);
        } catch (ConstraintViolationException e) {
            // Handle bean validation issues
            Map<String, String> responseObj = new HashMap<>();
            for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request: Validation failed", responseObj, Response.Status.BAD_REQUEST, e);

        } catch (PersistenceException e) {
            // Handle unique constraint violation (email)
            if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
                Map<String, String> responseObj = new HashMap<>();
                responseObj.put("email", "A customer with this email already exists");
                throw new RestServiceException("Conflict: Email exists", responseObj, Response.Status.CONFLICT, e);
            }
            // Handle other generic persistence exceptions
            throw new RestServiceException(e);
        }
        catch (Exception e) {
            // Handle generic exceptions
            throw new RestServiceException(e);
        }
        logger.info("createCustomer completed = " + customer);

        return builder.build();
    }
}

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
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Path("/customers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CustomerRestService {

    @Inject
    @Named("logger") // Inject the logger
    Logger log;

    @Inject
    CustomerService customerService;

    @GET
    @Operation(summary = "Fetch all Customers", description = "Returns a JSON array of all stored Customer objects.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Customers retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Customer[].class)))
    })
    public Response getAllCustomers() {
        log.info("GET /customers requested");
        List<Customer> customers = customerService.getAllCustomers();
        log.info("GET /customers completed");
        return Response.ok(customers).build();
    }

    @POST
    @Operation(summary = "Create a new Customer", description = "Creates a new Customer from the supplied JSON object.")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Customer created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Customer.class))),
            @APIResponse(responseCode = "400", description = "Invalid Customer data supplied (e.g., validation failure)"),
            @APIResponse(responseCode = "409", description = "Customer with this email already exists"),
            @APIResponse(responseCode = "500", description = "An unexpected server error occurred")
    })
    public Response createCustomer(
            @Parameter(description = "JSON representation of Customer object to be added", required = true)
            Customer customer) {

        log.info("POST /customers requested with customer: " + customer.getName());

        if (customer == null) {
            // Use the new exception
            throw new RestServiceException("Bad Request: No customer data provided", Response.Status.BAD_REQUEST);
        }

        try {
            Customer createdCustomer = customerService.createCustomer(customer);
            log.info("POST /customers successful for customer: " + createdCustomer.getName());
            return Response.status(Response.Status.CREATED).entity(createdCustomer).build();

        } catch (ConstraintViolationException e) {
            // Handle bean validation issues
            Map<String, String> responseObj = new HashMap<>();
            for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            // Use the new exception
            throw new RestServiceException("Bad Request: Validation failed", responseObj, Response.Status.BAD_REQUEST, e);

        } catch (UniqueEmailException e) {
            // Handle the unique constraint violation
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("email", "A customer with this email already exists");
            // Use the new exception
            throw new RestServiceException("Conflict: Email exists", responseObj, Response.Status.CONFLICT, e);

        } catch (Exception e) {
            // Handle generic exceptions
            log.severe("Error creating customer: " + e.getMessage());
            // Use the new exception
            throw new RestServiceException(e);
        }
    }


    //Part 2
    @DELETE
    @Path("/{id:[0-9]+}")
    @Operation(summary = "Delete a Customer", description = "Deletes a Customer by its ID. This will also delete all bookings associated with this customer.")
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "Customer deleted successfully"),
            @APIResponse(responseCode = "404", description = "Customer not found"),
            @APIResponse(responseCode = "500", description = "An unexpected server error occurred")
    })
    public Response deleteCustomer(
            @Parameter(description = "ID of the Customer to delete", required = true)
            @PathParam("id") Long id) {

        log.info("DELETE /customers/" + id + " requested");

        try {
            Customer customer = customerService.deleteCustomer(id);
            if (customer == null) {
                throw new RestServiceException("No customer found with ID " + id, Response.Status.NOT_FOUND);
            }
            log.info("DELETE /customers/" + id + " successful");
            return Response.noContent().build();

        } catch (Exception e) {
            log.severe("Error deleting customer: " + e.getMessage());
            throw new RestServiceException(e);
        }
    }
    //End of part 2
}
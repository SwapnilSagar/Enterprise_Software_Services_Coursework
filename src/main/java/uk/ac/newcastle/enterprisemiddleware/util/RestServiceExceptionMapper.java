package uk.ac.newcastle.enterprisemiddleware.util;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;

/**
 * <p>Handler object to convert {@link RestServiceException} exception into an actual {@link Response} containing JSON
 * so we can get error message, easily parsable by our API clients.</p>
 *
 * @author Swapnil Sagar
 */
@Provider
public class RestServiceExceptionMapper implements ExceptionMapper<RestServiceException> {



    @Inject
    @Named("logger")
    Logger log;

    @Context
    HttpHeaders headers;

    @Override
    public Response toResponse(final RestServiceException e) {
        Throwable root = e;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }



        log.severe("Mapping RestServiceException with status " + e.getStatus() +
                ", message: " + e.getMessage() + ", root cause: " + root);
        root.printStackTrace();

        Response.ResponseBuilder builder = Response.status(e.getStatus())
                .entity(new ErrorMessage(e.getMessage(), e.getReasons()));
        return builder.build();
    }


}
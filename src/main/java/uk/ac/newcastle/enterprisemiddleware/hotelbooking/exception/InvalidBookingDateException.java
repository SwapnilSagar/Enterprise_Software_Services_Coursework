package uk.ac.newcastle.enterprisemiddleware.hotelbooking.exception;

import javax.validation.ValidationException;

public class InvalidBookingDateException  extends ValidationException {

    public InvalidBookingDateException(String message) {
        super(message);
    }

    public InvalidBookingDateException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidBookingDateException(Throwable cause) {
        super(cause);
    }
}

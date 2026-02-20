package uk.ac.newcastle.enterprisemiddleware.hotelbooking.exception;

import javax.validation.ValidationException;

/**
 * @author Swapnil Sagar
 * */

public class BookingDateConflictException  extends ValidationException {

    public BookingDateConflictException(String message) {
        super(message);
    }

    public BookingDateConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookingDateConflictException(Throwable cause) {
        super(cause);
    }
}

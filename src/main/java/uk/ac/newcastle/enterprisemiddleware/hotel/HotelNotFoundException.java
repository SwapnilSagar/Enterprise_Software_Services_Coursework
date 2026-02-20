package uk.ac.newcastle.enterprisemiddleware.hotel;

import javax.validation.ValidationException;

/**
 * @author Swapnil Sagar
 * */

public class HotelNotFoundException  extends ValidationException {

    public HotelNotFoundException(String message) {
        super(message);
    }

    public HotelNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public HotelNotFoundException(Throwable cause) {
        super(cause);
    }
}

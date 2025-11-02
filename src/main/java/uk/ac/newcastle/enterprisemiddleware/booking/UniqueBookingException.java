package uk.ac.newcastle.enterprisemiddleware.booking;

import javax.validation.ValidationException;

/**
 * <p>ValidationException caused if a Booking conflicts with another for the same hotel and date.</p>
 * <p>This violates the uniqueness constraint.</p>
 */
public class UniqueBookingException extends ValidationException {

    public UniqueBookingException(String message) {
        super(message);
    }
}
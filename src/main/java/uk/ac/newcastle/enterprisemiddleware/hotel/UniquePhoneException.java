package uk.ac.newcastle.enterprisemiddleware.hotel;

import javax.validation.ValidationException;

/**
 * <p>ValidationException caused if a Hotel's phonenumber conflicts with that of another Hotel.</p>
 * <p>This violates the uniqueness constraint.</p>
 */
public class UniquePhoneException extends ValidationException {

    public UniquePhoneException(String message) {
        super(message);
    }
}
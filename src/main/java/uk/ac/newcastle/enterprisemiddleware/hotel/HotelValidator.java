package uk.ac.newcastle.enterprisemiddleware.hotel;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class HotelValidator {
    @Inject
    Validator validator;

    @Inject
    HotelRepository crud;

    /**
     * Validates the given Hotel object.
     *
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws UniquePhoneException If hotel with the same phone number already exists
     */
    void validateHotel(Hotel hotel) throws ConstraintViolationException, UniquePhoneException {
        // 1. Check for bean validation violations
        Set<ConstraintViolation<Hotel>> violations = validator.validate(hotel);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<>(violations));
        }

        // 2. Check the uniqueness of the phone number
        if (phoneAlreadyExists(hotel.getPhoneNumber())) {
            throw new UniquePhoneException("Unique Phone Violation: A hotel with this phone number already exists");
        }
    }

    /**
     * Checks if a hotel with the same phone number is already registered.
     */
    boolean phoneAlreadyExists(String phoneNumber) {
        return crud.findByPhoneNumber(phoneNumber) != null;
    }
}
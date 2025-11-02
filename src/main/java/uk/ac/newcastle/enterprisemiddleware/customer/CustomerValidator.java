package uk.ac.newcastle.enterprisemiddleware.customer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class CustomerValidator {
    @Inject
    Validator validator;

    @Inject
    CustomerRepository crud;

    /**
     * Validates the given Customer object.
     *
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws UniqueEmailException If customer with the same email already exists
     */
    void validateCustomer(Customer customer) throws ConstraintViolationException, UniqueEmailException {
        // 1. Check for bean validation violations
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<>(violations));
        }

        // 2. Check the uniqueness of the email address
        if (emailAlreadyExists(customer.getEmail())) {
            throw new UniqueEmailException("Unique Email Violation: A customer with this email already exists");
        }
    }

    /**
     * Checks if a customer with the same email address is already registered.
     */
    boolean emailAlreadyExists(String email) {
        return crud.findByEmail(email) != null;
    }
}
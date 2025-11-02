package uk.ac.newcastle.enterprisemiddleware.booking;

import uk.ac.newcastle.enterprisemiddleware.customer.CustomerRepository;
import uk.ac.newcastle.enterprisemiddleware.hotel.HotelRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.Set;


import java.time.LocalDate;


@ApplicationScoped
public class BookingValidator {
    @Inject
    Validator validator;

    @Inject
    BookingRepository bookingCrud;

    // We inject other repositories to validate relationships
    @Inject
    CustomerRepository customerCrud;

    @Inject
    HotelRepository hotelCrud;

    /**
     * Validates the given Booking object.
     *
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException If Booking data is invalid (e.g., non-existent customer/hotel)
     */
    void validateBooking(Booking booking) throws ConstraintViolationException, ValidationException {
        // 1. Check for bean validation violations
        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<>(violations));
        }

        // 2. Check for uniqueness (Part 2)
        if (booking.getHotel() == null || booking.getHotel().getId() == null) {
            throw new ValidationException("Hotel must be provided in booking");
        }
        if (bookingAlreadyExists(booking.getHotel().getId(), booking.getDate())) {
            // -----------------
            throw new UniqueBookingException("A booking for this hotel and date already exists");
        }

        // 3. Check if Customer and Hotel exist (Part 2)
        if (booking.getCustomer() == null || booking.getCustomer().getId() == null) {
            throw new ValidationException("Customer must be provided in booking");
        }
        if (customerCrud.findById(booking.getCustomer().getId()) == null) {
            // -----------------
            throw new ValidationException("Invalid Customer ID: No customer found with ID " + booking.getCustomer().getId());
        }

        if (hotelCrud.findById(booking.getHotel().getId()) == null) {
            throw new ValidationException("Invalid Hotel ID: No hotel found with ID " + booking.getHotel().getId());
        }
    }

    /**
     * Checks if a booking for the same hotel and date already exists.
     */
    boolean bookingAlreadyExists(Long hotelId, LocalDate date) {
        return bookingCrud.findByHotelAndDate(hotelId, date) != null;
    }
}
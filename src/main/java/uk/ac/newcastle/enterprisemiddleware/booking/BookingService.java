package uk.ac.newcastle.enterprisemiddleware.booking;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class BookingService {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    BookingValidator validator;

    @Inject
    BookingRepository repository;

    List<Booking> findBookingsByCustomerId(Long customerId) {
        return repository.findByCustomerId(customerId);
    }

    @Transactional
    public Booking createBooking(Booking booking) throws ConstraintViolationException, ValidationException {
        log.info("BookingService.create() - Validating and Creating booking");

        // Validate the booking data
        validator.validateBooking(booking);

        // Write the booking to the database
        return repository.create(booking);
    }

    @Transactional
    Booking deleteBooking(Long id) throws Exception {
        log.info("BookingService.delete() - Deleting booking " + id);

        Booking booking = repository.findById(id);

        if (booking == null) {
            // Return null if booking not found
            return null;
        }

        return repository.delete(booking);
    }
}
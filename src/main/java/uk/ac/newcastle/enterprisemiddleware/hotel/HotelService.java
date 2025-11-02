package uk.ac.newcastle.enterprisemiddleware.hotel;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class HotelService {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    HotelValidator validator;

    @Inject
    HotelRepository repository;

    /**
     * Returns a List of all persisted Hotel objects.
     */
    List<Hotel> getAllHotels() {
        return repository.findAllOrderedByName();
    }

    /**
     * Writes the provided Hotel object to the application database.
     * Validates the data in the provided Hotel object.
     */
    @Transactional
    Hotel createHotel(Hotel hotel) throws ConstraintViolationException, UniquePhoneException {
        log.info("HotelService.create() - Validating and Creating " + hotel.getName());

        // Validate the hotel data
        validator.validateHotel(hotel);

        // Write the hotel to the database
        return repository.create(hotel);
    }
    //Part 2
    @Transactional
    Hotel deleteHotel(Long id) throws Exception {
        log.info("HotelService.delete() - Deleting hotel " + id);
        Hotel hotel = repository.findById(id);
        if (hotel == null) {
            return null;
        }
        return repository.delete(hotel);
    }
    //End of part 2
}
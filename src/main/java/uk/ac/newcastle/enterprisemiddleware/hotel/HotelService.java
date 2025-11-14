package uk.ac.newcastle.enterprisemiddleware.hotel;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.Valid;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Swapnil Sagar
 * */
@Dependent
public class HotelService {

    @Inject
    @Named("logger")
    Logger logger;

    @Inject
    HotelRepository hotelRepository;

    public List<Hotel> getAllHotel(@Valid Hotel hotel){
        return hotelRepository.getAllRecords();
    }

    public List<Hotel> getAllHotelInfo(){
        String jpql = "SELECT DISTINCT t FROM Hotel t " +
                "LEFT JOIN FETCH t.bookings b ";
        return hotelRepository.getAllRelatedRecords(jpql, null);
    }

    public Hotel getHotelById(long id){
        return hotelRepository.getRecordById(id);
    }

    public Hotel findByPhoneNumber(String phoneNumber) {
        // Assumes your GenricRepository has a method like getRecordByField(String fieldName, Object value)
        // If not, you'll need to add a custom method to HotelRepository
        return hotelRepository.getRecordByField("phoneNumber", phoneNumber);
    }

    public void createHotel(Hotel hotel) throws Exception {
        logger.info("Creating hotel: " + hotel.toString());
        hotelRepository.create(hotel);
    }

    public void deleteHotel(Long id){
        logger.info("Deleting Hotel with ID: " + id);
        hotelRepository.delete(hotelRepository.getRecordById(id));
    }
}

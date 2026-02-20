package uk.ac.newcastle.enterprisemiddleware.hotel;

import uk.ac.newcastle.enterprisemiddleware.hotelbooking.HotelBooking;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
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


    public List<Hotel> getAllHotels(){
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
        return hotelRepository.getRecordByField("phoneNumber", phoneNumber);
    }

    public void createHotel(Hotel hotel) throws Exception {
        logger.info("Creating hotel: " + hotel.toString());
        hotelRepository.create(hotel);

    }

    public boolean deleteHotel(Long id){
        logger.info("Deleting Hotel with ID: " + id);
        String jpql = "SELECT b FROM Hotel b WHERE b.id = :id";

        List<Hotel> bookings = hotelRepository.getAllRelatedRecords(jpql,
                Map.of("id", id));

        if (bookings.isEmpty()) {
            throw new EntityNotFoundException("No HotelBooking found with id " + id);
        }

        return hotelRepository.delete(hotelRepository.getRecordById(id)) != null;
    }
}

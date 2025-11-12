package uk.ac.newcastle.enterprisemiddleware.hotel;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.Valid;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Mayank Kunwar
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

    public void createHotel(Hotel hotel) throws Exception {
        logger.info("Creating hotel: " + hotel.toString());
        hotelRepository.create(hotel);
    }

    public void deleteHotel(Long id){
        logger.info("Deleting Hotel with ID: " + id);
        hotelRepository.delete(hotelRepository.getRecordById(id));
    }
}

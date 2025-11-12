package uk.ac.newcastle.enterprisemiddleware.hotelbooking;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Mayank Kunwar
 * */
@Dependent
public class HotelBookingService {

    @Inject
    @Named("logger")
    Logger logger;

    @Inject
    HotelBookingRepository hotelBookingRepository;

    public List<HotelBooking> getAllBookings() {
        return hotelBookingRepository.getAllRecords();
    }

    public HotelBooking createBooking(HotelBooking booking) throws Exception {
        return hotelBookingRepository.create(booking);
    }

    public HotelBooking updateBooking(HotelBooking booking) {
        hotelBookingRepository.update(booking);
        return booking;
    }

    public boolean deleteBookingRecord(long bookingId){
        String jpql = "SELECT b FROM HotelBooking b WHERE b.id = :id";
        List<HotelBooking> bookings = hotelBookingRepository.getAllRelatedRecords(jpql,
                Map.of("id", bookingId));

        if (bookings.isEmpty()) {
            throw new EntityNotFoundException("No HotelBooking found with id " + bookingId);
        }

        return hotelBookingRepository.delete(bookings.get(0)) != null;
    }

    public boolean deleteByGlobalBookingId(String globalBookingId){
        String jpql = "SELECT b FROM HotelBooking b WHERE b.globalBookingId = :globalBookingId";
        List<HotelBooking> bookings = hotelBookingRepository.getAllRelatedRecords(jpql,
                Map.of("globalBookingId", globalBookingId));

        if (bookings.isEmpty()) {
            throw new EntityNotFoundException("No HotelBooking found with id " + globalBookingId);
        }

        return hotelBookingRepository.delete(bookings.get(0)) != null;
    }
}

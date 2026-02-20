package uk.ac.newcastle.enterprisemiddleware.hotelbooking;

import uk.ac.newcastle.enterprisemiddleware.hotelbooking.exception.BookingDateConflictException;
import uk.ac.newcastle.enterprisemiddleware.hotelbooking.exception.InvalidBookingDateException;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Swapnil Sagar
 * */
@Dependent
public class HotelBookingService
{

    @Inject
    @Named("logger")
    Logger logger;


    @Inject
    HotelBookingRepository hotelBookingRepository;


    public List<HotelBooking> getAllBookings() {
        return hotelBookingRepository.getAllRecords();
    }

    public HotelBooking createBooking(HotelBooking booking) throws Exception {
        Date bookingDate = booking.getBookingDate();
        if (bookingDate.before(new Date(System.currentTimeMillis()))) {
            throw new InvalidBookingDateException("Booking date is before current date");
        }
        if(bookingAlreadyExist(booking.getHotel().getId(), bookingDate)){
            throw new BookingDateConflictException("Booking date is already exist");
        }
        return hotelBookingRepository.create(booking);
    }



    private boolean bookingAlreadyExist(Long id, Date bookingDate) {
        String jpql = "SELECT b FROM HotelBooking b WHERE b.bookingDate = :bookingDate AND b.hotel.id = :id";
        List<HotelBooking> bookings = hotelBookingRepository.getAllRelatedRecords(jpql,
                Map.of("bookingDate", bookingDate, "id", id));
        return !bookings.isEmpty();

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

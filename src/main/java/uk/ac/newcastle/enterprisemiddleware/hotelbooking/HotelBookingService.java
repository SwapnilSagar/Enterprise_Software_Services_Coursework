package uk.ac.newcastle.enterprisemiddleware.hotelbooking;

import uk.ac.newcastle.enterprisemiddleware.hotelbooking.exception.BookingDateConflictException;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityNotFoundException;
import java.util.Calendar;
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
        // FIX #6 — Removed redundant manual date check: the @Future bean validation annotation on
        // HotelBooking.bookingDate already rejects past dates before this method is even reached.
        if (bookingAlreadyExist(booking.getHotel().getId(), bookingDate)) {
            throw new BookingDateConflictException("A booking already exists for this hotel on that date");
        }
        return hotelBookingRepository.create(booking);
    }



    private boolean bookingAlreadyExist(Long id, Date bookingDate) {
        // FIX #9 — Changed from exact timestamp comparison to calendar-date range comparison.
        // The old JPQL used `b.bookingDate = :bookingDate` which compares milliseconds exactly,
        // meaning two bookings 1ms apart for the same hotel would NOT be flagged as a conflict.
        // Hotels should only allow one booking per calendar day, so we compare using a date range.
        Calendar cal = Calendar.getInstance();
        cal.setTime(bookingDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startOfDay = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date startOfNextDay = cal.getTime();

        String jpql = "SELECT b FROM HotelBooking b WHERE b.hotel.id = :id " +
                "AND b.bookingDate >= :startOfDay AND b.bookingDate < :startOfNextDay";
        List<HotelBooking> bookings = hotelBookingRepository.getAllRelatedRecords(jpql,
                Map.of("id", id, "startOfDay", startOfDay, "startOfNextDay", startOfNextDay));
        return !bookings.isEmpty();
    }



    public HotelBooking updateBooking(HotelBooking booking) {
        hotelBookingRepository.update(booking);
        return booking;
    }

    // FIX #12 — Changed return type from boolean to void. The old method claimed to return boolean
    // but always either returned true or threw an exception — the false branch was dead code.
    // Callers now catch EntityNotFoundException for the not-found case.
    public void deleteBookingRecord(long bookingId) {
        String jpql = "SELECT b FROM HotelBooking b WHERE b.id = :id";
        List<HotelBooking> bookings = hotelBookingRepository.getAllRelatedRecords(jpql,
                Map.of("id", bookingId));

        if (bookings.isEmpty()) {
            throw new EntityNotFoundException("No HotelBooking found with id " + bookingId);
        }
        hotelBookingRepository.delete(bookings.get(0));
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

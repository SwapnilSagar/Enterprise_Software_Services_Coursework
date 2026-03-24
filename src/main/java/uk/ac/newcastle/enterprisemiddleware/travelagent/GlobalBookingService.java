package uk.ac.newcastle.enterprisemiddleware.travelagent;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Swapnil Sagar
 *
 *
 * */
@Dependent
public class GlobalBookingService {

    @Inject
    @Named("logger")
    Logger logger;

    @Inject
    GlobalBookingRepository repository;



    public GlobalBooking getBookingById(String id){
        return repository.getRecordById(id);
    }

    public List<GlobalBooking> getAllBookings(){
        return repository.getAllRecords();
    }



    public void createBooking(GlobalBooking booking) throws Exception {
        logger.info("Creating global booking with ID: " + booking.getId());
        repository.create(booking);
    }

    public void updateBooking(GlobalBooking booking){
        logger.info("Updating global booking with ID: " + booking.getId());
        repository.update(booking);
    }

    public void deleteBooking(String id){
        logger.info("Deleting global booking with ID: " + id);
        repository.delete(repository.getRecordById(id));


    }

    public List<String> getBookingIdByCustomerId(Long customerId) {
        // FIX #11 — Replaced manual for-loop + ArrayList with stream().map().collect() for cleaner, idiomatic Java.
        String jpql = "SELECT b from GlobalBooking b WHERE b.customer.id = :id";
        List<GlobalBooking> globalBookings = repository.getAllRelatedRecords(jpql, Map.of("id", customerId));
        return globalBookings.stream()
                .map(GlobalBooking::getId)
                .collect(Collectors.toList());
    }
}

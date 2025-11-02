package uk.ac.newcastle.enterprisemiddleware.travelagent;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import uk.ac.newcastle.enterprisemiddleware.booking.Booking;
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.customer.CustomerRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.validation.ValidationException;
import javax.ws.rs.WebApplicationException;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class TravelAgentService {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    EntityManager em;

    @Inject
    CustomerRepository customerRepository;

    // Inject the REST clients
    @Inject
    @RestClient
    HotelServiceClient hotelServiceClient;

    @Inject
    @RestClient
    TaxiServiceClient taxiServiceClient;

    @Inject
    @RestClient
    FlightServiceClient flightServiceClient;

    /**
     * Creates an aggregate booking.
     * This method contains the core rollback logic for Part 3.
     */
    @Transactional
    public TravelAgentBooking createTravelAgentBooking(Booking hotelBooking, Booking taxiBooking, Booking flightBooking, Long customerId) {

        Customer customer = customerRepository.findById(customerId);
        if (customer == null) {
            throw new ValidationException("Invalid Customer ID");
        }

        Booking detachedHotelBooking = null;
        Booking createdTaxiBooking = null;
        Booking createdFlightBooking = null;

        try {
            // 1. Book Hotel (your local service)
            hotelBooking.setCustomer(customer);
            detachedHotelBooking = hotelServiceClient.createBooking(hotelBooking); // This is a detached POJO
            log.info("Successfully booked hotel: ID " + detachedHotelBooking.getId());

            // 2. Book Taxi (remote service)
            taxiBooking.setCustomer(customer);
            createdTaxiBooking = taxiServiceClient.createBooking(taxiBooking);
            log.info("Successfully booked taxi: ID " + createdTaxiBooking.getId());

            // 3. Book Flight (remote service)
            flightBooking.setCustomer(customer);
            createdFlightBooking = flightServiceClient.createBooking(flightBooking);
            log.info("Successfully booked flight: ID " + createdFlightBooking.getId());

            // 4. All bookings succeeded.
            // Fetch the MANAGED version of the hotel booking from our local EntityManager
            Booking managedHotelBooking = em.find(Booking.class, detachedHotelBooking.getId());
            if (managedHotelBooking == null) {
                throw new Exception("Failed to find local hotel booking after creation.");
            }
            // -----------------------

            // 5. Create the aggregate booking.
            TravelAgentBooking aggregateBooking = new TravelAgentBooking();
            aggregateBooking.setCustomer(customer);
            aggregateBooking.setHotelBooking(managedHotelBooking); // Use the MANAGED entity
            aggregateBooking.setTaxiBookingId(createdTaxiBooking.getId());
            aggregateBooking.setFlightBookingId(createdFlightBooking.getId());

            em.persist(aggregateBooking);
            return aggregateBooking;

        } catch (Exception e) {
            // --- CRITICAL ROLLBACK LOGIC ---
            log.warning("Booking failed: " + e.getMessage() + ". Initiating rollback.");

            // Try to cancel hotel booking if it was created
            if (detachedHotelBooking != null) { // Use the detached object's ID
                try {
                    hotelServiceClient.deleteBooking(detachedHotelBooking.getId());
                    log.info("Successfully rolled back hotel booking: ID " + detachedHotelBooking.getId());
                } catch (Exception rollbackException) {
                    log.severe("Failed to roll back hotel booking: ID " + detachedHotelBooking.getId() + " - " + rollbackException.getMessage());
                }
            }

            // Try to cancel taxi booking if it was created
            if (createdTaxiBooking != null) {
                try {
                    taxiServiceClient.deleteBooking(createdTaxiBooking.getId());
                    log.info("Successfully rolled back taxi booking: ID " + createdTaxiBooking.getId());
                } catch (Exception rollbackException) {
                    log.severe("Failed to roll back taxi booking: ID " + createdTaxiBooking.getId() + " - " + rollbackException.getMessage());
                }
            }

            // No need to roll back the flight booking,
            // because if it fails, it's the last one in the chain.

            // Throw a new exception to inform the user
            throw new WebApplicationException("Booking failed, all partial bookings have been cancelled.", 500);
        }
    }

    /**
     * Lists all TravelAgentBookings for a given customer.
     */
    public List<TravelAgentBooking> findByCustomerId(Long customerId) {
        TypedQuery<TravelAgentBooking> query = em.createNamedQuery("TravelAgentBooking.findByCustomerId", TravelAgentBooking.class)
                .setParameter("customerId", customerId);
        return query.getResultList();
    }
}
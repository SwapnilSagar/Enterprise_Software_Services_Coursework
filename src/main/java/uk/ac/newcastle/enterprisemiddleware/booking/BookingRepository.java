package uk.ac.newcastle.enterprisemiddleware.booking;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class BookingRepository {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    EntityManager em;

    Booking findById(Long id) {
        return em.find(Booking.class, id);
    }

    List<Booking> findByCustomerId(Long customerId) {
        TypedQuery<Booking> query = em.createNamedQuery(Booking.FIND_BY_CUSTOMER_ID, Booking.class)
                .setParameter("customerId", customerId);
        return query.getResultList();
    }

    Booking findByHotelAndDate(Long hotelId, LocalDate date) {
        try {
            TypedQuery<Booking> query = em.createNamedQuery(Booking.FIND_BY_HOTEL_AND_DATE, Booking.class)
                    .setParameter("hotelId", hotelId) // This is still correct
                    .setParameter("date", date);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null; // Return null if no booking is found
        }
    }

    Booking create(Booking booking) {
        log.info("BookingRepository.create() - Creating booking for customer " + booking.getCustomer());
        em.persist(booking);
        return booking;
    }

    Booking delete(Booking booking) {
        log.info("BookingRepository.delete() - Deleting booking " + booking.getId());
        if (booking.getId() != null) {
            em.remove(em.merge(booking));
        } else {
            log.info("BookingRepository.delete() - No ID was found so can't Delete.");
        }
        return booking;
    }
}
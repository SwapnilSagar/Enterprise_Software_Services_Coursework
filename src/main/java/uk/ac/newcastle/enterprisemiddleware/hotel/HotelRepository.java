package uk.ac.newcastle.enterprisemiddleware.hotel;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class HotelRepository {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    EntityManager em;

    List<Hotel> findAllOrderedByName() {
        TypedQuery<Hotel> query = em.createNamedQuery(Hotel.FIND_ALL, Hotel.class);
        return query.getResultList();
    }

    public Hotel findById(Long id) {
        return em.find(Hotel.class, id);
    }

    Hotel findByPhoneNumber(String phoneNumber) {
        try {
            TypedQuery<Hotel> query = em.createNamedQuery(Hotel.FIND_BY_PHONE, Hotel.class).setParameter("phoneNumber", phoneNumber);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null; // Return null if no hotel is found
        }
    }

    Hotel create(Hotel hotel) {
        log.info("HotelRepository.create() - Creating " + hotel.getName());
        em.persist(hotel);
        return hotel;
    }
    //Part 2
    public Hotel delete(Hotel hotel) {
        log.info("HotelRepository.delete() - Deleting " + hotel.getName());
        if (hotel.getId() != null) {
            em.remove(em.merge(hotel));
        } else {
            log.info("HotelRepository.delete() - No ID was found so can't Delete.");
        }
        return hotel;
    }
    //End of part 2
}
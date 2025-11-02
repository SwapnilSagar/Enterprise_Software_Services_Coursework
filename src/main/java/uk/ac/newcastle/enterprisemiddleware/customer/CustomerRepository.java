package uk.ac.newcastle.enterprisemiddleware.customer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class CustomerRepository {

    @Inject
    @Named("logger") // Inject the logger from Resources.java
    Logger log;

    @Inject
    EntityManager em;

    List<Customer> findAllOrderedByName() {
        TypedQuery<Customer> query = em.createNamedQuery(Customer.FIND_ALL, Customer.class);
        return query.getResultList();
    }

    public Customer findById(Long id) {
        return em.find(Customer.class, id);
    }

    Customer findByEmail(String email) {
        try {
            TypedQuery<Customer> query = em.createNamedQuery(Customer.FIND_BY_EMAIL, Customer.class).setParameter("email", email);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null; // Return null if no customer is found
        }
    }

    Customer create(Customer customer) {
        log.info("CustomerRepository.create() - Creating " + customer.getName());
        em.persist(customer);
        return customer;
    }
    //Part 2
    public Customer delete(Customer customer) {
        log.info("CustomerRepository.delete() - Deleting " + customer.getName());
        if (customer.getId() != null) {
            em.remove(em.merge(customer));
        } else {
            log.info("CustomerRepository.delete() - No ID was found so can't Delete.");
        }
        return customer;
    }
    //End of part 2
}
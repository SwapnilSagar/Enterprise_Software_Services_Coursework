package uk.ac.newcastle.enterprisemiddleware.customer;

import uk.ac.newcastle.enterprisemiddleware.repository.GenricRepository;

import javax.enterprise.context.RequestScoped;

/**
 * @author Swapnil Sagar
 * */
@RequestScoped
public class CustomerRepository extends GenricRepository<Customer, Long> {
    public CustomerRepository() {
        super(Customer.class);
    }
}

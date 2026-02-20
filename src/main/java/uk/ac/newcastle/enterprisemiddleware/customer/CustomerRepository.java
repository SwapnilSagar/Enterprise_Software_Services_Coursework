package uk.ac.newcastle.enterprisemiddleware.customer;

import uk.ac.newcastle.enterprisemiddleware.repository.CommonRepository;

import javax.enterprise.context.RequestScoped;

/**
 * @author Swapnil Sagar
 * */
@RequestScoped
public class CustomerRepository extends CommonRepository<Customer, Long>
{
    public CustomerRepository() {
        super(Customer.class);
    }
}

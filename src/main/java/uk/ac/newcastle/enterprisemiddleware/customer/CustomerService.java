package uk.ac.newcastle.enterprisemiddleware.customer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class CustomerService {

    @Inject
    @Named("logger") // Inject the logger
    Logger log;

    @Inject
    CustomerValidator validator;

    @Inject
    CustomerRepository repository;

    List<Customer> getAllCustomers() {
        return repository.findAllOrderedByName();
    }

    @Transactional
    public Customer createCustomer(Customer customer) throws ConstraintViolationException, UniqueEmailException {
        log.info("CustomerService.create() - Validating and Creating " + customer.getName());

        // Validate the customer data
        validator.validateCustomer(customer);

        // Write the customer to the database
        return repository.create(customer);
    }


    //Part 2
    @Transactional
    Customer deleteCustomer(Long id) throws Exception {
        log.info("CustomerService.delete() - Deleting customer " + id);
        Customer customer = repository.findById(id);
        if (customer == null) {
            return null; // Let REST service handle 404
        }
        return repository.delete(customer);
    }
    //End of part 2
}
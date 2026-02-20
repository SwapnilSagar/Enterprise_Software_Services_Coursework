package uk.ac.newcastle.enterprisemiddleware.customer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


/**
 * @Dependent — CDI scope creates a new instance every time it’s injected.
 *
 *
 * Appropriate for stateless services and it’s lightweight.
 *
 * @author Swapnil Sagar
 * */

@Dependent
public class CustomerService {

    @Inject
    @Named("logger")

    Logger logger;

    @Inject
    CustomerRepository customerRepository;

    public List<Customer> getAllCustomers(){
        return customerRepository.getAllRecords();
    }



    public List<Customer> getAllCustomersInfo(){
        String jpql = "SELECT DISTINCT c FROM Customer c " +
                "LEFT JOIN FETCH c.bookings b ";
        return customerRepository.getAllRelatedRecords(jpql, null);
    }

    public Customer getCustomerById(long id){
        return customerRepository.getRecordById(id);
    }
    public Customer findByEmail(String email) {
        return customerRepository.getRecordByField("email", email);
    }



    public void createCustomer(Customer customer) throws Exception {
        logger.info("Creating customer: " + customer.toString());
        customerRepository.create(customer);
    }



    public void delete(Long id){
        logger.info("Deleting Customer with ID: " + id);
        Customer customer = customerRepository.getRecordById(id);
        if (customer != null) {
            customerRepository.delete(customer);
        }
    }
}

//Part 2
package uk.ac.newcastle.enterprisemiddleware.booking;

import uk.ac.newcastle.enterprisemiddleware.customer.Customer;

/**
 * <p>Simple POJO representing "Guest Booking" request.</p>
 *
 * <p>It deserializes a JSON request body having both new Customer and new Booking.</p>
 */
public class GuestBooking {

    private Customer customer;
    private Booking booking;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }
}
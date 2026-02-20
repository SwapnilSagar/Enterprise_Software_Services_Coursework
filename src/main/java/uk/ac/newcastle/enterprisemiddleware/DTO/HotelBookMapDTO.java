package uk.ac.newcastle.enterprisemiddleware.DTO;

import lombok.Data;
import uk.ac.newcastle.enterprisemiddleware.hotelbooking.Status;
import uk.ac.newcastle.enterprisemiddleware.customer.CustomerPayload;

import java.util.Date;

/**
 * @author Swapnil Sagar
 *
 *
 * */

@Data
public class HotelBookMapDTO
{
    private Long id;
    private String txID;
    private Status status;


    private Date bookingDate;
    private CustomerPayload customer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTxID() {
        return txID;
    }


    public void setTxID(String txID) {
        this.txID = txID;
    }


    public Status getStatus() {
        return status;
    }



    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getBookingDate() {
        return bookingDate;
    }


    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public CustomerPayload getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerPayload customer) {
        this.customer = customer;
    }
}

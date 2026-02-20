package uk.ac.newcastle.enterprisemiddleware.travelagent.client.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import uk.ac.newcastle.enterprisemiddleware.travelagent.Taxi;
import uk.ac.newcastle.enterprisemiddleware.hotelbooking.Status;

import java.util.Date;

/**
 * @author Swapnil Sagar
 * */

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaxiBookingResponse {
    private long id;


    private Status status;
    private String globalBookingId;
    private Taxi taxi;


    private Date bookingDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }



    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getGlobalBookingId() {
        return globalBookingId;
    }

    public void setGlobalBookingId(String globalBookingId) {
        this.globalBookingId = globalBookingId;
    }

    public Taxi getTaxi() {
        return taxi;
    }

    public void setTaxi(Taxi taxi) {
        this.taxi = taxi;
    }



    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }
}

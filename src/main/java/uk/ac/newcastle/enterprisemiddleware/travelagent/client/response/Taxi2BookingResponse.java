package uk.ac.newcastle.enterprisemiddleware.travelagent.client.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import uk.ac.newcastle.enterprisemiddleware.travelagent.Taxi2;

import java.util.Date;

/**
 * @author Swapnil Sagar
 * */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Taxi2BookingResponse {
    private long bookingId;


    private String globalId;
    private Taxi2 taxi;
    private Date bookingDate;

    public long getBookingId() {
        return bookingId;
    }

    public void setBookingId(long bookingId) {
        this.bookingId = bookingId;
    }



    public String getGlobalId() {
        return globalId;
    }

    public void setGlobalId(String globalId) {
        this.globalId = globalId;
    }



    public Taxi2 getTaxi() {
        return taxi;
    }



    public void setTaxi(Taxi2 taxi) {
        this.taxi = taxi;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }
}

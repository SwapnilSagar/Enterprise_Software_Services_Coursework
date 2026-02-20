package uk.ac.newcastle.enterprisemiddleware.travelagent.client.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import uk.ac.newcastle.enterprisemiddleware.hotel.Hotel;
import uk.ac.newcastle.enterprisemiddleware.hotelbooking.Status;

import java.util.Date;

/**
 *
 * @author Swapnil Sagar
 *
 *
 * */



@JsonIgnoreProperties(ignoreUnknown = true)
public class HotelBookingResponse {
    private long id;
    private Status status;
    private String globalBookingId;


    private Hotel hotel;
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

    public Hotel getHotel() {
        return hotel;
    }



    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }
}

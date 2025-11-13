package uk.ac.newcastle.enterprisemiddleware.hotelbooking;

import uk.ac.newcastle.enterprisemiddleware.hotel.HotelPayload;

import java.util.Date;

/**
 * @author Swapnil Sagar
 * */
public class HotelBookingPayload {
    private Long id;
    private Status status;
    private String globalBookingId;
    private HotelPayload hotel;
    private Date bookingDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public HotelPayload getHotel() {
        return hotel;
    }

    public void setHotel(HotelPayload hotel) {
        this.hotel = hotel;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }
}

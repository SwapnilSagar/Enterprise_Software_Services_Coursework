package uk.ac.newcastle.enterprisemiddleware.dto.hotelbookings;

import uk.ac.newcastle.enterprisemiddleware.hotelbooking.Status;
import uk.ac.newcastle.enterprisemiddleware.dto.customer.CustomerDTO;
import uk.ac.newcastle.enterprisemiddleware.dto.hotel.HotelDTO;

import java.util.Date;

/**
 * @author Mayank Kunwar
 * */
public class HotelBookingDTO {
    private Long id;
    private Status status;
    private String globalBookingId;
    private HotelDTO hotel;
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

    public HotelDTO getHotel() {
        return hotel;
    }

    public void setHotel(HotelDTO hotel) {
        this.hotel = hotel;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }
}

package uk.ac.newcastle.enterprisemiddleware.hotelbooking;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author Swapnil Sagar
 * */
public class HotelBookingRequest {

    @NotNull
    private String globalBookingId;


    @NotNull(message = "Hotel ID is required")
    private long hotelId;



    @NotNull(message = "Booking date is required")
    @Future(message = "Booking date must be in the future")
    private Date bookingDate;




    public String getGlobalBookingId() {
        return globalBookingId;
    }


    public long getHotelId() {
        return hotelId;
    }


    public Date getBookingDate() {
        return bookingDate;
    }

    public void setGlobalBookingId(String globalBookingId) {
        this.globalBookingId = globalBookingId;
    }




    public void setHotelId(long hotelId) {
        this.hotelId = hotelId;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    @Override

    public String toString() {
        return "HotelBookingRequest{" +
                "txId='" + globalBookingId + '\'' +
                ", hotelId=" + hotelId +
                ", bookingDate=" + bookingDate +
                '}';

    }
}

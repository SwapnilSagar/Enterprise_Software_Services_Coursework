package uk.ac.newcastle.enterprisemiddleware.agent.client.request;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author Swapnil Sagar
 * */
public class HotelBookingRequest {

    @NotNull
    @NotEmpty
    private String globalBookingId;

    @NotNull(message = "Hotel ID is required")
    private long hotelId;

    @NotNull(message = "Booking date is required")
    @Future(message = "Booking date must be in the future")
    private Date bookingDate;

    public @NotNull @NotEmpty String getGlobalBookingId() {
        return globalBookingId;
    }

    public void setGlobalBookingId(@NotNull @NotEmpty String globalBookingId) {
        this.globalBookingId = globalBookingId;
    }

    @NotNull(message = "Hotel ID is required")
    public long getHotelId() {
        return hotelId;
    }

    public void setHotelId(@NotNull(message = "Hotel ID is required") long hotelId) {
        this.hotelId = hotelId;
    }

    public @NotNull(message = "Booking date is required") @Future(message = "Booking date must be in the future") Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(@NotNull(message = "Booking date is required") @Future(message = "Booking date must be in the future") Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    @Override
    public String toString() {
        return "HotelBookingRequest{" +
                "globalBookingId='" + globalBookingId + '\'' +
                ", hotelId=" + hotelId +
                ", bookingDate=" + bookingDate +
                '}';
    }
}

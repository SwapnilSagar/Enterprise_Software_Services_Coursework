package uk.ac.newcastle.enterprisemiddleware.agent.client.request;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author Swapnil Sagar
 * */
public class FlightBookingRequest {

    @NotNull @NotEmpty
    private String globalBookingId;

    @NotNull(message = "Flight ID is required")
    private long flightId;

    @NotNull(message = "Booking date is required")
    @Future(message = "Booking date must be in the future")
    private Date bookingDate;

    public @NotNull @NotEmpty String getGlobalBookingId() {
        return globalBookingId;
    }

    public void setGlobalBookingId(@NotNull @NotEmpty String globalBookingId) {
        this.globalBookingId = globalBookingId;
    }

    @NotNull(message = "Flight ID is required")
    public long getFlightId() {
        return flightId;
    }

    public void setFlightId(@NotNull(message = "Flight ID is required") long flightId) {
        this.flightId = flightId;
    }

    public @NotNull(message = "Booking date is required") @Future(message = "Booking date must be in the future") Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(@NotNull(message = "Booking date is required") @Future(message = "Booking date must be in the future") Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    @Override
    public String toString() {
        return "FlightBookingRequest{" +
                "globalBookingId='" + globalBookingId + '\'' +
                ", flightId=" + flightId +
                ", bookingDate=" + bookingDate +
                '}';
    }
}

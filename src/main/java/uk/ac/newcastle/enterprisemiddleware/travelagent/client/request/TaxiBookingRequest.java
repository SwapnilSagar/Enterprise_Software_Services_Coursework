package uk.ac.newcastle.enterprisemiddleware.travelagent.client.request;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author Swapnil Sagar
 * */
public class TaxiBookingRequest {

    @NotNull
    @NotEmpty
    private String globalId;



    @NotNull(message = "Taxi ID is required")
    private long taxiId;



    @NotNull(message = "Booking date is required")
    @Future(message = "Booking date must be in the future")
    private Date bookingDate;

    public @NotNull @NotEmpty String getGlobalId() {
        return globalId;
    }



    public void setGlobalId(@NotNull @NotEmpty String globalId) {
        this.globalId = globalId;
    }

    @NotNull(message = "Taxi ID is required")
    public long getTaxiId() {
        return taxiId;
    }

    public void setTaxiId(@NotNull(message = "Taxi ID is required") long taxiId) {
        this.taxiId = taxiId;
    }

    public @NotNull(message = "Booking date is required") @Future(message = "Booking date must be in the future") Date getBookingDate() {
        return bookingDate;
    }



    public void setBookingDate(@NotNull(message = "Booking date is required") @Future(message = "Booking date must be in the future") Date bookingDate) {
        this.bookingDate = bookingDate;
    }



    @Override
    public String toString() {
        return "TaxiBookingRequest{" +
                "globalId='" + globalId + '\'' +
                ", taxiId=" + taxiId +
                ", bookingDate=" + bookingDate +
                '}';
    }
}

package uk.ac.newcastle.enterprisemiddleware.agent;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author Mayank Kunwar
 * */
public class GlobalBookingRequest {

    @Future
    private Date futureDate;

    @NotNull
    private Long flightID;

    @NotNull
    private Long taxiID;

    @NotNull
    private Long hotelID;

    @NotNull
    private Long customerID;

    public @Future Date getFutureDate() {
        return futureDate;
    }

    public void setFutureDate(@Future Date futureDate) {
        this.futureDate = futureDate;
    }

    public @NotNull Long getFlightID() {
        return flightID;
    }

    public void setFlightID(@NotNull Long flightID) {
        this.flightID = flightID;
    }

    public @NotNull Long getTaxiID() {
        return taxiID;
    }

    public void setTaxiID(@NotNull Long taxiID) {
        this.taxiID = taxiID;
    }

    public @NotNull Long getHotelID() {
        return hotelID;
    }

    public void setHotelID(@NotNull Long hotelID) {
        this.hotelID = hotelID;
    }

    public @NotNull Long getCustomerID() {
        return customerID;
    }

    public void setCustomerID(@NotNull Long customerID) {
        this.customerID = customerID;
    }
}

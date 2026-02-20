package uk.ac.newcastle.enterprisemiddleware.travelagent;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author Swapnil Sagar
 * */
public class GlobalBookingRequest {

    @Future
    private Date futureDate;

    @NotNull
    private Long taxi2ID;



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

    public @NotNull Long getTaxi2ID() {
        return taxi2ID;
    }

    public void setTaxi2ID(@NotNull Long taxi2ID) {
        this.taxi2ID = taxi2ID;
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

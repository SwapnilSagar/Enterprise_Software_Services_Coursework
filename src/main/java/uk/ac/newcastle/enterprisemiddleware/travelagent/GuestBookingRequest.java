package uk.ac.newcastle.enterprisemiddleware.travelagent;

import javax.validation.constraints.*;
import java.util.Date;

/**
 * @author Swapnil Sagar
 *
 *
 * */
public class GuestBookingRequest {

    @Future
    private Date futureDate;



    @NotNull
    private Long flightID;

    @NotNull
    private Long taxiID;

    @NotNull
    private Long hotelID;



    @NotNull
    @NotEmpty
    @Size(max = 49, message = "Name must be less than 50 characters")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Name must contain only letters and spaces")
    private String customerName;



    @NotNull
    @NotEmpty
    @Email(message = "The email address must be in the format of name@domain.com")
    private String email;



    @NotNull
    @Pattern(regexp = "^0\\d{10}$", message = "Must start with 0, contain only digits, and be 11 digits long")
    private String phoneNumber;

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

    public @NotNull @NotEmpty @Size(max = 49, message = "Name must be less than 50 characters") @Pattern(regexp = "^[A-Za-z ]+$", message = "Name must contain only letters and spaces") String getCustomerName() {
        return customerName;
    }


    public void setCustomerName(@NotNull @NotEmpty @Size(max = 49, message = "Name must be less than 50 characters") @Pattern(regexp = "^[A-Za-z ]+$", message = "Name must contain only letters and spaces") String customerName) {
        this.customerName = customerName;
    }

    public @NotNull @NotEmpty @Email(message = "The email address must be in the format of name@domain.com") String getEmail() {
        return email;
    }



    public void setEmail(@NotNull @NotEmpty @Email(message = "The email address must be in the format of name@domain.com") String email) {
        this.email = email;
    }

    public @NotNull @Pattern(regexp = "^0\\d{10}$", message = "Must start with 0, contain only digits, and be 11 digits long") String getPhoneNumber() {
        return phoneNumber;
    }



    public void setPhoneNumber(@NotNull @Pattern(regexp = "^0\\d{10}$", message = "Must start with 0, contain only digits, and be 11 digits long") String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

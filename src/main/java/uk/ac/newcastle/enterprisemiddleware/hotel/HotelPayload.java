package uk.ac.newcastle.enterprisemiddleware.hotel;

import lombok.Data;

import java.util.List;

/**
 * @author Swapnil Sagar
 * */
@Data
public class HotelPayload {
    private Long id;
    private String name;
    private String postcode;
    private String phoneNumber;
    private List<HotelBookMapPayload> bookings;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<HotelBookMapPayload> getBookings() {
        return bookings;
    }

    public void setBookings(List<HotelBookMapPayload> bookings) {
        this.bookings = bookings;
    }
}

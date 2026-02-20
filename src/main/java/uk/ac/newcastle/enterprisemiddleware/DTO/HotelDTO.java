package uk.ac.newcastle.enterprisemiddleware.DTO;

import lombok.Data;

import java.util.List;

/**
 * @author Swapnil Sagar
 * */
@Data
public class HotelDTO {
    private Long id;
    private String name;
    private String postcode;

    private String phoneNumber;
    private List<HotelBookMapDTO> bookings;

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

    public List<HotelBookMapDTO> getBookings() {
        return bookings;
    }

    public void setBookings(List<HotelBookMapDTO> bookings) {
        this.bookings = bookings;
    }
}

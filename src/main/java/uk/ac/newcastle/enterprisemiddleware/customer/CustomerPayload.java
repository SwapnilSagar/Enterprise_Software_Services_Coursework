package uk.ac.newcastle.enterprisemiddleware.customer;

import lombok.Data;
import uk.ac.newcastle.enterprisemiddleware.DTO.CustomerBookMapDTO;

import java.util.List;

/**
 * @author Swapnil Sagar
 * */
@Data
public class CustomerPayload
{
    private Long id;
    private String name;
    private String email;

    private String phoneNumber;
    private List<CustomerBookMapDTO> bookings;

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

    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }


    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<CustomerBookMapDTO> getBookings() {
        return bookings;
    }

    public void setBookings(List<CustomerBookMapDTO> bookings) {
        this.bookings = bookings;
    }
}
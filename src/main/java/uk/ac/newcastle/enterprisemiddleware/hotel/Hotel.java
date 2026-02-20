package uk.ac.newcastle.enterprisemiddleware.hotel;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import uk.ac.newcastle.enterprisemiddleware.hotelbooking.HotelBooking;

import javax.persistence.*;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

/**
 * @author Swapnil Sagar
 *
 *
 * */
@NoArgsConstructor
@Entity
@Table(name = "hotel", uniqueConstraints = @UniqueConstraint(columnNames = "phone_number"))
@XmlRootElement
public class Hotel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true)
    private Long id;



    @NotNull
    @NotEmpty
    @Size(max = 49, message = "Name must be less than 50 characters")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Name must contain only letters and spaces")
    private String name;



    @NotNull
    @Pattern(regexp = "^0\\d{10}$", message = "Must start with 0, contain only digits, and be 11 digits long")
    @Column(name = "phone_number")
    private String phoneNumber;

    @Pattern(
            regexp = "^[A-Za-z0-9]{6}$",
            message = "Postcode must be 6 alphanumeric characters"
    )
    private String postcode;



    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference(value = "hotel-bookings")
    @Schema(hidden = true)
    private List<HotelBooking> bookings;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public @NotNull @NotEmpty @Size(max = 49, message = "Name must be less than 50 characters") @Pattern(regexp = "^[A-Za-z ]+$", message = "Name must contain only letters and spaces") String getName() {
        return name;
    }

    public void setName(@NotNull @NotEmpty @Size(max = 49, message = "Name must be less than 50 characters") @Pattern(regexp = "^[A-Za-z ]+$", message = "Name must contain only letters and spaces") String name) {
        this.name = name;
    }


    public @NotNull @Pattern(regexp = "^0\\d{10}$", message = "Must start with 0, contain only digits, and be 11 digits long") String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(@NotNull @Pattern(regexp = "^0\\d{10}$", message = "Must start with 0, contain only digits, and be 11 digits long") String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }



    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(@Pattern(
            regexp = "^[A-Za-z0-9]{6}$",
            message = "Postcode must be 6 alphanumeric characters"
    ) String postcode) {
        this.postcode = postcode;
    }

    public List<HotelBooking> getBookings() {
        return bookings;
    }



    public void setBookings(List<HotelBooking> bookings) {
        this.bookings = bookings;
    }

    @Override
    public String toString()
    {
        return "Hotel{" +
                "id=" + id +
                ", postcode='" + postcode + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

package uk.ac.newcastle.enterprisemiddleware.hotel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

//Part 2
import uk.ac.newcastle.enterprisemiddleware.booking.Booking;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;
// End of part 2

@Entity
@NamedQueries({
        @NamedQuery(name = Hotel.FIND_ALL, query = "SELECT h FROM Hotel h ORDER BY h.name ASC"),
        @NamedQuery(name = Hotel.FIND_BY_PHONE, query = "SELECT h FROM Hotel h WHERE h.phoneNumber = :phoneNumber")
})
@Table(name = "hotel", uniqueConstraints = @UniqueConstraint(columnNames = "phone_number"))
public class Hotel implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String FIND_ALL = "Hotel.findAll";
    public static final String FIND_BY_PHONE = "Hotel.findByPhone";

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hotel_gen")
    @TableGenerator(name = "hotel_gen", allocationSize = 1, initialValue = 10)
    private Long id;

    @NotNull
    @Size(min = 1, max = 50)
    @Pattern(regexp = "^[A-Za-z]+$", message = "Name must be alphabetical (letters only, no spaces)")
    @Column(name = "name")
    private String name;

    @NotNull
    @Pattern(regexp = "^0[0-9]+$", message = "Phone number must start with 0 and be 11 digits long")
    @Column(name = "phone_number")
    private String phoneNumber;

    @NotNull
    @Size(min = 6, max = 6)
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Postcode must be 6 characters and alphanumeric")
    @Column(name = "postcode")
    private String postcode;

    //Part 2
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JsonIgnore // Prevents infinite loops
    private Set<Booking> bookings;


    public Set<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(Set<Booking> bookings) {
        this.bookings = bookings;
    }
    //End of part 2


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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hotel hotel = (Hotel) o;
        return phoneNumber.equals(hotel.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(phoneNumber);
    }
}
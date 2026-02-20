package uk.ac.newcastle.enterprisemiddleware.customer;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import uk.ac.newcastle.enterprisemiddleware.travelagent.GlobalBooking;

import javax.persistence.*;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @author Swapnil Sagar
 * */
@NoArgsConstructor
@Entity


@Table(name = "customer", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@XmlRootElement
public class Customer implements Serializable
{
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
    @NotEmpty
    @Email(message = "The email address must be in the format of name@domain.com")
    private String email;

    @NotNull
    @Pattern(regexp = "^0\\d{10}$", message = "Must start with 0, contain only digits, and be 11 digits long")
    private String phoneNumber;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference(value = "customer-bookings")
    @Schema(hidden = true)
    private List<GlobalBooking> bookings;

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(@NotNull @NotEmpty @Size(max = 49, message = "Name must be less than 50 characters") @Pattern(regexp = "^[A-Za-z ]+$", message = "Name must contain only letters and spaces") String name) {
        this.name = name;
    }

    public void setEmail(@NotNull @NotEmpty @Email(message = "The email address must be in the format of name@domain.com") String email) {
        this.email = email;
    }

    public void setPhoneNumber(@NotNull @Pattern(regexp = "^0\\d{10}$", message = "Must start with 0, contain only digits, and be 11 digits long") String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setBookings(List<GlobalBooking> bookings)
    {
        this.bookings = bookings;
    }

    public Long getId()
    {
        return this.id;
    }

    public  String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public List<GlobalBooking> getBookings() {
        return this.bookings;
    }

    @Override
    public String toString()
    {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id)
                && Objects.equals(email, customer.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

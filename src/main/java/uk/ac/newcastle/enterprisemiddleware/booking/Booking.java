package uk.ac.newcastle.enterprisemiddleware.booking;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

// Part 2
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.hotel.Hotel;
// End of part 2

@Entity
@NamedQueries({
        @NamedQuery(name = Booking.FIND_ALL, query = "SELECT b FROM Booking b"),
        // --- UPDATE THIS QUERY ---
        @NamedQuery(name = Booking.FIND_BY_CUSTOMER_ID, query = "SELECT b FROM Booking b WHERE b.customer.id = :customerId"),
        // --- UPDATE THIS QUERY ---
        @NamedQuery(name = Booking.FIND_BY_HOTEL_AND_DATE, query = "SELECT b FROM Booking b WHERE b.hotel.id = :hotelId AND b.date = :date")
})
@Table(name = "booking", uniqueConstraints = @UniqueConstraint(columnNames = {"hotel_id", "booking_date"}))
public class Booking implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String FIND_ALL = "Booking.findAll";
    public static final String FIND_BY_CUSTOMER_ID = "Booking.findAllByCustomerId";
    public static final String FIND_BY_HOTEL_AND_DATE = "Booking.findByHotelAndDate";

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "booking_gen")
    @TableGenerator(name = "booking_gen", allocationSize = 1, initialValue = 10)
    private Long id;

    //Part 2
    @NotNull(message = "Customer must be provided")
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    // ------------------------

    // --- REPLACE hotelId ---
    @NotNull(message = "Hotel must be provided")
    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;
    // End of part 2

    @NotNull(message = "Date must be provided")
    @Future(message = "Booking date must be in the future")
    @Column(name = "booking_date")
    private LocalDate date;




    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    //Part 2
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }
    //End of part 2
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return customer.equals(booking.customer) && hotel.equals(booking.hotel) && date.equals(booking.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customer, hotel, date);
    }
}
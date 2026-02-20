package uk.ac.newcastle.enterprisemiddleware.hotelbooking;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import uk.ac.newcastle.enterprisemiddleware.hotel.Hotel;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @author Swapnil Sagar
 * */
@NoArgsConstructor
@Entity

@Table(name = "hotel_booking", uniqueConstraints = @UniqueConstraint(columnNames = {"hotel_id", "global_booking_id", "booking_date"}))
@XmlRootElement
public class HotelBooking implements Serializable {



    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "global_booking_id")
    private String globalBookingId;

    @Column(name = "status")
    private Status status = Status.PENDING;



    @NotNull
    @ManyToOne
    @JoinColumn(name = "hotel_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_HOTEL_ID"))
    @JsonBackReference(value = "hotel-bookings")
    private Hotel hotel;

    @NotNull
    @Future(message = "Booking date must be in the future")
    @Column(name = "booking_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date bookingDate;



    public void setStatus(Status status) {
        this.status = status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setGlobalBookingId(String id) {
        this.globalBookingId = id;
    }

    public void setHotel(@NotNull Hotel hotel) {
        this.hotel = hotel;
    }

    public void setBookingDate(@NotNull @Future(message = "Booking date must be in the future") Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Long getId() {
        return id;
    }

    public @NotNull Hotel getHotel() {
        return hotel;
    }

    public @NotNull @Future(message = "Booking date must be in the future") Date getBookingDate() {
        return bookingDate;
    }

    public Status getStatus() {
        return status;
    }

    public @NotNull String getGlobalBookingId() {
        return globalBookingId;
    }

    @Override
    public String toString() {
        return "HotelBooking{" +
                "id=" + id +
                ", globalBookingId='" + globalBookingId + '\'' +
                ", status=" + status +
                ", hotel=" + hotel +
                ", bookingDate=" + bookingDate +
                '}';


    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HotelBooking that = (HotelBooking) o;
        return Objects.equals(id, that.id)
                && Objects.equals(hotel, that.hotel)
                && Objects.equals(bookingDate, that.bookingDate);
    }



    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

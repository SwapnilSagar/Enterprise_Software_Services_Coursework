package uk.ac.newcastle.enterprisemiddleware.travelagent;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.util.JsonUtils;

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
@Table(name = "global_booking")
@XmlRootElement
public class GlobalBooking implements Serializable {



    private static final long serialVersionUID = 1L;

    @Id
    private String id;



    @Column(name = "status")
    private GlobalStatus status = GlobalStatus.PENDING;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_CUSTOMER_ID"))
    @JsonBackReference(value = "customer-bookings")
    private Customer customer;



    @NotNull
    @Future(message = "Booking date must be in the future")
    @Column(name = "booking_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date bookingDate;



    @Column(columnDefinition = "TEXT", name = "booking_entity")  // for H2
    private String bookingJson;

    @Transient
    @Schema(hidden = true)
    private BookingEntity bookingEntity;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public GlobalStatus getStatus() {
        return status;
    }

    public void setStatus(GlobalStatus status) {
        this.status = status;
    }

    public String getBookingJson() {
        return bookingJson;
    }



    public void setBookingJson(String bookingJson) {
        this.bookingJson = bookingJson;
    }

    public BookingEntity getBookingEntity() {
        return bookingEntity;
    }

    public void setBookingEntity(BookingEntity bookingEntity) {
        this.bookingEntity = bookingEntity;
        // convert object to JSON for persistence
        this.bookingJson = JsonUtils.toJson(bookingEntity);
    }



    public @NotNull Customer getCustomer() {
        return customer;
    }

    public void setCustomer(@NotNull Customer customer) {
        this.customer = customer;
    }



    public @NotNull @Future(message = "Booking date must be in the future") Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(@NotNull @Future(message = "Booking date must be in the future") Date bookingDate) {
        this.bookingDate = bookingDate;
    }



    @PrePersist
    @PreUpdate
    private void serializeBookingEntity() {
        if (bookingEntity != null) {
            bookingJson = JsonUtils.toJson(bookingEntity);
        }
    }

    @PostLoad
    private void deserializeBookingEntity() {
        if (bookingJson != null) {
            bookingEntity = JsonUtils.fromJson(bookingJson, BookingEntity.class);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GlobalBooking that = (GlobalBooking) o;
        return Objects.equals(id, that.id);
    }



    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

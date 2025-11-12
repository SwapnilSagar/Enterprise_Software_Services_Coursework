package uk.ac.newcastle.enterprisemiddleware.dto.customer;

import lombok.Data;
import uk.ac.newcastle.enterprisemiddleware.agent.BookingEntity;
import uk.ac.newcastle.enterprisemiddleware.agent.GlobalStatus;

import java.util.Date;

/**
 * @author Mayank Kunwar
 * */
@Data
public class CustomerBookMapDTO {
    private String id;
    private GlobalStatus status;
    private Date bookingDate;
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

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public BookingEntity getBookingEntity() {
        return bookingEntity;
    }

    public void setBookingEntity(BookingEntity bookingEntity) {
        this.bookingEntity = bookingEntity;
    }
}

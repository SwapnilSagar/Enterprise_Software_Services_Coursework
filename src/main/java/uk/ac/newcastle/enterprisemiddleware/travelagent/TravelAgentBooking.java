package uk.ac.newcastle.enterprisemiddleware.travelagent;

import uk.ac.newcastle.enterprisemiddleware.booking.Booking;
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@NamedQuery(name = "TravelAgentBooking.findByCustomerId", query = "SELECT b FROM TravelAgentBooking b WHERE b.customer.id = :customerId")
@Table(name = "travel_agent_booking")
public class TravelAgentBooking implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "ta_booking_gen")
    @TableGenerator(name = "ta_booking_gen", allocationSize = 1, initialValue = 10)
    private Long id;

    // The customer who made this aggregate booking
    @NotNull
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    // This is a full relationship
    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "hotel_booking_id")
    private Booking hotelBooking;

    // The booking ID from the *remote* taxi service
    @NotNull
    @Column(name = "taxi_booking_id")
    private Long taxiBookingId;

    // The booking ID from the *remote* flight service
    @NotNull
    @Column(name = "flight_booking_id")
    private Long flightBookingId;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Booking getHotelBooking() {
        return hotelBooking;
    }

    public void setHotelBooking(Booking hotelBooking) {
        this.hotelBooking = hotelBooking;
    }

    public Long getTaxiBookingId() {
        return taxiBookingId;
    }

    public void setTaxiBookingId(Long taxiBookingId) {
        this.taxiBookingId = taxiBookingId;
    }

    public Long getFlightBookingId() {
        return flightBookingId;
    }

    public void setFlightBookingId(Long flightBookingId) {
        this.flightBookingId = flightBookingId;
    }
}
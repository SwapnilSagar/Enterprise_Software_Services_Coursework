package uk.ac.newcastle.enterprisemiddleware.travelagent;

import uk.ac.newcastle.enterprisemiddleware.booking.Booking;

/**
 * A POJO to represent the JSON request for a new TravelAgent booking.
 * It contains the Customer ID and the three booking objects.
 */
public class TravelAgentRequest {

    private Long customerId;
    private Booking hotelBooking;
    private Booking taxiBooking;
    private Booking flightBooking;

    // Getters and Setters
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Booking getHotelBooking() {
        return hotelBooking;
    }

    public void setHotelBooking(Booking hotelBooking) {
        this.hotelBooking = hotelBooking;
    }

    public Booking getTaxiBooking() {
        return taxiBooking;
    }

    public void setTaxiBooking(Booking taxiBooking) {
        this.taxiBooking = taxiBooking;
    }

    public Booking getFlightBooking() {
        return flightBooking;
    }

    public void setFlightBooking(Booking flightBooking) {
        this.flightBooking = flightBooking;
    }
}
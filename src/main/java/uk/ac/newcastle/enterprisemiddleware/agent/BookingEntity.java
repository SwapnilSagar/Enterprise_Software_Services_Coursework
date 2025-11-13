package uk.ac.newcastle.enterprisemiddleware.agent;

import uk.ac.newcastle.enterprisemiddleware.hotel.Hotel;

/**
 * @author Swapnil Sagar
 * */
public class BookingEntity {
    private Flight flight;
    private Taxi taxi;
    private Hotel hotel;

    public Flight getFlight() {
        return this.flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public Hotel getHotel() {
        return this.hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public Taxi getTaxi() {
        return taxi;
    }

    public void setTaxi(Taxi taxi) {
        this.taxi = taxi;
    }

    @Override
    public String toString() {
        return "BookingEntity{" +
                "flight=" + flight +
                ", hotel=" + hotel +
                ", taxi=" + hotel +
                '}';
    }
}

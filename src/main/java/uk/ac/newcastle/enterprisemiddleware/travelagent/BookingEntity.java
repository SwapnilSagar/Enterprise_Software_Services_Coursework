package uk.ac.newcastle.enterprisemiddleware.travelagent;

import uk.ac.newcastle.enterprisemiddleware.travelagent.client.response.HotelBookingResponse;
import uk.ac.newcastle.enterprisemiddleware.travelagent.client.response.Taxi2BookingResponse;
import uk.ac.newcastle.enterprisemiddleware.travelagent.client.response.TaxiBookingResponse;

/**
 *
 *
 * @author Swapnil Sagar
 *
 *
 * */


public class BookingEntity {
    private Taxi2BookingResponse taxi2;
    private HotelBookingResponse hotel;

    private TaxiBookingResponse taxi;

    public Taxi2BookingResponse getTaxi2() {
        return taxi2;
    }



    public void setTaxi2(Taxi2BookingResponse taxi2) {
        this.taxi2 = taxi2;
    }

    public HotelBookingResponse getHotel() {
        return hotel;
    }

    public void setHotel(HotelBookingResponse hotel) {
        this.hotel = hotel;
    }



    public TaxiBookingResponse getTaxi() {
        return taxi;
    }

    public void setTaxi(TaxiBookingResponse taxi) {
        this.taxi = taxi;
    }



    @Override
    public String toString() {
        return "BookingEntity{" +
                "taxi2=" + taxi2 +
                ", hotel=" + hotel +
                ", taxi=" + taxi +
                '}';
    }
}

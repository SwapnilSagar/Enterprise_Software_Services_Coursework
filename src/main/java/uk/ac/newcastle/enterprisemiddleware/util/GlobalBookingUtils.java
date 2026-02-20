package uk.ac.newcastle.enterprisemiddleware.util;

import uk.ac.newcastle.enterprisemiddleware.travelagent.*;
import uk.ac.newcastle.enterprisemiddleware.travelagent.client.request.TaxiBookingRequest;
import uk.ac.newcastle.enterprisemiddleware.travelagent.client.response.TaxiBookingResponse;
import uk.ac.newcastle.enterprisemiddleware.travelagent.client.response.Taxi2BookingResponse;
import uk.ac.newcastle.enterprisemiddleware.travelagent.client.response.HotelBookingResponse;
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.hotelbooking.HotelBookingRequest;

import java.util.Date;
import java.util.UUID;

/**
 * @author Swapnil Sagar
 * */
public class GlobalBookingUtils {



    public static TaxiBookingRequest getTaxiBookingRequest(String globalBookingId,
                                                           GlobalBookingRequest globalBookingRequest) {
        TaxiBookingRequest req = new TaxiBookingRequest();
        req.setBookingDate(globalBookingRequest.getFutureDate());
        req.setTaxiId(globalBookingRequest.getTaxiID());
        req.setGlobalId(globalBookingId);
        return req;
    }



    public static TaxiBookingRequest getTaxi2BookingRequest(String globalBookingId,
                                                            GlobalBookingRequest globalBookingRequest) {
        TaxiBookingRequest req = new TaxiBookingRequest();
        req.setBookingDate(globalBookingRequest.getFutureDate());
        req.setTaxiId(globalBookingRequest.getTaxi2ID());
        req.setGlobalId(globalBookingId);
        return req;
    }



    public static HotelBookingRequest getHotelBookingRequest(String globalBookingId,
                                                             GlobalBookingRequest globalBookingRequest) {
        HotelBookingRequest req = new HotelBookingRequest();
        req.setBookingDate(globalBookingRequest.getFutureDate());
        req.setHotelId(globalBookingRequest.getHotelID());
        req.setGlobalBookingId(globalBookingId);
        return req;
    }



    public static void updateBookingDetails(GlobalBooking booking,
                                            String taxi2Response, String hotelResponse, String taxiResponse) {
        Taxi2BookingResponse taxi2BookingResponse = JsonUtils.fromJson(taxi2Response, Taxi2BookingResponse.class);
        HotelBookingResponse hotelBookingResponse = JsonUtils.fromJson(hotelResponse, HotelBookingResponse.class);
        TaxiBookingResponse taxiBookingResponse = JsonUtils.fromJson(taxiResponse, TaxiBookingResponse.class);
        BookingEntity entity = new BookingEntity();
        entity.setTaxi2(taxi2BookingResponse);
        entity.setHotel(hotelBookingResponse);
        entity.setTaxi(taxiBookingResponse);
//        booking.setBookingEntity(entity);
        booking.setBookingJson(JsonUtils.toJson(entity));
    }



    public static GlobalBooking getBookingDetails(String globalBookingId, Date date, Customer customer) {
        GlobalBooking booking = new GlobalBooking();
        booking.setId(globalBookingId);
        booking.setStatus(GlobalStatus.PENDING);
        booking.setBookingDate(date);
        booking.setCustomer(customer);
        return booking;
    }

    public static Customer getCustomer(GuestBookingRequest request) {
        Customer customer = new Customer();
        customer.setName(request.getCustomerName());
        customer.setEmail(request.getEmail());
        customer.setPhoneNumber(request.getPhoneNumber());
        return customer;
    }




    public static GlobalBookingRequest getBookingRequest(GuestBookingRequest request, Long customerId) {
        GlobalBookingRequest req = new GlobalBookingRequest();
        req.setFutureDate(request.getFutureDate());
        req.setTaxi2ID(request.getFlightID());
        req.setHotelID(request.getHotelID());
        req.setTaxiID(request.getTaxiID());
        req.setCustomerID(customerId);
        return req;
    }



    public static String getBookingId() {
        return UUID.randomUUID().toString()
                .concat("-")
                .concat(String.valueOf(System.currentTimeMillis()));
    }
}

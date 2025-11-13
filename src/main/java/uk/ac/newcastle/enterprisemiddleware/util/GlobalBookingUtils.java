package uk.ac.newcastle.enterprisemiddleware.util;

import uk.ac.newcastle.enterprisemiddleware.agent.GlobalBooking;
import uk.ac.newcastle.enterprisemiddleware.agent.GlobalBookingRequest;
import uk.ac.newcastle.enterprisemiddleware.agent.GlobalStatus;
import uk.ac.newcastle.enterprisemiddleware.agent.GuestBookingRequest;
import uk.ac.newcastle.enterprisemiddleware.agent.client.request.FlightBookingRequest;
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.hotelbooking.HotelBookingRequest;

import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.UUID;

/**
 * @author Swapnil Sagar
 * */
public class GlobalBookingUtils {

    public static HotelBookingRequest getTaxiBookingRequest(String globalBookingId,
                                                            GlobalBookingRequest globalBookingRequest) {
        HotelBookingRequest req = new HotelBookingRequest();
        req.setBookingDate(globalBookingRequest.getFutureDate());
        req.setHotelId(globalBookingRequest.getTaxiID());
        req.setGlobalBookingId(globalBookingId);
        return req;
    }

    public static FlightBookingRequest getFlightBookingRequest(String globalBookingId,
                                                               GlobalBookingRequest globalBookingRequest) {
        FlightBookingRequest req = new FlightBookingRequest();
        req.setBookingDate(globalBookingRequest.getFutureDate());
        req.setFlightId(globalBookingRequest.getFlightID());
        req.setGlobalBookingId(globalBookingId);
        return req;
    }

    public static uk.ac.newcastle.enterprisemiddleware.agent.client.request.HotelBookingRequest getHotelBookingRequest(String globalBookingId,
                                                                                                                       GlobalBookingRequest globalBookingRequest) {
        uk.ac.newcastle.enterprisemiddleware.agent.client.request.HotelBookingRequest req = new uk.ac.newcastle.enterprisemiddleware.agent.client.request.HotelBookingRequest();
        req.setBookingDate(globalBookingRequest.getFutureDate());
        req.setHotelId(globalBookingRequest.getHotelID());
        req.setGlobalBookingId(globalBookingId);
        return req;
    }

    // TODO
    public static GlobalBooking updateBookingDetails(GlobalBooking booking,
                  Response flightResponse, Response hotelResponse, Response taxiResponse) {
        return null;
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
        req.setFlightID(request.getFlightID());
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

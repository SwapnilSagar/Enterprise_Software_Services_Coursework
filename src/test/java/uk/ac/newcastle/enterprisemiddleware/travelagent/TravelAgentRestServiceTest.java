package uk.ac.newcastle.enterprisemiddleware.travelagent;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import uk.ac.newcastle.enterprisemiddleware.booking.Booking;
import uk.ac.newcastle.enterprisemiddleware.hotel.Hotel;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TravelAgentRestServiceTest {

    @Test
    @Order(1)
    public void testCreateTravelAgentBookingSuccess() {
        // This test assumes all three REST clients point to the same (your) service.
        // All bookings should be valid.

        // 1. Hotel Booking (valid)
        Hotel hotel = new Hotel();
        hotel.setId(1L); // From import.sql
        Booking hotelBooking = new Booking();
        hotelBooking.setHotel(hotel);
        hotelBooking.setDate(LocalDate.now().plusDays(50));

        // 2. Taxi Booking (valid, uses Hotel 2 as a proxy)
        Hotel taxiAsHotel = new Hotel();
        taxiAsHotel.setId(2L); // From import.sql
        Booking taxiBooking = new Booking();
        taxiBooking.setHotel(taxiAsHotel); // Simulating a "taxi" booking
        taxiBooking.setDate(LocalDate.now().plusDays(50));

        // 3. Flight Booking (valid, uses Hotel 2 as a proxy on a different date)
        Booking flightBooking = new Booking();
        flightBooking.setHotel(taxiAsHotel); // Simulating a "flight" booking
        flightBooking.setDate(LocalDate.now().plusDays(51));

        // 4. Create the request payload
        TravelAgentRequest request = new TravelAgentRequest();
        request.setCustomerId(1L); // From import.sql
        request.setHotelBooking(hotelBooking);
        request.setTaxiBooking(taxiBooking);
        request.setFlightBooking(flightBooking);

        // 5. Make the request
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/agent/bookings")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("customer.id", equalTo(1))
                .body("hotelBooking.id", notNullValue())
                .body("taxiBookingId", notNullValue())
                .body("flightBookingId", notNullValue());
    }

    @Test
    @Order(2)
    public void testCreateTravelAgentBookingRollback() {
        // This test simulates a failure.
        // We make the *last* booking (flight) invalid by making it a duplicate.

        // 1. Hotel Booking (valid)
        Hotel hotel = new Hotel();
        hotel.setId(1L); // From import.sql
        Booking hotelBooking = new Booking();
        hotelBooking.setHotel(hotel);
        hotelBooking.setDate(LocalDate.now().plusDays(60)); // Unique date

        // 2. Taxi Booking (valid)
        Hotel taxiAsHotel = new Hotel();
        taxiAsHotel.setId(2L); // From import.sql
        Booking taxiBooking = new Booking();
        taxiAsHotel.setId(2L);
        taxiBooking.setHotel(taxiAsHotel);
        taxiBooking.setDate(LocalDate.now().plusDays(60)); // Unique date

        // 3. Flight Booking (INVALID - duplicate of import.sql booking)
        Booking flightBooking = new Booking();
        flightBooking.setHotel(hotel); // Hotel 1
        flightBooking.setDate(LocalDate.of(2099, 10, 25)); // Duplicate date

        // 4. Create the request payload
        TravelAgentRequest request = new TravelAgentRequest();
        request.setCustomerId(1L);
        request.setHotelBooking(hotelBooking);
        request.setTaxiBooking(taxiBooking);
        request.setFlightBooking(flightBooking);

        // 5. Make the request
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/agent/bookings")
                .then()
                .statusCode(500)
                .body("error", equalTo("Booking failed"))
                .body("reasons.error", equalTo("Booking failed, all partial bookings have been cancelled."));
    }
}
package uk.ac.newcastle.enterprisemiddleware.hotelbooking;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import uk.ac.newcastle.enterprisemiddleware.hotel.Hotel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * FIX #10 — Integration tests for HotelBookingRestService.
 * Previously, only CustomerRestService and HotelRestServiceTest had tests. The hotel booking module
 * (core business logic) was completely untested, making regressions undetectable without manual API calls.
 *
 * @author Swapnil Sagar
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HotelBookingRestServiceTest {

    private static Long createdHotelId;
    private static Long createdBookingId;

    // A future date used across tests (+10 days)
    private static final String FUTURE_DATE = LocalDate.now().plusDays(10)
            .format(DateTimeFormatter.ISO_LOCAL_DATE) + "T12:00:00.000+0000";

    // A different time on the same day to verify day-level conflict detection (FIX #9)
    private static final String SAME_DAY_DIFFERENT_TIME = LocalDate.now().plusDays(10)
            .format(DateTimeFormatter.ISO_LOCAL_DATE) + "T18:00:00.000+0000";

    @Test
    @Order(1)
    public void testCreateHotelForBooking() {
        Hotel hotel = new Hotel();
        hotel.setName("Hilton");
        hotel.setPhoneNumber("01199123456");
        hotel.setPostcode("NE18AB");

        String responseBody = given()
                .contentType(ContentType.JSON)
                .body(hotel)
                .when().post("/hotel")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .extract().body().asString();

        createdHotelId = Long.parseLong(
                io.restassured.path.json.JsonPath.from(responseBody).getString("id"));
    }

    @Test
    @Order(2)
    public void testCreateBookingSuccess() {
        // HotelBookingRequest fields: globalBookingId, hotelId, bookingDate
        String body = String.format(
                "{\"globalBookingId\":\"test-global-001\",\"hotelId\":%d,\"bookingDate\":\"%s\"}",
                createdHotelId, FUTURE_DATE);

        String responseBody = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when().post("/hotel-booking/")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .extract().body().asString();

        createdBookingId = Long.parseLong(
                io.restassured.path.json.JsonPath.from(responseBody).getString("id"));
    }

    @Test
    @Order(3)
    public void testCreateDuplicateBookingConflict() {
        // FIX #9 — Verifies that two bookings on different times but the same calendar day
        // for the same hotel are correctly rejected. Before the date-range fix, this test
        // would have PASSED both bookings through (different millisecond timestamps).
        String body = String.format(
                "{\"globalBookingId\":\"test-global-002\",\"hotelId\":%d,\"bookingDate\":\"%s\"}",
                createdHotelId, SAME_DAY_DIFFERENT_TIME);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when().post("/hotel-booking/")
                .then()
                .statusCode(409);
    }

    @Test
    @Order(4)
    public void testCreateBookingWithPastDateFails() {
        // FIX #6 — @Future validation rejects past dates at the bean validation level.
        String body = String.format(
                "{\"globalBookingId\":\"test-global-003\",\"hotelId\":%d,\"bookingDate\":\"2020-01-01T12:00:00.000+0000\"}",
                createdHotelId);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when().post("/hotel-booking/")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(5)
    public void testDeleteBookingByLocalIdSuccess() {
        given()
                .when().delete("/hotel-booking/" + createdBookingId)
                .then()
                .statusCode(204);
    }

    @Test
    @Order(6)
    public void testDeleteNonExistentBookingReturns404() {
        given()
                .when().delete("/hotel-booking/999999")
                .then()
                .statusCode(404);
    }
}

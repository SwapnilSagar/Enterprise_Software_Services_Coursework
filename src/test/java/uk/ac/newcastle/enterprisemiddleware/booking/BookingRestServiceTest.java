package uk.ac.newcastle.enterprisemiddleware.booking;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

// Part 2
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.hotel.Hotel;
//

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookingRestServiceTest {

    private static Long newBookingId; // To store the ID of the created booking for deletion

    @Test
    @Order(1)
    public void testGetBookingsByCustomerId() {
        given()
                .queryParam("customerId", 1L) // Customer 1 from import.sql
                .when().get("/bookings")
                .then()
                .statusCode(200)
                .body("$.size()", greaterThanOrEqualTo(1))
                .body("[0].hotel.id", equalTo(1));
    }

    @Test
    @Order(2)
    public void testGetBookingsNoCustomerId() {
        given()
                .when().get("/bookings")
                .then()
                .statusCode(400) // Expect Bad Request
                .body("error", equalTo("customerId query parameter is required"));
    }
    //Part 2
    @Test
    @Order(3)
    public void testCreateBookingSuccess() {
        // --- UPDATE BOOKING OBJECT ---
        Customer customer = new Customer();
        customer.setId(1L); // From import.sql

        Hotel hotel = new Hotel();
        hotel.setId(2L); // From import.sql

        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setHotel(hotel);
        booking.setDate(LocalDate.now().plusDays(5)); // Future date
        // ---------------------------

        // Extract the ID from the response for the delete test
        newBookingId = given()
                .contentType(ContentType.JSON)
                .body(booking)
                .when().post("/bookings")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("hotel.id", equalTo(2))
                .extract().body().as(Booking.class).getId();
    }

    @Test
    @Order(4)
    public void testCreateBookingValidationFailurePastDate() {
        // --- UPDATE BOOKING OBJECT ---
        Customer customer = new Customer();
        customer.setId(1L);
        Hotel hotel = new Hotel();
        hotel.setId(1L);

        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setHotel(hotel);
        booking.setDate(LocalDate.now().minusDays(1)); // Date in the past

        given()
                .contentType(ContentType.JSON)
                .body(booking)
                .when().post("/bookings")
                .then()
                .statusCode(400) // Expect Bad Request
                .body("reasons.date", equalTo("Booking date must be in the future"));
    }

    @Test
    @Order(5)
    public void testCreateBookingUniquenessFailure() {
        // --- UPDATE BOOKING OBJECT ---
        Customer customer = new Customer();
        customer.setId(1L);
        Hotel hotel = new Hotel();
        hotel.setId(1L);

        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setHotel(hotel);
        booking.setDate(LocalDate.of(2099, 10, 25)); // From import.sql

        given()
                .contentType(ContentType.JSON)
                .body(booking)
                .when().post("/bookings")
                .then()
                .statusCode(409) // Expect Conflict
                .body("reasons.booking", equalTo("A booking for this hotel and date already exists"));
    }

    @Test
    @Order(6)
    public void testCreateBookingInvalidCustomer() {
        // --- UPDATE BOOKING OBJECT ---
        Customer customer = new Customer();
        customer.setId(9999L); // Non-existent customer
        Hotel hotel = new Hotel();
        hotel.setId(1L);

        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setHotel(hotel);
        booking.setDate(LocalDate.now().plusDays(10));
        // ---------------------------

        given()
                .contentType(ContentType.JSON)
                .body(booking)
                .when().post("/bookings")
                .then()
                .statusCode(400)
                .body("reasons.validation", startsWith("Invalid Customer ID"));
    }
    //End of part 2
    @Test
    @Order(7)
    public void testDeleteBookingSuccess() {
        // We use the ID saved in testCreateBookingSuccess
        given()
                .when().delete("/bookings/" + newBookingId)
                .then()
                .statusCode(204); // Expect No Content
    }

    @Test
    @Order(8)
    public void testDeleteBookingNotFound() {
        given()
                .when().delete("/bookings/99999")
                .then()
                .statusCode(404) // Expect Not Found
                .body("error", equalTo("No booking found with ID 99999"));
    }
    //Part 2
    @Test
    @Order(9)
    public void testCascadeDeleteOnCustomer() {
        // 1. Create a new customer
        Customer customer = new Customer();
        customer.setName("Temp");
        customer.setEmail("temp@example.com");
        customer.setPhoneNumber("01111111111");

        Customer createdCustomer = given()
                .contentType(ContentType.JSON)
                .body(customer)
                .when().post("/customers")
                .then().statusCode(201)
                .extract().body().as(Customer.class);

        // 2. Create a new booking for this customer
        Booking booking = new Booking();
        booking.setCustomer(createdCustomer);
        Hotel hotel = new Hotel();
        hotel.setId(1L); // From import.sql
        booking.setHotel(hotel);
        booking.setDate(LocalDate.now().plusDays(20));

        Booking createdBooking = given()
                .contentType(ContentType.JSON)
                .body(booking)
                .when().post("/bookings")
                .then().statusCode(201)
                .extract().body().as(Booking.class);

        // 3. Delete the customer
        given()
                .when().delete("/customers/" + createdCustomer.getId())
                .then().statusCode(204);

        // 4. Verify the booking is also gone
        given()
                .when().delete("/bookings/" + createdBooking.getId())
                .then().statusCode(404); // Not Found, proves it was deleted
    }
    //End of part 2
}
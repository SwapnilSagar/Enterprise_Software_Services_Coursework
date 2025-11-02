//Part 2

package uk.ac.newcastle.enterprisemiddleware.booking;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.hotel.Hotel;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GuestBookingRestServiceTest {

    private static final String GUEST_EMAIL = "guest@example.com";

    @Test
    @Order(1)
    public void testCreateGuestBookingSuccess() {
        // 1. Create a new guest customer
        Customer customer = new Customer();
        customer.setName("Guest");
        customer.setEmail(GUEST_EMAIL);
        customer.setPhoneNumber("02223334455");

        // 2. Create a new booking
        Hotel hotel = new Hotel();
        hotel.setId(1L); // From import.sql

        Booking booking = new Booking();
        booking.setHotel(hotel);
        booking.setDate(LocalDate.now().plusDays(30));

        // 3. Create the GuestBooking payload
        GuestBooking guestBooking = new GuestBooking();
        guestBooking.setCustomer(customer);
        guestBooking.setBooking(booking);

        given()
                .contentType(ContentType.JSON)
                .body(guestBooking)
                .when().post("/guestbookings")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("customer.email", equalTo(GUEST_EMAIL));
    }

    @Test
    @Order(2)
    public void testTransactionRollbackOnFailure() {
        // 1. Create a new guest customer
        Customer customer = new Customer();
        customer.setName("Rollback");
        customer.setEmail("rollback@example.com");
        customer.setPhoneNumber("03334445566");

        // 2. Create an *invalid* booking (date in the past)
        Hotel hotel = new Hotel();
        hotel.setId(1L); // From import.sql

        Booking booking = new Booking();
        booking.setHotel(hotel);
        booking.setDate(LocalDate.now().minusDays(1)); // Invalid date

        // 3. Create the GuestBooking payload
        GuestBooking guestBooking = new GuestBooking();
        guestBooking.setCustomer(customer);
        guestBooking.setBooking(booking);

        given()
                .contentType(ContentType.JSON)
                .body(guestBooking)
                .when().post("/guestbookings")
                .then()
                .statusCode(400) // Expect a failure
                .body("reasons.date", equalTo("Booking date must be in the future"));

        // 4. THE IMPORTANT PART: Check that the customer was NOT created
        Response response = given()
                .when().get("/customers")
                .then()
                .statusCode(200)
                .extract().response();

        // Check that the list of customers does not contain the rollback email
        String emails = response.jsonPath().getString("email");
        assertEquals(false, emails.contains("rollback@example.com"), "Customer was created, transaction did not roll back!");
    }
}
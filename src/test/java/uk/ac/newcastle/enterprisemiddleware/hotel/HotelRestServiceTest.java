package uk.ac.newcastle.enterprisemiddleware.hotel;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HotelRestServiceTest {

    @Test
    @Order(1)
    public void testGetAllHotels() {
        given()
                .when().get("/hotels")
                .then()
                .statusCode(200)
                .body("$.size()", greaterThanOrEqualTo(2))
                .body("find { it.name == 'Grand' }.postcode", equalTo("NE17RU"));
    }

    @Test
    @Order(2)
    public void testCreateHotelSuccess() {
        Hotel hotel = new Hotel();
        hotel.setName("City");
        hotel.setPhoneNumber("01112223344");
        hotel.setPostcode("NE12AB");

        given()
                .contentType(ContentType.JSON)
                .body(hotel)
                .when().post("/hotels")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("City"));
    }

    @Test
    @Order(3)
    public void testCreateHotelValidationFailure() {
        Hotel hotel = new Hotel();
        hotel.setName("Bad Lodge");
        hotel.setPhoneNumber("12345"); // Invalid phone
        hotel.setPostcode("123456789"); // Invalid postcode

        given()
                .contentType(ContentType.JSON)
                .body(hotel)
                .when().post("/hotels")
                .then()
                .statusCode(400) // Expect Bad Request
                .body("reasons.phoneNumber", equalTo("Phone number must start with 0 and be 11 digits long"))
                .body("reasons.postcode", equalTo("size must be between 6 and 6"));
    }

    @Test
    @Order(4)
    public void testCreateHotelUniquenessFailure() {
        // Try to create a hotel with a phonenumber that already exists (from import.sql)
        Hotel hotel = new Hotel();
        hotel.setName("Duplicate");
        hotel.setPhoneNumber("01912087000"); // From import.sql
        hotel.setPostcode("NE19CD");

        given()
                .contentType(ContentType.JSON)
                .body(hotel)
                .when().post("/hotels")
                .then()
                .statusCode(409) // Expect Conflict
                .body("reasons.phoneNumber", equalTo("A hotel with this phone number already exists"));
    }
}
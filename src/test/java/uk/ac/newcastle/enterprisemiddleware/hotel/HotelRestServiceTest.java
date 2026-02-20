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

    private static String createdHotelId;
    private static String createdHotelPhone = "01234567891";

    @Test
    @Order(1)
    public void testCreateHotelSuccess() {
        // As requested, we send the Hotel ENTITY directly
        Hotel hotel = new Hotel();
        hotel.setName("Grand"); // Valid: alphabetical only
        hotel.setPhoneNumber(createdHotelPhone);
        hotel.setPostcode("NE17RU");

        String responseBody = given()
                .contentType(ContentType.JSON)
                .body(hotel)
                .when().post("/hotel") // Path is /hotel
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("Grand"))
                .body("phoneNumber", equalTo(createdHotelPhone))
                .extract().body().asString();

        createdHotelId = io.restassured.path.json.JsonPath.from(responseBody).getString("id");
    }



    @Test
    @Order(2)
    public void testRetrieveHotelByIdSuccess() {
        given()
                .when().get("/hotel/" + createdHotelId)
                .then()
                .statusCode(200)
                .body("id", equalTo(Integer.parseInt(createdHotelId)))
                .body("name", equalTo("Grand"));
    }

}
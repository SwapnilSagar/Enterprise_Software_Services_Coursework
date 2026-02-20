package uk.ac.newcastle.enterprisemiddleware.customer;

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
public class CustomerRestServiceTest {

    private static String createdCustomerId;
    private static String createdCustomerEmail = "john@example.com";

    @Test
    @Order(1)
    public void testCreateCustomerSuccess() {
        // As requested, we send the Customer ENTITY directly
        Customer customer = new Customer();
        customer.setName("John"); // Valid: alphabetical only
        customer.setEmail(createdCustomerEmail);
        customer.setPhoneNumber("01234567890");

        String responseBody = given()
                .contentType(ContentType.JSON)
                .body(customer)
                .when().post("/customers")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("John"))
                .body("email", equalTo(createdCustomerEmail))
                .extract().body().asString();

        // Save the ID for later tests
        createdCustomerId = io.restassured.path.json.JsonPath.from(responseBody).getString("id");
    }



    @Test
    @Order(2)
    public void testCreateCustomerUniquenessFailure() {
        // Try to create the same customer again
        Customer customer = new Customer();
        customer.setName("John");
        customer.setEmail(createdCustomerEmail); // Duplicate email
        customer.setPhoneNumber("09876543210"); // Different phone

        given()
                .contentType(ContentType.JSON)
                .body(customer)
                .when().post("/customers")
                .then()
                .statusCode(409)
                .body("reasons.email", equalTo("A customer with this email already exists"));
    }

    @Test
    @Order(3)
    public void testRetrieveCustomerByIdSuccess() {
        given()
                .when().get("/customers/" + createdCustomerId)
                .then()
                .statusCode(200)
                .body("id", equalTo(Integer.parseInt(createdCustomerId))) // DTO has Long, but JSON path parses as Int
                .body("name", equalTo("John"));
    }

    @Test
    @Order(4)
    public void testRetrieveCustomerByIdNotFound() {
        given()
                .when().get("/customers/99999")
                .then()
                .statusCode(404)
                .body("error", equalTo("No Customer with ID 99999 was found!"));
    }

    @Test
    @Order(5)
    public void testRetrieveAllCustomersBookingInfo() {
        given()
                .when().get("/customers/bookings")
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(1))) // Check that the list is not empty
                .body("find { it.id == " + createdCustomerId + " }.name", equalTo("John"));
    }
}
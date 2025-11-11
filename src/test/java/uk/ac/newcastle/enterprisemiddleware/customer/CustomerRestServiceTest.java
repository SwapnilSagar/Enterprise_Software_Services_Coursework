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

    @Test
    @Order(1)
    public void testGetAllCustomers() {
        given()
                .when().get("/customers")
                .then()
                .statusCode(200)
                // Check that our import.sql customer is there
                .body("$.size()", greaterThanOrEqualTo(1))
                .body("find { it.email == 'swap@gmail.com' }.name", equalTo("Swapnil Sagar"));
    }

    @Test
    @Order(2)
    public void testCreateCustomerSuccess() {
        Customer customer = new Customer();
        customer.setName("Jane");
        customer.setEmail("jane.doe@example.com");
        customer.setPhoneNumber("01234567890");

        given()
                .contentType(ContentType.JSON)
                .body(customer)
                .when().post("/customers")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("Jane"))
                .body("email", equalTo("jane.doe@example.com"))
                .body("phoneNumber", equalTo("01234567890"));
    }

    @Test
    @Order(3)
    public void testCreateCustomerValidationFailure() {
        Customer customer = new Customer();
        customer.setName("Invalid User"); // Contains a space, fails validation
        customer.setEmail("bad-email"); // Invalid email
        customer.setPhoneNumber("12345"); // Invalid phone

        given()
                .contentType(ContentType.JSON)
                .body(customer)
                .when().post("/customers")
                .then()
                .statusCode(400) // Expect Bad Request
                // Check the new JSON error body
                .body("reasons.name", equalTo("Name must be alphabetical (letters only, no spaces)"))
                .body("reasons.email", equalTo("Please use a valid email address"))

                // --- THIS IS THE FIX ---
                // Change the expected message to match the @Size violation
                .body("reasons.phoneNumber", equalTo("Phone number must start with 0 and be 11 digits long"));
    }

    @Test
    @Order(4)
    public void testCreateCustomerUniquenessFailure() {
        // Try to create a customer with an email that already exists (from import.sql)
        Customer customer = new Customer();
        customer.setName("AnotherJohn");
        customer.setEmail("swap@gmail.com");
        customer.setPhoneNumber("09876543210");

        given()
                .contentType(ContentType.JSON)
                .body(customer)
                .when().post("/customers")
                .then()
                .statusCode(409) // Expect Conflict
                // Check the new JSON error body
                .body("reasons.email", equalTo("A customer with this email already exists"));
    }
}
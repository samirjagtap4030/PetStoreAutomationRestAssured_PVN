package api.test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class NegativeUserTests {

	String baseUrl = "https://petstore.swagger.io/v2/user";

	// Case 1: Non-existing user GET -> 404 + error body
	@Test(priority = 1)
	public void testGetNonExistingUser() {

		given()
			.pathParam("username", "user_does_not_exist_9999")
		.when()
			.get(baseUrl + "/{username}")
		.then()
			.log().all()
			.statusCode(404)
			.body("code", equalTo(1))
			.body("type", equalTo("error"))
			.body("message", equalTo("User not found"));
	}

	// Case 2: Non-existing user DELETE -> 404
	@Test(priority = 2)
	public void testDeleteNonExistingUser() {

		Response response = given()
			.pathParam("username", "user_does_not_exist_9999")
		.when()
			.delete(baseUrl + "/{username}");

		Assert.assertEquals(response.getStatusCode(), 404);
	}

	// Case 3: Invalid JSON body POST -> document API behaviour
	@Test(priority = 3)
	public void testCreateUserWithInvalidBody() {

		String invalidBody = "{ \"id\": \"this_should_be_a_number\" }";

		Response response = given()
			.contentType(ContentType.JSON)
			.body(invalidBody)
		.when()
			.post(baseUrl);

		response.then().log().all();

		// Petstore is lenient -> may return 200 or 500, accept either
		Assert.assertTrue(
			response.getStatusCode() == 200 || response.getStatusCode() == 500,
			"Unexpected status: " + response.getStatusCode()
		);
	}
}

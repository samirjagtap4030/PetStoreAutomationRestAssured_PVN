package api.test;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.javafaker.Faker;

import api.endpoints.UserEndPoints;
import api.payload.User;
import io.restassured.response.Response;
import io.restassured.module.jsv.JsonSchemaValidator;

public class UserTests {

	Faker faker;
	User userPayload;
	
	public Logger logger; // for logs
	
	@BeforeClass
	public void setup()
	{
		faker=new Faker();
		userPayload=new User();
		
		userPayload.setId(faker.idNumber().hashCode());
		userPayload.setUsername(faker.name().username());
		userPayload.setFirstName(faker.name().firstName());
		userPayload.setLastName(faker.name().lastName());
		userPayload.setEmail(faker.internet().safeEmailAddress());
		userPayload.setPassword(faker.internet().password(5, 10));
		userPayload.setPhone(faker.phoneNumber().cellPhone());
		
		//logs
		logger= LogManager.getLogger(this.getClass());
		
		logger.debug("debugging.....");//  प्रिंट karate "debugging.....",no use
		
	}
	
	@Test(priority=1)
	public void testPostUser()
	{
		logger.info("********** Creating user  ***************");
		Response response=UserEndPoints.createUser(userPayload);
		response.then().log().all();
		
		Assert.assertEquals(response.getStatusCode(),200);
		// 2. Status line validation
	    Assert.assertEquals(response.getStatusLine(), "HTTP/1.1 200 OK");

	    // 3. Response time validation 
	    Assert.assertTrue(response.getTime() < 5000, "Response took too long");

	    // 4. Header validations
	    Assert.assertEquals(response.getContentType(), "application/json");
	    Assert.assertTrue(response.getHeader("Server").contains("Jetty"));
	    Assert.assertTrue(response.getHeader("Access-Control-Allow-Methods").contains("POST"));

	    // 5. Body field validations
	    Assert.assertEquals(response.jsonPath().getInt("code"), 200);
	    Assert.assertEquals(response.jsonPath().getString("type"), "unknown");
	    Assert.assertNotNull(response.jsonPath().getString("message"), "message should not be null");
	    Assert.assertFalse(response.jsonPath().getString("message").isEmpty(), "message should not be empty");
		
	    // 6. JSON Schema validation
	    response.then().assertThat()
	            .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/createUserSchema.json"));
	    
		logger.info("**********User is creatged  ***************");
			
	}
	
	@Test(priority=2)
	public void testGetUserByName()
	{
		logger.info("********** Reading User Info ***************");
		
		Response response=UserEndPoints.readUser(this.userPayload.getUsername());
		response.then().log().all();
		Assert.assertEquals(response.getStatusCode(),200);
		
		logger.info("**********User info  is displayed ***************");
		
	}
	
	@Test(priority=3)
	public void testUpdateUserByName()
	{
		logger.info("********** Updating User ***************");
		
		//update data using payload
		userPayload.setFirstName(faker.name().firstName());
		userPayload.setLastName(faker.name().lastName());
		userPayload.setEmail(faker.internet().safeEmailAddress());
		
		Response response=UserEndPoints.updateUser(this.userPayload.getUsername(),userPayload);
		response.then().log().body();
				
		Assert.assertEquals(response.getStatusCode(),200);
		
		logger.info("********** User updated ***************");
		//Checking data after update
		Response responseAfterupdate=UserEndPoints.readUser(this.userPayload.getUsername());
		Assert.assertEquals(responseAfterupdate.getStatusCode(),200);
			
	}
	
	@Test(priority=4)
	public void testDeleteUserByName()
	{
		logger.info("**********   Deleting User  ***************");
		
		Response response=UserEndPoints.deleteUser(this.userPayload.getUsername());
		Assert.assertEquals(response.getStatusCode(),200);
		
		logger.info("********** User deleted ***************");
	}
	
	
}

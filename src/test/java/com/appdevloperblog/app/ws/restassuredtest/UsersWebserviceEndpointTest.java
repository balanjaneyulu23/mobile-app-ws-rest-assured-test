package com.appdevloperblog.app.ws.restassuredtest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.restassured.RestAssured;
import io.restassured.response.Response;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UsersWebserviceEndpointTest {

	private final String CONTEXT_PATH = "/mobile-app-ws";

	private final String EMAIL_ADDRESS="balanjaneyulu25@gmail.com";
	private final String JSON = "application/json";

	private static String authorizationHeader;
	private static String userId;

	@BeforeEach
	void setUp() throws Exception {
		RestAssured.baseURI="http://localhost";
		RestAssured.port=8080;
	}

	@Test
	@Order(1)
	void testUserLogin() {
		Map<String, String> loginDetails = new HashMap<>();
		loginDetails.put("email", EMAIL_ADDRESS);
		loginDetails.put("password", "123");

		Response response =given()
				.contentType(JSON)
				.accept(JSON)
				.body(loginDetails)
				.when()
				.post(CONTEXT_PATH+"/users/login")
				.then()
				.statusCode(200)
				.extract().response();

		authorizationHeader = response.header("Authorization");
		userId = response.header("userId");

		assertNotNull(authorizationHeader);
		assertNotNull(userId);

	}

	@Test
	@Order(2)
	void testGetUserDetails() {
		Response response =given()
				.pathParam("id", userId)
				.header("Authorization", authorizationHeader)
				.accept(JSON)
				.when()
				.get(CONTEXT_PATH+"/users/{id}")
				.then()
				.statusCode(200)
				.contentType(JSON)
				.extract().response();

		String userPublicId = response.jsonPath().getString("userId");
		String userEmail = response.jsonPath().getString("email");
		String firstName = response.jsonPath().getString("firstName");
		String lastName = response.jsonPath().getString("lastName");

		List<Map<String, String>> addresses = response.jsonPath().getList("addresses");
		String addressId = addresses.get(0).get("addressId");

		assertNotNull(userPublicId);
		assertNotNull(userEmail);
		assertNotNull(firstName);
		assertNotNull(lastName);

		assertEquals(EMAIL_ADDRESS, userEmail);

		assertTrue(addresses.size() == 2);
		assertTrue(addressId.length() == 30);
	}

	@Test
	@Order(3)
	void testUpdateUserDetails() {
		Map<String, Object> userDetails = new HashMap<>();
		userDetails.put("firstName", "LeelaBala");
		userDetails.put("lastName", "pan");

		Response response =given()
				.contentType(JSON)
				.accept(JSON)
				.header("Authorization", authorizationHeader)
				.pathParam("id", userId)
				.body(userDetails)
				.when()
				.put(CONTEXT_PATH+"/users/{id}")
				.then()
				.statusCode(200)
				.contentType(JSON)
				.extract().response();


		String firstName = response.jsonPath().getString("firstName");
		String lastName = response.jsonPath().getString("lastName");


		assertNotNull(firstName);
		assertNotNull(lastName);

		assertEquals("LeelaBala",firstName);
		assertEquals("pan",lastName);

	}

	@Test
	@Order(4)
	void testDeleteUserDetails() {

		Response response =given()
				.accept(JSON)
				.header("Authorization", authorizationHeader)
				.pathParam("id", userId)
				.when()
				.delete(CONTEXT_PATH+"/users/{id}")
				.then()
				.statusCode(200)
				.extract().response();

		String operationResult = response.jsonPath().getString("operationResult");
		assertEquals("SUCCESS",operationResult);

	}

}

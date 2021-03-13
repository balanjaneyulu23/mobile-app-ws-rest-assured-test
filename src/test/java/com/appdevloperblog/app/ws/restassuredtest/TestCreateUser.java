package com.appdevloperblog.app.ws.restassuredtest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.restassured.RestAssured;
import io.restassured.response.Response;

class TestCreateUser {

	private final String CONTEXT_PATH = "/mobile-app-ws";

	@BeforeEach
	void setUp() throws Exception {
		RestAssured.baseURI="http://localhost";
		RestAssured.port=8080;
	}

	@Test
	void testCreateUser() {
		List<Map<String, Object>> userAddresses = new ArrayList<>();

		Map<String, Object> shippingAddress = new HashMap<>();
		shippingAddress.put("city", "Guntur");
		shippingAddress.put("country", "India");
		shippingAddress.put("streetName", "east bazar");
		shippingAddress.put("postalCode", "522034");
		shippingAddress.put("type", "shipping");
		
		Map<String, Object> billingAddress = new HashMap<>();
		billingAddress.put("city", "Guntur");
		billingAddress.put("country", "India");
		billingAddress.put("streetName", "east bazar");
		billingAddress.put("postalCode", "522034");
		billingAddress.put("type", "billing");

		userAddresses.add(billingAddress);
		userAddresses.add(shippingAddress);

		Map<String, Object> userDetails = new HashMap<>();
		userDetails.put("firstName", "LeelaBala9");
		userDetails.put("lastName", "pandrangi");
		userDetails.put("email", "balanjaneyulu25@gmail.com");
		userDetails.put("password", "123");
		userDetails.put("addresses", userAddresses);

		Response response = given().
				contentType("application/json")
				.accept("application/json")
				.body(userDetails)
				.when()
				.post(CONTEXT_PATH+"/users")
				.then()
				.statusCode(200)
				.contentType("application/json")
				.extract()
				.response();

		String userId = response.jsonPath().getString("userId");
		assertNotNull(userId);

		String bodyString = response.body().asString();

		try {
			JSONObject responseBodyJson = new JSONObject(bodyString);
			JSONArray addresses =responseBodyJson.getJSONArray("addresses");

			assertNotNull(addresses);
			assertTrue(addresses.length() == 2);
			String addressId = addresses.getJSONObject(0).getString("addressId");
			assertNotNull(addressId);
			assertTrue(addressId.length() == 30);
		} catch (JSONException e) {
			fail(e.getMessage());
		}

	}

}

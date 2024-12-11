package com.azvd.microservices.inventory;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MySQLContainer;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventoryServiceApplicationTests {

	@ServiceConnection
	static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.3.0");

	@LocalServerPort
	private Integer port;

	@BeforeEach
	void setUp() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	static {
		mySQLContainer.start();
	}

	@Test
	void shouldReturnTrueWhenInventoryHasItem() {
		RestAssured.given()
				.queryParam("skuCode", "iphone-15")
				.queryParam("quantity", 1)
				.when()
				.get("/api/inventory")
				.then()
				.statusCode(200)
				.body(org.hamcrest.Matchers.equalTo("true"));
	}

	@Test
	void shouldReturnFalseWhenInventoryDoesNotHaveItem() {
		RestAssured.given()
				.queryParam("skuCode", "iphone-15")
				.queryParam("quantity", 101)
				.when()
				.get("/api/inventory")
				.then()
				.statusCode(200)
				.body(org.hamcrest.Matchers.equalTo("false"));
	}
}

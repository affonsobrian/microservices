package com.azvd.microservices.product;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MongoDBContainer;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceApplicationTests {

	@ServiceConnection
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.5");

	@LocalServerPort
	private Integer port;

	@BeforeEach
	void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	static {
		mongoDBContainer.start();
	}

	@Test
	void shouldCreateProduct() {
		String requestBody = """
				{
					"name": "iPhone 15",
					"description": "A Phone from Apple",
					"price": 1000
				}
			""";
		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.post("/api/products")
				.then()
				.statusCode(201)
				.body("id", Matchers.notNullValue())
				.body("name", Matchers.equalTo("iPhone 15"))
				.body("description", Matchers.equalTo("A Phone from Apple"))
				.body("price", Matchers.equalTo(1000));
	}

	void shouldGetAllProducts() {
		RestAssured.get("/api/products")
				.then()
				.statusCode(200)
				.body("size()", Matchers.equalTo(1))
				.body("[0].id", Matchers.notNullValue())
				.body("[0].name", Matchers.equalTo("iPhone 15"))
				.body("[0].description", Matchers.equalTo("A Phone from Apple"))
				.body("[0].price", Matchers.equalTo(1000));
	}
}

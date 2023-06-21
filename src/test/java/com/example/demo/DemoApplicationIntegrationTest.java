package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.demo.dto.UserDto;
import com.example.demo.model.Customer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Disabled("it")
public class DemoApplicationIntegrationTest {

  @Autowired
  private WebTestClient webTestClient;

  @Test
  public void testCustomers() {
    webTestClient.get().uri("/customers")
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
        .expectBodyList(Customer.class)
        .consumeWith(req -> {
          assertNotNull(req.getResponseBody());
          assertEquals(req.getResponseBody().size(), 2);
          assertEquals(req.getResponseHeaders().getFirst("web-filter"),
              "web-filter-test");
        });
  }

  @Test
  public void testCustomer() {
    webTestClient.get().uri("/customer/1")
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
        .expectBodyList(Customer.class)
        .consumeWith(req -> {
          assertNotNull(req.getResponseBody());
          assertEquals(req.getResponseBody().size(), 1);
        });
  }

  @Test
  public void testCustomerNotFound() {
    webTestClient.get().uri("/customer/-1")
        .exchange()
        .expectStatus().isNotFound()
        .expectBodyList(Customer.class)
        .hasSize(0);
  }

  @Test
  public void testUsers() {
    webTestClient.get().uri("/users")
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
        .expectBodyList(UserDto.class)
        .hasSize(10);
  }
}

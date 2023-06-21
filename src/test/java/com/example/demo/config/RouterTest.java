package com.example.demo.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

@ExtendWith(MockitoExtension.class)
class RouterTest {

  @Mock
  private CustomerRepository customerRepository;

  @Test
  public void testRoute() {
    var customers = Flux.range(0, 2)
        .map(Long::valueOf)
        .zipWith(Flux.just("test1", "test2"), Customer::new);
    when(customerRepository.findAll())
        .thenReturn(customers);

    var router = new Router();

    var client = WebTestClient.bindToRouterFunction(router.route(customerRepository))
        .build();
    var result = client.get().uri("/rfn/customers").accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Customer.class)
        .hasSize(2)
        .returnResult();

    assertEquals(result.getResponseBody(), customers.collectList().block());
    assertNull(result.getResponseHeaders().getFirst("web-filter"));
  }
}
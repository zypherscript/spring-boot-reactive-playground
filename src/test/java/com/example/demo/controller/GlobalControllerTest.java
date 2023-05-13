package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.demo.messaging.IntervalMessageProducer;
import com.example.demo.model.Customer;
import com.example.demo.model.GreetingRequest;
import com.example.demo.model.GreetingResponse;
import com.example.demo.repository.CustomerRepository;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketRequester.RequestSpec;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@WebFluxTest(GlobalController.class)
public class GlobalControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @MockBean
  private CustomerRepository customerRepository;

  @MockBean
  private IntervalMessageProducer intervalMessageProducer;

  @MockBean
  private RSocketRequester rSocketRequester;

  @Mock
  private RequestSpec requestSpec;

  @Test
  public void testCustomers() {
    var customers = Flux.range(0, 2)
        .zipWith(Flux.just("test1", "test2"), Customer::new);
    when(customerRepository.findAll()).thenReturn(customers);

    webTestClient.get().uri("/customers")
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
        .expectBodyList(Customer.class)
        .hasSize(2)
        .contains(Objects.requireNonNull(customers.collectList()
                .block())
            .toArray(new Customer[0]));
  }

  @Test
  void testGreetingPublisherEndpointReturnsExpectedResults() {
    String name = "test";
    var greetingResponse = new GreetingResponse("Hello " + name + "!");
    when(intervalMessageProducer.produceGreetings(new GreetingRequest(name))).thenReturn(
        Flux.just(greetingResponse));

    webTestClient.get()
        .uri("/sse/" + name)
        .accept(MediaType.TEXT_EVENT_STREAM)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.parseMediaType("text/event-stream;charset=UTF-8"))
        .returnResult(GreetingResponse.class)
        .getResponseBody()
        .as(StepVerifier::create)
        .expectNext(greetingResponse)
        .thenCancel()
        .verify();
  }

  @Test
  void testGreet() {
    String name = "test";
    var greetingResponse = new GreetingResponse("Hello, %s!".formatted(name));
    when(rSocketRequester.route("greeting-rsocket")).thenReturn(requestSpec);
    when(requestSpec.data(any())).thenReturn(requestSpec);
    when(requestSpec.retrieveFlux(GreetingResponse.class)).thenReturn(
        Flux.just(greetingResponse, greetingResponse));

    var result = webTestClient.get()
        .uri("/greeting/{n}", name)
        .accept(MediaType.parseMediaType("text/event-stream;charset=UTF-8"))
        .exchange()
        .expectStatus().isOk()
        .returnResult(GreetingResponse.class);
    StepVerifier.create(result.getResponseBody())
        .expectNext(greetingResponse)
        .expectNext(greetingResponse)
        .thenCancel()
        .verify();
  }
}
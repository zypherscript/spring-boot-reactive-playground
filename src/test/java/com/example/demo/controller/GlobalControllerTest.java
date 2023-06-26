package com.example.demo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.demo.dto.UserDto;
import com.example.demo.fetcher.UserFetcher;
import com.example.demo.messaging.IntervalMessageProducer;
import com.example.demo.model.Customer;
import com.example.demo.model.GreetingRequest;
import com.example.demo.model.GreetingResponse;
import com.example.demo.model.Post;
import com.example.demo.repository.h2.CustomerRepository;
import com.example.demo.repository.mongo.PostRepository;
import java.util.List;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@WebFluxTest(value = GlobalController.class, properties = "jsonplaceholder.test=false")
public class GlobalControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @MockBean
  private CustomerRepository customerRepository;

  @MockBean
  private PostRepository postRepository;

  @MockBean
  private IntervalMessageProducer intervalMessageProducer;

  @MockBean
  private RSocketRequester rSocketRequester;

  @MockBean
  private UserFetcher userFetcher;

  @Mock
  private RequestSpec requestSpec;

  @Test
  public void testCustomers() {
    var customers = Flux.range(0, 2)
        .map(Long::valueOf)
        .zipWith(Flux.just("test1", "test2"), Customer::new);
    when(customerRepository.findAll()).thenReturn(customers);

    var response = webTestClient.get().uri("/customers")
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
        .expectBodyList(Customer.class)
        .hasSize(2)
        .returnResult();

    assertEquals(response.getResponseBody(), customers.collectList().block());
    assertEquals(response.getResponseHeaders().getFirst("web-filter"),
        "web-filter-test");
  }

  @Test
  public void testCustomer() {
    var customer = new Customer(1L, "test");
    when(customerRepository.findById(1L)).thenReturn(Mono.just(customer));

    webTestClient.get().uri("/customer/1")
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
        .expectBody(Customer.class)
        .isEqualTo(customer);
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

  @Test
  public void testUsers() {
    Flux<UserDto> users = Flux.just(
        new UserDto(1, "John Doe", "johndoe", "www.johndoe.com"),
        new UserDto(2, "Jane Smith", "janesmith", "www.janesmith.com")
    );
    when(userFetcher.retrieveUsers()).thenReturn(users);

    webTestClient.get().uri("/users")
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
        .expectBodyList(UserDto.class)
        .hasSize(2)
        .contains(Objects.requireNonNull(users.collectList()
                .block())
            .toArray(new UserDto[0]));
  }

  @Test
  public void testPosts() {
    var post1 = new Post(null, "Title 1", "Content 1");
    var post2 = Post.builder().title("Another Title").content("Content 2").build();
    when(postRepository.findAll()).thenReturn(Flux.just(post1, post2));

    var response = webTestClient.get().uri("/posts")
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
        .expectBodyList(Post.class)
        .hasSize(2)
        .returnResult();

    assertEquals(response.getResponseBody(), List.of(post1, post2));
    assertEquals(response.getResponseHeaders().getFirst("web-filter"),
        "web-filter-test");
  }
}
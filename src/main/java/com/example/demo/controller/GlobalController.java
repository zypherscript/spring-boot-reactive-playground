package com.example.demo.controller;

import com.example.demo.dto.UserDto;
import com.example.demo.fetcher.UserFetcher;
import com.example.demo.messaging.IntervalMessageProducer;
import com.example.demo.model.Customer;
import com.example.demo.model.GreetingRequest;
import com.example.demo.model.GreetingResponse;
import com.example.demo.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class GlobalController {

  private final CustomerRepository customerRepository;
  private final IntervalMessageProducer intervalMessageProducer;
  private final RSocketRequester rSocketRequester;
  private final UserFetcher userFetcher;

  @GetMapping("/customers")
  Flux<Customer> customers() {
    return customerRepository.findAll();
  }

  @GetMapping("/customer/{id}")
  Mono<Customer> customer(@PathVariable Long id) {
    return customerRepository.findById(id);
  }

  @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE, value = "/sse/{n}")
  Flux<GreetingResponse> greetingPublisher(@PathVariable String n) {
    return intervalMessageProducer.produceGreetings(new GreetingRequest(n));
  }

  @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE, value = "/greeting/{n}")
  public Publisher<GreetingResponse> greet(@PathVariable String n) {
    return rSocketRequester
        .route("greeting-rsocket")
        .data(new GreetingRequest(n))
        .retrieveFlux(GreetingResponse.class);
  }

  @GetMapping("/users")
  Flux<UserDto> users() {
    return userFetcher.retrieveUsers();
  }
}

package com.example.demo.rsocketserver.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.demo.messaging.IntervalMessageProducer;
import com.example.demo.model.GreetingRequest;
import com.example.demo.model.GreetingResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class RsocketControllerTest {

  @Mock
  private IntervalMessageProducer intervalMessageProducer;

  @InjectMocks
  private RsocketController rsocketController;

  @Test
  public void testGreet() {
    var greetingRequest = new GreetingRequest("test");
    var greetingResponse = new GreetingResponse();
    greetingResponse.setGreeting("Hello, test!");

    when(intervalMessageProducer.produceGreetings(any(GreetingRequest.class))).thenReturn(
        Flux.just(greetingResponse));

    var result = rsocketController.greet(greetingRequest);

    StepVerifier.create(result)
        .expectNext(greetingResponse)
        .verifyComplete();
  }
}
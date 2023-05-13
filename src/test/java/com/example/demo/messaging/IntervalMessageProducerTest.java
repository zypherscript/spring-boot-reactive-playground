package com.example.demo.messaging;

import com.example.demo.model.GreetingRequest;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class IntervalMessageProducerTest {

  @Test
  public void testProduceGreetings() {
    var request = new GreetingRequest();
    request.setName("test");

    var producer = new IntervalMessageProducer();
    var flux = producer.produceGreetings(request);

    StepVerifier.create(flux.take(3))
        .expectNextCount(3)
        .expectComplete()
        .verify(Duration.ofSeconds(5));
  }
}
package com.example.demo.messaging;

import com.example.demo.model.GreetingRequest;
import com.example.demo.model.GreetingResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class IntervalMessageProducer {

  public Flux<GreetingResponse> produceGreetings(GreetingRequest greetingRequest) {
    return Flux.fromStream(
            Stream.generate(() -> "Hi " + greetingRequest.getName() + " @ " + Instant.now()))
        .map(GreetingResponse::new)
        .delayElements(Duration.ofSeconds(1));
  }
}

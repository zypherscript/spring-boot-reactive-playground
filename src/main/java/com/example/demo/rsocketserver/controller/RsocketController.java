package com.example.demo.rsocketserver.controller;

import com.example.demo.messaging.IntervalMessageProducer;
import com.example.demo.model.GreetingRequest;
import com.example.demo.model.GreetingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
@RequiredArgsConstructor
public class RsocketController {

  private final IntervalMessageProducer intervalMessageProducer;

  @MessageMapping("greeting-rsocket")
  Flux<GreetingResponse> greet(GreetingRequest greetingRequest) {
    return intervalMessageProducer.produceGreetings(greetingRequest);
  }

}

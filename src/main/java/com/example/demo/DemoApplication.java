package com.example.demo;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

@SpringBootApplication
public class DemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(DemoApplication.class, args);
  }

  @Bean
  CommandLineRunner runner(CustomerRepository customerRepository,
      DatabaseClient databaseClient) {
    var ddl = databaseClient.sql(
            "create table customer(id serial primary key, name varchar(255) not null)")
        .fetch()
        .rowsUpdated();
    return args -> {
      var saved = Flux.just("test1", "test2")
          .map(n -> new Customer(null, n))
          .flatMap(customerRepository::save);
      var all = customerRepository.findAll();

      ddl
          .thenMany(saved)
          .thenMany(all)
          .subscribe(System.out::println);
    };
  }

  @Bean
  RouterFunction<ServerResponse> route(CustomerRepository customerRepository) {
    return RouterFunctions.route()
        .GET("/rfn/customers",
            request -> ServerResponse.ok().body(customerRepository.findAll(), Customer.class))
        .build();
  }
}

interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {

}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Customer {

  @Id
  Integer id;
  String name;
}

@RestController
@RequiredArgsConstructor
class CustomerRestController {

  private final CustomerRepository customerRepository;
  private final IntervalMessageProducer intervalMessageProducer;
  private final RSocketRequester rSocketRequester;

  @GetMapping("/customers")
  Flux<Customer> customers() {
    return customerRepository.findAll();
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
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class GreetingRequest {

  private String name;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class GreetingResponse {

  private String greeting;
}

@Component
class IntervalMessageProducer {

  Flux<GreetingResponse> produceGreetings(GreetingRequest greetingRequest) {
    return Flux.fromStream(
            Stream.generate(() -> "Hi " + greetingRequest.getName() + " @ " + Instant.now()))
        .map(GreetingResponse::new)
        .delayElements(Duration.ofSeconds(1));
  }
}

@Controller
@RequiredArgsConstructor
class RsocketController {

  private final IntervalMessageProducer intervalMessageProducer;

  @MessageMapping("greeting-rsocket")
  Flux<GreetingResponse> greet(GreetingRequest greetingRequest) {
    return intervalMessageProducer.produceGreetings(greetingRequest);
  }

}

@Configuration
class ClientConfiguration {

  @Bean
  public RSocketRequester getRSocketRequester(RSocketRequester.Builder builder) {

    return builder
        .rsocketConnector(
            rSocketConnector ->
                rSocketConnector.reconnect(Retry.fixedDelay(2, Duration.ofSeconds(2)))
        )
        .dataMimeType(MimeTypeUtils.APPLICATION_JSON)
        .tcp("localhost", 9797);
  }
}

package com.example.demo.config;

import com.example.demo.model.Customer;
import com.example.demo.repository.h2.CustomerRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Configuration
public class Router {

  @Bean
  RouterFunction<ServerResponse> routeAll(CustomerRepository customerRepository) {
    return RouterFunctions.route()
        .GET("/rfn/customers",
            request -> ServerResponse.ok().body(customerRepository.findAll(), Customer.class))
        .build();
  }

  @Bean
  RouterFunction<ServerResponse> route(CustomerRepository customerRepository) {
    return RouterFunctions.route()
        .GET("/rfn/customer/{id}", request -> {
          String id = request.pathVariable("id");
          return customerRepository.findById(Long.valueOf(id))
              .flatMap(customer -> ServerResponse.ok().body(Mono.just(customer), Customer.class))
              .switchIfEmpty(ServerResponse.notFound().build());
        })
        .build();
  }
}

package com.example.demo.config;

import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class Router {

  @Bean
  RouterFunction<ServerResponse> route(CustomerRepository customerRepository) {
    return RouterFunctions.route()
        .GET("/rfn/customers",
            request -> ServerResponse.ok().body(customerRepository.findAll(), Customer.class))
        .build();
  }
}

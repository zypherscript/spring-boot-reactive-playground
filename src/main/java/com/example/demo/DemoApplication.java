package com.example.demo;

import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class DemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(DemoApplication.class, args);
  }

  @Bean
  WebClient webClient() {
    return WebClient.builder().build();
  }

  @Bean
  @Profile("dev")
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
}

package com.example.demo;

import com.example.demo.dto.UserDto;
import com.example.demo.model.Customer;
import com.example.demo.repository.h2.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Flux;

@SpringBootApplication
@Slf4j
public class DemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(DemoApplication.class, args);
  }

  @Bean
  WebClient webClient() {
    return WebClient.builder().build();
  }

  @Bean
  @ConditionalOnProperty(name = "jsonplaceholder.test", havingValue = "true")
  ApplicationRunner applicationRunner(UserHttpClient userHttpClient) {
    return args -> userHttpClient.all().subscribe(userDto -> log.info(userDto.toString()));
  }

  @Bean
  @ConditionalOnProperty(name = "jsonplaceholder.test", havingValue = "true")
  UserHttpClient client(WebClient.Builder builder) {
    var wc = builder.baseUrl("https://jsonplaceholder.typicode.com").build();
    var wca = WebClientAdapter.forClient(wc);
    return HttpServiceProxyFactory
        .builder()
        .clientAdapter(wca)
        .build()
        .createClient(UserHttpClient.class);
  }

  interface UserHttpClient {

    @GetExchange("/users")
    Flux<UserDto> all();
  }

  @Bean
  @Profile("test")
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

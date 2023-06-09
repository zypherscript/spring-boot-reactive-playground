package com.example.demo.fetcher;

import com.example.demo.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class UserFetcher {

  private final WebClient webClient;

  public Flux<UserDto> retrieveUsers() {
    return webClient.get()
        .uri("https://jsonplaceholder.typicode.com/users")
        .retrieve()
        .bodyToFlux(UserDto.class);
  }
}

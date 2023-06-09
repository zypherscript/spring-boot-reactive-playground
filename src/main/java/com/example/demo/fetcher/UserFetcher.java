package com.example.demo.fetcher;

import com.example.demo.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class UserFetcher {

  private final WebClient webClient;

  private String userUrl;

  @Autowired
  public void setUserUrl(@Value("${user.url}") String userUrl) {
    this.userUrl = userUrl;
  }

  public Flux<UserDto> retrieveUsers() {
    return webClient.get()
        .uri(userUrl)
        .retrieve()
        .bodyToFlux(UserDto.class);
  }
}

package com.example.demo.fetcher;

import static org.mockito.Mockito.when;

import com.example.demo.dto.UserDto;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith({MockitoExtension.class})
@SuppressWarnings({"rawtypes", "unchecked"})
class UserFetcherTest {

  @Mock
  private WebClient webClient;
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersMock;
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriMock;
  @Mock
  private WebClient.ResponseSpec responseMock;

  @InjectMocks
  private UserFetcher userFetcher;

  private final String userUrl = "url";

  @BeforeEach
  void init() {
    userFetcher.setUserUrl(userUrl);
  }

  @Test
  void testRetrieveUsers() {
    var user1 = new UserDto(1, "John Doe", "johndoe", "www.johndoe.com");
    var user2 = new UserDto(2, "Jane Smith", "janesmith", "www.janesmith.com");

    when(webClient.get()).thenReturn(requestHeadersUriMock);
    when(requestHeadersUriMock.uri(this.userUrl))
        .thenReturn(requestHeadersMock);
    when(requestHeadersMock.retrieve()).thenReturn(responseMock);
    when(responseMock.bodyToFlux(UserDto.class)).thenReturn(
        Flux.fromIterable(List.of(user1, user2)));

    var resultFlux = userFetcher.retrieveUsers();

    StepVerifier.create(resultFlux)
        .expectNext(user1)
        .expectNext(user2)
        .verifyComplete();
  }
}
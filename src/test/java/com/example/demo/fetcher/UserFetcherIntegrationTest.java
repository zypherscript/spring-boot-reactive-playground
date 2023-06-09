package com.example.demo.fetcher;

import com.example.demo.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = "jsonplaceholder.url=http://localhost:9090")
@Import({UserFetcher.class})
@AutoConfigureWireMock(port = 9090)
class UserFetcherIntegrationTest {

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserFetcher userFetcher;

  @Test
  void validate_shouldReturnAScanId() throws Exception {
    var user1 = new UserDto(1, "John Doe", "johndoe", "www.johndoe.com");
    var user2 = new UserDto(2, "Jane Smith", "janesmith", "www.janesmith.com");
    var body = this.objectMapper.writeValueAsString(List.of(user1, user2));

    WireMock.stubFor(
        WireMock.get("/users")
            .willReturn(WireMock.aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(body))
    );

    var resultFlux = userFetcher.retrieveUsers();

    StepVerifier.create(resultFlux)
        .expectNext(user1)
        .expectNext(user2)
        .verifyComplete();
  }
}
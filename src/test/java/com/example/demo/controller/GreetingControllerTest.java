package com.example.demo.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(GreetingController.class)
@WithMockUser
class GreetingControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @Test
  public void testGreetingWithDefaultName() {
    webTestClient.get().uri("/greeting-view")
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class)
        .value(body -> {
          assertThat(body).contains("Hello, World!");
        });
  }

  @Test
  public void testGreetingWithNameParameter() {
    String name = "test";

    webTestClient.get().uri("/greeting-view?name={name}", name)
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class)
        .value(body -> {
          assertThat(body).contains("Hello, " + name + "!");
        });
  }
}
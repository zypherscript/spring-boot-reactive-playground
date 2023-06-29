package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.demo.dto.UserDto;
import com.example.demo.model.Customer;
import com.example.demo.model.Post;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.MultiValueMap;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Disabled("it")
@Slf4j
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@WithMockUser
public class DemoApplicationIntegrationTest {

  @Autowired
  private WebTestClient webTestClient;

  @ParameterizedTest
  @ValueSource(strings = {"", "/rfn"})
  public void testCustomers(String prefix) {
    webTestClient.get().uri(prefix + "/customers")
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
        .expectBodyList(Customer.class)
        .consumeWith(req -> {
          assertNotNull(req.getResponseBody());
          assertEquals(req.getResponseBody().size(), 2);
          assertEquals(req.getResponseHeaders().getFirst("web-filter"),
              "web-filter-test");
        });
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "/rfn"})
  public void testCustomer(String prefix) {
    webTestClient.get().uri(prefix + "/customer/1")
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
        .expectBodyList(Customer.class)
        .consumeWith(req -> {
          assertNotNull(req.getResponseBody());
          assertEquals(req.getResponseBody().size(), 1);
        });
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "/rfn"})
  public void testCustomerNotFound(String prefix) {
    webTestClient.get().uri(prefix + "/customer/-1")
        .exchange()
        .expectStatus().isNotFound()
        .expectBodyList(Customer.class)
        .hasSize(0);
  }

  @Test
  public void testUsers() {
    webTestClient.get().uri("/users")
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
        .expectBodyList(UserDto.class)
        .hasSize(10);
  }

  private MultiValueMap<String, HttpEntity<?>> generateBody() {
    var builder = new MultipartBodyBuilder();
    builder.part("fileParts", new ClassPathResource("/foo.txt"));
    return builder.build();
  }

  @Test
  public void testUpload() throws IOException {
    byte[] result = webTestClient
        .post()
        .uri("/multipart")
        .bodyValue(generateBody())
        .exchange()
        .expectStatus().isOk()
        .expectBody().returnResult().getResponseBody();

    var objectMapper = new ObjectMapper();
    var bodyMap = objectMapper.readValue(result, Map.class);

    String fileId = String.valueOf(bodyMap.get("id"));
    log.info("updated file id:" + fileId);
    assertNotNull(fileId);

    //test read
    webTestClient
        .get()
        .uri("/multipart/{id}", fileId)
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  public void testPostByTitleContains() {
    webTestClient.get().uri("/posts/one")
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
        .expectBodyList(Post.class)
        .consumeWith(req -> {
          assertNotNull(req.getResponseBody());
          assertEquals(req.getResponseBody().size(), 1);
          assertEquals(req.getResponseHeaders().getFirst("web-filter"),
              "web-filter-test");
        });
  }
}

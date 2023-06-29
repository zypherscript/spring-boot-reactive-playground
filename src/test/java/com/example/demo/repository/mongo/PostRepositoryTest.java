package com.example.demo.repository.mongo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.demo.DemoApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = DemoApplication.class)
@ActiveProfiles("test")
class PostRepositoryTest {

  @Autowired
  private PostRepository postRepository;

  @Test
  public void testFindByTitleContains() {
    // Call the method being tested
    var resultFlux = postRepository.findByTitleContains("one");

    // Verify the result using StepVerifier
    StepVerifier.create(resultFlux)
        .assertNext(post -> {
          var title = "Post one";
          assertEquals(title, post.getTitle());
          assertEquals("content of " + title, post.getContent());
          assertNotNull(post.getId());
        })
        .verifyComplete();
  }
}
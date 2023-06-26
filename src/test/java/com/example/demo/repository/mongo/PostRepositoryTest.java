package com.example.demo.repository.mongo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.demo.DemoApplication;
import com.example.demo.model.Post;
import java.util.List;
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
    // Create some test data
    var post1 = new Post(null, "Title 1", "Content 1");
    var post2 = Post.builder().title("Another Title").content("Content 2").build();
    var post3 = Post.builder().title("Some Title").content("Content 3").build();

    postRepository.saveAll(List.of(post1, post2, post3)).blockLast();

    // Call the method being tested
    var resultFlux = postRepository.findByTitleContains("1");

    // Verify the result using StepVerifier
    StepVerifier.create(resultFlux)
        .assertNext(post -> {
          assertEquals("Title 1", post.getTitle());
          assertEquals("Content 1", post.getContent());
          assertNotNull(post.getId());
        })
        .verifyComplete();
  }
}
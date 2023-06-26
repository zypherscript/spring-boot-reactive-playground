package com.example.demo.config;

import com.example.demo.model.Post;
import com.example.demo.repository.mongo.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("test")
class PostDataInitializer implements CommandLineRunner {

  private final PostRepository postRepository;

  @Override
  public void run(String[] args) {
    log.info("start data initialization  ...");
    this.postRepository
        .deleteAll()
        .thenMany(
            Flux
                .just("Post one", "Post two")
                .flatMap(
                    title -> this.postRepository.save(
                        Post.builder().title(title).content("content of " + title).build())
                )
        )
        .log()
        .subscribe(
            null,
            null,
            () -> log.info("done initialization...")
        );
  }
}

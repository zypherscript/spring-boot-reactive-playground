package com.example.demo.repository.redis;

import com.example.demo.model.redis.RedisPost;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class RedisPostRepositoryTest {

  @Autowired
  private ReactiveRedisTemplate<String, RedisPost> redisTemplate;

  @Autowired
  private RedisPostRepository postRepository;

  @AfterEach
  public void tearDown() {
    redisTemplate.opsForHash().delete("posts").block();
  }

  @Test
  public void findAll_ShouldReturnAllPosts() {
    RedisPost post1 = new RedisPost("1", "Title 1", "Content 1");
    RedisPost post2 = new RedisPost("2", "Title 2", "Content 2");
    redisTemplate.opsForHash().put("posts", post1.getId(), post1).block();
    redisTemplate.opsForHash().put("posts", post2.getId(), post2).block();

    Flux<RedisPost> result = postRepository.findAll();

    StepVerifier.create(result)
        .expectNext(post1, post2)
        .verifyComplete();
  }

  @Test
  public void testFindById() {
    RedisPost post = new RedisPost("1", "Title 1", "Content 1");
    redisTemplate.opsForHash().put("posts", post.getId(), post).block();

    Mono<RedisPost> resultMono = postRepository.findById(post.getId());
    StepVerifier.create(resultMono)
        .expectNext(post)
        .verifyComplete();
  }

  @Test
  public void testSave() {
    RedisPost post = new RedisPost("1", "Title 1", "Content 1");

    Mono<RedisPost> resultMono = postRepository.save(post);
    StepVerifier.create(resultMono)
        .expectNext(post)
        .verifyComplete();

    Mono<RedisPost> savedPostMono = redisTemplate.<String, RedisPost>opsForHash()
        .get("posts", post.getId());
    StepVerifier.create(savedPostMono)
        .expectNext(post)
        .verifyComplete();
  }

  @Test
  public void testDeleteById() {
    RedisPost post = new RedisPost("1", "Title 1", "Content 1");
    redisTemplate.opsForHash().put("posts", post.getId(), post).block();

    Mono<Void> resultMono = postRepository.deleteById(post.getId());
    StepVerifier.create(resultMono)
        .verifyComplete();

    Mono<RedisPost> deletedPostMono = redisTemplate.<String, RedisPost>opsForHash()
        .get("posts", post.getId());
    StepVerifier.create(deletedPostMono)
        .verifyComplete();
  }

  @Test
  public void testDeleteAll() {
    RedisPost post1 = new RedisPost("1", "Title 1", "Content 1");
    RedisPost post2 = new RedisPost("2", "Title 2", "Content 2");
    redisTemplate.opsForHash().put("posts", post1.getId(), post1).block();
    redisTemplate.opsForHash().put("posts", post2.getId(), post2).block();

    Mono<Boolean> resultMono = postRepository.deleteAll();
    StepVerifier.create(resultMono)
        .expectNext(true)
        .verifyComplete();

    Mono<Long> countMono = redisTemplate.opsForHash().size("posts");
    StepVerifier.create(countMono)
        .expectNext(0L)
        .verifyComplete();
  }
}
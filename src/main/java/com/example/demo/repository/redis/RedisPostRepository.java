package com.example.demo.repository.redis;

import com.example.demo.model.redis.RedisPost;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
class RedisPostRepository {

  private final ReactiveRedisOperations<String, RedisPost> template;

  Flux<RedisPost> findAll() {
    return template.<String, RedisPost>opsForHash().values("posts");
  }

  Mono<RedisPost> findById(String id) {
    return template.<String, RedisPost>opsForHash().get("posts", id);
  }

  Mono<RedisPost> save(RedisPost post) {
    if (post.getId() != null) {
      String id = UUID.randomUUID().toString();
      post.setId(id);
    }
    return template.<String, RedisPost>opsForHash().put("posts", post.getId(), post)
        .log()
        .map(p -> post);

  }

  Mono<Void> deleteById(String id) {
    return template.<String, RedisPost>opsForHash().remove("posts", id)
        .flatMap(p -> Mono.<Void>empty());
  }

  Mono<Boolean> deleteAll() {
    return template.<String, RedisPost>opsForHash().delete("posts");
  }
}

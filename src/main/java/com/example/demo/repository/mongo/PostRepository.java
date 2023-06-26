package com.example.demo.repository.mongo;

import com.example.demo.model.Post;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface PostRepository extends ReactiveMongoRepository<Post, String> {

  Flux<Post> findByTitleContains(String text);
}

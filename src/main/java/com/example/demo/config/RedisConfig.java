package com.example.demo.config;

import com.example.demo.model.redis.RedisPost;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories(basePackages = "com.example.demo.repository.redis")
public class RedisConfig {

  @Bean
  public ReactiveRedisTemplate<String, RedisPost> reactiveJsonPostRedisTemplate(
      ReactiveRedisConnectionFactory connectionFactory) {

    RedisSerializationContext<String, RedisPost> serializationContext = RedisSerializationContext
        .<String, RedisPost>newSerializationContext(new StringRedisSerializer())
        .hashKey(new StringRedisSerializer())
        .hashValue(new Jackson2JsonRedisSerializer<>(RedisPost.class))
        .build();

    return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
  }
}

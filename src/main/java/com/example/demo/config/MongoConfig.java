package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(basePackages = "com.example.demo.repository.mongo")
public class MongoConfig extends AbstractReactiveMongoConfiguration {

  @Override
  protected String getDatabaseName() {
    return "demo";
  }

  @Bean
  public ReactiveGridFsTemplate reactiveGridFsTemplate(
      ReactiveMongoDatabaseFactory databaseFactory,
      MappingMongoConverter mongoConverter) {
    return new ReactiveGridFsTemplate(databaseFactory, mongoConverter);
  }
}

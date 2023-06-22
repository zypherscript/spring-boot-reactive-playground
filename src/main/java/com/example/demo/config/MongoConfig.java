package com.example.demo.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories
public class MongoConfig extends AbstractReactiveMongoConfiguration {

  @Override
  protected String getDatabaseName() {
    return "demo";
  }

  @Override
  public MongoClient reactiveMongoClient() {
    //docker run -d -p 27017:27017 --name local-mongo mongo:latest
    return MongoClients.create("mongodb://localhost:27017/");
  }

  @Bean
  public ReactiveGridFsTemplate reactiveGridFsTemplate(
      ReactiveMongoDatabaseFactory databaseFactory,
      MappingMongoConverter mongoConverter) {
    return new ReactiveGridFsTemplate(databaseFactory, mongoConverter);
  }
}

package com.example.demo.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.demo.model.Customer;
import com.example.demo.repository.h2.CustomerRepository;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@DataR2dbcTest(properties = "jsonplaceholder.test=false")
class CustomerRepositoryTest {

  @Autowired
  private DatabaseClient databaseClient;
  @Autowired
  private CustomerRepository customerRepository;

  @Test
  public void testDatabaseClientExisted() {
    assertNotNull(databaseClient);
  }

  @Test
  public void testPostRepositoryExisted() {
    assertNotNull(customerRepository);
  }

  @Test
  public void testInsertAndQuery() {
    var statements = Arrays.asList(
        "drop table if exists customer;",
        "create table customer(id serial primary key, name varchar(255) not null);");

    statements.forEach(sql -> databaseClient.sql(sql)
        .fetch()
        .rowsUpdated()
        .as(StepVerifier::create)
        .expectNextCount(1)
        .verifyComplete());

    Flux.just("test1", "test2")
        .map(n -> new Customer(null, n))
        .flatMap(customerRepository::save)
        .subscribe();

    customerRepository.findAll()
        .as(StepVerifier::create)
        .consumeNextWith(customer -> assertEquals("test1", customer.getName()))
        .consumeNextWith(customer -> assertEquals("test2", customer.getName()))
        .verifyComplete();
  }
}
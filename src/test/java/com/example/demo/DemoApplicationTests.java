package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.controller.GlobalController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

  @Autowired
  private GlobalController globalController;

  @Test
  void contextLoads() {
    assertThat(globalController).isNotNull();
  }

}

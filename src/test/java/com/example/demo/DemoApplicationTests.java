package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.controller.GlobalController;
import com.example.demo.controller.GreetingController;
import com.example.demo.controller.MultipartController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

  @Autowired
  private GlobalController globalController;
  @Autowired
  private MultipartController multipartController;

  @Autowired
  private GreetingController greetingController;

  @Test
  void contextLoads() {
    assertThat(globalController).isNotNull();
    assertThat(multipartController).isNotNull();
    assertThat(greetingController).isNotNull();
  }

}

package com.example.demo.exception;

import static org.springframework.http.ResponseEntity.notFound;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

  @ExceptionHandler(CustomerNotFoundException.class)
  ResponseEntity<Object> customerNotFound(CustomerNotFoundException ex) {
    log.debug("handling exception::" + ex);
    return notFound().build();
  }
}

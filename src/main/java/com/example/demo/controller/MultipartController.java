package com.example.demo.controller;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/multipart")
@RequiredArgsConstructor
public class MultipartController {

  private final ReactiveGridFsTemplate gridFsTemplate;

  @PostMapping("")
  public Mono<ResponseEntity<Object>> upload(@RequestPart Mono<FilePart> fileParts) {
    return fileParts
        .flatMap(part -> this.gridFsTemplate.store(part.content(), part.filename()))
        .map(ObjectId::toHexString)
        .map(hexString -> ResponseEntity.ok().body(Map.of("id", hexString)));
  }

  @GetMapping("{id}")
  public Flux<Void> read(@PathVariable String id, ServerWebExchange exchange) {
    return this.gridFsTemplate.findOne(query(where("_id").is(id)))
        .log()
        .flatMap(gridFsTemplate::getResource)
        .flatMapMany(r -> exchange.getResponse().writeWith(r.getDownloadStream()));
  }

}

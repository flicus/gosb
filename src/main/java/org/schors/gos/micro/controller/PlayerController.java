package org.schors.gos.micro.controller;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import lombok.extern.slf4j.Slf4j;
import org.schors.gos.micro.model.Player;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller("/api/player")
@Slf4j
public class PlayerController {

  @Get
  public Flux<Player> get() {

    return Flux.empty();
  }

  @Get("/{id}")
  public Mono<Player> getById(@PathVariable String id) {

    return Mono.empty();
  }

  @Post
  public Mono<Player> create(@Body Player player) {

    return Mono.empty();
  }

  @Put("/{id}")
  public Mono<Player> update(@PathVariable String id, @Body Player player) {

    return Mono.empty();
  }

  @Delete("/{id}")
  public Mono<Boolean> delete(@PathVariable String id) {

    return Mono.from(Boolean.TRUE);
  }

}

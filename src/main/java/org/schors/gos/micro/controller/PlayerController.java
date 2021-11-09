package org.schors.gos.micro.controller;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.schors.gos.micro.model.Player;
import org.schors.gos.micro.repository.PlayerRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller("/api/player")
@Slf4j
@RequiredArgsConstructor
public class PlayerController {

  private PlayerRepository playerRepository;

  @Get
  public Flux<Player> get() {
    return Flux.fromIterable(playerRepository.getAllPlayers());
  }

  @Get("/{id}")
  public Mono<Player> getById(@PathVariable String id) {
    return Mono.just(playerRepository.getPlayerById(id));
  }

  @Post
  public Mono<Player> create(@Body Player player) {
    return Mono.just(playerRepository.createPlayer(player));
  }

  @Put("/{id}")
  public Mono<Player> update(@PathVariable String id, @Body Player player) {
    return Mono.just(playerRepository.updatePlayer(id, player));
  }

  @Delete("/{id}")
  public Mono<Boolean> delete(@PathVariable String id) {
    return Mono.just(playerRepository.deletePlayer(id));
  }

}

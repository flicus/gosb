package org.schors.gos.micro.controller;

import io.micronaut.http.annotation.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.schors.gos.micro.model.Player;
import org.schors.gos.micro.repository.PlayerRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller("/api/player")
@Slf4j
@AllArgsConstructor
public class PlayerController {

  private PlayerRepository playerRepository;

  @Get
  public Flux<Player> get() {
    log.debug("get all players");
    return playerRepository.getAllPlayers();
  }

  @Get("/{id}")
  public Mono<Player> getById(@PathVariable String id) {
    return playerRepository.getPlayerById(id);
  }

  @Post
  public Mono<Player> create(@Body Player player) {
    log.debug("create player: " + player);
    return playerRepository.createPlayer(player);
  }

  @Put("/{id}")
  public Mono<Player> update(@PathVariable String id, @Body Player player) {
    return playerRepository.updatePlayer(id, player);
  }

  @Delete("/{id}")
  public Mono<Boolean> delete(@PathVariable String id) {
    return playerRepository.deletePlayer(id);
  }

}

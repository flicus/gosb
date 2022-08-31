package org.schors.gos.micro.repository;

import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.schors.gos.micro.model.Player;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Slf4j
@Singleton
@Requires(property = "gosb.data", value = "ms")
@AllArgsConstructor
public class PlayerRepositoryMSImpl implements PlayerRepository {
  @Override
  public Flux<Player> getAllPlayers() {
    return null;
  }

  @Override
  public Mono<Player> getPlayerById(String id) {
    return null;
  }

  @Override
  public Mono<Player> createPlayer(Player player) {
    return null;
  }

  @Override
  public Mono<Player> updatePlayer(String id, Player player) {
    return null;
  }

  @Override
  public Mono<Boolean> deletePlayer(String id) {
    return null;
  }
}

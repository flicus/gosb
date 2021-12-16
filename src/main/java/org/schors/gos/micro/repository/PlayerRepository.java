package org.schors.gos.micro.repository;

import org.schors.gos.micro.model.Player;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PlayerRepository {
  Flux<Player> getAllPlayers();

  Mono<Player> getPlayerById(String id);

  Mono<Player> createPlayer(Player player);

  Mono<Player> updatePlayer(String id, Player player);

  Mono<Boolean> deletePlayer(String id);
}

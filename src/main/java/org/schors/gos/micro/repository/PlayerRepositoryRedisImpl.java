package org.schors.gos.micro.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.api.StatefulRedisConnection;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.schors.gos.micro.model.Player;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Singleton
@Requires(property = "gosb.data", value = "redis")
public class PlayerRepositoryRedisImpl implements PlayerRepository {

  private static final String PLAYER = "player";

  private final StatefulRedisConnection<String, String> connection;
  private final ObjectMapper objectMapper;

  public PlayerRepositoryRedisImpl(StatefulRedisConnection<String, String> connection, ObjectMapper objectMapper) {
    this.connection = connection;
    this.objectMapper = objectMapper;
  }

  private Player str2pl(String str) {
    try {
      return objectMapper.readValue(str, Player.class);
    } catch (JsonProcessingException e) {
      log.warn(e.getMessage(), e);
    }
    return null;
  }

  private String pl2str(Player player) {
    try {
      return objectMapper.writeValueAsString(player);
    } catch (JsonProcessingException e) {
      log.warn(e.getMessage(), e);
    }
    return null;
  }

  @Override
  public Flux<Player> getAllPlayers() {
    return connection.reactive()
      .hvals(PLAYER)
      .map(this::str2pl);
  }

  @Override
  public Mono<Player> getPlayerById(String id) {
    return connection.reactive().hget(PLAYER, id).map(this::str2pl);
  }

  @Override
  public Mono<Player> createPlayer(Player player) {
    if (player.getId() == null) {
      player.setId(String.valueOf(player.getName().hashCode()));
    }
    return connection.reactive()
      .hexists(PLAYER, player.getId())
      .flatMap(aBoolean -> {
        Mono<Player> result;
        if (aBoolean) {
          log.warn("player already exist: " + player.getId());
          result = Mono.error(new IllegalArgumentException("Already exist"));
        } else {
          log.debug("creating player: " + player);
          result = connection.reactive()
            .hset(PLAYER, player.getId(), pl2str(player))
            .map(aBoolean1 -> player);
        }
        return result;
      });
  }

  @Override
  public Mono<Player> updatePlayer(String id, Player player) {
    return connection.reactive()
      .hset(PLAYER, id, pl2str(player))
      .map(aBoolean -> player);
  }

  @Override
  public Mono<Boolean> deletePlayer(String id) {
    return connection.reactive().hdel(PLAYER, id).map(aLong -> aLong > 0);
  }
}

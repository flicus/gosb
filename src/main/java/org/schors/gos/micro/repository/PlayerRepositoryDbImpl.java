package org.schors.gos.micro.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.mapdb.DB;
import org.mapdb.Serializer;
import org.schors.gos.micro.model.Player;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentMap;

@Slf4j
@Singleton
@Requires(property = "gosb.data", value = "db")
public class PlayerRepositoryDbImpl implements PlayerRepository {

  private final DB db;
  private final ObjectMapper objectMapper;
  private final ConcurrentMap<String, String> map;

  public PlayerRepositoryDbImpl(DB db, ObjectMapper objectMapper) {
    this.db = db;
    this.objectMapper = objectMapper;
    map = db.hashMap("player", Serializer.STRING, Serializer.STRING).createOrOpen();
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
    return Flux
      .fromIterable(map.values())
      .map(this::str2pl)
      .onErrorContinue((throwable, o) -> {
      });
  }

  @Override
  public Mono<Player> getPlayerById(String id) {
    Player player = str2pl(map.get(id));
    return player != null ? Mono.just(player) : Mono.error(new IllegalArgumentException("No or corrupted player"));
  }

  @Override
  public Mono<Player> createPlayer(Player player) {
    if (player.getId() == null) {
      player.setId(String.valueOf(player.getName().hashCode()));
    }
    map.putIfAbsent(player.getId(), pl2str(player));
    db.commit();
    return Mono.just(player);
  }

  @Override
  public Mono<Player> updatePlayer(String id, Player player) {
    String str = pl2str(player);
    if (str != null) {
      map.put(id, str);
      db.commit();
      return Mono.just(player);
    }
    return Mono.error(new IllegalArgumentException(""));
  }

  @Override
  public Mono<Boolean> deletePlayer(String id) {
    Mono<Boolean> res = Mono.just(map.remove(id) != null);
    db.commit();
    return res;
  }
}

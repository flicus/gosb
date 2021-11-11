package org.schors.gos.micro.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.mapdb.DB;
import org.mapdb.Serializer;
import org.schors.gos.micro.model.Player;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

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

  @Override
  public List<Player> getAllPlayers() {
    return map
      .values()
      .stream()
      .map(s -> {
        try {
          return objectMapper.readValue(s, Player.class);
        } catch (JsonProcessingException e) {
          log.warn(e.getMessage(), e);
        }
        return null;
      })
      .filter(player -> player != null)
      .collect(Collectors.toList());
  }

  @Override
  public Player getPlayerById(String id) {
    try {
      return objectMapper.readValue(map.get(id), Player.class);
    } catch (JsonProcessingException e) {
      log.warn(e.getMessage(), e);
    }
    return null;
  }

  @Override
  public Player createPlayer(Player player) {
    if (player.getId() == null) {
      player.setId(String.valueOf(player.getName().hashCode()));
    }
    try {
      map.putIfAbsent(player.getId(), objectMapper.writeValueAsString(player));
      return player;
    } catch (JsonProcessingException e) {
      log.warn(e.getMessage(), e);
    }
    return null;
  }

  @Override
  public Player updatePlayer(String id, Player player) {
    try {
      map.put(id, objectMapper.writeValueAsString(player));
      return player;
    } catch (JsonProcessingException e) {
      log.warn(e.getMessage(), e);
    }
    return null;
  }

  @Override
  public Boolean deletePlayer(String id) {
    return map.remove(id) != null;
  }
}

package org.schors.gos.micro.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.mapdb.DB;
import org.mapdb.Serializer;
import org.schors.gos.micro.model.Alliance;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentMap;

@Slf4j
@Singleton
@Requires(property = "gosb.data", value = "db")
public class AllianceRepositoryDBImpl implements AllianceRepository {

  private final DB db;
  private final ObjectMapper objectMapper;
  private final ConcurrentMap<Integer, String> map;

  public AllianceRepositoryDBImpl(DB db, ObjectMapper objectMapper) {
    this.db = db;
    this.objectMapper = objectMapper;
    this.map = db.hashMap("alliances", Serializer.INTEGER, Serializer.STRING).createOrOpen();
  }

  private Alliance str2Al(String str) {
    try {
      return objectMapper.readValue(str, Alliance.class);
    } catch (JsonProcessingException e) {
      log.warn(e.getMessage(), e);
    }
    return null;
  }

  private String al2str(Alliance alliance) {
    try {
      return objectMapper.writeValueAsString(alliance);
    } catch (JsonProcessingException e) {
      log.warn(e.getMessage(), e);
    }
    return null;
  }


  @Override
  public Mono<Alliance> addAlliance(Alliance alliance) {
    if (alliance.getId() == null) {
      alliance.setId(alliance.getName().hashCode());
    }
    map.putIfAbsent(alliance.getId(), al2str(alliance));
    db.commit();
    return Mono.just(alliance);
  }

  @Override
  public Flux<Alliance> listAlliances() {
    return Flux
      .fromIterable(map.values())
      .map(this::str2Al)
      .onErrorContinue((throwable, o) -> {
      });
  }
}

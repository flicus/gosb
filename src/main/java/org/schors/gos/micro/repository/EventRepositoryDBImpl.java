package org.schors.gos.micro.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.mapdb.DB;
import org.mapdb.Serializer;
import org.schors.gos.micro.model.Event;
import org.schors.gos.micro.model.EventRecord;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Singleton
@Requires(property = "gosb.data", value = "db")
public class EventRepositoryDBImpl implements EventRepository {

  private final DB db;
  private final ObjectMapper objectMapper;
  private final ConcurrentMap<String, String> events;

  public EventRepositoryDBImpl(DB db, ObjectMapper objectMapper) {
    this.db = db;
    this.objectMapper = objectMapper;
    events = db.hashMap("events", Serializer.STRING, Serializer.STRING).createOrOpen();
  }


  @Override
  public Flux<Event> getEvents() {
    return Flux.fromStream(events.keySet().stream().map(s -> new Event(s, s)));
  }

  @Override
  public Mono<Boolean> deleteAllEvents() {
    return null;
  }

  @Override
  public Mono<Event> createEvent(Event event) {
    event.setId(UUID.randomUUID().toString());
    List<EventRecord> list = new ArrayList<>();
    events.put(event.getId(), er2str(list));
    db.commit();
    return Mono.just(event);
  }

  @Override
  public Flux<EventRecord> getRecords(String event, Integer count) {
    return Flux.fromIterable(str2er(events.get(event)));
  }

  @Override
  public Mono<EventRecord> createRecord(String event, EventRecord eventRecord) {
    List<EventRecord> list = str2er(events.get(event));
    list.add(eventRecord);
    events.put(event, er2str(list));
    db.commit();
    return Mono.just(eventRecord);
  }

  private List<EventRecord> str2er(String str) {
    try {
      return objectMapper.readValue(str, new TypeReference<>() {
      });
    } catch (JsonProcessingException e) {
      log.warn(e.getMessage(), e);
    }
    return null;
  }

  private String er2str(List<EventRecord> records) {
    try {
      return objectMapper.writeValueAsString(records);
    } catch (JsonProcessingException e) {
      log.warn(e.getMessage(), e);
    }
    return null;
  }
}

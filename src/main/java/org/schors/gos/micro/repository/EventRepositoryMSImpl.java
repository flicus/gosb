package org.schors.gos.micro.repository;

import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.microstream.concurrency.XThreads;
import one.microstream.storage.types.StorageManager;
import org.schors.gos.micro.model.Data;
import org.schors.gos.micro.model.Event;
import org.schors.gos.micro.model.EventRecord;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
@Singleton
@Requires(property = "gosb.data", value = "ms")
@AllArgsConstructor
public class EventRepositoryMSImpl implements EventRepository {

  private final StorageManager storageManager;

  @Override
  public Flux<Event> getEvents() {
    return Flux.fromStream(data().getEvents().stream());
  }

  @Override
  public Mono<Boolean> deleteAllEvents() {
    return XThreads.executeSynchronized(() -> {
      data().getEvents().clear();
      storageManager.store(data().getEvents());
      return Mono.just(true);
    });
  }

  @Override
  public Mono<Event> createEvent(Event event) {
    return XThreads.executeSynchronized(() ->
      data().getEvents().stream()
        .filter(e -> e.getName().equals(event.getName()))
        .findAny()
        .map(event1 -> Mono.<Event>error(new RuntimeException(String.format("Already exist:: %s", event.getName()))))
        .orElseGet(() -> {
          event.setId(UUID.randomUUID().toString());
          data().getEvents().add(event);
          storageManager.store(data().getEvents());
          return Mono.just(event);
        }));
  }

  @Override
  public Flux<EventRecord> getRecords(String id, Integer count) {
    Stream<EventRecord> stream = data().getEventRecords().computeIfAbsent(id, s -> new ArrayList<>()).stream();
    return count > 0 ? Flux.fromStream(stream.limit(count)) : Flux.fromStream(stream);
  }

  @Override
  public Mono<EventRecord> createRecord(String id, EventRecord eventRecord) {
    return XThreads.executeSynchronized(() -> {
      data().getEventRecords().computeIfAbsent(id, s -> new ArrayList<>()).add(eventRecord);
      storageManager.store(data().getEventRecords());
      return Mono.just(eventRecord);
    });
  }

  private Data data() {
    return (Data) storageManager.root();
  }
}

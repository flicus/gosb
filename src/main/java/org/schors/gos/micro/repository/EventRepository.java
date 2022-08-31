package org.schors.gos.micro.repository;

import org.schors.gos.micro.model.Event;
import org.schors.gos.micro.model.EventRecord;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventRepository {

  Flux<Event> getEvents();

  Mono<Boolean> deleteAllEvents();

  Mono<Event> createEvent(Event event);

  Flux<EventRecord> getRecords(String event, Integer count);

  Mono<EventRecord> createRecord(String event, EventRecord eventRecord);

}

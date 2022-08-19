package org.schors.gos.micro.repository;

import org.schors.gos.micro.model.EventRecord;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventRepository {

  Flux<String> getEvents();

  Mono<String> createEvent(String event);

  Flux<EventRecord> getRecords(String event);

  Mono<EventRecord> createRecord(String event, EventRecord eventRecord);

}

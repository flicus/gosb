package org.schors.gos.micro.controller;

import io.micronaut.http.annotation.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.schors.gos.micro.model.EventRecord;
import org.schors.gos.micro.repository.EventRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller("/api/event")
@Slf4j
@AllArgsConstructor
public class EventsController {

  private EventRepository eventRepository;


  @Get
  public Flux<String> getAllEvents() {
    return eventRepository.getEvents();
  }

  @Post
  public Mono<String> createEvent(@Body String event) {
    return eventRepository.createEvent(event);
  }

  @Get("/record/{id}/{count}")
  public Flux<EventRecord> getLastRecords(@PathVariable String id, @PathVariable Integer count) {
    return eventRepository.getRecords(id);
  }

  @Post("/record/{event}")
  public Mono<EventRecord> createRecord(@PathVariable String event, @Body EventRecord eventRecord) {
    return eventRepository.createRecord(event, eventRecord);
  }

}

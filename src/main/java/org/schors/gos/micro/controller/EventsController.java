package org.schors.gos.micro.controller;

import io.micronaut.http.annotation.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.schors.gos.micro.model.Event;
import org.schors.gos.micro.model.EventRecord;
import org.schors.gos.micro.repository.EventRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@Controller("/api/event")
@Slf4j
@AllArgsConstructor
public class EventsController {

  private EventRepository eventRepository;


  @Get
  public Flux<Event> getAllEvents() {
    return eventRepository.getEvents();
  }

  @Post
  public Mono<Event> createEvent(@Body Event event) {
    return eventRepository.createEvent(event);
  }

  @Delete
  public Mono<Boolean> deleteAllEvents() {
    return eventRepository.deleteAllEvents();
  }

  @Get("/record/{id}/{count}")
  public Flux<EventRecord> getLastRecords(@PathVariable String id, @PathVariable Integer count) {
    return eventRepository.getRecords(id, count);
  }

  @Get("/record/{id}")
  public Flux<EventRecord> getAllRecords(@PathVariable String id) {
    return eventRepository.getRecords(id, 0);
  }

  @Post("/record/{id}")
  public Mono<EventRecord> createRecord(@PathVariable String id, @Body EventRecord eventRecord) {
    eventRecord.setDate(new Date().toString());
    return eventRepository.createRecord(id, eventRecord);
  }

}

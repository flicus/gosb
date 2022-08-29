package org.schors.gos.micro.model;

import io.micronaut.core.annotation.Introspected;

import java.util.List;
import java.util.Map;

@Introspected
public class Data {
  private List<Event> events;
  private Map<String, EventRecord> eventRecords;

  public List<Event> getEvents() {
    return events;
  }

  public Map<String, EventRecord> getEventRecords() {
    return eventRecords;
  }
}

package org.schors.gos.micro.model;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Introspected
public class Data {
  private final List<Event> events = new ArrayList<>();
  private final Map<String, List<EventRecord>> eventRecords = new HashMap<>();

  @NonNull
  public List<Event> getEvents() {
    return events;
  }

  @NonNull
  public Map<String, List<EventRecord>> getEventRecords() {
    return eventRecords;
  }
}

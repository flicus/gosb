package org.schors.data;

import javax.enterprise.context.ApplicationScoped;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class EventRepository {

    public Multi<String> getEvents() {
        return Multi.createFrom().nothing();
    }

    public Uni<String> createRecord(String eventName, EventRecord eventRecord) {
        return Uni.createFrom().nothing();
    }
    
}

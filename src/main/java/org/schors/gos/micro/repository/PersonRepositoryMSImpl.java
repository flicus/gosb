package org.schors.gos.micro.repository;

import java.util.Optional;
import java.util.UUID;

import org.schors.gos.micro.model.Data;
import org.schors.gos.micro.model.Person;

import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.microstream.concurrency.XThreads;
import one.microstream.storage.types.StorageManager;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Singleton
@Requires(property = "gosb.data", value = "ms")
@AllArgsConstructor
public class PersonRepositoryMSImpl implements PersonRepository {

    private final StorageManager storageManager;

    @Override
    public Flux<Person> getAllPersons() {
        return Flux.fromStream(data().getPersons().stream());
    }

    @Override
    public Mono<Person> createPerson(Person person) {
        return XThreads.executeSynchronized(() -> {
            person.setId(UUID.randomUUID().toString());
            data().getPersons().add(person);
            storageManager.store(data().getPersons());
            return Mono.just(person);
        });
    }

    @Override
    public Mono<Boolean> deletePerson(String id) {
        return XThreads.executeSynchronized(() -> {
            Optional<Person> person = data().getPersons().stream()
                    .filter(p -> p.getId().equals(id))
                    .findAny();
            if (person.isPresent()) {
                data().getPersons().remove(person.get());
                storageManager.store(data().getPersons());
                return Mono.just(true);
            } else {
                return Mono.just(false);
            }
        });
    }

    @Override
    public Mono<Person> updatePerson(String id, Person person) {
        return XThreads.executeSynchronized(() -> {
            Optional<Person> toUpdate = data().getPersons().stream()
                    .filter(p -> p.getId().equals(id))
                    .findAny();
            if (toUpdate.isPresent()) {
                data().getPersons().remove(toUpdate.get());
                data().getPersons().add(person);
                storageManager.store(data().getPersons());
                return Mono.just(person);
            } else {
                return Mono.error(new IllegalArgumentException("Unknow person: " + person.getId()));
            }
        });
    }

    private Data data() {
        return (Data) storageManager.root();
    }

}

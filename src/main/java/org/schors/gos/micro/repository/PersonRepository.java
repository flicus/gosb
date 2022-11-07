package org.schors.gos.micro.repository;

import org.schors.gos.micro.model.Person;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PersonRepository {

    Flux<Person> getAllPersons();
    Mono<Person> createPerson(Person person);
    Mono<Boolean> deletePerson(String id);
    Mono<Person> updatePerson(String id, Person person);
    
}

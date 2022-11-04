package org.schors.gos.micro.controller;

import org.schors.gos.micro.model.Person;
import org.schors.gos.micro.repository.PersonRepository;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller("/api/person")
@Slf4j
@AllArgsConstructor
public class PersonController {

    private PersonRepository repository;

    @Get
    public Flux<Person> getAllPersons() {
        return repository.getAllPersons();
    }

    @Get
    public Mono<Person> createPerson(@Body Person person) {
        return repository.createPerson(person);
    }
    
    
}

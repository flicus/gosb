package org.schors.bot.config;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.eclipse.microprofile.config.spi.Converter;
import org.schors.data.Person;

import io.quarkus.logging.Log;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class PersonConverter implements Converter<List<Person>>{

    @Override
    public List<Person> convert(String value) throws IllegalArgumentException, NullPointerException {
        Log.debug(value);


        // JsonObject json = new JsonObject(value);

        List<Person> list = Optional.ofNullable(Json.decodeValue(value))
        .filter(obj -> obj instanceof JsonArray)
        .map(obj -> (JsonArray)obj)
        .get()
        .stream()
        .filter(obj -> obj instanceof JsonObject)
        .map(obj -> (JsonObject)obj)
        .map(jo -> jo.mapTo(Person.class))
        .collect(Collectors.toList());
        
        return list;

        // Person person = json.mapTo(Person.class);
        // return person;
    }
    
}

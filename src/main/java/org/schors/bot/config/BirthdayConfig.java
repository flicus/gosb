package org.schors.bot.config;

import java.util.List;

import org.schors.data.Person;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithConverter;

// @ConfigMapping(prefix = "gosb")
public interface BirthdayConfig {
    @WithConverter(PersonConverter.class)
    List<Person> birthdays();
}

package org.schors.gos.micro;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Data;
import org.schors.gos.micro.model.Person;

import java.util.List;

@Data
@ConfigurationProperties("gosb")
public class BirthdayConfig {

  private List<Person> birthdays;

}

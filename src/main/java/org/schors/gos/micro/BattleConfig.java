package org.schors.gos.micro;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Data;

import java.util.List;

@Data
@ConfigurationProperties("gosb.battle")
public class BattleConfig {
  private List<String> messages;
  private List<String> ends;
}

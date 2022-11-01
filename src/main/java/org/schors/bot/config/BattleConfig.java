package org.schors.bot.config;

import java.util.List;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "gosb.battle")
public interface BattleConfig {
    List<String> messages();
    List<String> ends();
}

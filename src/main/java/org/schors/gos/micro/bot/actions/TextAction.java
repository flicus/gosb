package org.schors.gos.micro.bot.actions;

import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.schors.gos.micro.bot.BotAction;
import org.schors.gos.micro.tg.TgSession;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;


@Slf4j
@Singleton
public class TextAction extends BotAction {

  @Override
  public int order() {
    return 100;
  }

  @Override
  public Boolean match(Update update, TgSession tgSession) {
    return update.hasMessage() && update.getMessage().hasText();
  }

  @Override
  public Mono<? extends BotApiObject> execute(Update update, TgSession tgSession) {
    log.debug("m: " + update.getMessage().getText());
    return Mono.empty();
  }
}

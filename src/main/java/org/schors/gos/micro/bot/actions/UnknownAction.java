package org.schors.gos.micro.bot.actions;

import jakarta.inject.Singleton;
import org.schors.gos.micro.bot.BotAction;
import org.schors.gos.micro.tg.TgSession;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;


@Singleton
public class UnknownAction extends BotAction {

  @Override
  public Boolean match(Update update, TgSession tgSession) {
    return false;
  }

  @Override
  public Mono<? extends BotApiObject> execute(Update update, TgSession tgSession) {
    return reply("Unknown command", update);
  }
}

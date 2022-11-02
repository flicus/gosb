package org.schors.gos.micro.bot.actions;

import jakarta.inject.Singleton;
import org.schors.gos.micro.bot.BotAction;
import org.schors.gos.micro.tg.TgSession;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;


@Singleton
public class BookAction extends BotAction {

  @Override
  public Boolean match(Update update, TgSession tgSession) {
    return update.hasMessage()
      && update.getMessage().hasText()
      && update.getMessage().getText().startsWith("/book");
  }

  @Override
  public Mono<? extends BotApiObject> execute(Update update, TgSession tgSession) {
    return reply("Hello", update);
  }
}
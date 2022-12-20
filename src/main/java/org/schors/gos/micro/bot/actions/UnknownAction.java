package org.schors.gos.micro.bot.actions;

import jakarta.inject.Singleton;
import org.schors.gos.micro.bot.BotAction;
import org.schors.gos.micro.tg.TgSession;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;


@Singleton
public class UnknownAction extends BotAction {

  @Override
  public Boolean match(Update update, TgSession tgSession) {
    return false;
  }

  @Override
  public Mono<Object> execute(Update update, TgSession tgSession) {
    return update.hasMessage() && update.getMessage().getChat().isUserChat()
    ? reply("Unknown command", update)
    : Mono.empty();
  }
}

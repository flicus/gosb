package org.schors.bot.actions;


import javax.enterprise.context.ApplicationScoped;

import org.schors.bot.BotAction;
import org.schors.tg.TgSession;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.objects.Update;

import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class BookAction extends BotAction {

  @Override
  public Boolean match(Update update, TgSession tgSession) {
    return update.hasMessage()
      && update.getMessage().hasText()
      && update.getMessage().getText().startsWith("/book");
  }

  @Override
  public Uni<? extends BotApiObject> execute(Update update, TgSession tgSession) {
    return reply("Hello", update);
  }
}

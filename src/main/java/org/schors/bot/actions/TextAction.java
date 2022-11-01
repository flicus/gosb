package org.schors.bot.actions;

import javax.enterprise.context.ApplicationScoped;

import org.schors.bot.BotAction;
import org.schors.tg.TgSession;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.objects.Update;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
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
  public Uni<? extends BotApiObject> execute(Update update, TgSession tgSession) {
    Log.debug("m: " + update.getMessage().getText());
    return Uni.createFrom().nothing();
  }
}

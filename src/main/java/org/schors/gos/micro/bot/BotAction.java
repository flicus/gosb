package org.schors.gos.micro.bot;

import jakarta.inject.Inject;
import org.schors.gos.micro.tg.TgSender;
import org.schors.gos.micro.tg.TgSession;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;


public abstract class BotAction implements ActionMatcher<Update, TgSession, Boolean>, ActionExecutor<Update, TgSession, Mono<? extends BotApiObject>> {

  @Inject
  protected TgSender sender;

  public int order() {
    return 0;
  }

}

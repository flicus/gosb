package org.schors.gos.micro.bot.actions.event;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.schors.gos.micro.bot.BotAction;
import org.schors.gos.micro.model.Event;
import org.schors.gos.micro.repository.EventRepository;
import org.schors.gos.micro.tg.TgSession;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;

@Singleton
public class EventListAction extends BotAction {

  @Inject
  private EventRepository repository;

  @Override
  public Mono<Message> execute(Update update, TgSession tgSession) {
    return repository.getEvents()
      .map(event -> event.getName())
      .reduce((s, s2) -> s.concat(", ").concat(s2))
      .map(events -> SendMessage.builder()
        .chatId(update.getMessage().getChatId())
        .text(events)
        .build())
      .flatMap(sendMessage -> sender.send(sendMessage));
  }

  @Override
  public Boolean match(Update update, TgSession tgSession) {
    return update.hasMessage()
      && update.getMessage().hasText()
      && update.getMessage().getText().startsWith("/eventList");
  }
}

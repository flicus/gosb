package org.schors.gos.micro.bot.actions.event;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.schors.gos.micro.bot.BotAction;
import org.schors.gos.micro.model.Event;
import org.schors.gos.micro.repository.EventRepository;
import org.schors.gos.micro.tg.TgSession;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;

@Singleton
@Slf4j
public class EventAddAction extends BotAction {

  @Inject
  private EventRepository repository;

  @Override
  public Mono<Message> execute(Update update, TgSession tgSession) {
    log.debug("### execute");
    String[] list = update.getMessage().getText().split(" ");
    if (list.length != 2) return reply("Срань какая то", update);

    Event event = new Event();
    event.setName(list[1]);
    return repository.createEvent(event)
      .map(event1 -> SendMessage.builder()
        .chatId(update.getMessage().getChatId())
        .text("Создал")
        .build())
      .flatMap(sendMessage -> sender.send(sendMessage));
  }

  @Override
  public Boolean match(Update update, TgSession tgSession) {
    return update.hasMessage()
      && update.getMessage().hasText()
      && update.getMessage().getText().startsWith("/eventAdd");
  }
}

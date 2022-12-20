package org.schors.gos.micro.bot.actions.event;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.schors.gos.micro.bot.BotAction;
import org.schors.gos.micro.model.EventRecord;
import org.schors.gos.micro.repository.EventRepository;
import org.schors.gos.micro.tg.TgSession;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;

import java.util.Date;

@Slf4j
@Singleton
public class EventRecordSelectedAction extends BotAction {

  @Inject
  private EventRepository repository;

  @Override
  public Boolean match(Update update, TgSession tgSession) {
    return update.hasCallbackQuery()
      && tgSession.containsKey("event_value");
  }

  @Override
  public Mono<Message> execute(Update update, TgSession tgSession) {
    String value = (String) tgSession.remove("event_value");
    String id = update.getCallbackQuery().getData();
    EventRecord eventRecord = new EventRecord(new Date().toString(), value);
    log.debug("### creating event record: ", eventRecord);
    return replyCallback(update)
      .flatMap(message -> repository.createRecord(id, eventRecord))
      .flatMap((record) -> replyCallback("Добавил", update));
  }
}

package org.schors.bot.actions;

import org.schors.bot.BotAction;
import org.schors.data.EventRecord;
import org.schors.data.EventRepository;
import org.schors.tg.TgSession;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.objects.Update;

import io.smallrye.mutiny.Uni;

import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class EventSelectedAction extends BotAction {

  @Inject
  private EventRepository repository;

  @Override
  public Boolean match(Update update, TgSession tgSession) {
    return update.hasCallbackQuery()
      && tgSession.containsKey("event_value");
  }

  @Override
  public Uni<? extends BotApiObject> execute(Update update, TgSession tgSession) {
    String value = (String) tgSession.remove("event_value");
    String event = update.getCallbackQuery().getData();
    EventRecord eventRecord = new EventRecord(new Date().toString(), value);
    return repository.createRecord(event, eventRecord)
      .log()
      .flatMap((arg0) -> replyCallback("Добавил", update));
  }
}

package org.schors.gos.micro.bot.actions.event;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.schors.gos.micro.bot.BotAction;
import org.schors.gos.micro.model.EventRecord;
import org.schors.gos.micro.repository.EventRepository;
import org.schors.gos.micro.tg.TgSession;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;

import java.util.Date;

@Singleton
public class ShowRecordSelectedAction extends BotAction {

  @Inject
  private EventRepository repository;

  @Override
  public Boolean match(Update update, TgSession tgSession) {
    return update.hasCallbackQuery()
      && tgSession.containsKey("eventRecord");
  }

  @Override
  public Mono<Message> execute(Update update, TgSession tgSession) {
    String value = (String) tgSession.remove("eventRecord");
    String id = update.getCallbackQuery().getData();
    return repository.getRecords(id, 10)
      .map(eventRecord -> eventRecord.getValue())
      .reduce((s, s2) -> s.concat(", ").concat(s2))
      .flatMap(s -> replyCallback(s, update));
  }
}

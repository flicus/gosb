package org.schors.gos.micro.bot.actions.event;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.schors.gos.micro.bot.BotAction;
import org.schors.gos.micro.repository.EventRepository;
import org.schors.gos.micro.tg.TgSession;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import reactor.core.publisher.Mono;

@Singleton
@Slf4j
public class EventRecordAction extends BotAction {

  @Inject
  private EventRepository repository;

  @Override
  public Boolean match(Update update, TgSession tgSession) {
    return update.hasMessage()
        && update.getMessage().hasText()
        && update.getMessage().getText().startsWith("/eventRecord");
  }

  @Override
  public Mono<Message> execute(Update update, TgSession tgSession) {
    log.debug("### execute");
    String[] list = update.getMessage().getText().split(" ");
    if (list.length > 1) {
      tgSession.put("event_value", list[1]);

      return repository.getEvents()
          .map(event -> InlineKeyboardButton.builder().text(event.getName()).callbackData(event.getId()).build())
          .collectList()
          .map(keyboard -> SendMessage.builder()
              .chatId(update.getMessage().getChatId())
              .text("Какой евент?")
              .replyMarkup(InlineKeyboardMarkup.builder().keyboardRow(keyboard).build())
              .build())
          .flatMap(sendMessage -> sender.send(sendMessage));
    } else {
      return sender.send(SendMessage.builder()
          .chatId(String.valueOf(update.getMessage().getChatId()))
          .text("Чо?")
          .build());
    }
  }
}

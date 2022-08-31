package org.schors.gos.micro.bot.actions;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.schors.gos.micro.bot.BotAction;
import org.schors.gos.micro.repository.EventRepository;
import org.schors.gos.micro.tg.TgSession;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import reactor.core.publisher.Mono;

import java.util.List;

@Singleton
public class EventAction extends BotAction {

  @Inject
  private EventRepository repository;

  @Override
  public Boolean match(Update update, TgSession tgSession) {
    return update.hasMessage()
      && update.getMessage().hasText()
      && update.getMessage().getText().startsWith("/event");
  }

  @Override
  public Mono<? extends BotApiObject> execute(Update update, TgSession tgSession) {
    String[] list = update.getMessage().getText().split(" ");
    SendMessage sendMessage;
    if (list.length > 1) {
      tgSession.put("event_value", list[1]);

      List<InlineKeyboardButton> buttons = repository.getEvents()
        .map(event -> InlineKeyboardButton.builder().text(event.getName()).callbackData(event.getId()).build())
        .collectList().block();

      sendMessage = SendMessage.builder()
        .chatId(String.valueOf(update.getMessage().getChatId()))
        .text("Какой евент?")
        .replyMarkup(InlineKeyboardMarkup.builder().keyboardRow(buttons).build())
        .build();
    } else {
      sendMessage = SendMessage.builder()
        .chatId(String.valueOf(update.getMessage().getChatId()))
        .text("Чо?")
        .build();
    }
    return sender.send(sendMessage);
  }
}

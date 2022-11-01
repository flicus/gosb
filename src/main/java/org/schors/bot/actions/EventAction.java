package org.schors.bot.actions;

import org.schors.bot.BotAction;
import org.schors.data.EventRepository;
import org.schors.tg.TgSender;
import org.schors.tg.TgSession;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import io.smallrye.mutiny.Uni;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
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
  public Uni<? extends BotApiObject> execute(Update update, TgSession tgSession) {
    String[] list = update.getMessage().getText().split(" ");
    SendMessage sendMessage;
    if (list.length > 1) {
      tgSession.put("event_value", list[1]);

      List<InlineKeyboardButton> buttons = repository.getEvents()
      .onItem()
      .transform(event -> InlineKeyboardButton.builder().text(event).callbackData(event).build())
      .collect().asList().await().indefinitely();

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
    return sender.sendMessage(sendMessage);
  }
}

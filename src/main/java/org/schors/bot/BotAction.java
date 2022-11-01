package org.schors.bot;

import javax.inject.Inject;

import org.schors.tg.TgSender;
import org.schors.tg.TgSession;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery.AnswerCallbackQueryBuilder;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import io.smallrye.mutiny.Uni;

public abstract class BotAction implements ActionMatcher<Update, TgSession, Boolean>,
    ActionExecutor<Update, TgSession, Uni<? extends BotApiObject>> {

  @Inject
  protected TgSender sender;

  public int order() {
    return 0;
  }

  public Uni<Message> replyCallback(Update update) {
    return replyCallback(null, update);
  }

  public Uni<Message> replyCallback(String text, Update update) {
    AnswerCallbackQueryBuilder res = AnswerCallbackQuery.builder()
        .callbackQueryId(update.getCallbackQuery().getId());
    if (text != null) {
      res.text(text);
    }
    return sender.sendMessage(res.build());
  }

  public Uni<Message> reply(String text, Update update) {
    return sender.sendMessage(SendMessage.builder()
        .text(text)
        .chatId(update.getMessage().getChatId())
        .build());
  }

}

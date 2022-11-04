package org.schors.gos.micro.bot;

import jakarta.inject.Inject;
import org.schors.gos.micro.tg.TgSender;
import org.schors.gos.micro.tg.TgSession;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery.AnswerCallbackQueryBuilder;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;


public abstract class BotAction implements ActionMatcher<Update, TgSession, Boolean>, ActionExecutor<Update, TgSession, Mono<? extends BotApiObject>> {

  @Inject
  protected TgSender sender;

  public int order() {
    return 50;
  }

  public Mono<Message> replyCallback(Update update) {
    return replyCallback(null, update);
  }

  public Mono<Message> replyCallback(String text, Update update) {
    AnswerCallbackQueryBuilder res = AnswerCallbackQuery.builder()
    .callbackQueryId(update.getCallbackQuery().getId());
    if (text != null) {
      res.text(text);
    }
    return sender.send(res.build());
  }

  public Mono<Message> reply(String text, Update update) {
    return sender.send(SendMessage.builder()
    .text(text)
    .chatId(update.getMessage().getChatId())
    .build());
  }

}

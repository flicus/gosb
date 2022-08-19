package org.schors.gos.micro.tg;

import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import reactor.core.publisher.Mono;


@Slf4j
@Singleton
@Requires(property = "gosb.bot.enabled", value = "true")
@Requires(property = "gosb.bot.new", value = "true")
public class TgSender {
  private TgClient tgClient;

  public TgSender(TgClient tgClient) {
    this.tgClient = tgClient;
  }

  public Mono<Message> send(BotApiMethod message) {
    return tgClient.sendMessage(message).map(messageApiResponse -> messageApiResponse.getResult());
  }

  public void edit(SendMessage sendMessage) {

  }
}

package org.schors.gos.micro.tg;

import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodBoolean;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.ApiResponse;
import org.telegram.telegrambots.meta.api.objects.Message;
import reactor.core.publisher.Mono;

import java.io.Serializable;


@Slf4j
@Singleton
@Requires(property = "gosb.bot.enabled", value = "true")
@Requires(property = "gosb.bot.new", value = "true")
public class TgSender {
  private final TgClient tgClient;

  public TgSender(TgClient tgClient) {
    this.tgClient = tgClient;
  }

  public <T extends Serializable> Mono<T> send(BotApiMethod<T> message) {
    log.debug("###> {}", message);
    return tgClient
      .sendMessage(message)
      .doOnEach(apiResponseSignal -> {
        log.debug("#< {}", apiResponseSignal.get());
      })
      .map(ApiResponse::getResult);
  }

  public Mono<Boolean> delete(BotApiMethod message) {
    return tgClient.deleteMessage(message).map(ApiResponse::getResult);
  }

  public void edit(SendMessage sendMessage) {

  }
}

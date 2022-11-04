package org.schors.gos.micro.tg;

import io.micronaut.context.annotation.Value;
import io.micronaut.core.type.Argument;
import io.micronaut.core.type.GenericArgument;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.GetUpdates;
import org.telegram.telegrambots.meta.api.objects.ApiResponse;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;

import static io.micronaut.http.HttpRequest.POST;

@Singleton
public class TgClient {

  @Client("telegram")
  @Inject
  HttpClient client;

  @Value("${gosb.bot.token}")
  String token;

  Mono<ApiResponse<ArrayList<Update>>> getUpdates(GetUpdates getUpdates) {
    return Mono.from(client
    .retrieve(POST(token + "/getupdates", getUpdates), new GenericArgument<ApiResponse<ArrayList<Update>>>() {}));
  }


  Mono<ApiResponse<Message>> sendMessage(BotApiMethod sendMessage) {
    return Mono.from(client
    .retrieve(POST(token + "/sendmessage", sendMessage), new GenericArgument<ApiResponse<Message>>(){}));
  }
}

package org.schors.gos.micro.tg;

import io.micronaut.core.async.annotation.SingleResult;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.GetUpdates;
import org.telegram.telegrambots.meta.api.objects.ApiResponse;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Client("telegram")
public interface TgClient {

  @SingleResult
  @Post("getupdates")
  Mono<ApiResponse<ArrayList<Update>>> getUpdates(@Body GetUpdates getUpdates);

  @SingleResult
  @Post("sendmessage")
  Mono<ApiResponse<Message>> sendMessage(@Body BotApiMethod sendMessage);

  @SingleResult
  @Post("deletemessage")
  Mono<ApiResponse<Boolean>> deleteMessage(@Body BotApiMethod deleteMessage);
}
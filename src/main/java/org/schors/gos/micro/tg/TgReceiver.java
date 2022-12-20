package org.schors.gos.micro.tg;

import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.updates.GetUpdates;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
@Singleton
@Requires(property = "gosb.bot.enabled", value = "true")
@Requires(property = "gosb.bot.new", value = "true")
public class TgReceiver {

  private int lastReceivedUpdate = 0;
  private Consumer<Update> consumer;
  private TgClient tgClient;
  private Scheduler httpExecutor = Schedulers.newSingle("receiver::http");
//  private Scheduler actionExecutors = Schedulers.newBoundedElastic(10, 20, "receiver::actions");

  public TgReceiver(TgClient tgClient) {
    this.tgClient = tgClient;
  }

  public void subscribe(Consumer<Update> consumer) {
    this.consumer = consumer;
    httpExecutor.schedule(new Worker());
  }

  private class Worker implements Runnable {

    @Override
    public void run() {
      log.debug("get: " + new Date());
      Optional.ofNullable(consumer)
        .ifPresent(updateConsumer -> {
          GetUpdates request = GetUpdates.builder().limit(100).timeout(50).offset(lastReceivedUpdate + 1).build();
          tgClient.getUpdates(request)
            .log()
            .flatMapMany(arrayListApiResponse -> Flux.fromIterable(arrayListApiResponse.getResult()))
            .filter(update -> lastReceivedUpdate < update.getUpdateId())
            .doOnNext(update -> {
              if (lastReceivedUpdate < update.getUpdateId()) {
                lastReceivedUpdate = update.getUpdateId();
              }
            })
            .doFinally(signalType -> httpExecutor.schedule(new Worker(), 500, TimeUnit.MILLISECONDS))
//            .publishOn(actionExecutors)
            .subscribe(updateConsumer, throwable -> log.error("!: " + throwable.getMessage()));
        });
    }
  }
}

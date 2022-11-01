package org.schors.tg;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.telegram.telegrambots.meta.api.methods.updates.GetUpdates;
import org.telegram.telegrambots.meta.api.objects.Update;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;

@ApplicationScoped
public class TgReceiver {

    private int lastReceivedUpdate = 0;
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private Consumer<Update> consumer;

    @RestClient
    private TgClient tgClient;

    public void subscribe(Consumer<Update> consumer) {
        this.consumer = consumer;
        scheduler.execute(new Worker());
    }

    private class Worker implements Runnable {

        @Override
        public void run() {
            Log.debug("onRun: " + lastReceivedUpdate);
            Optional.ofNullable(consumer)
                    .ifPresent(consumer -> {
                        GetUpdates request = GetUpdates
                                .builder()
                                .limit(100)
                                .timeout(50)
                                .offset(lastReceivedUpdate + 1)
                                .build();
                        tgClient
                                .getUpdates(request)
                                .onItem()
                                .transformToMulti(apiResponse -> Multi
                                        .createFrom()
                                        .items(apiResponse.getResult().stream()))
                                .select()
                                .where(update -> lastReceivedUpdate < update.getUpdateId())
                                .invoke(update -> {
                                    if (lastReceivedUpdate < update.getUpdateId()) {
                                        lastReceivedUpdate = update.getUpdateId();
                                    }
                                })
                                .onFailure().invoke(failure -> {
                                    Log.error("!: " + failure.getMessage());
                                    scheduler.schedule(new Worker(), 500, TimeUnit.MILLISECONDS);
                                })
                                .onCompletion().invoke(() -> {
                                    Log.debug("onComplete: " + lastReceivedUpdate);
                                    scheduler.schedule(new Worker(), 500, TimeUnit.MILLISECONDS);
                                })
                                .subscribe()
                                .with(consumer);
                    });

        }
    }
}

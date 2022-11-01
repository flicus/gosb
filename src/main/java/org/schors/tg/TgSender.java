package org.schors.tg;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class TgSender {

    @RestClient
    private TgClient tgClient;

    public Uni<Message> sendMessage(BotApiMethod method) {
        return tgClient.sendMessage(method).map(apiResponse -> apiResponse.getResult());
    }


    
}

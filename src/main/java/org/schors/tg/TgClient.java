package org.schors.tg;

import java.util.ArrayList;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.GetUpdates;
import org.telegram.telegrambots.meta.api.objects.ApiResponse;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import io.smallrye.mutiny.Uni;

@RegisterRestClient(configKey = "telegram-client")
public interface TgClient {

    @POST
    @Path("getupdates")
    Uni<ApiResponse<ArrayList<Update>>> getUpdates(GetUpdates getUpdates);

    @POST
    @Path("sendmessage")
    Uni<ApiResponse<Message>> sendMessage(BotApiMethod sendMessage);
    
}

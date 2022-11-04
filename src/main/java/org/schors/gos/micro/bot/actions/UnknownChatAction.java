package org.schors.gos.micro.bot.actions;

import org.schors.gos.micro.bot.BotAction;
import org.schors.gos.micro.tg.TgSession;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.methods.groupadministration.LeaveChat;
import org.telegram.telegrambots.meta.api.objects.Update;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Singleton
public class UnknownChatAction extends BotAction {

    @Value("${gosb.bot.groupId}")
    private Long chatId;

    @Override
    public int order() {
        return 0;
    }

    @Override
    public Boolean match(Update update, TgSession session) {
        return update.hasMessage() 
        && !update.getMessage().getChat().isUserChat()
        && update.getMessage().getChatId() != chatId;
    }

    @Override
    public Mono<? extends BotApiObject> execute(Update update, TgSession session) {

        log.warn(String.format("!! unwanted message: %s, type: %s, name: %s, who: 's%' s% s%", 
        update.getMessage().getText(),
        update.getMessage().getChat().getType(),
        update.getMessage().getChat().getUserName(),
        update.getMessage().getFrom().getUserName(),
        update.getMessage().getFrom().getFirstName(),
        update.getMessage().getFrom().getLastName()));

        return sender.send(LeaveChat.builder().chatId(update.getMessage().getChatId()).build());
    }
    
}

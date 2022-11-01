package org.schors.tg;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

@ApplicationScoped
public class TgSessionManager {

    private Cache<String, TgSession> sessions = CacheBuilder.newBuilder()
    .maximumSize(1000)
    .expireAfterAccess(8, TimeUnit.HOURS)
    .build();

    public TgSession getSession(Update update) {
        String sessionId = sessionId(update);
        TgSession session = sessions.getIfPresent(sessionId);
        if (session == null) {
          session = new TgSession();
          sessions.put(sessionId, session);
        }
        return session;
      }
    
      private String sessionId(final Update update) {
        Long chatId = Optional.ofNullable(update.getMessage())
          .map(Message::getChatId)
          .orElseGet(() -> update.getCallbackQuery().getMessage().getChatId());
        User user = Optional.ofNullable(update.getMessage())
          .map(Message::getFrom)
          .orElseGet(() -> update.getCallbackQuery().getFrom());
        return "" + chatId + "|" + user.getId();
      }
 
}
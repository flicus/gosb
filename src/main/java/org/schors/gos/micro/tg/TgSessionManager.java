package org.schors.gos.micro.tg;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Singleton
public class TgSessionManager {

  private Cache<String, TgSession> sessions = CacheBuilder.newBuilder()
    .maximumSize(1000)
    .expireAfterAccess(8, TimeUnit.HOURS)
    .build();

  public Optional<TgSession> getSession(Update update) {
    return sessionId(update)
      .map(sessionId -> {
        TgSession session = sessions.getIfPresent(sessionId);
        if (session == null) {
          session = new TgSession();
          sessions.put(sessionId, session);
        }
        return session;
      });
  }

  private Optional<String> sessionId(final Update update) {
    return Arrays.stream(UpdateType.values())
      .filter(updateType -> updateType.test(update))
      .findAny()
      .map(updateType -> String.format("%d|%d", updateType.getChatId(update), updateType.getUser(update).getId()));
  }

  private Optional<User> extractUser(final Update update) {
    return Arrays.stream(UpdateType.values())
      .filter(updateType -> updateType.test(update))
      .findAny()
      .map(updateType -> updateType.getUser(update));
  }

  private Optional<Long> extractChatId(final Update update) {
    return Arrays.stream(UpdateType.values())
      .filter(updateType -> updateType.test(update))
      .findAny()
      .map(updateType -> updateType.getChatId(update));
  }

}

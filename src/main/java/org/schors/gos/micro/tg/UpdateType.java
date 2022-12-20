package org.schors.gos.micro.tg;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.function.Function;
import java.util.function.Predicate;

public enum UpdateType implements Predicate<Update> {
//  ALL(update -> true),
  MESSAGE(Update::hasMessage, update -> update.getMessage().getFrom(), update -> update.getMessage().getChatId()),
  EDITED_MESSAGE(Update::hasEditedMessage, update -> update.getEditedMessage().getFrom(), update ->  update.getEditedMessage().getChatId()),
  CHANNEL_POST(Update::hasChannelPost, update -> update.getChannelPost().getFrom(), update -> update.getChannelPost().getChatId()),
  EDITED_CHANNEL_POST(Update::hasEditedChannelPost, update -> update.getEditedChannelPost().getFrom(), update -> update.getEditedChannelPost().getChatId()),
  INLINE_QUERY(Update::hasInlineQuery, update -> update.getInlineQuery().getFrom(), update -> null),
  CHOSEN_INLINE_RESULT(Update::hasChosenInlineQuery, update -> update.getChosenInlineQuery().getFrom(), update -> null),
  CALLBACK_QUERY(Update::hasCallbackQuery, update -> update.getCallbackQuery().getFrom(), update -> update.getCallbackQuery().getMessage().getChatId()),
  SHIPPING_QUERY(Update::hasShippingQuery, update -> update.getShippingQuery().getFrom(), update -> null),
  PRE_CHECKOUT_QUERY(Update::hasPreCheckoutQuery, update -> update.getPreCheckoutQuery().getFrom(), update -> null),
  POLL(Update::hasPoll, update -> null, update -> null),
  POLL_ANSWER(Update::hasPollAnswer, update -> update.getPollAnswer().getUser(), update -> null),
  MY_CHAT_MEMBER(Update::hasMyChatMember, update -> update.getMyChatMember().getFrom(), update -> update.getMyChatMember().getChat().getId()),
  CHAT_MEMBER(Update::hasChatMember, update -> update.getChatMember().getFrom(), update -> update.getChatMember().getChat().getId());

  private final Predicate<Update> predicate;
  private final Function<Update, User> userExtractor;
  private final Function<Update, Long> chatIdExtractor;

  UpdateType(final Predicate<Update> predicate, Function<Update, User> userExtractor, Function<Update, Long> chatIdExtractor) {
    this.predicate = predicate;
    this.userExtractor = userExtractor;
    this.chatIdExtractor = chatIdExtractor;
  }

  @Override
  public boolean test(final Update update) {
    return this.predicate.test(update);
  }

  public User getUser(final Update update) {
    return this.userExtractor.apply(update);
  }

  public Long getChatId(final Update update) {
    return this.chatIdExtractor.apply(update);
  }
}

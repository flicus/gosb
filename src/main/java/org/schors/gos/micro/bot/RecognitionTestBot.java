package org.schors.gos.micro.bot;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.schors.gos.micro.BattleConfig;
import org.schors.gos.micro.BirthdayConfig;
import org.schors.gos.micro.model.Player;
import org.schors.gos.micro.model.PlayerLayout;
import org.schors.gos.micro.notifier.DeleteMessageJob;
import org.schors.gos.micro.notifier.SendBirthdayJob;
import org.schors.gos.micro.notifier.SendMessageJob;
import org.schors.gos.micro.repository.BattleRepositoryDbImpl;
import org.schors.gos.micro.repository.PlayerRepositoryDbImpl;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.quartz.DateBuilder.futureDate;

@Slf4j
@Singleton
@Requires(property = "gosb.bot.enabled", value = "true")
@Requires(property = "gosb.bot.new", value = "false")
public class RecognitionTestBot extends TelegramLongPollingBot {

  private final PlayerRepositoryDbImpl playerRepository;
  private final BattleRepositoryDbImpl battleRepository;
  private PlayerLayout playerLayout = null;
  private List<Player> players = null;
  private Scheduler scheduler;
  private BattleConfig battleConfig;
  private BirthdayConfig birthdayConfig;

  @Value("${gosb.bot.name}")
  private String botName;

  @Value("${gosb.bot.token}")
  private String botToken;

  public RecognitionTestBot(PlayerRepositoryDbImpl playerRepository, BattleRepositoryDbImpl battleRepository,
                            BattleConfig battleConfig, BirthdayConfig birthdayConfig) {
    this.playerRepository = playerRepository;
    this.battleRepository = battleRepository;
    this.battleConfig = battleConfig;
    this.birthdayConfig = birthdayConfig;
  }

  @SneakyThrows
  @EventListener
  public void onStartupEvent(StartupEvent event) {
    TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
    api.registerBot(this);

    scheduler = StdSchedulerFactory.getDefaultScheduler();
    scheduler.start();

    JobDetail job = JobBuilder.newJob(SendMessageJob.class).withIdentity("battleNotify").build();
    job.getJobDataMap().put("msg", battleConfig.getMessages());
    job.getJobDataMap().put("bot", this);

    JobDetail endBattle = JobBuilder.newJob(SendMessageJob.class).withIdentity("endBattleNotify").build();
    endBattle.getJobDataMap().put("msg", battleConfig.getEnds());
    endBattle.getJobDataMap().put("bot", this);

    JobDetail birthday = JobBuilder.newJob(SendBirthdayJob.class).withIdentity("birtday").build();
    birthday.getJobDataMap().put("bot", this);

    Trigger trigger12 = TriggerBuilder
      .newTrigger()
      .startNow()
      .withIdentity("battle12")
      .withSchedule(DailyTimeIntervalScheduleBuilder
        .dailyTimeIntervalSchedule()
        .onDaysOfTheWeek(DateBuilder.TUESDAY,
          DateBuilder.WEDNESDAY,
          DateBuilder.FRIDAY,
          DateBuilder.SUNDAY)
        .startingDailyAt(TimeOfDay
          .hourAndMinuteOfDay(
            12,
            0))
        // .withInterval(3,
        // DateBuilder.IntervalUnit.HOUR)
        .withRepeatCount(0))
      .forJob(job)
      .build();

    Trigger trigger15 = TriggerBuilder
      .newTrigger()
      .startNow()
      .withIdentity("battle15")
      .withSchedule(DailyTimeIntervalScheduleBuilder
        .dailyTimeIntervalSchedule()
        .onDaysOfTheWeek(DateBuilder.TUESDAY,
          DateBuilder.WEDNESDAY,
          DateBuilder.FRIDAY,
          DateBuilder.SUNDAY)
        .startingDailyAt(TimeOfDay
          .hourAndMinuteOfDay(
            15,
            0))
        // .withInterval(3,
        // DateBuilder.IntervalUnit.HOUR)
        .withRepeatCount(0))
      .forJob(job)
      .build();

    Trigger trigger18 = TriggerBuilder
      .newTrigger()
      .startNow()
      .withIdentity("battle18")
      .withSchedule(DailyTimeIntervalScheduleBuilder
        .dailyTimeIntervalSchedule()
        .onDaysOfTheWeek(DateBuilder.TUESDAY,
          DateBuilder.WEDNESDAY,
          DateBuilder.FRIDAY,
          DateBuilder.SUNDAY)
        .startingDailyAt(TimeOfDay
          .hourAndMinuteOfDay(
            18,
            0))
        // .withInterval(3,
        // DateBuilder.IntervalUnit.HOUR)
        .withRepeatCount(0))
      .forJob(job)
      .build();

    Trigger trigger21 = TriggerBuilder
      .newTrigger()
      .startNow()
      .withIdentity("battle21")
      .withSchedule(DailyTimeIntervalScheduleBuilder
        .dailyTimeIntervalSchedule()
        .onDaysOfTheWeek(DateBuilder.TUESDAY,
          DateBuilder.WEDNESDAY,
          DateBuilder.FRIDAY,
          DateBuilder.SUNDAY)
        .startingDailyAt(TimeOfDay
          .hourAndMinuteOfDay(
            21,
            0))
        // .withInterval(3,
        // DateBuilder.IntervalUnit.HOUR)
        .withRepeatCount(0))
      .forJob(job)
      .build();

    Trigger endTrigger = TriggerBuilder
      .newTrigger()
      .startNow()
      .withIdentity("endBattle")
      .withSchedule(DailyTimeIntervalScheduleBuilder
        .dailyTimeIntervalSchedule()
        .onDaysOfTheWeek(DateBuilder.TUESDAY,
          DateBuilder.WEDNESDAY,
          DateBuilder.FRIDAY,
          DateBuilder.SUNDAY)
        .startingDailyAt(TimeOfDay
          .hourAndMinuteOfDay(
            21,
            55))
        .withRepeatCount(0))
      .forJob(endBattle)
      .build();

    scheduler.scheduleJob(job, Set.of(trigger12, trigger15, trigger18, trigger21), true);
    scheduler.scheduleJob(endBattle, endTrigger);

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    Set<Trigger> birthdays = birthdayConfig.getBirthdays().stream()
      .map(bdth -> {
        try {
          Date date = dateFormat.parse(bdth.getDate());
          date.setHours(8);
          date.setMinutes(0);
          return TriggerBuilder
            .newTrigger()
            .usingJobData("name", bdth.getName())
            .withIdentity("birthday")
            .startAt(date)
            .forJob(birthday)
            .build();
        } catch (ParseException e) {
          log.error(e.getMessage(), e);
          return null;
        }
      })
      .filter(trg -> trg != null)
      .collect(Collectors.toSet());

    scheduler.scheduleJob(birthday, birthdays, true);
  }

  @SneakyThrows
  public void scheduleDeleteMessage(Integer messageId) {
    JobDetail job = JobBuilder.newJob(DeleteMessageJob.class).withIdentity("deleteNotify" + messageId).build();
    job.getJobDataMap().put("msgId", messageId);
    job.getJobDataMap().put("bot", this);
    Trigger deleteTrigger = TriggerBuilder
      .newTrigger()
      .startAt(futureDate(1, DateBuilder.IntervalUnit.HOUR))
      // .startNow()
      .withIdentity("deleteNotify" + messageId)
      .forJob(job)
      // .withSchedule(SimpleScheduleBuilder.repeatMinutelyForTotalCount(0))
      .build();
    scheduler.scheduleJob(job, deleteTrigger);
  }

  public static <T> Stream<Stream<T>> getTuples(Collection<T> items, int size) {
    int page = 0;
    Stream.Builder<Stream<T>> builder = Stream.builder();
    Stream<T> stream;
    do {
      stream = items.stream().skip(size * page++).limit(size);
      builder.add(stream);
    } while (items.size() - size * page > 0);
    return builder.build();
  }

  @Override
  public String getBotUsername() {
    return botName;
  }

  @Override
  public String getBotToken() {
    return botToken;
  }
  // -417220779

  @Override
  public void onUpdateReceived(Update update) {
    log.debug(update.toString());
    if (update.hasMessage() && !update.getMessage().isGroupMessage()) {
      if (update.getMessage().hasPhoto()) {
        PhotoSize photo = update.getMessage().getPhoto().stream().max(Comparator.comparing(PhotoSize::getFileSize))
          .get();
        String url = null;
        if (photo.getFilePath() != null) { // If the file_path is already present, we are done!
          url = photo.getFilePath();
        } else { // If not, let find it
          // We create a GetFile method and set the file_id from the photo
          GetFile getFileMethod = new GetFile();
          getFileMethod.setFileId(photo.getFileId());
          try {
            // We execute the method using AbsSender::execute method.
            org.telegram.telegrambots.meta.api.objects.File file = execute(getFileMethod);
            // We now have the file_path
            url = file.getFilePath();
          } catch (TelegramApiException e) {
            e.printStackTrace();
          }
        }
        try {
          File file = this.downloadFile(url);
          playerLayout = Recognition.recognize(file);
          execute(SendMessage.builder()
            .chatId(String.valueOf(update.getMessage().getChatId()))
            .text(playerLayout.readable()).build());
          InlineKeyboardMarkup.InlineKeyboardMarkupBuilder inlineKeyboardMarkupBuilder = InlineKeyboardMarkup.builder();
          playerRepository
            .getAllPlayers()
            .window(3)
            .doOnNext(playerFlux -> inlineKeyboardMarkupBuilder.keyboardRow(playerFlux
              .map(player -> InlineKeyboardButton.builder()
                .text(player.getName())
                .callbackData(player.getId())
                .build())
              .collectList()
              .block()));
          execute(
            SendMessage.builder()
              .chatId(String.valueOf(update.getMessage().getChatId()))
              .text("Чья расстановка?")
              .replyMarkup(inlineKeyboardMarkupBuilder.build())
              .build());

        } catch (TelegramApiException e) {
          e.printStackTrace();
        }
      } else if (update.hasCallbackQuery()) {
        try {
          execute(AnswerCallbackQuery.builder()
            .callbackQueryId(update.getCallbackQuery().getId())
            .build());
        } catch (TelegramApiException e) {
          e.printStackTrace();
        }
        if (playerLayout != null) {
          String id = update.getCallbackQuery().getData();
          playerLayout.setPlayer(players.stream().filter(player -> player.getId().equals(id)).findAny().get());
          battleRepository.addPlayerLayout(playerLayout);
          playerLayout = null;
          players = null;
        }
        // try {
        // execute(SendMessage.builder().chatId(String.valueOf(update.getCallbackQuery().getChatId())).text("Добавлено").build());
        // } catch (TelegramApiException e) {
        // e.printStackTrace();
        // }
      } else {
        try {
          execute(SendMessage.builder()
            .chatId(String.valueOf(update.getMessage().getChatId()))
            .text("Изображение не найдено")
            .build());
        } catch (TelegramApiException e) {
          e.printStackTrace();
        }
      }
    }
  }
}

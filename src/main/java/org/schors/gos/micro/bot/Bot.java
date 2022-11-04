package org.schors.gos.micro.bot;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.schors.gos.micro.BattleConfig;
import org.schors.gos.micro.BirthdayConfig;
import org.schors.gos.micro.bot.actions.UnknownAction;
import org.schors.gos.micro.notifier.SendBirthdayJob;
import org.schors.gos.micro.notifier.SendMessageJob;
import org.schors.gos.micro.tg.TgReceiver;
import org.schors.gos.micro.tg.TgSender;
import org.schors.gos.micro.tg.TgSession;
import org.schors.gos.micro.tg.TgSessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Singleton
@Requires(property = "gosb.bot.enabled", value = "true")
@Requires(property = "gosb.bot.new", value = "true")
public class Bot {

  @Inject
  private Scheduler scheduler;
  @Inject
  private TgSessionManager sessionManager;
  @Inject
  private TgReceiver receiver;
  @Inject
  private TgSender sender;
  @Inject
  private List<BotAction> actions;
  @Inject
  private BattleConfig battleConfig;
  @Inject
  private BirthdayConfig birthdayConfig;
  @Inject
  private UnknownAction defaultAction;

  @Value("${gosb.bot.groupId}")
  private Long chatId;

  @SneakyThrows
  @EventListener
  public void onStartupEvent(StartupEvent event) {

    startScheduledTasks();

    receiver
        .subscribe(update -> {
          TgSession session = sessionManager.getSession(update);
          actions.stream()
              .filter(botAction -> botAction.match(update, session))
              .min((o1, o2) -> o1.order() > o2.order() ? 1 : -1)
              .orElse(defaultAction)
              .execute(update, session)
              .log()
              .subscribe();
        });
  }

  private void startScheduledTasks() throws SchedulerException {

    JobDetail job = JobBuilder.newJob(SendMessageJob.class).withIdentity("battleNotify").build();
    job.getJobDataMap().put("msg", battleConfig.getMessages());
    job.getJobDataMap().put("chatId", chatId);
    job.getJobDataMap().put("sender", sender);
    job.getJobDataMap().put("executor", scheduler);

    JobDetail endBattle = JobBuilder.newJob(SendMessageJob.class).withIdentity("endBattleNotify").build();
    endBattle.getJobDataMap().put("msg", battleConfig.getEnds());
    endBattle.getJobDataMap().put("chatId", chatId);
    endBattle.getJobDataMap().put("sender", sender);
    endBattle.getJobDataMap().put("executor", scheduler);

    JobDetail birthday = JobBuilder.newJob(SendBirthdayJob.class).withIdentity("birtday").build();
    birthday.getJobDataMap().put("chatId", chatId);
    birthday.getJobDataMap().put("sender", sender);
    birthday.getJobDataMap().put("executor", scheduler);

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
            .startingDailyAt(TimeOfDay.hourAndMinuteOfDay(12, 0))
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
            .startingDailyAt(TimeOfDay.hourAndMinuteOfDay(15, 0))
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
            .startingDailyAt(TimeOfDay.hourAndMinuteOfDay(18, 0))
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
            .startingDailyAt(TimeOfDay.hourAndMinuteOfDay(21, 0))
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
            .startingDailyAt(TimeOfDay.hourAndMinuteOfDay(21, 55))
            .withRepeatCount(0))
        .forJob(endBattle)
        .build();

    scheduler.scheduleJob(job, Set.of(trigger12, trigger15, trigger18, trigger21), true);
    scheduler.scheduleJob(endBattle, endTrigger);

    Trigger bdTrigger = TriggerBuilder
        .newTrigger()
        .startNow()
        .withIdentity("birthday")
        .withSchedule(DailyTimeIntervalScheduleBuilder
            .dailyTimeIntervalSchedule()
            .startingDailyAt(TimeOfDay.hourAndMinuteOfDay(7, 0))
            .withRepeatCount(0))
        .forJob(birthday)
        .build();

    scheduler.scheduleJob(birthday, bdTrigger);

  }
}

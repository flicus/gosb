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
import org.schors.gos.micro.notifier.NotifyJob;
import org.schors.gos.micro.notifier.RandomMessageJob;
import org.schors.gos.micro.repository.PersonRepository;
import org.schors.gos.micro.tg.TgReceiver;
import org.schors.gos.micro.tg.TgSender;
import org.schors.gos.micro.tg.TgSession;
import org.schors.gos.micro.tg.TgSessionManager;

import java.util.List;
import java.util.Set;

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
  @Inject
  private PersonRepository personRepository;

  @Value("${gosb.bot.writeGroups}")
  private Set<Long> writeGroups;

  @SneakyThrows
  @EventListener
  public void onStartupEvent(StartupEvent event) {

    startScheduledTasks();

    receiver
        .subscribe(update -> {
          log.debug(update.toString());
          TgSession session = sessionManager.getSession(update);
          actions.stream()
              .filter(botAction -> botAction.match(update, session))
              .min((o1, o2) -> o1.order() > o2.order() ? 1 : -1)
              .orElse(defaultAction)
              .execute(update, session)
            //   .log()
              .subscribe();
        });
  }

  private void startScheduledTasks() throws SchedulerException {

    JobDetail messageJob = JobBuilder.newJob(NotifyJob.class).withIdentity("registrationNotify").build();
    messageJob.getJobDataMap().put("msg", "Регистрация в битву!");
    messageJob.getJobDataMap().put("chatId", writeGroups);
    messageJob.getJobDataMap().put("sender", sender);
    messageJob.getJobDataMap().put("executor", scheduler);

    JobDetail randomMessageJob = JobBuilder.newJob(RandomMessageJob.class).withIdentity("battleNotify").build();
    randomMessageJob.getJobDataMap().put("msg", battleConfig.getMessages());
    randomMessageJob.getJobDataMap().put("chatId", writeGroups);
    randomMessageJob.getJobDataMap().put("sender", sender);
    randomMessageJob.getJobDataMap().put("executor", scheduler);

    JobDetail endBattle = JobBuilder.newJob(RandomMessageJob.class).withIdentity("endBattleNotify").build();
    endBattle.getJobDataMap().put("msg", battleConfig.getEnds());
    endBattle.getJobDataMap().put("chatId", writeGroups);
    endBattle.getJobDataMap().put("sender", sender);
    endBattle.getJobDataMap().put("executor", scheduler);

    JobDetail birthday = JobBuilder.newJob(SendBirthdayJob.class).withIdentity("birtday").build();
    birthday.getJobDataMap().put("chatId", writeGroups);
    birthday.getJobDataMap().put("sender", sender);
    birthday.getJobDataMap().put("executor", scheduler);
    birthday.getJobDataMap().put("persons", personRepository);
    birthday.getJobDataMap().put("bdc", birthdayConfig);

    //gmt
    Trigger registration = TriggerBuilder
        .newTrigger()
        .startNow()
        .withIdentity("registration1")
        .withSchedule(DailyTimeIntervalScheduleBuilder
            .dailyTimeIntervalSchedule()
            .onDaysOfTheWeek(DateBuilder.MONDAY,
                DateBuilder.TUESDAY)
            .startingDailyAt(TimeOfDay.hourAndMinuteOfDay(9, 0))
            .withRepeatCount(0))
        .forJob(messageJob)
        .build();

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
            .startingDailyAt(TimeOfDay.hourAndMinuteOfDay(10, 0))
            .withRepeatCount(0))
        .forJob(randomMessageJob)
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
            .startingDailyAt(TimeOfDay.hourAndMinuteOfDay(13, 0))
            .withRepeatCount(0))
        .forJob(randomMessageJob)
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
            .startingDailyAt(TimeOfDay.hourAndMinuteOfDay(16, 0))
            .withRepeatCount(0))
        .forJob(randomMessageJob)
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
            .startingDailyAt(TimeOfDay.hourAndMinuteOfDay(19, 0))
            .withRepeatCount(0))
        .forJob(randomMessageJob)
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
            .startingDailyAt(TimeOfDay.hourAndMinuteOfDay(19, 55))
            .withRepeatCount(0))
        .forJob(endBattle)
        .build();

    scheduler.scheduleJob(messageJob, registration);
    scheduler.scheduleJob(randomMessageJob, Set.of(trigger12, trigger15, trigger18, trigger21), true);
    scheduler.scheduleJob(endBattle, endTrigger);

    Trigger bdTrigger = TriggerBuilder
        .newTrigger()
        .startNow()
        .withIdentity("birthday")
        .withSchedule(DailyTimeIntervalScheduleBuilder
            .dailyTimeIntervalSchedule()
            .startingDailyAt(TimeOfDay.hourAndMinuteOfDay(5, 0))
            .withRepeatCount(0))
        .forJob(birthday)
        .build();

    scheduler.scheduleJob(birthday, bdTrigger);

  }
}

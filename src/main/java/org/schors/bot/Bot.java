package org.schors.bot;

import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.quartz.DailyTimeIntervalScheduleBuilder;
import org.quartz.DateBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TimeOfDay;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.schors.bot.actions.UnknownAction;
import org.schors.bot.config.BattleConfig;
import org.schors.bot.config.BirthdayConfig;
import org.schors.notifier.SendBirthdayJob;
import org.schors.notifier.SendMessageJob;
import org.schors.tg.TgReceiver;
import org.schors.tg.TgSender;
import org.schors.tg.TgSession;
import org.schors.tg.TgSessionManager;

import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class Bot {

    @Inject
    private Scheduler scheduler;

    @Inject
    private TgReceiver receiver;

    @Inject TgSender sender;

    @Inject
    private TgSessionManager sessionManager;

    @Inject
    private Instance<BotAction> actions;

    @Inject
    private UnknownAction defaultAction;

    @ConfigProperty(name = "gosb.bot.groupId")
    private String chatId;

    @Inject
    private BattleConfig battleConfig;

    // @Inject 
    // private BirthdayConfig birthdayConfig;

    void onStart(@Observes StartupEvent ev) {
        Log.debug("### Start");

        try {
            startScheduledTasks();
        } catch(Exception e) {
            Log.error(e.getMessage(), e);
        }

        receiver.subscribe(update -> {
            Log.debug("### onUpdate: " + update.getUpdateId());
            TgSession session = sessionManager.getSession(update);
            actions.stream()
                    .filter(action -> action.match(update, session))
                    .sorted((o1, o2) -> o1.order() > o2.order() ? 1 : -1)
                    .findFirst()
                    .orElse(defaultAction)
                    .execute(update, session)
                    .log()
                    .subscribe();
        });

    }

    private void startScheduledTasks() throws SchedulerException {

        JobDetail job = JobBuilder.newJob(SendMessageJob.class).withIdentity("battleNotify").build();
        job.getJobDataMap().put("msg", battleConfig.messages());
        job.getJobDataMap().put("chatId", chatId);
        job.getJobDataMap().put("sender", sender);
        job.getJobDataMap().put("executor", scheduler);
    
        JobDetail endBattle = JobBuilder.newJob(SendMessageJob.class).withIdentity("endBattleNotify").build();
        endBattle.getJobDataMap().put("msg", battleConfig.ends());
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
        
      }
}

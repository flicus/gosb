package org.schors.gos.micro.notifier;

import lombok.SneakyThrows;
import org.quartz.DailyTimeIntervalScheduleBuilder;
import org.quartz.DateBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.TimeOfDay;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BattleNotifier {

  private final Scheduler scheduler;

  @Autowired
  private BattleScheduler battleScheduler;

  @SneakyThrows
  public BattleNotifier() {
    scheduler = StdSchedulerFactory.getDefaultScheduler();
    scheduler.start();

    JobDetail job = JobBuilder.newJob(NotifyJob.class).withIdentity("battleNotify").build();
    job.getJobDataMap().put("msg", "Битва! Ура, бежим бить!");

    JobDetail endBattle = JobBuilder.newJob(NotifyJob.class).withIdentity("endBattleNotify").build();
    job.getJobDataMap().put("msg", "Сливаем допы!");

    Trigger trigger = TriggerBuilder
      .newTrigger()
      .startNow()
      .withIdentity("battle")
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
                      .withInterval(3,
                                    DateBuilder.IntervalUnit.HOUR))
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
                      .withRepeatCount(1))
      .forJob(endBattle)
      .build();

    scheduler.scheduleJob(job, trigger);
    scheduler.scheduleJob(endBattle, endTrigger);

  }


}

package org.schors.gos.micro.notifier;

import lombok.SneakyThrows;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class BattleNotifier {

  private final Scheduler scheduler;

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

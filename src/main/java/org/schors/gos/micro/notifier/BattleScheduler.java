package org.schors.gos.micro.notifier;

import org.quartz.*;

public class BattleScheduler {


  public ScheduleBuilder<CronTrigger> weekly() {
    return CronScheduleBuilder.atHourAndMinuteOnGivenDaysOfWeek(12,
      0,
      DateBuilder.TUESDAY,
      DateBuilder.WEDNESDAY,
      DateBuilder.FRIDAY,
      DateBuilder.SUNDAY);
  }

  public Scheduler inDay() {
    return null;
  }
}

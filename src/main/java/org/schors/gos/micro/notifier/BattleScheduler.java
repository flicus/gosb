package org.schors.gos.micro.notifier;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.DateBuilder;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.springframework.stereotype.Component;

@Component
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

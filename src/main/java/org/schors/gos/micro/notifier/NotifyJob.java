package org.schors.gos.micro.notifier;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.time.LocalDateTime;

public class NotifyJob implements Job {
  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    System.out.println(LocalDateTime.now() + " : " + context.getJobDetail().getJobDataMap().getString("msg"));
  }
}

package org.schors.gos.micro.notifier;

import org.quartz.DateBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.schors.gos.micro.tg.TgSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import lombok.extern.slf4j.Slf4j;

import java.util.Set;

import static org.quartz.DateBuilder.futureDate;

@Slf4j
public class NotifyJob implements Job {
  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    String text = context.getJobDetail().getJobDataMap().getString("msg");
    Set<Long> chatId = (Set<Long>) context.getJobDetail().getJobDataMap().get("chatId");
    TgSender sender = (TgSender) context.getJobDetail().getJobDataMap().get("sender");
    Scheduler scheduler = (Scheduler) context.getJobDetail().getJobDataMap().get("executor");

    log.debug("trigger: " + context.getTrigger().getKey().getName());

    chatId.forEach(cid -> sender.send(SendMessage.builder()
        .chatId(cid)
        .text(text)
        .build())
      .subscribe(message -> {
        JobDetail job = JobBuilder
          .newJob(DeleteMessageJob.class)
          .withIdentity("deleteNotify" + message.getMessageId())
          .build();
        job.getJobDataMap().put("msgId", message.getMessageId());
        job.getJobDataMap().put("chatId", cid);
        job.getJobDataMap().put("sender", sender);
        Trigger deleteTrigger = TriggerBuilder
          .newTrigger()
          .startAt(futureDate(1, DateBuilder.IntervalUnit.HOUR))
          .withIdentity("deleteNotify" + message.getMessageId())
          .forJob(job)
          .build();
        try {
          scheduler.scheduleJob(job, deleteTrigger);
        } catch (SchedulerException e) {
          log.warn(e.getMessage(), e);
        }
      }));
  }
}

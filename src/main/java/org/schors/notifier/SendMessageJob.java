package org.schors.notifier;

import java.util.List;
import java.util.Random;

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
import org.schors.tg.TgSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import io.quarkus.logging.Log;

import static org.quartz.DateBuilder.futureDate;

public class SendMessageJob implements Job {

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    List<String> messages = (List<String>) context.getJobDetail().getJobDataMap().get("msg");
    String chatId = context.getJobDetail().getJobDataMap().getString("chatId");
    TgSender sender = (TgSender) context.getJobDetail().getJobDataMap().get("sender");
    Scheduler scheduler = (Scheduler) context.getJobDetail().getJobDataMap().get("executor");

    Log.debug("trigger: " + context.getTrigger().getKey().getName());

    Random r = new Random();
    r.setSeed(System.currentTimeMillis());
    int idx = r.nextInt(messages.size());

    Message message = sender.sendMessage(SendMessage.builder()
        .chatId(chatId)
        .text(messages.get(idx))
        .build())
      .log()
      .await().indefinitely();

    JobDetail job = JobBuilder
      .newJob(DeleteMessageJob.class)
      .withIdentity("deleteNotify" + message.getMessageId())
      .build();
    job.getJobDataMap().put("msgId", message.getMessageId());
    Trigger deleteTrigger = TriggerBuilder
      .newTrigger()
      .startAt(futureDate(1, DateBuilder.IntervalUnit.HOUR))
      .withIdentity("deleteNotify" + message.getMessageId())
      .forJob(job)
      .build();
    try {
      scheduler.scheduleJob(job, deleteTrigger);
    } catch (SchedulerException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}

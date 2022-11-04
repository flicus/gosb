package org.schors.gos.micro.notifier;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.schors.gos.micro.tg.TgSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.Random;

import static org.quartz.DateBuilder.futureDate;


@Slf4j
public class SendMessageJob implements Job {

  @SneakyThrows
  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    List<String> messages = (List<String>) context.getJobDetail().getJobDataMap().get("msg");
    Long chatId = context.getJobDetail().getJobDataMap().getLong("chatId");
    TgSender sender = (TgSender) context.getJobDetail().getJobDataMap().get("sender");
    Scheduler scheduler = (Scheduler) context.getJobDetail().getJobDataMap().get("executor");

    log.debug("trigger: " + context.getTrigger().getKey().getName());

    Random r = new Random();
    r.setSeed(System.currentTimeMillis());
    int idx = r.nextInt(messages.size());

    Message message = sender.send(SendMessage.builder()
        .chatId(chatId)
        .text(messages.get(idx))
        .build())
      .log()
      .block();

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
    scheduler.scheduleJob(job, deleteTrigger);
  }
}

package org.schors.gos.micro.notifier;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.schors.gos.micro.tg.TgSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.quartz.DateBuilder.futureDate;

@Slf4j
public class RandomMessageJob implements Job {

  @SneakyThrows
  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    List<String> messages = (List<String>) context.getJobDetail().getJobDataMap().get("msg");
    Set<Long> chatId = (Set<Long>) context.getJobDetail().getJobDataMap().get("chatId");
    TgSender sender = (TgSender) context.getJobDetail().getJobDataMap().get("sender");
    Scheduler scheduler = (Scheduler) context.getJobDetail().getJobDataMap().get("executor");

    log.debug("trigger: " + context.getTrigger().getKey().getName());

    Random r = new Random();
    r.setSeed(System.currentTimeMillis());
    int idx = r.nextInt(messages.size());

    chatId.forEach(cid -> sender.send(SendMessage.builder()
        .chatId(cid)
        .text(messages.get(idx))
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

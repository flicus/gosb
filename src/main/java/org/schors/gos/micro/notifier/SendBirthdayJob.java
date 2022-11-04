package org.schors.gos.micro.notifier;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.schors.gos.micro.tg.TgSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Slf4j
public class SendBirthdayJob implements Job {

  @SneakyThrows
  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    String name = context.getJobDetail().getJobDataMap().getString("name");
    Long chatId = context.getJobDetail().getJobDataMap().getLong("chatId");
    TgSender sender = (TgSender) context.getJobDetail().getJobDataMap().get("sender");
    log.debug("trigger: " + context.getTrigger().getKey().getName());

    sender
      .send(SendMessage.builder()
        .chatId(chatId)
        .text("С днем рождения, " + name)
        .build())
      .log();
  }

}

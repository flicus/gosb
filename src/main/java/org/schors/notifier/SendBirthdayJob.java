package org.schors.notifier;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.schors.tg.TgSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import io.quarkus.logging.Log;

public class SendBirthdayJob implements Job {

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    String name = context.getJobDetail().getJobDataMap().getString("name");
    String chatId = context.getJobDetail().getJobDataMap().getString("chatId");
    TgSender sender = (TgSender) context.getJobDetail().getJobDataMap().get("sender");
    Log.debug("trigger: " + context.getTrigger().getKey().getName());

    sender
      .sendMessage(SendMessage.builder()
        .chatId(chatId)
        .text("С днем рождения, " + name)
        .build())
      .log();
  }

}

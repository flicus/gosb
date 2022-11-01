package org.schors.notifier;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.schors.tg.TgSender;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

import io.quarkus.logging.Log;

public class DeleteMessageJob implements Job {

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    Integer msgId = (Integer) context.getJobDetail().getJobDataMap().get("msgId");
    String chatId = context.getJobDetail().getJobDataMap().getString("chatId");
    TgSender sender = (TgSender) context.getJobDetail().getJobDataMap().get("sender");
    Log.debug("delete: " + msgId);
    sender
      .sendMessage(DeleteMessage.builder().chatId(chatId).messageId(msgId).build())
      .log();
  }
}

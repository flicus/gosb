package org.schors.gos.micro.notifier;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.schors.gos.micro.tg.TgSender;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

@Slf4j
public class DeleteMessageJob implements Job {

  @SneakyThrows
  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    Integer msgId = (Integer) context.getJobDetail().getJobDataMap().get("msgId");
    Long chatId = context.getJobDetail().getJobDataMap().getLong("chatId");
    TgSender sender = (TgSender) context.getJobDetail().getJobDataMap().get("sender");

    log.debug("delete: " + msgId);
    sender
      .send(DeleteMessage.builder().chatId(chatId).messageId(msgId).build())
      .subscribe();
  }
}

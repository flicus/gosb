package org.schors.gos.micro.notifier;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.schors.gos.micro.repository.PersonRepository;
import org.schors.gos.micro.tg.TgSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Slf4j
@AllArgsConstructor
public class SendBirthdayJob implements Job {

  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

  private PersonRepository personRepository;

  @SneakyThrows
  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    Long chatId = context.getJobDetail().getJobDataMap().getLong("chatId");
    TgSender sender = (TgSender) context.getJobDetail().getJobDataMap().get("sender");
    log.debug("trigger: " + context.getTrigger().getKey().getName());

    personRepository.getAllPersons()
        .filter(p -> {
          Date date = null;
          try {
            date = dateFormat.parse(p.getDate());
          } catch (ParseException e) {
            log.warn("Wrong date: ", e);
          }
          if (date == null) return false;
          Date now = new Date();
          return now.getDay() == date.getDay() && now.getMonth() == date.getMonth();
        })
        .subscribe(p -> sender
            .send(SendMessage.builder()
                .chatId(chatId)
                .text("С днем рождения, " + p.getName())
                .build())
            .log());
  }

}

package org.schors.gos.micro.notifier;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.schors.gos.micro.BirthdayConfig;
import org.schors.gos.micro.repository.PersonRepository;
import org.schors.gos.micro.tg.TgSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Slf4j
public class SendBirthdayJob implements Job {

  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

  @SneakyThrows
  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    Set<Long> chatId = (Set<Long>) context.getJobDetail().getJobDataMap().get("chatId");
    TgSender sender = (TgSender) context.getJobDetail().getJobDataMap().get("sender");
    PersonRepository personRepository = (PersonRepository) context.getJobDetail().getJobDataMap().get("persons");
    BirthdayConfig bdc = (BirthdayConfig) context.getJobDetail().getJobDataMap().get("bdc");
    log.debug("trigger: " + context.getTrigger().getKey().getName());

    Date now = new Date();
    Flux.concat(Flux.fromStream(bdc.getBirthdays().stream()),
        personRepository.getAllPersons())
      .distinct()
      .filter(p -> {
        log.debug(p.toString());
        Date date = null;
        try {
          date = dateFormat.parse(p.getDate());
        } catch (Exception e) {
          log.warn("Wrong date: ", e);
        }
        if (date == null)
          return false;
        return now.getDay() == date.getDay() && now.getMonth() == date.getMonth();
      })
      .subscribe(p -> chatId.forEach(cid -> sender
        .send(SendMessage.builder()
          .chatId(cid)
          .text("С днем рождения, " + p.getName())
          .build())));
  }

}

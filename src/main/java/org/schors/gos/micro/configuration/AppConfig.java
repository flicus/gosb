package org.schors.gos.micro.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

@Factory
public class AppConfig {

  @Bean
  public ObjectMapper mapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.enable(SerializationFeature.WRITE_NULL_MAP_VALUES);
    mapper.enable(SerializationFeature.WRITE_NULL_MAP_VALUES);
    return mapper;
  }

  @SneakyThrows
  @Bean
  public Scheduler scheduler() {
    Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
    scheduler.start();
    return scheduler;
  }


  @Singleton
  public DB getDB() {
    DB db = DBMaker
      .fileDB("gosb.db")
      .closeOnJvmShutdown()
      .transactionEnable()
      .make();
    return db;
  }
}



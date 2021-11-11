package org.schors.gos.micro.configuration;

import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;
import org.mapdb.DB;
import org.mapdb.DBMaker;

@Factory
public class AppConfig {

//    @Bean
//    public ObjectMapper mapper() {
//        return DatabindCodec
//            .mapper()
//            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
//            .disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
//            .disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
//            .registerModule(new JavaTimeModule());
//    }

  @Singleton
  public DB getDB() {
    DB db = DBMaker
      .fileDB("gosb.db")
      .closeOnJvmShutdown()
      .fileMmapEnable().make();
    return db;
  }
}



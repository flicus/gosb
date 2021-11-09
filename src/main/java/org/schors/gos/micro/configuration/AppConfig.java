package org.schors.gos.micro.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vertx.core.Vertx;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.core.spi.VerticleFactory;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public Vertx vertx(VerticleFactory verticleFactory) {
        Vertx vertx = Vertx.vertx();
        vertx.registerVerticleFactory(verticleFactory);
        return vertx;
    }

    @Bean
    public ObjectMapper mapper() {
        return DatabindCodec
            .mapper()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
            .disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
            .registerModule(new JavaTimeModule());
    }

    @Bean
    public DB getDB() {
        DB db = DBMaker
            .fileDB("gosb.db")
            .closeOnJvmShutdown()
            .fileMmapEnable().make();
        return db;
    }
}



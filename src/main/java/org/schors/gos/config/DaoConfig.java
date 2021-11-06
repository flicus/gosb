package org.schors.gos.config;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DaoConfig {

    @Bean
    public DB getDB() {
        DB db = DBMaker.fileDB("gosb.db").fileMmapEnable().make();
        return db;
    }
}

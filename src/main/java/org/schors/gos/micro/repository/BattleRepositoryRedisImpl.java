package org.schors.gos.micro.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.api.StatefulRedisConnection;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.schors.gos.micro.model.PlayerLayout;
import org.schors.gos.micro.model.Week;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.DayOfWeek.MONDAY;

@Slf4j
@Singleton
@Requires(property = "gosb.data", value = "redis")
public class BattleRepositoryRedisImpl implements BattleRepository {

  private static final String WEEK = "week";

  private final StatefulRedisConnection<String, String> connection;
  private final ObjectMapper objectMapper;

  public BattleRepositoryRedisImpl(StatefulRedisConnection<String, String> connection, ObjectMapper objectMapper) {
    this.connection = connection;
    this.objectMapper = objectMapper;
  }

  @Override
  public Flux<String> getWeeks() {
    return connection.reactive().hkeys(WEEK);
  }

  @Override
  public Mono<Week> getWeek(String id) {
    return connection.reactive()
      .hget(WEEK, id)
      .map(this::str2week);
  }

  private Week str2week(String str) {
    try {
      return objectMapper.readValue(str, Week.class);
    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
    }
    return null;
  }

  private String week2str(Week week) {
    String weekString = null;
    try {
      weekString = objectMapper.writeValueAsString(week);
    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
    }
    return weekString;
  }

  @Override
  public Mono<Week> getCurrentWeek() {
    LocalDate weekStart = LocalDate.now().with(MONDAY);
    String date = weekStart.format(DateTimeFormatter.BASIC_ISO_DATE);

    return connection.reactive()
      .hget(WEEK, date)
      .map(this::str2week)
      .onErrorResume(throwable -> {
        Week week = new Week();
        week.setDate(date);
        week.setPlayerLayouts(new ArrayList<>());
        connection.reactive().hset(WEEK, date, week2str(week));
        return Mono.just(week);
      });
  }

  @Override
  public Mono<Boolean> deleteWeek(String date) {
    return connection.reactive().hdel(WEEK, date).map(aLong -> aLong > 0);
  }

  @Override
  public Mono<Week> addPlayerLayout(PlayerLayout playerLayout) {
    LocalDate weekStart = LocalDate.now().with(MONDAY);
    String date = weekStart.format(DateTimeFormatter.BASIC_ISO_DATE);
    return connection.reactive().hget(WEEK, date)
      .map(this::str2week)
      .doOnSuccess(week -> {
        Boolean alreadyThere = week
          .getPlayerLayouts()
          .stream()
          .anyMatch(pl -> pl.getPlayer().getId().equals(playerLayout.getPlayer().getId()));
        if (alreadyThere) {
          List<PlayerLayout> newLayouts = week.getPlayerLayouts().stream()
            .filter(pl -> !pl.getPlayer()
              .getId()
              .equals(playerLayout.getPlayer().getId()))
            .collect(Collectors.toList());
          newLayouts.add(playerLayout);
          week.setPlayerLayouts(newLayouts);
        } else {
          week.getPlayerLayouts().add(playerLayout);
        }
        connection.reactive().hset(WEEK, date, week2str(week));
      });
  }

  @Override
  public Mono<Week> updateWeek(String date, Week newWeek) {
    return connection.reactive().hset(WEEK, date, week2str(newWeek)).map(aBoolean -> newWeek);
  }
}

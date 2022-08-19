package org.schors.gos.micro.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.mapdb.DB;
import org.mapdb.Serializer;
import org.schors.gos.micro.model.BattleType;
import org.schors.gos.micro.model.WeekPlan;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import static java.time.DayOfWeek.MONDAY;

@Slf4j
@Singleton
@Requires(property = "gosb.data", value = "db")
public class WeekPlanRepositoryImpl implements WeekPlanRepository {

  private final DB db;
  private final ObjectMapper objectMapper;
  private final ConcurrentMap<String, String> map;

  public WeekPlanRepositoryImpl(DB db, ObjectMapper objectMapper) {
    this.db = db;
    this.objectMapper = objectMapper;
    this.map = db.hashMap("weekPlan", Serializer.STRING, Serializer.STRING).createOrOpen();
  }

  private WeekPlan str2plan(String str) {
    try {
      System.out.println(str);
      return objectMapper.readValue(str, WeekPlan.class);
    } catch (JsonProcessingException e) {
      log.warn(e.getMessage(), e);
    }
    return null;
  }

  private String plan2str(WeekPlan player) {
    try {
      return objectMapper.writeValueAsString(player);
    } catch (JsonProcessingException e) {
      log.warn(e.getMessage(), e);
    }
    return null;
  }

  @Override
  public Mono<WeekPlan> updateDamage(WeekPlan weekPlan) {
    map.put(weekPlan.getDate(), plan2str(weekPlan));
    db.commit();
    return Mono.just(weekPlan);
  }

  @Override
  public Mono<WeekPlan> updateDamage(String date, BattleType battleType, Map<Integer, String> damage) {
    return Mono
      .just(map.get(date))
      .map(this::str2plan)
      .defaultIfEmpty(createNew(date, battleType, damage))
      .doOnSuccess(weekPlan -> {
        weekPlan.getDamage().put(battleType, damage);
        map.put(date, plan2str(weekPlan));
        db.commit();
      });
  }

  private WeekPlan createNew(String date, BattleType battleType, Map<Integer, String> damage) {
    WeekPlan weekPlan = new WeekPlan();
    weekPlan.setDate(date);
    Map<BattleType, Map<Integer, String>> tmp = new HashMap<>();
    weekPlan.setDamage(tmp);
    tmp.put(battleType, damage);
    return weekPlan;
  }

  @Override
  public Mono<WeekPlan> updateAllianceDamage(String date, BattleType battleType, Integer alliance, String damage) {
    return Mono
      .just(map.get(date))
      .map(this::str2plan)
      .defaultIfEmpty(createNew(date, battleType, new HashMap<>()))
      .doOnSuccess(weekPlan -> {
        weekPlan.getDamage().get(battleType).put(alliance, damage);
        map.put(date, plan2str(weekPlan));
        db.commit();
      });
  }

  @Override
  public Mono<WeekPlan> getWeek(String date) {
    return Mono
      .just(map.get(date))
      .map(this::str2plan);
  }

  @Override
  public Flux<WeekPlan> getAllWeeks() {
    return Flux
      .fromIterable(map.values())
      .map(this::str2plan);
  }

  @Override
  public Flux<WeekPlan> getLastWeeks(int weekCount) {
    return Flux
      .fromIterable(map.values())
      .takeLast(weekCount)
      .map(this::str2plan)
      .onErrorReturn(createCurrent())
//      .defaultIfEmpty(createCurrent())

      ;
  }

  private WeekPlan createCurrent() {
    WeekPlan weekPlan = new WeekPlan();
    LocalDate weekStart = LocalDate.now().with(MONDAY);
    String date = weekStart.format(DateTimeFormatter.BASIC_ISO_DATE);
    weekPlan.setDate(date);
    weekPlan.setDamage(new HashMap<>());
    weekPlan.getDamage().put(BattleType.BATTLE2, new HashMap<>());
    weekPlan.getDamage().put(BattleType.BATTLE3, new HashMap<>());
    weekPlan.getDamage().put(BattleType.BATTLE5, new HashMap<>());
    weekPlan.getDamage().put(BattleType.BATTLE7, new HashMap<>());
    map.put(date, plan2str(weekPlan));
    db.commit();
    return weekPlan;
  }
}

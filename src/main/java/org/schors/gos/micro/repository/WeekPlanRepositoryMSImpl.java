package org.schors.gos.micro.repository;

import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.schors.gos.micro.model.BattleType;
import org.schors.gos.micro.model.WeekPlan;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Singleton
@Requires(property = "gosb.data", value = "ms")
@AllArgsConstructor
public class WeekPlanRepositoryMSImpl implements WeekPlanRepository {
  @Override
  public Mono<WeekPlan> updateDamage(WeekPlan weekPlan) {
    return null;
  }

  @Override
  public Mono<WeekPlan> updateDamage(String date, BattleType battleType, Map<Integer, String> damage) {
    return null;
  }

  @Override
  public Mono<WeekPlan> updateAllianceDamage(String date, BattleType battleType, Integer alliance, String damage) {
    return null;
  }

  @Override
  public Mono<WeekPlan> getWeek(String date) {
    return null;
  }

  @Override
  public Flux<WeekPlan> getAllWeeks() {
    return null;
  }

  @Override
  public Flux<WeekPlan> getLastWeeks(int weekCount) {
    return null;
  }
}

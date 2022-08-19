package org.schors.gos.micro.repository;

import org.schors.gos.micro.model.BattleType;
import org.schors.gos.micro.model.WeekPlan;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface WeekPlanRepository {

  Mono<WeekPlan> updateDamage(WeekPlan weekPlan);

  Mono<WeekPlan> updateDamage(String date, BattleType battleType, Map<Integer, String> damage);

  Mono<WeekPlan> updateAllianceDamage(String date, BattleType battleType, Integer alliance, String damage);

  Mono<WeekPlan> getWeek(String date);

  Flux<WeekPlan> getAllWeeks();

  Flux<WeekPlan> getLastWeeks(int weekCount);

}

package org.schors.gos.micro.controller;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.schors.gos.micro.model.Alliance;
import org.schors.gos.micro.model.WeekPlan;
import org.schors.gos.micro.repository.AllianceRepository;
import org.schors.gos.micro.repository.WeekPlanRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller("/api/plan")
@Slf4j
@AllArgsConstructor
public class PlanController {

  private AllianceRepository allianceRepository;
  private WeekPlanRepository weekPlanRepository;

  @Get("/alliance")
  public Flux<Alliance> getAlliances() {
    return allianceRepository.listAlliances();
  }

  @Post("/alliance")
  public Mono<Alliance> createAlliance(@Body Alliance alliance) {
    return allianceRepository.addAlliance(alliance);
  }

  @Get("/week")
  public Flux<WeekPlan> getCurrentPlan() {
    return weekPlanRepository.getLastWeeks(2);
  }

  @Post("/week")
  public Mono<WeekPlan> updateWeekPlan(@Body WeekPlan weekPlan) {
    return weekPlanRepository.updateDamage(weekPlan);
  }


}

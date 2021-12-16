package org.schors.gos.micro.controller;

import io.micronaut.http.annotation.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.schors.gos.micro.model.PlayerLayout;
import org.schors.gos.micro.model.Week;
import org.schors.gos.micro.repository.BattleRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller("/api/week")
@Slf4j
@AllArgsConstructor
public class WeekController {

  private BattleRepository battleRepository;

  @Get("/all")
  public Flux<String> getWeeks() {
    return battleRepository.getWeeks();
  }

  @Get("/{id}")
  public Mono<Week> getWeek(@PathVariable String id) {
    return battleRepository.getWeek(id);
  }

  @Get
  public Mono<Week> getCurrentWeek() {
    return battleRepository.getCurrentWeek();
  }

  @Post
  public Mono<Week> addPlayerLayout(@Body PlayerLayout playerLayout) {
    return battleRepository.addPlayerLayout(playerLayout);
  }

  @Delete("/{id}")
  public Mono<Boolean> deleteWeek(@PathVariable String id) {
    return battleRepository.deleteWeek(id);
  }

  @Put("/{id}")
  public Mono<Week> updateWeek(@PathVariable String id, @Body Week week) {
    return battleRepository.updateWeek(id, week);
  }


}

package org.schors.gos.micro.controller;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.schors.gos.micro.model.PlayerLayout;
import org.schors.gos.micro.model.Week;
import org.schors.gos.micro.repository.BattleRepository;
import reactor.core.publisher.Mono;

@Controller("/api/week")
@Slf4j
@RequiredArgsConstructor
public class WeekController {

  private BattleRepository battleRepository;

  @Get
  public Mono<Week> getCurrentWeek() {
    return Mono.just(battleRepository.getCurrentWeek());
  }

  @Post
  public Mono<Week> addPlayerLayout(@Body PlayerLayout playerLayout) {
    return Mono.just(battleRepository.addPlayerLayout(playerLayout));
  }

  @Delete("/{id}")
  public Mono<Boolean> deleteWeek(@PathVariable String id) {
    return Mono.just(battleRepository.deleteWeek(id));
  }

  @Put("/{id}")
  public Mono<Week> updateWeek(@PathVariable String id, @Body Week week) {
    return Mono.just(battleRepository.updateWeek(id, week));
  }


}

package org.schors.gos.micro.repository;

import org.schors.gos.micro.model.PlayerLayout;
import org.schors.gos.micro.model.Week;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BattleRepository {

  Flux<String> getWeeks();

  Mono<Week> getWeek(String id);

  Mono<Week> getCurrentWeek();

  Mono<Boolean> deleteWeek(String date);

  Mono<Week> addPlayerLayout(PlayerLayout playerLayout);

  Mono<Week> updateWeek(String date, Week newWeek);
}

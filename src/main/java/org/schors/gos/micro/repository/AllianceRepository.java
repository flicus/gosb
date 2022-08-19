package org.schors.gos.micro.repository;

import org.schors.gos.micro.model.Alliance;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AllianceRepository {

  Mono<Alliance> addAlliance(Alliance alliance);

  Flux<Alliance> listAlliances();

}

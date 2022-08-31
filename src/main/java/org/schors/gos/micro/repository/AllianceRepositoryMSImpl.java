package org.schors.gos.micro.repository;

import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.schors.gos.micro.model.Alliance;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Singleton
@Requires(property = "gosb.data", value = "ms")
@AllArgsConstructor
public class AllianceRepositoryMSImpl implements AllianceRepository {
  @Override
  public Mono<Alliance> addAlliance(Alliance alliance) {
    return null;
  }

  @Override
  public Flux<Alliance> listAlliances() {
    return null;
  }
}

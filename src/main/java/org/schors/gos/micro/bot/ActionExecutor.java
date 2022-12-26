package org.schors.gos.micro.bot;

import reactor.core.publisher.Mono;

@FunctionalInterface
public interface ActionExecutor<T, U, R> {
  Mono<R> execute(T t, U u);
}

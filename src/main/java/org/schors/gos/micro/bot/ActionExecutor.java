package org.schors.gos.micro.bot;

@FunctionalInterface
public interface ActionExecutor<T, U, R> {
  R execute(T t, U u);
}

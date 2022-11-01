package org.schors.bot;

@FunctionalInterface
public interface ActionExecutor<T, U, R> {
  R execute(T t, U u);
}

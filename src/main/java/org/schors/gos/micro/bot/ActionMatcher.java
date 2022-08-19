package org.schors.gos.micro.bot;

@FunctionalInterface
public interface ActionMatcher<T, U, R> {
  R match(T t, U u);
}

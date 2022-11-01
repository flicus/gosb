package org.schors.bot;

@FunctionalInterface
public interface ActionMatcher<T, U, R> {
  R match(T t, U u);
}

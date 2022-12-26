package org.schors.gos.micro.bot;

@FunctionalInterface
public interface ActionMatcher<T, U> {
  Boolean match(T t, U u);
}

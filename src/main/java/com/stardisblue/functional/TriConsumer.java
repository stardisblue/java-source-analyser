package com.stardisblue.functional;

@FunctionalInterface
public interface TriConsumer<T, U, C> {
    void apply(T t, U u, C c);
}

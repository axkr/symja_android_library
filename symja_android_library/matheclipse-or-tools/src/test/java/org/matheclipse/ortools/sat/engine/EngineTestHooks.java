package org.matheclipse.ortools.sat.engine;

/** Access to package-private settings of the engine for tests. */
public final class EngineTestHooks {
  private EngineTestHooks() {}

  /** Sets the largest initial domain span with a bitset representation; returns the old one. */
  public static long setBitsetLimit(long limit) {
    long old = DomainStore.bitsetLimit;
    DomainStore.bitsetLimit = limit;
    return old;
  }
}

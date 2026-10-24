package org.matheclipse.ortools.constraintsolver.cp;

import java.util.ArrayList;
import java.util.IdentityHashMap;

/**
 * A constraint of the {@link CpStore}: it is executed whenever one of its variables changes and
 * removes the values which can't be part of a solution (or throws {@link CpFail}).
 *
 * <p>
 * Part of the simplified engine behind the OR-Tools constraint solver API of this module.
 */
public abstract class Propagator {

  /** The distinct variables this propagator watches. */
  final CpVar[] vars;

  boolean inQueue;

  int index;

  /** Creates a propagator watching the distinct variables among <code>watched</code>. */
  protected Propagator(CpVar... watched) {
    this.vars = distinct(watched);
  }

  private static CpVar[] distinct(CpVar[] watched) {
    IdentityHashMap<CpVar, Boolean> seen = new IdentityHashMap<>();
    ArrayList<CpVar> list = new ArrayList<>(watched.length);
    for (CpVar v : watched) {
      if (v != null && seen.put(v, Boolean.TRUE) == null) {
        list.add(v);
      }
    }
    return list.toArray(new CpVar[0]);
  }

  /** The variables watched by this propagator. */
  public CpVar[] variables() {
    return vars;
  }

  /** Removes the values which are inconsistent with the constraint. */
  public abstract void propagate();

  /** Whether all watched variables are bound. */
  protected boolean allBound() {
    for (CpVar v : vars) {
      if (!v.isBound()) {
        return false;
      }
    }
    return true;
  }
}

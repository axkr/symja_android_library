package org.matheclipse.ortools.sat.engine;

/**
 * A propagator of the Java constraint engine (replacing the C++ CP-SAT propagators of OR-Tools).
 *
 * <p>
 * A propagator must be <i>sound</i>: it only removes values which cannot be part of a solution of
 * its constraint. It doesn't need to be complete; every solution is verified against the original
 * model before it is reported, so a propagator may wait until its variables are fixed.
 */
abstract class Propagator {
  /** Index in {@link DomainStore#propagators}. */
  int id;
  /** 0 for cheap propagators which run first, 1 for expensive ones. */
  int priority;
  /** Number of failures caused by this propagator, used by the dom/wdeg heuristic. */
  double weight = 1;

  /** The variables whose changes wake up this propagator. */
  abstract int[] watchedVars();

  /** Filters the domains; returns false if the constraint cannot be satisfied anymore. */
  abstract boolean propagate(DomainStore s);

  /** Concatenates variable arrays (used to build {@link #watchedVars()}). */
  static int[] concat(int[]... arrays) {
    int n = 0;
    for (int[] a : arrays) {
      n += a.length;
    }
    int[] r = new int[n];
    int k = 0;
    for (int[] a : arrays) {
      for (int x : a) {
        r[k++] = x;
      }
    }
    return r;
  }

  /** Returns the variables of the given literals. */
  static int[] litVars(int[] lits) {
    int[] r = new int[lits.length];
    for (int i = 0; i < lits.length; i++) {
      r[i] = DomainStore.litVar(lits[i]);
    }
    return r;
  }

  /** Returns the variables (&gt;= 0) of the given affine views. */
  static int[] affVars(Affine... exprs) {
    int n = 0;
    for (Affine e : exprs) {
      if (e.var >= 0) {
        n++;
      }
    }
    int[] r = new int[n];
    int k = 0;
    for (Affine e : exprs) {
      if (e.var >= 0) {
        r[k++] = e.var;
      }
    }
    return r;
  }

  /**
   * A propagator of a constraint with enforcement literals: the constraint must hold if all the
   * literals are true and is ignored if one of them is false.
   */
  abstract static class Enforced extends Propagator {
    final int[] enforcement;

    Enforced(int[] enforcement) {
      this.enforcement = enforcement == null ? new int[0] : enforcement;
    }

    @Override
    final boolean propagate(DomainStore s) {
      int unfixed = 0;
      int lastUnfixed = 0;
      for (int lit : enforcement) {
        if (s.litFalse(lit)) {
          return true;
        }
        if (!s.litTrue(lit)) {
          unfixed++;
          lastUnfixed = lit;
        }
      }
      if (unfixed == 0) {
        return propagateEnforced(s);
      }
      if (unfixed == 1 && isInfeasible(s)) {
        return s.setLit(lastUnfixed, false);
      }
      return true;
    }

    /** Propagation when all enforcement literals are true. */
    abstract boolean propagateEnforced(DomainStore s);

    /**
     * Returns true if the constraint is certainly violated by the current domains. The default
     * implementation detects nothing.
     */
    boolean isInfeasible(DomainStore s) {
      return false;
    }

    int[] enforcementVars() {
      return litVars(enforcement);
    }
  }
}

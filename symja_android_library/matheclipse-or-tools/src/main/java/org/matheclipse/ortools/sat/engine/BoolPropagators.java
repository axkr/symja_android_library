package org.matheclipse.ortools.sat.engine;

/**
 * Propagators of the Boolean constraints bool_or, at_most_one, exactly_one and bool_xor of the
 * Java engine which replaces the C++ CP-SAT solver of OR-Tools (the bool_and constraint and the
 * enforcement literals of bool_or are translated into clauses).
 */
final class BoolPropagators {
  private BoolPropagators() {}

  /** At least one literal is true. */
  static final class Clause extends Propagator {
    final int[] lits;
    private final int[] vars;

    Clause(int[] lits) {
      this.lits = lits;
      this.vars = litVars(lits);
    }

    @Override
    int[] watchedVars() {
      return vars;
    }

    @Override
    boolean propagate(DomainStore s) {
      int unfixed = 0;
      int last = 0;
      for (int lit : lits) {
        if (s.litTrue(lit)) {
          return true;
        }
        if (!s.litFalse(lit)) {
          unfixed++;
          last = lit;
          if (unfixed > 1) {
            return true;
          }
        }
      }
      if (unfixed == 0) {
        return false;
      }
      return s.setLit(last, true);
    }
  }

  /** At most one literal is true; with {@code exactly} exactly one. */
  static final class AtMostOne extends Propagator {
    final int[] lits;
    final boolean exactly;
    private final int[] vars;

    AtMostOne(int[] lits, boolean exactly) {
      this.lits = lits;
      this.exactly = exactly;
      this.vars = litVars(lits);
    }

    @Override
    int[] watchedVars() {
      return vars;
    }

    @Override
    boolean propagate(DomainStore s) {
      int trueIndex = -1;
      int unfixed = 0;
      int lastUnfixed = 0;
      for (int i = 0; i < lits.length; i++) {
        int lit = lits[i];
        if (s.litTrue(lit)) {
          if (trueIndex >= 0) {
            return false;
          }
          trueIndex = i;
        } else if (!s.litFalse(lit)) {
          unfixed++;
          lastUnfixed = lit;
        }
      }
      if (trueIndex >= 0) {
        for (int i = 0; i < lits.length && unfixed > 0; i++) {
          if (i != trueIndex && !s.litFixed(lits[i])) {
            // setting a literal false may make a later complementary literal true
            if (!s.setLit(lits[i], false)) {
              return false;
            }
          }
        }
        // a literal and its negation in the list: check again
        int numTrue = 0;
        for (int lit : lits) {
          if (s.litTrue(lit)) {
            numTrue++;
          }
        }
        return numTrue <= 1;
      }
      if (exactly) {
        if (unfixed == 0) {
          return false;
        }
        if (unfixed == 1) {
          return s.setLit(lastUnfixed, true);
        }
      }
      return true;
    }
  }

  /** An odd number of literals is true (with enforcement literals). */
  static final class Xor extends Propagator.Enforced {
    final int[] lits;
    private final int[] vars;

    Xor(int[] lits, int[] enforcement) {
      super(enforcement);
      this.lits = lits;
      this.vars = concat(litVars(lits), enforcementVars());
    }

    @Override
    int[] watchedVars() {
      return vars;
    }

    @Override
    boolean propagateEnforced(DomainStore s) {
      int parity = 0;
      int unfixed = 0;
      int last = 0;
      for (int lit : lits) {
        if (s.litTrue(lit)) {
          parity ^= 1;
        } else if (!s.litFalse(lit)) {
          unfixed++;
          last = lit;
          if (unfixed > 1) {
            return true;
          }
        }
      }
      if (unfixed == 0) {
        return parity == 1;
      }
      return s.setLit(last, parity == 0);
    }

    @Override
    boolean isInfeasible(DomainStore s) {
      int parity = 0;
      for (int lit : lits) {
        if (s.litTrue(lit)) {
          parity ^= 1;
        } else if (!s.litFalse(lit)) {
          return false;
        }
      }
      return parity == 0;
    }
  }
}

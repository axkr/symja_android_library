package org.matheclipse.ortools.sat.engine;

import org.matheclipse.ortools.util.Domain;

/**
 * Bounds consistent propagator of {@code sum(coeffs[i] * vars[i]) in domain}, with enforcement
 * literals, of the Java engine which replaces the C++ CP-SAT solver of OR-Tools. Holes of the domain are used to tighten the activity bounds and, once a single
 * variable is left, to remove the values of this variable which map into a hole.
 *
 * <p>
 * The upper bound of the activity can additionally be tightened monotonically with
 * {@link #tightenUpperBound(long)}; the search uses this for the objective.
 */
final class LinearPropagator extends Propagator.Enforced {
  final int[] vars;
  final long[] coeffs;
  final Domain domain;
  private final boolean holes;
  private final int[] watched;
  /** An additional (not trailed) upper bound of the activity. */
  private long dynamicUpper = Long.MAX_VALUE;

  LinearPropagator(int[] vars, long[] coeffs, Domain domain, int[] enforcement) {
    super(enforcement);
    this.vars = vars;
    this.coeffs = coeffs;
    this.domain = domain;
    this.holes = domain.numIntervals() > 1;
    this.watched = concat(vars, enforcementVars());
    this.priority = vars.length > 64 ? 1 : 0;
  }

  @Override
  int[] watchedVars() {
    return watched;
  }

  /** Requires {@code activity <= value} from now on (not undone by backtracking). */
  void tightenUpperBound(long value) {
    if (value < dynamicUpper) {
      dynamicUpper = value;
    }
  }

  long dynamicUpperBound() {
    return dynamicUpper;
  }

  /** Minimum of the activity under the current domains. */
  long minActivity(DomainStore s) {
    long sum = 0;
    for (int i = 0; i < vars.length; i++) {
      long c = coeffs[i];
      sum += c >= 0 ? c * s.lb[vars[i]] : c * s.ub[vars[i]];
    }
    return sum;
  }

  long maxActivity(DomainStore s) {
    long sum = 0;
    for (int i = 0; i < vars.length; i++) {
      long c = coeffs[i];
      sum += c >= 0 ? c * s.ub[vars[i]] : c * s.lb[vars[i]];
    }
    return sum;
  }

  @Override
  boolean isInfeasible(DomainStore s) {
    long minAct = minActivity(s);
    long maxAct = maxActivity(s);
    long hi = Math.min(domain.max(), dynamicUpper);
    if (minAct > hi || maxAct < domain.min()) {
      return true;
    }
    return holes && !domain.overlapsWith(new Domain(minAct, maxAct));
  }

  @Override
  boolean propagateEnforced(DomainStore s) {
    final int n = vars.length;
    for (int pass = 0; pass < 8; pass++) {
      long minAct = 0;
      long maxAct = 0;
      int unfixed = 0;
      int lastUnfixed = -1;
      for (int i = 0; i < n; i++) {
        int v = vars[i];
        long c = coeffs[i];
        long l = s.lb[v];
        long u = s.ub[v];
        if (c >= 0) {
          minAct += c * l;
          maxAct += c * u;
        } else {
          minAct += c * u;
          maxAct += c * l;
        }
        if (l != u) {
          unfixed++;
          lastUnfixed = i;
        }
      }
      long lo = domain.min();
      long hi = Math.min(domain.max(), dynamicUpper);
      if (minAct > hi || maxAct < lo) {
        return false;
      }
      if (holes) {
        // the activity must be a value of the domain in [minAct, maxAct]
        long l2 = domain.valueAtOrAfter(Math.max(lo, minAct));
        long h2 = domain.valueAtOrBefore(Math.min(hi, maxAct));
        if (!domain.contains(l2) || !domain.contains(h2) || l2 > h2 || l2 > maxAct
            || h2 < minAct) {
          return false;
        }
        lo = Math.max(lo, l2);
        hi = Math.min(hi, h2);
      }
      if (unfixed == 0) {
        return minAct >= lo && minAct <= hi && (!holes || domain.contains(minAct));
      }
      // clamp to the reachable range so that the differences below cannot overflow
      lo = Math.max(lo, minAct);
      hi = Math.min(hi, maxAct);
      if (unfixed == 1 && holes) {
        int v = vars[lastUnfixed];
        long c = coeffs[lastUnfixed];
        long rest = minAct - (c >= 0 ? c * s.lb[v] : c * s.ub[v]);
        if (!filterLastVariable(s, v, c, rest)) {
          return false;
        }
        return true;
      }
      boolean changed = false;
      for (int i = 0; i < n; i++) {
        int v = vars[i];
        long c = coeffs[i];
        long l = s.lb[v];
        long u = s.ub[v];
        if (l == u) {
          continue;
        }
        long termMin = c >= 0 ? c * l : c * u;
        long termMax = c >= 0 ? c * u : c * l;
        long maxTerm = hi - (minAct - termMin);
        long minTerm = lo - (maxAct - termMax);
        if (termMax > maxTerm) {
          boolean ok = c > 0 ? s.setUb(v, DomainStore.floorDiv(maxTerm, c))
              : s.setLb(v, DomainStore.ceilDiv(maxTerm, c));
          if (!ok) {
            return false;
          }
          changed = true;
        }
        if (termMin < minTerm) {
          boolean ok = c > 0 ? s.setLb(v, DomainStore.ceilDiv(minTerm, c))
              : s.setUb(v, DomainStore.floorDiv(minTerm, c));
          if (!ok) {
            return false;
          }
          changed = true;
        }
      }
      if (!changed) {
        return true;
      }
    }
    // not at the fixpoint yet: run again later
    s.enqueue(this);
    return true;
  }

  // the only unfixed variable v: c * v + rest must be in the domain
  private boolean filterLastVariable(DomainStore s, int v, long c, long rest) {
    if (s.hasBitset(v)) {
      long x = s.lb[v];
      while (x != Long.MAX_VALUE && x <= s.ub[v]) {
        long next = s.nextValueAtOrAfter(v, x + 1);
        if (!domain.contains(c * x + rest)) {
          if (!s.remove(v, x)) {
            return false;
          }
        }
        if (next == Long.MAX_VALUE) {
          break;
        }
        x = next;
      }
      return true;
    }
    Domain allowed = domain.additionWith(new Domain(-rest)).inverseMultiplicationBy(c);
    return s.intersect(v, allowed);
  }
}

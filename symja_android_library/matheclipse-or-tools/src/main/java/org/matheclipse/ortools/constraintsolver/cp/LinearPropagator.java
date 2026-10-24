package org.matheclipse.ortools.constraintsolver.cp;

import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Bounds propagation of <code>lo &lt;= sum(coefs[i] * vars[i]) &lt;= hi</code>.
 *
 * <p>
 * Part of the simplified engine behind the OR-Tools constraint solver API of this module.
 * Repeated variables are merged; infinite bounds (<code>Long.MIN_VALUE</code>,
 * <code>Long.MAX_VALUE</code>) and saturated terms don't prune.
 */
public final class LinearPropagator extends Propagator {

  private final long[] coefs;

  private final CpVar[] terms;

  private final long lo;

  private final long hi;

  /** Creates the propagator; the arrays are copied. */
  public LinearPropagator(long[] coefs, CpVar[] vars, long lo, long hi) {
    super(vars);
    Map<CpVar, long[]> merged = new LinkedHashMap<>();
    IdentityHashMap<CpVar, long[]> byIdentity = new IdentityHashMap<>();
    for (int i = 0; i < vars.length; i++) {
      long[] c = byIdentity.get(vars[i]);
      if (c == null) {
        c = new long[1];
        byIdentity.put(vars[i], c);
        merged.put(vars[i], c);
      }
      c[0] = CapOps.capAdd(c[0], coefs[i]);
    }
    int n = 0;
    for (long[] c : merged.values()) {
      if (c[0] != 0) {
        n++;
      }
    }
    this.coefs = new long[n];
    this.terms = new CpVar[n];
    int k = 0;
    for (Map.Entry<CpVar, long[]> e : merged.entrySet()) {
      if (e.getValue()[0] != 0) {
        this.coefs[k] = e.getValue()[0];
        this.terms[k] = e.getKey();
        k++;
      }
    }
    this.lo = lo;
    this.hi = hi;
  }

  /** The smallest value of the term <code>a * v</code>. */
  static long termMin(long a, CpVar v) {
    return a > 0 ? CapOps.capProd(a, v.min) : CapOps.capProd(a, v.max);
  }

  /** The largest value of the term <code>a * v</code>. */
  static long termMax(long a, CpVar v) {
    return a > 0 ? CapOps.capProd(a, v.max) : CapOps.capProd(a, v.min);
  }

  /** The bounds <code>{min, max}</code> of <code>sum(coefs[i] * vars[i]) + offset</code>. */
  public static long[] bounds(long[] coefs, CpVar[] vars, long offset) {
    long mn = offset;
    long mx = offset;
    for (int i = 0; i < vars.length; i++) {
      mn = CapOps.capAdd(mn, termMin(coefs[i], vars[i]));
      mx = CapOps.capAdd(mx, termMax(coefs[i], vars[i]));
    }
    return new long[] {mn, mx};
  }

  @Override
  public void propagate() {
    final int n = terms.length;
    if (n == 0) {
      if (lo > 0 || hi < 0) {
        throw CpFail.INSTANCE;
      }
      return;
    }
    for (int iteration = 0; iteration < 32; iteration++) {
      // sums of the finite term bounds, plus the number of infinite (or overflowing) ones
      long sumMin = 0;
      long sumMax = 0;
      int minInf = 0;
      int maxInf = 0;
      int minInfIndex = -1;
      int maxInfIndex = -1;
      for (int i = 0; i < n; i++) {
        long tmin = termMin(coefs[i], terms[i]);
        long tmax = termMax(coefs[i], terms[i]);
        if (CapOps.isInfinite(tmin)) {
          minInf++;
          minInfIndex = i;
        } else {
          long s = sumMin + tmin;
          if (((sumMin ^ s) & (tmin ^ s)) < 0) {
            minInf += 2;
          }
          sumMin = s;
        }
        if (CapOps.isInfinite(tmax)) {
          maxInf++;
          maxInfIndex = i;
        } else {
          long s = sumMax + tmax;
          if (((sumMax ^ s) & (tmax ^ s)) < 0) {
            maxInf += 2;
          }
          sumMax = s;
        }
      }
      if (minInf == 0 && hi != Long.MAX_VALUE && sumMin > hi) {
        throw CpFail.INSTANCE;
      }
      if (maxInf == 0 && lo != Long.MIN_VALUE && sumMax < lo) {
        throw CpFail.INSTANCE;
      }
      boolean changed = false;
      boolean recompute = false;
      for (int i = 0; i < n && !recompute; i++) {
        long a = coefs[i];
        CpVar v = terms[i];
        long oldMin = termMin(a, v);
        long oldMax = termMax(a, v);
        boolean modified = false;
        // upper bound of a * v from sumMin and hi
        if (hi != Long.MAX_VALUE && (minInf == 0 || (minInf == 1 && minInfIndex == i))) {
          long rest = minInf == 0 ? sumMin - oldMin : sumMin;
          long bound = CapOps.capSub(hi, rest);
          if (!CapOps.isInfinite(bound)) {
            if (a > 0) {
              long m = CapOps.floorDiv(bound, a);
              if (m < v.max) {
                v.setMax(m);
                modified = true;
              }
            } else {
              long m = CapOps.ceilDiv(bound, a);
              if (m > v.min) {
                v.setMin(m);
                modified = true;
              }
            }
          }
        }
        // lower bound of a * v from sumMax and lo
        if (lo != Long.MIN_VALUE && (maxInf == 0 || (maxInf == 1 && maxInfIndex == i))) {
          long rest = maxInf == 0 ? sumMax - oldMax : sumMax;
          long bound = CapOps.capSub(lo, rest);
          if (!CapOps.isInfinite(bound)) {
            if (a > 0) {
              long m = CapOps.ceilDiv(bound, a);
              if (m > v.min) {
                v.setMin(m);
                modified = true;
              }
            } else {
              long m = CapOps.floorDiv(bound, a);
              if (m < v.max) {
                v.setMax(m);
                modified = true;
              }
            }
          }
        }
        if (modified) {
          changed = true;
          long newMin = termMin(a, v);
          long newMax = termMax(a, v);
          if (CapOps.isInfinite(oldMin) || CapOps.isInfinite(newMin)
              || CapOps.isInfinite(oldMax) || CapOps.isInfinite(newMax)) {
            recompute = true;
          } else {
            sumMin += newMin - oldMin;
            sumMax += newMax - oldMax;
            if (minInf == 0 && hi != Long.MAX_VALUE && sumMin > hi) {
              throw CpFail.INSTANCE;
            }
            if (maxInf == 0 && lo != Long.MIN_VALUE && sumMax < lo) {
              throw CpFail.INSTANCE;
            }
          }
        }
      }
      if (!changed) {
        return;
      }
    }
    // not at the fixpoint yet: run again later
    terms[0].store.enqueue(this);
  }
}

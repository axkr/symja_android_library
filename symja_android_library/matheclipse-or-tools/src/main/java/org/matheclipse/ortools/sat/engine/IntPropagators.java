package org.matheclipse.ortools.sat.engine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.matheclipse.ortools.util.Domain;

/**
 * Propagators of the integer constraints all_diff, element, table, lin_max, int_prod, int_div,
 * int_mod, inverse and automaton of the Java engine which replaces the C++ CP-SAT solver of
 * OR-Tools. All arguments are affine views; the loader replaces longer linear expressions by
 * auxiliary variables.
 */
final class IntPropagators {
  private IntPropagators() {}

  static int[] watched(Affine[] exprs, int[] enforcementVars) {
    return Propagator.concat(Propagator.affVars(exprs), enforcementVars);
  }

  /** All expressions take different values. */
  static final class AllDifferent extends Propagator.Enforced {
    final Affine[] exprs;
    private final int[] watched;
    private final Integer[] order;

    AllDifferent(Affine[] exprs, int[] enforcement) {
      super(enforcement);
      this.exprs = exprs;
      this.watched = watched(exprs, enforcementVars());
      this.order = new Integer[exprs.length];
      this.priority = exprs.length > 16 ? 1 : 0;
    }

    @Override
    int[] watchedVars() {
      return watched;
    }

    @Override
    boolean isInfeasible(DomainStore s) {
      for (int i = 0; i < exprs.length; i++) {
        if (!exprs[i].isFixed(s)) {
          continue;
        }
        long v = exprs[i].value(s);
        for (int j = i + 1; j < exprs.length; j++) {
          if (exprs[j].isFixed(s) && exprs[j].value(s) == v) {
            return true;
          }
        }
      }
      return false;
    }

    @Override
    boolean propagateEnforced(DomainStore s) {
      final int n = exprs.length;
      for (int round = 0; round < 4 * n + 4; round++) {
        boolean changed = false;
        // value elimination
        for (int i = 0; i < n; i++) {
          if (!exprs[i].isFixed(s)) {
            continue;
          }
          long v = exprs[i].value(s);
          for (int j = 0; j < n; j++) {
            if (j == i) {
              continue;
            }
            Affine e = exprs[j];
            if (e.isFixed(s)) {
              if (e.value(s) == v) {
                return false;
              }
            } else if (e.contains(s, v)) {
              if (!e.remove(s, v)) {
                return false;
              }
              if (!e.contains(s, v)) {
                changed = true;
              }
            }
          }
        }
        if (changed) {
          continue;
        }
        // Hall intervals on the bounds
        int r = hallIntervals(s);
        if (r < 0) {
          return false;
        }
        if (r == 0) {
          return true;
        }
      }
      s.enqueue(this);
      return true;
    }

    // returns -1 on failure, 1 if a bound changed, 0 otherwise
    private int hallIntervals(DomainStore s) {
      final int n = exprs.length;
      final long[] mins = new long[n];
      final long[] maxs = new long[n];
      for (int i = 0; i < n; i++) {
        mins[i] = exprs[i].min(s);
        maxs[i] = exprs[i].max(s);
        order[i] = i;
      }
      Arrays.sort(order, (a, b) -> Long.compare(maxs[a], maxs[b]));
      for (int k = 0; k < n; k++) {
        long lo = mins[k];
        int count = 0;
        for (int t = 0; t < n; t++) {
          int j = order[t];
          if (mins[j] < lo) {
            continue;
          }
          count++;
          long hi = maxs[j];
          long capacity = hi - lo + 1;
          if (capacity < count) {
            return -1;
          }
          if (capacity == count) {
            boolean changed = false;
            for (int i = 0; i < n; i++) {
              if (mins[i] >= lo && maxs[i] <= hi) {
                continue;
              }
              Affine e = exprs[i];
              if (mins[i] >= lo && mins[i] <= hi) {
                if (!e.setMin(s, hi + 1)) {
                  return -1;
                }
                changed = true;
              }
              if (maxs[i] >= lo && maxs[i] <= hi) {
                if (!e.setMax(s, lo - 1)) {
                  return -1;
                }
                changed = true;
              }
            }
            if (changed) {
              return 1;
            }
          }
        }
      }
      return 0;
    }
  }

  /** {@code target == exprs[index]}. */
  static final class Element extends Propagator.Enforced {
    final Affine index;
    final Affine[] exprs;
    final Affine target;
    private final int[] watched;

    Element(Affine index, Affine[] exprs, Affine target, int[] enforcement) {
      super(enforcement);
      this.index = index;
      this.exprs = exprs;
      this.target = target;
      Affine[] all = Arrays.copyOf(exprs, exprs.length + 2);
      all[exprs.length] = index;
      all[exprs.length + 1] = target;
      this.watched = watched(all, enforcementVars());
      this.priority = 1;
    }

    @Override
    int[] watchedVars() {
      return watched;
    }

    private boolean compatible(DomainStore s, Affine e) {
      long emin = e.min(s);
      long emax = e.max(s);
      if (emax < target.min(s) || emin > target.max(s)) {
        return false;
      }
      if (e.isFixed(s)) {
        return target.contains(s, emin);
      }
      if (target.isFixed(s)) {
        return e.contains(s, target.value(s));
      }
      return true;
    }

    @Override
    boolean isInfeasible(DomainStore s) {
      long lo = Math.max(0, index.min(s));
      long hi = Math.min(exprs.length - 1, index.max(s));
      for (long k = lo; k <= hi; k++) {
        if (index.contains(s, k) && compatible(s, exprs[(int) k])) {
          return false;
        }
      }
      return true;
    }

    @Override
    boolean propagateEnforced(DomainStore s) {
      final int n = exprs.length;
      if (!index.setMin(s, 0) || !index.setMax(s, n - 1)) {
        return false;
      }
      long tmin = Long.MAX_VALUE;
      long tmax = Long.MIN_VALUE;
      int numValid = 0;
      int lastValid = -1;
      long lo = index.min(s);
      long hi = index.max(s);
      for (long k = lo; k <= hi; k++) {
        if (!index.contains(s, k)) {
          continue;
        }
        Affine e = exprs[(int) k];
        if (!compatible(s, e)) {
          if (!index.remove(s, k)) {
            return false;
          }
          continue;
        }
        numValid++;
        lastValid = (int) k;
        tmin = Math.min(tmin, e.min(s));
        tmax = Math.max(tmax, e.max(s));
      }
      if (numValid == 0) {
        return false;
      }
      if (index.isFixed(s) || numValid == 1) {
        int k = index.isFixed(s) ? (int) index.value(s) : lastValid;
        if (numValid == 1 && !index.isFixed(s) && !index.fix(s, k)) {
          return false;
        }
        return Equality.propagate(s, exprs[k], target);
      }
      if (!target.setMin(s, tmin) || !target.setMax(s, tmax)) {
        return false;
      }
      // value supports of a small target
      if (!target.isConstant() && s.hasBitset(target.var) && target.size(s) * numValid <= 20000) {
        long t = target.min(s);
        while (t != Long.MAX_VALUE) {
          long next = target.nextValueAtOrAfter(s, t + 1);
          boolean supported = false;
          for (long k = index.min(s); k <= index.max(s) && !supported; k++) {
            if (index.contains(s, k) && exprs[(int) k].contains(s, t)) {
              supported = true;
            }
          }
          if (!supported && !target.remove(s, t)) {
            return false;
          }
          t = next;
        }
      }
      return true;
    }
  }

  /** Propagation of {@code a == b} between two affine views. */
  static final class Equality {
    private Equality() {}

    static boolean propagate(DomainStore s, Affine a, Affine b) {
      for (int pass = 0; pass < 4; pass++) {
        long lo = Math.max(a.min(s), b.min(s));
        long hi = Math.min(a.max(s), b.max(s));
        if (lo > hi) {
          return false;
        }
        if (!a.setMin(s, lo) || !a.setMax(s, hi) || !b.setMin(s, lo) || !b.setMax(s, hi)) {
          return false;
        }
        if (a.min(s) == lo && a.max(s) == hi && b.min(s) == lo && b.max(s) == hi) {
          break;
        }
      }
      // value filtering for small domains
      if (!a.isConstant() && !b.isConstant() && s.hasBitset(a.var) && s.hasBitset(b.var)) {
        if (!filterBy(s, a, b) || !filterBy(s, b, a)) {
          return false;
        }
      }
      return true;
    }

    // removes the values of x which are not values of y
    private static boolean filterBy(DomainStore s, Affine x, Affine y) {
      long v = x.min(s);
      while (v != Long.MAX_VALUE) {
        long next = x.nextValueAtOrAfter(s, v + 1);
        if (!y.contains(s, v) && !x.remove(s, v)) {
          return false;
        }
        v = next;
      }
      return true;
    }
  }

  /**
   * Table constraint by simple tabular reduction: the indices of the still valid tuples are kept
   * in a reversible sparse set. Supports the negated (forbidden assignments) form.
   */
  static final class Table extends Propagator.Enforced {
    final Affine[] cols;
    final long[] values;
    final int numTuples;
    final boolean negated;
    private final int[] watched;
    private final int[] perm;
    private int countHandle = -1;

    Table(Affine[] cols, long[] values, boolean negated, int[] enforcement) {
      super(enforcement);
      this.cols = cols;
      this.values = values;
      this.numTuples = cols.length == 0 ? 0 : values.length / cols.length;
      this.negated = negated;
      this.watched = watched(cols, enforcementVars());
      this.perm = new int[numTuples];
      for (int i = 0; i < numTuples; i++) {
        perm[i] = i;
      }
      this.priority = 1;
    }

    void init(DomainStore s) {
      countHandle = s.newRev(numTuples);
    }

    @Override
    int[] watchedVars() {
      return watched;
    }

    private boolean tupleValid(DomainStore s, int t) {
      final int m = cols.length;
      int base = t * m;
      for (int j = 0; j < m; j++) {
        if (!cols[j].contains(s, values[base + j])) {
          return false;
        }
      }
      return true;
    }

    @Override
    boolean isInfeasible(DomainStore s) {
      if (cols.length == 0) {
        return false;
      }
      if (negated) {
        for (Affine c : cols) {
          if (!c.isFixed(s)) {
            return false;
          }
        }
        for (int t = 0; t < numTuples; t++) {
          if (tupleValid(s, t)) {
            return true;
          }
        }
        return false;
      }
      int count = (int) s.getRev(countHandle);
      for (int p = 0; p < count; p++) {
        if (tupleValid(s, perm[p])) {
          return false;
        }
      }
      return true;
    }

    @Override
    boolean propagateEnforced(DomainStore s) {
      final int m = cols.length;
      if (m == 0) {
        return true;
      }
      if (negated) {
        return propagateNegated(s);
      }
      int count = (int) s.getRev(countHandle);
      for (int p = count - 1; p >= 0; p--) {
        int t = perm[p];
        if (!tupleValid(s, t)) {
          count--;
          perm[p] = perm[count];
          perm[count] = t;
        }
      }
      s.setRev(countHandle, count);
      if (count == 0) {
        return false;
      }
      // supports
      for (int j = 0; j < m; j++) {
        Affine c = cols[j];
        if (c.isFixed(s)) {
          continue;
        }
        int v = c.var;
        if (s.hasBitset(v)) {
          long lbv = s.lb[v];
          long span = s.ub[v] - lbv + 1;
          long[] mask = new long[(int) ((span + 63) / 64)];
          for (int p = 0; p < count; p++) {
            long w = values[perm[p] * m + j];
            if (!c.contains(s, w)) {
              continue; // removed by an earlier column on the same variable
            }
            long x = (w - c.offset) / c.coeff;
            long off = x - lbv;
            mask[(int) (off >>> 6)] |= 1L << (off & 63);
          }
          long x = s.lb[v];
          while (x != Long.MAX_VALUE) {
            long next = s.nextValueAtOrAfter(v, x + 1);
            long off = x - lbv;
            if ((mask[(int) (off >>> 6)] & (1L << (off & 63))) == 0) {
              if (!s.remove(v, x)) {
                return false;
              }
            }
            x = next;
          }
        } else {
          long minX = Long.MAX_VALUE;
          long maxX = Long.MIN_VALUE;
          for (int p = 0; p < count; p++) {
            long w = values[perm[p] * m + j];
            if (!c.contains(s, w)) {
              continue;
            }
            long x = (w - c.offset) / c.coeff;
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
          }
          if (minX > maxX || !s.setLb(v, minX) || !s.setUb(v, maxX)) {
            return false;
          }
        }
      }
      return true;
    }

    private boolean propagateNegated(DomainStore s) {
      final int m = cols.length;
      int unfixed = -1;
      for (int j = 0; j < m; j++) {
        if (!cols[j].isFixed(s)) {
          if (unfixed >= 0) {
            return true;
          }
          unfixed = j;
        }
      }
      for (int t = 0; t < numTuples; t++) {
        int base = t * m;
        boolean match = true;
        for (int j = 0; j < m && match; j++) {
          if (j != unfixed && cols[j].value(s) != values[base + j]) {
            match = false;
          }
        }
        if (!match) {
          continue;
        }
        if (unfixed < 0) {
          return false;
        }
        if (!cols[unfixed].remove(s, values[base + unfixed])) {
          return false;
        }
        if (cols[unfixed].isFixed(s)) {
          // fully fixed now: check again
          return propagateNegated(s);
        }
      }
      return true;
    }
  }

  /** {@code target == max(exprs)}. */
  static final class LinMax extends Propagator.Enforced {
    final Affine target;
    final Affine[] exprs;
    private final int[] watched;

    LinMax(Affine target, Affine[] exprs, int[] enforcement) {
      super(enforcement);
      this.target = target;
      this.exprs = exprs;
      Affine[] all = Arrays.copyOf(exprs, exprs.length + 1);
      all[exprs.length] = target;
      this.watched = watched(all, enforcementVars());
    }

    @Override
    int[] watchedVars() {
      return watched;
    }

    @Override
    boolean isInfeasible(DomainStore s) {
      if (exprs.length == 0) {
        return true;
      }
      long maxOfMins = Long.MIN_VALUE;
      long maxOfMaxs = Long.MIN_VALUE;
      for (Affine e : exprs) {
        maxOfMins = Math.max(maxOfMins, e.min(s));
        maxOfMaxs = Math.max(maxOfMaxs, e.max(s));
      }
      return target.min(s) > maxOfMaxs || target.max(s) < maxOfMins;
    }

    @Override
    boolean propagateEnforced(DomainStore s) {
      if (exprs.length == 0) {
        return false;
      }
      for (int pass = 0; pass < 8; pass++) {
        long maxOfMins = Long.MIN_VALUE;
        long maxOfMaxs = Long.MIN_VALUE;
        for (Affine e : exprs) {
          maxOfMins = Math.max(maxOfMins, e.min(s));
          maxOfMaxs = Math.max(maxOfMaxs, e.max(s));
        }
        long tmin0 = target.min(s);
        long tmax0 = target.max(s);
        if (!target.setMin(s, maxOfMins) || !target.setMax(s, maxOfMaxs)) {
          return false;
        }
        long tmax = target.max(s);
        long tmin = target.min(s);
        boolean changed = tmin != tmin0 || tmax != tmax0;
        int candidates = 0;
        Affine last = null;
        for (Affine e : exprs) {
          if (e.max(s) > tmax) {
            if (!e.setMax(s, tmax)) {
              return false;
            }
            changed = true;
          }
          if (e.max(s) >= tmin) {
            candidates++;
            last = e;
          }
        }
        if (candidates == 0) {
          return false;
        }
        if (candidates == 1 && last.min(s) < tmin) {
          if (!last.setMin(s, tmin)) {
            return false;
          }
          changed = true;
        }
        if (!changed) {
          return true;
        }
      }
      s.enqueue(this);
      return true;
    }
  }

  /** {@code z == x * y}. */
  static final class Product extends Propagator.Enforced {
    final Affine z;
    final Affine x;
    final Affine y;
    private final int[] watched;

    Product(Affine z, Affine x, Affine y, int[] enforcement) {
      super(enforcement);
      this.z = z;
      this.x = x;
      this.y = y;
      this.watched = watched(new Affine[] {z, x, y}, enforcementVars());
    }

    @Override
    int[] watchedVars() {
      return watched;
    }

    @Override
    boolean isInfeasible(DomainStore s) {
      long[] r = productRange(x.min(s), x.max(s), y.min(s), y.max(s));
      return r[1] < z.min(s) || r[0] > z.max(s);
    }

    static long[] productRange(long a1, long a2, long b1, long b2) {
      long p1 = Domain.capProd(a1, b1);
      long p2 = Domain.capProd(a1, b2);
      long p3 = Domain.capProd(a2, b1);
      long p4 = Domain.capProd(a2, b2);
      return new long[] {Math.min(Math.min(p1, p2), Math.min(p3, p4)),
          Math.max(Math.max(p1, p2), Math.max(p3, p4))};
    }

    @Override
    boolean propagateEnforced(DomainStore s) {
      for (int pass = 0; pass < 8; pass++) {
        long zmin = z.min(s);
        long zmax = z.max(s);
        long xmin = x.min(s);
        long xmax = x.max(s);
        long ymin = y.min(s);
        long ymax = y.max(s);
        long[] r = productRange(xmin, xmax, ymin, ymax);
        if (!z.setMin(s, r[0]) || !z.setMax(s, r[1])) {
          return false;
        }
        if (!divideBounds(s, x, y)) {
          return false;
        }
        if (!divideBounds(s, y, x)) {
          return false;
        }
        if (z.min(s) == zmin && z.max(s) == zmax && x.min(s) == xmin && x.max(s) == xmax
            && y.min(s) == ymin && y.max(s) == ymax) {
          return true;
        }
      }
      s.enqueue(this);
      return true;
    }

    // a = z / b
    private boolean divideBounds(DomainStore s, Affine a, Affine b) {
      long zmin = z.min(s);
      long zmax = z.max(s);
      long bmin = b.min(s);
      long bmax = b.max(s);
      boolean zHasZero = zmin <= 0 && zmax >= 0;
      if (bmin <= 0 && bmax >= 0) {
        if (zHasZero) {
          return true; // a is free when b == 0 and z == 0
        }
        // b != 0 and a != 0
        if (!b.remove(s, 0) || !a.remove(s, 0)) {
          return false;
        }
        bmin = b.min(s);
        bmax = b.max(s);
      }
      long lo = Long.MAX_VALUE;
      long hi = Long.MIN_VALUE;
      if (bmin < 0) {
        long b2 = Math.min(bmax, -1);
        long[] q = quotientRange(zmin, zmax, bmin, b2);
        lo = Math.min(lo, q[0]);
        hi = Math.max(hi, q[1]);
      }
      if (bmax > 0) {
        long b1 = Math.max(bmin, 1);
        long[] q = quotientRange(zmin, zmax, b1, bmax);
        lo = Math.min(lo, q[0]);
        hi = Math.max(hi, q[1]);
      }
      if (lo > hi) {
        return false;
      }
      return a.setMin(s, lo) && a.setMax(s, hi);
    }

    // range of real z / b for b of fixed sign, rounded inwards to integers
    private static long[] quotientRange(long z1, long z2, long b1, long b2) {
      long lo = Math.min(Math.min(DomainStore.ceilDiv(z1, b1), DomainStore.ceilDiv(z1, b2)),
          Math.min(DomainStore.ceilDiv(z2, b1), DomainStore.ceilDiv(z2, b2)));
      long hi = Math.max(Math.max(DomainStore.floorDiv(z1, b1), DomainStore.floorDiv(z1, b2)),
          Math.max(DomainStore.floorDiv(z2, b1), DomainStore.floorDiv(z2, b2)));
      return new long[] {lo, hi};
    }
  }

  /** {@code t == n / d} rounded towards zero; 0 is not a value of d. */
  static final class Division extends Propagator.Enforced {
    final Affine t;
    final Affine n;
    final Affine d;
    private final int[] watched;

    Division(Affine t, Affine n, Affine d, int[] enforcement) {
      super(enforcement);
      this.t = t;
      this.n = n;
      this.d = d;
      this.watched = watched(new Affine[] {t, n, d}, enforcementVars());
    }

    @Override
    int[] watchedVars() {
      return watched;
    }

    private long[] quotient(DomainStore s) {
      long nmin = n.min(s);
      long nmax = n.max(s);
      long dmin = d.min(s);
      long dmax = d.max(s);
      long lo = Long.MAX_VALUE;
      long hi = Long.MIN_VALUE;
      if (dmin < 0) {
        long d2 = Math.min(dmax, -1);
        long[] c = {nmin / dmin, nmin / d2, nmax / dmin, nmax / d2};
        for (long v : c) {
          lo = Math.min(lo, v);
          hi = Math.max(hi, v);
        }
      }
      if (dmax > 0) {
        long d1 = Math.max(dmin, 1);
        long[] c = {nmin / d1, nmin / dmax, nmax / d1, nmax / dmax};
        for (long v : c) {
          lo = Math.min(lo, v);
          hi = Math.max(hi, v);
        }
      }
      return new long[] {lo, hi};
    }

    @Override
    boolean isInfeasible(DomainStore s) {
      if (d.min(s) == 0 && d.max(s) == 0) {
        return true;
      }
      long[] q = quotient(s);
      return q[0] > t.max(s) || q[1] < t.min(s);
    }

    @Override
    boolean propagateEnforced(DomainStore s) {
      if (!d.remove(s, 0)) {
        return false;
      }
      for (int pass = 0; pass < 8; pass++) {
        long tmin0 = t.min(s);
        long tmax0 = t.max(s);
        long nmin0 = n.min(s);
        long nmax0 = n.max(s);
        long[] q = quotient(s);
        if (q[0] > q[1] || !t.setMin(s, q[0]) || !t.setMax(s, q[1])) {
          return false;
        }
        if (d.isFixed(s)) {
          long c = d.value(s);
          long tl = t.min(s);
          long tu = t.max(s);
          if (c < 0) {
            // t == -(n / -c)
            c = -c;
            long tmp = -tu;
            tu = -tl;
            tl = tmp;
          }
          long nLo = tl > 0 ? tl * c : tl * c - (c - 1);
          long nHi = tu >= 0 ? tu * c + (c - 1) : tu * c;
          if (!n.setMin(s, nLo) || !n.setMax(s, nHi)) {
            return false;
          }
        }
        if (t.min(s) == tmin0 && t.max(s) == tmax0 && n.min(s) == nmin0
            && n.max(s) == nmax0) {
          return true;
        }
      }
      s.enqueue(this);
      return true;
    }
  }

  /** {@code t == n % m} with the sign of n; m is strictly positive. */
  static final class Modulo extends Propagator.Enforced {
    final Affine t;
    final Affine n;
    final Affine m;
    private final int[] watched;

    Modulo(Affine t, Affine n, Affine m, int[] enforcement) {
      super(enforcement);
      this.t = t;
      this.n = n;
      this.m = m;
      this.watched = watched(new Affine[] {t, n, m}, enforcementVars());
    }

    @Override
    int[] watchedVars() {
      return watched;
    }

    private long[] range(DomainStore s) {
      long mmax = m.max(s);
      long nmin = n.min(s);
      long nmax = n.max(s);
      long lo = -(mmax - 1);
      long hi = mmax - 1;
      if (nmin >= 0) {
        lo = 0;
        hi = Math.min(hi, nmax);
      } else if (nmax <= 0) {
        hi = 0;
        lo = Math.max(lo, nmin);
      } else {
        lo = Math.max(lo, nmin);
        hi = Math.min(hi, nmax);
      }
      if (n.isFixed(s) && m.isFixed(s)) {
        long r = n.value(s) % m.value(s);
        lo = r;
        hi = r;
      }
      return new long[] {lo, hi};
    }

    @Override
    boolean isInfeasible(DomainStore s) {
      if (m.max(s) <= 0) {
        return true;
      }
      long[] r = range(s);
      return r[0] > t.max(s) || r[1] < t.min(s);
    }

    @Override
    boolean propagateEnforced(DomainStore s) {
      if (!m.setMin(s, 1)) {
        return false;
      }
      long[] r = range(s);
      if (!t.setMin(s, r[0]) || !t.setMax(s, r[1])) {
        return false;
      }
      // the sign of n is the sign of t
      if (t.min(s) > 0 && !n.setMin(s, 1)) {
        return false;
      }
      if (t.max(s) < 0 && !n.setMax(s, -1)) {
        return false;
      }
      // |t| < m
      long minAbsT = (t.min(s) > 0) ? t.min(s) : (t.max(s) < 0 ? -t.max(s) : 0);
      if (!m.setMin(s, minAbsT + 1)) {
        return false;
      }
      // exact filtering when m is fixed and n small
      if (m.isFixed(s) && !n.isConstant() && s.hasBitset(n.var) && n.size(s) <= 4096) {
        long mod = m.value(s);
        long lo = Long.MAX_VALUE;
        long hi = Long.MIN_VALUE;
        long v = n.min(s);
        java.util.HashSet<Long> supported = new java.util.HashSet<>();
        while (v != Long.MAX_VALUE) {
          long next = n.nextValueAtOrAfter(s, v + 1);
          long rem = v % mod;
          if (!t.contains(s, rem)) {
            if (!n.remove(s, v)) {
              return false;
            }
          } else {
            supported.add(rem);
            lo = Math.min(lo, rem);
            hi = Math.max(hi, rem);
          }
          v = next;
        }
        if (lo > hi || !t.setMin(s, lo) || !t.setMax(s, hi)) {
          return false;
        }
        if (!t.isConstant() && s.hasBitset(t.var)) {
          long w = t.min(s);
          while (w != Long.MAX_VALUE) {
            long next = t.nextValueAtOrAfter(s, w + 1);
            if (!supported.contains(w) && !t.remove(s, w)) {
              return false;
            }
            w = next;
          }
        }
      }
      return true;
    }
  }

  /**
   * {@code f[i] == j <=> g[j] == i} over affine expressions (the legacy variable form uses
   * identity views); all expressions take values in {@code [0, n)}.
   */
  static final class Inverse extends Propagator.Enforced {
    final Affine[] f;
    final Affine[] g;
    private final int[] watched;

    Inverse(Affine[] f, Affine[] g, int[] enforcement) {
      super(enforcement);
      this.f = f;
      this.g = g;
      this.watched = concat(Propagator.affVars(f), Propagator.affVars(g), enforcementVars());
      this.priority = 1;
    }

    @Override
    int[] watchedVars() {
      return watched;
    }

    @Override
    boolean isInfeasible(DomainStore s) {
      int n = f.length;
      for (int i = 0; i < n; i++) {
        if (f[i].min(s) > n - 1 || f[i].max(s) < 0 || g[i].min(s) > n - 1 || g[i].max(s) < 0) {
          return true;
        }
        if (f[i].isFixed(s)) {
          int j = (int) f[i].value(s);
          if (!g[j].contains(s, i)) {
            return true;
          }
        }
      }
      return false;
    }

    @Override
    boolean propagateEnforced(DomainStore s) {
      final int n = f.length;
      for (int i = 0; i < n; i++) {
        if (!f[i].setMin(s, 0) || !f[i].setMax(s, n - 1) || !g[i].setMin(s, 0)
            || !g[i].setMax(s, n - 1)) {
          return false;
        }
      }
      for (int pass = 0; pass < 2 * n + 2; pass++) {
        boolean changed = false;
        for (int side = 0; side < 2; side++) {
          Affine[] a = side == 0 ? f : g;
          Affine[] b = side == 0 ? g : f;
          for (int i = 0; i < n; i++) {
            long x = a[i].min(s);
            while (x != Long.MAX_VALUE) {
              long next = a[i].nextValueAtOrAfter(s, x + 1);
              if (!b[(int) x].contains(s, i)) {
                if (!a[i].remove(s, x)) {
                  return false;
                }
                if (!a[i].contains(s, x)) {
                  changed = true;
                }
              }
              x = next;
            }
          }
        }
        if (!changed) {
          return true;
        }
      }
      s.enqueue(this);
      return true;
    }
  }

  /** The sequence of expression values is accepted by an automaton. */
  static final class Automaton extends Propagator.Enforced {
    final Affine[] exprs;
    final int numStates;
    final int start;
    final boolean[] isFinal;
    /** per state: transitions as label -> head */
    final long[][] labels;
    final int[][] heads;
    private final int[] watched;

    Automaton(Affine[] exprs, long startingState, long[] finals, long[] tails, long[] tHeads,
        long[] tLabels, int[] enforcement) {
      super(enforcement);
      this.exprs = exprs;
      Map<Long, Integer> ids = new HashMap<>();
      ids.put(startingState, 0);
      for (long f : finals) {
        ids.putIfAbsent(f, ids.size());
      }
      for (int i = 0; i < tails.length; i++) {
        ids.putIfAbsent(tails[i], ids.size());
        ids.putIfAbsent(tHeads[i], ids.size());
      }
      this.numStates = ids.size();
      this.start = 0;
      this.isFinal = new boolean[numStates];
      for (long f : finals) {
        isFinal[ids.get(f)] = true;
      }
      int[] counts = new int[numStates];
      for (int i = 0; i < tails.length; i++) {
        counts[ids.get(tails[i])]++;
      }
      labels = new long[numStates][];
      heads = new int[numStates][];
      for (int q = 0; q < numStates; q++) {
        labels[q] = new long[counts[q]];
        heads[q] = new int[counts[q]];
      }
      int[] fill = new int[numStates];
      for (int i = 0; i < tails.length; i++) {
        int q = ids.get(tails[i]);
        labels[q][fill[q]] = tLabels[i];
        heads[q][fill[q]] = ids.get(tHeads[i]);
        fill[q]++;
      }
      this.watched = watched(exprs, enforcementVars());
      this.priority = 1;
    }

    @Override
    int[] watchedVars() {
      return watched;
    }

    // returns the supported labels per layer, or null if no accepting path exists
    private boolean[][] supportedTransitions(DomainStore s, boolean[][] reach) {
      final int n = exprs.length;
      reach[0][start] = true;
      for (int i = 0; i < n; i++) {
        for (int q = 0; q < numStates; q++) {
          if (!reach[i][q]) {
            continue;
          }
          for (int k = 0; k < labels[q].length; k++) {
            if (exprs[i].contains(s, labels[q][k])) {
              reach[i + 1][heads[q][k]] = true;
            }
          }
        }
      }
      boolean[][] co = new boolean[n + 1][numStates];
      boolean any = false;
      for (int q = 0; q < numStates; q++) {
        if (reach[n][q] && isFinal[q]) {
          co[n][q] = true;
          any = true;
        }
      }
      if (!any) {
        return null;
      }
      for (int i = n - 1; i >= 0; i--) {
        for (int q = 0; q < numStates; q++) {
          if (!reach[i][q]) {
            continue;
          }
          for (int k = 0; k < labels[q].length; k++) {
            if (co[i + 1][heads[q][k]] && exprs[i].contains(s, labels[q][k])) {
              co[i][q] = true;
              break;
            }
          }
        }
      }
      return co[0][start] ? co : null;
    }

    @Override
    boolean isInfeasible(DomainStore s) {
      boolean[][] reach = new boolean[exprs.length + 1][numStates];
      return supportedTransitions(s, reach) == null;
    }

    @Override
    boolean propagateEnforced(DomainStore s) {
      final int n = exprs.length;
      boolean[][] reach = new boolean[n + 1][numStates];
      boolean[][] co = supportedTransitions(s, reach);
      if (co == null) {
        return false;
      }
      for (int i = 0; i < n; i++) {
        Affine e = exprs[i];
        if (e.isFixed(s)) {
          continue;
        }
        java.util.HashSet<Long> support = new java.util.HashSet<>();
        long lo = Long.MAX_VALUE;
        long hi = Long.MIN_VALUE;
        for (int q = 0; q < numStates; q++) {
          if (!reach[i][q] || !co[i][q]) {
            continue;
          }
          for (int k = 0; k < labels[q].length; k++) {
            long l = labels[q][k];
            if (co[i + 1][heads[q][k]] && e.contains(s, l)) {
              support.add(l);
              lo = Math.min(lo, l);
              hi = Math.max(hi, l);
            }
          }
        }
        if (lo > hi || !e.setMin(s, lo) || !e.setMax(s, hi)) {
          return false;
        }
        if (s.hasBitset(e.var)) {
          long w = e.min(s);
          while (w != Long.MAX_VALUE) {
            long next = e.nextValueAtOrAfter(s, w + 1);
            if (!support.contains(w) && !e.remove(s, w)) {
              return false;
            }
            w = next;
          }
        }
      }
      return true;
    }
  }
}

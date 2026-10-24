package org.matheclipse.ortools.constraintsolver.cp;

import java.util.Arrays;
import java.util.function.LongBinaryOperator;
import java.util.function.LongUnaryOperator;

/**
 * The propagators of the small constraint engine behind the OR-Tools <code>Solver</code> facade
 * of this module (besides {@link LinearPropagator}). They reimplement the semantics of the
 * corresponding OR-Tools constraints with simple bounds (and, where cheap, domain) reasoning.
 */
public final class Propagators {

  private Propagators() {}

  /** Comparison operators of reified linear relations. */
  public enum Relation {
    /** <code>expr == c</code>. */
    EQ,
    /** <code>expr != c</code>. */
    NE,
    /** <code>expr &lt;= c</code>. */
    LE,
    /** <code>expr &gt;= c</code>. */
    GE
  }

  /** <code>sum(coefs[i] * vars[i]) != c</code>. */
  public static final class NotEqual extends Propagator {
    private final long[] coefs;

    private final CpVar[] terms;

    private final long c;

    /** Creates the propagator. */
    public NotEqual(long[] coefs, CpVar[] vars, long c) {
      super(vars);
      this.coefs = coefs.clone();
      this.terms = vars.clone();
      this.c = c;
    }

    @Override
    public void propagate() {
      int unbound = -1;
      long rest = 0;
      for (int i = 0; i < terms.length; i++) {
        if (terms[i].isBound()) {
          rest = CapOps.capAdd(rest, CapOps.capProd(coefs[i], terms[i].min));
        } else {
          if (unbound >= 0 && terms[unbound] != terms[i]) {
            return;
          }
          if (unbound >= 0) {
            // the same variable twice: give up the pruning
            return;
          }
          unbound = i;
        }
      }
      if (unbound < 0) {
        if (rest == c) {
          throw CpFail.INSTANCE;
        }
        return;
      }
      long target = CapOps.capSub(c, rest);
      long a = coefs[unbound];
      if (!CapOps.isInfinite(target) && target % a == 0) {
        terms[unbound].removeValue(target / a);
      }
    }
  }

  /** All variables take different values. */
  public static final class AllDifferent extends Propagator {
    private final CpVar[] xs;

    private final boolean strong;

    private final Integer[] order;

    /** Creates the propagator; <code>strong</code> adds Hall interval bounds reasoning. */
    public AllDifferent(CpVar[] vars, boolean strong) {
      super(vars);
      this.xs = vars.clone();
      this.strong = strong;
      this.order = new Integer[vars.length];
    }

    @Override
    public void propagate() {
      final int n = xs.length;
      boolean again = true;
      while (again) {
        again = false;
        for (int i = 0; i < n; i++) {
          CpVar x = xs[i];
          if (!x.isBound()) {
            continue;
          }
          long v = x.min;
          for (int j = 0; j < n; j++) {
            if (j != i) {
              CpVar y = xs[j];
              if (y == x) {
                throw CpFail.INSTANCE;
              }
              if (y.contains(v)) {
                boolean wasBound = y.isBound();
                y.removeValue(v);
                if (!wasBound && y.isBound()) {
                  again = true;
                }
              }
            }
          }
        }
      }
      if (strong && n > 1) {
        hallIntervals();
      }
    }

    private void hallIntervals() {
      final int n = xs.length;
      for (int i = 0; i < n; i++) {
        order[i] = i;
      }
      // candidates for the upper end U of a Hall interval
      long[] maxima = new long[n];
      for (int i = 0; i < n; i++) {
        maxima[i] = xs[i].max;
      }
      Arrays.sort(maxima);
      Arrays.sort(order, (a, b) -> Long.compare(xs[b].min, xs[a].min));
      long previousU = Long.MIN_VALUE;
      for (int u = 0; u < n; u++) {
        long upper = maxima[u];
        if (u > 0 && upper == previousU) {
          continue;
        }
        previousU = upper;
        int count = 0;
        for (int k = 0; k < n; k++) {
          CpVar x = xs[order[k]];
          if (x.max > upper) {
            continue;
          }
          count++;
          long lower = x.min;
          long width = upper - lower + 1;
          if (count > width) {
            throw CpFail.INSTANCE;
          }
          if (count == width) {
            // [lower, upper] is a Hall interval: no other variable may take a value in it
            boolean pruned = false;
            for (int j = 0; j < n; j++) {
              CpVar y = xs[j];
              if (y.min >= lower && y.max <= upper) {
                continue;
              }
              if (y.min >= lower && y.min <= upper) {
                y.setMin(upper + 1);
                pruned = true;
              }
              if (y.max >= lower && y.max <= upper) {
                y.setMax(lower - 1);
                pruned = true;
              }
            }
            if (pruned) {
              xs[0].store.enqueue(this);
              return;
            }
          }
        }
      }
    }
  }

  /** <code>z == x * y</code>. */
  public static final class Product extends Propagator {
    private final CpVar x;

    private final CpVar y;

    private final CpVar z;

    /** Creates the propagator. */
    public Product(CpVar x, CpVar y, CpVar z) {
      super(x, y, z);
      this.x = x;
      this.y = y;
      this.z = z;
    }

    /** The bounds of the product of the ranges of <code>x</code> and <code>y</code>. */
    public static long[] bounds(CpVar x, CpVar y) {
      if (x == y) {
        long a = CapOps.capProd(x.min, x.min);
        long b = CapOps.capProd(x.max, x.max);
        long lo = (x.min <= 0 && x.max >= 0) ? 0 : Math.min(a, b);
        return new long[] {lo, Math.max(a, b)};
      }
      long p1 = CapOps.capProd(x.min, y.min);
      long p2 = CapOps.capProd(x.min, y.max);
      long p3 = CapOps.capProd(x.max, y.min);
      long p4 = CapOps.capProd(x.max, y.max);
      return new long[] {Math.min(Math.min(p1, p2), Math.min(p3, p4)),
          Math.max(Math.max(p1, p2), Math.max(p3, p4))};
    }

    @Override
    public void propagate() {
      for (int iteration = 0; iteration < 16; iteration++) {
        long[] b = bounds(x, y);
        z.setRange(b[0], b[1]);
        boolean changed = false;
        if (x == y) {
          if (!CapOps.isInfinite(z.max)) {
            long r = (long) Math.floor(Math.sqrt((double) Math.max(0, z.max)));
            while (r * r > z.max) {
              r--;
            }
            while ((r + 1) * (r + 1) <= z.max) {
              r++;
            }
            long oldMin = x.min;
            long oldMax = x.max;
            x.setRange(-r, r);
            changed = oldMin != x.min || oldMax != x.max;
          }
        } else {
          changed |= divide(x, z, y);
          changed |= divide(y, z, x);
        }
        if (x.isBound() && y.isBound()) {
          z.setValue(CapOps.capProd(x.min, y.min));
          return;
        }
        if (!changed) {
          return;
        }
      }
    }

    /** Restricts <code>target</code> to <code>num / den</code> when <code>den</code> excludes 0. */
    private static boolean divide(CpVar target, CpVar num, CpVar den) {
      if (den.min <= 0 && den.max >= 0) {
        if (den.isBound() && den.min == 0 && !num.contains(0)) {
          throw CpFail.INSTANCE;
        }
        return false;
      }
      if (CapOps.isInfinite(num.min) || CapOps.isInfinite(num.max)) {
        return false;
      }
      double q1 = (double) num.min / den.min;
      double q2 = (double) num.min / den.max;
      double q3 = (double) num.max / den.min;
      double q4 = (double) num.max / den.max;
      double lo = Math.min(Math.min(q1, q2), Math.min(q3, q4));
      double hi = Math.max(Math.max(q1, q2), Math.max(q3, q4));
      if (Math.abs(lo) > 1e15 || Math.abs(hi) > 1e15) {
        return false;
      }
      long l = (long) Math.ceil(lo - 1e-9);
      long u = (long) Math.floor(hi + 1e-9);
      long oldMin = target.min;
      long oldMax = target.max;
      target.setRange(l, u);
      return oldMin != target.min || oldMax != target.max;
    }
  }

  /** <code>z == |x|</code>. */
  public static final class Abs extends Propagator {
    private final CpVar x;

    private final CpVar z;

    /** Creates the propagator. */
    public Abs(CpVar x, CpVar z) {
      super(x, z);
      this.x = x;
      this.z = z;
    }

    /** The bounds of <code>|x|</code>. */
    public static long[] bounds(CpVar x) {
      long a = Math.abs(Math.max(x.min, -Long.MAX_VALUE));
      long b = Math.abs(Math.max(x.max, -Long.MAX_VALUE));
      long lo = x.min >= 0 ? x.min : (x.max <= 0 ? -x.max : 0);
      return new long[] {lo, Math.max(a, b)};
    }

    @Override
    public void propagate() {
      for (int iteration = 0; iteration < 8; iteration++) {
        long[] b = bounds(x);
        z.setRange(b[0], b[1]);
        long oldMin = x.min;
        long oldMax = x.max;
        x.setRange(-z.max, z.max);
        if (z.min > 0) {
          if (x.min > -z.min) {
            x.setMin(z.min);
          }
          if (x.max < z.min) {
            x.setMax(-z.min);
          }
          if (x.min > -z.min || x.max < z.min) {
            // covered above
          } else {
            x.removeInterval(-z.min + 1, z.min - 1);
          }
        }
        if (oldMin == x.min && oldMax == x.max) {
          return;
        }
      }
    }
  }

  /** <code>z == x / y</code> with the integer division of C++ (rounding towards zero). */
  public static final class Div extends Propagator {
    private final CpVar x;

    private final CpVar y;

    private final CpVar z;

    /** Creates the propagator. */
    public Div(CpVar x, CpVar y, CpVar z) {
      super(x, y, z);
      this.x = x;
      this.y = y;
      this.z = z;
    }

    /** The bounds of <code>x / y</code> or <code>null</code> if they can't be computed. */
    public static long[] bounds(CpVar x, CpVar y) {
      if (y.min <= 0 && y.max >= 0) {
        long m = Math.max(Math.abs(Math.max(x.min, -Long.MAX_VALUE)),
            Math.abs(Math.max(x.max, -Long.MAX_VALUE)));
        return new long[] {-m, m};
      }
      long q1 = x.min / y.min;
      long q2 = x.min / y.max;
      long q3 = x.max / y.min;
      long q4 = x.max / y.max;
      return new long[] {Math.min(Math.min(q1, q2), Math.min(q3, q4)),
          Math.max(Math.max(q1, q2), Math.max(q3, q4))};
    }

    @Override
    public void propagate() {
      y.removeValue(0);
      long[] b = bounds(x, y);
      z.setRange(b[0], b[1]);
      if (y.isBound() && y.min > 0 && x.min >= 0 && z.min >= 0) {
        long c = y.min;
        x.setRange(CapOps.capProd(z.min, c), CapOps.capAdd(CapOps.capProd(z.max, c), c - 1));
      }
      if (x.isBound() && y.isBound()) {
        z.setValue(x.min / y.min);
      }
    }
  }

  /** <code>z == x % y</code> with the remainder of C++ (sign of the dividend). */
  public static final class Mod extends Propagator {
    private final CpVar x;

    private final CpVar y;

    private final CpVar z;

    /** Creates the propagator. */
    public Mod(CpVar x, CpVar y, CpVar z) {
      super(x, y, z);
      this.x = x;
      this.y = y;
      this.z = z;
    }

    /** The bounds of <code>x % y</code>. */
    public static long[] bounds(CpVar x, CpVar y) {
      long m = Math.max(Math.abs(Math.max(y.min, -Long.MAX_VALUE)),
          Math.abs(Math.max(y.max, -Long.MAX_VALUE)));
      long lo = x.min >= 0 ? 0 : -(m - 1);
      long hi = x.max <= 0 ? 0 : m - 1;
      if (x.min >= 0) {
        hi = Math.min(hi, x.max);
      }
      if (x.max <= 0) {
        lo = Math.max(lo, x.min);
      }
      return new long[] {lo, hi};
    }

    @Override
    public void propagate() {
      y.removeValue(0);
      long[] b = bounds(x, y);
      z.setRange(b[0], b[1]);
      if (x.isBound() && y.isBound()) {
        z.setValue(x.min % y.min);
      }
    }
  }

  /** <code>z == min(xs)</code> or <code>z == max(xs)</code>. */
  public static final class MinMax extends Propagator {
    private final CpVar[] xs;

    private final CpVar z;

    private final boolean isMax;

    /** Creates the propagator. */
    public MinMax(CpVar[] xs, CpVar z, boolean isMax) {
      super(concat(xs, z));
      this.xs = xs.clone();
      this.z = z;
      this.isMax = isMax;
    }

    /** The bounds of <code>min(xs)</code> or <code>max(xs)</code>. */
    public static long[] bounds(CpVar[] xs, boolean isMax) {
      long lo = isMax ? Long.MIN_VALUE : Long.MAX_VALUE;
      long hi = lo;
      for (CpVar x : xs) {
        if (isMax) {
          lo = Math.max(lo, x.min);
          hi = Math.max(hi, x.max);
        } else {
          lo = Math.min(lo, x.min);
          hi = Math.min(hi, x.max);
        }
      }
      return new long[] {lo, hi};
    }

    @Override
    public void propagate() {
      long[] b = bounds(xs, isMax);
      z.setRange(b[0], b[1]);
      if (isMax) {
        // every x <= z.max; if a single x can reach z.min it must
        CpVar candidate = null;
        int count = 0;
        for (CpVar x : xs) {
          x.setMax(z.max);
          if (x.max >= z.min) {
            candidate = x;
            count++;
          }
        }
        if (count == 0) {
          throw CpFail.INSTANCE;
        }
        if (count == 1) {
          candidate.setMin(z.min);
        }
      } else {
        CpVar candidate = null;
        int count = 0;
        for (CpVar x : xs) {
          x.setMin(z.min);
          if (x.min <= z.max) {
            candidate = x;
            count++;
          }
        }
        if (count == 0) {
          throw CpFail.INSTANCE;
        }
        if (count == 1) {
          candidate.setMax(z.max);
        }
      }
    }
  }

  /** <code>z == values[index]</code> for a table of constants. */
  public static final class Element extends Propagator {
    private final long[] values;

    private final CpVar index;

    private final CpVar z;

    /** Creates the propagator. */
    public Element(long[] values, CpVar index, CpVar z) {
      super(index, z);
      this.values = values.clone();
      this.index = index;
      this.z = z;
    }

    @Override
    public void propagate() {
      index.setRange(0, values.length - 1);
      long lo = Long.MAX_VALUE;
      long hi = Long.MIN_VALUE;
      for (long i = index.min; i <= index.max; i++) {
        if (!index.contains(i)) {
          continue;
        }
        long v = values[(int) i];
        if (!z.contains(v)) {
          index.removeValue(i);
          continue;
        }
        lo = Math.min(lo, v);
        hi = Math.max(hi, v);
      }
      if (lo > hi) {
        throw CpFail.INSTANCE;
      }
      z.setRange(lo, hi);
    }
  }

  /** <code>z == f(index)</code> for a function evaluated on the (small) index domain. */
  public static final class ElementFunction extends Propagator {
    private final LongUnaryOperator f;

    private final CpVar index;

    private final CpVar z;

    /** Creates the propagator. */
    public ElementFunction(LongUnaryOperator f, CpVar index, CpVar z) {
      super(index, z);
      this.f = f;
      this.index = index;
      this.z = z;
    }

    /** The bounds of <code>f</code> over the domain of <code>index</code>. */
    public static long[] bounds(LongUnaryOperator f, CpVar index) {
      checkSize(index);
      long lo = Long.MAX_VALUE;
      long hi = Long.MIN_VALUE;
      for (long i : index.values()) {
        long v = f.applyAsLong(i);
        lo = Math.min(lo, v);
        hi = Math.max(hi, v);
      }
      return new long[] {lo, hi};
    }

    @Override
    public void propagate() {
      long lo = Long.MAX_VALUE;
      long hi = Long.MIN_VALUE;
      for (long i : index.values()) {
        long v = f.applyAsLong(i);
        if (!z.contains(v)) {
          index.removeValue(i);
          continue;
        }
        lo = Math.min(lo, v);
        hi = Math.max(hi, v);
      }
      if (lo > hi) {
        throw CpFail.INSTANCE;
      }
      z.setRange(lo, hi);
    }
  }

  /** <code>z == f(i, j)</code> for a function evaluated on the (small) index domains. */
  public static final class ElementFunction2 extends Propagator {
    private final LongBinaryOperator f;

    private final CpVar i1;

    private final CpVar i2;

    private final CpVar z;

    /** Creates the propagator. */
    public ElementFunction2(LongBinaryOperator f, CpVar i1, CpVar i2, CpVar z) {
      super(i1, i2, z);
      this.f = f;
      this.i1 = i1;
      this.i2 = i2;
      this.z = z;
    }

    /** The bounds of <code>f</code> over the index domains. */
    public static long[] bounds(LongBinaryOperator f, CpVar i1, CpVar i2) {
      checkSize(i1);
      checkSize(i2);
      long lo = Long.MAX_VALUE;
      long hi = Long.MIN_VALUE;
      for (long a : i1.values()) {
        for (long b : i2.values()) {
          long v = f.applyAsLong(a, b);
          lo = Math.min(lo, v);
          hi = Math.max(hi, v);
        }
      }
      return new long[] {lo, hi};
    }

    @Override
    public void propagate() {
      long lo = Long.MAX_VALUE;
      long hi = Long.MIN_VALUE;
      long[] as = i1.values();
      long[] bs = i2.values();
      boolean[] supportB = new boolean[bs.length];
      for (long a : as) {
        boolean supported = false;
        for (int k = 0; k < bs.length; k++) {
          long v = f.applyAsLong(a, bs[k]);
          if (z.contains(v)) {
            supported = true;
            supportB[k] = true;
            lo = Math.min(lo, v);
            hi = Math.max(hi, v);
          }
        }
        if (!supported) {
          i1.removeValue(a);
        }
      }
      for (int k = 0; k < bs.length; k++) {
        if (!supportB[k]) {
          i2.removeValue(bs[k]);
        }
      }
      if (lo > hi) {
        throw CpFail.INSTANCE;
      }
      z.setRange(lo, hi);
    }
  }

  /** <code>z == xs[index]</code>. */
  public static final class ElementVar extends Propagator {
    private final CpVar[] xs;

    private final CpVar index;

    private final CpVar z;

    /** Creates the propagator. */
    public ElementVar(CpVar[] xs, CpVar index, CpVar z) {
      super(concat(concat(xs, index), z));
      this.xs = xs.clone();
      this.index = index;
      this.z = z;
    }

    @Override
    public void propagate() {
      index.setRange(0, xs.length - 1);
      long lo = Long.MAX_VALUE;
      long hi = Long.MIN_VALUE;
      for (long i : index.values()) {
        CpVar x = xs[(int) i];
        if (x.max < z.min || x.min > z.max) {
          index.removeValue(i);
          continue;
        }
        lo = Math.min(lo, x.min);
        hi = Math.max(hi, x.max);
      }
      if (lo > hi) {
        throw CpFail.INSTANCE;
      }
      z.setRange(lo, hi);
      if (index.isBound()) {
        CpVar x = xs[(int) index.min];
        x.setRange(z.min, z.max);
        z.setRange(x.min, x.max);
      }
    }
  }

  /** <code>b == (sum(coefs[i] * vars[i]) relation c)</code> with a 0/1 variable b. */
  public static final class Reified extends Propagator {
    private final long[] coefs;

    private final CpVar[] terms;

    private final Relation relation;

    private final long c;

    private final CpVar b;

    private final Propagator positive;

    private final Propagator negative;

    /** Creates the propagator. */
    public Reified(long[] coefs, CpVar[] vars, Relation relation, long c, CpVar b) {
      super(concat(vars, b));
      this.coefs = coefs.clone();
      this.terms = vars.clone();
      this.relation = relation;
      this.c = c;
      this.b = b;
      switch (relation) {
        case EQ:
          positive = new LinearPropagator(coefs, terms, c, c);
          negative = new NotEqual(coefs, terms, c);
          break;
        case NE:
          positive = new NotEqual(coefs, terms, c);
          negative = new LinearPropagator(coefs, terms, c, c);
          break;
        case LE:
          positive = new LinearPropagator(coefs, terms, Long.MIN_VALUE, c);
          negative = new LinearPropagator(coefs, terms, CapOps.capAdd(c, 1), Long.MAX_VALUE);
          break;
        default:
          positive = new LinearPropagator(coefs, terms, c, Long.MAX_VALUE);
          negative = new LinearPropagator(coefs, terms, Long.MIN_VALUE, CapOps.capSub(c, 1));
          break;
      }
    }

    @Override
    public void propagate() {
      b.setRange(0, 1);
      if (b.isBound()) {
        if (b.min == 1) {
          positive.propagate();
        } else {
          negative.propagate();
        }
        return;
      }
      long[] bounds = LinearPropagator.bounds(coefs, terms, 0);
      int truth = 0; // 1 entailed, -1 disentailed
      switch (relation) {
        case EQ:
        case NE: {
          boolean entailed = bounds[0] == c && bounds[1] == c;
          boolean disentailed = c < bounds[0] || c > bounds[1];
          if (!disentailed && terms.length == 1 && coefs[0] != 0) {
            disentailed = c % coefs[0] != 0 || !terms[0].contains(c / coefs[0]);
          }
          if (entailed) {
            truth = 1;
          } else if (disentailed) {
            truth = -1;
          }
          if (relation == Relation.NE) {
            truth = -truth;
          }
          break;
        }
        case LE:
          if (bounds[1] <= c && !CapOps.isInfinite(bounds[1])) {
            truth = 1;
          } else if (bounds[0] > c && !CapOps.isInfinite(bounds[0])) {
            truth = -1;
          }
          break;
        default:
          if (bounds[0] >= c && !CapOps.isInfinite(bounds[0])) {
            truth = 1;
          } else if (bounds[1] < c && !CapOps.isInfinite(bounds[1])) {
            truth = -1;
          }
          break;
      }
      if (truth == 1) {
        b.setValue(1);
      } else if (truth == -1) {
        b.setValue(0);
      }
    }
  }

  /** Restricts a variable to (or excludes it from) a set of values or intervals. */
  public static final class Member extends Propagator {
    private final CpVar x;

    private final long[] starts;

    private final long[] ends;

    private final boolean inside;

    /** <code>x</code> in (or not in, if <code>inside</code> is false) the union of intervals. */
    public Member(CpVar x, long[] starts, long[] ends, boolean inside) {
      super(x);
      this.x = x;
      Integer[] order = new Integer[starts.length];
      for (int i = 0; i < order.length; i++) {
        order[i] = i;
      }
      Arrays.sort(order, (a, b) -> Long.compare(starts[a], starts[b]));
      this.starts = new long[starts.length];
      this.ends = new long[starts.length];
      for (int i = 0; i < order.length; i++) {
        this.starts[i] = starts[order[i]];
        this.ends[i] = ends[order[i]];
      }
      this.inside = inside;
    }

    @Override
    public void propagate() {
      if (!inside) {
        for (int i = 0; i < starts.length; i++) {
          x.removeInterval(starts[i], ends[i]);
        }
        return;
      }
      if (starts.length == 0) {
        throw CpFail.INSTANCE;
      }
      long lo = starts[0];
      long hi = ends[0];
      for (int i = 1; i < starts.length; i++) {
        hi = Math.max(hi, ends[i]);
      }
      x.setRange(lo, hi);
      long covered = ends[0];
      for (int i = 1; i < starts.length; i++) {
        if (starts[i] > covered + 1) {
          x.removeInterval(covered + 1, starts[i] - 1);
        }
        covered = Math.max(covered, ends[i]);
      }
    }
  }

  /** <code>z == (cond ? expr : value)</code> with a 0/1 condition. */
  public static final class Conditional extends Propagator {
    private final CpVar cond;

    private final CpVar expr;

    private final long value;

    private final CpVar z;

    /** Creates the propagator. */
    public Conditional(CpVar cond, CpVar expr, long value, CpVar z) {
      super(cond, expr, z);
      this.cond = cond;
      this.expr = expr;
      this.value = value;
      this.z = z;
    }

    @Override
    public void propagate() {
      cond.setRange(0, 1);
      if (cond.isBound()) {
        if (cond.min == 1) {
          z.setRange(expr.min, expr.max);
          expr.setRange(z.min, z.max);
        } else {
          z.setValue(value);
        }
        return;
      }
      z.setRange(Math.min(expr.min, value), Math.max(expr.max, value));
      if (!z.contains(value)) {
        cond.setValue(1);
      } else if (expr.max < z.min || expr.min > z.max) {
        cond.setValue(0);
      }
    }
  }

  /** The number of <code>xs[i] == value</code> equals <code>count</code>. */
  public static final class Count extends Propagator {
    private final CpVar[] xs;

    private final long value;

    private final CpVar count;

    /** Creates the propagator. */
    public Count(CpVar[] xs, long value, CpVar count) {
      super(concat(xs, count));
      this.xs = xs.clone();
      this.value = value;
      this.count = count;
    }

    @Override
    public void propagate() {
      int bound = 0;
      int possible = 0;
      for (CpVar x : xs) {
        if (x.contains(value)) {
          possible++;
          if (x.isBound()) {
            bound++;
          }
        }
      }
      count.setRange(bound, possible);
      if (count.isBound()) {
        if (count.min == bound) {
          for (CpVar x : xs) {
            if (!x.isBound()) {
              x.removeValue(value);
            }
          }
        } else if (count.min == possible) {
          for (CpVar x : xs) {
            if (x.contains(value)) {
              x.setValue(value);
            }
          }
        }
      }
    }
  }

  /**
   * Time-table propagation of a cumulative resource: at every time the sum of the demands of the
   * performed tasks <code>[start, start + duration)</code> covering it is at most the capacity.
   */
  public static final class Cumulative extends Propagator {
    private final CpVar[] start;

    private final CpVar[] duration;

    private final CpVar[] performed;

    private final CpVar[] demand;

    private final CpVar capacity;

    /** Creates the propagator; <code>performed</code> entries may be bound to 1. */
    public Cumulative(CpVar[] start, CpVar[] duration, CpVar[] performed, CpVar[] demand,
        CpVar capacity) {
      super(concat(concat(concat(concat(start, duration), performed), demand), capacity));
      this.start = start.clone();
      this.duration = duration.clone();
      this.performed = performed.clone();
      this.demand = demand.clone();
      this.capacity = capacity;
    }

    @Override
    public void propagate() {
      final int n = start.length;
      for (int iteration = 0; iteration < 32; iteration++) {
        // compulsory parts of the tasks which must be performed
        long[] times = new long[2 * n];
        long[] deltas = new long[2 * n];
        int events = 0;
        for (int i = 0; i < n; i++) {
          if (performed[i].min == 1 && demand[i].min > 0) {
            long from = start[i].max;
            long to = CapOps.capAdd(start[i].min, duration[i].min);
            if (from < to) {
              times[events] = from;
              deltas[events++] = demand[i].min;
              times[events] = to;
              deltas[events++] = -demand[i].min;
            }
          }
        }
        Integer[] order = new Integer[events];
        for (int k = 0; k < events; k++) {
          order[k] = k;
        }
        final long[] t = times;
        final long[] d = deltas;
        Arrays.sort(order, (a, b) -> t[a] != t[b] ? Long.compare(t[a], t[b])
            : Long.compare(d[a], d[b]));
        // profile segments [segStart[k], segEnd[k]) with height segHeight[k]
        long[] segStart = new long[events];
        long[] segEnd = new long[events];
        long[] segHeight = new long[events];
        int segments = 0;
        long height = 0;
        for (int k = 0; k < events; k++) {
          int e = order[k];
          height += deltas[e];
          long from = times[e];
          long to = k + 1 < events ? times[order[k + 1]] : from;
          if (to > from && height > 0) {
            if (height > capacity.max) {
              throw CpFail.INSTANCE;
            }
            segStart[segments] = from;
            segEnd[segments] = to;
            segHeight[segments] = height;
            segments++;
          }
        }
        if (segments > 0 && capacity.min < maxHeight(segHeight, segments)) {
          capacity.setMin(maxHeight(segHeight, segments));
        }
        boolean changed = false;
        for (int i = 0; i < n; i++) {
          if (performed[i].max == 0 || demand[i].min <= 0 || duration[i].min <= 0) {
            continue;
          }
          long dur = duration[i].min;
          long dem = demand[i].min;
          long ownFrom = performed[i].min == 1 ? start[i].max : 0;
          long ownTo = performed[i].min == 1 ? CapOps.capAdd(start[i].min, dur) : 0;
          // push the earliest start over the overloaded segments
          long est = start[i].min;
          boolean moved = true;
          while (moved) {
            moved = false;
            for (int k = 0; k < segments; k++) {
              long h = segHeight[k];
              if (segStart[k] >= ownFrom && segEnd[k] <= ownTo) {
                h -= dem;
              }
              if (h + dem > capacity.max && segStart[k] < CapOps.capAdd(est, dur)
                  && segEnd[k] > est) {
                est = segEnd[k];
                moved = true;
              }
            }
          }
          long lst = start[i].max;
          moved = true;
          while (moved) {
            moved = false;
            for (int k = 0; k < segments; k++) {
              long h = segHeight[k];
              if (segStart[k] >= ownFrom && segEnd[k] <= ownTo) {
                h -= dem;
              }
              if (h + dem > capacity.max && segStart[k] < CapOps.capAdd(lst, dur)
                  && segEnd[k] > lst) {
                lst = CapOps.capSub(segStart[k], dur);
                moved = true;
              }
            }
          }
          if (est > start[i].min || lst < start[i].max) {
            if (est > lst) {
              if (performed[i].min == 1) {
                throw CpFail.INSTANCE;
              }
              performed[i].setValue(0);
              changed = true;
            } else if (performed[i].min == 1) {
              start[i].setRange(est, lst);
              changed = true;
            }
          }
        }
        if (!changed) {
          return;
        }
      }
    }

    private static long maxHeight(long[] heights, int count) {
      long m = 0;
      for (int k = 0; k < count; k++) {
        m = Math.max(m, heights[k]);
      }
      return m;
    }
  }

  static void checkSize(CpVar index) {
    if (index.size() > 10_000_000L) {
      throw new UnsupportedOperationException("element index domain too large");
    }
  }

  static CpVar[] concat(CpVar[] a, CpVar b) {
    CpVar[] r = Arrays.copyOf(a, a.length + 1);
    r[a.length] = b;
    return r;
  }

  static CpVar[] concat(CpVar[] a, CpVar[] b) {
    CpVar[] r = Arrays.copyOf(a, a.length + b.length);
    System.arraycopy(b, 0, r, a.length, b.length);
    return r;
  }
}

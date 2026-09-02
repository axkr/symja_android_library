package org.matheclipse.core.builtin;

import java.util.ArrayList;
import java.util.function.DoubleUnaryOperator;
import java.util.function.LongPredicate;
import org.hipparchus.distribution.IntegerDistribution;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.IntervalDataSym;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.statistics.IDiscreteDistribution;
import org.matheclipse.external.fastutil.longs.LongArrayList;

/**
 * Utility methods shared by the <code>Probability</code>, <code>NProbability</code>,
 * <code>Expectation</code> and <code>NExpectation</code> implementations in
 * {@link StatisticsFunctions}:
 *
 * <ul>
 * <li>conversion of a predicate on a scalar random variable into an {@link S#IntervalData} region,
 * <li>probability computation from the distributions CDF over such a region (symbolic and machine
 * numeric),
 * <li>numerically robust (overflow safe, compensated) summation over the support of discrete
 * distributions.
 * </ul>
 */
final class DistributionRegion {

  /** Maximum number of integer points enumerated term by term in exact symbolic sums. */
  static final int SYMBOLIC_ENUMERATION_LIMIT = 512;

  /** Maximum number of integer points enumerated term by term in machine numeric sums. */
  static final long NUMERIC_ENUMERATION_LIMIT = 250_000L;

  /** Probability mass allowed to be ignored in each tail of a truncated numeric summation. */
  private static final double TAIL_EPSILON = 1.0e-16;

  private DistributionRegion() {
    // no instances
  }

  /**
   * Convert a predicate on the scalar variable <code>x</code> into an {@link S#IntervalData} region
   * by structural analysis. Relational operators, <code>Inequality</code> and
   * <code>And</code>/<code>Or</code> combinations of them are supported; no algebraic solving (e.g.
   * {@link S#Reduce}) is attempted.
   *
   * @return {@link F#NIL} if the predicate cannot be converted exactly
   */
  static IAST regionFromPredicate(IExpr predicate, IExpr x) {
    if (predicate.isAST(S.And) || predicate.isAST(S.Or)) {
      IAST logical = (IAST) predicate;
      boolean intersect = predicate.isAST(S.And);
      IAST result = F.NIL;
      for (int i = 1; i < logical.size(); i++) {
        IAST region = regionFromPredicate(logical.get(i), x);
        if (region.isNIL()) {
          return F.NIL;
        }
        if (result.isNIL()) {
          result = region;
        } else {
          result = intersect ? IntervalDataSym.intersection(result, region)
              : IntervalDataSym.union(result, region);
        }
        if (!result.isIntervalData()) {
          return F.NIL;
        }
      }
      return result;
    }
    if (predicate.isRelational() && predicate.isAST()) {
      IAST region = IntervalDataSym.relationToIntervalSet((IAST) predicate, x);
      if (region.isPresent() && region.isIntervalData()) {
        return region;
      }
    }
    return F.NIL;
  }

  /**
   * Convert the predicate into a region with the help of {@link S#Reduce} and verify the result by
   * sampling, because <code>Reduce</code> does not solve all predicates correctly. Only intended
   * for machine numeric use.
   */
  static IAST regionFromPredicateViaReduce(IExpr predicate, IExpr x, EvalEngine engine) {
    IExpr reduced = engine.evaluate(F.Reduce(predicate, x));
    if (reduced.equals(predicate)) {
      return F.NIL;
    }
    if (reduced.isTrue() || reduced.isFalse()) {
      // Reduce is not reliable for every predicate - verify the constant result by sampling
      boolean expected = reduced.isTrue();
      double[] samples = {-1000.0, -10.0, -1.5, -0.5, 0.0, 0.5, 1.5, 10.0, 1000.0};
      try {
        for (double sample : samples) {
          if (engine.evalTrue(F.subst(predicate, x, F.num(sample))) != expected) {
            return F.NIL;
          }
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return F.NIL;
      }
      if (expected) {
        return F.IntervalData(F.List(F.CNInfinity, S.Less, S.Less, F.CInfinity));
      }
      return F.IntervalData();
    }
    IAST region = regionFromPredicate(reduced, x);
    if (region.isPresent() && validateRegion(region, predicate, x, engine)) {
      return region;
    }
    return F.NIL;
  }

  /**
   * Test numerically whether <code>value</code> lies in one of the region pieces. The region bounds
   * must be numerically evaluable.
   */
  private static boolean regionContains(IAST region, double value) {
    for (int i = 1; i < region.size(); i++) {
      IAST piece = (IAST) region.get(i);
      double min =
          piece.arg1().isNegativeInfinity() ? Double.NEGATIVE_INFINITY : piece.arg1().evalfNaN();
      double max = piece.arg4().isInfinity() ? Double.POSITIVE_INFINITY : piece.arg4().evalfNaN();
      boolean leftOk = piece.arg2() == S.LessEqual ? value >= min : value > min;
      boolean rightOk = piece.arg3() == S.LessEqual ? value <= max : value < max;
      if (leftOk && rightOk) {
        return true;
      }
    }
    return false;
  }

  /**
   * Verify by sampling around the region boundaries that the region describes the same set as the
   * original predicate.
   */
  static boolean validateRegion(IAST region, IExpr predicate, IExpr x, EvalEngine engine) {
    ArrayList<Double> samples = new ArrayList<Double>();
    for (int i = 1; i < region.size(); i++) {
      IAST piece = (IAST) region.get(i);
      double min =
          piece.arg1().isNegativeInfinity() ? Double.NEGATIVE_INFINITY : piece.arg1().evalfNaN();
      double max = piece.arg4().isInfinity() ? Double.POSITIVE_INFINITY : piece.arg4().evalfNaN();
      if (Double.isNaN(min) || Double.isNaN(max)) {
        return false;
      }
      double interior;
      if (Double.isInfinite(min) && Double.isInfinite(max)) {
        interior = 0.0;
      } else if (Double.isInfinite(min)) {
        interior = max - 1.0;
      } else if (Double.isInfinite(max)) {
        interior = min + 1.0;
      } else {
        interior = (min + max) / 2.0;
      }
      samples.add(interior);
      if (Double.isFinite(min)) {
        samples.add(min - sampleDelta(min));
      }
      if (Double.isFinite(max)) {
        samples.add(max + sampleDelta(max));
      }
    }
    try {
      for (double sample : samples) {
        boolean predicateTrue = engine.evalTrue(F.subst(predicate, x, F.num(sample)));
        if (predicateTrue != regionContains(region, sample)) {
          return false;
        }
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return false;
    }
    return true;
  }

  private static double sampleDelta(double bound) {
    return Math.max(1.0e-6, Math.abs(bound) * 1.0e-6);
  }

  /**
   * Convert the region into inclusive integer windows <code>{lo1, hi1, lo2, hi2, ...}</code>.
   * <code>Long.MIN_VALUE</code>/<code>Long.MAX_VALUE</code> mark unbounded ends. Empty pieces are
   * dropped; the windows keep the ascending region order.
   *
   * @return <code>null</code> if an endpoint cannot be converted
   */
  static long[] integerWindows(IAST region, EvalEngine engine) {
    LongArrayList windows = new LongArrayList();
    for (int i = 1; i < region.size(); i++) {
      IAST piece = (IAST) region.get(i);
      long lo;
      IExpr min = piece.arg1();
      if (min.isNegativeInfinity()) {
        lo = Long.MIN_VALUE;
      } else if (min.isInteger()) {
        lo = min.toLongDefault();
        if (lo == Long.MIN_VALUE) {
          return null;
        }
        if (piece.arg2() == S.Less) {
          lo++;
        }
      } else {
        lo = engine.evaluate(F.Ceiling(min)).toLongDefault();
        if (lo == Long.MIN_VALUE) {
          return null;
        }
      }
      long hi;
      IExpr max = piece.arg4();
      if (max.isInfinity()) {
        hi = Long.MAX_VALUE;
      } else if (max.isInteger()) {
        hi = max.toLongDefault();
        if (hi == Long.MIN_VALUE) {
          return null;
        }
        if (piece.arg3() == S.Less) {
          hi--;
        }
      } else {
        hi = engine.evaluate(F.Floor(max)).toLongDefault();
        if (hi == Long.MIN_VALUE) {
          return null;
        }
      }
      if (lo <= hi) {
        windows.add(lo);
        windows.add(hi);
      }
    }
    return windows.toLongArray();
  }

  /** Intersect every window with <code>[lo, hi]</code>. */
  static long[] clampWindows(long[] windows, long lo, long hi) {
    LongArrayList clamped = new LongArrayList();
    for (int i = 0; i < windows.length; i += 2) {
      long min = Math.max(windows[i], lo);
      long max = Math.min(windows[i + 1], hi);
      if (min <= max) {
        clamped.add(min);
        clamped.add(max);
      }
    }
    return clamped.toLongArray();
  }

  /** Intersection of two ascending disjoint window lists. */
  static long[] intersectWindows(long[] first, long[] second) {
    LongArrayList result = new LongArrayList();
    int i = 0;
    int j = 0;
    while (i < first.length && j < second.length) {
      long lo = Math.max(first[i], second[j]);
      long hi = Math.min(first[i + 1], second[j + 1]);
      if (lo <= hi) {
        result.add(lo);
        result.add(hi);
      }
      if (first[i + 1] < second[j + 1]) {
        i += 2;
      } else {
        j += 2;
      }
    }
    return result.toLongArray();
  }

  /**
   * The complement of the (ascending, disjoint) windows within <code>[lo, hi]</code>.
   */
  static long[] complementWindows(long[] windows, long lo, long hi) {
    LongArrayList complement = new LongArrayList();
    long cursor = lo;
    for (int i = 0; i < windows.length; i += 2) {
      long min = Math.max(windows[i], lo);
      long max = Math.min(windows[i + 1], hi);
      if (min > max) {
        continue;
      }
      if (cursor < min) {
        complement.add(cursor);
        complement.add(min - 1);
      }
      if (max == Long.MAX_VALUE) {
        return complement.toLongArray();
      }
      cursor = Math.max(cursor, max + 1);
    }
    if (cursor <= hi) {
      complement.add(cursor);
      complement.add(hi);
    }
    return complement.toLongArray();
  }

  /**
   * Total number of integer points covered by the windows; <code>Long.MAX_VALUE</code> if unbounded
   * or too large.
   */
  static long countWindows(long[] windows) {
    long total = 0;
    for (int i = 0; i < windows.length; i += 2) {
      if (windows[i] == Long.MIN_VALUE || windows[i + 1] == Long.MAX_VALUE) {
        return Long.MAX_VALUE;
      }
      long count = windows[i + 1] - windows[i] + 1;
      if (count <= 0) {
        return Long.MAX_VALUE;
      }
      total += count;
      if (total < 0) {
        return Long.MAX_VALUE;
      }
    }
    return total;
  }

  /** The distributions support lower bound widened to <code>long</code> sentinel values. */
  static long supportLowerBound(IDiscreteDistribution dist, IExpr distribution) {
    int bound = dist.getSupportLowerBound(distribution);
    return bound == Integer.MIN_VALUE ? Long.MIN_VALUE : bound;
  }

  /** The distributions support upper bound widened to <code>long</code> sentinel values. */
  static long supportUpperBound(IDiscreteDistribution dist, IExpr distribution) {
    int bound = dist.getSupportUpperBound(distribution);
    return bound == Integer.MAX_VALUE ? Long.MAX_VALUE : bound;
  }

  /**
   * Evaluate <code>CDF(distribution, value)</code>; only accept results where the CDF and all
   * <code>Piecewise</code> conditions could be resolved.
   */
  static IExpr cdfExact(IExpr distribution, IExpr value, EvalEngine engine) {
    IExpr cdf = engine.evaluate(F.CDF(distribution, value));
    if (cdf.isFree(S.CDF, true) && cdf.isFree(S.Piecewise, true)) {
      return cdf;
    }
    return F.NIL;
  }

  /**
   * Evaluate <code>CDF(distribution, value)</code> as a machine number.
   *
   * @return <code>Double.NaN</code> if no real number in <code>[0, 1]</code> was computed
   */
  static double cdfNumeric(IExpr distribution, IExpr value, EvalEngine engine) {
    try {
      IExpr cdf = engine.evalN(F.CDF(distribution, value));
      if (cdf.isReal()) {
        double result = cdf.evalfNaN();
        if (result >= -1.0e-6 && result <= 1.0 + 1.0e-6) {
          return Math.min(1.0, Math.max(0.0, result));
        }
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return Double.NaN;
  }

  /**
   * Exact probability of the integer windows as a telescoping sum of CDF differences
   * <code>P(lo &lt;= X &lt;= hi) == CDF(hi) - CDF(lo - 1)</code>.
   */
  static IExpr probabilityFromCDFDiscrete(IExpr distribution, long[] windows, EvalEngine engine) {
    IASTAppendable sum = F.PlusAlloc(windows.length / 2 + 1);
    for (int i = 0; i < windows.length; i += 2) {
      long lo = windows[i];
      long hi = windows[i + 1];
      IExpr upper = hi == Long.MAX_VALUE ? F.C1 : cdfExact(distribution, F.ZZ(hi), engine);
      IExpr lower = lo == Long.MIN_VALUE ? F.C0 : cdfExact(distribution, F.ZZ(lo - 1), engine);
      if (upper.isNIL() || lower.isNIL()) {
        return F.NIL;
      }
      sum.append(F.Subtract(upper, lower));
    }
    return engine.evaluate(sum);
  }

  /**
   * Exact probability of the region as a sum of CDF differences over the region pieces. For
   * comparability with the integration based results <code>Erfc(z)</code> is rewritten as
   * <code>1 - Erf(z)</code>.
   */
  static IExpr probabilityFromCDFContinuous(IExpr distribution, IAST region, EvalEngine engine) {
    IASTAppendable sum = F.PlusAlloc(region.argSize() + 1);
    for (int i = 1; i < region.size(); i++) {
      IAST piece = (IAST) region.get(i);
      IExpr min = piece.arg1();
      IExpr max = piece.arg4();
      IExpr upper = max.isInfinity() ? F.C1 : cdfExact(distribution, max, engine);
      IExpr lower = min.isNegativeInfinity() ? F.C0 : cdfExact(distribution, min, engine);
      if (upper.isNIL() || lower.isNIL()) {
        return F.NIL;
      }
      sum.append(F.Subtract(upper, lower));
    }
    IExpr result =
        F.subst(sum, e -> e.isAST(S.Erfc, 2) ? F.Subtract(F.C1, F.Erf(e.first())) : F.NIL);
    // expand to combine the constant terms of the CDF differences
    return engine.evaluate(F.Expand(result));
  }

  /**
   * Machine probability of the region from numeric CDF differences.
   *
   * @return <code>Double.NaN</code> if a CDF value could not be computed
   */
  static double probabilityNumericContinuous(IExpr distribution, IAST region, EvalEngine engine) {
    double total = 0.0;
    for (int i = 1; i < region.size(); i++) {
      IAST piece = (IAST) region.get(i);
      IExpr min = piece.arg1();
      IExpr max = piece.arg4();
      double upper = max.isInfinity() ? 1.0 : cdfNumeric(distribution, max, engine);
      double lower = min.isNegativeInfinity() ? 0.0 : cdfNumeric(distribution, min, engine);
      if (Double.isNaN(upper) || Double.isNaN(lower)) {
        return Double.NaN;
      }
      total += upper - lower;
    }
    return total;
  }

  /**
   * Machine probability of the integer windows from numeric CDF differences.
   *
   * @return <code>Double.NaN</code> if a CDF value could not be computed
   */
  static double probabilityNumericDiscrete(IExpr distribution, long[] windows, EvalEngine engine) {
    double total = 0.0;
    for (int i = 0; i < windows.length; i += 2) {
      long lo = windows[i];
      long hi = windows[i + 1];
      double upper = hi == Long.MAX_VALUE ? 1.0 : cdfNumeric(distribution, F.ZZ(hi), engine);
      double lower = lo == Long.MIN_VALUE ? 0.0 : cdfNumeric(distribution, F.ZZ(lo - 1), engine);
      if (Double.isNaN(upper) || Double.isNaN(lower)) {
        return Double.NaN;
      }
      total += upper - lower;
    }
    return total;
  }

  /**
   * Map a symja distribution AST with machine numeric parameters onto the corresponding
   * (numerically stable) hipparchus distribution.
   *
   * @return <code>null</code> if there is no hipparchus implementation or a parameter is not
   *         machine numeric
   */
  static IntegerDistribution hipparchusDiscrete(IExpr distribution) {
    if (!distribution.isAST()) {
      return null;
    }
    IAST dist = (IAST) distribution;
    try {
      switch (dist.headID()) {
        case ID.BernoulliDistribution:
          if (dist.isAST1()) {
            double p = dist.arg1().evalfNaN();
            if (p >= 0.0 && p <= 1.0) {
              return new org.hipparchus.distribution.discrete.BinomialDistribution(1, p);
            }
          }
          break;
        case ID.BinomialDistribution:
          if (dist.isAST2()) {
            int n = dist.arg1().toIntDefault();
            double p = dist.arg2().evalfNaN();
            if (n >= 0 && p >= 0.0 && p <= 1.0) {
              return new org.hipparchus.distribution.discrete.BinomialDistribution(n, p);
            }
          }
          break;
        case ID.DiscreteUniformDistribution:
          if (dist.isAST1() && dist.arg1().isList2()) {
            IAST minMax = (IAST) dist.arg1();
            int min = minMax.arg1().toIntDefault();
            int max = minMax.arg2().toIntDefault();
            if (min != Integer.MIN_VALUE && max != Integer.MIN_VALUE && min <= max) {
              return new org.hipparchus.distribution.discrete.UniformIntegerDistribution(min, max);
            }
          }
          break;
        case ID.GeometricDistribution:
          if (dist.isAST1()) {
            double p = dist.arg1().evalfNaN();
            if (p > 0.0 && p <= 1.0) {
              return new org.hipparchus.distribution.discrete.GeometricDistribution(p);
            }
          }
          break;
        case ID.HypergeometricDistribution:
          if (dist.isAST3()) {
            int n = dist.arg1().toIntDefault();
            int ns = dist.arg2().toIntDefault();
            int nt = dist.arg3().toIntDefault();
            if (n >= 0 && ns >= 0 && nt > 0 && n <= nt && ns <= nt) {
              return new org.hipparchus.distribution.discrete.HypergeometricDistribution(nt, ns, n);
            }
          }
          break;
        case ID.PoissonDistribution:
          if (dist.isAST1()) {
            double mean = dist.arg1().evalfNaN();
            if (mean > 0.0) {
              return new org.hipparchus.distribution.discrete.PoissonDistribution(mean);
            }
          }
          break;
        case ID.ZipfDistribution:
          if (dist.isAST2()) {
            int n = dist.arg1().toIntDefault();
            double exponent = dist.arg2().evalfNaN() + 1.0;
            if (n >= 1 && exponent > 0.0 && Double.isFinite(exponent)) {
              return new org.hipparchus.distribution.discrete.ZipfDistribution(n, exponent);
            }
          }
          break;
        default:
          break;
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return null;
  }

  /**
   * Machine probability of the integer windows as cumulative probability differences of the
   * hipparchus distribution.
   */
  static double windowsProbability(IntegerDistribution hipparchusDistribution, long[] windows) {
    double total = 0.0;
    for (int i = 0; i < windows.length; i += 2) {
      long lo = windows[i];
      long hi = windows[i + 1];
      double upper = hi >= Integer.MAX_VALUE - 1L ? 1.0
          : hipparchusDistribution.cumulativeProbability((int) hi);
      double lower = lo <= Integer.MIN_VALUE + 1L ? 0.0
          : hipparchusDistribution.cumulativeProbability((int) (lo - 1));
      if (Double.isNaN(upper) || Double.isNaN(lower)) {
        return Double.NaN;
      }
      total += upper - lower;
    }
    return Math.min(1.0, Math.max(0.0, total));
  }

  /**
   * A window <code>{lo, hi}</code> which covers all but roughly <code>2*TAIL_EPSILON</code> of the
   * probability mass of the distribution.
   */
  static long[] quantileWindow(IntegerDistribution hipparchusDistribution) {
    try {
      long lo = hipparchusDistribution.inverseCumulativeProbability(TAIL_EPSILON);
      long hi = hipparchusDistribution.inverseCumulativeProbability(1.0 - TAIL_EPSILON);
      return new long[] {lo - 1, hi + 1};
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return null;
    }
  }

  /**
   * Compensated (Kahan) summation of <code>term(j)</code> over all integer points of the windows.
   *
   * @param filter optional restriction of the summation points; may be <code>null</code>
   * @return <code>Double.NaN</code> if a term is not finite or the windows are unbounded
   */
  static double kahanSum(DoubleUnaryOperator term, LongPredicate filter, long[] windows) {
    if (countWindows(windows) > NUMERIC_ENUMERATION_LIMIT) {
      return Double.NaN;
    }
    double sum = 0.0;
    double compensation = 0.0;
    for (int i = 0; i < windows.length; i += 2) {
      for (long j = windows[i]; j <= windows[i + 1]; j++) {
        if (filter != null && !filter.test(j)) {
          continue;
        }
        double value = term.applyAsDouble(j);
        if (Double.isNaN(value)) {
          return Double.NaN;
        }
        if (value == 0.0) {
          continue;
        }
        if (!Double.isFinite(value)) {
          return Double.NaN;
        }
        double y = value - compensation;
        double t = sum + y;
        compensation = (t - sum) - y;
        sum = t;
      }
    }
    return sum;
  }

  /**
   * Machine expectation <code>E[f(X)]</code> of a discrete hipparchus distribution: sum over the
   * quantile window, then adaptively extend the upper tail until its contribution is negligible.
   * This also protects against <code>f</code> growing faster than the probability mass decays: in
   * that (divergent) case <code>Double.NaN</code> is returned.
   */
  static double expectationNumeric(IntegerDistribution hipparchusDistribution,
      DoubleUnaryOperator function, long supportLo, long supportHi) {
    long[] window = quantileWindow(hipparchusDistribution);
    if (window == null || supportLo == Long.MIN_VALUE) {
      return Double.NaN;
    }
    long lo = Math.max(supportLo, window[0]);
    long hi = Math.min(supportHi, window[1]);
    if (lo > hi) {
      return Double.NaN;
    }
    DoubleUnaryOperator term = j -> {
      double probability = hipparchusDistribution.probability((int) j);
      return probability == 0.0 ? 0.0 : function.applyAsDouble(j) * probability;
    };
    double sum = kahanSum(term, null, new long[] {lo, hi});
    if (Double.isNaN(sum)) {
      return Double.NaN;
    }
    // extend the upper tail adaptively as long as it contributes
    long blockSize = Math.max(16L, (hi - lo + 1L) / 4L);
    long cursor = hi + 1;
    int block = 0;
    while (cursor <= supportHi && cursor <= Integer.MAX_VALUE - 2L) {
      if (block++ >= 12) {
        // the tail did not converge - assume a divergent expectation
        return Double.NaN;
      }
      long blockEnd = Math.min(supportHi, cursor + blockSize - 1);
      blockEnd = Math.min(blockEnd, Integer.MAX_VALUE - 2L);
      double delta = kahanSum(term, null, new long[] {cursor, blockEnd});
      if (Double.isNaN(delta)) {
        return Double.NaN;
      }
      sum += delta;
      if (Math.abs(delta) <= 1.0e-12 * (Math.abs(sum) + 1.0e-12)) {
        return sum;
      }
      cursor = blockEnd + 1;
      blockSize *= 2;
    }
    return sum;
  }

  /**
   * Compile <code>function</code> into a machine numeric unary function of <code>variable</code>.
   * Evaluation failures yield <code>Double.NaN</code>.
   *
   * @return <code>null</code> if <code>variable</code> is not a symbol
   */
  static DoubleUnaryOperator compile(IExpr function, IExpr variable, EvalEngine engine) {
    if (!variable.isSymbol()) {
      return null;
    }
    UnaryNumerical unary = new UnaryNumerical(function, (ISymbol) variable, Double.NaN, engine);
    return unary;
  }

  /**
   * Exact symbolic sum of <code>termTemplate</code> substituted at every integer point of the
   * windows, optionally filtered by the predicate. The caller must check the window count against
   * {@link #SYMBOLIC_ENUMERATION_LIMIT} in advance.
   *
   * @return {@link F#NIL} if the predicate is undecidable at one of the points (e.g. it contains
   *         additional symbolic parameters)
   */
  static IExpr enumerateSymbolic(IExpr termTemplate, IExpr x, long[] windows, IExpr predicate,
      EvalEngine engine) {
    long count = countWindows(windows);
    if (count > SYMBOLIC_ENUMERATION_LIMIT) {
      return F.NIL;
    }
    IASTAppendable sum = F.PlusAlloc((int) count + 1);
    for (int i = 0; i < windows.length; i += 2) {
      for (long j = windows[i]; j <= windows[i + 1]; j++) {
        IExpr point = F.ZZ(j);
        if (predicate.isPresent()) {
          IExpr truth = definiteTruthValue(predicate, x, j, engine);
          if (truth.isNIL()) {
            return F.NIL;
          }
          if (truth.isFalse()) {
            continue;
          }
        }
        sum.append(F.subst(termTemplate, x, point));
      }
    }
    return engine.evaluate(sum);
  }

  /**
   * Evaluate the predicate at the integer point, falling back to machine numeric evaluation for
   * expressions like <code>E^1000 &lt; 3</code> which stay unevaluated in exact mode.
   *
   * @return {@link S#True}, {@link S#False} or {@link F#NIL} if undecidable
   */
  static IExpr definiteTruthValue(IExpr predicate, IExpr x, long point, EvalEngine engine) {
    IExpr test = engine.evaluate(F.subst(predicate, x, F.ZZ(point)));
    if (test.isTrue() || test.isFalse()) {
      return test;
    }
    try {
      test = engine.evalN(test);
      if (test.isTrue() || test.isFalse()) {
        return test;
      }
      // a positive magnitude beyond the double range appears as Overflow()
      IExpr replaced = F.subst(test, e -> e.isAST(S.Overflow, 1) ? F.CInfinity : F.NIL);
      if (replaced != test) {
        test = engine.evaluate(replaced);
        if (test.isTrue() || test.isFalse()) {
          return test;
        }
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return F.NIL;
  }
}

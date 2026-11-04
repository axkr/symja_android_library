package org.matheclipse.core.reflection.system;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;

/**
 * <code>FindDivisions({xmin, xmax}, n)</code> - about <code>n</code> equally spaced points with a
 * nice step <code>1</code>, <code>2</code>, <code>2.5</code> or <code>5</code> times a power of
 * <code>10</code>, which cover the interval. <code>FindDivisions({xmin, xmax, dx}, n)</code> uses
 * multiples of <code>dx</code>, <code>FindDivisions({xmin, xmax}, n, k)</code> a step which is a
 * power of <code>k</code> and <code>FindDivisions({xmin, xmax}, {n1, n2, ...})</code> divides every
 * interval of the previous level again.
 */
public class FindDivisions extends AbstractFunctionEvaluator {

  /** The most points of one level. */
  private static final int MAX_POINTS = 100_000;

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr range = ast.arg1();
    if (!range.isList() || range.argSize() < 2 || range.argSize() > 3) {
      return F.NIL;
    }
    int base = 10;
    if (ast.isAST3()) {
      base = ast.arg3().toIntDefault();
      if (base < 2) {
        return F.NIL;
      }
    }
    boolean inexact = false;
    IRational min = toRational(range.first());
    IRational max = toRational(range.second());
    if (min == null || max == null || !min.isLT(max)) {
      return F.NIL;
    }
    inexact = range.first().isInexactNumber() || range.second().isInexactNumber();
    IExpr result = F.NIL;
    if (range.argSize() == 3) {
      IExpr unit = range.getAt(3);
      int n = ast.arg2().toIntDefault();
      if (n <= 0 || base != 10) {
        return F.NIL;
      }
      IRational rationalUnit = toRational(unit);
      if (rationalUnit != null) {
        if (!rationalUnit.isPositive()) {
          return F.NIL;
        }
        IRational step = multipleStep(min, max, n, rationalUnit);
        result = step == null ? F.NIL : points(min, max, step);
      } else {
        // a symbolic unit like Pi: the points are integer multiples of the unit
        double d = unit.evalfNaN();
        if (!(d > 0.0)) {
          return F.NIL;
        }
        double lo = min.doubleValue() / d;
        double hi = max.doubleValue() / d;
        IRational nice = niceStep((hi - lo) / n);
        if (nice == null) {
          return F.NIL;
        }
        BigInteger m = roundHalfEvenAtLeast1(nice);
        long first = (long) Math.floor(lo / m.doubleValue());
        long last = (long) Math.ceil(hi / m.doubleValue());
        if (last < first || last - first > MAX_POINTS) {
          return F.NIL;
        }
        IASTAppendable list = F.ListAlloc((int) (last - first + 1));
        for (long i = first; i <= last; i++) {
          list.append(engine.evaluate(F.Times(F.ZZ(BigInteger.valueOf(i).multiply(m)), unit)));
        }
        result = list;
      }
    } else if (ast.arg2().isList()) {
      if (base != 10 || ast.arg2().argSize() == 0) {
        return F.NIL;
      }
      int[] counts = new int[ast.arg2().argSize()];
      for (int i = 0; i < counts.length; i++) {
        counts[i] = ast.arg2().getAt(i + 1).toIntDefault();
        if (counts[i] <= 0) {
          return F.NIL;
        }
      }
      result = nested(min, max, counts, 0, engine);
    } else {
      int n = ast.arg2().toIntDefault();
      if (n <= 0) {
        return F.NIL;
      }
      double raw = max.subtract(min).doubleValue() / n;
      IRational step = base == 10 ? niceStep(raw) : basePowerStep(raw, base);
      result = step == null ? F.NIL : points(min, max, step);
    }
    if (result.isPresent() && inexact && !isIntegerArray(result)) {
      return engine.evalN(result);
    }
    return result;
  }

  /** Points which are all integers stay exact also for an inexact interval. */
  private static boolean isIntegerArray(IExpr expr) {
    if (expr.isList()) {
      return ((IAST) expr).forAll(FindDivisions::isIntegerArray);
    }
    return expr.isInteger();
  }

  /** The levels of the nested divisions with the counts from <code>index</code> on. */
  private static IExpr nested(IRational min, IRational max, int[] counts, int index,
      EvalEngine engine) {
    engine.checkTimeLimit();
    IRational step = niceStep(max.subtract(min).doubleValue() / counts[index]);
    IExpr major = step == null ? F.NIL : points(min, max, step);
    if (major.isNIL()) {
      return F.NIL;
    }
    IASTAppendable levels = F.ListAlloc(counts.length - index);
    levels.append(major);
    if (index == counts.length - 1) {
      return levels;
    }
    IAST majorList = (IAST) major;
    IASTAppendable[] subLevels = new IASTAppendable[counts.length - index - 1];
    for (int k = 0; k < subLevels.length; k++) {
      subLevels[k] = F.ListAlloc(majorList.argSize());
    }
    for (int i = 1; i < majorList.argSize(); i++) {
      IExpr sub = nested((IRational) majorList.get(i), (IRational) majorList.get(i + 1), counts,
          index + 1, engine);
      if (sub.isNIL()) {
        return F.NIL;
      }
      for (int k = 0; k < subLevels.length; k++) {
        subLevels[k].append(sub.getAt(k + 1));
      }
    }
    for (IASTAppendable level : subLevels) {
      levels.append(level);
    }
    return levels;
  }

  /** The step for the points which are multiples of the rational <code>unit</code>. */
  private static IRational multipleStep(IRational min, IRational max, int n, IRational unit) {
    double units = max.subtract(min).doubleValue() / n / unit.doubleValue();
    IRational nice = niceStep(units);
    if (nice == null) {
      return null;
    }
    return unit.multiply(F.ZZ(roundHalfEvenAtLeast1(nice)));
  }

  /** The multiples of <code>step</code> from below <code>min</code> to above <code>max</code>. */
  private static IExpr points(IRational min, IRational max, IRational step) {
    BigInteger first = floor(min.divideBy(step));
    BigInteger last = floor(max.divideBy(step).negate()).negate();
    if (last.compareTo(first) < 0
        || last.subtract(first).compareTo(BigInteger.valueOf(MAX_POINTS)) > 0) {
      return F.NIL;
    }
    int size = last.subtract(first).intValue() + 1;
    IASTAppendable list = F.ListAlloc(size);
    for (int i = 0; i < size; i++) {
      list.append(step.multiply(F.ZZ(first.add(BigInteger.valueOf(i)))));
    }
    return list;
  }

  private static BigInteger floor(IRational r) {
    BigInteger[] qr = r.toBigNumerator().divideAndRemainder(r.toBigDenominator());
    return qr[1].signum() < 0 ? qr[0].subtract(BigInteger.ONE) : qr[0];
  }

  /**
   * The nice step <code>1</code>, <code>2</code>, <code>5/2</code>, <code>5</code> times a power of
   * <code>10</code> which is the smallest one not below <code>raw</code>.
   */
  private static IRational niceStep(double raw) {
    if (!(raw > 0.0) || !Double.isFinite(raw)) {
      return null;
    }
    int k = (int) Math.floor(Math.log10(raw));
    final double eps = 1.0 + 1.0e-9;
    while (Math.pow(10.0, k) > raw * eps) {
      k--;
    }
    while (Math.pow(10.0, k + 1) <= raw * eps) {
      k++;
    }
    double mantissa = raw / Math.pow(10.0, k);
    IRational nice;
    if (mantissa <= eps) {
      nice = F.C1;
    } else if (mantissa <= 2.0 * eps) {
      nice = F.C2;
    } else if (mantissa <= 2.5 * eps) {
      nice = F.QQ(5, 2);
    } else if (mantissa <= 5.0 * eps) {
      nice = F.C5;
    } else {
      nice = F.C10;
    }
    if (Math.abs(k) > 300) {
      return null;
    }
    IInteger power = F.ZZ(BigInteger.TEN.pow(Math.abs(k)));
    return k >= 0 ? nice.multiply(power) : nice.divideBy(power);
  }

  /** The power of <code>base</code> which is nearest to <code>raw</code> on a log scale. */
  private static IRational basePowerStep(double raw, int base) {
    if (!(raw > 0.0) || !Double.isFinite(raw)) {
      return null;
    }
    long k = Math.round(Math.log(raw) / Math.log(base));
    if (Math.abs(k) > 1000) {
      return null;
    }
    IInteger power = F.ZZ(BigInteger.valueOf(base).pow((int) Math.abs(k)));
    return k >= 0 ? power : F.C1.divideBy(power);
  }

  /** <code>r</code> rounded to an integer, half to even, but at least <code>1</code>. */
  private static BigInteger roundHalfEvenAtLeast1(IRational r) {
    BigDecimal value = new BigDecimal(r.toBigNumerator())
        .divide(new BigDecimal(r.toBigDenominator()), 0, java.math.RoundingMode.HALF_EVEN);
    BigInteger m = value.toBigInteger();
    return m.signum() > 0 ? m : BigInteger.ONE;
  }

  /** A rational number or a machine number as its exact rational value, otherwise null. */
  private static IRational toRational(IExpr x) {
    if (x.isRational()) {
      return (IRational) x;
    }
    if (x.isReal() && x.isNumber()) {
      double value = x.evalfNaN();
      if (!Double.isFinite(value)) {
        return null;
      }
      BigDecimal decimal = new BigDecimal(Double.toString(value));
      int scale = Math.max(decimal.scale(), 0);
      return F.QQ(F.ZZ(decimal.movePointRight(scale).toBigIntegerExact()),
          F.ZZ(BigInteger.TEN.pow(scale)));
    }
    return null;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_3;
  }
}

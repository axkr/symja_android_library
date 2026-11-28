package org.matheclipse.core.builtin;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import org.matheclipse.core.annotation.AllowNIL;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Functions with a fractal structure: <code>CantorStaircase</code>,
 * <code>MinkowskiQuestionMark</code>, <code>MandelbrotSetIterationCount</code> and
 * <code>MandelbrotSetMemberQ</code>.
 */
public final class FractalNumberFunctions {

  /** The largest denominator for which an exact value is computed digit by digit. */
  private static final int MAX_DENOMINATOR = 1_000_000;

  /** The largest exponent of 2 in an exact <code>MinkowskiQuestionMark</code> value. */
  private static final long MAX_EXPONENT = 100_000L;

  /** The default <code>MaxIterations</code> of the Mandelbrot functions. */
  private static final int DEFAULT_MAX_ITERATIONS = 1000;

  private static class Initializer {

    private static void init() {
      S.CantorStaircase.setEvaluator(new CantorStaircase());
      S.MinkowskiQuestionMark.setEvaluator(new MinkowskiQuestionMark());
      S.MandelbrotSetIterationCount.setEvaluator(new Mandelbrot(false));
      S.MandelbrotSetMemberQ.setEvaluator(new Mandelbrot(true));
    }
  }

  /**
   * <code>CantorStaircase(x)</code> - the Cantor function: the base 3 digits of <code>x</code> up
   * to the first digit <code>1</code>, with each <code>2</code> read as a binary <code>1</code>.
   * It is <code>0</code> for <code>x &lt;= 0</code> and <code>1</code> for <code>x &gt;= 1</code>,
   * exact for rational <code>x</code>.
   */
  private static final class CantorStaircase extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr x = ast.arg1();
      if (x.isInteger() || x.isFraction()) {
        if (x.isNegative() || x.isZero()) {
          return F.C0;
        }
        if (!x.lessThan(F.C1).isTrue()) {
          return F.C1;
        }
        IFraction fraction = (IFraction) x;
        BigInteger p = fraction.toBigNumerator();
        BigInteger q = fraction.toBigDenominator();
        if (q.bitLength() > 31 || q.intValue() > MAX_DENOMINATOR) {
          return F.NIL;
        }
        return exact(p.intValue(), q.intValue(), engine);
      }
      if (x.isReal()) {
        double value = x.evalf();
        if (value <= 0.0) {
          return F.CD0;
        }
        if (value >= 1.0) {
          return F.CD1;
        }
        double result = 0.0;
        double weight = 0.5;
        for (int i = 0; i < 60 && weight > 0.0; i++) {
          value *= 3.0;
          int digit = (int) Math.floor(value);
          value -= digit;
          if (digit == 1) {
            result += weight;
            break;
          }
          if (digit == 2) {
            result += weight;
          }
          weight *= 0.5;
        }
        return F.num(result);
      }
      return F.NIL;
    }

    /** The exact value for <code>0 &lt; p/q &lt; 1</code>. */
    private static IExpr exact(int p, int q, EvalEngine engine) {
      // the binary digits, and the position at which each remainder was seen
      StringBuilder bits = new StringBuilder();
      Map<Integer, Integer> seen = new HashMap<Integer, Integer>();
      long remainder = p;
      while (true) {
        engine.checkTimeLimit();
        Integer start = seen.get((int) remainder);
        if (start != null) {
          // the digits from start on repeat
          return periodic(bits.substring(0, start), bits.substring(start));
        }
        seen.put((int) remainder, bits.length());
        long scaled = 3 * remainder;
        int digit = (int) (scaled / q);
        remainder = scaled % q;
        if (digit == 1) {
          bits.append('1');
          return periodic(bits.toString(), "");
        }
        bits.append(digit == 2 ? '1' : '0');
        if (remainder == 0) {
          return periodic(bits.toString(), "");
        }
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
    }
  }

  /**
   * The value of the binary fraction <code>0.prefix(period)(period)...</code>.
   */
  private static IExpr periodic(String prefix, String period) {
    BigInteger prefixValue = prefix.isEmpty() ? BigInteger.ZERO : new BigInteger(prefix, 2);
    BigInteger prefixScale = BigInteger.ONE.shiftLeft(prefix.length());
    IExpr result = F.QQ(prefixValue, prefixScale);
    if (period.isEmpty()) {
      return result;
    }
    BigInteger periodValue = new BigInteger(period, 2);
    BigInteger periodScale = BigInteger.ONE.shiftLeft(period.length()).subtract(BigInteger.ONE);
    // 0.(period) = period / (2^L - 1), shifted behind the prefix
    return result.add(F.QQ(periodValue, periodScale.multiply(prefixScale)));
  }

  /**
   * <code>MinkowskiQuestionMark(x)</code> - Minkowski's question mark function
   * <code>a0 + 2 Sum((-1)^(k+1) 2^-(a1+...+ak))</code> of the continued fraction
   * <code>{a0, a1, a2, ...}</code> of <code>x</code>, as <code>ContinuedFraction</code> gives it.
   * Exact for rational numbers and quadratic irrationals, whose periodic tail is summed as a
   * geometric series.
   */
  private static final class MinkowskiQuestionMark extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr x = ast.arg1();
      if (x.isInteger()) {
        return x;
      }
      if (x.isReal() && x.isInexactNumber()) {
        IExpr terms = engine.evaluate(F.binaryAST2(S.ContinuedFraction, x, F.ZZ(64)));
        if (!terms.isList() || terms.argSize() < 1) {
          return F.NIL;
        }
        IAST list = (IAST) terms;
        double result = list.arg1().evalf();
        double sum = 0.0;
        for (int k = 2; k < list.size(); k++) {
          sum += list.get(k).evalf();
          if (sum > 1100.0) {
            break;
          }
          result += 2.0 * (k % 2 == 0 ? 1.0 : -1.0) * Math.pow(2.0, -sum);
        }
        return F.num(result);
      }
      if (!x.isNumericFunction(true) || x.isInexactNumber()) {
        return F.NIL;
      }
      // quiet: a number which is no quadratic irrational has no periodic expansion
      IExpr terms = engine.evalQuiet(F.ContinuedFraction(x));
      if (!terms.isList() || terms.argSize() < 1) {
        return F.NIL;
      }
      IAST list = (IAST) terms;
      IAST period = list.last().isList() ? (IAST) list.last() : F.CEmptyList;
      int prefixEnd = period.isEmpty() ? list.argSize() : list.argSize() - 1;
      if (!x.isFraction() && period.isEmpty()) {
        // not a rational number and not a quadratic irrational
        return F.NIL;
      }
      IExpr a0 = list.arg1();
      if (!a0.isInteger()) {
        return F.NIL;
      }
      IASTAppendable sum = F.PlusAlloc(list.argSize() + 2);
      sum.append(a0);
      long exponent = 0;
      int k = 0;
      for (int i = 2; i <= prefixEnd; i++) {
        long a = list.get(i).toLongDefault();
        if (a == Long.MIN_VALUE) {
          return F.NIL;
        }
        exponent += a;
        k++;
        if (Math.abs(exponent) > MAX_EXPONENT) {
          return F.NIL;
        }
        sum.append(term(k, exponent));
      }
      if (!period.isEmpty()) {
        // one block of the period, and the factor each further block adds
        IASTAppendable block = F.PlusAlloc(period.argSize());
        long blockExponent = 0;
        for (IExpr element : period) {
          long a = element.toLongDefault();
          if (a == Long.MIN_VALUE) {
            return F.NIL;
          }
          blockExponent += a;
          k++;
          if (Math.abs(exponent + blockExponent) > MAX_EXPONENT) {
            return F.NIL;
          }
          block.append(term(k, exponent + blockExponent));
        }
        IExpr ratio = F.Times(period.argSize() % 2 == 0 ? F.C1 : F.CN1,
            F.Power(F.C2, F.ZZ(-blockExponent)));
        sum.append(F.Divide(block, F.Subtract(F.C1, ratio)));
      }
      return sum;
    }

    /** The term <code>2 (-1)^(k+1) 2^-exponent</code>. */
    private static IExpr term(int k, long exponent) {
      IExpr value = F.Power(F.C2, F.ZZ(1 - exponent));
      return k % 2 == 1 ? value : F.Negate(value);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
    }
  }

  /**
   * <code>MandelbrotSetIterationCount(c)</code> - the number of iterates <code>z(k)</code> of
   * <code>z -&gt; z^2 + c</code>, starting from <code>z(0) = 0</code>, with
   * <code>Abs(z(k)) &lt;= 2</code>, up to <code>MaxIterations</code> (default 1000). A point in the
   * main cardioid or in the period 2 bulb, which never escapes, gives <code>MaxIterations + 1</code>.
   *
   * <p>
   * <code>MandelbrotSetMemberQ(c)</code> - <code>True</code> if <code>c</code> is in the Mandelbrot
   * set: in the main cardioid or the period 2 bulb, or with an orbit which hasn't escaped after
   * <code>MaxIterations</code> steps.
   */
  private static final class Mandelbrot extends AbstractFunctionEvaluator {

    private final boolean memberQ;

    Mandelbrot(boolean memberQ) {
      this.memberQ = memberQ;
    }

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      int maxIterations = DEFAULT_MAX_ITERATIONS;
      for (int i = 2; i < ast.size(); i++) {
        IExpr arg = ast.get(i);
        IExpr value = arg;
        if (arg.isRuleAST()) {
          if (arg.first() != S.MaxIterations) {
            return F.NIL;
          }
          value = arg.second();
        }
        if (value.isInfinity()) {
          maxIterations = Integer.MAX_VALUE;
        } else {
          maxIterations = value.toIntDefault();
          if (maxIterations < 0) {
            return F.NIL;
          }
        }
      }
      IExpr c = ast.arg1();
      if (!c.isNumericFunction(true)) {
        return F.NIL;
      }
      IExpr numeric = engine.evalN(c);
      if (!numeric.isNumber()) {
        return F.NIL;
      }
      org.hipparchus.complex.Complex point = numeric.evalfc();
      double x = point.getReal();
      double y = point.getImaginary();
      if (insideCardioidOrBulb(x, y)) {
        return memberQ ? S.True : F.ZZ((long) maxIterations + 1L);
      }
      double zx = 0.0;
      double zy = 0.0;
      for (int k = 1; k <= maxIterations; k++) {
        if ((k & 0x3FF) == 0) {
          engine.checkTimeLimit();
        }
        double nx = zx * zx - zy * zy + x;
        zy = 2.0 * zx * zy + y;
        zx = nx;
        if (zx * zx + zy * zy > 4.0) {
          return memberQ ? S.False : F.ZZ(k - 1);
        }
      }
      if (memberQ) {
        // Maximum number of iterations reached. Point may not actually be in the Mandelbrot set,
        // but just extremely close.
        Errors.printMessage(S.MandelbrotSetMemberQ, "maxiter", F.CEmptyList, engine);
        return S.True;
      }
      return F.ZZ(maxIterations);
    }

    /** Whether <code>x + I y</code> is in the main cardioid or in the period 2 bulb. */
    private static boolean insideCardioidOrBulb(double x, double y) {
      double xq = x - 0.25;
      double q = xq * xq + y * y;
      return q * (q + xq) <= 0.25 * y * y || (x + 1.0) * (x + 1.0) + y * y <= 0.0625;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE);
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private FractalNumberFunctions() {}
}

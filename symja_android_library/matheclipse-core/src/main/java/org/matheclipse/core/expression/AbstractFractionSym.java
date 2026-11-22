package org.matheclipse.core.expression;

import static org.matheclipse.core.expression.NumberUtil.hasIntValue;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.NoSuchElementException;
import java.util.function.DoubleFunction;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.fraction.BigFraction;
import org.hipparchus.util.ArithmeticUtils;
import org.hipparchus.util.FastMath;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.combinatoric.BinomialCache;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.steps.StepLevel;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.BigIntegerLimitExceeded;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;
import org.matheclipse.core.visit.RationalizeNumericsVisitor;
import org.matheclipse.external.fastutil.ints.Int2IntMap;
import org.matheclipse.external.fastutil.ints.Int2IntRBTreeMap;

/**
 * Abstract base class for FractionSym and BigFractionSym
 *
 * @see FractionSym
 * @see BigFractionSym
 */
public abstract class AbstractFractionSym implements IFraction {

  private static final long serialVersionUID = -8743141041586314213L;

  static final int LOW_NUMER = -32;
  static final int HIGH_NUMER = 32;
  static final int LOW_DENOM = 2;
  static final int HIGH_DENOM = 32;

  /**
   * The normalized fractions <code>n/d</code> for <code>LOW_NUMER &lt;= n &lt;= HIGH_NUMER</code>
   * and <code>LOW_DENOM &lt;= d &lt;= HIGH_DENOM</code>, indexed by
   * <code>[n - LOW_NUMER][d - LOW_DENOM]</code>. Equal values share one instance, as long as the
   * reduced denominator is inside the table, so <code>2/4</code> and <code>1/2</code> are the same
   * object.
   */
  static final FractionSym[][] CACHE = createCache();

  private static FractionSym[][] createCache() {
    FractionSym[][] cache =
        new FractionSym[(HIGH_NUMER - LOW_NUMER) + 1][(HIGH_DENOM - LOW_DENOM) + 1];
    // ascending denominators, so that the reduced form of an entry is already in the table
    for (int d = LOW_DENOM; d <= HIGH_DENOM; d++) {
      for (int n = LOW_NUMER; n <= HIGH_NUMER; n++) {
        int gcd = ArithmeticUtils.gcd(n, d);
        int reducedNumerator = n / gcd;
        int reducedDenominator = d / gcd;
        cache[n - LOW_NUMER][d - LOW_DENOM] = gcd > 1 && reducedDenominator >= LOW_DENOM //
            ? cache[reducedNumerator - LOW_NUMER][reducedDenominator - LOW_DENOM]
            : new FractionSym(reducedNumerator, reducedDenominator);
      }
    }
    return cache;
  }

  /**
   * Compute the Bernoulli number of the first kind with the <a href=
   * "https://oeis.org/wiki/User:Peter_Luschny/ComputationAndAsymptoticsOfBernoulliNumbers#Seidel">Seidel
   * algorithm</a>
   * 
   * @param n
   * @return throws ArithmeticException if n is a negative int number
   */
  // public static IRational bernoulliNumber(int n) {
  // if (n == 0) {
  // return F.C1;
  // } else if (n == 1) {
  // return F.CN1D2;
  // } else if (n < 0) {
  // throw new ArithmeticException("BernoulliB(n): n is not a positive int number");
  // } else if (n % 2 != 0) {
  // // http://fungrim.org/entry/a98234/
  // return F.C0;
  // }
  // if (n > Config.MAX_AST_SIZE) {
  // throw new ASTElementLimitExceeded(n);
  // }
  //
  // synchronized (BernoulliCache.CACHE) {
  // if (n < BernoulliCache.CACHE.size() && BernoulliCache.CACHE.get(n) != null) {
  // return BernoulliCache.CACHE.get(n);
  // }
  //
  // int oldSize = BernoulliCache.CACHE.size();
  // if (n >= oldSize) {
  // BernoulliCache.CACHE.addAll(Collections.nCopies(n - oldSize + 1, null));
  // }
  //
  // if (n == 0) {
  // BernoulliCache.CACHE.set(0, F.C1);
  // return F.C1;
  // }
  // if (n == 1) {
  // BernoulliCache.CACHE.set(1, F.CN1D2);
  // return F.CN1D2;
  // }
  // if (n % 2 != 0) {
  // BernoulliCache.CACHE.set(n, F.C0);
  // return F.C0;
  // }
  //
  // // Seidel's algorithm implementation
  // int m = n / 2;
  // IRational[] d = new IRational[m + 3];
  // IRational[] r = new IRational[m + 3];
  // for (int i = 0; i < d.length; i++) {
  // d[i] = F.C0;
  // }
  // d[1] = F.C1;
  // r[0] = F.C1;
  //
  // long iterationLimit = EvalEngine.get().getIterationLimit();
  // if (iterationLimit > 0 && iterationLimit < Integer.MAX_VALUE / 2) {
  // iterationLimit *= 10L;
  // iterationLimit += n;
  // if (iterationLimit > 0L && iterationLimit <= n) {
  // IterationLimitExceeded.throwIt(iterationLimit, F.BernoulliB(F.ZZ(n)));
  // }
  // }
  // int iterationCounter = 0;
  //
  // boolean b = true;
  // int h = 1;
  // IInteger p = F.C1;
  // IInteger s = F.CN2;
  //
  // IInteger q = F.C0;
  // for (int i = 0; i < n; i++) {
  // iterationCounter += h;
  // if (iterationLimit > 0L && iterationLimit <= iterationCounter) {
  // IterationLimitExceeded.throwIt(iterationCounter, F.BernoulliB(F.ZZ(n)));
  // }
  //
  // if (b) {
  // h++;
  // p = p.multiply(4);
  // s = s.negate();
  // q = s.multiply(p.subtract(F.C1));
  // for (int k = h; k > 0; k--) {
  // d[k] = d[k].add(d[k + 1]);
  // }
  // } else {
  // for (int k = 1; k < h; k++) {
  // d[k] = d[k].add(d[k - 1]);
  // }
  // r[h] = d[h - 1].divideBy(q);
  //
  // }
  // b = !b;
  // }
  // for (int i = oldSize; i <= n; i++) {
  // if (i % 2 != 0) {
  // BernoulliCache.CACHE.set(i, F.C0);
  // } else {
  // BernoulliCache.CACHE.set(i, r[i / 2 + 1]);
  // }
  // }
  // return BernoulliCache.CACHE.get(n);
  // }
  // }


  /**
   * Compute the Bernoulli number of the first kind.
   *
   * @param n
   * @return throws ArithmeticException if n is a negative int number
   */
  public static IRational bernoulliNumber(int n) {
    if (n == 0) {
      return F.C1;
    } else if (n == 1) {
      return F.CN1D2;
    } else if (n < 0) {
      throw new ArithmeticException("BernoulliB(n): n is not a positive int number");
    } else if (n % 2 != 0) {
      // http://fungrim.org/entry/a98234/
      return F.C0;
    }
    if (n > Config.MAX_AST_SIZE) {
      throw new ASTElementLimitExceeded(n);
    }
    IFraction[] bernoulli = new IFraction[n + 1];
    bernoulli[0] = FractionSym.ONE;
    bernoulli[1] = AbstractFractionSym.valueOf(-1L, 2L);

    long iterationLimit = EvalEngine.get().getIterationLimit();
    if (iterationLimit > 0 && iterationLimit < Integer.MAX_VALUE / 2) {
      iterationLimit *= 10L;
      iterationLimit += n;
      if (iterationLimit > 0L && iterationLimit <= n) {
        IterationLimitExceeded.throwIt(iterationLimit, F.BernoulliB(F.ZZ(n)));
      }
    }

    if (n <= BinomialCache.MAX_N) {
      iterationLimit = -1;
    }
    int iterationCounter = 0;
    for (int k = 2; k <= n; k++) {
      bernoulli[k] = FractionSym.ZERO;
      iterationCounter += k;
      if (iterationLimit > 0 && iterationLimit <= iterationCounter) {
        IterationLimitExceeded.throwIt(iterationCounter, F.BernoulliB(F.ZZ(n)));
      }
      for (int i = 0; i < k; i++) {
        if (!bernoulli[i].isZero()) {
          int nb = k + 1;
          int mb = nb - i;
          IFraction bin = AbstractFractionSym.valueOf(AbstractIntegerSym.binomial(nb, mb));
          bernoulli[k] = bernoulli[k].sub(bin.mul(bernoulli[i]));
        }
      }
      bernoulli[k] = bernoulli[k].div(AbstractFractionSym.valueOf(k + 1));

    }
    return bernoulli[n].normalize();
  }

  /**
   * Compute the Bernoulli number of the first kind.
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Bernoulli_number">Wikipedia - Bernoulli number</a>.
   * <br>
   * For better performing implementations see
   * <a href= "http://oeis.org/wiki/User:Peter_Luschny/ComputationAndAsymptoticsOfBernoulliNumbers"
   * >ComputationAndAsymptoticsOfBernoulliNumbers</a>
   *
   * @param n
   * @return throws ArithmeticException if n is not an non-negative Java int number
   */
  public static IRational bernoulliNumber(final IInteger n) {
    int bn = n.toIntDefault(-1);
    if (bn >= 0) {
      return bernoulliNumber(bn);
    }
    throw new ArithmeticException("BernoulliB(n): n is not a positive int number");
  }

  /**
   * Returns the last element of the series of convergent-steps to approximate the given value.
   * 
   * @param value value to approximate
   * @param maxConvergents maximum number of convergents to examine
   * @param limit
   * @return the fraction of last element of the series of convergents and a boolean if that element
   *         satisfies the specified convergent test
   */
  private static IFraction convergeFraction(double value, int maxConvergents, double limit) {
    return rationalize(value, v -> {
      org.hipparchus.util.Pair<BigFraction, Boolean> convergent = BigFraction.convergent(v,
          maxConvergents, (p, q) -> FastMath.abs(p * q - v * q * q) <= limit);
      return convergent.getSecond().booleanValue() ? convergent.getFirst() : null;
    });
  }

  private static IFraction fractionOf(BigInteger numerator, BigInteger denominator) {
    if (denominator.signum() == 0) {
      throw getDivisionTroughZeroException(F.ZZ(numerator)); // Infinite expression `1` encountered.
    }
    if (hasIntValue(denominator) && hasIntValue(numerator)) {
      return valueOf(numerator.intValue(), denominator.intValue());
    }
    return null;
  }

  public static BigInteger gcd(BigInteger i1, BigInteger i2) {
    if (i1.equals(BigInteger.ONE) || i2.equals(BigInteger.ONE)) {
      return BigInteger.ONE;
    } else if (hasIntValue(i1) && hasIntValue(i2)) {
      return BigInteger.valueOf(AbstractIntegerSym.gcd(i1.intValue(), i2.intValue()));
    } else {
      return i1.gcd(i2);
    }
  }

  /**
   * Returns a new instance of {@link ArgumentTypeException} with message shortcut <code>infy</code>
   * - <code>Infinite expression `1` encountered</code>
   * 
   * @param num
   * @return
   */
  private static ArgumentTypeException getDivisionTroughZeroException(IInteger num) {
    // Infinite expression `1` encountered.
    String str = Errors.getMessage("infy", F.list(F.Rational(num, F.C0)), EvalEngine.get());
    return new ArgumentTypeException(str);
  }

  private static IExpr getInfiniteOrInteger(double value) {
    if (Double.isNaN(value)) {
      return F.NIL;
    } else if (value == Double.POSITIVE_INFINITY) {
      return F.CInfinity;
    } else if (value == Double.NEGATIVE_INFINITY) {
      return F.CNInfinity;
    }
    long integerValue = (long) value;
    if (value == integerValue) { // also catches value == 0
      return F.ZZ(integerValue); // take shortcut
    }
    return null;
  }

  /**
   * From <code>2^53</code> on a <code>double</code> is an integer, and there is no continued
   * fraction of it: the long numbers of that calculation gave one which was 16 away from
   * <code>123456789012345680.0</code>.
   */
  public static final double MIN_INTEGER_DOUBLE = 0x1.0p53;

  private static IFraction rationalize(double value, DoubleFunction<BigFraction> f) {
    if (Math.abs(value) >= MIN_INTEGER_DOUBLE) {
      return null;
    }
    try {
      BigFraction fraction = f.apply(value < 0 ? -value : value);
      if (fraction != null) {// && fraction.getNumerator().signum()!=0) {
        return valueOf(value < 0 ? fraction.negate() : fraction);
      }
    } catch (MathRuntimeException e) { // assume no solution
    }
    return null;
  }

  public static IExpr rationalize(IExpr arg1) {
    return rationalize(arg1, Config.DOUBLE_EPSILON, true);
  }

  /**
   * Rationalize only pure numeric numbers in <code>expr</code>.
   *
   * @param expr
   * @return {@link F#NIL} if no expression was transformed
   */
  public static IExpr rationalize(IExpr expr, boolean useConvergenceMethod) {
    return AbstractFractionSym.rationalize(expr, Config.DOUBLE_EPSILON, useConvergenceMethod);
  }

  /**
   * Rationalize only pure numeric numbers in expression <code>expr</code>.
   *
   * @param expr
   * @param epsilon
   * @param useConvergenceMethod
   * @return {@link F#NIL} if no expression was transformed
   */
  public static IExpr rationalize(IExpr expr, double epsilon, boolean useConvergenceMethod) {
    return rationalize(expr, epsilon, useConvergenceMethod, false);
  }

  /**
   * Rationalize only pure numeric numbers in <code>expr</code>.
   *
   * @param keepLargeNumbers a number without a fraction which its digits determine is left as it
   *        is - see {@link RationalizeNumericsVisitor}; otherwise it is the integer which it is
   * @return {@link F#NIL} if no expression was transformed
   */
  public static IExpr rationalize(IExpr expr, double epsilon, boolean useConvergenceMethod,
      boolean keepLargeNumbers) {
    RationalizeNumericsVisitor rationalizeVisitor =
        new RationalizeNumericsVisitor(epsilon, useConvergenceMethod, keepLargeNumbers);
    return expr.accept(rationalizeVisitor);
  }

  public static IFraction valueOf(BigFraction fraction) {
    IFraction f = fractionOf(fraction.getNumerator(), fraction.getDenominator());
    return f != null ? f : new BigFractionSym(fraction);
  }

  public static IFraction valueOf(BigInteger numerator) {
    return valueOf(numerator, BigInteger.ONE);
  }

  /**
   * Construct a rational from two BigIntegers. Use this method to create a rational number if the
   * numerator or denominator may be to big to fit in an Java int. This method normalizes the
   * rational number.
   *
   * @param numerator Numerator
   * @param denominator Denominator
   * @return
   */
  public static IFraction valueOf(BigInteger numerator, BigInteger denominator) {
    IFraction f = fractionOf(numerator, denominator);
    if (f != null) {
      return f;
    }
    BigFractionSym fraction = new BigFractionSym(numerator, denominator);
    // the BigFraction has cancelled the gcd: (791*10^20)/(20*10^20) is 791/20 now, and a value
    // which fits into int must be the FractionSym valueOf(long, long) creates, because
    // BigFractionSym#equals() never accepts a FractionSym
    f = fractionOf(fraction.toBigNumerator(), fraction.toBigDenominator());
    return f != null ? f : fraction;
  }

  public static IFraction valueOf(IInteger numerator) {
    if (numerator instanceof IntegerSym) {
      return valueOf(((IntegerSym) numerator).fIntValue);
    }
    return valueOf(numerator.toBigNumerator());
  }

  public static IFraction valueOf(IInteger numerator, IInteger denominator) {
    if (numerator instanceof IntegerSym && denominator instanceof IntegerSym) {
      return valueOf(((IntegerSym) numerator).fIntValue, ((IntegerSym) denominator).fIntValue);
    }
    return valueOf(numerator.toBigNumerator(), denominator.toBigNumerator());
  }

  /**
   * Construct a rational from two longs. Use this method to create a rational number. This method
   * normalizes the rational number and may return a previously created one. This method does not
   * work if called with value Long.MIN_VALUE.
   *
   * @param numerator Numerator.
   * @return
   */
  public static IFraction valueOf(long numerator) {
    if (numerator == 0) {
      return FractionSym.ZERO;
    }
    if (numerator == 1) {
      return FractionSym.ONE;
    }
    if (numerator == -1) {
      return FractionSym.MONE;
    }

    if (Integer.MIN_VALUE < numerator && numerator <= Integer.MAX_VALUE) {
      return new FractionSym((int) numerator, 1);
    }
    return new BigFractionSym(BigInteger.valueOf(numerator), BigInteger.ONE);
  }

  /**
   * Construct a rational from two longs. Use this method to create a rational number. This method
   * normalizes the rational number and may return a previously created one. This method does not
   * work if called with value Long.MIN_VALUE.
   *
   * @param numerator Numerator.
   * @param denominator Denominator.
   * @return
   */
  public static IFraction valueOf(long numerator, long denominator) {
    if (numerator > Long.MIN_VALUE && denominator > Long.MIN_VALUE) {
      if (denominator == 0) {
        throw getDivisionTroughZeroException(F.ZZ(numerator));
      } else if (numerator == 0) {
        return FractionSym.ZERO;
      } else if (numerator == denominator) {
        return FractionSym.ONE;
      }
      if (denominator < 0) {
        numerator = -numerator;
        denominator = -denominator;
      }
      if (numerator >= FractionSym.LOW_NUMER && numerator <= FractionSym.HIGH_NUMER //
          && denominator >= FractionSym.LOW_DENOM && denominator <= FractionSym.HIGH_DENOM) {
        return FractionSym.CACHE[((int) numerator) + (-FractionSym.LOW_NUMER)][((int) denominator)
            + (-FractionSym.LOW_DENOM)];
      }
      if (numerator != 1 && denominator != 1) {
        long gcd = Math.abs(ArithmeticUtils.gcd(numerator, denominator));
        if (gcd != 1L) {
          if (EvalEngine.get().isTraceLevel(StepLevel.ARITHMETIC)) {
            IAST divide = F.Rational(F.ZZ(numerator), F.ZZ(denominator));
            EvalEngine.get().addTraceStep(StepLevel.ARITHMETIC, divide, divide,
                F.List(S.Rational, F.$str("FractionCancelGCD"), divide, F.ZZ(gcd)));
          }
          numerator /= gcd;
          denominator /= gcd;
        }
      }

      if (denominator == 1) {
        if (numerator == 1) {
          return FractionSym.ONE;
        }
        if (numerator == -1) {
          return FractionSym.MONE;
        }
        if (numerator == 0) {
          return FractionSym.ZERO;
        }
      }

      if (Integer.MIN_VALUE < numerator && numerator <= Integer.MAX_VALUE
          && denominator <= Integer.MAX_VALUE) {
        return new FractionSym((int) numerator, (int) denominator);
      }
    } else {
      if (denominator == 0) {
        throw getDivisionTroughZeroException(F.ZZ(numerator));
      } else if (numerator == 0) {
        return FractionSym.ZERO;
      } else if (numerator == denominator) {
        return FractionSym.ONE;
      }
    }
    return new BigFractionSym(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator));
  }

  /**
   * Returns the last element of the series of convergent-steps to approximate the given value.
   *
   * @param value value to approximate
   */
  public static IFraction valueOfConvergent(double value) {
    IFraction fraction = convergeFraction(value, 20, 0.5E-4);
    if (fraction == null) {
      throw new NoSuchElementException("No converging fraction found for value " + value);
    }
    return fraction;
  }

  /**
   * Returns the exact fraction of the shortest decimal representation of <code>value</code>, i.e.
   * of the decimal literal the <code>double</code> was created from. For example
   * <code>1.25663706E-6</code> is converted to <code>62831853/50000000000000</code>.
   *
   * <p>
   * Note that this is the fraction of the <i>decimal</i> representation, not of the exact binary
   * value of the <code>double</code> (see {@link #valueOfExact(double)}), so it is only faithful
   * for numbers which really are of machine precision.
   *
   * @param value a finite machine precision double value
   * @return the exact fraction of {@link Double#toString(double)}
   */
  public static IFraction valueOfDecimal(double value) {
    BigDecimal decimal = new BigDecimal(Double.toString(value));
    int scale = decimal.scale();
    if (scale <= 0) {
      return valueOf(decimal.toBigIntegerExact());
    }
    return valueOf(decimal.unscaledValue(), BigInteger.TEN.pow(scale));
  }

  /**
   * Rationalize the given double value with <code>epsilon</code> maximum error allowed.
   *
   * @param value the double value to convert to a fraction.
   * @param epsilon maximum error allowed. The resulting fraction is within epsilon of value, in
   *        absolute terms.
   * @return
   */
  public static IFraction valueOfEpsilon(double value, double epsilon) {
    IFraction fraction = rationalize(value, v -> new BigFraction(v, epsilon, 200));
    return fraction != null ? fraction : valueOf(new BigFraction(value));
  }

  /**
   * Rationalizes the given double value exactly.
   * <p>
   * This methods returns an {@link IExpr} that, when being evaluated to a double value (using
   * {@link IExpr#evalf()}), results to the exact same value (per bit) as the given one.
   * </p>
   * <p>
   * Although it is not possible to express all real numbers as a fraction of two integers, it is
   * possible for all finite floating-point numbers to be expressed as fraction with exact same
   * value, because floating-point numbers are finite in their representation and therefore cannot
   * express all real numbers exactly. But this allows the exact representation as a fraction.<br>
   * Nevertheless this may lead to unexpected results. For example the value {@code 0.7} is
   * rationalized to {@code 3152519739159347/4503599627370496} and not the expected {@code 7/10}.
   * </p>
   * There is no guarantee made about the specific type of the returned expression. Not all possible
   * values of a double, especially small ones, can be expressed as {@link IFraction} in such way
   * that it evaluates to the same double value. In such cases the value is expressed by
   * {@code  mantissa * 2 ^ power}.
   * 
   * @param value the double value to convert
   * @return an IExpr that evaluates to the exact same double value
   */
  public static IExpr valueOfExact(double value) {
    IExpr ii = getInfiniteOrInteger(value);
    if (ii != null) {
      return ii;
    }
    BigFraction exactFraction = new BigFraction(value); // computes exact fraction representation
    int denominatorExponent = exactFraction.getDenominator().bitLength() - 1;
    if (denominatorExponent <= Double.MAX_EXPONENT) {
      return valueOf(exactFraction);
    }
    // The fractions denominator cannot be expressed as double value and would lead to an infinite
    // denominator double-value this has the consequence that the fraction will become return zero
    // when evaluated to double. Instead express the value as mantissa2 * 2 ^ exp2

    int exp2 = Math.getExponent(value);
    double mantissa2 = value * Math.pow(2, -exp2);

    IExpr mantissaFraction = getInfiniteOrInteger(mantissa2); // try shortcut
    if (mantissaFraction == null) {
      mantissaFraction = valueOf(new BigFraction(mantissa2));
    }
    return F.Times(mantissaFraction, F.Power(F.C2, F.ZZ(exp2)));
  }

  /**
   * Rationalizes the given double value exactly while tending to return results that are closer to
   * what a human would expect and are therefore considered 'nicer'.
   * <p>
   * This method has the same constraints for the returned value like {@link #valueOfExact(double)},
   * which often does return less 'nice' results, but tends to run longer.
   * </p>
   * 
   * @param value the double value to convert
   * @return an IExpr that evaluates to the exact same double value
   * @see #valueOfExact(double)
   * @see #valueOfConvergent(double)
   */
  public static IExpr valueOfExactNice(double value) {
    IExpr ii = getInfiniteOrInteger(value);
    if (ii != null) {
      return ii;
    }
    if (FastMath.abs(Math.getExponent(value)) < 100) {
      // For values in the order of small powers of ten and only a few decimals (e.g. 3.75, 0.01,
      // 124.6) the convergence approach usually achieves results that are closer to what a human
      // would compute and are therefore considered 'nicer'. To honor this we try a few convergence
      // iterations and take that result if it is exact.
      // The number of maxIterations is a tuning parameter and reflects how bad we want nicer
      // results. Since valueOfExact() is faster the fewer iterations are attempted before giving up
      // the faster this method is in average.
      IFraction fraction = convergeFraction(value, 5, 0.0);
      if (fraction != null && value == fraction.doubleValue()) {
        return fraction;
      }
    }
    return valueOfExact(value);
  }

  /** {@inheritDoc} */
  @Override
  public IExpr accept(IVisitor visitor) {
    return visitor.visit(this);
  }

  /** {@inheritDoc} */
  @Override
  public boolean accept(IVisitorBoolean visitor) {
    return visitor.visit(this);
  }

  /** {@inheritDoc} */
  @Override
  public int accept(IVisitorInt visitor) {
    return visitor.visit(this);
  }

  /** {@inheritDoc} */
  @Override
  public long accept(IVisitorLong visitor) {
    return visitor.visit(this);
  }

  /** {@inheritDoc} */
  @Override
  public IRational add(IRational that) {
    if (that instanceof IFraction) {
      return add((IFraction) that);
    }
    if (that instanceof IntegerSym) {
      return add(AbstractFractionSym.valueOf(((IntegerSym) that).fIntValue));
    }
    return add(AbstractFractionSym.valueOf(((BigIntegerSym) that).fBigIntValue));
  }

  /** {@inheritDoc} */
  @Override
  public IReal add(IReal that) {
    if (that instanceof IRational) {
      return add((IRational) that);
    }
    return Num.valueOf(doubleValue() + that.doubleValue());
  }

  /**
   * Returns <code>this+(fac1*fac2)</code>.
   *
   * @param fac1 the first factor
   * @param fac2 the second factor
   * @return <code>this+(fac1*fac2)</code>
   */
  public IFraction addmul(IFraction fac1, IFraction fac2) {
    return add(fac1.mul(fac2));
  }

  @Override
  public ApcomplexNum apcomplexNumValue() {
    return ApcomplexNum.valueOf(apcomplexValue());
  }

  @Override
  public Apcomplex apcomplexValue() {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    Apfloat real = h.divide(ApfloatNum.apfloatOf(toBigNumerator(), h.precision()),
        ApfloatNum.apfloatOf(toBigDenominator(), h.precision()));
    return new Apcomplex(real);
  }

  @Override
  public ApfloatNum apfloatNumValue() {
    return ApfloatNum.valueOf(toBigNumerator(), toBigDenominator());
  }

  @Override
  public Apfloat apfloatValue() {
    long precision = EvalEngine.getApfloat().precision();
    Apfloat n = ApfloatNum.apfloatOf(toBigNumerator(), precision);
    Apfloat d = ApfloatNum.apfloatOf(toBigDenominator(), precision);
    return n.divide(d);
  }

  @Override
  public Pair asNumerDenom() {
    return F.pair(numerator(), denominator());
  }

  @Override
  public void checkBitLength() {
    if (Integer.MAX_VALUE > Config.MAX_BIT_LENGTH) {
      long bitLength = (long) toBigNumerator().bitLength() + toBigDenominator().bitLength();
      if (bitLength > Config.MAX_BIT_LENGTH) {
        BigIntegerLimitExceeded.throwIt(bitLength);
      }
    }
  }

  @Override
  // CPD-OFF literal constant table
  public int compareTo(IExpr expr) {
    if (expr.isNumber() && !expr.isReal()) {
      if (expr.isInexactNumber()) {
        // the same order from both sides: an inexact complex number compares its real part
        // numerically
        return -expr.compareTo(this);
      }
      int c = this.compareTo(expr.re());
      if (c != 0) {
        return c;
      }
      // a real number comes before the exact complex numbers with the same real part
      return -1;
    }
    return IExpr.compareHierarchy(this, expr);
  }

  /** {@inheritDoc} */
  @Override
  public int complexSign() {
  // CPD-ON
    return toBigNumerator().signum();
  }

  @Override
  public IExpr copy() {
    return this;
  }

  /**
   * Returns the denominator of this fraction.
   *
   * @return denominator
   */
  @Override
  public IInteger denominator() {
    return AbstractIntegerSym.valueOf(toBigDenominator());
  }

  /** {@inheritDoc} */
  @Override
  public IRational divideBy(IRational that) {
    if (that instanceof IFraction) {
      return this.div((IFraction) that);
    }
    if (that instanceof IntegerSym) {
      return this.div(AbstractFractionSym.valueOf(((IntegerSym) that).fIntValue));
    }
    return this.div(AbstractFractionSym.valueOf(((BigIntegerSym) that).fBigIntValue));
  }

  /** {@inheritDoc} */
  @Override
  public IReal divideBy(IReal that) {
    if (that instanceof IRational) {
      return this.divideBy((IRational) that);
    }
    return Num.valueOf(doubleValue() / that.doubleValue());
  }

  @Override
  public boolean equalsInt(int i) {
    return toBigNumerator().equals(BigInteger.valueOf(i))
        && toBigDenominator().equals(BigInteger.ONE);
  }

  /** {@inheritDoc} */
  @Override
  public IExpr evaluate(EvalEngine engine) {
    if (engine.isNumericMode()) {
      return numericNumber();
    }
    INumber cTemp = normalize();
    return (cTemp == this) ? F.NIL : cTemp;
  }

  @Override
  /** {@inheritDoc} */
  public IASTAppendable factorInteger() {
    IInteger num = numerator();
    IInteger den = denominator();
    IASTAppendable numResult = num.factorInteger();
    IASTAppendable denResult = den.factorInteger();
    // negate the exponents of the denominator part
    denResult.forEach(list -> ((IASTMutable) list).set(2, list.second().negate()));
    IASTAppendable result = F.ListAlloc(denResult.argSize() + numResult.argSize());
    // add the factors from the numerator part
    result.appendArgs(denResult);
    result.appendArgs(numResult);
    EvalAttributes.sort(result);
    return result;
  }

  @Override
  public IAST factorSmallPrimes(int rootNumerator, int rootDenominator) {
    IInteger b = numerator();
    boolean isNegative = false;
    if (complexSign() < 0) {
      b = b.negate();
      isNegative = true;
    }
    IInteger d = denominator();
    Int2IntMap bMap = new Int2IntRBTreeMap();
    BigInteger number = b.toBigNumerator();
    IAST bAST = AbstractIntegerSym.factorBigInteger(number, rootNumerator, isNegative,
        rootNumerator, rootDenominator, bMap);
    Int2IntMap dMap = new Int2IntRBTreeMap();
    number = d.toBigNumerator();
    IAST dAST = AbstractIntegerSym.factorBigInteger(number, rootNumerator, false, rootNumerator,
        rootDenominator, dMap);
    if (bAST.isPresent()) {
      if (dAST.isPresent()) {
        return F.Times(bAST, F.Power(dAST, F.CN1));
      }
      return F.Times(bAST, F.Power(denominator(), F.QQ(-rootNumerator, rootDenominator)));
    } else if (dAST.isPresent()) {
      return F.Times(F.Power(numerator(), F.QQ(rootNumerator, rootDenominator)),
          F.Power(dAST, F.CN1));
    }
    return F.NIL;
  }

  /** {@inheritDoc} */
  @Override
  public IRational gcd(IRational that) {
    if (that.isZero()) {
      return this;
    }
    if (this.isZero()) {
      return that;
    // CPD-OFF literal constant table
    }
    BigInteger tdenom = this.toBigDenominator();
    BigInteger odenom = that.toBigDenominator();
    BigInteger gcddenom = tdenom.gcd(odenom);
    BigInteger denom = tdenom.divide(gcddenom).multiply(odenom);
    BigInteger num = toBigNumerator().gcd(that.toBigNumerator());
    return AbstractFractionSym.valueOf(num, denom);
  }

  @Override
  public IRational getImaginaryPart() {
    return F.C0;
  }

  @Override
  public IRational getRealPart() {
    return this;
  }

  @Override
  public ISymbol head() {
    return S.Rational;
    // CPD-ON
  }

  @Override
  public int hierarchy() {
    return FRACTIONID;
  }

  @Override
  public IReal im() {
    return F.C0;
  }

  @Override
  public double imDoubleValue() {
    return 0.0;
  }

  /** {@inheritDoc} */
  @Override
  public IInteger integerPart() {
    return isNegative() ? ceilFraction() : floorFraction();
  }

  @Override
  public boolean isGT(IReal obj) {
    if (obj instanceof FractionSym) {
      return compareTo((obj)) > 0;
    }
    if (obj instanceof IInteger) {
      return compareTo(
          AbstractFractionSym.valueOf(((IInteger) obj).toBigNumerator(), BigInteger.ONE)) > 0;
    }
    return RealNumberComparison.isGreater(this, obj);
  }

  /**
   * Check whether this rational represents an integral value (i.e. the denominator equals 1).
   *
   * @return <code>true</code> iff value is integral.
   */
  public abstract boolean isIntegral();

  @Override
  public boolean isLT(IReal obj) {
    if (obj instanceof FractionSym) {
      return compareTo((obj)) < 0;
    }
    if (obj instanceof IInteger) {
      return compareTo(
          AbstractFractionSym.valueOf(((IInteger) obj).toBigNumerator(), BigInteger.ONE)) < 0;
    }
    return RealNumberComparison.isLess(this, obj);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNumEqualRational(IRational value) throws ArithmeticException {
    return equals(value);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isRationalValue(IRational value) {
    return equals(value);
  }

  @Override
  public long leafCount() {
    return 3;
  }

  @Override
  public long leafCountSimplify() {
    return 1 + numerator().leafCountSimplify() + denominator().leafCountSimplify();
  }

  /**
   * Return a new rational representing <code>this * other</code>.
   *
   * @param other Rational to multiply.
   * @return Product of <code>this</code> and <code>other</code>.
   */
  @Override
  public IFraction mul(IFraction other) {
    if (other.isOne()) {
      return this;
    }
    if (other.isZero()) {
      return other;
    }
    if (other.isMinusOne()) {
      return this.negate();
    }

    BigInteger newnum = toBigNumerator().multiply(other.toBigNumerator());
    BigInteger newdenom = toBigDenominator().multiply(other.toBigDenominator());
    return valueOf(newnum, newdenom);
  }

  /** {@inheritDoc} */
  @Override
  public IRational multiply(IRational that) {
    if (that instanceof IFraction) {
      return mul((IFraction) that);
    }
    if (that instanceof IntegerSym) {
      return mul(AbstractFractionSym.valueOf(((IntegerSym) that).fIntValue));
    }
    return mul(AbstractFractionSym.valueOf(((BigIntegerSym) that).fBigIntValue));
  }

  /** {@inheritDoc} */
  @Override
  public IReal multiply(IReal that) {
    if (that instanceof IRational) {
      return multiply((IRational) that);
    }
    return Num.valueOf(doubleValue() * that.doubleValue());
  }

  /**
   * Returns the numerator of this fraction.
   *
   * @return denominator
   */
  @Override
  public IInteger numerator() {
    return AbstractIntegerSym.valueOf(toBigNumerator());
  }

  @Override
  public INumber numericNumber() {
    return F.num(this);
  }

  @Override
  public Num numValue() {
    return Num.valueOf(doubleValue());
  }

  @Override
  public IReal opposite() {
    return this.negate();
  }

  /** {@inheritDoc} */
  @Override
  public IExpr plus(IExpr that) {
    if (that instanceof INumber) {
      return this.plus((INumber) that);
    }
    return IFraction.super.plus(that);
  }

  /** {@inheritDoc} */
  @Override
  public INumber plus(INumber that) {
    if (that instanceof IFraction) {
      return this.add((IFraction) that).normalize();
    }
    if (that instanceof IntegerSym) {
      return this.add(valueOf(((IntegerSym) that).fIntValue)).normalize();
    }
    if (that instanceof BigIntegerSym) {
      return this.add(valueOf(((BigIntegerSym) that).fBigIntValue)).normalize();
    }
    if (that instanceof ComplexSym) {
      return ((ComplexSym) that).add(ComplexSym.valueOf(this)).normalize();
    }
    if (that instanceof Num) {
      if (F.isExactZero(that)) {
        return this;
      }
      // the machine number takes this number at machine precision, or promoted beyond it
      return ((Num) that).plus((INumber) this);
    }
    if (that instanceof ComplexNum) {
      if (F.isExactZero(that)) {
        return this;
      }
      return F.complexNum(evalfc().add(that.evalfc()));
    }
    if (that instanceof ApfloatNum) {
      // the error of the number carries on, also the one of a zero of a given accuracy
      return ((ApfloatNum) that).plus(this);
    }
    if (that instanceof ApcomplexNum) {
      if (F.isExactZero(that)) {
        return this;
      }
      if (((ApcomplexNum) that).isSignificanceTracked()) {
        // the error of the number carries on
        return ((ApcomplexNum) that).plus(this);
      }
      return apcomplexNumValue().add(((ApcomplexNum) that).apcomplexNumValue());
    }
    throw new java.lang.ArithmeticException();
  }

  @Override
  public IExpr power(IExpr that) {
    if (that instanceof IInteger) {
      if (that.isZero()) {
        if (!this.isZero()) {
          return F.C1;
        }
        return IFraction.super.power(that);
      } else if (that.isOne()) {
        return this;
      } else if (that.isMinusOne()) {
        return inverse();
      }
      long n = that.toLongDefault();
      if (F.isPresent(n)) {
        return power(n);
      }
    }
    return IFraction.super.power(that);
  }

  /** {@inheritDoc} */
  @Override
  public final IFraction powerRational(long n) throws ArithmeticException {
    if (n == 0L) {
      if (!this.isZero()) {
        return FractionSym.ONE;
      }
      throw new ArithmeticException("Indeterminate: 0^0");
    } else if (n == 1L) {
      return this;
    } else if (n == -1L) {
      return inverseFraction();
    }
    long exp = n;
    if (n < 0) {
      if (F.isNotPresent(n)) {
        throw new java.lang.ArithmeticException();
      }
      exp *= -1;
    }
    BigIntegerLimitExceeded.checkPower(
        Math.max(toBigNumerator().bitLength(), toBigDenominator().bitLength()), exp);
    int b2pow = 0;

    while ((exp & 1) == 0) {
      b2pow++;
      exp >>= 1;
    }

    IFraction result = this;
    IFraction x = result;

    while ((exp >>= 1) > 0) {
      x = x.mul(x);
      if ((exp & 1) != 0) {
        result.checkBitLength();
        result = result.mul(x);
      }
    }

    while (b2pow-- > 0) {
      result.checkBitLength();
      result = result.mul(result);
    }
    if (n < 0) {
      return result.inverseFraction();
    }
    return result;
  }

  @Override
  public IReal re() {
    return this;
  }

  /**
   * Return a new rational representing <code>this - other</code>.
   *
   * @param other Rational to subtract.
   * @return Difference of <code>this</code> and <code>other</code>.
   */
  @Override
  public IFraction sub(IFraction other) {
    return add(other.negate());
  }

  /**
   * Returns <code>(this-s)/d</code>.
   *
   * @param s
   * @param d the denominator
   * @return <code>(this-s)/d</code>
   */
  public IFraction subdiv(IFraction s, FractionSym d) {
    return sub(s).div(d);
  }

  /** {@inheritDoc} */
  @Override
  // CPD-OFF literal constant table
  public IRational subtract(IRational that) {
    if (isZero()) {
      return that.negate();
    }
    return this.add(that.negate());
  }

  /** {@inheritDoc} */
  @Override
  public IReal subtractFrom(IReal that) {
    if (that instanceof IRational) {
      return this.add((IRational) that.negate());
    }
    return Num.valueOf(doubleValue() - that.doubleValue());
  }

  /** {@inheritDoc} */
  @Override
  public IExpr times(IExpr that) {
  // CPD-ON
    if (that instanceof INumber) {
      return this.times((INumber) that);
    }
    return IFraction.super.times(that);
  }

  /** {@inheritDoc} */
  @Override
  public INumber times(INumber that) {
    if (that instanceof IFraction) {
      return this.mul((IFraction) that).normalize();
    }
    if (that instanceof IntegerSym) {
      return this.mul(valueOf(((IntegerSym) that).fIntValue)).normalize();
    }
    if (that instanceof BigIntegerSym) {
      return this.mul(valueOf(((BigIntegerSym) that).fBigIntValue)).normalize();
    }
    if (that instanceof ComplexSym) {
      return ((ComplexSym) that).multiply(ComplexSym.valueOf(this)).normalize();
    }
    if (that instanceof ApfloatNum) {
      return ((ApfloatNum) that).times(this);
    }
    // a machine number, or an arbitrary-precision complex number: an inexact 1 or -1 gives an
    // inexact product, 1./225 is no exact 1/225
    return AbstractIntegerSym.timesInexact(this, that);
  }

  @Override
  public int toIntRoot() {
    return Config.INVALID_INT;
  }

  @Override
  public int toIntRoot(int defaultValue) {
    return defaultValue;
  }
}

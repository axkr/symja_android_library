package org.matheclipse.core.expression;

import static org.matheclipse.core.expression.NumberUtil.hasIntValue;
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
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.BigIntegerLimitExceeded;
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
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntRBTreeMap;

/**
 * Abstract base class for FractionSym and BigFractionSym
 *
 * @see FractionSym
 * @see BigFractionSym
 */
public abstract class AbstractFractionSym implements IFraction {
  private static final long serialVersionUID = -8743141041586314213L;

  public static BigInteger gcd(BigInteger i1, BigInteger i2) {
    if (i1.equals(BigInteger.ONE) || i2.equals(BigInteger.ONE)) {
      return BigInteger.ONE;
    } else if (hasIntValue(i1) && hasIntValue(i2)) {
      return BigInteger.valueOf(IInteger.gcd(i1.intValue(), i2.intValue()));
    } else {
      return i1.gcd(i2);
    }
  }

  public static IFraction valueOf(BigFraction fraction) {
    IFraction f = fractionOf(fraction.getNumerator(), fraction.getDenominator());
    return f != null ? f : new BigFractionSym(fraction);
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
    return f != null ? f : new BigFractionSym(numerator, denominator);
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
   * @param denominator Denominator.
   * @return
   */
  public static IFraction valueOf(long numerator, long denominator) {
    if (denominator == 0) {
      throw getDivisionTroughZeroException(F.ZZ(numerator)); // Infinite expression `1` encountered.
    } else if (numerator == 0) {
      return FractionSym.ZERO;
    } else if (numerator == denominator) {
      return FractionSym.ONE;
    }
    if (numerator > Long.MIN_VALUE && denominator > Long.MIN_VALUE) {
      if (denominator < 0) {
        numerator = -numerator;
        denominator = -denominator;
      }
      if (numerator != 1 && denominator != 1) {
        long gcd = Math.abs(ArithmeticUtils.gcd(numerator, denominator));
        if (gcd != 1L) {
          if (Config.TRACE_BASIC_ARITHMETIC && EvalEngine.get().isTraceMode()) {
            if (EvalEngine.get().isTraceMode()) {
              IAST divide = F.Rational(F.ZZ(numerator), F.ZZ(denominator));
              EvalEngine.get().addTraceStep(divide, divide,
                  F.List(S.Rational, F.$str("FractionCancelGCD"), divide, F.ZZ(gcd)));
            }
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
    }
    return new BigFractionSym(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator));
  }

  private static ArgumentTypeException getDivisionTroughZeroException(IInteger num) {
    String str = Errors.getMessage("infy", F.list(F.Rational(num, F.C0)), EvalEngine.get());
    return new ArgumentTypeException(str);
  }

  public static IFraction valueOf(IInteger numerator) {
    if (numerator instanceof IntegerSym) {
      return valueOf(((IntegerSym) numerator).fIntValue);
    }
    return valueOf(numerator.toBigNumerator());
  }

  public static IFraction valueOf(BigInteger numerator) {
    return valueOf(numerator, BigInteger.ONE);
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

  private static IFraction rationalize(double value, DoubleFunction<BigFraction> f) {
    try {
      BigFraction fraction = f.apply(value < 0 ? -value : value);
      if (fraction != null) {// && fraction.getNumerator().signum()!=0) {
        return valueOf(value < 0 ? fraction.negate() : fraction);
      }
    } catch (MathRuntimeException e) { // assume no solution
    }
    return null;
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
    Apfloat real = h.divide(new Apfloat(toBigNumerator(), h.precision()),
        new Apfloat(toBigDenominator(), h.precision()));
    return new Apcomplex(real);
  }

  @Override
  public ApfloatNum apfloatNumValue() {
    return ApfloatNum.valueOf(toBigNumerator(), toBigDenominator());
  }

  @Override
  public Apfloat apfloatValue() {
    long precision = EvalEngine.getApfloat().precision();
    Apfloat n = new Apfloat(toBigNumerator(), precision);
    Apfloat d = new Apfloat(toBigDenominator(), precision);
    return n.divide(d);
  }

  @Override
  public Pair asNumerDenom() {
    return F.pair(numerator(), denominator());
  }

  @Override
  public int compareTo(IExpr expr) {
    if (expr.isNumber() && !expr.isReal()) {
      int c = this.compareTo(expr.re());
      if (c != 0) {
        return c;
      }
      IExpr im = expr.im();
      return im.isPositive() ? -1 : im.isNegative() ? 1 : IExpr.compareHierarchy(this, expr);
    }
    return IExpr.compareHierarchy(this, expr);
  }

  @Override
  public IExpr copy() {
    return this;
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
    IASTAppendable result = den.factorInteger();

    // negate the exponents of the denominator part
    result.forEach(list -> ((IASTMutable) list).set(2, list.second().negate()));
    // add the factors from the numerator part
    result.appendArgs(num.factorInteger());
    EvalAttributes.sort(result);
    return result;
  }

  @Override
  public IAST factorSmallPrimes(int numerator, int root) {
    IInteger b = numerator();
    boolean isNegative = false;
    if (complexSign() < 0) {
      b = b.negate();
      isNegative = true;
    }
    if (numerator != 1) {
      b = b.powerRational(numerator);
    }
    IInteger d = denominator();
    if (numerator != 1) {
      d = d.powerRational(numerator);
    }
    Int2IntMap bMap = new Int2IntRBTreeMap();
    BigInteger number = b.toBigNumerator();
    IAST bAST = AbstractIntegerSym.factorBigInteger(number, isNegative, numerator, root, bMap);
    Int2IntMap dMap = new Int2IntRBTreeMap();
    number = d.toBigNumerator();
    IAST dAST = AbstractIntegerSym.factorBigInteger(number, false, numerator, root, dMap);
    if (bAST.isPresent()) {
      if (dAST.isPresent()) {
        return F.Times(bAST, F.Power(dAST, F.CN1));
      }
      return F.Times(bAST, F.Power(denominator(), F.QQ(-numerator, root)));
    } else if (dAST.isPresent()) {
      return F.Times(F.Power(numerator(), F.QQ(numerator, root)), F.Power(dAST, F.CN1));
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
    }
    BigInteger tdenom = this.toBigDenominator();
    BigInteger odenom = that.toBigDenominator();
    BigInteger gcddenom = tdenom.gcd(odenom);
    BigInteger denom = tdenom.divide(gcddenom).multiply(odenom);
    BigInteger num = toBigNumerator().gcd(that.toBigNumerator());
    return AbstractFractionSym.valueOf(num, denom);
  }

  /** {@inheritDoc} */
  @Override
  public IInteger integerPart() {
    return isNegative() ? ceilFraction() : floorFraction();
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

  @Override
  public IReal im() {
    return F.C0;
  }

  @Override
  public double imDoubleValue() {
    return 0.0;
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
  public IReal re() {
    return this;
  }

  @Override
  public double reDoubleValue() {
    return doubleValue();
  }

  @Override
  public IRational roundClosest(IReal multiple) {
    if (!multiple.isRational()) {
      multiple = F.fraction(multiple.doubleValue(), Config.DOUBLE_EPSILON);
    }
    IInteger ii = this.divideBy((IRational) multiple).roundExpr();
    return ii.multiply((IRational) multiple);
  }

  @Override
  public ISymbol head() {
    return S.Rational;
  }

  @Override
  public int hierarchy() {
    return FRACTIONID;
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
    return doubleValue() > obj.doubleValue();
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
    return doubleValue() < obj.doubleValue();
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
  public long leafCountSimplify() {
    return 1 + numerator().leafCountSimplify() + denominator().leafCountSimplify();
  }

  @Override
  public long leafCount() {
    return 3;
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
      return ((IFraction) this).negate();
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
    return ((IFraction) this).negate();
  }

  /** {@inheritDoc} */
  @Override
  public INumber plus(INumber that) {
    if (that.isZero()) {
      return this;
    }
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
    if (that instanceof INum) {
      if (that instanceof ApfloatNum) {
        return apfloatNumValue().add(((ApfloatNum) that).apfloatNumValue());
      }
      return F.num(((Num) that).value + evalf());
    }
    if (that instanceof IComplexNum) {
      if (that instanceof ApcomplexNum) {
        return apcomplexNumValue().add(((ApcomplexNum) that).apcomplexNumValue());
      }
      return F.complexNum(evalfc().add(that.evalfc()));
    }
    throw new java.lang.ArithmeticException();
  }

  /** {@inheritDoc} */
  @Override
  public IExpr plus(IExpr that) {
    if (that instanceof INumber) {
      return this.plus((INumber) that);
    }
    return IFraction.super.plus(that);
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
      long n = ((IInteger) that).toLongDefault();
      if (n != Long.MIN_VALUE) {
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
      return inverse();
    }
    long exp = n;
    if (n < 0) {
      if (n == Long.MIN_VALUE) {
        throw new java.lang.ArithmeticException();
      }
      exp *= -1;
    }
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
      return result.inverse();
    }
    return result;
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

  /** {@inheritDoc} */
  @Override
  public int complexSign() {
    return toBigNumerator().signum();
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
  public INumber times(INumber that) {
    if (that.isZero()) {
      return F.C0;
    }
    if (that.isOne()) {
      return this;
    }
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
    if (that instanceof INum) {
      if (that instanceof ApfloatNum) {
        return apfloatNumValue().multiply(((ApfloatNum) that).apfloatNumValue());
      }
      return F.num(((Num) that).value * evalf());
    }
    if (that instanceof IComplexNum) {
      if (that instanceof ApcomplexNum) {
        return apcomplexNumValue().multiply(((ApcomplexNum) that).apcomplexNumValue());
      }
      return F.complexNum(evalfc().multiply(that.evalfc()));
    }
    throw new java.lang.ArithmeticException();
  }

  /** {@inheritDoc} */
  @Override
  public IExpr times(IExpr that) {
    if (that instanceof INumber) {
      return this.times((INumber) that);
    }
    return IFraction.super.times(that);
  }
}

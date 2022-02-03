package org.matheclipse.core.expression;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.DoubleFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.hipparchus.exception.LocalizedCoreFormats;
import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.fraction.BigFraction;
import org.hipparchus.util.ArithmeticUtils;
import org.hipparchus.util.FastMath;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.BigIntegerLimitExceeded;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISignedNumber;
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

  /**
   * Generate a {@link Stream stream} of convergents from a real number.
   * <p>
   * See:
   * <a href="https://github.com/Hipparchus-Math/hipparchus/issues/176">hipparchus/issues/176</a>
   *
   * @param value value to approximate
   * @param maxConbvergents maximum number of convergents.
   * @return stream of {@link BigFraction} convergents approximating {@code value}
   */
  // TODO: replace this by Hipparcus BigFraction.convergents() after next Hipparchus release
  public static Stream<BigFraction> convergents(double value, int maxConvergents) {
    if (FastMath.abs(value) > Integer.MAX_VALUE) {
      throw new MathIllegalStateException(LocalizedCoreFormats.FRACTION_CONVERSION_OVERFLOW, value,
          value, 1l);
    }
    return StreamSupport
        .stream(Spliterators.spliteratorUnknownSize(generatingIterator(value, maxConvergents),
            Spliterator.DISTINCT | Spliterator.ORDERED), false);
  }

  /**
   * Iterator for generating continuous fractions.
   *
   * @param value value to approximate
   * @param maxConbvergents maximum number of convergents.
   * @return iterator iterating over continuous fractions aproximating {@code value}
   * @since 2.1
   */
  private static Iterator<BigFraction> generatingIterator(double value, int maxConvergents) {
    return new Iterator<BigFraction>() {
      private static final long OVERFLOW = Integer.MAX_VALUE;
      private long p0 = 0;
      private long q0 = 1;
      private long p1 = 1;
      private long q1 = 0;
      private double r1 = value;
      private boolean stop = false;
      private int n = 0;

      /** {@inheritDoc} */
      @Override
      public boolean hasNext() {
        return n < maxConvergents && !stop;
      }

      /** {@inheritDoc} */
      @Override
      public BigFraction next() {
        ++n;

        final long a1 = (long) FastMath.floor(r1);
        long p2 = (a1 * p1) + p0;
        long q2 = (a1 * q1) + q0;

        final double convergent = (double) p2 / (double) q2;
        // stop = Precision.equals(convergent, value, 1);
        // if ((p2 > OVERFLOW || q2 > OVERFLOW) && !stop) {
        // throw new MathIllegalStateException(LocalizedCoreFormats.FRACTION_CONVERSION_OVERFLOW,
        // value, p2, q2);
        // }
        p0 = p1;
        p1 = p2;
        q0 = q1;
        q1 = q2;
        r1 = 1.0 / (r1 - a1);
        return new BigFraction(p2, q2);
      }

    };
  }

  private static final Logger LOGGER = LogManager.getLogger();

  public static BigInteger gcd(BigInteger i1, BigInteger i2) {
    if (i1.equals(BigInteger.ONE) || i2.equals(BigInteger.ONE))
      return BigInteger.ONE;
    int l1 = i1.bitLength();
    int l2 = i2.bitLength();
    if (l1 < 31 && l2 < 31) {
      return BigInteger.valueOf(ArithmeticUtils.gcd(i1.intValue(), i2.intValue()));
    } else if (l1 < 63 && l2 < 63) {
      return BigInteger.valueOf(ArithmeticUtils.gcd(i1.longValue(), i2.longValue()));
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
    if (BigInteger.ZERO.equals(denominator)) {
      throw getDivisionTroughZeroException(F.ZZ(numerator)); // Infinite expression `1` encountered.
    }
    if (denominator.bitLength() <= 31 && numerator.bitLength() <= 31) {
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
      if (numerator != 1 && denominator != 1) {
        long gcd = Math.abs(ArithmeticUtils.gcd(numerator, denominator));
        if (denominator < 0) {
          gcd = -gcd;
        }
        numerator /= gcd;
        denominator /= gcd;
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
    String str = IOFunctions.getMessage("infy", F.List(F.Rational(num, F.C0)), EvalEngine.get());
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
    return rationalize(value, v -> new BigFraction(v, epsilon, 200));
  }


  public static IFraction valueOfConvergent(double value) {
    return rationalize(value,
        v -> convergents(v, 20).filter(f -> isConverged(v, f)).findFirst().orElseThrow(
            () -> new NoSuchElementException("No converging fraction found for value " + value)));
  }

  private static boolean isConverged(double value, BigFraction result) {
    BigInteger denominator = result.getDenominator();
    double qSquared = denominator.multiply(denominator).doubleValue();
    double lhs = FastMath.abs(result.doubleValue() - value) * qSquared;
    return lhs < 1E-4;
  }

  private static IFraction rationalize(double value, DoubleFunction<BigFraction> f) {
    try {
      BigFraction fraction = f.apply(value < 0 ? -value : value);
      return valueOf(value < 0 ? fraction.negate() : fraction);
    } catch (MathIllegalStateException e) {
      return valueOf(new BigFraction(value));
    }
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
  public int compareTo(IExpr expr) {
    if (expr.isNumber()) {
      int c = this.compareTo(((INumber) expr).re());
      if (c != 0) {
        return c;
      }
    }
    return -1;
  }

  @Override
  public IExpr copy() {
    try {
      return (IExpr) clone();
    } catch (CloneNotSupportedException e) {
      LOGGER.error("AbstractFractionSym.copy() failed", e);
      return null;
    }
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
  public ISignedNumber divideBy(ISignedNumber that) {
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
    // SortedMap<Integer, Integer> bMap = new TreeMap<Integer, Integer>();
    Int2IntMap bMap = new Int2IntRBTreeMap();
    BigInteger number = b.toBigNumerator();
    IAST bAST = AbstractIntegerSym.factorBigInteger(number, isNegative, numerator, root, bMap);
    // SortedMap<Integer, Integer> dMap = new TreeMap<Integer, Integer>();
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
  public ISignedNumber im() {
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
  public ISignedNumber re() {
    return this;
  }

  @Override
  public double reDoubleValue() {
    return doubleValue();
  }

  @Override
  public IRational roundClosest(ISignedNumber multiple) {
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
  public boolean isGT(ISignedNumber obj) {
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
  public boolean isLT(ISignedNumber obj) {
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


  @Override
  public INumber numericNumber() {
    return F.num(this);
  }

  @Override
  public Num numValue() {
    return Num.valueOf(doubleValue());
  }

  @Override
  public ISignedNumber opposite() {
    return ((IFraction) this).negate();
  }

  /**
   * @param that
   * @return
   */
  @Override
  public IExpr plus(IExpr that) {
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
  public ISignedNumber subtractFrom(ISignedNumber that) {
    if (that instanceof IRational) {
      return this.add((IRational) that.negate());
    }
    return Num.valueOf(doubleValue() - that.doubleValue());
  }

  /** {@inheritDoc} */
  @Override
  public IExpr times(IExpr that) {
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
    return IFraction.super.times(that);
  }
}

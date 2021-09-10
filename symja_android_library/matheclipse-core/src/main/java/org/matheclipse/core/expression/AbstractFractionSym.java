package org.matheclipse.core.expression;

import java.math.BigInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.fraction.BigFraction;
import org.hipparchus.util.ArithmeticUtils;
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
  private static final Logger LOGGER = LogManager.getLogger();

  public static final FractionSym ZERO = new FractionSym(0, 1);

  public static final FractionSym ONE = new FractionSym(1, 1);

  public static final FractionSym MONE = new FractionSym(-1, 1);
  /** */
  private static final long serialVersionUID = -8743141041586314213L;

  public static BigInteger gcd(BigInteger i1, BigInteger i2) {
    if (i1.equals(BigInteger.ONE) || i2.equals(BigInteger.ONE)) return BigInteger.ONE;
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
    BigInteger num = fraction.getNumerator();
    BigInteger den = fraction.getDenominator();
    if (BigInteger.ZERO.equals(den)) {
      // Infinite expression `1` encountered.
      String str =
          IOFunctions.getMessage("infy", F.List(F.Rational(F.ZZ(num), F.C0)), EvalEngine.get());
      throw new ArgumentTypeException(str);
    }
    if (den.bitLength() <= 31 && num.bitLength() <= 31) {
      return valueOf(num.intValue(), den.intValue());
    }
    return new BigFractionSym(fraction);
  }

  public static IFraction valueOf(BigInteger num) {
    return valueOf(num, BigInteger.ONE);
  }

  /**
   * Construct a rational from two BigIntegers. Use this method to create a rational number if the
   * numerator or denominator may be to big to fit in an Java int. This method normalizes the
   * rational number.
   *
   * @param num Numerator
   * @param den Denominator
   * @return
   */
  public static IFraction valueOf(BigInteger num, BigInteger den) {
    if (BigInteger.ZERO.equals(den)) {
      // Infinite expression `1` encountered.
      String str =
          IOFunctions.getMessage("infy", F.List(F.Rational(F.ZZ(num), F.C0)), EvalEngine.get());
      throw new ArgumentTypeException(str);
    }
    if (den.bitLength() <= 31 && num.bitLength() <= 31) {
      return valueOf(num.intValue(), den.intValue());
    }
    return new BigFractionSym(num, den);
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
   * @param newnum Numerator.
   * @return
   */
  public static IFraction valueOf(long newnum) {
    if (newnum == 0) {
      return ZERO;
    }
    if (newnum == 1) {
      return ONE;
    }
    if (newnum == -1) {
      return MONE;
    }

    if (Integer.MIN_VALUE < newnum && newnum <= Integer.MAX_VALUE) {
      return new FractionSym((int) newnum, 1);
    }
    return new BigFractionSym(BigInteger.valueOf(newnum), BigInteger.ONE);
  }

  /**
   * Construct a rational from two longs. Use this method to create a rational number. This method
   * normalizes the rational number and may return a previously created one. This method does not
   * work if called with value Long.MIN_VALUE.
   *
   * @param newnum Numerator.
   * @param newdenom Denominator.
   * @return
   */
  public static IFraction valueOf(long newnum, long newdenom) {
    if (newdenom == 0) {
      // Infinite expression `1` encountered.
      String str =
          IOFunctions.getMessage(
              "infy", F.List(F.Rational(F.ZZ(newnum), F.ZZ(newdenom))), EvalEngine.get());
      throw new ArgumentTypeException(str);
    }
    if (newnum > Long.MIN_VALUE && newdenom > Long.MIN_VALUE) {
      if (newdenom != 1) {
        long gcd2 = Math.abs(ArithmeticUtils.gcd(newnum, newdenom));
        if (newdenom < 0) {
          gcd2 = -gcd2;
        }
        newnum /= gcd2;
        newdenom /= gcd2;
      }

      if (newdenom == 1) {
        if (newnum == 0) {
          return ZERO;
        }
        if (newnum == 1) {
          return ONE;
        }
        if (newnum == -1) {
          return MONE;
        }
      }

      if (Integer.MIN_VALUE < newnum
          && newnum <= Integer.MAX_VALUE
          && newdenom <= Integer.MAX_VALUE) {
        return new FractionSym((int) newnum, (int) newdenom);
      }
    }
    return new BigFractionSym(BigInteger.valueOf(newnum), BigInteger.valueOf(newdenom));
  }

  /**
   * Rationalize the given double value with <code>Config.DOUBLE_EPSILON</code> maximum error
   * allowed.
   *
   * @param value
   * @return
   */
  public static IFraction valueOfEpsilon(final double value) {
    return valueOfEpsilon(value, Config.DOUBLE_EPSILON);
  }

  /**
   * Rationalize the given double value with <code>epsilon</code> maximum error allowed.
   *
   * @param value the double value to convert to a fraction.
   * @param epsilon maximum error allowed. The resulting fraction is within epsilon of value, in
   *     absolute terms.
   * @return
   */
  public static IFraction valueOfEpsilon(final double value, final double epsilon) {
    BigFraction fraction;
    try {
      if (value < 0.0) {
        fraction = new BigFraction(-value, epsilon, 200);
        return valueOf(fraction.negate());
      } else {
        fraction = new BigFraction(value, epsilon, 200);
        return valueOf(fraction);
      }
    } catch (MathIllegalStateException e) {
      fraction = new BigFraction(value);
    }
    return valueOf(fraction);
  }

  /**
   * Compute the absolute of this rational.
   *
   * @return Rational that is equal to the absolute value of this rational.
   */
  @Override
  public abstract IFraction abs();

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

  @Override
  public abstract IFraction add(IFraction other);

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

  public Apcomplex apcomplexValue() {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    Apfloat real =
        h.divide(
            new Apfloat(toBigNumerator(), h.precision()),
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
  public abstract IInteger ceilFraction();

  @Override
  public int compareTo(final IExpr expr) {
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
    final INumber cTemp = normalize();
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
  public abstract IFraction inverse();

  @Override
  public boolean isGT(ISignedNumber obj) {
    if (obj instanceof FractionSym) {
      return compareTo((obj)) > 0;
    }
    if (obj instanceof IInteger) {
      return compareTo(
              AbstractFractionSym.valueOf(((IInteger) obj).toBigNumerator(), BigInteger.ONE))
          > 0;
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
              AbstractFractionSym.valueOf(((IInteger) obj).toBigNumerator(), BigInteger.ONE))
          < 0;
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
  public abstract IFraction negate();

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
  public IExpr plus(final IExpr that) {
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

  /** {@inheritDoc} */
  @Override
  public final IFraction powerRational(final long n) throws ArithmeticException {
    if (n == 0L) {
      if (!this.isZero()) {
        return AbstractFractionSym.ONE;
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
      final long bitLength = toBigNumerator().bitLength() + toBigDenominator().bitLength();
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
  public IExpr times(final IExpr that) {
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

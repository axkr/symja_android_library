package org.matheclipse.core.expression;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Function;
import org.hipparchus.fraction.BigFraction;
import org.hipparchus.util.FastMath;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.exception.BigIntegerLimitExceeded;
import org.matheclipse.core.eval.util.SourceCodeProperties;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.ParserConfig;
import edu.jas.arith.BigRational;

/**
 * IFraction implementation which uses methods of the Apache {@link BigFraction} methods.
 *
 * @see IFraction
 * @see FractionSym
 */
public class BigFractionSym extends AbstractFractionSym {

  /** */
  private static final long serialVersionUID = -553051997353641162L;

  transient int fHashValue;

  BigFraction fFraction;

  BigFractionSym(BigFraction fraction) {
    fFraction = fraction;
    checkBitLength();
  }

  // public void checkBitLength( ) {
  // BigInteger temp = fFraction.getNumerator();
  // if (Config.MAX_BIT_LENGTH < temp.bitLength()) {
  // BigIntegerLimitExceeded.throwIt(temp.bitLength());
  // }
  // temp = fFraction.getDenominator();
  // if (Config.MAX_BIT_LENGTH < temp.bitLength()) {
  // BigIntegerLimitExceeded.throwIt(temp.bitLength());
  // }
  // }

  /**
   * Construct a rational from two BigIntegers. <b>Note:</b> the constructor is package private and
   * does not normalize. Use the static constructor valueOf instead.
   *
   * @param nom Numerator
   * @param denom Denominator
   */
  BigFractionSym(BigInteger nom, BigInteger denom) {
    int bitLength = nom.bitLength();
    if (Config.MAX_BIT_LENGTH < bitLength) {
      BigIntegerLimitExceeded.throwIt(bitLength);
    }
    bitLength = denom.bitLength();
    if (Config.MAX_BIT_LENGTH < bitLength) {
      BigIntegerLimitExceeded.throwIt(bitLength);
    }
    fFraction = new BigFraction(nom, denom);
  }

  @Override
  public IFraction abs() {
    if (isNegative()) {
      return valueOf(fFraction.abs());
    }
    return this;
  }

  /**
   * Return a new rational representing <code>this + other</code>.
   *
   * @param other Rational to add.
   * @return Sum of <code>this</code> and <code>other</code>.
   */
  @Override
  public IFraction add(IFraction other) {
    if (other.isZero()) {
      return this;
    }
    if (this.isZero()) {
      return other;
    }

    if (other instanceof BigFractionSym) {
      BigFraction res = fFraction.add(((BigFractionSym) other).toBigFraction());
      return valueOf(res);
    }

    BigInteger tdenom = toBigDenominator();
    BigInteger odenom = other.toBigDenominator();
    if (tdenom.equals(odenom)) {
      return valueOf(toBigNumerator().add(other.toBigNumerator()), tdenom);
    }
    BigInteger gcd = tdenom.gcd(odenom);
    BigInteger tdenomgcd = tdenom.divide(gcd);
    BigInteger odenomgcd = odenom.divide(gcd);
    BigInteger newnum =
        toBigNumerator().multiply(odenomgcd).add(other.toBigNumerator().multiply(tdenomgcd));
    BigInteger newdenom = tdenom.multiply(odenomgcd);
    return valueOf(newnum, newdenom);
  }

  @Override
  public IRational add(IRational parm1) {
    if (parm1.isZero()) {
      return this;
    }
    if (parm1 instanceof IFraction) {
      return add((IFraction) parm1).normalize();
    }
    IInteger p1 = (IInteger) parm1;
    BigInteger newnum = toBigNumerator().add(toBigDenominator().multiply(p1.toBigNumerator()));
    return valueOf(newnum, toBigDenominator()).normalize();
  }

  @Override
  public IInteger ceil() {
    if (isIntegral()) {
      return AbstractIntegerSym.valueOf(toBigNumerator());
    }
    BigInteger div = toBigNumerator().divide(toBigDenominator());
    if (toBigNumerator().signum() > 0)
      div = div.add(BigInteger.ONE);
    return AbstractIntegerSym.valueOf(div);
  }

  /**
   * Return a new rational representing the smallest integral rational not smaller than <code>this
   * </code>.
   *
   * @return Next bigger integer of <code>this</code>.
   */
  @Override
  public IInteger ceilFraction() {
    if (isIntegral()) {
      return F.ZZ(toBigNumerator());
    }
    BigInteger div = toBigNumerator().divide(toBigDenominator());
    if (toBigNumerator().signum() > 0) {
      div = div.add(BigInteger.ONE);
    }
    return F.ZZ(div);
  }

  @Override
  public int compareAbsValueToOne() {
    BigFraction temp = fFraction;
    if (fFraction.compareTo(BigFraction.ZERO) < 0) {
      temp = temp.negate();
    }
    return temp.compareTo(BigFraction.ONE);
  }

  @Override
  public int compareInt(final int value) {
    BigInteger dOn = toBigDenominator().multiply(BigInteger.valueOf(value));
    return toBigNumerator().compareTo(dOn);
  }

  @Override
  public int compareTo(IExpr expr) {
    if (expr instanceof IRational) {
      if (expr instanceof IFraction) {
        BigInteger valthis = toBigNumerator().multiply(((IFraction) expr).toBigDenominator());
        BigInteger valo = ((IFraction) expr).toBigNumerator().multiply(toBigDenominator());
        return valthis.compareTo(valo);
      }
      if (expr instanceof IInteger) {
        return fFraction
            .compareTo(new BigFraction(((IInteger) expr).toBigNumerator(), BigInteger.ONE));
      }
    }
    if (expr.isReal()) {
      return Double.compare(fFraction.doubleValue(), ((IReal) expr).doubleValue());
    }
    // if (expr.isNumber()) {
    // int c = this.compareTo(((INumber) expr).re());
    // if (c != 0) {
    // return c;
    // }
    // if (expr.isReal()) {
    // return 1;
    // }
    // return F.C0.compareTo(((INumber) expr).im());
    // }
    return super.compareTo(expr);
  }

  @Override
  public ComplexNum complexNumValue() {
    // double precision complex number
    // double nr = toBigNumerator().doubleValue();
    // double dr = toBigDenominator().doubleValue();
    double real = this.doubleValue();
    return ComplexNum.valueOf(real);
  }

  /** {@inheritDoc} */
  @Override
  public IRational dec() {
    return add(F.CN1);
  }

  /** {@inheritDoc} */
  @Override
  public IRational inc() {
    return add(F.C1);
  }

  /**
   * Return a new rational representing <code>this / other</code>.
   *
   * @param other Rational to divide.
   * @return Quotient of <code>this</code> and <code>other</code>.
   */
  @Override
  public IFraction div(IFraction other) {
    if (other.isOne()) {
      return this;
    }
    if (other.isMinusOne()) {
      return this.negate();
    }
    BigInteger denom = toBigDenominator().multiply(other.toBigNumerator());
    BigInteger nom = toBigNumerator().multiply(other.toBigDenominator());
    // +-inf : -c = -+inf
    if (denom.equals(BigInteger.ZERO) && other.toBigNumerator().signum() == -1)
      nom = nom.negate();
    return valueOf(nom, denom);
  }

  @Override
  public IInteger[] divideAndRemainder() {
    IInteger[] result = new IInteger[2];
    BigInteger[] intResult = toBigNumerator().divideAndRemainder(toBigDenominator());
    result[0] = AbstractIntegerSym.valueOf(intResult[0]);
    result[1] = AbstractIntegerSym.valueOf(intResult[1]);
    return result;
  }

  @Override
  public double doubleValue() {
    // Thanks to org.hipparchus.fraction.BigFraction#doubleValue
    BigInteger numerator = fFraction.getNumerator();
    BigInteger denominator = fFraction.getDenominator();
    double result = numerator.doubleValue() / denominator.doubleValue();
    if (Double.isInfinite(result) || Double.isNaN(result)) {
      int shift = FastMath.max(numerator.bitLength(),denominator.bitLength()) - FastMath.getExponent(Double.MAX_VALUE);
      result = numerator.shiftRight(shift).doubleValue() / denominator.shiftRight(shift).doubleValue();
    }

    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof BigFractionSym) {
      BigFractionSym r = (BigFractionSym) o;
      return fFraction.equals(r.fFraction);
    }
    // EQ_CHECK_FOR_OPERAND_NOT_COMPATIBLE_WITH_THIS
    // if (o instanceof FractionSym) {
    // final FractionSym r = (FractionSym) o;
    // return equalsFraction(r.fNumerator, r.fDenominator);
    // }
    return false;
  }

  @Override
  public final boolean equalsFraction(final int numerator, final int denominator) {
    BigInteger num = fFraction.getNumerator();
    BigInteger den = fFraction.getDenominator();
    return num.intValue() == numerator && den.intValue() == denominator && num.bitLength() <= 31
        && den.bitLength() <= 31;
  }

  @Override
  public boolean equalsInt(final int numerator) {
    BigInteger num = fFraction.getNumerator();
    return num.intValue() == numerator && fFraction.getDenominator().equals(BigInteger.ONE)
        && num.bitLength() <= 31;
  }

  @Override
  public IInteger floor() {
    if (isIntegral()) {
      return AbstractIntegerSym.valueOf(toBigNumerator());
    }
    BigInteger div = toBigNumerator().divide(toBigDenominator());
    if (toBigNumerator().signum() < 0) {
      div = div.subtract(BigInteger.ONE);
    }
    return AbstractIntegerSym.valueOf(div);
  }

  /**
   * Return a new rational representing the biggest integral rational not bigger than <code>this
   * </code>.
   *
   * @return Next smaller integer of <code>this</code>.
   */
  @Override
  public IInteger floorFraction() {
    if (isIntegral()) {
      return F.ZZ(toBigNumerator());
    }
    BigInteger div = toBigNumerator().divide(toBigDenominator());
    if (toBigNumerator().signum() < 0) {
      div = div.subtract(BigInteger.ONE);
    }
    return F.ZZ(div);
  }

  /**
   * Returns the fractional part of the rational
   *
   * @return Next smaller integer of <code>this</code>.
   */
  @Override
  public IFraction fractionalPart() {
    if (isIntegral()) {
      return FractionSym.ZERO;
    }
    BigInteger den = fFraction.getDenominator();
    BigInteger newnum = fFraction.getNumerator().mod(den);
    if (isNegative()) {
      return AbstractFractionSym.valueOf(newnum.negate(), den);
    }
    return AbstractFractionSym.valueOf(newnum, den);
  }

  @Override
  public String fullFormString() {
    StringBuilder buf = new StringBuilder("Rational");
    if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
      buf.append('(');
    } else {
      buf.append('[');
    }
    buf.append(fFraction.getNumerator().toString());
    buf.append(',');
    buf.append(fFraction.getDenominator().toString());
    if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
      buf.append(')');
    } else {
      buf.append(']');
    }
    return buf.toString();
  }

  @Override
  public IExpr gcd(IExpr that) {
    if (that instanceof IFraction) {
      BigFraction arg2 = ((IFraction) that).toBigFraction();
      return valueOf(fFraction.getNumerator().gcd(arg2.getNumerator()),
          AbstractIntegerSym.lcm(fFraction.getDenominator(), arg2.getDenominator()));
    }
    return super.gcd(that);
  }

  /**
   * Compute the gcd of two rationals (this and other). The gcd is the rational number, such that
   * dividing this and other with the gcd will yield two co-prime integers.
   *
   * @param that the second rational argument.
   * @return the gcd of this and other.
   */
  @Override
  public IFraction gcd(IFraction that) {
    if (that.isZero()) {
      return this;
    }
    BigInteger tdenom = this.toBigDenominator();
    BigInteger odenom = that.toBigDenominator();
    BigInteger gcddenom = tdenom.gcd(odenom);
    BigInteger denom = tdenom.divide(gcddenom).multiply(odenom);
    BigInteger num = toBigNumerator().gcd(that.toBigNumerator());
    return AbstractFractionSym.valueOf(num, denom);
  }

  @Override
  public BigInteger toBigDenominator() {
    return fFraction.getDenominator();
  }

  @Override
  public BigInteger toBigNumerator() {
    return fFraction.getNumerator();
  }

  /** {@inheritDoc} */
  @Override
  public BigFraction toBigFraction() {
    return fFraction;
  }

  @Override
  public BigRational toBigRational() {
    return new BigRational(new edu.jas.arith.BigInteger(toBigNumerator()),
        new edu.jas.arith.BigInteger(toBigDenominator()));
  }

  @Override
  public int hashCode() {
    if (fHashValue == 0 && fFraction != null) {
      fHashValue = fFraction.hashCode();
    }
    return fHashValue;
  }

  /**
   * Return a new rational representing <code>this / other</code>.
   *
   * @param other Rational to divide.
   * @return Quotient of <code>this</code> and <code>other</code>.
   */
  public IFraction idiv(IFraction other) {
    BigInteger num = toBigDenominator().multiply(other.toBigNumerator());
    BigInteger denom = toBigNumerator().multiply(other.toBigDenominator());

    if (denom.equals(BigInteger.ZERO) && toBigNumerator().signum() == -1) {
      num = num.negate();
    }
    return valueOf(num, denom);
  }

  @Override
  public CharSequence internalJavaString(SourceCodeProperties properties, int depth,
      Function<ISymbol, ? extends CharSequence> variables) {
    String prefix = SourceCodeProperties.getPrefixF(properties);
    BigInteger numerator = fFraction.getNumerator();
    BigInteger denominator = fFraction.getDenominator();
    if (NumberUtil.hasIntValue(numerator) && NumberUtil.hasIntValue(denominator)) {
      int num = numerator.intValue();
      if (num == 1) {
        switch (denominator.intValue()) {
          case 2:
            return prefix + "C1D2";
          case 3:
            return prefix + "C1D3";
          case 4:
            return prefix + "C1D4";
          default:
        }
      } else if (num == -1) {
        switch (denominator.intValue()) {
          case 2:
            return prefix + "CN1D2";
          case 3:
            return prefix + "CN1D3";
          case 4:
            return prefix + "CN1D4";
          default:
        }
      }
    }
    if (NumberUtil.hasLongValue(numerator) && NumberUtil.hasLongValue(denominator)) {
      return prefix + "QQ(" + numerator.toString() + "L," + denominator.toString() + "L)";
    } else {
      CharSequence numCode = new BigIntegerSym(numerator).internalJavaString(properties, 0, null);
      CharSequence denCode = new BigIntegerSym(denominator).internalJavaString(properties, 0, null);
      return prefix + "QQ(" + numCode + "," + denCode + ")";
    }
  }

  /**
   * Returns a new rational representing the inverse of <code>this</code>.
   *
   * @return Inverse of <code>this</code>.
   */
  @Override
  public IFraction inverse() {
    return valueOf(fFraction.reciprocal());
  }

  /**
   * Check whether this rational represents an integral value (i.e. the denominator equals 1).
   *
   * @return <code>true</code> iff value is integral.
   */
  @Override
  public boolean isIntegral() {
    return fFraction.getDenominator().equals(BigInteger.ONE);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isMinusOne() {
    return fFraction.equals(BigFraction.MINUS_ONE);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNegative() {
    return fFraction.getNumerator().compareTo(BigInteger.ZERO) < 0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isOne() {
    return fFraction.equals(BigFraction.ONE);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPositive() {
    return fFraction.getNumerator().compareTo(BigInteger.ZERO) > 0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isZero() {
    return fFraction.equals(BigFraction.ZERO);
  }

  /**
   * Return a new rational representing <code>this * other</code>.
   *
   * @param other big integer to multiply.
   * @return Product of <code>this</code> and <code>other</code>.
   */
  public IFraction mul(BigInteger other) {
    if (other.bitLength() < 2) {
      int oint = other.intValue();
      if (oint == 1) {
        return this;
      }
      if (oint == -1) {
        return this.negate();
      }
      if (oint == 0) {
        return FractionSym.ZERO;
      }
    }
    return valueOf(toBigNumerator().multiply(other), toBigDenominator());
  }

  @Override
  public IRational multiply(IRational parm1) {
    if (parm1.isOne()) {
      return this;
    }
    if (parm1.isZero()) {
      return parm1;
    }
    if (parm1.isMinusOne()) {
      return this.negate();
    }
    if (parm1 instanceof IFraction) {
      return mul((IFraction) parm1).normalize();
    }
    IInteger p1 = (IInteger) parm1;
    BigInteger newnum = toBigNumerator().multiply(p1.toBigNumerator());
    return valueOf(newnum, toBigDenominator()).normalize();
  }

  @Override
  public IRational multiply(int n) {
    if (n == 1) {
      return this;
    }
    if (n == 0) {
      return FractionSym.ZERO;
    }
    if (n == -1) {
      return this.negate();
    }
    BigInteger newnum = toBigNumerator().multiply(BigInteger.valueOf(n));
    return valueOf(newnum, toBigDenominator()).normalize();
  }

  /**
   * Returns a new rational equal to <code>-this</code>.
   *
   * @return <code>-this</code>
   */
  @Override
  public IFraction negate() {
    return valueOf(fFraction.negate());
  }

  @Override
  public IRational normalize() {
    if (toBigDenominator().equals(BigInteger.ONE)) {
      return F.ZZ(toBigNumerator());
    }
    if (toBigNumerator().equals(BigInteger.ZERO)) {
      return F.C0;
    }
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public IInteger roundExpr() {
    return AbstractIntegerSym.valueOf(NumberUtil.round(fFraction, BigDecimal.ROUND_HALF_EVEN));
  }

  /** {@inheritDoc} */
  @Override
  public int complexSign() {
    return fFraction.getNumerator().signum();
  }

  /** {@inheritDoc} */
  @Override
  public int toInt() throws ArithmeticException {
    if (toBigDenominator().equals(BigInteger.ONE)) {
      return NumberUtil.intValueExact(toBigNumerator());
      // int val = NumberUtil.toIntDefault(toBigNumerator());
      // if (val != Integer.MIN_VALUE) {
      // return val;
      // }
    } else if (toBigNumerator().equals(BigInteger.ZERO)) {
      return 0;
    }
    throw new ArithmeticException("toInt: denominator != 1");
  }

  /** {@inheritDoc} */
  @Override
  public int toIntDefault(int defaultValue) {
    if (toBigDenominator().equals(BigInteger.ONE)) {
      try {
        return NumberUtil.intValueExact(toBigNumerator());
      } catch (java.lang.ArithmeticException aex) {
        return defaultValue;
      }
    }
    return fFraction.equals(BigFraction.ZERO) ? 0 : defaultValue;
  }

  /** {@inheritDoc} */
  @Override
  public long toLongDefault(long defaultValue) {
    if (toBigDenominator().equals(BigInteger.ONE)) {
      try {
        BigInteger numerator = toBigNumerator();
        if (NumberUtil.hasLongValue(numerator)) {
          // Android doesn't know method longValueExact
          return numerator.longValue();
        }
      } catch (java.lang.ArithmeticException aex) {
        //
      }
      return defaultValue;
    }
    return fFraction.equals(BigFraction.ZERO) ? 0 : defaultValue;
  }

  /** {@inheritDoc} */
  @Override
  public long toLong() throws ArithmeticException {
    if (toBigDenominator().equals(BigInteger.ONE)) {
      if (NumberUtil.hasLongValue(toBigNumerator())) {
        // Android doesn't know method longValueExact
        return toBigNumerator().longValue();
      }
    } else if (toBigNumerator().equals(BigInteger.ZERO)) {
      return 0L;
    }
    throw new ArithmeticException("toLong: denominator != 1");
  }

  @Override
  public String toString() {
    try {
      StringBuilder sb = new StringBuilder();
      OutputFormFactory.get().convertFraction(sb, this, Integer.MIN_VALUE,
          OutputFormFactory.NO_PLUS_CALL);
      return sb.toString();
    } catch (Exception e1) {
      // fall back to simple output format
      return fFraction.getNumerator().toString() + "/" + fFraction.getDenominator().toString();
    }
  }
}

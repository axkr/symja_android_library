package org.matheclipse.core.expression;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Function;
import org.hipparchus.fraction.BigFraction;
import org.hipparchus.util.ArithmeticUtils;
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
 * IFraction implementation which reimplements methods of the Apache <code>
 * org.apache.commons.math3.fraction.Fraction</code> methods.
 *
 * @see AbstractFractionSym
 * @see BigFractionSym
 */
public class FractionSym extends AbstractFractionSym {
  private static final long serialVersionUID = 1225728601457694359L;

  public static final FractionSym ZERO = new FractionSym(0, 1);
  public static final FractionSym ONE = new FractionSym(1, 1);
  public static final FractionSym MONE = new FractionSym(-1, 1);
  public static final FractionSym TWO = new FractionSym(2, 1);

  /**
   * Check if numerator and denominator are equal and the numerator isn't zero.
   *
   * @param num Numerator
   * @param den Denominator
   * @return
   */
  private static boolean isOne(long num, long den) {
    return num == den && num != 0;
  }

  int fNumerator;

  int fDenominator;

  /** constructor for serialization */
  private FractionSym() {}

  /**
   * Construct a rational from two ints. The constructor is private and does not normalize. Use the
   * static constructor valueOf instead.
   *
   * @param nom Numerator
   * @param denom Denominator
   */
  FractionSym(int nom, int denom) {
    fNumerator = nom;
    fDenominator = denom;
  }

  /**
   * Compute the absolute of this rational.
   *
   * @return Rational that is equal to the absolute value of this rational.
   */
  @Override
  public IFraction abs() {
    if (isNegative()) {
      return valueOf(Math.abs((long) fNumerator), fDenominator);
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
    if (isZero()) {
      return other;
    }
    if (other instanceof BigFractionSym) {
      return ((BigFractionSym) other).add(this);
    } else {
      FractionSym fs = (FractionSym) other;
      if (fs.fNumerator == 0) {
        return this;
      }
      if (fDenominator == fs.fDenominator) {
        return valueOf((long) fNumerator + fs.fNumerator, fDenominator);
      }
      int gcd = ArithmeticUtils.gcd(fDenominator, fs.fDenominator);
      if (gcd == 1) {
        long denomgcd = fDenominator;
        long otherdenomgcd = fs.fDenominator;
        long newdenom = denomgcd * otherdenomgcd;
        long newnum = otherdenomgcd * fNumerator + (long) fDenominator * (long) fs.fNumerator;
        return valueOf(newnum, newdenom);
      }
      long denomgcd = ((long) fDenominator) / gcd;
      long otherdenomgcd = ((long) fs.fDenominator) / gcd;
      long newdenom = denomgcd * fs.fDenominator;
      long newnum = otherdenomgcd * fNumerator + denomgcd * fs.fNumerator;
      return valueOf(newnum, newdenom);
    }
  }

  @Override
  public IRational add(IRational parm1) {
    if (parm1.isZero()) {
      return this;
    }
    if (parm1 instanceof IFraction) {
      return add((IFraction) parm1).normalize();
    }
    if (parm1 instanceof IntegerSym) {
      IntegerSym is = (IntegerSym) parm1;
      long newnum = fNumerator + (long) fDenominator * (long) is.fIntValue;
      return valueOf(newnum, fDenominator).normalize();
    }
    BigInteger newnum = toBigNumerator().add(toBigDenominator().multiply(parm1.toBigNumerator()));
    return valueOf(newnum, toBigDenominator()).normalize();
  }

  @Override
  public IInteger ceil() {
    if (fDenominator == 1) {
      return AbstractIntegerSym.valueOf(fNumerator);
    }
    int div = fNumerator / fDenominator;
    // Java rounds the wrong way for positive numbers.
    // We know that the division is not exact due to
    // normalization and mdenom != 1, so adding
    // one fixes the result for positive numbers.
    if (fNumerator > 0) {
      div++;
    }
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
    if (fDenominator == 1) {
      return F.ZZ(fNumerator);
    }
    int div = fNumerator / fDenominator;
    if (fNumerator > 0) {
      div++;
    }
    return F.ZZ(div);
  }

  @Override
  public int compareAbsValueToOne() {
    long num = fNumerator;
    if (fNumerator < 0) {
      num *= (-1);
    }
    if (isOne(num, fDenominator)) {
      return 0;
    }
    return (num > fDenominator) ? 1 : -1;
  }

  @Override
  public int compareInt(final int value) {
    long valo = (long) fDenominator * (long) value;
    return fNumerator < valo ? -1 : fNumerator == valo ? 0 : 1;
  }

  @Override
  public int compareTo(IExpr expr) {
    if (expr instanceof FractionSym) {
      final FractionSym temp = (FractionSym) expr;
      final int numerator = temp.fNumerator;
      if (temp.fDenominator == fDenominator) {
        return (fNumerator < numerator) ? -1 : ((fNumerator == numerator) ? 0 : 1);
      }
      long valt = (long) fNumerator * (long) temp.fDenominator;
      long valo = (long) fDenominator * (long) numerator;
      return valt < valo ? -1 : valt == valo ? 0 : 1;
    } else if (expr instanceof IRational) {
      if (expr instanceof IntegerSym) {
        return compareInt(((IntegerSym) expr).fIntValue);
      } else if (expr instanceof BigIntegerSym) {
        return new BigFractionSym(((AbstractIntegerSym) expr).toBigNumerator().negate(),
            BigInteger.ONE).compareTo(this);
      } else if (expr instanceof BigFractionSym) {
        return -expr.compareTo(this);
      }
    } else if (expr.isReal()) {
      return Double.compare(doubleValue(), ((IReal) expr).doubleValue());
    }
    return super.compareTo(expr);
  }

  @Override
  public ComplexNum complexNumValue() {
    // double precision complex number
    double nr = fNumerator;
    double dr = fDenominator;
    return ComplexNum.valueOf(nr / dr);
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
    if (other instanceof BigFractionSym) {
      return ((BigFractionSym) other).idiv(this);
    }

    FractionSym fs = (FractionSym) other;
    if (fs.fDenominator == 1) {
      if (fs.fNumerator == 1) {
        return this;
      }
      if (fs.fNumerator == -1) {
        return this.negate();
      }
    }
    long newnum = (long) fNumerator * fs.fDenominator;
    long newdenom = (long) fDenominator * fs.fNumerator;
    // +-inf : -c = -+inf
    if (newdenom == 0 && fs.fNumerator < 0)
      newnum = -newnum;
    return valueOf(newnum, newdenom);
  }

  @Override
  public IInteger[] divideAndRemainder() {
    IInteger[] result = new IInteger[2];
    result[0] = AbstractIntegerSym.valueOf(fNumerator / fDenominator);
    result[1] = AbstractIntegerSym.valueOf(fNumerator % fDenominator);
    return result;
  }

  @Override
  public double doubleValue() {
    return ((double) fNumerator) / ((double) fDenominator);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof IFraction) {
      return ((IFraction) o).equalsFraction(fNumerator, fDenominator);
    }
    return false;
  }

  @Override
  public final boolean equalsFraction(final int numerator, final int denominator) {
    return fNumerator == numerator && fDenominator == denominator;
  }

  @Override
  public boolean equalsInt(final int value) {
    return fNumerator == value && fDenominator == 1;
  }

  @Override
  public IInteger floor() {
    if (fDenominator == 1) {
      return AbstractIntegerSym.valueOf(fNumerator);
    }
    int div = fNumerator / fDenominator;
    // Java rounds the wrong way for negative numbers.
    // We know that the division is not exact due to
    // normalization and mdenom != 1, so subtracting
    // one fixes the result for negative numbers.
    if (fNumerator < 0) {
      div--;
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
    if (fDenominator == 1) {
      return F.ZZ(fNumerator);
    }
    int div = fNumerator / fDenominator;
    if (fNumerator < 0) {
      div--;
    }
    return F.ZZ(div);
  }

  /**
   * Returns the fractional part of the rational, i.e. the number this.sub(this.floor()).
   *
   * @return Next smaller integer of <code>this</code>.
   */
  @Override
  public IFraction fractionalPart() {
    if (fDenominator == 1) {
      return FractionSym.ZERO;
    }
    return valueOf(fNumerator % fDenominator, fDenominator);
  }

  @Override
  public String fullFormString() {
    StringBuilder buf = new StringBuilder("Rational");
    if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
      buf.append('(');
    } else {
      buf.append('[');
    }
    buf.append(Integer.toString(fNumerator));
    buf.append(',');
    buf.append(Integer.toString(fDenominator));
    if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
      buf.append(')');
    } else {
      buf.append(']');
    }
    return buf.toString();
  }

  @Override
  public IExpr gcd(IExpr that) {
    if (that instanceof FractionSym) {
      return gcd((FractionSym) that);
    }
    return super.gcd(that);
  }

  /**
   * Compute the gcd of two rationals (this and other). The gcd is the rational number, such that
   * dividing this and other with the gcd will yield two co-prime integers.
   *
   * @param other the second rational argument.
   * @return the gcd of this and other.
   */
  @Override
  public IFraction gcd(IFraction other) {
    if (isZero()) {
      return other;
    }
    if (other.isZero()) {
      return this;
    }
    if (other instanceof BigFractionSym) {
      return ((BigFractionSym) other).gcd(this);
    }
    /* new numerator = gcd(num, other.num) */
    /* new denominator = lcm(denom, other.denom) */
    FractionSym fs = (FractionSym) other;
    int gcddenom = ArithmeticUtils.gcd(fDenominator, fs.fDenominator);
    long denom = ((long) (fDenominator / gcddenom)) * (long) fs.fDenominator;
    long num = ArithmeticUtils.gcd(fNumerator < 0 ? -fNumerator : fNumerator,
        fs.fNumerator < 0 ? -fs.fNumerator : fs.fNumerator);
    return valueOf(num, denom);
  }

  @Override
  public IExpr lcm(IExpr that) {
    if (that instanceof BigFractionSym) {
      return ((BigFractionSym) that).lcm(this);
    }
    if (that instanceof FractionSym) {
      return lcm((FractionSym) that);
    }
    return super.lcm(that);
  }

  public IFraction lcm(FractionSym other) {
    /* new numerator = lcm(num, other.num) */
    /* new denominator = gcd(denom, other.denom) */
    FractionSym fs = other;
    int numerator = fNumerator < 0 ? -fNumerator : fNumerator;
    int numeratorOther = fs.fNumerator < 0 ? -fs.fNumerator : fs.fNumerator;
    int gcddenom = ArithmeticUtils.gcd(numerator, numeratorOther);
    long newNumerator = ((long) (numerator / gcddenom)) * (long) numeratorOther;

    long newDenominator = ArithmeticUtils.gcd(fDenominator, fs.fDenominator);
    return valueOf(newNumerator, newDenominator);
  }

  @Override
  public BigInteger toBigDenominator() {
    return BigInteger.valueOf(fDenominator);
  }

  @Override
  public BigInteger toBigNumerator() {
    return BigInteger.valueOf(fNumerator);
  }

  /** {@inheritDoc} */
  @Override
  public BigFraction toBigFraction() {
    return new BigFraction(fNumerator, fDenominator);
  }

  @Override
  public BigRational toBigRational() {
    return new BigRational(fNumerator, fDenominator);
  }

  @Override
  public int hashCode() {
    return 37 * (37 * 17 + fNumerator) + fDenominator;
  }

  @Override
  public CharSequence internalFormString(boolean symbolsAsFactoryMethod, int depth) {
    SourceCodeProperties p = SourceCodeProperties.stringFormProperties(symbolsAsFactoryMethod);
    return internalJavaString(p, depth, x -> null);
  }

  @Override
  public CharSequence internalJavaString(SourceCodeProperties properties, int depth,
      Function<ISymbol, ? extends CharSequence> variables) {
    String prefix = SourceCodeProperties.getPrefixF(properties);
    StringBuilder javaForm = new StringBuilder(prefix);
    if (fNumerator == 1) {
      switch (fDenominator) {
        case 2:
          return javaForm.append("C1D2");
        case 3:
          return javaForm.append("C1D3");
        case 4:
          return javaForm.append("C1D4");
        default:
      }
    }
    if (fNumerator == -1) {
      switch (fDenominator) {
        case 2:
          return javaForm.append("CN1D2");
        case 3:
          return javaForm.append("CN1D3");
        case 4:
          return javaForm.append("CN1D4");
        default:
      }
    }
    return javaForm.append("QQ(").append(fNumerator).append("L,").append(fDenominator).append("L)");
  }

  @Override
  public CharSequence internalScalaString(boolean symbolsAsFactoryMethod, int depth) {
    SourceCodeProperties p = SourceCodeProperties.scalaFormProperties(symbolsAsFactoryMethod);
    return internalJavaString(p, depth, x -> null);
  }

  /**
   * Returns a new rational representing the inverse of <code>this</code>.
   *
   * @return Inverse of <code>this</code>.
   */
  @Override
  public IFraction inverse() {
    return valueOf(fDenominator, fNumerator);
  }

  /**
   * Check whether this rational corresponds to a (finite) rational value. This function can be used
   * to test for infinites and NaNs.
   *
   * @return true if and only if this rational is not infinite or NaN.
   */
  public boolean isDefined() {
    return fDenominator != 0;
  }

  /**
   * Check whether this rational represents an integral value.
   *
   * @return <code>true</code> iff value is integral.
   */
  @Override
  public boolean isIntegral() {
    return fDenominator == 1;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isMinusOne() {
    return fNumerator == (-1 * fDenominator) && fNumerator != 0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNegative() {
    return fNumerator < 0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isOne() {
    return fNumerator == fDenominator && fNumerator != 0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPositive() {
    return fNumerator > 0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isZero() {
    return fNumerator == 0;
  }

  /**
   * Return a new rational representing <code>this * other</code>.
   *
   * @param other big integer to multiply.
   * @return Product of <code>this</code> and <code>other</code>.
   */
  public IFraction mul(BigInteger other) {
    if (other.bitLength() <= 31) {
      int oint = other.intValue();
      if (oint == 1)
        return this;
      if (oint == -1)
        return this.negate();
      long newnum = (long) fNumerator * (long) oint;
      return valueOf(newnum, fDenominator);
    }

    if (this.isOne()) {
      return valueOf(other, BigInteger.ONE);
    }
    if (this.isMinusOne()) {
      return valueOf(other.negate(), BigInteger.ONE);
    }

    return valueOf(toBigNumerator().multiply(other), toBigDenominator());
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
    if (this.isOne()) {
      return other;
    }
    if (other.isMinusOne()) {
      return this.negate();
    }
    if (this.isMinusOne()) {
      return other.negate();
    }
    if (other instanceof BigFractionSym) {
      return other.mul(this);
    }

    FractionSym fs = (FractionSym) other;
    long newnum = (long) fNumerator * (long) fs.fNumerator;
    long newdenom = (long) fDenominator * (long) fs.fDenominator;
    return valueOf(newnum, newdenom);
  }

  @Override
  public IRational multiply(IRational parm1) {
    if (isZero() || parm1.isZero()) {
      return F.C0;
    }
    if (parm1.isOne()) {
      return this;
    }
    if (parm1.isMinusOne()) {
      return this.negate();
    }
    if (parm1 instanceof IFraction) {
      return mul((IFraction) parm1);
    }
    if (parm1 instanceof IntegerSym) {
      IntegerSym is = (IntegerSym) parm1;
      long newnum = (long) fNumerator * (long) is.fIntValue;
      return valueOf(newnum, fDenominator).normalize();
    }
    BigIntegerSym p1 = (BigIntegerSym) parm1;
    BigInteger newnum = toBigNumerator().multiply(p1.toBigNumerator());
    return valueOf(newnum, toBigDenominator()).normalize();
  }

  @Override
  public IRational multiply(int n) {
    if (isZero() || n == 0) {
      return F.C0;
    }
    if (n == 1) {
      return this;
    }
    if (n == -1) {
      return this.negate();
    }
    long newnum = (long) fNumerator * (long) n;
    return valueOf(newnum, fDenominator).normalize();
  }

  /**
   * Returns a new rational equal to <code>-this</code>.
   *
   * @return <code>-this</code>.
   */
  @Override
  public IFraction negate() {
    return AbstractFractionSym.valueOf(-(long) fNumerator, fDenominator);
  }

  /** {@inheritDoc} */
  @Override
  public IRational normalize() {
    if (fDenominator == 1) {
      return F.ZZ(fNumerator);
    }
    if (isZero()) {
      return F.C0;
    }
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public IInteger roundExpr() {
    BigFraction temp = new BigFraction(fNumerator, fDenominator);
    return AbstractIntegerSym.valueOf(NumberUtil.round(temp, BigDecimal.ROUND_HALF_EVEN));
  }

  /** {@inheritDoc} */
  @Override
  public int complexSign() {
    return fNumerator < 0 ? -1 : fNumerator == 0 ? 0 : 1;
  }

  /** {@inheritDoc} */
  @Override
  public int toInt() throws ArithmeticException {
    if (fDenominator == 1) {
      return fNumerator;
    }
    if (fNumerator == 0) {
      return 0;
    }
    throw new ArithmeticException("toInt: denominator != 1");
  }

  /** {@inheritDoc} */
  @Override
  public int toIntDefault(int defaultValue) {
    if (fDenominator == 1) {
      return fNumerator;
    }
    if (fNumerator == 0) {
      return 0;
    }
    return defaultValue;
  }

  /** {@inheritDoc} */
  @Override
  public long toLongDefault(long defaultValue) {
    if (fDenominator == 1) {
      return fNumerator;
    }
    if (fNumerator == 0) {
      return 0;
    }
    return defaultValue;
  }

  /** {@inheritDoc} */
  @Override
  public long toLong() throws ArithmeticException {
    if (fDenominator == 1) {
      return fNumerator;
    }
    if (fNumerator == 0) {
      return 0L;
    }
    throw new ArithmeticException("toLong: denominator != 1");
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    try {
      StringBuilder sb = new StringBuilder();
      OutputFormFactory.get().convertFraction(sb, toBigNumerator(), toBigDenominator(),
          Integer.MIN_VALUE, OutputFormFactory.NO_PLUS_CALL);
      return sb.toString();
    } catch (Exception e1) {
      // fall back to simple output format
      return toBigNumerator().toString() + "/" + toBigDenominator().toString();
    }
  }

  private Object writeReplace() {
    return optional();
  }
}

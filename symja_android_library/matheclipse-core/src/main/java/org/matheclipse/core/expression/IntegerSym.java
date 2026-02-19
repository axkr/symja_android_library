package org.matheclipse.core.expression;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.function.Function;
import org.hipparchus.exception.LocalizedCoreFormats;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.fraction.BigFraction;
import org.hipparchus.util.ArithmeticUtils;
import org.matheclipse.core.eval.util.SourceCodeProperties;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IPair;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numbertheory.Primality;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import edu.jas.arith.BigRational;

/**
 * IInteger implementation which uses an internal <code>int</code> value
 *
 * @see AbstractIntegerSym
 * @see BigIntegerSym
 */
public class IntegerSym extends AbstractIntegerSym {

  private static final long serialVersionUID = 6389228668633533063L;

  static final int LOW = -128;
  static final int HIGH = 128;
  static final IntegerSym[] CACHE = new IntegerSym[] {new IntegerSym(-128), new IntegerSym(-127),
      new IntegerSym(-126), new IntegerSym(-125), new IntegerSym(-124), new IntegerSym(-123),
      new IntegerSym(-122), new IntegerSym(-121), new IntegerSym(-120), new IntegerSym(-119),
      new IntegerSym(-118), new IntegerSym(-117), new IntegerSym(-116), new IntegerSym(-115),
      new IntegerSym(-114), new IntegerSym(-113), new IntegerSym(-112), new IntegerSym(-111),
      new IntegerSym(-110), new IntegerSym(-109), new IntegerSym(-108), new IntegerSym(-107),
      new IntegerSym(-106), new IntegerSym(-105), new IntegerSym(-104), new IntegerSym(-103),
      new IntegerSym(-102), new IntegerSym(-101), new IntegerSym(-100), new IntegerSym(-99),
      new IntegerSym(-98), new IntegerSym(-97), new IntegerSym(-96), new IntegerSym(-95),
      new IntegerSym(-94), new IntegerSym(-93), new IntegerSym(-92), new IntegerSym(-91),
      new IntegerSym(-90), new IntegerSym(-89), new IntegerSym(-88), new IntegerSym(-87),
      new IntegerSym(-86), new IntegerSym(-85), new IntegerSym(-84), new IntegerSym(-83),
      new IntegerSym(-82), new IntegerSym(-81), new IntegerSym(-80), new IntegerSym(-79),
      new IntegerSym(-78), new IntegerSym(-77), new IntegerSym(-76), new IntegerSym(-75),
      new IntegerSym(-74), new IntegerSym(-73), new IntegerSym(-72), new IntegerSym(-71),
      new IntegerSym(-70), new IntegerSym(-69), new IntegerSym(-68), new IntegerSym(-67),
      new IntegerSym(-66), new IntegerSym(-65), new IntegerSym(-64), new IntegerSym(-63),
      new IntegerSym(-62), new IntegerSym(-61), new IntegerSym(-60), new IntegerSym(-59),
      new IntegerSym(-58), new IntegerSym(-57), new IntegerSym(-56), new IntegerSym(-55),
      new IntegerSym(-54), new IntegerSym(-53), new IntegerSym(-52), new IntegerSym(-51),
      new IntegerSym(-50), new IntegerSym(-49), new IntegerSym(-48), new IntegerSym(-47),
      new IntegerSym(-46), new IntegerSym(-45), new IntegerSym(-44), new IntegerSym(-43),
      new IntegerSym(-42), new IntegerSym(-41), new IntegerSym(-40), new IntegerSym(-39),
      new IntegerSym(-38), new IntegerSym(-37), new IntegerSym(-36), new IntegerSym(-35),
      new IntegerSym(-34), new IntegerSym(-33), new IntegerSym(-32), new IntegerSym(-31),
      new IntegerSym(-30), new IntegerSym(-29), new IntegerSym(-28), new IntegerSym(-27),
      new IntegerSym(-26), new IntegerSym(-25), new IntegerSym(-24), new IntegerSym(-23),
      new IntegerSym(-22), new IntegerSym(-21), new IntegerSym(-20), new IntegerSym(-19),
      new IntegerSym(-18), new IntegerSym(-17), new IntegerSym(-16), new IntegerSym(-15),
      new IntegerSym(-14), new IntegerSym(-13), new IntegerSym(-12), new IntegerSym(-11),
      new IntegerSym(-10), new IntegerSym(-9), new IntegerSym(-8), new IntegerSym(-7),
      new IntegerSym(-6), new IntegerSym(-5), new IntegerSym(-4), new IntegerSym(-3),
      new IntegerSym(-2), new IntegerSym(-1), new IntegerSym(0), new IntegerSym(1),
      new IntegerSym(2), new IntegerSym(3), new IntegerSym(4), new IntegerSym(5), new IntegerSym(6),
      new IntegerSym(7), new IntegerSym(8), new IntegerSym(9), new IntegerSym(10),
      new IntegerSym(11), new IntegerSym(12), new IntegerSym(13), new IntegerSym(14),
      new IntegerSym(15), new IntegerSym(16), new IntegerSym(17), new IntegerSym(18),
      new IntegerSym(19), new IntegerSym(20), new IntegerSym(21), new IntegerSym(22),
      new IntegerSym(23), new IntegerSym(24), new IntegerSym(25), new IntegerSym(26),
      new IntegerSym(27), new IntegerSym(28), new IntegerSym(29), new IntegerSym(30),
      new IntegerSym(31), new IntegerSym(32), new IntegerSym(33), new IntegerSym(34),
      new IntegerSym(35), new IntegerSym(36), new IntegerSym(37), new IntegerSym(38),
      new IntegerSym(39), new IntegerSym(40), new IntegerSym(41), new IntegerSym(42),
      new IntegerSym(43), new IntegerSym(44), new IntegerSym(45), new IntegerSym(46),
      new IntegerSym(47), new IntegerSym(48), new IntegerSym(49), new IntegerSym(50),
      new IntegerSym(51), new IntegerSym(52), new IntegerSym(53), new IntegerSym(54),
      new IntegerSym(55), new IntegerSym(56), new IntegerSym(57), new IntegerSym(58),
      new IntegerSym(59), new IntegerSym(60), new IntegerSym(61), new IntegerSym(62),
      new IntegerSym(63), new IntegerSym(64), new IntegerSym(65), new IntegerSym(66),
      new IntegerSym(67), new IntegerSym(68), new IntegerSym(69), new IntegerSym(70),
      new IntegerSym(71), new IntegerSym(72), new IntegerSym(73), new IntegerSym(74),
      new IntegerSym(75), new IntegerSym(76), new IntegerSym(77), new IntegerSym(78),
      new IntegerSym(79), new IntegerSym(80), new IntegerSym(81), new IntegerSym(82),
      new IntegerSym(83), new IntegerSym(84), new IntegerSym(85), new IntegerSym(86),
      new IntegerSym(87), new IntegerSym(88), new IntegerSym(89), new IntegerSym(90),
      new IntegerSym(91), new IntegerSym(92), new IntegerSym(93), new IntegerSym(94),
      new IntegerSym(95), new IntegerSym(96), new IntegerSym(97), new IntegerSym(98),
      new IntegerSym(99), new IntegerSym(100), new IntegerSym(101), new IntegerSym(102),
      new IntegerSym(103), new IntegerSym(104), new IntegerSym(105), new IntegerSym(106),
      new IntegerSym(107), new IntegerSym(108), new IntegerSym(109), new IntegerSym(110),
      new IntegerSym(111), new IntegerSym(112), new IntegerSym(113), new IntegerSym(114),
      new IntegerSym(115), new IntegerSym(116), new IntegerSym(117), new IntegerSym(118),
      new IntegerSym(119), new IntegerSym(120), new IntegerSym(121), new IntegerSym(122),
      new IntegerSym(123), new IntegerSym(124), new IntegerSym(125), new IntegerSym(126),
      new IntegerSym(127), new IntegerSym(128) //
  };

  // static {
  // printCacheSource();
  // }

  private static void printCacheSource() {
    System.out.println("static final IntegerSym[] CACHE = new IntegerSym[] {");
    IntegerSym[] CACHE = new IntegerSym[(HIGH - LOW) + 1];
    int j = LOW;
    for (int k = 0; k < CACHE.length; k++) {
      CACHE[k] = new IntegerSym(j);
      System.out.print("new IntegerSym(" + j + ")");
      j++;
      if (k < CACHE.length - 1) {
        System.out.print(",");
      }
    }
    System.out.print(" // \n};");
  }

  /* package private */ int fIntValue;

  /** do not use directly, needed for serialization/deserialization */
  public IntegerSym() {
    fIntValue = 0;
  }

  /**
   * do not use directly, needed for serialization/deserialization
   *
   * @param value
   */
  public IntegerSym(int value) {
    fIntValue = value;
  }

  /** {@inheritDoc} */
  @Override
  public IInteger abs() {
    if (fIntValue < 0) {
      return valueOf(fIntValue * (-1L));
    }
    return this;
  }

  /**
   * @param that
   * @return
   */
  @Override
  public IInteger add(final IInteger that) {
    if (fIntValue == 0) {
      return that;
    }
    if (that instanceof BigIntegerSym) {
      return ((BigIntegerSym) that).add(this);
    }
    IntegerSym is = (IntegerSym) that;
    if (is.fIntValue == 0) {
      return this;
    }
    return valueOf((long) fIntValue + is.fIntValue);
  }

  @Override
  public IInteger add(final int that) {
    if (fIntValue == 0) {
      return valueOf(that);
    }
    if (that == 0) {
      return this;
    }
    return valueOf((long) fIntValue + (long) that);
  }

  @Override
  public IRational add(IRational parm1) {
    if (parm1.isZero()) {
      return this;
    }
    if (parm1 instanceof AbstractFractionSym) {
      return ((AbstractFractionSym) parm1).add(this);
    }
    if (parm1 instanceof IntegerSym) {
      IntegerSym is = (IntegerSym) parm1;
      long newnum = (long) fIntValue + (long) is.fIntValue;
      return valueOf(newnum);
    }
    BigIntegerSym p1 = (BigIntegerSym) parm1;
    BigInteger newnum = toBigNumerator().add(p1.toBigNumerator());
    return valueOf(newnum);
  }

  @Override
  public IReal add(IReal that) {
    if (that instanceof IRational) {
      return add((IRational) that);
    }
    return Num.valueOf((fIntValue) + that.doubleValue());
  }

  @Override
  public long bitLength() {
    if (fIntValue == 0) {
      return 0L;
    }
    return 32 - Integer.numberOfLeadingZeros(fIntValue < 0 ? -fIntValue : fIntValue);
  }

  @Override
  public byte byteValue() {
    return (byte) fIntValue;
  }

  /** {@inheritDoc} */
  @Override
  public int compareAbsValueToOne() {
    long num = fIntValue;
    if (fIntValue < 0) {
      num *= (-1);
    }
    if (num == 1L) {
      return 0;
    }
    return (num > 0) ? 1 : -1;
  }

  @Override
  public int compareInt(final int value) {
    return (fIntValue > value) ? 1 : (fIntValue == value) ? 0 : -1;
  }

  /**
   * Compares this expression with the specified expression for order. Returns a negative integer,
   * zero, or a positive integer as this expression is canonical less than, equal to, or greater
   * than the specified expression.
   */
  @Override
  public int compareTo(final IExpr expr) {
    if (expr instanceof IRational) {
      if (expr instanceof IntegerSym) {
        final int num = ((IntegerSym) expr).fIntValue;
        return (fIntValue < num) ? -1 : ((num == fIntValue) ? 0 : 1);
      } else if (expr instanceof AbstractFractionSym) {
        return -((AbstractFractionSym) expr).compareTo(AbstractFractionSym.valueOf(fIntValue));
      } else if (expr instanceof BigIntegerSym) {
        return -expr.compareTo(this);
      }
    } else if (expr.isReal()) {
      return Double.compare(fIntValue, ((IReal) expr).doubleValue());
    }
    return super.compareTo(expr);
  }

  @Override
  public ComplexNum complexNumValue() {
    // double precision complex number
    return ComplexNum.valueOf(doubleValue());
  }

  @Override
  public int complexSign() {
    return (fIntValue > 0) ? 1 : (fIntValue == 0) ? 0 : -1;
  }

  /** {@inheritDoc} */
  @Override
  public IInteger dec() {
    return add(F.CN1);
  }

  /** {@inheritDoc} */
  @Override
  public IInteger denominator() {
    return F.C1;
  }

  @Override
  public IInteger div(final IInteger that) {
    if (that instanceof IntegerSym) {
      return valueOf(fIntValue / ((IntegerSym) that).fIntValue);
    }
    return valueOf(toBigNumerator().divide(that.toBigNumerator()));
  }

  /** {@inheritDoc} */
  @Override
  public IInteger[] divideAndRemainder(final IInteger that) {
    final IInteger[] res = new IntegerSym[2];
    BigInteger[] largeRes = toBigNumerator().divideAndRemainder(that.toBigNumerator());
    res[0] = valueOf(largeRes[0]);
    res[1] = valueOf(largeRes[1]);

    return res;
  }

  @Override
  public IReal divideBy(IReal that) {
    if (that instanceof IntegerSym) {
      return AbstractFractionSym.valueOf(this).divideBy(that);
    }
    if (that instanceof AbstractFractionSym) {
      return AbstractFractionSym.valueOf(this).divideBy(that);
    }
    return Num.valueOf((fIntValue) / that.doubleValue());
  }

  /** @return */
  @Override
  public double doubleValue() {
    return fIntValue;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof IntegerSym) {
      return fIntValue == ((IntegerSym) obj).fIntValue;
    }
    // EQ_CHECK_FOR_OPERAND_NOT_COMPATIBLE_WITH_THIS
    // if (obj instanceof BigIntegerSym) {
    // return ((BigIntegerSym) obj).equalsInt(fIntValue);
    // }
    return false;
  }

  @Override
  public final boolean equalsFraction(final int numerator, final int denominator) {
    return denominator == 1 && fIntValue == numerator;
  }

  @Override
  public boolean equalsInt(final int value) {
    return fIntValue == value;
  }

  /**
   * Get the highest exponent of <code>base</code> that divides <code>this</code>
   *
   * @return the exponent
   */
  @Override
  public IExpr exponent(IInteger base) {
    IInteger b = this;
    if (complexSign() < 0) {
      b = b.negate();
    } else if (b.isZero()) {
      return F.CInfinity;
    } else if (b.isOne()) {
      return F.C0;
    }
    if (b.equals(base)) {
      return F.C1;
    }
    BigInteger rest = Primality.countExponent(b.toBigNumerator(), base.toBigNumerator());
    return valueOf(rest);
  }

  /**
   * Returns the greatest common divisor of this large integer and the one specified. If a
   * {@link LocalizedCoreFormats#GCD_OVERFLOW_32_BITS} error is thrown in the <code>int</code>
   * calculation, the calculation switches to {@link BigInteger#gcd(BigInteger)} calculation.
   */
  @Override
  public IInteger gcd(final IInteger that) {
    if (that.isZero()) {
      return this;
    }
    if (this.isZero()) {
      return that;
    }
    if (that instanceof IntegerSym) {
      int p = fIntValue;
      int q = ((IntegerSym) that).fIntValue;
      return valueOf(AbstractIntegerSym.gcd(p, q));
    }
    return valueOf(toBigNumerator().gcd(that.toBigNumerator()));
  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
    return fIntValue;
  }

  /** {@inheritDoc} */
  @Override
  public IReal im() {
    return F.C0;
  }

  /** {@inheritDoc} */
  @Override
  public IInteger inc() {
    return add(F.C1);
  }

  @Override
  public long integerLength(IInteger radix) {
    long length = 0L;
    IInteger ai = this;
    while (!ai.isZero()) {
      ai = ai.div(radix);
      length++;
    }
    return length;
  }

  @Override
  public CharSequence internalJavaString(SourceCodeProperties properties, int depth,
      Function<ISymbol, ? extends CharSequence> variables) {
    String prefix = SourceCodeProperties.getPrefixF(properties);
    StringBuilder javaForm = new StringBuilder(prefix);
    int value = NumberUtil.toInt(fIntValue);
    switch (value) {
      case -1:
        return javaForm.append("CN1");
      case -2:
        return javaForm.append("CN2");
      case -3:
        return javaForm.append("CN3");
      case -4:
        return javaForm.append("CN4");
      case -5:
        return javaForm.append("CN5");
      case -6:
        return javaForm.append("CN6");
      case -7:
        return javaForm.append("CN7");
      case -8:
        return javaForm.append("CN8");
      case -9:
        return javaForm.append("CN9");
      case -10:
        return javaForm.append("CN10");
      case 0:
        return javaForm.append("C0");
      case 1:
        return javaForm.append("C1");
      case 2:
        return javaForm.append("C2");
      case 3:
        return javaForm.append("C3");
      case 4:
        return javaForm.append("C4");
      case 5:
        return javaForm.append("C5");
      case 6:
        return javaForm.append("C6");
      case 7:
        return javaForm.append("C7");
      case 8:
        return javaForm.append("C8");
      case 9:
        return javaForm.append("C9");
      case 10:
        return javaForm.append("C10");
      default:
    }
    return javaForm.append("ZZ(").append(value).append("L)");
  }

  @Override
  public CharSequence internalScalaString(boolean symbolsAsFactoryMethod, int depth) {
    SourceCodeProperties p = SourceCodeProperties.scalaFormProperties(symbolsAsFactoryMethod);
    return internalJavaString(p, depth, x -> null);
  }

  @Override
  public int intValue() {
    return fIntValue;
  }

  /** @return */
  @Override
  public IRational inverse() {
    if (isOne() || isMinusOne()) {
      return this;
    }
    if (fIntValue < 0) {
      return AbstractFractionSym.valueOf(-1, -fIntValue);
    }
    return AbstractFractionSym.valueOf(1, fIntValue);
  }

  /** {@inheritDoc} */
  @Override
  public IInteger iquo(final IInteger that) {
    BigInteger[] largeRes = toBigNumerator().divideAndRemainder(that.toBigNumerator());
    return valueOf(largeRes[0]);
  }

  /** {@inheritDoc} */
  @Override
  public IInteger irem(final IInteger that) {
    BigInteger[] largeRes = toBigNumerator().divideAndRemainder(that.toBigNumerator());
    return valueOf(largeRes[1]);
  }

  @Override
  public boolean isEven() {
    return (fIntValue & 0x00000001) == 0x00000000;
  }

  @Override
  public boolean isGT(IReal obj) {
    if (obj instanceof IntegerSym) {
      return fIntValue > ((IntegerSym) obj).fIntValue;
    }
    if (obj instanceof BigIntegerSym) {
      return toBigNumerator().compareTo(((BigIntegerSym) obj).toBigNumerator()) > 0;
    }
    if (obj instanceof AbstractFractionSym) {
      return AbstractFractionSym.valueOf(fIntValue).compareTo(obj) > 0;
    }
    return doubleValue() > obj.doubleValue();
  }

  /**
   * @param that
   * @return
   */
  public boolean isLargerThan(final BigInteger that) {
    return toBigNumerator().compareTo(that) > 0;
  }

  @Override
  public boolean isLT(IReal obj) {
    if (obj instanceof IntegerSym) {
      return fIntValue < ((IntegerSym) obj).fIntValue;
    }
    if (obj instanceof BigIntegerSym) {
      return toBigNumerator().compareTo(((BigIntegerSym) obj).toBigNumerator()) < 0;
    }
    if (obj instanceof AbstractFractionSym) {
      return AbstractFractionSym.valueOf(fIntValue).compareTo(obj) < 0;
    }
    return doubleValue() < obj.doubleValue();
  }

  @Override
  public boolean isMinusOne() {
    return fIntValue == -1;
  }

  @Override
  public boolean isMultipleOf(IInteger other) {
    if (other.isZero()) {
      // x.isMultipleOf(0) is true iff x is 0
      return this.isZero();
    }

    if (this.isZero()) {
      // 0.isMultipleOf(x) is true for non-zero x (zero case handled above)
      return true;
    }

    if (other.isOne() || other.isMinusOne()) {
      return true;
    }

    if (other instanceof IntegerSym) {
      long otherVal = ((IntegerSym) other).fIntValue;
      return (this.fIntValue % otherVal == 0L);
    } else {
      return new BigIntegerSym(this.fIntValue).isMultipleOf(other);
    }
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNegative() {
    return fIntValue < 0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNonNegative() {
    return fIntValue >= 0;
  }

  @Override
  public boolean isOdd() {
    return (fIntValue & 0x00000001) == 0x00000001;
  }

  @Override
  public boolean isOne() {
    return fIntValue == 1;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPositive() {
    return fIntValue > 0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isProbablePrime() {
    if (fIntValue < 0) {
      return negate().isProbablePrime();
    }
    return LongMath.isPrime(fIntValue);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isProbablePrime(int certainty) {
    return LongMath.isPrime(fIntValue);
    // return toBigNumerator().isProbablePrime(certainty);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isRationalValue(IRational value) {
    return equals(value);
  }

  @Override
  public boolean isZero() {
    return fIntValue == 0;
  }

  @Override
  public IInteger lcm(final IInteger that) {
    if (that instanceof IntegerSym) {
      try {
        return valueOf(ArithmeticUtils.lcm(fIntValue, ((IntegerSym) that).fIntValue));
      } catch (MathRuntimeException ex) {
        //
      }
    }
    return super.lcm(that);
  }

  @Override
  public long longValue() {
    return fIntValue;
  }

  @Override
  public IInteger mod(final IInteger that) {
    if (that instanceof IntegerSym) {
      return valueOf(IntMath.mod(fIntValue, ((IntegerSym) that).fIntValue));
    }
    return valueOf(toBigNumerator().mod(that.toBigNumerator()));
  }

  @Override
  public IInteger modInverse(final IInteger that) {
    int a = fIntValue;
    if (a >= 0 && that instanceof IntegerSym) {
      int b = ((IntegerSym) that).fIntValue;
      if (b <= 0) {
        throw new ArithmeticException("integer: modulus not positive");
      }
      if (a == 0) {
        throw new ArithmeticException("integer argument not invertible.");
      }
      if (b == 1) {
        return F.C0;
      }
      int modVal = a;
      if (modVal == 1) {
        return F.C1;
      }
      int b0 = b;
      int x0 = 0;
      int x1 = 1;
      while (modVal > 1) {
        if (b == 0) {
          throw new ArithmeticException("integer argument not invertible.");
        }
        int q = modVal / b;
        int t = b;
        b = modVal % b;
        modVal = t;
        t = x0;
        x0 = x1 - q * x0;
        x1 = t;
      }
      if (x1 > 0) {
        return AbstractIntegerSym.valueOf(x1);
      }
      return AbstractIntegerSym.valueOf(x1 + b0);
    }
    return valueOf(toBigNumerator().modInverse(that.toBigNumerator()));
  }

  @Override
  public IInteger modPow(final IInteger exp, final IInteger m) {
    if (m.isZero()) {
      throw new ArithmeticException("the argument " + m.toString() + " should be nonzero.");
    }
    return valueOf(toBigNumerator().modPow(exp.toBigNumerator(), m.toBigNumerator()));
  }

  /**
   * @param that
   * @return
   */
  @Override
  public IInteger multiply(final IInteger that) {
    switch (fIntValue) {
      case 0:
        return F.C0;
      case 1:
        return that;
      case -1:
        return that.negate();
      default:
        if (that instanceof BigIntegerSym) {
          return ((BigIntegerSym) that).multiply(this);
        }
        IntegerSym is = (IntegerSym) that;
        if (is.fIntValue == 1) {
          return this;
        }
        return valueOf((long) fIntValue * is.fIntValue);
    }
  }

  /**
   * @param value
   * @return
   */
  @Override
  public IInteger multiply(int value) {
    switch (fIntValue) {
      case 0:
        return F.C0;
      case 1:
        return valueOf(value);
      case -1:
        return valueOf(value).negate();
      default:
        if (value == 0) {
          return F.C0;
        }
        if (value == 1) {
          return this;
        }
        return valueOf((long) fIntValue * value);
    }
  }

  @Override
  public IRational multiply(IRational parm1) {
    if (parm1.isZero()) {
      return F.C0;
    }
    if (parm1.isOne()) {
      return this;
    }
    if (parm1.isMinusOne()) {
      return this.negate();
    }
    if (parm1 instanceof AbstractFractionSym) {
      return ((AbstractFractionSym) parm1).multiply(this);
    }
    if (parm1 instanceof IntegerSym) {
      IntegerSym is = (IntegerSym) parm1;
      long newnum = (long) fIntValue * (long) is.fIntValue;
      return valueOf(newnum);
    }
    BigIntegerSym p1 = (BigIntegerSym) parm1;
    BigInteger newnum = toBigNumerator().multiply(p1.toBigNumerator());
    return valueOf(newnum);
  }

  @Override
  public IReal multiply(IReal that) {
    if (that instanceof IRational) {
      return multiply((IRational) that);
    }
    return Num.valueOf((fIntValue) * that.doubleValue());
  }

  @Override
  public IInteger negate() {
    if (fIntValue == Integer.MIN_VALUE) {
      // gives Integer.MAX_VALUE+1 which is a long number
      return valueOf(-1L * fIntValue);
    }
    return valueOf(-fIntValue);
  }

  @Override
  public IRational normalize() {
    return this;
  }

  /**
   * Returns the nth-root of this integer.
   *
   * @return <code>k<code> such as <code>k^n <= this < (k + 1)^n</code>
   * @throws IllegalArgumentException if {@code this < 0}
   * @throws ArithmeticException if this integer is negative and n is even.
   */
  @Override
  public IPair nthRoot(int n) throws ArithmeticException {
    if (n < 0) {
      throw new IllegalArgumentException("nthRoot(" + n + ") n must be >= 0");
    }
    if (n == 2) {
      IInteger result = valueOf(IntMath.sqrt(fIntValue, RoundingMode.DOWN));
      boolean exact = result.pow(n).equals(this);
      return F.pair(result, F.booleSymbol(exact));
    }
    if (complexSign() == 0) {
      return F.pair(F.C0, S.True);
    } else if (complexSign() < 0) {
      if (n % 2 == 0) {
        // even exponent n
        throw new ArithmeticException();
      } else {
        // odd exponent n
        IPair p = negate().nthRoot(n);
        return F.pair(p.first().negate(), p.second());
      }
    } else {
      IInteger result;
      IInteger temp = this;
      do {
        result = temp;
        temp = divideAndRemainder(temp.powerRational(((long) n) - 1))[0]
            .add(temp.multiply(AbstractIntegerSym.valueOf(n - 1)))
            .divideAndRemainder(AbstractIntegerSym.valueOf(n))[0];
      } while (temp.compareTo(result) < 0);
      boolean exact = result.pow(n).equals(this);
      return F.pair(result, F.booleSymbol(exact));
    }
  }

  @Override
  public IInteger[] nthRootSplit(int n) throws ArithmeticException {
    IInteger[] result = new IInteger[2];
    if (complexSign() == 0) {
      result[0] = F.C0;
      result[1] = F.C1;
      return result;
    } else if (complexSign() < 0) {
      if (n % 2 == 0) {
        // even exponent n
        throw new ArithmeticException();
      } else {
        // odd exponent n
        result = negate().nthRootSplit(n);
        result[1] = result[1].negate();
        return result;
      }
    }

    long b = fIntValue;
    long[] nthRoot = Primality.countRoot1021(b, n);
    result[0] = AbstractIntegerSym.valueOf(nthRoot[0]);
    result[1] = AbstractIntegerSym.valueOf(nthRoot[1]);
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public IInteger numerator() {
    return this;
  }

  @Override
  public final INumber numericNumber() {
    return F.num(this);
  }

  @Override
  public Num numValue() {
    return Num.valueOf(doubleValue());
  }

  @Override
  public IInteger quotient(final IInteger that) {
    if (that instanceof BigIntegerSym) {
      return super.quotient(that);
    }
    int thatValue = ((IntegerSym) that).fIntValue;
    long quotient = fIntValue / thatValue;
    long mod = fIntValue % thatValue;
    if (mod == 0L) {
      return valueOf(quotient);
    }
    if (quotient < 0) {
      return valueOf(quotient - 1);
    }
    return valueOf(quotient);
  }

  /** {@inheritDoc} */
  @Override
  public IReal re() {
    return this;
  }

  @Override
  public void readExternal(ObjectInput objectInput) throws IOException {
    byte attributeFlags = objectInput.readByte();
    int value;
    switch (attributeFlags) {
      case 1:
        value = objectInput.readByte();
        fIntValue = value;
        return;
      case 2:
        value = objectInput.readShort();
        fIntValue = value;
        return;
      case 4:
        value = objectInput.readInt();
        fIntValue = value;
        return;
      default:
    }
  }

  @Override
  public IExpr remainder(final IExpr that) {
    if (that instanceof IntegerSym) {
      return valueOf(toBigNumerator().remainder(((IntegerSym) that).toBigNumerator()));
    }
    if (that instanceof BigIntegerSym) {
      return valueOf(toBigNumerator().remainder(((BigIntegerSym) that).fBigIntValue));
    }
    return this;
  }

  public IInteger remainder(final IInteger that) {
    return valueOf(toBigNumerator().remainder(that.toBigNumerator()));
  }

  @Override
  public IInteger roundExpr() {
    return this;
  }

  @Override
  public IInteger shiftLeft(final int n) {
    if (n == 0) {
      return this;
    }
    if (n <= 31) {
      return valueOf(fIntValue << n);
    }
    return valueOf(toBigNumerator().shiftLeft(n));
  }

  @Override
  public IInteger shiftRight(final int n) {
    if (n == 0) {
      return this;
    }
    return valueOf(fIntValue >> n);
  }

  @Override
  public IExpr sqr() {
    return this.multiply(this);
  }

  /**
   * Returns the integer square root of this integer.
   *
   * @return <code>k<code> such as <code>k^2 <= this < (k + 1)^2</code>. If this integer is negative
   *         or it's impossible to find a square root return <code>F.Sqrt(this)</code>.
   */
  @Override
  public IExpr sqrt() {
    if (fIntValue >= 0) {
      try {
        return valueOf(IntMath.sqrt(fIntValue, RoundingMode.UNNECESSARY));
      } catch (ArithmeticException | IllegalArgumentException ex) {
      }
    }
    return F.Sqrt(this);
  }

  @Override
  public IInteger subtract(final IInteger that) {
    return add(that.negate());
  }

  @Override
  public IRational subtract(IRational parm1) {
    if (parm1.isZero()) {
      return this;
    }
    if (parm1 instanceof AbstractFractionSym) {
      return ((AbstractFractionSym) parm1).negate().add(this);
    }
    if (parm1 instanceof IntegerSym) {
      IntegerSym is = (IntegerSym) parm1;
      long newnum = (long) fIntValue - (long) is.fIntValue;
      return valueOf(newnum);
    }
    BigIntegerSym p1 = (BigIntegerSym) parm1;
    BigInteger newnum = toBigNumerator().subtract(p1.toBigNumerator());
    return valueOf(newnum);
  }

  /** {@inheritDoc} */
  @Override
  public BigInteger toBigDenominator() {
    return BigInteger.ONE;
  }

  /** {@inheritDoc} */
  @Override
  public BigFraction toBigFraction() {
    return new BigFraction(fIntValue);
  }

  /** {@inheritDoc} */
  @Override
  public BigInteger toBigNumerator() {
    return BigInteger.valueOf(fIntValue);
  }

  @Override
  public BigRational toBigRational() {
    return new BigRational(fIntValue);
  }

  /** {@inheritDoc} */
  @Override
  public int toInt() throws ArithmeticException {
    return fIntValue;
  }

  /** {@inheritDoc} */
  @Override
  public int toIntDefault(int defaultValue) {
    return fIntValue;
  }

  /** {@inheritDoc} */
  @Override
  public long toLong() throws ArithmeticException {
    return fIntValue;
  }

  /** {@inheritDoc} */
  @Override
  public long toLongDefault(long defaultValue) {
    return fIntValue;
  }

  @Override
  public String toString() {
    return Integer.toString(fIntValue);
  }

  @Override
  public int uniformFlags() {
    return UniformFlags.INT | UniformFlags.INTEGER | UniformFlags.NUMBER | UniformFlags.ATOM;
  }

  @Override
  public void writeExternal(ObjectOutput objectOutput) throws IOException {
    if (fIntValue <= Byte.MAX_VALUE && fIntValue >= Byte.MIN_VALUE) {
      objectOutput.writeByte(1);
      objectOutput.writeByte((byte) fIntValue);
      return;
    }
    if (fIntValue <= Short.MAX_VALUE && fIntValue >= Short.MIN_VALUE) {
      objectOutput.writeByte(2);
      objectOutput.writeShort((short) fIntValue);
      return;
    }
    objectOutput.writeByte(4);
    objectOutput.writeInt(fIntValue);
  }

  private Object writeReplace() {
    return optional();
  }
}

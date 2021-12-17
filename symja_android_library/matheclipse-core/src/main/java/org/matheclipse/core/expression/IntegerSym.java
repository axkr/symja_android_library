package org.matheclipse.core.expression;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.function.Function;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.fraction.BigFraction;
import org.hipparchus.util.ArithmeticUtils;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.numbertheory.Primality;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;

/**
 * IInteger implementation which uses an internal <code>int</code> value
 *
 * @see AbstractIntegerSym
 * @see BigIntegerSym
 */
public class IntegerSym extends AbstractIntegerSym {

  /** */
  private static final long serialVersionUID = 6389228668633533063L;

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
  public long bitLength() {
    if (fIntValue == 0) {
      return 0L;
    }
    return 32 - Integer.numberOfLeadingZeros(fIntValue < 0 ? -fIntValue : fIntValue);
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
      return Double.compare(fIntValue, ((ISignedNumber) expr).doubleValue());
    }
    return super.compareTo(expr);
  }

  @Override
  public ComplexNum complexNumValue() {
    // double precision complex number
    return ComplexNum.valueOf(doubleValue());
  }

  /** {@inheritDoc} */
  @Override
  public IInteger dec() {
    return add(F.CN1);
  }

  /** {@inheritDoc} */
  @Override
  public IInteger inc() {
    return add(F.C1);
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
  public ISignedNumber divideBy(ISignedNumber that) {
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

  /** Returns the greatest common divisor of this large integer and the one specified. */
  @Override
  public IInteger gcd(final IInteger that) {
    if (that instanceof IntegerSym) {
      try {
        return valueOf(ArithmeticUtils.gcd(fIntValue, ((IntegerSym) that).fIntValue));
      } catch (MathRuntimeException ex) {
        //
      }
    }
    return valueOf(toBigNumerator().gcd(that.toBigNumerator()));
  }

  /** {@inheritDoc} */
  @Override
  public IInteger denominator() {
    return F.C1;
  }

  /** {@inheritDoc} */
  @Override
  public BigFraction toBigFraction() {
    return new BigFraction(fIntValue);
  }

  /** {@inheritDoc} */
  @Override
  public IInteger numerator() {
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
    return fIntValue;
  }

  /** {@inheritDoc} */
  @Override
  public ISignedNumber im() {
    return F.C0;
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
      Function<IExpr, ? extends CharSequence> variables) {
    String prefix = AbstractAST.getPrefixF(properties);
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
    SourceCodeProperties p = AbstractAST.scalaFormProperties(symbolsAsFactoryMethod);
    return internalJavaString(p, depth, F.CNullFunction);
  }

  @Override
  public byte byteValue() {
    return (byte) fIntValue;
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

  @Override
  public boolean isEven() {
    return (fIntValue & 0x00000001) == 0x00000000;
  }

  @Override
  public boolean isGT(ISignedNumber obj) {
    if (obj instanceof IntegerSym) {
      return fIntValue > ((IntegerSym) obj).fIntValue;
    }
    if (obj instanceof BigIntegerSym) {
      return toBigNumerator().compareTo(((BigIntegerSym) obj).toBigNumerator()) > 0;
    }
    if (obj instanceof AbstractFractionSym) {
      return -((AbstractFractionSym) obj).compareTo(AbstractFractionSym.valueOf(fIntValue)) > 0;
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
  public boolean isLT(ISignedNumber obj) {
    if (obj instanceof IntegerSym) {
      return fIntValue < ((IntegerSym) obj).fIntValue;
    }
    if (obj instanceof BigIntegerSym) {
      return toBigNumerator().compareTo(((BigIntegerSym) obj).toBigNumerator()) < 0;
    }
    if (obj instanceof AbstractFractionSym) {
      return -((AbstractFractionSym) obj).compareTo(AbstractFractionSym.valueOf(fIntValue)) < 0;
    }
    return doubleValue() < obj.doubleValue();
  }

  @Override
  public boolean isMinusOne() {
    return fIntValue == -1;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNegative() {
    return fIntValue < 0;
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
  public IExpr nthRoot(int n) throws ArithmeticException {
    if (n < 0) {
      throw new IllegalArgumentException("nthRoot(" + n + ") n must be >= 0");
    }
    if (n == 2) {
      return sqrt();
    }
    if (complexSign() == 0) {
      return F.C0;
    } else if (complexSign() < 0) {
      if (n % 2 == 0) {
        // even exponent n
        throw new ArithmeticException();
      } else {
        // odd exponent n
        return negate().nthRoot(n).negate();
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
      return result;
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
  public ISignedNumber re() {
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
  public int complexSign() {
    return (fIntValue > 0) ? 1 : (fIntValue == 0) ? 0 : -1;
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
  public BigInteger toBigNumerator() {
    return BigInteger.valueOf(fIntValue);
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
  public long toLongDefault(long defaultValue) {
    return fIntValue;
  }

  /** {@inheritDoc} */
  @Override
  public long toLong() throws ArithmeticException {
    return fIntValue;
  }

  @Override
  public String toString() {
    return Integer.toString(fIntValue);
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

package org.matheclipse.core.numerics.utils;

import java.io.EOFException;
import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.Aprational;
import org.apfloat.AprationalMath;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.BigIntegerLimitExceeded;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

public class RealDigitsResult {
  public static RealDigitsResult create(Apfloat a) {
    return create(a, a.radix());
  }

  public static RealDigitsResult create(Apfloat a, int radix) {
    return create(a, radix, Long.MIN_VALUE);
  }

  public static RealDigitsResult create(Apfloat a, int radix, long length) {
    RealDigitsResult rd = new RealDigitsResult();
    rd.digits(a, radix, length, Long.MIN_VALUE);
    // System.out.println(rd.digitsList + " : " + rd.numberOfLeftDigits);
    return rd;
  }

  public static RealDigitsResult create(Apfloat a, int radix, long length, long startDigit) {
    RealDigitsResult rd = new RealDigitsResult();
    rd.digits(a, radix, length, startDigit);
    // System.out.println(rd.digitsList + " : " + rd.numberOfLeftDigits);
    return rd;
  }

  public static RealDigitsResult create(Aprational a) {
    return create(a, a.radix());
  }

  public static RealDigitsResult create(Aprational a, int radix) {
    RealDigitsResult rd = new RealDigitsResult();
    rd.digits(a, radix);
    // System.out.println(rd.digitsList + " : " + rd.numberOfLeftDigits);
    return rd;
  }

  IASTAppendable digitsList;

  long numberOfLeftDigits;

  private RealDigitsResult() {}

  private void digits(Apfloat a, int radix, long length, long startDigit) {
    a = a.toRadix(radix);
    if (F.isNotPresent(length)) {
      if (a.isZero()) {
        length = 1;
      } else if (a.precision() == Apfloat.INFINITE) {
        length = Math.max(a.scale(), a.size());
      } else {
        length = a.precision();
      }
    }

    if (a.isZero()) {
      if (length > Config.MAX_AST_SIZE) {
        throw new ASTElementLimitExceeded(length);
      }
      digitsList = F.constantArray(F.C0, (int) length);
      numberOfLeftDigits = F.isNotPresent(startDigit) ? 1 : startDigit + 1;
      return;
    }
    long scale = a.scale();

    numberOfLeftDigits = scale;
    final long lengthFinal = length;
    if (lengthFinal > Config.MAX_AST_SIZE) {
      throw new ASTElementLimitExceeded(lengthFinal);
    }
    digitsList = F.ListAlloc((int) lengthFinal);
    if (F.isPresent(startDigit)) {
      long adjust = scale - 1;
      if (adjust < startDigit) {
        // Pad beginning of list with zeros
        long padLength = Math.min(length, startDigit - adjust);
        pad(padLength, F.C0);
      } else if (adjust > startDigit) {
        // Truncate first significant digits
        a = ApfloatMath.scale(a, -startDigit - 1);
        a = a.frac();
        a = ApfloatMath.scale(a, startDigit + 1);
        // If the number now has some extra leading zeros (i.e. the scale became smaller) they won't
        // be printed, print them explicitly
        adjust = a.scale() - 1;
        if (adjust < startDigit) {
          long padLength = Math.min(length, startDigit - adjust);
          pad(padLength, F.C0);
        }
      }
      numberOfLeftDigits = startDigit + 1;
    }
    long size = a.size();
    Writer writer = new Writer() {
      long writeSize;

      @Override
      public void close() {}

      @Override
      public void flush() {}

      @Override
      public void write(char[] cbuf, int off, int len) throws IOException {
        for (int i = 0; i < len; i++) {
          if (digitsList.argSize() >= lengthFinal || writeSize >= size) {
            throw new EOFException();
          }
          char c = cbuf[i + off];
          if (c != '.' && c != '-') {
            digitsList.append(Character.digit(c, radix));
            writeSize++;
          }
        }
      }
    };
    try {
      a.writeTo(writer);
    } catch (EOFException e) {
      // Indication that we got the desired number of digits written
    } catch (IOException e) {
      throw new RuntimeException("Error writing digits", e);
    }
    long precision = a.precision();
    if (length > digitsList.argSize() && precision > digitsList.argSize()) {
      // Pad with trailing zeros as long as we have precision
      long padLength = Math.min(length, precision) - digitsList.argSize();
      pad(padLength, F.C0);
    }
    if (length > digitsList.argSize()) {
      // Pad the remaining requested length with nulls as no significant digits exist
      long padLength = length - digitsList.argSize();
      pad(padLength, S.Indeterminate);
    }
  }

  /**
   * Create the digits of the non-negative number <code>num/den</code> in a base, which may be
   * greater than <code>36</code>.
   *
   * @param num the non-negative numerator
   * @param den the positive denominator
   * @param radix the base
   * @param length the number of digits or {@link Long#MIN_VALUE} for all available digits
   * @param startDigit the exponent of the first digit or {@link Long#MIN_VALUE} for the leading
   *        digit
   * @param available the number of significant digits of an approximate number, or
   *        {@link Long#MAX_VALUE} for an exact number; digits after the significant ones are
   *        <code>Indeterminate</code>
   */
  public static RealDigitsResult create(BigInteger num, BigInteger den, int radix, long length,
      long startDigit, long available) {
    RealDigitsResult rd = new RealDigitsResult();
    rd.digits(num, den, radix, length, startDigit, available);
    return rd;
  }

  private void digits(BigInteger num, BigInteger den, int radix, long length, long startDigit,
      long available) {
    final BigInteger b = BigInteger.valueOf(radix);
    final boolean zero = num.signum() == 0;
    final long scale = zero ? 1 : scale(num, den, b);
    if (F.isNotPresent(length)) {
      length = zero ? 1 : available == Long.MAX_VALUE ? Math.max(scale, 1) : available;
    }
    if (length > Config.MAX_AST_SIZE) {
      throw new ASTElementLimitExceeded(length);
    }
    final long firstExp = F.isPresent(startDigit) ? startDigit : scale - 1;
    numberOfLeftDigits = firstExp + 1;
    final long lastExp = firstExp - length + 1;
    // the smallest exponent of a significant digit
    final long lastValid =
        zero || available == Long.MAX_VALUE ? Long.MIN_VALUE : scale - available;
    final long lowExp = Math.max(lastExp, lastValid);
    final IExpr[] digitArray = new IExpr[(int) length];
    if (lowExp <= firstExp) {
      BigInteger n = floorTimesPower(num, den, b, -lowExp);
      for (long p = lowExp; p <= firstExp; p++) {
        BigInteger[] qr = n.divideAndRemainder(b);
        digitArray[(int) (firstExp - p)] = F.ZZ(qr[1].intValue());
        n = qr[0];
      }
    }
    digitsList = F.ListAlloc((int) length);
    for (int i = 0; i < digitArray.length; i++) {
      digitsList.append(digitArray[i] == null ? S.Indeterminate : digitArray[i]);
    }
  }

  /**
   * The exponent <code>e</code> with <code>b^(e-1) &lt;= num/den &lt; b^e</code> for a positive
   * number <code>num/den</code>.
   */
  private static long scale(BigInteger num, BigInteger den, BigInteger b) {
    long e = (long) Math.floor((num.bitLength() - den.bitLength()) * Math.log(2.0)
        / Math.log(b.doubleValue()));
    while (floorTimesPower(num, den, b, -e).signum() > 0) {
      e++;
    }
    while (floorTimesPower(num, den, b, 1 - e).signum() == 0) {
      e--;
    }
    return e;
  }

  /** <code>Floor(num/den * b^k)</code> */
  private static BigInteger floorTimesPower(BigInteger num, BigInteger den, BigInteger b, long k) {
    if (k >= 0) {
      return num.multiply(power(b, k)).divide(den);
    }
    return num.divide(den.multiply(power(b, -k)));
  }

  /**
   * <code>b^k</code> for a base <code>b &gt; 1</code> and <code>k &gt;= 0</code>.
   *
   * @throws BigIntegerLimitExceeded if the power would have more than
   *         {@link Config#MAX_BIT_LENGTH} bits
   */
  public static BigInteger power(BigInteger b, long k) {
    if ((double) k * b.bitLength() > Config.MAX_BIT_LENGTH) {
      throw new BigIntegerLimitExceeded(Config.MAX_BIT_LENGTH);
    }
    return b.pow((int) k);
  }

  /** The digits of a non-negative integer in a base, the most significant digit first. */
  private static IASTAppendable integerDigits(BigInteger n, BigInteger b) {
    IASTAppendable result = F.ListAlloc(16);
    do {
      BigInteger[] qr = n.divideAndRemainder(b);
      result.append(1, F.ZZ(qr[1].intValue()));
      n = qr[0];
    } while (n.signum() > 0);
    return result;
  }

  /**
   * Create a list of digits for a rational number. The list will contain the integer part and the
   * repeating part of the expansion of the rational number in the given base.
   * <p>
   * <a href="https://en.wikipedia.org/wiki/Repeating_decimal">Wikipedia - Repeating decimal</a>
   *
   * @param a
   * @param radix
   */
  private void digits(Aprational a, int radix) {
    a = AprationalMath.abs(a);
    if (a.isZero()) {
      digitsList = F.ListAlloc(2);
      digitsList.append(F.CListC0);
      numberOfLeftDigits = 1;
      return;
    }
    final BigInteger b = BigInteger.valueOf(radix);
    final BigInteger num = a.numerator().toBigInteger();
    final BigInteger q = a.denominator().toBigInteger();
    BigInteger[] intFrac = num.divideAndRemainder(q);
    BigInteger i = intFrac[0];
    BigInteger p = intFrac[1];
    final long scale = scale(num, q, b);
    IASTAppendable s = F.ListAlloc(15);
    int pos = 1;
    int start = -1;
    Map<BigInteger, Integer> occurs = new HashMap<>();
    while (!occurs.containsKey(p)) {
      occurs.put(p, pos);
      BigInteger[] qr = b.multiply(p).divideAndRemainder(q);
      BigInteger z = qr[0];
      p = qr[1];
      if (p.signum() == 0) {
        if (z.signum() != 0) {
          s.append(z.intValue());
        }
        if (i.signum() != 0) {
          IAST dList = integerDigits(i, b);
          for (int j = 1; j < dList.size(); j++) {
            s.append(j, dList.get(j));
          }
        } else {
          if (start > 0) {
            s = s.subList(start, s.size());
          }
        }
        digitsList = s;
        numberOfLeftDigits = scale;
        return;
      }
      s.append(z.intValue());
      if (start < 0 && z.signum() != 0) {
        start = pos;
      }
      pos++;
    }
    IASTAppendable initial = s.subList(1, occurs.get(p));
    IASTAppendable repetend = s.subList(occurs.get(p), pos);
    if (i.signum() != 0) {
      IAST dList = integerDigits(i, b);
      for (int j = 1; j < dList.size(); j++) {
        initial.append(j, dList.get(j));
      }
    } else {
      initial = initial.subList(Math.min(initial.size(), start), initial.size());
      // Move leading zeros to the end in case there is no integer part
      while (repetend.get(1).isZero()) {
        repetend.remove(1);
        repetend.append(F.C0);
      }
    }
    // the shortest pre-period: 6.666... is {{{6}}, 1} and not {{6, {6}}, 1}
    while (initial.argSize() > 0 && initial.last().equals(repetend.last())) {
      initial.remove(initial.argSize());
      IExpr last = repetend.last();
      repetend.remove(repetend.argSize());
      repetend.append(1, last);
    }
    digitsList = F.ListAlloc(initial.argSize() + 2);
    digitsList.appendArgs(initial);
    digitsList.append(repetend);
    numberOfLeftDigits = scale;
  }

  public IASTAppendable getDigitsList() {
    return digitsList;
  }

  public long getNumberOfLeftDigits() {
    return numberOfLeftDigits;
  }

  private void pad(long padLength, IExpr digit) {
    if (padLength > Config.MAX_AST_SIZE) {
      throw new ASTElementLimitExceeded(padLength);
    }
    IASTAppendable constantArray = F.constantArray(digit, (int) padLength);
    digitsList.appendArgs(constantArray);
  }

}

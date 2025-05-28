package org.matheclipse.core.numerics.utils;

import java.io.EOFException;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.Apint;
import org.apfloat.Aprational;
import org.apfloat.AprationalMath;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
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
    if (length == Long.MIN_VALUE) {
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
      numberOfLeftDigits = startDigit == Long.MIN_VALUE ? 1 : startDigit + 1;
      return;
    }
    long scale = a.scale();

    numberOfLeftDigits = scale;
    final long lengthFinal = length;
    if (lengthFinal > Config.MAX_AST_SIZE) {
      throw new ASTElementLimitExceeded(lengthFinal);
    }
    digitsList = F.ListAlloc((int) lengthFinal);
    if (startDigit != Long.MIN_VALUE) {
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
   * Create a list of digits for a rational number. The list will contain the integer part and the
   * repeating part of the decimal expansion of the rational number.
   * <p>
   * <a href="https://en.wikipedia.org/wiki/Repeating_decimal">Wikipedia - Repeating decimal</a>
   * 
   * @param a
   * @param radix
   */
  private void digits(Aprational a, int radix) {
    a = AprationalMath.abs(a.toRadix(radix));
    if (a.isZero()) {
      digitsList = F.ListAlloc(2);
      digitsList.append(F.CListC0);
      numberOfLeftDigits = 1;
      return;
    }
    Apint b = new Apint(radix, radix);
    Apint i = a.truncate();
    Aprational r = a.frac();
    Apint p = r.numerator();
    Apint q = r.denominator();
    IASTAppendable s = F.ListAlloc(15);
    int pos = 1;
    int start = -1;
    Map<Apint, Integer> occurs = new HashMap<>();
    while (!occurs.containsKey(p)) {
      occurs.put(p, pos);
      Apint bp = b.multiply(p);
      Apint z = bp.divide((Aprational) q).floor();
      p = bp.subtract(z.multiply(q));
      if (p.isZero()) {
        if (!z.isZero()) {
          s.append(z.intValue());
        }
        if (!i.isZero()) {
          RealDigitsResult realDigits = create((Apfloat) i);
          IAST dList = realDigits.getDigitsList();
          for (int j = 1; j < dList.size(); j++) {
            s.append(j, dList.get(j));
          }
        } else {
          if (start > 0) {
            s = s.subList(start, s.size());
          }
        }
        digitsList = s;
        numberOfLeftDigits = a.scale();
        return;
      }
      s.append(z.intValue());
      if (start < 0 && !z.isZero()) {
        start = pos;
      }
      pos++;
    }
    IASTAppendable initial = s.subList(1, occurs.get(p));
    IASTAppendable repetend = s.subList(occurs.get(p), pos);
    if (!i.isZero()) {
      RealDigitsResult realDigits = create((Apfloat) i);
      IAST dList = realDigits.getDigitsList();
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
    digitsList = F.ListAlloc(initial.argSize() + 2);
    digitsList.appendArgs(initial);
    digitsList.append(repetend);
    numberOfLeftDigits = a.scale();
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

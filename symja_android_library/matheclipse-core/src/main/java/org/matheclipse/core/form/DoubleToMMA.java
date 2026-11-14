package org.matheclipse.core.form;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import org.matheclipse.parser.client.ParserConfig;

/**
 * Convert a Java <code>double</code> value into a string similar to the Mathematica output format.
 */
public class DoubleToMMA {

  /**
   * Convert a Java <code>double</code> value into a string similar to the Mathematica output
   * format.
   *
   * @param buf a string builder where the output should be appended
   * @param value
   * @param exponent use scientific notation for all numbers with exponents outside the range <code>
   *     -exponent</code> to <code>exponent</code>.
   * @param significantFigures one more than the number of significant digits which are printed;
   *        in fixed notation a number of at least 10 shows one digit more
   * @param texScientificNotation if <code>true</code> write the power of ten in TeX
   */
  public static void doubleToMMA(Appendable buf, double value, int exponent, int significantFigures,
      boolean texScientificNotation, boolean relaxedSyntax) throws IOException {
    if (Double.isNaN(value)) {
      buf.append("Indeterminate");
      return;
    }
    if (Double.isInfinite(value)) {
      if (value < 0) {
        buf.append("-Infinity");
        // buf.append("Underflow");
      } else {
        buf.append("Infinity");
        // buf.append("Overflow");
      }
      // buf.append(relaxedSyntax ? "()" : "[]");
      return;
    }
    if (value == 0.0) {
      buf.append(1.0 / value < 0.0 ? "-0.0" : "0.0");
      return;
    }
    final BigDecimal shortest = new BigDecimal(Double.toString(value));
    final int exp = decimalExponent(shortest);
    if (exponent > 0 && -exponent <= exp && exp <= exponent) {
      // all integer digits and up to significantFigures - 1 digits in total, at least one of them
      // a fraction digit
      int fractionDigits = exp > 0 ? significantFigures - exp - 1 : -exp + significantFigures - 2;
      fractionDigits = Math.min(Math.max(fractionDigits, 1), MAX_FRACTION_DIGITS);
      String test = shortest.setScale(fractionDigits, RoundingMode.HALF_UP).stripTrailingZeros()
          .toPlainString();
      buf.append(test);
      if (test.indexOf('.') < 0) {
        // ensure trailing zero after decimal point
        buf.append(".0");
      }
      return;
    }
    appendScientific(buf, roundHalfUp(value, significantFigures - 1), texScientificNotation);
  }

  /** The most fraction digits written in fixed notation. */
  private static final int MAX_FRACTION_DIGITS = 30;

  /**
   * The value rounded to <code>digits</code> significant decimal digits. The digits which are
   * rounded are those of the shortest decimal number which reads back as <code>value</code>, and
   * a tie is rounded away from zero: <code>0.1234565</code> is <code>0.123457</code> with 6
   * digits, although the binary number is slightly less than the tie.
   */
  private static BigDecimal roundHalfUp(double value, int digits) {
    return new BigDecimal(Double.toString(value))
        .round(new MathContext(Math.max(digits, 1), RoundingMode.HALF_UP));
  }

  /** The exponent <code>e</code> of a non-zero number <code>d.ddd*10^e</code>. */
  private static int decimalExponent(BigDecimal decimal) {
    return decimal.precision() - decimal.scale() - 1;
  }

  /**
   * Write an already rounded non-zero number as <code>d.ddd*10^e</code> without trailing zeros
   * in the mantissa. The exponent is the one of the rounded number: <code>999999.9</code> rounded
   * to 3 digits is <code>1.*10^6</code>.
   */
  private static void appendScientific(Appendable buf, BigDecimal rounded,
      boolean texScientificNotation) throws IOException {
    final int exponent = decimalExponent(rounded);
    String digits = rounded.unscaledValue().abs().toString();
    int end = digits.length();
    while (end > 1 && digits.charAt(end - 1) == '0') {
      end--;
    }
    if (rounded.signum() < 0) {
      buf.append('-');
    }
    buf.append(digits.charAt(0));
    buf.append('.');
    buf.append(digits, 1, end);
    appendPowerOfTen(buf, exponent, texScientificNotation);
  }

  /**
   * Write the factor <code>*10^e</code> of a number in scientific notation; in TeX it is
   * <code>\times 10^e</code>, with the exponent in braces if it has more than one character.
   */
  public static void appendPowerOfTen(Appendable buf, long exponent,
      boolean texScientificNotation) throws IOException {
    String exponentStr = Long.toString(exponent);
    if (texScientificNotation) {
      buf.append("\\times 10^");
      if (exponentStr.length() > 1) {
        buf.append('{').append(exponentStr).append('}');
      } else {
        buf.append(exponentStr);
      }
    } else {
      buf.append("*10^");
      buf.append(exponentStr);
    }
  }

  /**
   * Convert a Java <code>double</code> value into a string similar to the Mathematica output
   * format.
   *
   * @param buf a string builder where the output should be appended
   * @param value the double value which should be formatted
   * @param exponent use scientific notation for all numbers with exponents outside the range <code>
   *     -exponent</code> to <code>exponent</code>.
   * @param significantFigures the number of significant figures which should be printed
   */
  public static void doubleToMMA(StringBuilder buf, double value, int exponent,
      int significantFigures) {
    try {
      doubleToMMA(buf, value, exponent, significantFigures, false,
          ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS);
    } catch (IOException ioex) {
    }
  }

  /**
   * Convert a Java <code>double</code> value into a string similar to the Mathematica output
   * format.
   *
   * @param buf a string builder where the output should be appended
   * @param value the double value which should be formatted
   * @param exponent use scientific notation for all numbers with exponents outside the range <code>
   *     -exponent</code> to <code>exponent</code>.
   * @param significantFigures the number of significant figures which should be printed
   */
  public static void doubleToMMA(StringBuilder buf, double value, int exponent,
      int significantFigures, boolean relaxedSyntax) {
    try {
      doubleToMMA(buf, value, exponent, significantFigures, false, relaxedSyntax);
    } catch (IOException ioex) {
    }
  }

  /**
   * Write a Java double value in scientific notation.
   *
   * @param buf
   * @param value
   * @param significantFigures the number of digits of the mantissa
   * @param exponent unused: the exponent written is the one of the rounded value, which is one
   *        more than the exponent of <code>value</code> if the rounding carries into a new digit
   * @param texScientificNotation if <code>true</code> write the power of ten in TeX
   * @throws IOException
   */
  public static void doubleToScientific(Appendable buf, double value, int significantFigures,
      int exponent, boolean texScientificNotation) throws IOException {
    if (value == 0.0 || Double.isNaN(value) || Double.isInfinite(value)) {
      doubleToMMA(buf, value, 0, significantFigures + 1, texScientificNotation, false);
      return;
    }
    appendScientific(buf, roundHalfUp(value, significantFigures), texScientificNotation);
  }

  /**
   * Convert a Java <code>double</code> value into a string similar to the Mathematica scientific
   * output format.
   *
   * @param buf a string builder where the output should be appended
   * @param value the double value which should be formatted
   * @param significantFigures the number of significant figures which should be printed
   */
  public static void doubleToScientific(StringBuilder buf, double value, int significantFigures) {
    try {
      doubleToScientific(buf, value, significantFigures, Integer.MIN_VALUE, false);
    } catch (IOException ioex) {
    }
  }

  private DoubleToMMA() {}
}

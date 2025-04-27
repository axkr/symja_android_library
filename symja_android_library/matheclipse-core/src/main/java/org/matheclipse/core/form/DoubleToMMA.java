package org.matheclipse.core.form;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.parser.client.ParserConfig;

/**
 * Convert a Java <code>double</code> value into a string similar to the Mathematica output format.
 */
public class DoubleToMMA {
  private static final Logger LOGGER = LogManager.getLogger(DoubleToMMA.class);

  private static final String HASH_STR = "##############################";

  /**
   * Convert a Java <code>double</code> value into a string similar to the Mathematica output
   * format.
   *
   * @param buf a string builder where the output should be appended
   * @param value
   * @param exponent use scientific notation for all numbers with exponents outside the range <code>
   *     -exponent</code> to <code>exponent</code>.
   * @param significantFigures
   * @param texScientificNotation TODO
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
    String s = String.format(Locale.US, "%16.16E", value);
    int start = s.indexOf('E');
    String expStr = s.substring(start + 1);
    // on Android you may receive a '+' sign: 1.2345123456789000E+04
    if (expStr.startsWith("+")) {
      expStr = expStr.substring(1);
    }
    int exp = Integer.parseInt(expStr);
    if (exponent > 0 && -exponent <= exp && exp <= exponent) {
      DecimalFormatSymbols usSymbols = new DecimalFormatSymbols(Locale.US);
      DecimalFormat format;
      int hashSize;
      if (exp > 0) {
        hashSize = significantFigures - exp - 1;
        if (hashSize <= 0) {
          hashSize = 1;
        }
        if (hashSize >= HASH_STR.length()) {
          hashSize = HASH_STR.length();
        }
        format = new DecimalFormat( //
            HASH_STR.substring(0, exp) //
                + "0." //
                + HASH_STR.substring(0, hashSize),
            usSymbols);
      } else {
        hashSize = -exp + significantFigures - 2;
        if (hashSize <= 0) {
          hashSize = 1;
        }
        if (hashSize >= HASH_STR.length()) {
          hashSize = HASH_STR.length();
        }
        format = new DecimalFormat("#." + HASH_STR.substring(0, hashSize), usSymbols);
      }
      String test = format.format(value);
      start = test.indexOf('E');
      if (start > 0) {
        test = test.substring(0, start);
      }
      test = test.trim();
      if (test.contains(".")) {
        for (int i = test.length() - 1; i >= 0; i--) {
          if (test.charAt(i) != '0') {
            if (test.charAt(i) == '.') {
              // ensure trailing zero after decimal point
              test = test.substring(0, i + 2);
              break;
            }
            test = test.substring(0, i + 1);
            break;
          }
        }
      }

      buf.append(test);
      if (test.indexOf(".") < 0) {
        buf.append(".0");
      }
      return;
    }
    doubleToScientific(buf, value, significantFigures - 1, exp, texScientificNotation);
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
      LOGGER.error("DoubleToMMA.doubleToMMA() failed", ioex);
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
      LOGGER.error("DoubleToMMA.doubleToMMA() failed", ioex);
    }
  }

  /**
   * Write a Java double value in scientific notation.
   *
   * @param buf
   * @param value
   * @param significantFigures
   * @param exponent
   * @param texScientificNotation if <code>true</code> use <code>123^{456}</code> style to write the
   *        exponent
   * @throws IOException
   */
  public static void doubleToScientific(Appendable buf, double value, int significantFigures,
      int exponent, boolean texScientificNotation) throws IOException {
    int start;
    String s = String.format(Locale.US, "%1." + (significantFigures - 1) + "E", value);
    start = s.indexOf('E');
    if (exponent == Integer.MIN_VALUE) {
      exponent = Integer.parseInt(s.substring(start + 1));
    }
    s = s.substring(0, start);

    if (s.charAt(s.length() - 1) == '0') {
      int indx = s.indexOf(".");
      if (indx > 0) {
        s = removeTrailingZeroes(s);
      }
    }
    buf.append(s.trim());
    if (texScientificNotation) {
      buf.append("*10^{");
      buf.append(Integer.toString(exponent));
      buf.append("}");
    } else {
      buf.append("*10^");
      buf.append(Integer.toString(exponent));
    }
  }

  /**
   * Remove zeros from the end of the string.
   * 
   * @param s
   * @return
   */
  private static String removeTrailingZeroes(String s) {
    StringBuilder sb = new StringBuilder(s);
    while (sb.charAt(sb.length() - 1) == '0') {
      sb.setLength(sb.length() - 1);
    }
    return sb.toString();
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
      LOGGER.error("DoubleToMMA.doubleToScientific() failed", ioex);
    }
  }

  private DoubleToMMA() {}
}

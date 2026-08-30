package org.matheclipse.core.form.output;

import java.io.IOException;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.form.NumberFormatter;
import org.matheclipse.core.form.NumberFormatter.FormattedNumber;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.parser.client.ParserConfig;

/**
 * Renders a <code>ScientificForm, EngineeringForm, NumberForm, AccountingForm, PaddedForm,
 * DecimalForm</code> wrapper as the plain ASCII string which <code>ToString</code> returns.
 *
 * <p>
 * Like <code>OutputForm</code> this is a three line form: a factored out exponent is written above
 * the mantissa and a fraction above and below its fraction bar, so that
 * <code>ToString(ScientificForm(1234.5))</code> is <code>"3\n1.2345×10"</code>. Everything
 * which is not a number itself is handed to {@link OutputFormFactory}, which installs the same
 * {@link NumberFormatter} for the wrapped subtree.
 *
 * <p>
 * The padding characters which fill the field to the left of the number are only meaningful in a
 * column of numbers, so the result is unindented - <code>ToString(PaddedForm(123456, 8))</code> is
 * <code>"123456"</code>, not <code>"  123456"</code>.
 */
public class NumberFormPrinter {

  private NumberFormPrinter() {}

  /**
   * @param formAST a number form wrapper such as <code>NumberForm(1234.5, NumberPoint -&gt; ",")
   *        </code>
   * @return <code>null</code> if <code>formAST</code> is not a number form wrapper or could not be
   *         converted
   */
  public static String print(IAST formAST, EvalEngine engine) {
    NumberFormatter formatter = NumberFormatter.of(formAST, engine);
    if (formatter == null || formAST.size() < 2) {
      return null;
    }
    String[] lines = threeLines(formatter, formAST.arg1(), engine);
    if (lines == null) {
      String singleLine = convert(formAST, engine);
      if (singleLine == null) {
        return null;
      }
      lines = new String[] {"", singleLine, ""};
    }
    return join(lines);
  }

  /**
   * Split a number into the three lines it occupies, or return <code>null</code> if
   * <code>expr</code> is not a number which this class lays out itself.
   */
  private static String[] threeLines(NumberFormatter formatter, IExpr expr, EvalEngine engine) {
    if (expr.isFraction()) {
      IFraction fraction = (IFraction) expr;
      FormattedNumber numerator = formatter.format(fraction.toBigNumerator());
      FormattedNumber denominator = formatter.format(fraction.toBigDenominator());
      if (numerator == null || denominator == null || numerator.custom.isPresent()
          || denominator.custom.isPresent()) {
        return null;
      }
      ASCIIPrettyPrinter3 printer = new ASCIIPrettyPrinter3();
      printer.printFraction(numerator.mantissa, denominator.mantissa);
      return printer.toStringBuilder();
    }
    FormattedNumber formatted = format(formatter, expr, engine.getSignificantFigures() + 1);
    if (formatted == null || formatted.custom.isPresent()) {
      return null;
    }
    if (!formatted.scientific) {
      return new String[] {"", formatted.mantissa, ""};
    }
    return new String[] {Integer.toString(formatted.exponent),
        formatted.mantissa + formatter.getMultiplier() + "10", ""};
  }

  private static FormattedNumber format(NumberFormatter formatter, IExpr expr,
      int significantFigures) {
    if (expr.isInteger()) {
      return formatter.format(((IInteger) expr).toBigNumerator());
    }
    if (expr instanceof ApfloatNum) {
      return formatter.format(((ApfloatNum) expr).apfloatValue(), significantFigures);
    }
    if (expr instanceof INum) {
      return formatter.format(((INum) expr).doubleValue(), significantFigures);
    }
    return null;
  }

  /** Print the whole wrapper with {@link OutputFormFactory}, which installs the formatter. */
  private static String convert(IAST formAST, EvalEngine engine) {
    int significantFigures = engine.getSignificantFigures();
    OutputFormFactory factory = OutputFormFactory.get(ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS,
        false, true, significantFigures - 1, significantFigures + 1);
    StringBuilder buf = new StringBuilder();
    try {
      return factory.convert(buf, formAST) ? buf.toString() : null;
    } catch (RuntimeException rex) {
      return null;
    }
  }

  /**
   * Join the three lines, dropping the blank ones above and below and the field padding in front
   * of the number.
   */
  private static String join(String[] lines) {
    String[] trimmed = new String[lines.length];
    int first = -1;
    int last = -1;
    int indent = Integer.MAX_VALUE;
    for (int i = 0; i < lines.length; i++) {
      trimmed[i] = rtrim(lines[i]);
      if (!trimmed[i].isEmpty()) {
        if (first < 0) {
          first = i;
        }
        last = i;
        indent = Math.min(indent, trimmed[i].length() - ltrim(trimmed[i]).length());
      }
    }
    if (first < 0) {
      return "";
    }
    StringBuilder buf = new StringBuilder();
    for (int i = first; i <= last; i++) {
      if (i > first) {
        buf.append('\n');
      }
      buf.append(trimmed[i].length() < indent ? "" : trimmed[i].substring(indent));
    }
    return buf.toString();
  }

  private static String rtrim(String line) {
    int end = line.length();
    while (end > 0 && line.charAt(end - 1) == ' ') {
      end--;
    }
    return line.substring(0, end);
  }

  private static String ltrim(String line) {
    int start = 0;
    while (start < line.length() && line.charAt(start) == ' ') {
      start++;
    }
    return line.substring(start);
  }
}

package org.matheclipse.core.form;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import org.apfloat.Apfloat;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Formats real numbers for the <code>ScientificForm, EngineeringForm, NumberForm, AccountingForm,
 * PaddedForm, DecimalForm</code> display wrappers.
 *
 * <p>
 * The wrappers stay unevaluated in the expression tree; the output factories
 * ({@link org.matheclipse.core.form.output.OutputFormFactory},
 * {@link org.matheclipse.core.form.tex.TeXFormFactory},
 * {@link org.matheclipse.core.form.mathml.MathMLFormFactory}) install a
 * <code>NumberFormatter</code> for the duration of the wrapped subtree and route every
 * <code>double</code>, <code>Apfloat</code> and integer through it.
 *
 * <p>
 * This class produces the mantissa and the base-10 exponent only. Assembling
 * <code>mantissa &times; 10^exponent</code> is left to each factory, because the notation differs
 * ("*10^7", "\times 10^{7}", "&lt;msup&gt;").
 */
public class NumberFormatter {

  /** Which display wrapper this formatter implements. */
  public enum FormKind {
    SCIENTIFIC, ENGINEERING, NUMBER, ACCOUNTING, PADDED, DECIMAL
  }

  /** Marker for <code>DigitBlock -&gt; Infinity</code> (never break a digit block). */
  private static final int NO_DIGIT_BLOCK = Integer.MAX_VALUE;

  /**
   * <code>NumberSeparator</code> default. The right hand separator is empty, so that
   * <code>DigitBlock -&gt; n</code> only becomes visible in the fraction if the caller asks for a
   * separator there.
   */
  private static final IExpr DEFAULT_SEPARATOR = F.list(F.stringx(","), F.stringx(""));

  /** A formatted number, split into the parts each output factory assembles differently. */
  public static final class FormattedNumber {
    /** The complete mantissa including signs, separators and padding. */
    public final String mantissa;

    /** The base-10 exponent. Only meaningful if {@link #scientific} is <code>true</code>. */
    public final int exponent;

    /** <code>true</code> if the number should be shown as <code>mantissa*10^exponent</code>. */
    public final boolean scientific;

    /**
     * The result of a user supplied <code>NumberFormat</code> function, or {@link F#NIL}. If
     * present the factory should print this expression instead of assembling the parts itself.
     */
    public final IExpr custom;

    private FormattedNumber(String mantissa, int exponent, boolean scientific, IExpr custom) {
      this.mantissa = mantissa;
      this.exponent = exponent;
      this.scientific = scientific;
      this.custom = custom;
    }
  }

  private final FormKind kind;

  /** Total number of digits requested (<code>n</code>), or {@code -1}. */
  private final int totalDigits;

  /** Digits right of the decimal point (<code>f</code>), or {@code -1}. */
  private final int fractionDigits;

  /**
   * <code>DefaultPrintPrecision</code>: the number of significant digits to use when the wrapper
   * does not specify <code>n</code> itself, or {@code -1} for <code>Automatic</code>.
   */
  private final int defaultPrintPrecision;

  private final int digitBlockLeft;
  private final int digitBlockRight;
  private final String separatorLeft;
  private final String separatorRight;
  private final String paddingLeft;
  private final String paddingRight;
  private final String numberPoint;
  private final String signNegative;
  private final String signNegativeClose;
  private final String signPositive;
  private final boolean signPadding;
  private final String multiplier;
  private final int exponentStep;
  private final IExpr exponentFunction;
  private final IExpr numberFormat;
  private final int thresholdLow;
  private final int thresholdHigh;

  private NumberFormatter(FormKind kind, int totalDigits, int fractionDigits, OptionArgs options) {
    this.kind = kind;
    this.totalDigits = totalDigits;
    this.fractionDigits = fractionDigits;

    IExpr printPrecision = option(options, S.DefaultPrintPrecision, S.Automatic);
    int precision = printPrecision == S.Automatic ? -1 : printPrecision.toIntDefault();
    this.defaultPrintPrecision = precision > 0 ? precision : -1;

    IExpr digitBlock = option(options, S.DigitBlock, F.CInfinity);
    this.digitBlockLeft = digitBlockAt(digitBlock, 1);
    this.digitBlockRight = digitBlockAt(digitBlock, 2);

    IExpr separator = option(options, S.NumberSeparator, DEFAULT_SEPARATOR);
    this.separatorLeft = bothSidesString(separator, 1, ",");
    this.separatorRight = bothSidesString(separator, 2, "");

    IExpr padding = option(options, S.NumberPadding, defaultPadding(kind));
    this.paddingLeft = bothSidesString(padding, 1, "");
    this.paddingRight = bothSidesString(padding, 2, defaultPaddingRight(kind));

    IExpr point = option(options, S.NumberPoint, F.stringx("."));
    this.numberPoint = point == S.Automatic ? "." : asString(point, ".");

    IExpr signs = option(options, S.NumberSigns, defaultSigns(kind));
    IExpr negative = elementAt(signs, 1);
    if (negative.isList() && negative.size() == 3) {
      // AccountingForm style {{"(", ")"}, ""}
      this.signNegative = asString(negative.first(), "(");
      this.signNegativeClose = asString(negative.second(), ")");
    } else {
      this.signNegative = negative.isPresent() ? asString(negative, "-") : "-";
      this.signNegativeClose = "";
    }
    this.signPositive = stringAt(signs, 2, "");

    this.signPadding = option(options, S.SignPadding, S.False).isTrue();
    this.multiplier = asString(option(options, S.NumberMultiplier, F.stringx("×")), "×");

    int step = option(options, S.ExponentStep, F.ZZ(defaultExponentStep(kind))).toIntDefault();
    this.exponentStep = step < 1 ? 1 : step;
    this.exponentFunction = option(options, S.ExponentFunction, S.Automatic);
    this.numberFormat = option(options, S.NumberFormat, S.Automatic);

    IExpr threshold =
        option(options, S.ScientificNotationThreshold, F.list(F.ZZ(-5), F.ZZ(6)));
    this.thresholdLow = intAt(threshold, 1, -5);
    this.thresholdHigh = intAt(threshold, 2, 6);
  }

  /**
   * Build a formatter from a display wrapper such as
   * <code>ScientificForm(expr, 4, DigitBlock -&gt; 3)</code>.
   *
   * @param formAST the wrapper expression; its head selects the {@link FormKind}
   * @param engine the evaluation engine used to read the registered option defaults
   * @return a formatter, or <code>null</code> if the head is not one of the number form wrappers
   */
  public static NumberFormatter of(IAST formAST, EvalEngine engine) {
    FormKind kind = formKind(formAST.head());
    if (kind == null) {
      return null;
    }
    int totalDigits = -1;
    int fractionDigits = -1;
    int optionStart = 2;
    if (formAST.size() > 2) {
      IExpr arg2 = formAST.arg2();
      if (arg2.isList() && arg2.size() == 3) {
        totalDigits = arg2.first().toIntDefault();
        fractionDigits = arg2.second().toIntDefault();
        optionStart = 3;
      } else if (arg2.isInteger()) {
        totalDigits = arg2.toIntDefault();
        optionStart = 3;
      }
      if (totalDigits == Integer.MIN_VALUE) {
        totalDigits = -1;
      }
      if (fractionDigits == Integer.MIN_VALUE) {
        fractionDigits = -1;
      }
    }
    OptionArgs options =
        new OptionArgs((ISymbol) formAST.head(), formAST, optionStart, engine, true);
    return new NumberFormatter(kind, totalDigits, fractionDigits, options);
  }

  /**
   * Map a display wrapper head onto its {@link FormKind}.
   *
   * @return <code>null</code> if <code>head</code> is not a number form wrapper
   */
  public static FormKind formKind(IExpr head) {
    if (head.isBuiltInSymbol()) {
      switch (((ISymbol) head).ordinal()) {
        case ID.ScientificForm:
          return FormKind.SCIENTIFIC;
        case ID.EngineeringForm:
          return FormKind.ENGINEERING;
        case ID.NumberForm:
          return FormKind.NUMBER;
        case ID.AccountingForm:
          return FormKind.ACCOUNTING;
        case ID.PaddedForm:
          return FormKind.PADDED;
        case ID.DecimalForm:
          return FormKind.DECIMAL;
        default:
          return null;
      }
    }
    return null;
  }

  /** The <code>NumberMultiplier</code> string the factory should place before <code>10^e</code>. */
  public String getMultiplier() {
    return multiplier;
  }

  /**
   * Format a machine <code>double</code>.
   *
   * @param value the value to format
   * @param significantFigures the number of significant figures to use when the wrapper does not
   *        specify <code>n</code> itself
   */
  public FormattedNumber format(double value, int significantFigures) {
    if (Double.isNaN(value) || Double.isInfinite(value)) {
      return null;
    }
    return format(new BigDecimal(Double.toString(value)), significantDigits(significantFigures, -1),
        true);
  }

  /**
   * The number of significant digits to print: the wrapper's own <code>n</code> wins, then
   * <code>DefaultPrintPrecision</code>, then the value's own precision, then the factory's setting.
   */
  private int significantDigits(int significantFigures, int valuePrecision) {
    if (totalDigits > 0) {
      return totalDigits;
    }
    if (defaultPrintPrecision > 0) {
      return defaultPrintPrecision;
    }
    if (valuePrecision > 0) {
      return valuePrecision;
    }
    return significantFigures > 0 ? significantFigures : 6;
  }

  /** Format an arbitrary precision value. */
  public FormattedNumber format(Apfloat value, int significantFigures) {
    BigDecimal decimal;
    try {
      decimal = new BigDecimal(value.toString(true));
    } catch (NumberFormatException nfe) {
      return null;
    }
    int precision = (int) Math.min(value.precision(), Integer.MAX_VALUE);
    return format(decimal, significantDigits(significantFigures, precision), true);
  }

  /**
   * Format an exact integer. Integers never switch to scientific notation - only the digit block,
   * padding and sign options apply.
   */
  public FormattedNumber format(BigInteger value) {
    return format(new BigDecimal(value), totalDigits > 0 ? totalDigits : -1, false);
  }

  private FormattedNumber format(BigDecimal value, int significantFigures, boolean approximate) {
    boolean negative = value.signum() < 0;
    BigDecimal abs = value.abs();

    int scientificExponent = scientificExponent(abs, significantFigures);
    Integer exponent = determineExponent(abs, scientificExponent, approximate);
    int shift = exponent == null ? 0 : exponent.intValue();

    BigDecimal mantissa = shift == 0 ? abs : abs.movePointLeft(shift);
    if (fractionDigits >= 0) {
      mantissa = mantissa.setScale(fractionDigits, RoundingMode.HALF_UP);
      if (kind == FormKind.ACCOUNTING) {
        // an accounting column shows no trailing zeros - it fills the field behind the number
        mantissa = stripTrailingZeros(mantissa);
      }
    } else if (significantFigures > 0) {
      mantissa = mantissa.round(new MathContext(significantFigures, RoundingMode.HALF_UP));
      mantissa = stripTrailingZeros(mantissa);
    }

    String plain = mantissa.abs().toPlainString();
    int pointIndex = plain.indexOf('.');
    String integerPart = pointIndex < 0 ? plain : plain.substring(0, pointIndex);
    String fractionPart = pointIndex < 0 ? "" : plain.substring(pointIndex + 1);

    if (fractionDigits >= 0 && kind != FormKind.ACCOUNTING) {
      fractionPart = fitFraction(fractionPart, fractionDigits);
    }

    // group the digits before padding, so that the padding characters are never separated into
    // digit blocks themselves
    StringBuilder digits = new StringBuilder();
    digits.append(group(integerPart, digitBlockLeft, separatorLeft, true));
    if (fractionPart.length() > 0) {
      digits.append(numberPoint);
      digits.append(group(fractionPart, digitBlockRight, separatorRight, false));
    } else if (approximate) {
      // an approximate real always shows the decimal point: "2." not "2"
      digits.append(numberPoint);
    }

    String assembled = applySignsAndPadding(digits.toString(), integerPart.length(), negative);
    assembled = fillField(assembled);
    IExpr custom = applyNumberFormat(assembled, exponent);
    return new FormattedNumber(assembled, shift, exponent != null, custom);
  }

  /**
   * The exponent <code>e</code> with <code>value == d.ddd * 10^e</code>, computed after rounding to
   * <code>significantFigures</code> so that e.g. <code>9.99</code> at 2 digits reports
   * <code>e == 1</code>.
   */
  private static int scientificExponent(BigDecimal abs, int significantFigures) {
    if (abs.signum() == 0) {
      return 0;
    }
    BigDecimal rounded = significantFigures > 0
        ? abs.round(new MathContext(significantFigures, RoundingMode.HALF_UP))
        : abs;
    return rounded.precision() - rounded.scale() - 1;
  }

  /**
   * Apply <code>ExponentFunction</code> and <code>ExponentStep</code>.
   *
   * @return the exponent to factor out, or <code>null</code> to print without scientific notation
   */
  private Integer determineExponent(BigDecimal abs, int scientificExponent, boolean approximate) {
    if (!approximate || abs.signum() == 0) {
      // exact integers and zero never use scientific notation
      return null;
    }
    if (kind == FormKind.ACCOUNTING || kind == FormKind.DECIMAL) {
      // these two forms never use scientific notation, not even with an ExponentFunction
      return null;
    }
    if (exponentFunction != S.Automatic) {
      IExpr result = EvalEngine.get().evaluate(F.unaryAST1(exponentFunction, F.ZZ(scientificExponent)));
      if (result.isSymbol() && result == S.Null) {
        return null;
      }
      int value = result.toIntDefault();
      if (value == Integer.MIN_VALUE) {
        return null;
      }
      return Integer.valueOf(floorToStep(value));
    }

    switch (kind) {
      case NUMBER:
        if (scientificExponent >= -5 && scientificExponent <= 5) {
          return null;
        }
        break;
      case PADDED:
        if (scientificExponent >= thresholdLow && scientificExponent < thresholdHigh) {
          return null;
        }
        break;
      case SCIENTIFIC:
      case ENGINEERING:
      default:
        break;
    }
    int stepped = floorToStep(scientificExponent);
    return stepped == 0 ? null : Integer.valueOf(stepped);
  }

  private int floorToStep(int exponent) {
    return exponentStep <= 1 ? exponent : Math.floorDiv(exponent, exponentStep) * exponentStep;
  }

  /** Evaluate a user supplied <code>NumberFormat</code> function on the assembled parts. */
  private IExpr applyNumberFormat(String mantissa, Integer exponent) {
    if (numberFormat == S.Automatic) {
      return F.NIL;
    }
    IExpr exponentString = exponent == null ? F.stringx("") : F.stringx(exponent.toString());
    try {
      return EvalEngine.get().evaluate(
          F.ternaryAST3(numberFormat, F.stringx(mantissa), F.stringx("10"), exponentString));
    } catch (RuntimeException rex) {
      // a broken NumberFormat function must not break printing
      return F.NIL;
    }
  }

  /**
   * Apply <code>NumberSigns</code> and the left <code>NumberPadding</code>.
   *
   * <p>
   * The sign occupies one of the padded positions, so that a column of padded numbers lines up
   * whether or not an individual entry is negative. <code>SignPadding</code> decides which side of
   * the padding the sign ends up on: <code>False</code> (the default) pads first and writes the
   * sign directly in front of the digits, <code>True</code> writes the sign first and pads behind
   * it.
   *
   * @param digits the grouped digits, decimal point included
   * @param integerLength the number of integer digits before grouping, used to size the padding
   */
  private String applySignsAndPadding(String digits, int integerLength, boolean negative) {
    String sign = negative ? signNegative : signPositive;
    String close = negative ? signNegativeClose : "";

    StringBuilder padding = new StringBuilder();
    if (totalDigits > 0 && !paddingLeft.isEmpty() && kind != FormKind.ACCOUNTING) {
      // one position of the field belongs to the sign, whether or not this entry has one
      int target = (fractionDigits >= 0 ? totalDigits - fractionDigits : totalDigits) + 1;
      for (int i = integerLength + sign.length(); i < target; i++) {
        padding.append(paddingLeft);
      }
    }
    if (padding.length() == 0) {
      return sign + digits + close;
    }
    return signPadding ? sign + padding + digits + close : padding + sign + digits + close;
  }

  /**
   * Truncate or right-pad the fraction to exactly <code>digits</code> characters. An empty
   * <code>NumberPadding</code> means "do not pad" - the fraction then stops after its own digits.
   */
  private String fitFraction(String fractionPart, int digits) {
    if (fractionPart.length() >= digits) {
      return fractionPart.substring(0, digits);
    }
    if (paddingRight.isEmpty()) {
      return fractionPart;
    }
    StringBuilder sb = new StringBuilder(fractionPart);
    while (sb.length() < digits) {
      sb.append(paddingRight);
    }
    return sb.toString();
  }

  /**
   * Fill the field of an <code>AccountingForm</code> entry on the right. The closing sign of a
   * negative entry stays next to the digits - <code>AccountingForm(-12.3, {6, 2},
   * NumberPadding -&gt; {" ", "0"})</code> is <code>(12.3)0</code> - so that a column of entries
   * ends at the same position whether or not an individual entry is negative.
   */
  private String fillField(String assembled) {
    if (kind != FormKind.ACCOUNTING || totalDigits <= 0 || paddingRight.isEmpty()) {
      return assembled;
    }
    StringBuilder sb = new StringBuilder(assembled);
    // the field holds `totalDigits` digits and the number point
    while (sb.length() < totalDigits + 1) {
      sb.append(paddingRight);
    }
    return sb.toString();
  }

  /** Insert <code>NumberSeparator</code> strings every <code>blockSize</code> digits. */
  private static String group(String digits, int blockSize, String separator,
      boolean fromRight) {
    if (blockSize == NO_DIGIT_BLOCK || blockSize <= 0 || separator.isEmpty()
        || digits.length() <= blockSize) {
      return digits;
    }
    StringBuilder sb = new StringBuilder();
    if (fromRight) {
      int first = digits.length() % blockSize;
      if (first == 0) {
        first = blockSize;
      }
      sb.append(digits, 0, first);
      for (int i = first; i < digits.length(); i += blockSize) {
        sb.append(separator);
        sb.append(digits, i, i + blockSize);
      }
    } else {
      for (int i = 0; i < digits.length(); i += blockSize) {
        if (i > 0) {
          sb.append(separator);
        }
        sb.append(digits, i, Math.min(i + blockSize, digits.length()));
      }
    }
    return sb.toString();
  }

  private static BigDecimal stripTrailingZeros(BigDecimal value) {
    BigDecimal stripped = value.stripTrailingZeros();
    return stripped.scale() < 0 ? stripped.setScale(0) : stripped;
  }

  private static IExpr option(OptionArgs options, ISymbol symbol, IExpr defaultValue) {
    IExpr value = options.getOption(symbol);
    return value.isPresent() ? value : defaultValue;
  }

  private static IExpr defaultPadding(FormKind kind) {
    switch (kind) {
      case PADDED:
        return F.list(F.stringx(" "), F.stringx("0"));
      case NUMBER:
      case DECIMAL:
        return F.list(F.stringx(""), F.stringx("0"));
      default:
        return F.list(F.stringx(""), F.stringx(""));
    }
  }

  private static String defaultPaddingRight(FormKind kind) {
    return kind == FormKind.PADDED || kind == FormKind.NUMBER || kind == FormKind.DECIMAL ? "0" : "";
  }

  private static IExpr defaultSigns(FormKind kind) {
    if (kind == FormKind.ACCOUNTING) {
      return F.list(F.list(F.stringx("("), F.stringx(")")), F.stringx(""));
    }
    return F.list(F.stringx("-"), F.stringx(""));
  }

  private static int defaultExponentStep(FormKind kind) {
    return kind == FormKind.ENGINEERING ? 3 : 1;
  }

  private static IExpr elementAt(IExpr expr, int index) {
    if (expr.isList() && index < expr.size()) {
      return expr.getAt(index);
    }
    return index == 1 ? expr : F.NIL;
  }

  /**
   * Read one side of an option which may be given either as a pair or as a single value that
   * applies to both sides, such as <code>NumberSeparator -&gt; "'"</code>.
   */
  private static String bothSidesString(IExpr expr, int index, String defaultValue) {
    return stringAt(expr.isList() ? expr : F.list(expr, expr), index, defaultValue);
  }

  private static String stringAt(IExpr expr, int index, String defaultValue) {
    IExpr element = elementAt(expr, index);
    return element.isPresent() ? asString(element, defaultValue) : defaultValue;
  }

  private static int intAt(IExpr expr, int index, int defaultValue) {
    IExpr element = elementAt(expr, index);
    if (element.isPresent()) {
      int value = element.toIntDefault();
      if (value != Integer.MIN_VALUE) {
        return value;
      }
    }
    return defaultValue;
  }

  private static int digitBlockAt(IExpr expr, int index) {
    IExpr element = elementAt(expr.isList() ? expr : F.list(expr, expr), index);
    if (element.isPresent() && !element.isInfinity()) {
      int value = element.toIntDefault();
      if (value > 0) {
        return value;
      }
    }
    return NO_DIGIT_BLOCK;
  }

  private static String asString(IExpr expr, String defaultValue) {
    if (expr.isString()) {
      return expr.toString();
    }
    return expr.isPresent() ? expr.toString() : defaultValue;
  }
}

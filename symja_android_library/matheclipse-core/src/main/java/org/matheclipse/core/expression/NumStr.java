package org.matheclipse.core.expression;

import org.apfloat.Apfloat;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInexactNumber;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.parser.client.ParserConfig;

public final class NumStr extends Num {
  /** */
  private static final long serialVersionUID = -6378124858265275437L;

  private String fFloatStr;
  private long fPrecision;
  private int fExponent;

  /**
   * 
   * Constructs a {@link NumStr} from a string representation of a floating-point number and an
   * optional exponent. The constructor parses the input string to extract the base and exponent,
   * calculates the numeric value, and determines the precision.
   * 
   * @param floatStr the string representation of the floating-point number
   */
  public NumStr(String floatStr) {
    this(floatStr, 0);
  }

  /**
   * 
   * Constructs a {@link NumStr} from a string representation of a floating-point number and an
   * optional exponent. The constructor parses the input string to extract the base and exponent,
   * calculates the numeric value, and determines the precision.
   * 
   * @param floatStr the string representation of the floating-point number
   * @param exponent the optional exponent to adjust the numeric value (overridden if the string
   *        contains an explicit exponent)
   */
  public NumStr(String floatStr, int exponent) {
    int index = floatStr.indexOf("*^");
    fExponent = exponent;
    fFloatStr = floatStr;
    if (index > 0) {
      fFloatStr = floatStr.substring(0, index);
      fExponent = Integer.parseInt(floatStr.substring(index + 2));
    }

    if (fExponent != 0) {
      this.value = Double.parseDouble(fFloatStr + "E" + fExponent);
    } else {
      this.value = Double.parseDouble(fFloatStr);
    }

    // the significant digits: 0.000123 has 3, leading zeros don't count
    fPrecision = 0;
    boolean significant = false;
    for (int i = 0; i < fFloatStr.length(); i++) {
      final char c = fFloatStr.charAt(i);
      if (c >= '1' && c <= '9') {
        significant = true;
      }
      if (significant && c >= '0' && c <= '9') {
        fPrecision++;
      }
    }
    if (fPrecision < ParserConfig.MACHINE_PRECISION) {
      fPrecision = ParserConfig.MACHINE_PRECISION;
    }
  }

  /**
   * This literal as an arbitrary precision number of its precision: the value of a literal like
   * <code>1.*^400</code>, which is beyond the double range.
   */
  public ApfloatNum promoted() {
    return ApfloatNum.promotedLiteral(fFloatStr, fExponent, fPrecision);
  }

  @Override
  public INum abs() {
    if (apfloatDigits()) {
      return apfloatNumValue().abs();
    }
    return super.abs();
  }

  @Override
  public IInexactNumber acos() {
    if (apfloatDigits()) {
      return apfloatNumValue().acos();
    }
    return super.acos();
  }

  /**
   * Whether the literal was written with more digits than a machine number has. A literal with no
   * more digits is a machine number, also inside <code>N(expr, p)</code>: <code>N(1.5, 30)</code>
   * is the machine number <code>1.5</code>. A longer literal has the precision of its digits.
   */
  private boolean hasDigits() {
    return fPrecision > ParserConfig.MACHINE_PRECISION;
  }

  /** Arbitrary precision mode and a literal of more digits than a machine number. */
  private boolean apfloatDigits() {
    return hasDigits() && EvalEngine.isApfloatMode();
  }

  @Override
  public ApfloatNum apfloatNumValue() {
    // the written digits, with the precision of a machine number for a short literal
    final long precision = fPrecision;
    if (fExponent == 0) {
      return ApfloatNum.valueOf(fFloatStr, precision);
    }
    return ApfloatNum.valueOf(fFloatStr + "E" + fExponent, precision);
  }

  @Override
  public INum numericValue() {
    if (fPrecision > ParserConfig.MACHINE_PRECISION) {
      if (fExponent == 0) {
        return ApfloatNum.valueOf(fFloatStr, fPrecision);
      }
      return ApfloatNum.valueOf(fFloatStr + "E" + fExponent, fPrecision);
    }
    return new Num(doubleValue());
  }

  @Override
  public Apfloat apfloatValue() {
    final long precision = fPrecision;
    if (fExponent == 0) {
      return new Apfloat(fFloatStr, precision);
    }
    return new Apfloat(fFloatStr + "E" + fExponent, precision);
  }

  @Override
  public IInexactNumber asin() {
    if (apfloatDigits()) {
      return apfloatNumValue().asin();
    }
    return super.asin();
  }

  @Override
  public IInexactNumber atan() {
    if (apfloatDigits()) {
      return apfloatNumValue().atan();
    }
    return super.atan();
  }

  @Override
  public INum cos() {
    if (apfloatDigits()) {
      return apfloatNumValue().cos();
    }
    return super.cos();
  }

  @Override
  public INum cosh() {
    if (apfloatDigits()) {
      return apfloatNumValue().cosh();
    }
    return super.cosh();
  }

  /** {@inheritDoc} */
  @Override
  public long determinePrecision(boolean postParserProcessing) {
    return precision();
  }

  @Override
  public IExpr evaluate(EvalEngine engine) {
    if (engine.isNumericMode() && engine.isArbitraryMode() && hasDigits()) {
      // the digits of the literal, no more than the precision asked for
      final long precision = Math.min(fPrecision, engine.getNumericPrecision());
      if (fExponent == 0) {
        return ApfloatNum.valueOf(fFloatStr, precision);
      }
      return ApfloatNum.valueOf(fFloatStr + "E" + fExponent, precision);
      // } else {
      // if (fPrecision > ParserConfig.MACHINE_PRECISION) {
      // if (fExponent == 0) {
      // return ApfloatNum.valueOf(fFloatStr, fPrecision);
      // }
      // return ApfloatNum.valueOf(fFloatStr + "E" + fExponent, fPrecision);
      // }
    }
    return super.evaluate(engine);
  }

  @Override
  public INumber evaluatePrecision(EvalEngine engine) {
    if (engine.isArbitraryMode() && hasDigits()) {
      final long precision = Math.min(fPrecision, engine.getNumericPrecision());
      // long precision = fPrecision;
      // if (engine.isApfloat()) {
      // precision = fPrecision < engine.getNumericPrecision() ? fPrecision :
      // engine.getNumericPrecision();
      // }
      // engine.setNumericPrecision(precision);
      if (fExponent == 0) {
        return ApfloatNum.valueOf(fFloatStr, precision);
      }
      return ApfloatNum.valueOf(fFloatStr + "E" + fExponent, precision);
    }
    return super.evaluatePrecision(engine);
  }

  @Override
  public INum exp() {
    if (apfloatDigits()) {
      return apfloatNumValue().exp();
    }
    return super.exp();
  }

  @Override
  public int getExponent() {
    return fExponent;
  }

  public String getFloatStr() {
    return fFloatStr;
  }

  @Override
  public IInexactNumber log() {
    if (apfloatDigits()) {
      if (isNegative()) {
        return apcomplexNumValue().log();
      }
      return apfloatNumValue().log();
    }
    return super.log();
  }

  @Override
  public Num negate() {
    if (fFloatStr.length() > 0 && fFloatStr.charAt(0) == '-') {
      return new NumStr(fFloatStr.substring(1), fExponent);
    }
    return new NumStr("-" + fFloatStr, fExponent);
  }

  @Override
  public IExpr plus(final IExpr that) {
    if (apfloatDigits()) {
      if (that instanceof ApfloatNum) {
        ApfloatNum arg2 = (ApfloatNum) that;
        return apfloatNumValue().add(arg2.apfloatNumValue());
      }
      if (that instanceof Num) {
        return apfloatNumValue().add(((INum) that).apfloatNumValue());
      }
      if (that instanceof ApcomplexNum) {
        return ApcomplexNum.valueOf(apfloatValue()).add((ApcomplexNum) that);
      }
      if (that instanceof ComplexNum) {
        return ApcomplexNum.valueOf(apfloatValue()).add(((ComplexNum) that).apcomplexNumValue());
      }
    }
    return super.plus(that);
  }

  @Override
  public long precision() {
    return fPrecision;
  }

  @Override
  public INum sin() {
    if (apfloatDigits()) {
      return apfloatNumValue().sin();
    }
    return super.sin();
  }

  @Override
  public INum sinh() {
    if (apfloatDigits()) {
      return apfloatNumValue().sinh();
    }
    return super.sinh();
  }

  @Override
  public IExpr sqr() {
    if (apfloatDigits()) {
      return apfloatNumValue().sqr();
    }
    return super.sqr();
  }

  @Override
  public IExpr sqrt() {
    if (apfloatDigits()) {
      if (value < 0.0) {
        return apcomplexNumValue().sqrt();
      }
      return apfloatNumValue().sqrt();
    }
    return super.sqrt();
  }

  @Override
  public INum tan() {
    if (apfloatDigits()) {
      return apfloatNumValue().tan();
    }
    return super.tan();
  }

  @Override
  public INum tanh() {
    if (apfloatDigits()) {
      return apfloatNumValue().tanh();
    }
    return super.tanh();
  }

  @Override
  public IExpr times(final IExpr that) {
    if (apfloatDigits()) {
      if (that instanceof ApfloatNum) {
        ApfloatNum arg2 = (ApfloatNum) that;
        return apfloatNumValue().multiply(arg2.apfloatNumValue());
      }
      if (that instanceof Num) {
        return apfloatNumValue().multiply(((INum) that).apfloatNumValue());
      }
      if (that instanceof ApcomplexNum) {
        return ApcomplexNum.valueOf(apfloatValue()).multiply((ApcomplexNum) that);
      }
      if (that instanceof ComplexNum) {
        return ApcomplexNum.valueOf(apfloatValue())
            .multiply(((ComplexNum) that).apcomplexNumValue());
      }
    }
    return super.times(that);
  }
}

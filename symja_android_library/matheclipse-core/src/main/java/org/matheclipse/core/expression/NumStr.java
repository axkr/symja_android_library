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

    fPrecision = fFloatStr.length();
    if (fFloatStr.startsWith("0.")) {
      fPrecision -= 2;
    } else if (fFloatStr.indexOf(".") > 0) {
      fPrecision--;
    }
    if (fPrecision < ParserConfig.MACHINE_PRECISION) {
      fPrecision = ParserConfig.MACHINE_PRECISION;
    }
  }

  @Override
  public INum abs() {
    if (EvalEngine.isApfloatMode()) {
      return apfloatNumValue().abs();
    }
    return super.abs();
  }

  @Override
  public IInexactNumber acos() {
    if (EvalEngine.isApfloatMode()) {
      return apfloatNumValue().acos();
    }
    return super.acos();
  }

  @Override
  public ApfloatNum apfloatNumValue() {
    long precision = EvalEngine.getApfloat().precision();
    precision = (fPrecision > precision) ? fPrecision : precision;
    if (fExponent == 0) {
      return ApfloatNum.valueOf(fFloatStr, precision);
    }
    return ApfloatNum.valueOf(fFloatStr + "E" + fExponent, precision);
  }

  @Override
  public Apfloat apfloatValue() {
    long precision = EvalEngine.getApfloat().precision();
    precision = (fPrecision > precision) ? fPrecision : precision;
    if (fExponent == 0) {
      return new Apfloat(fFloatStr, precision);
    }
    return new Apfloat(fFloatStr + "E" + fExponent, precision);
  }

  @Override
  public IInexactNumber asin() {
    if (EvalEngine.isApfloatMode()) {
      return apfloatNumValue().asin();
    }
    return super.asin();
  }

  @Override
  public IInexactNumber atan() {
    if (EvalEngine.isApfloatMode()) {
      return apfloatNumValue().atan();
    }
    return super.atan();
  }

  @Override
  public INum cos() {
    if (EvalEngine.isApfloatMode()) {
      return apfloatNumValue().cos();
    }
    return super.cos();
  }

  @Override
  public INum cosh() {
    if (EvalEngine.isApfloatMode()) {
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
    if (engine.isNumericMode() && engine.isArbitraryMode()) {
      if (fExponent == 0) {
        return ApfloatNum.valueOf(fFloatStr, engine.getNumericPrecision());
      }
      return ApfloatNum.valueOf(fFloatStr + "E" + fExponent, engine.getNumericPrecision());
    }
    return super.evaluate(engine);
  }

  @Override
  public INumber evaluatePrecision(EvalEngine engine) {
    if (engine.isArbitraryMode()) {
      long precision =
          fPrecision < engine.getNumericPrecision() ? engine.getNumericPrecision() : fPrecision;
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
    if (EvalEngine.isApfloatMode()) {
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
    if (EvalEngine.isApfloatMode()) {
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
    if (EvalEngine.isApfloatMode()) {
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
    if (EvalEngine.isApfloatMode()) {
      return apfloatNumValue().sin();
    }
    return super.sin();
  }

  @Override
  public INum sinh() {
    if (EvalEngine.isApfloatMode()) {
      return apfloatNumValue().sinh();
    }
    return super.sinh();
  }

  @Override
  public IExpr sqr() {
    if (EvalEngine.isApfloatMode()) {
      return apfloatNumValue().sqr();
    }
    return super.sqr();
  }

  @Override
  public IExpr sqrt() {
    if (EvalEngine.isApfloatMode()) {
      if (value < 0.0) {
        return apcomplexNumValue().sqrt();
      }
      return apfloatNumValue().sqrt();
    }
    return super.sqrt();
  }

  @Override
  public INum tan() {
    if (EvalEngine.isApfloatMode()) {
      return apfloatNumValue().tan();
    }
    return super.tan();
  }

  @Override
  public INum tanh() {
    if (EvalEngine.isApfloatMode()) {
      return apfloatNumValue().tanh();
    }
    return super.tanh();
  }

  @Override
  public IExpr times(final IExpr that) {
    if (EvalEngine.isApfloatMode()) {
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

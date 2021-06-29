package org.matheclipse.core.expression;

import java.math.RoundingMode;
import java.util.function.Function;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatRuntimeException;
import org.hipparchus.util.MathUtils;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.form.DoubleToMMA;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;
import org.matheclipse.parser.client.FEConfig;

import com.google.common.math.DoubleMath;

/**
 * <code>INum</code> implementation which wraps a <code>double</code> value to represent a numeric
 * floating-point number.
 */
public class Num implements INum {
  /** */
  private static final long serialVersionUID = 188084692735007429L;

  /**
   * Returns a {@code Num} instance representing the specified {@code double} value. If a new {@code
   * Num} instance is not required, this method should generally be used in preference to the
   * constructor {@link #Num(double)}, as this method is likely to yield significantly better space
   * and time performance by caching frequently requested values.
   *
   * @param d a double value.
   * @return a {@code Double} instance representing {@code d}.
   */
  public static Num valueOf(final double d) {
    if (d >= (-1.1) && d <= 1.1) {
      try {
        int i = DoubleMath.roundToInt(d, RoundingMode.UNNECESSARY);
        if (i >= (-1) && i <= 1) {
          switch (i) {
            case -1:
              if (d == (-1.0d)) {
                return F.CND1;
              }
              break;
            case 0:
              if (d == 0.0d || d == -0.0d) {
                return F.CD0;
              }
              break;
            case 1:
              if (d == 1.0d) {
                return F.CD1;
              }
              break;
          }
        }
      } catch (ArithmeticException ae) {
        //
      }
    }
    return new Num(d);
  }

  /**
   * @param chars
   * @return
   */
  public static double valueOf(final String chars) {
    return Double.parseDouble(chars);
  }

  double fDouble;

  protected Num() {
    fDouble = 0.0;
  }

  /* package private */ Num(final double value) {
    fDouble = value;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr accept(IVisitor visitor) {
    return visitor.visit(this);
  }

  /** {@inheritDoc} */
  @Override
  public boolean accept(IVisitorBoolean visitor) {
    return visitor.visit(this);
  }

  /** {@inheritDoc} */
  @Override
  public int accept(IVisitorInt visitor) {
    return visitor.visit(this);
  }

  /** {@inheritDoc} */
  @Override
  public long accept(IVisitorLong visitor) {
    return visitor.visit(this);
  }

  @Override
  public INum add(final INum val) {
    // if (val instanceof ApfloatNum) {
    // return ApfloatNum.valueOf(fDouble, ((ApfloatNum) val).precision()).add(val);
    // }
    if (val instanceof ApfloatNum) {
      Apfloat arg2 = ((ApfloatNum) val).apfloatValue();
      return F.num(arg2.add(apfloatValue()));
    }

    return valueOf(fDouble + val.getRealPart());
  }

  @Override
  public ApcomplexNum apcomplexNumValue() {
    return ApcomplexNum.valueOf(apcomplexValue());
  }

  public Apcomplex apcomplexValue() {
    return new Apcomplex(new Apfloat(fDouble));
  }

  @Override
  public ApfloatNum apfloatNumValue() {
    return ApfloatNum.valueOf(fDouble);
  }

  @Override
  public Apfloat apfloatValue() {
    return new Apfloat(fDouble);
  }

  /** {@inheritDoc} */
  @Override
  public IInteger ceilFraction() {
    try {
      return F.ZZ(NumberUtil.toLong(Math.ceil(fDouble)));
    } catch (ArithmeticException ae) {
      ArgumentTypeException.throwArg(this, F.Ceiling(this));
    }
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public int compareAbsValueToOne() {
    double temp = Math.abs(fDouble);
    return Double.compare(temp, 1.0);
  }

  /**
   * @param that
   * @return
   */
  public int compareTo(final double that) {
    return Double.compare(fDouble, that);
  }

  /**
   * Compares this expression with the specified expression for order. Returns a negative integer,
   * zero, or a positive integer as this expression is canonical less than, equal to, or greater
   * than the specified expression.
   */
  @Override
  public int compareTo(final IExpr expr) {
    if (expr instanceof Num) {
      return Double.compare(fDouble, ((Num) expr).fDouble);
    }
    if (expr.isNumber()) {
      if (expr.isReal()) {
        return Double.compare(fDouble, ((ISignedNumber) expr).doubleValue());
      }
      int c = this.compareTo(((INumber) expr).re());
      if (c != 0) {
        return c;
      }
    }
    return -1; // INum.super.compareTo(expr);
  }

  @Override
  public ComplexNum complexNumValue() {
    // double precision complex number
    return ComplexNum.valueOf(doubleValue(), 0.0);
  }

  @Override
  public IExpr copy() {
    try {
      return (IExpr) clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
      return null;
    }
  }

  //  /**
  //   * @param that
  //   * @return
  //   */
  //  public double divide(final double that) {
  //    return fDouble / that;
  //  }

  @Override
  public ISignedNumber divideBy(ISignedNumber that) {
    if (that instanceof Num) {
      return valueOf(fDouble / ((Num) that).fDouble);
    }
    if (that instanceof ApfloatNum) {
      return (ISignedNumber) apfloatNumValue().divide(that.apfloatNumValue());
    }
    return valueOf(fDouble / that.doubleValue());
  }

  /** @return */
  @Override
  public double doubleValue() {
    return fDouble;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr dec() {
    return valueOf(fDouble - 1.0);
  }

  /** {@inheritDoc} */
  @Override
  public IExpr inc() {
    return valueOf(fDouble + 1.0);
  }

  /** {@inheritDoc} */
  @Override
  public Num abs() {
    return valueOf(Math.abs(fDouble));
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof Num) {
      final Num c = (Num) other;
      if (Double.isNaN(c.fDouble)) {
        return Double.isNaN(fDouble);
      } else {
        return Double.doubleToLongBits(fDouble) //
            == Double.doubleToLongBits(c.fDouble);
      }
    }
    return false;
  }

  @Override
  public boolean equalsInt(final int i) {
    return F.isNumIntValue(fDouble, i);
  }

  @Override
  public IExpr evaluate(EvalEngine engine) {
    if (fDouble == Double.POSITIVE_INFINITY) {
      return F.CInfinity;
    }
    if (fDouble == Double.NEGATIVE_INFINITY) {
      return F.CNInfinity;
    }
    if (Double.isNaN(fDouble)) {
      return S.Indeterminate;
    }
    if (engine.isNumericMode() && engine.isArbitraryMode()) {
      return ApfloatNum.valueOf(fDouble);
    }
    return F.NIL;
  }

  @Override
  public INumber evaluatePrecision(EvalEngine engine) {
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public double evalDouble() {
    return fDouble;
  }

  @Override
  public ISignedNumber evalReal() {
    return this;
  }

  @Override
  public INumber evalNumber() {
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public ISignedNumber fractionalPart() {
    return F.num(getRealPart() % 1);
  }

  /** {@inheritDoc} */
  @Override
  public String fullFormString() {
    return fullFormString(fDouble);
  }

  public static String fullFormString(double d) {
    String result = Double.toString(d);
    if (!FEConfig.EXPLICIT_TIMES_OPERATOR) {
      int indx = result.indexOf("E");
      if (indx > 0) {
        result = result.replace("E", "`*^");
      } else {
        result = result + "`";
      }
    }
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public IInteger integerPart() {
    return isNegative() ? ceilFraction() : floorFraction();
  }

  /** {@inheritDoc} */
  @Override
  public IInteger floorFraction() {
    try {
      return F.ZZ(NumberUtil.toLong(Math.floor(fDouble)));
    } catch (ArithmeticException ae) {
      ArgumentTypeException.throwArg(this, F.Floor(this));
    }
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public ISignedNumber im() {
    return F.CD0;
  }

  @Override
  public double imDoubleValue() {
    return 0.0;
  }

  /** {@inheritDoc} */
  @Override
  public ISignedNumber re() {
    return this;
  }

  @Override
  public double reDoubleValue() {
    return doubleValue();
  }

  @Override
  public double getRealPart() {
    double temp = fDouble;
    if (temp == (-0.0)) {
      temp = 0.0;
    }
    return temp;
  }

  @Override
  public final int hashCode() {
    if (Double.isNaN(fDouble)) {
      return 11;
    }
    return 37 * 17 * MathUtils.hash(fDouble);
    // return Double.hashCode(fDouble);
  }

  @Override
  public ISymbol head() {
    return S.Real;
  }

  @Override
  public int hierarchy() {
    return DOUBLEID;
  }

  @Override
  public String internalFormString(boolean symbolsAsFactoryMethod, int depth) {
    return internalJavaString(symbolsAsFactoryMethod, depth, false, false, false, F.CNullFunction);
  }

  @Override
  public String internalJavaString(
      boolean symbolsAsFactoryMethod,
      int depth,
      boolean useOperators,
      boolean usePrefix,
      boolean noSymbolPrefix,
      Function<IExpr, String> variables) {
    String prefix = usePrefix ? "F." : "";
    if (isZero()) {
      return prefix + "CD0";
    } else if (isOne()) {
      return prefix + "CD1";
    } else if (isMinusOne()) {
      return prefix + "CND1";
    }
    return prefix + "num(" + fDouble + ")";
  }

  @Override
  public String internalScalaString(boolean symbolsAsFactoryMethod, int depth) {
    return internalJavaString(symbolsAsFactoryMethod, depth, true, false, false, F.CNullFunction);
  }

  /** @return */
  @Override
  public int intValue() {
    return (int) fDouble;
  }

  @Override
  public ISignedNumber inverse() {
    if (isOne()) {
      return this;
    }
    return valueOf(1 / fDouble);
  }

  /** {@inheritDoc} */
  @Override
  public long determinePrecision() {
    return FEConfig.MACHINE_PRECISION;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isE() {
    return F.isZero(fDouble - Math.E);
  }

  @Override
  public boolean isGT(ISignedNumber that) {
    return fDouble > that.doubleValue();
  }

  /** @return */
  public boolean isInfinite() {
    return Double.isInfinite(fDouble);
  }

  @Override
  public boolean isLT(ISignedNumber that) {
    return fDouble < that.doubleValue();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isMinusOne() {
    return F.isZero(fDouble + 1.0);
  }

  /** @return */
  public boolean isNaN() {
    return Double.isNaN(fDouble);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNegative() {
    return fDouble < 0.0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNumEqualInteger(IInteger value) throws ArithmeticException {
    return F.isNumEqualInteger(fDouble, value);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNumEqualRational(IRational value) throws ArithmeticException {
    return F.isNumEqualRational(fDouble, value);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNumIntValue() {
    return F.isNumIntValue(fDouble);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isOne() {
    return F.isZero(fDouble - 1.0);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPi() {
    return F.isZero(fDouble - Math.PI);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPositive() {
    return fDouble > 0.0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isRationalValue(IRational value) {
    return F.isZero(fDouble - value.doubleValue());
  }

  @Override
  public boolean isSame(IExpr expression, double epsilon) {
    if (expression instanceof Num) {
      return F.isZero(fDouble - ((Num) expression).fDouble, epsilon);
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isZero() {
    return F.isZero(fDouble, Config.DOUBLE_TOLERANCE);
  }

  @Override
  public long leafCountSimplify() {
    return 2;
  }

  /** @return */
  public long longValue() {
    return (long) fDouble;
  }

  /**
   * @param that
   * @return
   */
  public double minus(final double that) {
    return fDouble - that;
  }

  @Override
  public INum multiply(final INum val) {
    if (val instanceof ApfloatNum) {
      return ApfloatNum.valueOf(fDouble).multiply(val);
    }
    return valueOf(fDouble * val.getRealPart());
  }

  /** @return */
  @Override
  public ISignedNumber negate() {
    return valueOf(-fDouble);
  }

  @Override
  public Num numValue() {
    return this;
  }

  /** @return */
  @Override
  public ISignedNumber opposite() {
    return valueOf(-fDouble);
  }

  /**
   * @param that
   * @return
   */
  public double plus(final double that) {
    return fDouble + that;
  }

  @Override
  public IExpr plus(final IExpr that) {
    if (that instanceof Num) {
      return valueOf(fDouble + ((Num) that).fDouble);
    }
    if (that instanceof IComplexNum) {
      if (that instanceof ApcomplexNum) {
        return apcomplexNumValue().add(((ApcomplexNum) that).apcomplexNumValue());
      }
      return ComplexNum.valueOf(fDouble).add((ComplexNum) that);
    }
    if (that instanceof ApfloatNum) {
      return apfloatNumValue().add(((ApfloatNum) that).apfloatNumValue());
    }
    return INum.super.plus(that);
  }

  @Override
  public INum pow(final INum val) {
    return valueOf(Math.pow(fDouble, val.getRealPart()));
  }

  @Override
  public long precision() throws ApfloatRuntimeException {
    return 15L;
  }

  @Override
  public IInteger roundExpr() {
    return F.ZZ(DoubleMath.roundToBigInteger(fDouble, RoundingMode.HALF_EVEN));
  }

  @Override
  public ISignedNumber roundClosest(ISignedNumber multiple) {
    if (multiple.isRational()) {
      return F.ZZ(
              DoubleMath.roundToBigInteger(
                  fDouble / multiple.doubleValue(), RoundingMode.HALF_EVEN))
          .multiply((IRational) multiple);
    }
    double factor = multiple.doubleValue();
    return F.num(
        DoubleMath.roundToBigInteger(fDouble / factor, RoundingMode.HALF_EVEN).doubleValue()
            * factor);
  }

  @Override
  public int complexSign() {
    return (int) Math.signum(fDouble);
  }

  /** @return */
  // public double sqrt() {
  // return Math.sqrt(fDouble);
  // }

  @Override
  public IExpr sqrt() {
    return valueOf(Math.sqrt(fDouble));
  }

  @Override
  public ISignedNumber subtractFrom(ISignedNumber that) {
    if (that instanceof Num) {
      return valueOf(fDouble - ((Num) that).fDouble);
    }
    if (that instanceof ApfloatNum) {
      return (ISignedNumber) apfloatNumValue().subtract(that.apfloatNumValue());
    }
    return valueOf(doubleValue() - that.doubleValue());
  }

  @Override
  public IExpr times(final IExpr that) {
    if (that instanceof Num) {
      return valueOf(fDouble * ((Num) that).fDouble);
    }
    if (that instanceof IComplexNum) {
      if (that instanceof ApcomplexNum) {
        return apcomplexNumValue().multiply(((ApcomplexNum) that).apcomplexNumValue());
      }
      return ComplexNum.valueOf(fDouble).multiply((ComplexNum) that);
    }
    if (that instanceof ApfloatNum) {
      return apfloatNumValue().multiply(((ApfloatNum) that).apfloatNumValue());
    }

    return INum.super.times(that);
  }

  /** {@inheritDoc} */
  @Override
  public int toInt() throws ArithmeticException {
    return NumberUtil.toInt(fDouble);
  }

  /** {@inheritDoc} */
  @Override
  public int toIntDefault(int defaultValue) {
    try {
      return NumberUtil.toInt(fDouble);
    } catch (ArithmeticException ae) {
      return defaultValue;
    }
  }

  /** {@inheritDoc} */
  @Override
  public long toLongDefault(long defaultValue) {
    try {
      return NumberUtil.toLong(fDouble);
    } catch (ArithmeticException ae) {
      return defaultValue;
    }
  }

  /** {@inheritDoc} */
  @Override
  public long toLong() throws ArithmeticException {
    return NumberUtil.toLong(fDouble);
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    if (FEConfig.EXPLICIT_TIMES_OPERATOR) {
      return Double.toString(fDouble);
    }
    StringBuilder buf = new StringBuilder();
    DoubleToMMA.doubleToMMA(buf, fDouble, 5, 7);
    return buf.toString();
  }

  @Override
  public IExpr ulp() {
    return valueOf(Math.ulp(fDouble));
  }

  @Override
  public IExpr cos() {
    return valueOf(Math.cos(fDouble));
  }

  @Override
  public IExpr cosh() {
    return valueOf(Math.cosh(fDouble));
  }

  @Override
  public IExpr exp() {
    return valueOf(Math.exp(fDouble));
  }

  @Override
  public IExpr log() {
    return valueOf(Math.log(fDouble));
  }

  @Override
  public IExpr pow(int n) {
    return valueOf(Math.pow(fDouble, n));
  }

  @Override
  public IExpr rootN(int n) {
    return valueOf(Math.pow(fDouble, 1.0 / n));
  }

  @Override
  public IExpr sign() {
    if (isNaN() || isZero()) {
      return this;
    }
    return valueOf(Math.abs(fDouble));
  }

  @Override
  public IExpr sin() {
    return valueOf(Math.sin(fDouble));
  }

  @Override
  public IExpr sinh() {
    return valueOf(Math.sinh(fDouble));
  }

  @Override
  public IExpr tan() {
    return valueOf(Math.tan(fDouble));
  }

  @Override
  public IExpr tanh() {
    return valueOf(Math.tanh(fDouble));
  }

  @Override
  public IExpr getPi() {
    return F.num(Math.PI);
  }

  @Override
  public IExpr toDegrees() {
    // radians * (180 / Pi)
    return valueOf(fDouble * 180.0 / Math.PI);
  }

  @Override
  public IExpr toRadians() {
    // degrees * (Pi / 180)
    return valueOf(fDouble * Math.PI / 180.0);
  }
}

package org.matheclipse.core.expression;

import java.math.RoundingMode;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.matheclipse.parser.client.ParserConfig;
import com.google.common.math.DoubleMath;

/**
 * <code>INum</code> implementation which wraps a <code>double</code> value to represent a numeric
 * floating-point number.
 */
public class Num implements INum {
  /** */
  private static final long serialVersionUID = 188084692735007429L;

  private static final Logger LOGGER = LogManager.getLogger();

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
      int i = (int) d;
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
    if (val instanceof ApfloatNum) {
      Apfloat arg2 = ((ApfloatNum) val).apfloatValue();
      return F.num(EvalEngine.getApfloat().add(apfloatValue(), arg2));
    }
    return valueOf(fDouble + val.getRealPart());
  }

  @Override
  public INum subtract(final INum val) {
    if (val instanceof ApfloatNum) {
      Apfloat arg2 = ((ApfloatNum) val).apfloatValue();
      return F.num(EvalEngine.getApfloat().subtract(apfloatValue(), arg2));
    }
    return valueOf(fDouble - val.getRealPart());
  }

  @Override
  public INum divide(final INum val) {
    if (val instanceof ApfloatNum) {
      Apfloat arg2 = ((ApfloatNum) val).apfloatValue();
      return F.num(EvalEngine.getApfloat().divide(apfloatValue(), arg2));
    }
    return valueOf(fDouble / val.getRealPart());
  }

  @Override
  public ApcomplexNum apcomplexNumValue() {
    return ApcomplexNum.valueOf(apcomplexValue());
  }

  @Override
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
      LOGGER.error("Num.copy() failed", e);
      return null;
    }
  }

  @Override
  public ISignedNumber divideBy(ISignedNumber that) {
    if (that instanceof Num) {
      return valueOf(fDouble / ((Num) that).fDouble);
    }
    if (that instanceof ApfloatNum) {
      return apfloatNumValue().divide(that.apfloatNumValue());
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
    if (!ParserConfig.EXPLICIT_TIMES_OPERATOR) {
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
  public CharSequence internalFormString(boolean symbolsAsFactoryMethod, int depth) {
    SourceCodeProperties p = AbstractAST.stringFormProperties(symbolsAsFactoryMethod);
    return internalJavaString(p, depth, x -> null);
  }

  @Override
  public CharSequence internalJavaString(SourceCodeProperties properties, int depth,
      Function<ISymbol, ? extends CharSequence> variables) {
    String prefix = AbstractAST.getPrefixF(properties);
    StringBuilder javaForm = new StringBuilder(prefix);
    if (isZero()) {
      return javaForm.append("CD0");
    } else if (isOne()) {
      return javaForm.append("CD1");
    } else if (isMinusOne()) {
      return javaForm.append("CND1");
    }
    return javaForm.append("num(").append(fDouble).append(")");
  }

  @Override
  public CharSequence internalScalaString(boolean symbolsAsFactoryMethod, int depth) {
    SourceCodeProperties p = AbstractAST.scalaFormProperties(symbolsAsFactoryMethod);
    return internalJavaString(p, depth, x -> null);
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
    return ParserConfig.MACHINE_PRECISION;
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
  @Override
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
  @Override
  public boolean isNaN() {
    return Double.isNaN(fDouble);
  }

  /** @return */
  @Override
  public boolean isMathematicalIntegerNegative() {
    return DoubleMath.isMathematicalInteger(fDouble) && fDouble < 0.0;
  }

  /** @return */
  @Override
  public boolean isMathematicalIntegerNonNegative() {
    return DoubleMath.isMathematicalInteger(fDouble) && fDouble >= 0.0;
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
  public boolean isZero(double tolerance) {
    return F.isZero(fDouble, tolerance);
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
      return F
          .num(EvalEngine.getApfloat().multiply(apfloatValue(), ((ApfloatNum) val).apfloatValue()));
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
  public IExpr power(final IExpr that) {
    if (that instanceof Num) {
      if (fDouble < 0.0) {
        return ComplexNum.valueOf(fDouble).power(that);
      }
      return valueOf(Math.pow(fDouble, ((Num) that).getRealPart()));
    }
    if (that instanceof IComplexNum) {
      if (that instanceof ApcomplexNum) {
        return F.complexNum(
            EvalEngine.getApfloat().pow(apcomplexValue(), ((ApcomplexNum) that).apcomplexValue()));
      }
      return ComplexNum.valueOf(fDouble).power(that);
    }
    if (that instanceof ApfloatNum) {
      if (fDouble < 0.0) {
        return F.complexNum(
            EvalEngine.getApfloat().pow(apfloatValue(), ((ApfloatNum) that).apcomplexValue()));
      }
      return F.num(EvalEngine.getApfloat().pow(apfloatValue(), ((ApfloatNum) that).apfloatValue()));
    }

    return INum.super.power(that);
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
          DoubleMath.roundToBigInteger(fDouble / multiple.doubleValue(), RoundingMode.HALF_EVEN))
          .multiply((IRational) multiple);
    }
    double factor = multiple.doubleValue();
    return F
        .num(DoubleMath.roundToBigInteger(fDouble / factor, RoundingMode.HALF_EVEN).doubleValue()
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
    if (fDouble < 0.0) {
      org.hipparchus.complex.Complex c = new org.hipparchus.complex.Complex(fDouble);
      return F.complexNum(c.sqrt());
    }
    return valueOf(Math.sqrt(fDouble));
  }

  @Override
  public ISignedNumber subtractFrom(ISignedNumber that) {
    if (that instanceof Num) {
      return valueOf(fDouble - ((Num) that).fDouble);
    }
    if (that instanceof ApfloatNum) {
      return apfloatNumValue().subtract(that.apfloatNumValue());
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
    if (DoubleMath.isMathematicalInteger(fDouble)) {
      return (int) fDouble;
    }
    return defaultValue;
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
    if (ParserConfig.EXPLICIT_TIMES_OPERATOR) {
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

package org.matheclipse.core.expression;

import java.math.BigInteger;
import java.math.RoundingMode;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.matheclipse.core.eval.EvalEngine;
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

/**
 * <code>INum</code> implementation which wraps a <code>Apfloat</code> value to represent a numeric
 * floating-point number.
 */
public class ApfloatNum implements INum {

  /** */
  private static final long serialVersionUID = 2500259920655377884L;

  Apfloat fApfloat;

  public static final Apint MINUS_ONE = new Apint(-1);

  /**
   * Create a new instance.
   *
   * @param value
   * @param precision
   * @return
   */
  public static ApfloatNum valueOf(final double value) {
    return valueOf(new Apfloat(value));
  }

  public static ApfloatNum valueOf(final Apfloat value) {
    return new ApfloatNum(value);
  }

  public static ApfloatNum valueOf(final BigInteger numerator) {
    return new ApfloatNum(numerator);
  }

  public static ApfloatNum valueOf(final BigInteger numerator, final BigInteger denominator) {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    Apfloat n = new Apfloat(numerator, h.precision());
    Apfloat d = new Apfloat(denominator, h.precision());
    return new ApfloatNum(h.divide(n, d));
  }

  public static ApfloatNum valueOf(final String value, long precision) {
    return new ApfloatNum(value, precision);
  }

  private ApfloatNum(final String value, long precision) {
    fApfloat = new Apfloat(value, precision);
  }

  private ApfloatNum(final BigInteger value) {
    fApfloat = new Apfloat(value, EvalEngine.getApfloat().precision());
  }

  private ApfloatNum(final Apfloat value) {
    fApfloat = value;
  }

  @Override
  public int hierarchy() {
    return DOUBLEID;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNumEqualInteger(IInteger ii) throws ArithmeticException {
    return fApfloat.truncate().equals(new Apint(ii.toBigNumerator()))
        && fApfloat.frac().equals(Apfloat.ZERO);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNumEqualRational(IRational value) throws ArithmeticException {
    long precision = fApfloat.precision();
    return fApfloat.equals(
        new Apfloat(value.toBigNumerator(), precision)
            .divide(new Apfloat(value.toBigDenominator(), precision)));
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNumIntValue() {
    return fApfloat.frac().equals(Apfloat.ZERO);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNegative() {
    return fApfloat.signum() == -1;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPositive() {
    return fApfloat.signum() > 0;
  }

  @Override
  public boolean equalsInt(final int i) {
    try {
      return fApfloat.intValueExact() == i;
    } catch (RuntimeException rex) {
      // ArithmeticException
    }
    return false;
  }

  @Override
  public IExpr evaluate(EvalEngine engine) {
    // if (engine.isNumericMode() && engine.getNumericPrecision() <=
    // ApfloatNum.DOUBLE_PRECISION) {
    // return Num.valueOf(fApfloat.doubleValue());
    // }
    return F.NIL;
  }

  @Override
  public ISignedNumber evalReal() {
    return this;
  }

  @Override
  public INumber evalNumber() {
    return this;
  }

  @Override
  public INum add(final INum value) {
    return valueOf(EvalEngine.getApfloat().add(fApfloat, value.apfloatValue()));
  }

  @Override
  public INum subtract(final INum value) {
    return valueOf(EvalEngine.getApfloat().subtract(fApfloat, value.apfloatValue()));
  }

  @Override
  public INum divide(final INum value) {
    return valueOf(EvalEngine.getApfloat().divide(fApfloat, value.apfloatValue()));
  }

  @Override
  public INum multiply(final INum value) {
    return valueOf(EvalEngine.getApfloat().multiply(fApfloat, value.apfloatValue()));
  }

  @Override
  public INum pow(final INum value) {
    return valueOf(EvalEngine.getApfloat().pow(fApfloat, value.apfloatValue()));
  }

  @Override
  public IExpr power(final IExpr that) {
    if (that instanceof IComplexNum) {
      return F.complexNum(
          EvalEngine.getApfloat().pow(fApfloat, ((IComplexNum) that).apcomplexValue()));
    }
    if (that instanceof INum) {
      if (fApfloat.compareTo(Apfloat.ZERO) < 0) {
        return F.complexNum(
            EvalEngine.getApfloat().pow(fApfloat, ((IComplexNum) that).apcomplexValue()));
      }
      return valueOf(EvalEngine.getApfloat().pow(fApfloat, ((INum) that).apfloatValue()));
    }
    return INum.super.power(that);
  }

  @Override
  public long precision() throws ApfloatRuntimeException {
    return fApfloat.precision();
  }

  /** {@inheritDoc} */
  @Override
  public ApfloatNum abs() {
    return valueOf(EvalEngine.getApfloat().abs(fApfloat));
  }

  /** {@inheritDoc} */
  @Override
  public int compareAbsValueToOne() {
    return EvalEngine.getApfloat().abs(fApfloat).compareTo(Apfloat.ONE);
  }

  /** {@inheritDoc} */
  @Override
  public IExpr dec() {
    return add(F.CND1);
  }

  /** {@inheritDoc} */
  @Override
  public long determinePrecision() {
    return precision();
  }

  /** {@inheritDoc} */
  @Override
  public IExpr inc() {
    return add(F.CD1);
  }

  @Override
  public IExpr plus(final IExpr that) {
    if (that instanceof ApfloatNum) {
      return add((ApfloatNum) that);
    }
    if (that instanceof Num) {
      return add(ApfloatNum.valueOf(((Num) that).getRealPart()));
    }
    if (that instanceof ApcomplexNum) {
      return ApcomplexNum.valueOf(fApfloat).add((ApcomplexNum) that);
    }
    if (that instanceof ComplexNum) {
      ComplexNum cn = (ComplexNum) that;
      return ApcomplexNum.valueOf(fApfloat)
          .add(ApcomplexNum.valueOf(cn.getRealPart(), cn.getImaginaryPart()));
    }
    return INum.super.plus(that);
  }

  @Override
  public ISignedNumber divideBy(ISignedNumber that) {
    return valueOf(EvalEngine.getApfloat().divide(fApfloat, that.apfloatValue()));
  }

  @Override
  public ISignedNumber subtractFrom(ISignedNumber that) {
    return valueOf(EvalEngine.getApfloat().subtract(fApfloat, that.apfloatValue()));
  }

  /** @return */
  @Override
  public double doubleValue() {
    return fApfloat.doubleValue();
  }

  @Override
  public Apfloat apfloatValue() {
    return fApfloat;
  }

  //  public Apfloat apfloatValue() {
  //    return fApfloat;
  //  }

  @Override
  public boolean equals(final Object arg0) {
    if (this == arg0) {
      return true;
    }
    if (arg0 instanceof ApfloatNum) {
      return fApfloat.equals(((ApfloatNum) arg0).fApfloat);
    }
    return false;
  }

  @Override
  public boolean isSame(IExpr expression, double epsilon) {
    if (expression instanceof ApfloatNum) {
      return fApfloat.equals(((ApfloatNum) expression).fApfloat);
    }
    return false;
  }

  @Override
  public final int hashCode() {
    return fApfloat.hashCode();
  }

  /** @return */
  @Override
  public int intValue() {
    return fApfloat.intValue();
  }

  /** {@inheritDoc} */
  @Override
  public int toInt() throws ArithmeticException {
    return fApfloat.intValueExact();
  }

  /** {@inheritDoc} */
  @Override
  public int toIntDefault(int defaultValue) {
    try {
      return fApfloat.intValueExact();
    } catch (RuntimeException rex) {
      // ArithmeticException
    }
    return defaultValue;
  }

  /** {@inheritDoc} */
  @Override
  public long toLongDefault(long defaultValue) {
    try {
      return fApfloat.longValueExact();
    } catch (RuntimeException rex) {
      // ArithmeticException
    }
    return defaultValue;
  }

  /** {@inheritDoc} */
  @Override
  public long toLong() throws ArithmeticException {
    return fApfloat.longValueExact();
  }

  @Override
  public long leafCountSimplify() {
    return 2;
  }

  /** @return */
  public long longValue() {
    return fApfloat.longValue();
  }

  @Override
  public IExpr times(final IExpr that) {
    if (that instanceof ApfloatNum) {
      return multiply((ApfloatNum) that);
    }
    if (that instanceof Num) {
      return multiply(ApfloatNum.valueOf(((Num) that).getRealPart()));
    }
    if (that instanceof ApcomplexNum) {
      return ApcomplexNum.valueOf(fApfloat).multiply((ApcomplexNum) that);
    }
    if (that instanceof ComplexNum) {
      ComplexNum cn = (ComplexNum) that;
      return ApcomplexNum.valueOf(fApfloat)
          .multiply(ApcomplexNum.valueOf(cn.getRealPart(), cn.getImaginaryPart()));
    }
    return INum.super.times(that);
  }

  /** @return */
  @Override
  public ApfloatNum negate() {
    return valueOf(EvalEngine.getApfloat().negate(fApfloat));
  }

  /** @return */
  @Override
  public ApfloatNum opposite() {
    return valueOf(EvalEngine.getApfloat().negate(fApfloat));
  }

  @Override
  public ApfloatNum inverse() {
    if (isOne()) {
      return this;
    }
    return valueOf(EvalEngine.getApfloat().inverseRoot(fApfloat, 1));
  }

  @Override
  public IExpr sqrt() {
    return valueOf(EvalEngine.getApfloat().sqrt(fApfloat));
  }

  @Override
  public double getRealPart() {
    double temp = fApfloat.doubleValue();
    if (temp == (-0.0)) {
      temp = 0.0;
    }
    return temp;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isE() {
    return fApfloat.equals(EvalEngine.getApfloat().exp(Apfloat.ONE));
  }

  /** {@inheritDoc} */
  @Override
  public boolean isMinusOne() {
    return fApfloat.equals(ApfloatNum.MINUS_ONE);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isOne() {
    return fApfloat.equals(Apfloat.ONE);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPi() {
    return fApfloat.equals(EvalEngine.getApfloat().pi());
  }

  /** {@inheritDoc} */
  @Override
  public boolean isRationalValue(IRational value) {
    return fApfloat.equals(value.apfloatNumValue().fApfloat);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isZero() {
    return fApfloat.signum() == 0;
  }

  @Override
  public IInteger roundExpr() {
    Apfloat f = ApfloatMath.round(fApfloat, 1, RoundingMode.HALF_EVEN);
    return F.ZZ(ApfloatMath.floor(f).toBigInteger());
  }

  @Override
  public ISignedNumber roundClosest(ISignedNumber multiple) {
    throw new ArithmeticException("Apfloat: Round closest not implemented");
    // final long precision = precision();
    // Apfloat factor = multiple.apfloatNumValue(precision).fApfloat;
    // return F.num(EvalEngine.getApfloat().round(fApfloat.divide(factor), precision,
    // RoundingMode.HALF_EVEN).multiply(factor));
  }

  /** {@inheritDoc} */
  @Override
  public int complexSign() {
    return fApfloat.signum();
  }

  /** {@inheritDoc} */
  @Override
  public IInteger ceilFraction() {
    return F.ZZ(ApfloatMath.ceil(fApfloat).toBigInteger());
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

  /** {@inheritDoc} */
  @Override
  public ISignedNumber fractionalPart() {
    return F.num(fApfloat.frac());
  }

  /** {@inheritDoc} */
  @Override
  public String fullFormString() {
    return fullFormString(fApfloat);
  }

  public static String fullFormString(Apfloat apfloat) {
    String str = apfloat.toString();
    long precision = apfloat.precision();
    if (!FEConfig.EXPLICIT_TIMES_OPERATOR) {
      int indx = str.indexOf("e");
      if (indx > 0) {
        str = str.substring(0, indx) + "`" + precision + "*^" + str.substring(indx + 1);
      } else {
        str = str + "`" + precision;
      }
    }
    return str;
  }

  /** {@inheritDoc} */
  @Override
  public IInteger floorFraction() {
    return F.ZZ(ApfloatMath.floor(fApfloat).toBigInteger());
  }

  /** {@inheritDoc} */
  @Override
  public IInteger integerPart() {
    return isNegative() ? ceilFraction() : floorFraction();
  }

  /**
   * Compares this expression with the specified expression for order. Returns a negative integer,
   * zero, or a positive integer as this expression is canonical less than, equal to, or greater
   * than the specified expression.
   */
  @Override
  public int compareTo(final IExpr expr) {
    if (expr instanceof ApfloatNum) {
      return fApfloat.compareTo(((ApfloatNum) expr).fApfloat);
    }
    if (expr.isNumber()) {
      if (expr.isReal()) {
        return fApfloat.compareTo(((ISignedNumber) expr).apfloatValue());
      }
      int c = this.compareTo(((INumber) expr).re());
      if (c != 0) {
        return c;
      }
    }
    return -1;
  }

  @Override
  public boolean isLT(ISignedNumber that) {
    if (that instanceof ApfloatNum) {
      return fApfloat.compareTo(((ApfloatNum) that).fApfloat) < 0;
    }
    return doubleValue() < that.doubleValue();
  }

  @Override
  public boolean isGT(ISignedNumber that) {
    if (that instanceof ApfloatNum) {
      return fApfloat.compareTo(((ApfloatNum) that).fApfloat) > 0;
    }
    return doubleValue() > that.doubleValue();
  }

  @Override
  public ISymbol head() {
    return S.Real;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    String str = fApfloat.toString();
    if (FEConfig.EXPLICIT_TIMES_OPERATOR) {
      return str.replace("e", "E");
    }
    int index = str.indexOf('e');
    if (index > 0) {
      String exponentStr = str.substring(index + 1);
      String result = str.substring(0, index);
      return result + "*10^" + exponentStr;
    }
    return str;
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

  @Override
  public long accept(IVisitorLong visitor) {
    return visitor.visit(this);
  }

  /** {@inheritDoc} */
  @Override
  public ISignedNumber im() {
    return F.CD0;
  }

  /** {@inheritDoc} */
  @Override
  public ISignedNumber re() {
    return this;
  }

  @Override
  public ApfloatNum apfloatNumValue() {
    return this;
  }

  //  public ApfloatNum apfloatNumValue() {
  //    return this;
  //  }

  @Override
  public Num numValue() {
    return Num.valueOf(doubleValue());
  }

  public Apcomplex apcomplexValue() {
    return new Apcomplex(fApfloat);
  }

  @Override
  public ApcomplexNum apcomplexNumValue() {
    return ApcomplexNum.valueOf(fApfloat);
  }

  @Override
  public ComplexNum complexNumValue() {
    return ComplexNum.valueOf(fApfloat.doubleValue());
  }

  @Override
  public double imDoubleValue() {
    return 0.0;
  }

  @Override
  public double reDoubleValue() {
    return doubleValue();
  }

  @Override
  public IExpr multiply(int value) {
    return valueOf(EvalEngine.getApfloat().multiply(fApfloat, new Apfloat(value)));
  }

  @Override
  public IExpr acos() {
    return valueOf(EvalEngine.getApfloat().acos(fApfloat));
  }

  @Override
  public IExpr acosh() {
    return valueOf(EvalEngine.getApfloat().acosh(fApfloat));
  }

  @Override
  public IExpr add(double value) {
    return valueOf(EvalEngine.getApfloat().add(fApfloat, new Apfloat(value)));
  }

  @Override
  public IExpr asin() {
    return valueOf(EvalEngine.getApfloat().asin(fApfloat));
  }

  @Override
  public IExpr asinh() {
    return valueOf(EvalEngine.getApfloat().asinh(fApfloat));
  }

  @Override
  public IExpr atan() {
    return valueOf(EvalEngine.getApfloat().atan(fApfloat));
  }

  @Override
  public IExpr atanh() {
    return valueOf(EvalEngine.getApfloat().atanh(fApfloat));
  }

  @Override
  public IExpr cbrt() {
    return valueOf(EvalEngine.getApfloat().cbrt(fApfloat));
  }

  @Override
  public IExpr ceil() {
    return valueOf(EvalEngine.getApfloat().ceil(fApfloat));
  }

  @Override
  public IExpr copySign(double d) {
    return valueOf(EvalEngine.getApfloat().copySign(fApfloat, new Apfloat(d)));
  }

  @Override
  public IExpr cos() {
    return valueOf(EvalEngine.getApfloat().cos(fApfloat));
  }

  @Override
  public IExpr cosh() {
    return valueOf(EvalEngine.getApfloat().cosh(fApfloat));
  }

  @Override
  public IExpr divide(double value) {
    return valueOf(EvalEngine.getApfloat().divide(fApfloat, new Apfloat(value)));
  }

  @Override
  public IExpr divide(final IExpr that) {
    if (that instanceof ApfloatNum) {
      return divide((ApfloatNum) that);
    }
    if (that instanceof Num) {
      return divide(((Num) that).getRealPart());
    }
    if (that instanceof ApcomplexNum) {
      return F.complexNum(
          EvalEngine.getApfloat().divide(fApfloat, ((ApcomplexNum) that).apcomplexValue()));
    }
    if (that instanceof ComplexNum) {
      return F.complexNum(
          EvalEngine.getApfloat().divide(fApfloat, ((ComplexNum) that).apcomplexValue()));
    }
    return INum.super.divide(that);
  }

  @Override
  public IExpr expm1() {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    return valueOf(h.subtract(h.exp(fApfloat), Apfloat.ONE));
  }

  @Override
  public IExpr floor() {
    return valueOf(EvalEngine.getApfloat().floor(fApfloat));
  }

  @Override
  public double getReal() {
    return fApfloat.doubleValue();
  }

  @Override
  public IExpr exp() {
    return valueOf(EvalEngine.getApfloat().exp(fApfloat));
  }

  @Override
  public IExpr log() {
    return valueOf(EvalEngine.getApfloat().log(fApfloat));
  }

  @Override
  public IExpr log10() {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    return valueOf(h.log(fApfloat, new Apfloat(10)));
  }

  @Override
  public IExpr log1p() {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    return valueOf(h.log(h.add(fApfloat, Apfloat.ONE)));
  }

  @Override
  public IExpr multiply(double value) {
    return valueOf(EvalEngine.getApfloat().multiply(fApfloat, new Apfloat(value)));
  }

  @Override
  public ApfloatNum newInstance(double d) {
    return valueOf(d);
  }

  @Override
  public IExpr pow(int n) {
    return valueOf(EvalEngine.getApfloat().pow(fApfloat, n));
  }

  @Override
  public IExpr pow(double value) {
    return valueOf(EvalEngine.getApfloat().pow(fApfloat, new Apfloat(value)));
  }

  @Override
  public IExpr reciprocal() {
    return valueOf(EvalEngine.getApfloat().inverseRoot(fApfloat, 1));
  }

  @Override
  public IExpr remainder(double value) {
    return valueOf(EvalEngine.getApfloat().mod(fApfloat, new Apfloat(value)));
  }

  @Override
  public IExpr rint() {
    return valueOf(apfloatRint(fApfloat));
  }

  static Apfloat apfloatRint(Apfloat fApfloat) {
    if (fApfloat.scale() > 0) {
      return ApfloatMath.round(fApfloat, fApfloat.scale(), RoundingMode.HALF_EVEN);
    }
    if (ApfloatMath.abs(fApfloat).compareTo(new Apfloat("0.5")) <= 0) {
      return Apfloat.ZERO;
    }
    return ApfloatMath.copySign(Apfloat.ONE, fApfloat);
  }

  @Override
  public IExpr scalb(int n) {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    return valueOf(h.multiply(fApfloat, h.pow(new Apfloat(2), n)));
  }

  @Override
  public IExpr rootN(int n) {
    return valueOf(EvalEngine.getApfloat().root(fApfloat, n));
  }

  @Override
  public IExpr sign() {
    if (isNaN() || isZero()) {
      return this;
    }
    return valueOf(EvalEngine.getApfloat().abs(fApfloat));
  }

  @Override
  public IExpr sin() {
    return valueOf(EvalEngine.getApfloat().sin(fApfloat));
  }

  @Override
  public IExpr sinh() {
    return valueOf(EvalEngine.getApfloat().sinh(fApfloat));
  }

  @Override
  public IExpr subtract(double value) {
    return valueOf(EvalEngine.getApfloat().subtract(fApfloat, new Apfloat(value)));
  }

  @Override
  public IExpr subtract(final IExpr that) {
    if (that instanceof ApfloatNum) {
      return subtract((ApfloatNum) that);
    }
    if (that instanceof Num) {
      return subtract(((Num) that).getRealPart());
    }
    if (that instanceof ApcomplexNum) {
      return F.complexNum(
          EvalEngine.getApfloat().subtract(fApfloat, ((ApcomplexNum) that).apcomplexValue()));
    }
    if (that instanceof ComplexNum) {
      return F.complexNum(
          EvalEngine.getApfloat().subtract(fApfloat, ((ComplexNum) that).apcomplexValue()));
    }
    return INum.super.subtract(that);
  }

  @Override
  public IExpr tan() {
    return valueOf(EvalEngine.getApfloat().tan(fApfloat));
  }

  @Override
  public IExpr tanh() {
    return valueOf(EvalEngine.getApfloat().tanh(fApfloat));
  }

  @Override
  public IExpr ulp() {
    return valueOf(EvalEngine.getApfloat().ulp(Apfloat.ONE));
  }

  public IExpr getPi() {
    return valueOf(EvalEngine.getApfloat().pi());
  }

  @Override
  public IExpr toDegrees() {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    // radians * (180 / Pi)
    return valueOf(toDegrees(fApfloat, h));
  }

  @Override
  public IExpr toRadians() {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    // degrees * (Pi / 180)
    return valueOf(toRadians(fApfloat, h));
  }

  /**
   * <code>radians * (180 / Pi)</code>
   *
   * @param radians
   * @param h
   * @return
   */
  static Apfloat toDegrees(Apfloat radians, FixedPrecisionApfloatHelper h) {
    return h.divide(h.multiply(radians, new Apfloat(180)), h.pi());
  }

  /**
   * <code>degrees * (Pi / 180)</code>
   *
   * @param degrees
   * @param h
   * @return
   */
  static Apfloat toRadians(Apfloat degrees, FixedPrecisionApfloatHelper h) {
    return h.divide(h.multiply(degrees, h.pi()), new Apfloat(180));
  }
}

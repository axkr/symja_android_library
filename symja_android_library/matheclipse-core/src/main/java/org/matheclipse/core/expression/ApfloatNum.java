package org.matheclipse.core.expression;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.matheclipse.core.eval.EvalEngine;
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
  public static ApfloatNum valueOf(final double value, long precision) {
    return new ApfloatNum(value, precision);
  }

  public static ApfloatNum valueOf(final Apfloat value) {
    return new ApfloatNum(value);
  }

  public static ApfloatNum valueOf(final BigInteger numerator, long precision) {
    return new ApfloatNum(numerator, precision);
  }

  public static ApfloatNum valueOf(
      final BigInteger numerator, final BigInteger denominator, long precision) {
    Apfloat n = new Apfloat(numerator, precision);
    Apfloat d = new Apfloat(denominator, precision);
    return new ApfloatNum(n.divide(d));
  }

  public static ApfloatNum valueOf(final String value, long precision) {
    return new ApfloatNum(value, precision);
  }

  private ApfloatNum(final double value, long precision) {
    fApfloat = new Apfloat(new BigDecimal(value), precision);
  }

  private ApfloatNum(final String value, long precision) {
    fApfloat = new Apfloat(value, precision);
  }

  private ApfloatNum(final BigInteger value, long precision) {
    fApfloat = new Apfloat(value, precision);
  }

  private ApfloatNum(final Apfloat value) {
    fApfloat = value;
  }

  @Override
  public int hierarchy() {
    // TODO check this ID
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
  public INum add(final INum val) {
    return valueOf(fApfloat.add(val.apfloatValue(fApfloat.precision())));
  }

  @Override
  public INum multiply(final INum val) {
    return valueOf(fApfloat.multiply(val.apfloatValue(fApfloat.precision())));
  }

  @Override
  public INum pow(final INum val) {
    return valueOf(ApfloatMath.pow(fApfloat, val.apfloatValue(fApfloat.precision())));
  }

  @Override
  public long precision() throws ApfloatRuntimeException {
    return fApfloat.precision();
  }

  /** {@inheritDoc} */
  @Override
  public ApfloatNum abs() {
    return valueOf(ApfloatMath.abs(fApfloat));
  }

  /** {@inheritDoc} */
  @Override
  public int compareAbsValueToOne() {
    return ApfloatMath.abs(fApfloat).compareTo(Apfloat.ONE);
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
      return add(ApfloatNum.valueOf(((Num) that).getRealPart(), fApfloat.precision()));
    }
    if (that instanceof ApcomplexNum) {
      return ApcomplexNum.valueOf(fApfloat).add((ApcomplexNum) that);
    }
    if (that instanceof ComplexNum) {
      ComplexNum cn = (ComplexNum) that;
      return ApcomplexNum.valueOf(fApfloat)
          .add(ApcomplexNum.valueOf(cn.getRealPart(), cn.getImaginaryPart(), fApfloat.precision()));
    }
    return INum.super.plus(that);
  }

  @Override
  public ISignedNumber divideBy(ISignedNumber that) {
    return valueOf(fApfloat.divide(that.apfloatValue(fApfloat.precision())));
  }

  @Override
  public ISignedNumber subtractFrom(ISignedNumber that) {
    return valueOf(fApfloat.subtract(that.apfloatValue(fApfloat.precision())));
  }

  /** @return */
  @Override
  public double doubleValue() {
    return fApfloat.doubleValue();
  }

  @Override
  public Apfloat apfloatValue(long precision) {
    return fApfloat;
  }

  public Apfloat apfloatValue() {
    return fApfloat;
  }

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

  public Apfloat exp() {
    return ApfloatMath.exp(fApfloat);
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

  public Apfloat log() {
    return ApfloatMath.log(fApfloat);
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
      return multiply(ApfloatNum.valueOf(((Num) that).getRealPart(), fApfloat.precision()));
    }
    if (that instanceof ApcomplexNum) {
      return ApcomplexNum.valueOf(fApfloat).multiply((ApcomplexNum) that);
    }
    if (that instanceof ComplexNum) {
      ComplexNum cn = (ComplexNum) that;
      return ApcomplexNum.valueOf(fApfloat)
          .multiply(
              ApcomplexNum.valueOf(cn.getRealPart(), cn.getImaginaryPart(), fApfloat.precision()));
    }
    return INum.super.times(that);
  }

  /** @return */
  @Override
  public ApfloatNum negate() {
    return valueOf(fApfloat.negate());
  }

  /** @return */
  @Override
  public ApfloatNum opposite() {
    return valueOf(fApfloat.negate());
  }

  @Override
  public ApfloatNum inverse() {
    if (isOne()) {
      return this;
    }
    return valueOf(ApfloatMath.inverseRoot(fApfloat, 1));
  }

  @Override
  public IExpr sqrt() {
    return valueOf(ApfloatMath.sqrt(fApfloat));
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
    return fApfloat.equals(ApfloatMath.exp(new Apfloat(1, fApfloat.precision())));
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
    return fApfloat.equals(ApfloatMath.pi(fApfloat.precision()));
  }

  /** {@inheritDoc} */
  @Override
  public boolean isRationalValue(IRational value) {
    return fApfloat.equals(value.apfloatNumValue(fApfloat.precision()).fApfloat);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isZero() {
    return fApfloat.signum() == 0;
  }

  @Override
  public IInteger round() {
    Apfloat f = ApfloatMath.round(fApfloat, 1, RoundingMode.HALF_EVEN);
    return F.ZZ(ApfloatMath.floor(f).toBigInteger());
  }

  @Override
  public ISignedNumber roundClosest(ISignedNumber multiple) {
    throw new ArithmeticException("Apfloat: Round closest not implemented");
    // final long precision = precision();
    // Apfloat factor = multiple.apfloatNumValue(precision).fApfloat;
    // return F.num(ApfloatMath.round(fApfloat.divide(factor), precision,
    // RoundingMode.HALF_EVEN).multiply(factor));
  }

  /** {@inheritDoc} */
  @Override
  public int sign() {
    return fApfloat.signum();
  }

  /** {@inheritDoc} */
  @Override
  public int complexSign() {
    return sign();
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
        return fApfloat.compareTo(((ISignedNumber) expr).apfloatValue(precision()));
      }
      int c = this.compareTo(((INumber) expr).re());
      if (c != 0) {
        return c;
      }
    }
    return -1;
    // return INum.super.compareTo(expr);
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
  public ApfloatNum apfloatNumValue(long precision) {
    return this;
  }

  public ApfloatNum apfloatNumValue() {
    return this;
  }

  @Override
  public Num numValue() {
    return Num.valueOf(doubleValue());
  }

  public Apcomplex apcomplexValue(long precision) {
    return new Apcomplex(fApfloat);
  }

  @Override
  public ApcomplexNum apcomplexNumValue(long precision) {
    return ApcomplexNum.valueOf(fApfloat, new Apfloat(0, fApfloat.precision()));
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
}

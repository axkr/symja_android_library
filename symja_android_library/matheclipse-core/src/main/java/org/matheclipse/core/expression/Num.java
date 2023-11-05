package org.matheclipse.core.expression;

import java.util.function.Function;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.Aprational;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.hipparchus.complex.Complex;
import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.util.MathUtils;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.functions.HypergeometricJS;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.util.SourceCodeProperties;
import org.matheclipse.core.form.DoubleToMMA;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInexactNumber;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
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

  public static double valueOf(final String chars) {
    return Double.parseDouble(chars);
  }

  protected double value;

  protected Num() {
    this.value = 0.0;
  }

  /* package private */ Num(final double value) {
    this.value = value;
  }

  /** {@inheritDoc} */
  @Override
  public INum abs() {
    if (isNegative()) {
      return valueOf(Math.abs(value));
    }
    return this;
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
  public IInexactNumber acos() {
    // https://github.com/Hipparchus-Math/hipparchus/issues/128
    if (value > 1.0 || value < -1.0) {
      return F.complexNum(Complex.valueOf(value).acos());
    }
    return valueOf(Math.acos(value));
  }

  @Override
  public INum add(final INum val) {
    if (val instanceof ApfloatNum) {
      Apfloat arg2 = ((ApfloatNum) val).apfloatValue();
      return F.num(EvalEngine.getApfloat().add(apfloatValue(), arg2));
    }
    return valueOf(value + val.getRealPart());
  }

  @Override
  public IReal add(final IReal val) {
    if (val instanceof INum) {
      return multiply((INum) val);
    }
    return val.add(this);
  }

  @Override
  public ApcomplexNum apcomplexNumValue() {
    return ApcomplexNum.valueOf(apcomplexValue());
  }

  @Override
  public Apcomplex apcomplexValue() {
    return new Apcomplex(new Apfloat(value));
  }

  @Override
  public ApfloatNum apfloatNumValue() {
    return ApfloatNum.valueOf(value);
  }

  @Override
  public Apfloat apfloatValue() {
    return new Apfloat(value);
  }

  @Override
  public IInexactNumber asin() {
    // https://github.com/Hipparchus-Math/hipparchus/issues/128
    if (value > 1.0) {
      return F.complexNum(Complex.valueOf(value, -0.0).asin());
    } else if (value < -1.0) {
      return F.complexNum(Complex.valueOf(value, 0.0).asin());
    }
    return valueOf(Math.asin(value));
  }

  @Override
  public IInexactNumber atan() {
    return valueOf(Math.atan(value));
  }

  /** {@inheritDoc} */
  @Override
  public IInteger ceilFraction() {
    try {
      return F.ZZ(NumberUtil.toLong(Math.ceil(value)));
    } catch (ArithmeticException ae) {
      ArgumentTypeException.throwArg(this, F.Ceiling(this));
    }
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public int compareAbsValueToOne() {
    double temp = Math.abs(value);
    return Double.compare(temp, 1.0);
  }

  public int compareTo(final double that) {
    return Double.compare(value, that);
  }

  /**
   * Compares this expression with the specified expression for order. Returns a negative integer,
   * zero, or a positive integer as this expression is canonical less than, equal to, or greater
   * than the specified expression.
   */
  @Override
  public int compareTo(final IExpr expr) {
    if (expr instanceof Num) {
      return Double.compare(value, ((Num) expr).value);
    }
    if (expr.isNumber()) {
      if (expr.isReal()) {
        return Double.compare(value, ((IReal) expr).doubleValue());
      }
      int c = this.compareTo(((INumber) expr).re());
      if (c != 0) {
        return c;
      }
      IExpr im = expr.im();
      return im.isPositive() ? -1 : im.isNegative() ? 1 : IExpr.compareHierarchy(this, expr);
    }
    return IExpr.compareHierarchy(this, expr);
  }

  @Override
  public ComplexNum complexNumValue() {
    // double precision complex number
    return ComplexNum.valueOf(doubleValue(), 0.0);
  }

  @Override
  public int complexSign() {
    return (int) Math.signum(value);
  }

  @Override
  public IExpr copy() {
    return this;
  }

  @Override
  public INum cos() {
    return valueOf(Math.cos(value));
  }

  @Override
  public INum cosh() {
    return valueOf(Math.cosh(value));
  }

  /** {@inheritDoc} */
  @Override
  public IExpr dec() {
    return valueOf(value - 1.0);
  }

  /** {@inheritDoc} */
  @Override
  public long determinePrecision() {
    return ParserConfig.MACHINE_PRECISION;
  }

  @Override
  public INum divide(final INum val) {
    if (val instanceof ApfloatNum) {
      Apfloat arg2 = ((ApfloatNum) val).apfloatValue();
      return F.num(EvalEngine.getApfloat().divide(apfloatValue(), arg2));
    }
    return valueOf(value / val.getRealPart());
  }

  @Override
  public IReal divideBy(IReal that) {
    if (that instanceof Num) {
      return valueOf(value / ((Num) that).value);
    }
    if (that instanceof ApfloatNum) {
      return apfloatNumValue().divide(that.apfloatNumValue());
    }
    return valueOf(value / that.doubleValue());
  }

  @Override
  public double doubleValue() {
    return value;
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof Num) {
      final Num c = (Num) other;
      return Double.compare(value, c.value) == 0;
    }
    return false;
  }

  @Override
  public boolean equalsInt(final int i) {
    return F.isNumIntValue(value, i);
  }

  @Override
  public IExpr erf() {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloatDouble();
    try {
      Apint two = new Apint(2);
      // 1/2
      Aprational oneHalf = new Aprational(Apint.ONE, new Apint(2));
      // 3/2
      Aprational threeHalf = new Aprational(new Apint(3), new Apint(2));
      Apfloat x = apfloatValue();
      Apfloat erf = h.hypergeometric1F1(oneHalf, threeHalf, h.multiply(x, x).negate()).multiply(two)
          .multiply(x).divide(h.sqrt(h.pi()));
      return F.num(erf.doubleValue());
    } catch (Exception ce) {
      //
    }
    return F.NIL;
  }

  @Override
  public IExpr erfc() {
    try {
      return Num.valueOf(de.lab4inf.math.functions.Erf.erfc(value));
      // if (arg1 >= 0. && arg1 <= 2.0) {
      // return Num.valueOf(org.hipparchus.special.Erf.erfc(arg1));
      // }
    } catch (final MathIllegalStateException e) {
    }

    return F.NIL;
  }

  @Override
  public INumber evalNumber() {
    return this;
  }

  @Override
  public IReal evalReal() {
    return this;
  }

  @Override
  public IExpr evaluate(EvalEngine engine) {
    // if (value == Double.POSITIVE_INFINITY) {
    // return F.Overflow();
    // // return F.CInfinity;
    // }
    // if (value == Double.NEGATIVE_INFINITY) {
    // return F.Underflow();
    // // return F.CNInfinity;
    // }
    if (Double.isNaN(value)) {
      return S.Indeterminate;
    }
    if (engine.isNumericMode() && engine.isArbitraryMode()) {
      return ApfloatNum.valueOf(value);
    }
    return F.NIL;
  }

  @Override
  public INumber evaluatePrecision(EvalEngine engine) {
    return this;
  }

  @Override
  public INum exp() {
    return valueOf(Math.exp(value));
  }

  /** {@inheritDoc} */
  @Override
  public IInteger floorFraction() {
    try {
      return F.ZZ(NumberUtil.toLong(Math.floor(value)));
    } catch (ArithmeticException ae) {
      ArgumentTypeException.throwArg(this, F.Floor(this));
    }
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public IReal fractionalPart() {
    return F.num(getRealPart() % 1);
  }

  /** {@inheritDoc} */
  @Override
  public String fullFormString() {
    return fullFormString(value);
  }

  @Override
  public Num getPi() {
    return F.num(Math.PI);
  }

  @Override
  public double getRealPart() {
    double temp = value;
    if (temp == (-0.0)) {
      temp = 0.0;
    }
    return temp;
  }

  @Override
  public final int hashCode() {
    if (Double.isNaN(value)) {
      return 11;
    }
    return 37 * 17 * MathUtils.hash(value);
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
  public IExpr fresnelC() {
    Apcomplex fresnelC = ApcomplexNum.fresnelC(apfloatValue(), EvalEngine.getApfloatDouble());
    return F.complexNum(fresnelC.real().doubleValue(), fresnelC.imag().doubleValue());
  }

  @Override
  public IExpr fresnelS() {
    Apcomplex fresnelS = ApcomplexNum.fresnelS(apfloatValue(), EvalEngine.getApfloatDouble());
    return F.complexNum(fresnelS.real().doubleValue(), fresnelS.imag().doubleValue());
  }

  @Override
  public IExpr hypergeometric0F1(IExpr arg2) {
    try {
      return F.num(HypergeometricJS.hypergeometric0F1(value, arg2.evalf()));
    } catch (RuntimeException e) {
      // try as computation with complex numbers
    }
    try {
      return F.complexNum(
          HypergeometricJS.hypergeometric0F1(new Complex(value), ((ComplexNum) arg2).evalfc()));
    } catch (RuntimeException e) {
      // try as computation with complex numbers
    }
    return INum.super.hypergeometric0F1(arg2);
  }

  @Override
  public IExpr hypergeometric1F1(IExpr arg2, IExpr arg3) {
    try {
      return F.num(HypergeometricJS.hypergeometric1F1(value, //
          arg2.evalf(), //
          arg3.evalf()));
    } catch (RuntimeException e) {
      // try as computation with complex numbers
    }

    try {
      return F.complexNum(HypergeometricJS.hypergeometric1F1(new Complex(value), //
          arg2.evalfc(), //
          arg3.evalfc()));
    } catch (RuntimeException e) {
      // try as computation with complex numbers
    }
    return INum.super.hypergeometric1F1(arg2, arg3);
  }

  @Override
  public IExpr hypergeometric2F1(IExpr arg2, IExpr arg3, IExpr arg4) {
    if (arg2 instanceof IReal && arg3 instanceof IReal && arg4 instanceof IReal) {
      try {
        Apfloat hypergeometric2f1 = EvalEngine.getApfloatDouble().hypergeometric2F1(apfloatValue(),
            ((IReal) arg2).apfloatValue(), ((IReal) arg3).apfloatValue(),
            ((IReal) arg4).apfloatValue());
        return F.num(hypergeometric2f1.doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException ex) {
        if (ex.getMessage().equals("Division by zero")) {
          return F.ComplexInfinity;
        }
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber && arg3 instanceof INumber && arg4 instanceof INumber) {
      try {
        Apcomplex hypergeometric2f1 = EvalEngine.getApfloatDouble().hypergeometric2F1(
            apcomplexValue(), ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue(),
            ((INumber) arg4).apcomplexValue());
        return F.complexNum(hypergeometric2f1.real().doubleValue(),
            hypergeometric2f1.imag().doubleValue());
      } catch (ArithmeticException aex) {
        if (aex.getMessage().equals("Division by zero")) {
          return F.ComplexInfinity;
        } else {
          // aex.printStackTrace();
        }
      }
    }
    return INum.super.hypergeometric2F1(arg2, arg3, arg4);
    // try {
    // return F.num(HypergeometricJS.hypergeometric2F1(value, //
    // arg2.evalf(), //
    // arg3.evalf(), //
    // arg4.evalf()));
    // } catch (RuntimeException e) {
    // // try as computation with complex numbers
    // }
    //
    // try {
    // return F.complexNum(HypergeometricJS.hypergeometric2F1(new Complex(value), //
    // arg2.evalfc(), //
    // arg3.evalfc(), //
    // arg4.evalfc()));
    // } catch (RuntimeException e) {
    // // try as computation with complex numbers
    // }
    // return INum.super.hypergeometric2F1(arg2, arg3, arg4);
  }

  /** {@inheritDoc} */
  @Override
  public IReal im() {
    return F.CD0;
  }

  @Override
  public double imDoubleValue() {
    return 0.0;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr inc() {
    return valueOf(value + 1.0);
  }

  /** {@inheritDoc} */
  @Override
  public IInteger integerPart() {
    return isNegative() ? ceilFraction() : floorFraction();
  }

  @Override
  public CharSequence internalFormString(boolean symbolsAsFactoryMethod, int depth) {
    SourceCodeProperties p = SourceCodeProperties.stringFormProperties(symbolsAsFactoryMethod);
    return internalJavaString(p, depth, x -> null);
  }

  @Override
  public CharSequence internalJavaString(SourceCodeProperties properties, int depth,
      Function<ISymbol, ? extends CharSequence> variables) {
    String prefix = SourceCodeProperties.getPrefixF(properties);
    StringBuilder javaForm = new StringBuilder(prefix);
    if (isZero()) {
      return javaForm.append("CD0");
    } else if (isOne()) {
      return javaForm.append("CD1");
    } else if (isMinusOne()) {
      return javaForm.append("CND1");
    }
    return javaForm.append("num(").append(value).append(")");
  }

  @Override
  public CharSequence internalScalaString(boolean symbolsAsFactoryMethod, int depth) {
    SourceCodeProperties p = SourceCodeProperties.scalaFormProperties(symbolsAsFactoryMethod);
    return internalJavaString(p, depth, x -> null);
  }

  @Override
  public int intValue() {
    return (int) value;
  }

  @Override
  public Num inverse() {
    if (isOne()) {
      return this;
    }
    return valueOf(1 / value);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isE() {
    return F.isZero(value - Math.E);
  }

  @Override
  public boolean isGT(IReal that) {
    return value > that.doubleValue();
  }

  @Override
  public boolean isInexactNumber() {
    return Double.isFinite(value);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isIndeterminate() {
    return Double.isNaN(value);
  }

  @Override
  public boolean isInfinite() {
    return Double.isInfinite(value);
  }

  @Override
  public boolean isLT(IReal that) {
    return value < that.doubleValue();
  }

  @Override
  public boolean isMathematicalIntegerNegative() {
    return DoubleMath.isMathematicalInteger(value) && value < 0.0;
  }

  @Override
  public boolean isMathematicalIntegerNonNegative() {
    return DoubleMath.isMathematicalInteger(value) && value >= 0.0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isMinusOne() {
    return F.isZero(value + 1.0);
  }

  @Override
  public boolean isNaN() {
    return Double.isNaN(value);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNegative() {
    return value < 0.0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNumEqualInteger(IInteger value) throws ArithmeticException {
    return F.isNumEqualInteger(this.value, value);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNumEqualRational(IRational value) throws ArithmeticException {
    return F.isNumEqualRational(this.value, value);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNumIntValue() {
    return F.isNumIntValue(value);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isOne() {
    return F.isZero(value - 1.0);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPi() {
    return F.isZero(value - Math.PI);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPositive() {
    return value > 0.0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isRationalValue(IRational value) {
    return F.isZero(this.value - value.doubleValue());
  }

  @Override
  public boolean isSame(IExpr expression) {
    if (expression instanceof Num) {
      final Num c = (Num) expression;
      return F.isAlmostSame(value, c.value);
    }
    return false;
  }

  @Override
  public boolean isSame(IExpr expression, double epsilon) {
    if (expression instanceof Num) {
      return F.isZero(value - ((Num) expression).value, epsilon);
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isZero() {
    return F.isZero(value, Config.DOUBLE_TOLERANCE);
  }

  @Override
  public boolean isZero(double tolerance) {
    return F.isZero(value, tolerance);
  }

  @Override
  public long leafCountSimplify() {
    return 2;
  }

  @Override
  public IInexactNumber log() {
    if (isNegative()) {
      return ComplexNum.valueOf(new Complex(value).log());
    }
    return valueOf(Math.log(value));
  }

  public long longValue() {
    return (long) value;
  }

  public double minus(final double that) {
    return value - that;
  }

  @Override
  public INum multiply(final INum val) {
    if (val instanceof ApfloatNum) {
      return F
          .num(EvalEngine.getApfloat().multiply(apfloatValue(), ((ApfloatNum) val).apfloatValue()));
    }
    return valueOf(value * val.getRealPart());
  }

  @Override
  public IReal multiply(final IReal val) {
    if (val instanceof INum) {
      return multiply((INum) val);
    }
    return val.multiply(this);
  }

  @Override
  public Num negate() {
    return valueOf(-value);
  }

  @Override
  public Num numValue() {
    return this;
  }

  @Override
  public INum one() {
    return F.CD1;
  }

  @Override
  public IReal opposite() {
    return valueOf(-value);
  }

  public double plus(final double that) {
    return value + that;
  }

  @Override
  public IExpr plus(final IExpr that) {
    if (that instanceof INumber) {
      return plus((INumber) that);
    }
    return INum.super.plus(that);
  }

  @Override
  public INumber plus(final INumber that) {
    if (that instanceof IInexactNumber) {
      return plus((IInexactNumber) that);
    }
    if (that instanceof IReal) {
      return Num.valueOf(value + that.evalf());
    }
    if (that instanceof ComplexSym) {
      return F.complexNum(new Complex(value).add(that.evalfc()));
    }
    throw new java.lang.ArithmeticException();
  }

  @Override
  public IInexactNumber plus(final IInexactNumber that) {
    if (that instanceof INum) {
      if (that instanceof ApfloatNum) {
        return apfloatNumValue().add(((ApfloatNum) that).apfloatNumValue());
      }
      return valueOf(value + ((Num) that).value);
    }
    if (that instanceof IComplexNum) {
      if (that instanceof ApcomplexNum) {
        return apcomplexNumValue().add(((ApcomplexNum) that).apcomplexNumValue());
      }
      return ComplexNum.valueOf(value).add((ComplexNum) that);
    }
    throw new java.lang.ArithmeticException();
  }

  @Override
  public Num pow(int n) {
    return valueOf(Math.pow(value, n));
  }

  @Override
  public INum pow(final INum val) {
    return valueOf(Math.pow(value, val.getRealPart()));
  }

  @Override
  public IExpr power(final IExpr that) {
    if (that instanceof Num) {
      if (value < 0.0) {
        return ComplexNum.valueOf(value).power(that);
      }
      return valueOf(Math.pow(value, ((Num) that).getRealPart()));
    }
    if (that instanceof IComplexNum) {
      if (that instanceof ApcomplexNum) {
        return F.complexNum(
            EvalEngine.getApfloat().pow(apcomplexValue(), ((ApcomplexNum) that).apcomplexValue()));
      }
      return ComplexNum.valueOf(value).power(that);
    }
    if (that instanceof ApfloatNum) {
      if (value < 0.0) {
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

  /** {@inheritDoc} */
  @Override
  public IReal re() {
    return this;
  }

  @Override
  public double reDoubleValue() {
    return doubleValue();
  }

  @Override
  public Num rootN(int n) {
    return valueOf(Math.pow(value, 1.0 / n));
  }

  @Override
  public IReal roundClosest(IReal multiple) {
    if (multiple.isRational()) {
      return F
          .ZZ(DoubleMath.roundToBigInteger(value / multiple.doubleValue(), Config.ROUNDING_MODE))
          .multiply((IRational) multiple);
    }
    double factor = multiple.doubleValue();
    return F.num(
        DoubleMath.roundToBigInteger(value / factor, Config.ROUNDING_MODE).doubleValue() * factor);
  }

  @Override
  public IInteger roundExpr() {
    return F.ZZ(DoubleMath.roundToBigInteger(value, Config.ROUNDING_MODE));
  }

  @Override
  public Num sign() {
    return F.num((int) Math.signum(value));
  }

  @Override
  public INum sin() {
    return valueOf(Math.sin(value));
  }

  @Override
  public INum sinh() {
    return valueOf(Math.sinh(value));
  }

  @Override
  public IExpr sqr() {
    return valueOf(value * value);
  }

  @Override
  public IExpr sqrt() {
    if (value < 0.0) {
      org.hipparchus.complex.Complex c = new org.hipparchus.complex.Complex(value);
      return F.complexNum(c.sqrt());
    }
    return valueOf(Math.sqrt(value));
  }

  @Override
  public INum subtract(final INum val) {
    if (val instanceof ApfloatNum) {
      Apfloat arg2 = ((ApfloatNum) val).apfloatValue();
      return F.num(EvalEngine.getApfloat().subtract(apfloatValue(), arg2));
    }
    return valueOf(value - val.getRealPart());
  }

  @Override
  public IReal subtractFrom(IReal that) {
    if (that instanceof Num) {
      return valueOf(value - ((Num) that).value);
    }
    if (that instanceof ApfloatNum) {
      return apfloatNumValue().subtract(that.apfloatNumValue());
    }
    return valueOf(doubleValue() - that.doubleValue());
  }

  @Override
  public INum tan() {
    return valueOf(Math.tan(value));
  }

  @Override
  public INum tanh() {
    return valueOf(Math.tanh(value));
  }

  @Override
  public IExpr times(final IExpr that) {
    if (that instanceof INumber) {
      return times((INumber) that);
    }
    return INum.super.times(that);
  }

  @Override
  public INumber times(final INumber that) {
    if (that instanceof IInexactNumber) {
      return times((IInexactNumber) that);
    }
    if (that instanceof IReal) {
      return Num.valueOf(value * that.evalf());
    }
    if (that instanceof ComplexSym) {
      return F.complexNum(new Complex(value).multiply(that.evalfc()));
    }
    throw new java.lang.ArithmeticException();
  }

  @Override
  public IInexactNumber times(final IInexactNumber that) {
    if (that instanceof INum) {
      if (that instanceof ApfloatNum) {
        return apfloatNumValue().multiply(((ApfloatNum) that).apfloatNumValue());
      }
      return valueOf(value * ((Num) that).value);
    }
    if (that instanceof IComplexNum) {
      if (that instanceof ApcomplexNum) {
        return apcomplexNumValue().multiply(((ApcomplexNum) that).apcomplexNumValue());
      }
      return ComplexNum.valueOf(value).multiply((ComplexNum) that);
    }
    throw new java.lang.ArithmeticException();
  }

  @Override
  public Num toDegrees() {
    // radians * (180 / Pi)
    return valueOf(value * 180.0 / Math.PI);
  }

  /** {@inheritDoc} */
  @Override
  public int toInt() throws ArithmeticException {
    return NumberUtil.toInt(value);
  }

  /** {@inheritDoc} */
  @Override
  public int toIntDefault(int defaultValue) {
    return F.toIntDefault(value, defaultValue);
  }

  /** {@inheritDoc} */
  @Override
  public long toLong() throws ArithmeticException {
    return NumberUtil.toLong(value);
  }

  /** {@inheritDoc} */
  @Override
  public long toLongDefault(long defaultValue) {
    try {
      return NumberUtil.toLong(value);
    } catch (ArithmeticException ae) {
      return defaultValue;
    }
  }

  @Override
  public Num toRadians() {
    // degrees * (Pi / 180)
    return valueOf(value * Math.PI / 180.0);
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    if (ParserConfig.EXPLICIT_TIMES_OPERATOR) {
      return Double.toString(value);
    }
    StringBuilder buf = new StringBuilder();
    DoubleToMMA.doubleToMMA(buf, value, 5, 7);
    return buf.toString();
  }

  @Override
  public Num ulp() {
    return valueOf(Math.ulp(value));
  }

  @Override
  public INum zero() {
    return F.CD0;
  }

  @Override
  public Num reciprocal() {
    return inverse();
  }

}

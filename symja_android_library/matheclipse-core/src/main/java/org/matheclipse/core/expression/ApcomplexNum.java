package org.matheclipse.core.expression;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInexactNumber;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;
import org.matheclipse.parser.client.ParserConfig;

/**
 * <code>IComplexNum</code> implementation which wraps a {@link Apcomplex} value to represent a
 * numeric complex floating-point number.
 */
public class ApcomplexNum implements IComplexNum {

  public static ApcomplexNum valueOf(final Apcomplex value) {
    return new ApcomplexNum(value);
  }

  public static ApcomplexNum valueOf(final Apfloat real) {
    return new ApcomplexNum(real);
  }

  public static ApcomplexNum valueOf(final Apfloat real, final Apfloat imag) {
    return new ApcomplexNum(real, imag);
  }

  public static ApcomplexNum valueOf(final double real) {
    return valueOf(EvalEngine.getApfloat().valueOf(new Apcomplex(new Apfloat(real))));
  }

  public static ApcomplexNum valueOf(final double real, final double imaginary) {
    return valueOf(
        EvalEngine.getApfloat().valueOf(new Apcomplex(new Apfloat(real), new Apfloat(imaginary))));
  }

  public static ApcomplexNum valueOf(final Complex c, long precision) {
    return valueOf(new Apcomplex(new Apfloat(new BigDecimal(c.getReal()), precision),
        new Apfloat(new BigDecimal(c.getImaginary()), precision)));
  }

  public static ApcomplexNum valueOf(final String realPart, final String imaginaryPart,
      long precision) {
    return new ApcomplexNum(realPart, imaginaryPart, precision);
  }

  /**
   * Create a <code>ApcomplexNum</code> complex number from the real and imaginary <code>BigInteger
   * </code> parts.
   *
   * @param realNumerator the real numbers numerator part
   * @param realDenominator the real numbers denominator part
   * @param imagNumerator the imaginary numbers numerator part
   * @param imagDenominator the imaginary numbers denominator part
   * @return a new <code>ApcomplexNum</code> complex number object
   */
  public static ApcomplexNum valueOf(final BigInteger realNumerator,
      final BigInteger realDenominator, final BigInteger imagNumerator,
      final BigInteger imagDenominator) {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    long prec = h.precision();
    Apfloat real = h.divide(new Apfloat(realNumerator, prec), new Apfloat(realDenominator, prec));
    Apfloat imag = h.divide(new Apfloat(imagNumerator, prec), new Apfloat(imagDenominator, prec));
    return new ApcomplexNum(real, imag);
  }

  /** */
  private static final long serialVersionUID = -6033055105824482264L;

  /** The square root of -1. A number representing "0.0 + 1.0i" */
  public static final ApcomplexNum I = new ApcomplexNum(Apcomplex.I);

  /** A complex number representing "NaN + NaNi" */
  // public static final ApfloatComplexNum NaN = valueOf(Double.NaN,
  // Double.NaN);

  /** A complex number representing "1.0 + 0.0i" */
  public static final ApcomplexNum ONE = new ApcomplexNum(Apcomplex.ONE);

  /** A complex number representing "-1.0 + 0.0i" */
  public static final ApcomplexNum MINUS_ONE = new ApcomplexNum(new Apfloat(-1));

  /** A complex number representing "0.0 + 0.0i" */
  public static final ApcomplexNum ZERO = new ApcomplexNum(Apcomplex.ZERO);

  Apcomplex fApcomplex;

  private ApcomplexNum(Apcomplex complex) {
    fApcomplex = complex;
  }

  private ApcomplexNum(Apfloat real) {
    fApcomplex = new Apcomplex(real);
  }

  private ApcomplexNum(Apfloat real, Apfloat imag) {
    fApcomplex = new Apcomplex(real, imag);
  }

  private ApcomplexNum(final String realPart, String imaginaryPart, long precision) {
    fApcomplex =
        new Apcomplex(new Apfloat(realPart, precision), new Apfloat(imaginaryPart, precision));
  }

  /** @return */
  @Override
  public double getImaginaryPart() {
    double temp = fApcomplex.imag().doubleValue();
    if (temp == (-0.0)) {
      temp = 0.0;
    }
    return temp;
  }

  @Override
  public Apcomplex apcomplexValue() {
    return fApcomplex;
  }

  @Override
  public ApcomplexNum apcomplexNumValue() {
    return this;
  }

  @Override
  public ComplexNum complexNumValue() {
    return ComplexNum.valueOf(fApcomplex.real().doubleValue(), fApcomplex.imag().doubleValue());
  }

  /** {@inheritDoc} */
  @Override
  public IExpr dec() {
    return add(MINUS_ONE);
  }

  /** {@inheritDoc} */
  @Override
  public long determinePrecision() {
    return precision();
  }

  /** {@inheritDoc} */
  @Override
  public IExpr inc() {
    return add(ONE);
  }

  /** @return */
  @Override
  public double getRealPart() {
    double temp = fApcomplex.real().doubleValue();
    if (temp == (-0.0)) {
      temp = 0.0;
    }
    return temp;
  }

  @Override
  public boolean isOne() {
    return fApcomplex.real().compareTo(Apfloat.ONE) == 0 //
        && fApcomplex.imag().signum() == 0;
  }

  @Override
  public boolean isMinusOne() {
    return fApcomplex.real().compareTo(ApfloatNum.MINUS_ONE) == 0 //
        && fApcomplex.imag().signum() == 0;
  }

  @Override
  public boolean isZero() {
    return fApcomplex.real().signum() == 0 //
        && fApcomplex.imag().signum() == 0;
  }

  @Override
  public boolean isZero(double tolerance) {
    return F.isZero(fApcomplex.real().doubleValue(), tolerance) && //
        F.isZero(fApcomplex.imag().doubleValue(), tolerance);
  }

  @Override
  public int hierarchy() {
    return DOUBLECOMPLEXID;
  }

  @Override
  public IExpr hypergeometric0F1(IExpr arg2) {
    if (arg2 instanceof INumber) {
      return valueOf(
          EvalEngine.getApfloat().hypergeometric0F1(fApcomplex, ((INumber) arg2).apcomplexValue()));
    }
    return IComplexNum.super.hypergeometric0F1(arg2);
  }

  @Override
  public IExpr fresnelC() {
    return valueOf(EvalEngine.getApfloat().fresnelC(fApcomplex));
    // return valueOf(fresnelC(fApcomplex, EvalEngine.getApfloat()));
  }

  // public static Apcomplex fresnelC(Apcomplex z, FixedPrecisionApfloatHelper apfloat) {
  // // Complex m1 = HypergeometricJS.hypergeometric1F1(new Complex(0.5), new Complex(1.5),
  // // new Complex(0, Math.PI / 2).multiply(x.multiply(x)));
  // // Complex m2 = HypergeometricJS.hypergeometric1F1(new Complex(0.5), new Complex(1.5),
  // // new Complex(0, -Math.PI / 2).multiply(x.multiply(x)));
  // //
  // // return mul(0.5, x, m1.add(m2));
  // // z*z
  // Apcomplex sqr = apfloat.multiply(z, z);
  // // Math.PI / 2
  // Apfloat piHalf = apfloat.divide(apfloat.pi(), new Apint(2));
  // // 1/2
  // Aprational oneHalf = new Aprational(Apint.ONE, new Apint(2));
  // // 3/2
  // Aprational threeHalf = new Aprational(new Apint(3), new Apint(2));
  // Apcomplex m1 = apfloat.hypergeometric1F1(oneHalf, threeHalf,
  // new Apcomplex(Apfloat.ZERO, piHalf).multiply(sqr));
  // Apcomplex m2 = apfloat.hypergeometric1F1(oneHalf, threeHalf,
  // new Apcomplex(Apfloat.ZERO, piHalf.negate()).multiply(sqr));
  //
  // return apfloat.multiply(oneHalf, z).multiply(apfloat.add(m1, m2));
  // }

  @Override
  public IExpr fresnelS() {
    return valueOf(EvalEngine.getApfloat().fresnelS(fApcomplex));
    // return valueOf(fresnelS(fApcomplex, EvalEngine.getApfloat()));
  }

  // public static Apcomplex fresnelS(Apcomplex z, FixedPrecisionApfloatHelper apfloat) {
  // // Complex m1 = HypergeometricJS.hypergeometric1F1(new Complex(0.5), new Complex(1.5),
  // // new Complex(0, Math.PI / 2).multiply(x.multiply(x)));
  // // Complex m2 = HypergeometricJS.hypergeometric1F1(new Complex(0.5), new Complex(1.5),
  // // new Complex(0, -Math.PI / 2).multiply(x.multiply(x)));
  // //
  // // return mul(new Complex(0, -0.5), x, sub(m1, m2));
  //
  // // z*z
  // Apcomplex sqr = apfloat.multiply(z, z);
  // // Math.PI / 2
  // Apfloat piHalf = apfloat.divide(apfloat.pi(), new Apint(2));
  // // 1/2
  // Aprational oneHalf = new Aprational(Apint.ONE, new Apint(2));
  // // -1/2
  // Aprational minusOneHalf = new Aprational(new Apint(-1), new Apint(2));
  // // 3/2
  // Aprational threeHalf = new Aprational(new Apint(3), new Apint(2));
  // Apcomplex m1 = apfloat.hypergeometric1F1(oneHalf, threeHalf,
  // new Apcomplex(Apfloat.ZERO, piHalf).multiply(sqr));
  // Apcomplex m2 = apfloat.hypergeometric1F1(oneHalf, threeHalf,
  // new Apcomplex(Apfloat.ZERO, piHalf.negate()).multiply(sqr));
  //
  // return apfloat.multiply(new Apcomplex(Apfloat.ZERO, minusOneHalf), z)
  // .multiply(apfloat.subtract(m1, m2));
  // }

  @Override
  public IExpr hypergeometric1F1(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      return valueOf(EvalEngine.getApfloat().hypergeometric1F1(fApcomplex,
          ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue()));
    }
    return IComplexNum.super.hypergeometric1F1(arg2, arg3);
  }

  @Override
  public IExpr hypergeometric2F1(IExpr arg2, IExpr arg3, IExpr arg4) {
    if (arg2 instanceof INumber && arg3 instanceof INumber && arg4 instanceof INumber) {
      return valueOf(
          EvalEngine.getApfloat().hypergeometric2F1(fApcomplex, ((INumber) arg2).apcomplexValue(),
              ((INumber) arg3).apcomplexValue(), ((INumber) arg4).apcomplexValue()));
    }
    return IComplexNum.super.hypergeometric2F1(arg2, arg3, arg4);
  }

  @Override
  public IExpr hypergeometricU(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      return valueOf(EvalEngine.getApfloat().hypergeometric1F1(fApcomplex,
          ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue()));
    }
    return IComplexNum.super.hypergeometricU(arg2, arg3);
  }

  @Override
  public IComplexNum add(final IComplexNum val) {
    return valueOf(EvalEngine.getApfloat().add(fApcomplex, val.apcomplexValue()));
  }

  public ApcomplexNum add(final ApcomplexNum that) {
    return valueOf(EvalEngine.getApfloat().add(fApcomplex, that.fApcomplex));
  }

  @Override
  public IComplexNum multiply(final IComplexNum val) {
    return valueOf(EvalEngine.getApfloat().multiply(fApcomplex, val.apcomplexValue()));
  }

  @Override
  public IExpr multiply(final IExpr that) {
    if (that instanceof IComplexNum) {
      return valueOf(
          EvalEngine.getApfloat().multiply(fApcomplex, ((IComplexNum) that).apcomplexValue()));
    }
    if (that instanceof INum) {
      return valueOf(EvalEngine.getApfloat().multiply(fApcomplex, ((INum) that).apcomplexValue()));
    }
    return IComplexNum.super.multiply(that);
  }

  @Override
  public IComplexNum pow(final IComplexNum val) {
    return valueOf(EvalEngine.getApfloat().pow(fApcomplex, val.apcomplexValue()));
  }

  @Override
  public IExpr power(final IExpr that) {
    if (that instanceof IComplexNum) {
      return valueOf(
          EvalEngine.getApfloat().pow(fApcomplex, ((IComplexNum) that).apcomplexValue()));
    }
    if (that instanceof INum) {
      return valueOf(EvalEngine.getApfloat().pow(fApcomplex, ((INum) that).apcomplexValue()));
    }
    return IComplexNum.super.power(that);
  }

  /** @return */
  @Override
  public IComplexNum conjugate() {
    return valueOf(EvalEngine.getApfloat().conj(fApcomplex));
  }

  @Override
  public IExpr copy() {
    return this;
  }

  public ApcomplexNum divide(final ApcomplexNum that) throws ArithmeticException {
    return valueOf(EvalEngine.getApfloat().divide(fApcomplex, that.fApcomplex));
  }

  @Override
  public IComplexNum divide(final IComplexNum val) {
    return valueOf(EvalEngine.getApfloat().divide(fApcomplex, val.apcomplexValue()));
  }

  @Override
  public IExpr divide(final IExpr that) {
    if (that.isZero()) {
      // Infinite expression `1` encountered.
      Errors.printMessage(S.Divide, "infy", F.list(F.Divide(this, that)), EvalEngine.get());
      return F.CComplexInfinity;
    }
    if (that instanceof IComplexNum) {
      return valueOf(
          EvalEngine.getApfloat().divide(fApcomplex, ((IComplexNum) that).apcomplexValue()));
    }
    if (that instanceof INum) {
      return valueOf(EvalEngine.getApfloat().divide(fApcomplex, ((INum) that).apcomplexValue()));
    }
    return IComplexNum.super.divide(that);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof ApcomplexNum) {
      return fApcomplex.equals(((ApcomplexNum) obj).fApcomplex);
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr evaluate(EvalEngine engine) {
    // if (fApcomplex.imag().equals(Apcomplex.ZERO)) {
    // return ApfloatNum.valueOf(fApcomplex.real());
    // }
    return F.NIL;
  }

  @Override
  public IExpr erf() {
    return valueOf(EvalEngine.getApfloat().erf(fApcomplex));
    // FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    // try {
    // Apcomplex erf = erf(fApcomplex, h);
    // return F.complexNum(erf);
    // } catch (Exception ce) {
    // //
    // }
    // return F.NIL;
  }

  // private static Apcomplex erf(Apcomplex x, FixedPrecisionApfloatHelper h) {
  // Apint two = new Apint(2);
  // Aprational oneHalf = new Aprational(Apint.ONE, new Apint(2));
  // // 3/2
  // Aprational threeHalf = new Aprational(new Apint(3), new Apint(2));
  // Apcomplex erf = h.hypergeometric1F1(oneHalf, threeHalf, h.multiply(x,
  // x).negate()).multiply(two)
  // .multiply(x).divide(h.sqrt(h.pi()));
  // return erf;
  // }

  @Override
  public IExpr erfc() {
    return valueOf(EvalEngine.getApfloat().erfc(fApcomplex));
    // FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    // try {
    // Apcomplex c = erf(fApcomplex, h);
    // return F.complexNum(h.subtract(Apcomplex.ONE, c));
    // } catch (Exception ce) {
    // //
    // }
    // return F.NIL;
  }

  @Override
  public INumber evalNumber() {
    return this;
  }

  @Override
  public boolean isSame(IExpr expression, double epsilon) {
    if (expression instanceof ApcomplexNum) {
      return fApcomplex.equals(((ApcomplexNum) expression).fApcomplex);
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public double dabs() {
    // if (isNaN()) {
    // return Double.NaN;
    // }
    //
    // if (isInfinite()) {
    // return Double.POSITIVE_INFINITY;
    // }

    if (Math.abs(reDoubleValue()) < Math.abs(imDoubleValue())) {
      if (fApcomplex.imag().signum() == 0) {
        // if (imDoubleValue() == 0.0) {
        return Math.abs(reDoubleValue());
      }
      Apfloat d = EvalEngine.getApfloat().divide(fApcomplex.real(), fApcomplex.imag());
      final double q = d.doubleValue();
      return (Math.abs(imDoubleValue()) * Math.sqrt(1 + q * q));
    } else {
      if (fApcomplex.real().signum() == 0) {
        // if (reDoubleValue() == 0.0) {
        return Math.abs(imDoubleValue());
      }
      Apfloat d = EvalEngine.getApfloat().divide(fApcomplex.imag(), fApcomplex.real());
      final double q = d.doubleValue();
      return (Math.abs(reDoubleValue()) * Math.sqrt(1 + q * q));
    }

  }

  @Override
  public IExpr complexArg() {
    try {
      return F.num(EvalEngine.getApfloat().arg(fApcomplex));
    } catch (ArithmeticException aex) {
      // Indeterminate expression `1` encountered.
      Errors.printMessage(S.Arg, "indet", F.list(F.Arg(this)), EvalEngine.get());
      return S.Indeterminate;
    }
  }

  /** {@inheritDoc} */
  @Override
  public int compareAbsValueToOne() {
    double temp = dabs();
    return Double.compare(temp, 1.0);
  }

  @Override
  public double imDoubleValue() {
    return fApcomplex.imag().doubleValue();
  }

  @Override
  public double reDoubleValue() {
    return fApcomplex.real().doubleValue();
  }

  @Override
  public final int hashCode() {
    return fApcomplex.hashCode();
  }

  @Override
  public long leafCountSimplify() {
    return 5;
  }

  /**
   * @param that
   * @return
   */
  public ApcomplexNum multiply(final ApcomplexNum that) {
    return valueOf(EvalEngine.getApfloat().multiply(fApcomplex, that.fApcomplex));
  }

  /** @return */
  @Override
  public ApcomplexNum negate() {
    return valueOf(EvalEngine.getApfloat().negate(fApcomplex));
  }

  /** @return */
  @Override
  public INumber opposite() {
    return valueOf(EvalEngine.getApfloat().negate(fApcomplex));
  }

  @Override
  public INumber plus(final INumber that) {
    return ApcomplexNum.valueOf(EvalEngine.getApfloat().add(fApcomplex, that.apcomplexValue()));
  }

  @Override
  public IInexactNumber plus(final IInexactNumber that) {
    return ApcomplexNum.valueOf(EvalEngine.getApfloat().add(fApcomplex, that.apcomplexValue()));
  }

  /**
   * @param that
   * @return
   */
  @Override
  public IExpr plus(final IExpr that) {
    if (that instanceof INumber) {
      return plus((INumber) that);
    }
    return IComplexNum.super.plus(that);
  }

  @Override
  public INumber inverse() {
    return valueOf(EvalEngine.getApfloat().inverseRoot(fApcomplex, 1));
  }

  /**
   * @param that
   * @return
   */
  public Apcomplex subtract(final Apcomplex that) {
    return EvalEngine.getApfloat().subtract(fApcomplex, that);
  }

  public ApcomplexNum subtract(final ApcomplexNum that) {
    return valueOf(EvalEngine.getApfloat().subtract(fApcomplex, that.fApcomplex));
  }

  @Override
  public IComplexNum subtract(final IComplexNum val) {
    return valueOf(EvalEngine.getApfloat().subtract(fApcomplex, ((ApcomplexNum) val).fApcomplex));
  }

  @Override
  public INumber times(final INumber that) {
    return ApcomplexNum
        .valueOf(EvalEngine.getApfloat().multiply(fApcomplex, that.apcomplexValue()));
  }

  @Override
  public IInexactNumber times(final IInexactNumber that) {
    return ApcomplexNum
        .valueOf(EvalEngine.getApfloat().multiply(fApcomplex, that.apcomplexValue()));
  }

  /**
   * @param that
   * @return
   */
  @Override
  public IExpr times(final IExpr that) {
    if (that instanceof INumber) {
      return times((INumber) that);
    }
    return IComplexNum.super.times(that);
  }

  @Override
  public IAST toPolarCoordinates() {
    return F.list(abs(), complexArg());
  }

  @Override
  public String toString() {
    try {
      StringBuilder sb = new StringBuilder();
      OutputFormFactory.get().convertApcomplex(sb, fApcomplex, Integer.MIN_VALUE,
          OutputFormFactory.NO_PLUS_CALL);
      return sb.toString();
    } catch (Exception e1) {
      // fall back to simple output format
    }

    String str = fApcomplex.toString();
    if (ParserConfig.EXPLICIT_TIMES_OPERATOR) {
      return str.replace("e", "E");
    }
    int index = str.indexOf('e');
    if (index > 0) {
      String exponentStr1 = str.substring(++index);
      str = str.replace("e", "*10^");
      int index2 = exponentStr1.indexOf('e');
      if (index2 > 0) {
        str = str.replace("e", "*10^");
      }
    }
    return str;
  }

  @Override
  public int complexSign() {
    final int i = fApcomplex.real().signum();
    if (i == 0) {
      return fApcomplex.imag().signum();
    }
    return i;
  }

  public int compareTo(final Apcomplex that) {
    // https://github.com/mtommila/apfloat/issues/27
    int result = fApcomplex.real().compareTo(that.real());
    if (result == 0) {
      result = ApfloatMath.abs(fApcomplex.imag()).compareTo(ApfloatMath.abs(that.imag()));
    }
    if (result == 0) {
      result = Integer.compare(fApcomplex.imag().signum(), that.imag().signum());
    }
    return result;
  }

  /**
   * Compares this expression with the specified expression for order. Returns a negative integer,
   * zero, or a positive integer as this expression is canonical less than, equal to, or greater
   * than the specified expression.
   */
  @Override
  public int compareTo(final IExpr expr) {
    if (expr.isNumber()) {
      if (expr instanceof ApcomplexNum) {
        return compareTo(((ApcomplexNum) expr).fApcomplex);
      }
      try {
        return compareTo(((INumber) expr).apcomplexValue());
      } catch (NumberFormatException nfe) {
        //
      }
    }
    return IExpr.compareHierarchy(this, expr);
  }

  @Override
  public ISymbol head() {
    return S.Complex;
  }

  public Apcomplex getComplex() {
    return fApcomplex;
  }

  public Complex getCMComplex() {
    return new Complex(fApcomplex.real().doubleValue(), fApcomplex.imag().doubleValue());
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
  public boolean equalsInt(int i) {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public IReal im() {
    return F.num(fApcomplex.imag());
  }

  /** {@inheritDoc} */
  @Override
  public IReal re() {
    return F.num(fApcomplex.real());
  }

  /** {@inheritDoc} */
  @Override
  public INumber roundExpr() {
    return F.CC(F.ZZ(NumberUtil.round(fApcomplex.real(), Config.ROUNDING_MODE)),
        F.ZZ(NumberUtil.round(fApcomplex.imag(), Config.ROUNDING_MODE)));
  }

  @Override
  public IExpr sqrt() {
    return valueOf(EvalEngine.getApfloat().sqrt(fApcomplex));
  }

  @Override
  public IExpr sqr() {
    return this.multiply(this);
  }

  @Override
  public INumber ceilFraction() throws ArithmeticException {
    return F.CC(F.ZZ(ApfloatMath.ceil(fApcomplex.real()).toBigInteger()),
        F.ZZ(ApfloatMath.ceil(fApcomplex.imag()).toBigInteger()));
  }

  /** {@inheritDoc} */
  @Override
  public INumber fractionalPart() {
    return F.complexNum(fApcomplex.real().frac(), fApcomplex.imag().frac());
  }

  @Override
  public INumber floorFraction() throws ArithmeticException {
    return F.CC(F.ZZ(ApfloatMath.floor(fApcomplex.real()).toBigInteger()),
        F.ZZ(ApfloatMath.floor(fApcomplex.imag()).toBigInteger()));
  }

  /** {@inheritDoc} */
  @Override
  public String fullFormString() {
    StringBuilder buf = new StringBuilder();
    long precision = fApcomplex.precision();
    String str = fApcomplex.real().toString();
    if (!ParserConfig.EXPLICIT_TIMES_OPERATOR) {
      int indx = str.indexOf("e");
      if (indx > 0) {
        str = str.substring(0, indx) + "`" + precision + "*^" + str.substring(indx + 1);
      } else {
        str = str + "`" + precision;
      }
    }
    buf.append(str);
    buf.append(',');
    str = fApcomplex.imag().toString();
    if (!ParserConfig.EXPLICIT_TIMES_OPERATOR) {
      int indx = str.indexOf("e");
      if (indx > 0) {
        str = str.substring(0, indx) + "``" + precision + "*^" + str.substring(indx + 1);
      } else {
        str = str + "`" + precision;
      }
    }
    buf.append(str);
    if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
      buf.append(')');
    } else {
      buf.append(']');
    }
    return buf.toString();
  }

  /** {@inheritDoc} */
  @Override
  public IComplex integerPart() {
    // isNegative() ? ceilFraction() : floorFraction();
    Apfloat re = fApcomplex.real();
    Apfloat im = fApcomplex.imag();
    IInteger reInt;
    IInteger imInt;
    if (re.signum() == -1) {
      // ceilFraction
      reInt = F.ZZ(ApfloatMath.ceil(re).toBigInteger());
    } else {
      // floorFraction
      reInt = F.ZZ(ApfloatMath.floor(re).toBigInteger());
    }
    if (im.signum() == -1) {
      // ceilFraction
      imInt = F.ZZ(ApfloatMath.ceil(im).toBigInteger());
    } else {
      // floorFraction
      imInt = F.ZZ(ApfloatMath.floor(im).toBigInteger());
    }
    return F.CC(reInt, imInt);
  }

  @Override
  public long precision() throws ApfloatRuntimeException {
    return fApcomplex.precision();
  }

  @Override
  public IExpr abs() {
    return ApfloatNum.valueOf(EvalEngine.getApfloat().abs(fApcomplex));
  }

  @Override
  public ApcomplexNum acos() {
    return valueOf(EvalEngine.getApfloat().acos(fApcomplex));
  }

  @Override
  public ApcomplexNum acosh() {
    return valueOf(EvalEngine.getApfloat().acosh(fApcomplex));
  }

  @Override
  public ApcomplexNum add(double value) {
    return valueOf(EvalEngine.getApfloat().divide(fApcomplex, new Apfloat(value)));
  }

  @Override
  public ApcomplexNum asin() {
    return valueOf(EvalEngine.getApfloat().asin(fApcomplex));
  }

  @Override
  public ApcomplexNum asinh() {
    return valueOf(EvalEngine.getApfloat().asinh(fApcomplex));
  }

  @Override
  public ApcomplexNum atan() {
    return valueOf(EvalEngine.getApfloat().atan(fApcomplex));
  }

  @Override
  public IExpr atan2(IExpr value) {
    try {
      if (value instanceof ApcomplexNum) {
        Apcomplex th = fApcomplex;
        Apcomplex x = ((ApcomplexNum) value).fApcomplex;

        // compute r = sqrt(x^2+y^2)
        FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
        final Apcomplex r = h.sqrt(h.add(h.multiply(x, x), h.multiply(th, th)));

        if (x.real().compareTo(Apfloat.ZERO) >= 0) {
          // compute atan2(y, x) = 2 atan(y / (r + x))
          return valueOf(h.multiply(h.atan(h.divide(th, h.add(r, x))), new Apfloat(2)));
        } else {
          // compute atan2(y, x) = +/- pi - 2 atan(y / (r - x))
          return valueOf(
              h.add(h.multiply(h.atan(h.divide(th, h.subtract(r, x))), new Apfloat(-2)), h.pi()));
        }
      }
      return IComplexNum.super.atan2(value);
    } catch (ArithmeticException aex) {
      // Indeterminate expression `1` encountered.
      Errors.printMessage(S.ArcTan, "indet", F.list(F.ArcTan(value, this)), EvalEngine.get());
      return S.Indeterminate;
    }
  }

  @Override
  public ApcomplexNum atanh() {
    return valueOf(EvalEngine.getApfloat().atanh(fApcomplex));
  }

  @Override
  public IExpr besselI(IExpr arg2) {
    if (arg2 instanceof INumber) {
      Apcomplex besselI =
          EvalEngine.getApfloat().besselI(apcomplexValue(), ((INumber) arg2).apcomplexValue());
      return F.complexNum(besselI);
    }
    return IComplexNum.super.besselI(arg2);
  }

  @Override
  public IExpr besselJ(IExpr arg2) {
    if (arg2 instanceof INumber) {
      Apcomplex besselJ =
          EvalEngine.getApfloat().besselI(apcomplexValue(), ((INumber) arg2).apcomplexValue());
      return F.complexNum(besselJ);
    }
    return IComplexNum.super.besselJ(arg2);
  }

  @Override
  public IExpr besselK(IExpr arg2) {
    if (arg2 instanceof INumber) {
      Apcomplex besselK =
          EvalEngine.getApfloat().besselK(apcomplexValue(), ((INumber) arg2).apcomplexValue());
      return F.complexNum(besselK);
    }
    return IComplexNum.super.besselK(arg2);
  }

  @Override
  public IExpr besselY(IExpr arg2) {
    if (arg2 instanceof INumber) {
      Apcomplex besselY =
          EvalEngine.getApfloat().besselY(apcomplexValue(), ((INumber) arg2).apcomplexValue());
      return F.complexNum(besselY);
    }
    return IComplexNum.super.besselY(arg2);
  }

  @Override
  public ApcomplexNum cbrt() {
    return valueOf(EvalEngine.getApfloat().cbrt(fApcomplex));
  }

  @Override
  public ApcomplexNum ceil() {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    return valueOf(h.ceil(fApcomplex.real()), //
        h.ceil(fApcomplex.imag()));
  }

  @Override
  public IExpr copySign(double d) {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    Apfloat sign = new Apfloat(d);
    return valueOf(h.copySign(fApcomplex.real(), sign), //
        h.copySign(fApcomplex.imag(), sign));
  }

  @Override
  public ApcomplexNum cos() {
    return valueOf(EvalEngine.getApfloat().cos(fApcomplex));
  }

  @Override
  public ApcomplexNum cosh() {
    return valueOf(EvalEngine.getApfloat().cosh(fApcomplex));
  }

  @Override
  public ApcomplexNum divide(double value) {
    return valueOf(EvalEngine.getApfloat().divide(fApcomplex, new Apfloat(value)));
  }

  /** {@inheritDoc} */
  @Override
  public ApcomplexNum expm1() {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    return valueOf(h.subtract(h.exp(fApcomplex), Apfloat.ONE));
  }

  @Override
  public IExpr floor() {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    return valueOf(h.floor(fApcomplex.real()), //
        h.floor(fApcomplex.imag()));
  }

  @Override
  public double getReal() {
    return fApcomplex.real().doubleValue();
  }

  @Override
  public ApcomplexNum exp() {
    return valueOf(EvalEngine.getApfloat().exp(fApcomplex));
  }

  @Override
  public ApcomplexNum log() {
    return valueOf(EvalEngine.getApfloat().log(fApcomplex));
  }

  @Override
  public ApcomplexNum log10() {
    return valueOf(EvalEngine.getApfloat().log(fApcomplex, new Apfloat(10)));
  }

  @Override
  public ApcomplexNum log1p() {
    return valueOf(EvalEngine.getApfloat().log(fApcomplex.add(Apfloat.ONE)));
  }

  @Override
  public ApcomplexNum multiply(double value) {
    return valueOf(EvalEngine.getApfloat().multiply(fApcomplex, new Apfloat(value)));
  }

  @Override
  public ApcomplexNum multiply(int value) {
    return valueOf(EvalEngine.getApfloat().multiply(fApcomplex, new Apfloat(value)));
  }

  @Override
  public ApcomplexNum newInstance(double d) {
    return valueOf(d);
  }

  @Override
  public IExpr pochhammer(IExpr arg2) {
    if (arg2 instanceof INumber) {
      Apcomplex pochhammer =
          EvalEngine.getApfloat().pochhammer(apcomplexValue(), ((INumber) arg2).apcomplexValue());
      return F.complexNum(pochhammer);
    }
    return IComplexNum.super.pochhammer(arg2);
  }

  @Override
  public IExpr pow(int n) {
    return valueOf(EvalEngine.getApfloat().pow(fApcomplex, n));
  }

  @Override
  public IExpr pow(double value) {
    return valueOf(EvalEngine.getApfloat().pow(fApcomplex, new Apfloat(value)));
  }

  @Override
  public ApcomplexNum reciprocal() {
    return valueOf(EvalEngine.getApfloat().inverseRoot(fApcomplex, 1));
  }

  @Override
  public IExpr remainder(double value) {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    return valueOf(h.mod(fApcomplex.real(), new Apfloat(value)), //
        h.mod(fApcomplex.imag(), new Apfloat(value)));
  }

  @Override
  public IExpr rint() {
    return valueOf( //
        ApfloatNum.apfloatRint(fApcomplex.real()), //
        ApfloatNum.apfloatRint(fApcomplex.imag()));
  }

  @Override
  public IExpr scalb(int n) {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    return valueOf(h.multiply(fApcomplex, h.pow(new Apfloat(2), n)));
  }

  @Override
  public IExpr rootN(int n) {
    return valueOf(EvalEngine.getApfloat().root(fApcomplex, n));
  }

  @Override
  public IExpr sign() {
    if (isNaN() || isZero()) {
      return this;
    }
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    return valueOf(h.divide(fApcomplex, h.abs(fApcomplex)));
  }

  @Override
  public ApcomplexNum sin() {
    return valueOf(EvalEngine.getApfloat().sin(fApcomplex));
  }

  @Override
  public ApcomplexNum sinh() {
    return valueOf(EvalEngine.getApfloat().sinh(fApcomplex));
  }

  @Override
  public IExpr subtract(double value) {
    return valueOf(EvalEngine.getApfloat().subtract(fApcomplex, new Apfloat(value)));
  }

  @Override
  public IExpr subtract(final IExpr that) {
    if (that instanceof IComplexNum) {
      return valueOf(
          EvalEngine.getApfloat().subtract(fApcomplex, ((IComplexNum) that).apcomplexValue()));
    }
    if (that instanceof INum) {
      return valueOf(EvalEngine.getApfloat().subtract(fApcomplex, ((INum) that).apcomplexValue()));
    }
    return IComplexNum.super.subtract(that);
  }

  @Override
  public ApcomplexNum tan() {
    return valueOf(EvalEngine.getApfloat().tan(fApcomplex));
  }

  @Override
  public ApcomplexNum tanh() {
    return valueOf(EvalEngine.getApfloat().tanh(fApcomplex));
  }

  @Override
  public IExpr ulp() {
    return valueOf(EvalEngine.getApfloat().ulp(Apfloat.ONE));
  }

  @Override
  public IExpr getPi() {
    return valueOf(EvalEngine.getApfloat().pi());
  }

  @Override
  public IExpr toDegrees() {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    return valueOf(ApfloatNum.toDegrees(fApcomplex.real(), h),
        ApfloatNum.toDegrees(fApcomplex.imag(), h));
  }

  @Override
  public IExpr toRadians() {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    return valueOf(ApfloatNum.toRadians(fApcomplex.real(), h),
        ApfloatNum.toRadians(fApcomplex.imag(), h));
  }

  @Override
  public IComplexNum zero() {
    return ZERO;
  }

  @Override
  public IComplexNum one() {
    return ONE;
  }
}

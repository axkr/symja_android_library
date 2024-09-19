package org.matheclipse.core.expression;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatArithmeticException;
import org.apfloat.ApfloatMath;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.apfloat.InfiniteExpansionException;
import org.apfloat.LossOfPrecisionException;
import org.apfloat.NumericComputationException;
import org.apfloat.OverflowException;
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

  /** */
  private static final long serialVersionUID = -6033055105824482264L;

  /** The square root of -1. A number representing "0.0 + 1.0i" */
  public static final ApcomplexNum I = new ApcomplexNum(Apcomplex.I);

  /** A complex number representing "1.0 + 0.0i" */
  public static final ApcomplexNum ONE = new ApcomplexNum(Apcomplex.ONE);

  /** A complex number representing "-1.0 + 0.0i" */
  public static final ApcomplexNum MINUS_ONE = new ApcomplexNum(new Apfloat(-1));

  /** A complex number representing "0.0 + 0.0i" */
  public static final ApcomplexNum ZERO = new ApcomplexNum(Apcomplex.ZERO);

  public static ApcomplexNum valueOf(final Apcomplex value) {
    return new ApcomplexNum(value);
  }

  public static ApcomplexNum valueOf(final Apfloat real) {
    return new ApcomplexNum(real);
  }

  public static ApcomplexNum valueOf(final Apfloat real, final Apfloat imag) {
    return new ApcomplexNum(real, imag);
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

  public static ApcomplexNum valueOf(final Complex c, long precision) {
    return valueOf(new Apcomplex(new Apfloat(new BigDecimal(c.getReal()), precision),
        new Apfloat(new BigDecimal(c.getImaginary()), precision)));
  }

  /** A complex number representing "NaN + NaNi" */
  // public static final ApfloatComplexNum NaN = valueOf(Double.NaN,
  // Double.NaN);

  public static ApcomplexNum valueOf(final double real) {
    return valueOf(EvalEngine.getApfloat().valueOf(new Apcomplex(new Apfloat(real))));
  }

  public static ApcomplexNum valueOf(final double real, final double imaginary) {
    return valueOf(
        EvalEngine.getApfloat().valueOf(new Apcomplex(new Apfloat(real), new Apfloat(imaginary))));
  }

  public static ApcomplexNum valueOf(final String realPart, final String imaginaryPart,
      long precision) {
    return new ApcomplexNum(realPart, imaginaryPart, precision);
  }

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

  @Override
  public IExpr abs() {
    return ApfloatNum.valueOf(EvalEngine.getApfloat().abs(fApcomplex));
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
  public ApcomplexNum acos() {
    return valueOf(EvalEngine.getApfloat().acos(fApcomplex));
  }

  @Override
  public ApcomplexNum acosh() {
    return valueOf(EvalEngine.getApfloat().acosh(fApcomplex));
  }

  public ApcomplexNum add(final ApcomplexNum that) {
    return valueOf(EvalEngine.getApfloat().add(fApcomplex, that.fApcomplex));
  }

  @Override
  public ApcomplexNum add(double value) {
    return valueOf(EvalEngine.getApfloat().divide(fApcomplex, new Apfloat(value)));
  }

  @Override
  public IComplexNum add(final IComplexNum val) {
    return valueOf(EvalEngine.getApfloat().add(fApcomplex, val.apcomplexValue()));
  }

  @Override
  public IExpr agm(IExpr arg2) {
    if (arg2 instanceof INumber) {
      return valueOf(EvalEngine.getApfloat().agm(fApcomplex, ((INumber) arg2).apcomplexValue()));
    }
    return IComplexNum.super.agm(arg2);
  }

  @Override
  public IExpr airyAi() {
    return valueOf(EvalEngine.getApfloat().airyAi(fApcomplex));
  }

  @Override
  public IExpr airyAiPrime() {
    return valueOf(EvalEngine.getApfloat().airyAiPrime(fApcomplex));
  }

  @Override
  public IExpr airyBi() {
    return valueOf(EvalEngine.getApfloat().airyBi(fApcomplex));
  }

  @Override
  public IExpr airyBiPrime() {
    return valueOf(EvalEngine.getApfloat().airyBiPrime(fApcomplex));
  }

  @Override
  public ApcomplexNum apcomplexNumValue() {
    return this;
  }

  @Override
  public Apcomplex apcomplexValue() {
    return fApcomplex;
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
  public IExpr beta(IExpr b) {
    if (b instanceof INumber) {
      try {
        return valueOf(EvalEngine.getApfloat().beta(fApcomplex, ((INumber) b).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // java.lang.ArithmeticException: Beta is infinite
      }
    }
    return IComplexNum.super.beta(b);
  }

  @Override
  public IExpr beta(IExpr a, IExpr b) {
    if (a instanceof INumber && b instanceof INumber) {
      try {
        return valueOf(EvalEngine.getApfloat().beta(fApcomplex, ((INumber) a).apcomplexValue(),
            ((INumber) b).apcomplexValue()));
      } catch (ApfloatArithmeticException aaex) {
        if ("divide.byZero".equals(aaex.getLocalizationKey())) {
          return F.ComplexInfinity;
        }
      } catch (ArithmeticException | NumericComputationException aex) {
        if ("Division by zero".equals(aex.getMessage())) {
          return F.ComplexInfinity;
        }
      }
    }
    return IComplexNum.super.beta(a, b);
  }

  @Override
  public IExpr beta(IExpr x2, IExpr a, IExpr b) {
    if (x2 instanceof INumber && a instanceof INumber && b instanceof INumber) {
      try {
        return valueOf(EvalEngine.getApfloat().beta(fApcomplex, ((INumber) x2).apcomplexValue(),
            ((INumber) a).apcomplexValue(), ((INumber) b).apcomplexValue()));
      } catch (NumericComputationException e) {
        //
      } catch (ApfloatArithmeticException aaex) {
        if ("divide.byZero".equals(aaex.getLocalizationKey())) {
          return F.ComplexInfinity;
        }
      } catch (ArithmeticException aex) {
        if ("Division by zero".equals(aex.getMessage())) {
          return F.ComplexInfinity;
        }
      }
    }
    return IComplexNum.super.beta(x2, a, b);
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
  public INumber ceilFraction() throws ArithmeticException {
    return F.CC(F.ZZ(ApfloatMath.ceil(fApcomplex.real()).toBigInteger()),
        F.ZZ(ApfloatMath.ceil(fApcomplex.imag()).toBigInteger()));
  }

  @Override
  public IExpr chebyshevT(IExpr arg2) {
    if (arg2 instanceof INumber) {
      // if (arg2 instanceof IReal) {
      // try {
      // Apcomplex chebyshevT =
      // EvalEngine.getApfloat().chebyshevT(apcomplexValue(), ((IReal) arg2).apfloatValue());
      // return F.complexNum(chebyshevT);
      // } catch (NumericComputationException are) {
      //
      // }
      // }
      try {
        Apcomplex chebyshevT =
            EvalEngine.getApfloat().chebyshevT(apcomplexValue(), ((INumber) arg2).apcomplexValue());
        return F.complexNum(chebyshevT);
      } catch (NumericComputationException are) {

      }
    }
    return IComplexNum.super.chebyshevT(arg2);
  }

  @Override
  public IExpr chebyshevU(IExpr arg2) {
    if (arg2 instanceof INumber) {
      // if (arg2 instanceof IReal) {
      // Apcomplex chebyshevU =
      // EvalEngine.getApfloat().chebyshevU(apcomplexValue(), ((IReal) arg2).apfloatValue());
      // return F.complexNum(chebyshevU);
      // }
      try {
        Apcomplex chebyshevU =
            EvalEngine.getApfloat().chebyshevU(apcomplexValue(), ((INumber) arg2).apcomplexValue());
        return F.complexNum(chebyshevU);
      } catch (ArithmeticException | NumericComputationException are) {

      }
    }
    return IComplexNum.super.chebyshevU(arg2);
  }

  /** {@inheritDoc} */
  @Override
  public int compareAbsValueToOne() {
    double temp = dabs();
    return Double.compare(temp, 1.0);
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
  public IExpr complexArg() {
    try {
      return F.num(EvalEngine.getApfloat().arg(fApcomplex));
    } catch (ArithmeticException | NumericComputationException ex) {
      // Indeterminate expression `1` encountered.
      Errors.printMessage(S.Arg, "indet", F.list(F.Arg(this)), EvalEngine.get());
      return S.Indeterminate;
    }
  }

  @Override
  public ComplexNum complexNumValue() {
    return ComplexNum.valueOf(fApcomplex.real().doubleValue(), fApcomplex.imag().doubleValue());
  }

  @Override
  public int complexSign() {
    final int signum = fApcomplex.real().signum();
    return (signum == 0) ? fApcomplex.imag().signum() : signum;
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
  public IExpr coshIntegral() {
    return valueOf(EvalEngine.getApfloat().coshIntegral(fApcomplex));
  }

  @Override
  public IExpr cosIntegral() {
    return valueOf(EvalEngine.getApfloat().cosIntegral(fApcomplex));
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

  @Override
  public IExpr digamma() {
    try {
      return valueOf(EvalEngine.getApfloat().digamma(fApcomplex));
    } catch (ArithmeticException | NumericComputationException aex) {
    }
    return IComplexNum.super.digamma();
  }

  public ApcomplexNum divide(final ApcomplexNum that) throws ArithmeticException {
    return valueOf(EvalEngine.getApfloat().divide(fApcomplex, that.fApcomplex));
  }

  @Override
  public ApcomplexNum divide(double value) {
    return valueOf(EvalEngine.getApfloat().divide(fApcomplex, new Apfloat(value)));
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
  public IExpr ellipticE() {
    return valueOf(EvalEngine.getApfloat().ellipticE(fApcomplex));
  }

  @Override
  public IExpr ellipticK() {
    return valueOf(EvalEngine.getApfloat().ellipticK(fApcomplex));
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

  @Override
  public boolean equalsInt(int i) {
    return false;
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

  @Override
  public IExpr erfc() {
    try {
      return valueOf(EvalEngine.getApfloat().erfc(fApcomplex));
    } catch (OverflowException of) {
      // return Underflow? https://github.com/mtommila/apfloat/issues/38
      return F.Overflow();
    } catch (ArithmeticException | NumericComputationException e) {
      e.printStackTrace();
    }
    return IComplexNum.super.erfc();
  }

  @Override
  public IExpr erfi() {
    return valueOf(EvalEngine.getApfloat().erfi(fApcomplex));
  }

  @Override
  public INumber evalNumber() {
    return this;
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
  public ApcomplexNum exp() {
    return valueOf(EvalEngine.getApfloat().exp(fApcomplex));
  }

  @Override
  public IExpr expIntegralE(IExpr z) {
    if (z instanceof INumber) {
      return valueOf(
          EvalEngine.getApfloat().expIntegralE(fApcomplex, ((INumber) z).apcomplexValue()));
    }
    return IComplexNum.super.expIntegralE(z);
  }

  @Override
  public IExpr expIntegralEi() {
    return valueOf(EvalEngine.getApfloat().expIntegralEi(fApcomplex));
  }

  /** {@inheritDoc} */
  @Override
  public ApcomplexNum expm1() {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    return valueOf(h.subtract(h.exp(fApcomplex), Apfloat.ONE));
  }

  @Override
  public IExpr factorial() {
    try {
      FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
      return valueOf(h.gamma(h.add(fApcomplex, Apfloat.ONE)));
    } catch (ArithmeticException | NumericComputationException e) {
      // try as computation with complex numbers
    }
    return IComplexNum.super.factorial();
  }

  @Override
  public IExpr fibonacci(IExpr arg2) {
    if (arg2 instanceof INumber) {
      try {
        return F.complexNum(
            EvalEngine.getApfloat().fibonacci(fApcomplex, ((INumber) arg2).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return IComplexNum.super.fibonacci(arg2);
  }

  @Override
  public IExpr floor() {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    return valueOf(h.floor(fApcomplex.real()), //
        h.floor(fApcomplex.imag()));
  }

  @Override
  public INumber floorFraction() throws ArithmeticException {
    return F.CC(F.ZZ(ApfloatMath.floor(fApcomplex.real()).toBigInteger()),
        F.ZZ(ApfloatMath.floor(fApcomplex.imag()).toBigInteger()));
  }

  /** {@inheritDoc} */
  @Override
  public INumber fractionalPart() {
    return F.complexNum(fApcomplex.real().frac(), fApcomplex.imag().frac());
  }

  @Override
  public IExpr fresnelC() {
    return valueOf(EvalEngine.getApfloat().fresnelC(fApcomplex));
    // return valueOf(fresnelC(fApcomplex, EvalEngine.getApfloat()));
  }

  @Override
  public IExpr fresnelS() {
    return valueOf(EvalEngine.getApfloat().fresnelS(fApcomplex));
    // return valueOf(fresnelS(fApcomplex, EvalEngine.getApfloat()));
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

  @Override
  public IExpr gamma() {
    if (isZero() || isMathematicalIntegerNegative()) {
      return F.CComplexInfinity;
    }
    try {
      return valueOf(EvalEngine.getApfloat().gamma(fApcomplex));
    } catch (OverflowException of) {
      return F.Overflow();
    } catch (ArithmeticException | NumericComputationException are) {
      return Errors.printMessage(S.Gamma, are, EvalEngine.get());
    }
    // return IComplexNum.super.gamma();
  }

  @Override
  public IExpr gamma(IExpr x) {
    if (isZero() && x.isZero()) {
      return F.CInfinity;
    }
    // if (x instanceof IReal) {
    // try {
    // return valueOf(EvalEngine.getApfloat().gamma(fApcomplex, ((IReal) x).apfloatValue()));
    // } catch (ArithmeticException | NumericComputationException e) {
    // // try as computation with complex numbers
    // }
    // }
    if (x instanceof INumber) {
      try {
        return F
            .complexNum(EvalEngine.getApfloat().gamma(fApcomplex, ((INumber) x).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException are) {
        // Upper gamma with first argument real part non-positive and second argment zero
        return Errors.printMessage(S.Gamma, are, EvalEngine.get());
      }
    }
    return IComplexNum.super.gamma(x);
  }

  @Override
  public IExpr gamma(IExpr x0, IExpr x1) {
    if (isZero()) {
      if (x0.isZero()) {
        return F.CComplexInfinity;
      }
      if (x1.isZero()) {
        return F.CNInfinity;
      }
    }
    // if (x0 instanceof IReal && x1 instanceof IReal) {
    // try {
    // return valueOf(EvalEngine.getApfloat().gamma(fApcomplex, ((IReal) x0).apfloatValue(),
    // ((IReal) x1).apfloatValue()));
    // } catch (ArithmeticException | NumericComputationException e) {
    // // try as computation with complex numbers
    // }
    // }
    if (x0 instanceof INumber && x1 instanceof INumber) {
      try {
        return F.complexNum(EvalEngine.getApfloat().gamma(fApcomplex,
            ((INumber) x0).apcomplexValue(), ((INumber) x1).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException are) {
        return Errors.printMessage(S.Gamma, are, EvalEngine.get());
      }

    }
    return IComplexNum.super.gamma(x0, x1);
  }

  @Override
  public IExpr gegenbauerC(IExpr arg2) {
    if (arg2 instanceof INumber) {
      try {
        return F.complexNum(
            EvalEngine.getApfloat().gegenbauerC(fApcomplex, ((INumber) arg2).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return IComplexNum.super.gegenbauerC(arg2);
  }

  @Override
  public IExpr gegenbauerC(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      try {
        return F.complexNum(EvalEngine.getApfloat().gegenbauerC(fApcomplex,
            ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return IComplexNum.super.gegenbauerC(arg2, arg3);
  }

  public Complex getCMComplex() {
    return new Complex(fApcomplex.real().doubleValue(), fApcomplex.imag().doubleValue());
  }

  public Apcomplex getComplex() {
    return fApcomplex;
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
  public IExpr getPi() {
    return valueOf(EvalEngine.getApfloat().pi());
  }

  @Override
  public double getReal() {
    return fApcomplex.real().doubleValue();
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
  public IExpr harmonicNumber() {
    Apcomplex harmonicNumber = EvalEngine.getApfloat().harmonicNumber(fApcomplex);
    return valueOf(harmonicNumber);
  }

  @Override
  public IExpr harmonicNumber(IExpr r) {
    if (r instanceof INumber) {
      try {
        FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
        Apcomplex harmonicNumber = h.harmonicNumber(fApcomplex, ((INumber) r).apcomplexValue());
        return valueOf(harmonicNumber);
      } catch (ArithmeticException | NumericComputationException aex) {

      }
    }
    return IComplexNum.super.harmonicNumber(r);
  }

  @Override
  public final int hashCode() {
    return fApcomplex.hashCode();
  }

  @Override
  public ISymbol head() {
    return S.Complex;
  }

  @Override
  public IExpr hermiteH(IExpr arg2) {
    if (arg2 instanceof INumber) {
      try {
        Apcomplex hermiteH =
            EvalEngine.getApfloat().hermiteH(apcomplexValue(), ((INumber) arg2).apcomplexValue());
        return F.complexNum(hermiteH);
      } catch (ArithmeticException | NumericComputationException are) {

      }
    }
    return IComplexNum.super.hermiteH(arg2);
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
  public IExpr hypergeometric0F1Regularized(IExpr arg2) {
    if (arg2 instanceof INumber) {
      return valueOf(EvalEngine.getApfloat().hypergeometric0F1Regularized(fApcomplex,
          ((INumber) arg2).apcomplexValue()));
    }
    return IComplexNum.super.hypergeometric0F1Regularized(arg2);
  }

  @Override
  public IExpr hypergeometric1F1(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      return valueOf(EvalEngine.getApfloat().hypergeometric1F1(fApcomplex,
          ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue()));
    }
    return IComplexNum.super.hypergeometric1F1(arg2, arg3);
  }

  @Override
  public IExpr hypergeometric1F1Regularized(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      return valueOf(EvalEngine.getApfloat().hypergeometric1F1Regularized(fApcomplex,
          ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue()));
    }
    return IComplexNum.super.hypergeometric1F1Regularized(arg2, arg3);
  }

  @Override
  public IExpr hypergeometric2F1(IExpr arg2, IExpr arg3, IExpr arg4) {
    if (arg2 instanceof INumber && arg3 instanceof INumber && arg4 instanceof INumber) {
      try {
        return valueOf(
            EvalEngine.getApfloat().hypergeometric2F1(fApcomplex, ((INumber) arg2).apcomplexValue(),
                ((INumber) arg3).apcomplexValue(), ((INumber) arg4).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return IComplexNum.super.hypergeometric2F1(arg2, arg3, arg4);
  }

  @Override
  public IExpr hypergeometric2F1Regularized(IExpr arg2, IExpr arg3, IExpr arg4) {
    if (arg2 instanceof INumber && arg3 instanceof INumber && arg4 instanceof INumber) {
      try {
        return valueOf(EvalEngine.getApfloat().hypergeometric2F1Regularized(fApcomplex,
            ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue(),
            ((INumber) arg4).apcomplexValue()));
      } catch (NumericComputationException ex) {
        // org.apfloat.OverflowException: Apfloat disk file storage is disabled
      }
    }
    return IComplexNum.super.hypergeometric2F1Regularized(arg2, arg3, arg4);
  }

  @Override
  public IExpr hypergeometricU(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      return valueOf(EvalEngine.getApfloat().hypergeometric1F1(fApcomplex,
          ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue()));
    }
    return IComplexNum.super.hypergeometricU(arg2, arg3);
  }

  /** {@inheritDoc} */
  @Override
  public IReal im() {
    return F.num(fApcomplex.imag());
  }

  @Override
  public double imDoubleValue() {
    return fApcomplex.imag().doubleValue();
  }

  /** {@inheritDoc} */
  @Override
  public IExpr inc() {
    return add(ONE);
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
  public INumber inverse() {
    return valueOf(EvalEngine.getApfloat().inverseRoot(fApcomplex, 1));
  }

  @Override
  public boolean isMinusOne() {
    return fApcomplex.imag().isZero() && fApcomplex.real().compareTo(ApfloatNum.MINUS_ONE) == 0;
  }

  @Override
  public boolean isOne() {
    return fApcomplex.imag().isZero() && fApcomplex.real().compareTo(Apfloat.ONE) == 0;
  }

  @Override
  public boolean isSame(IExpr expression, double epsilon) {
    if (expression instanceof ApcomplexNum) {
      return fApcomplex.equals(((ApcomplexNum) expression).fApcomplex);
    }
    return false;
  }

  @Override
  public boolean isZero() {
    return fApcomplex.isZero();
  }

  @Override
  public boolean isZero(double tolerance) {
    return F.isZero(fApcomplex.real().doubleValue(), tolerance) && //
        F.isZero(fApcomplex.imag().doubleValue(), tolerance);
  }

  @Override
  public IExpr jacobiP(IExpr arg2, IExpr arg3, IExpr arg4) {
    if (arg2 instanceof INumber && arg3 instanceof INumber && arg4 instanceof INumber) {
      try {
        return valueOf(
            EvalEngine.getApfloat().jacobiP(fApcomplex, ((INumber) arg2).apcomplexValue(),
                ((INumber) arg3).apcomplexValue(), ((INumber) arg4).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return IComplexNum.super.jacobiP(arg2, arg3, arg4);
  }

  @Override
  public IExpr laguerreL(IExpr arg2) {
    if (arg2 instanceof INumber) {
      try {
        return F.complexNum(
            EvalEngine.getApfloat().laguerreL(fApcomplex, ((INumber) arg2).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return IComplexNum.super.laguerreL(arg2);
  }

  @Override
  public IExpr laguerreL(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      try {
        return F.complexNum(EvalEngine.getApfloat().laguerreL(fApcomplex,
            ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return IComplexNum.super.laguerreL(arg2, arg3);
  }

  @Override
  public long leafCountSimplify() {
    return 5;
  }

  @Override
  public IExpr legendreP(IExpr arg2) {
    if (arg2 instanceof INumber) {
      try {
        return F.complexNum(
            EvalEngine.getApfloat().legendreP(fApcomplex, ((INumber) arg2).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return IComplexNum.super.legendreP(arg2);
  }

  @Override
  public IExpr legendreP(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      try {
        return F.complexNum(EvalEngine.getApfloat().legendreP(fApcomplex,
            ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return IComplexNum.super.legendreP(arg2, arg3);
  }

  @Override
  public IExpr legendreQ(IExpr arg2) {
    if (arg2 instanceof INumber) {
      try {
        return F.complexNum(
            EvalEngine.getApfloat().legendreQ(fApcomplex, ((INumber) arg2).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return IComplexNum.super.legendreQ(arg2);
  }

  @Override
  public IExpr legendreQ(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      try {
        return F.complexNum(EvalEngine.getApfloat().legendreQ(fApcomplex,
            ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return IComplexNum.super.legendreQ(arg2, arg3);
  }

  @Override
  public ApcomplexNum log() {
    return valueOf(EvalEngine.getApfloat().log(fApcomplex));
  }

  @Override
  public IExpr log(final IExpr base) {
    if (base instanceof INumber) {
      return ApcomplexNum.valueOf(
          EvalEngine.getApfloat().log(apcomplexValue(), ((INumber) base).apcomplexValue()));
    }
    return IComplexNum.super.log(base);
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
  public IExpr logGamma() {
    try {
      Apcomplex logGamma = EvalEngine.getApfloat().logGamma(fApcomplex);
      return F.complexNum(logGamma);
    } catch (ApfloatArithmeticException aaex) {
      String localizationKey = aaex.getLocalizationKey();
      if ("logGamma.ofZero".equals(localizationKey)) {
        return F.CInfinity;
      }
      if ("logGamma.ofNegativeInteger".equals(localizationKey)) {
        return F.CInfinity;
      }
      aaex.printStackTrace();
    }
    return IComplexNum.super.logGamma();
  }

  @Override
  public IExpr logIntegral() {
    return valueOf(EvalEngine.getApfloat().logIntegral(fApcomplex));
  }

  @Override
  public IExpr logisticSigmoid() {
    return valueOf(EvalEngine.getApfloat().logisticSigmoid(fApcomplex));
  }

  /**
   * @param that
   * @return
   */
  public ApcomplexNum multiply(final ApcomplexNum that) {
    return valueOf(EvalEngine.getApfloat().multiply(fApcomplex, that.fApcomplex));
  }

  @Override
  public ApcomplexNum multiply(double value) {
    return valueOf(EvalEngine.getApfloat().multiply(fApcomplex, new Apfloat(value)));
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
  public ApcomplexNum multiply(int value) {
    return valueOf(EvalEngine.getApfloat().multiply(fApcomplex, new Apfloat(value)));
  }

  /** @return */
  @Override
  public ApcomplexNum negate() {
    return valueOf(EvalEngine.getApfloat().negate(fApcomplex));
  }

  @Override
  public ApcomplexNum newInstance(double d) {
    return valueOf(d);
  }

  @Override
  public IComplexNum one() {
    return ONE;
  }

  /** @return */
  @Override
  public INumber opposite() {
    return valueOf(EvalEngine.getApfloat().negate(fApcomplex));
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
  public IInexactNumber plus(final IInexactNumber that) {
    return ApcomplexNum.valueOf(EvalEngine.getApfloat().add(fApcomplex, that.apcomplexValue()));
  }

  @Override
  public INumber plus(final INumber that) {
    return ApcomplexNum.valueOf(EvalEngine.getApfloat().add(fApcomplex, that.apcomplexValue()));
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
  public IExpr polyGamma(long n) {
    try {
      Apcomplex polygamma = EvalEngine.getApfloat().polygamma(n, fApcomplex);
      return F.complexNum(polygamma);
    } catch (ArithmeticException | NumericComputationException aex) {
      // java.lang.ArithmeticException: Polygamma of nonpositive integer
    }
    return IComplexNum.super.polyGamma(n);
  }

  @Override
  public IExpr polyLog(IExpr arg2) {
    if (arg2 instanceof INumber) {
      try {
        return valueOf(
            EvalEngine.getApfloat().polylog(fApcomplex, ((INumber) arg2).apcomplexValue()));
      } catch (LossOfPrecisionException lope) {
        // Complete loss of precision
      } catch (InfiniteExpansionException iee) {
        // Cannot calculate power to infinite precision
      } catch (ArithmeticException | NumericComputationException ex) {
      }
    }
    return IComplexNum.super.polyLog(arg2);
  }

  @Override
  public IExpr pow(double value) {
    return valueOf(EvalEngine.getApfloat().pow(fApcomplex, new Apfloat(value)));
  }

  @Override
  public IComplexNum pow(final IComplexNum val) {
    return valueOf(EvalEngine.getApfloat().pow(fApcomplex, val.apcomplexValue()));
  }

  @Override
  public IExpr pow(int n) {
    return valueOf(EvalEngine.getApfloat().pow(fApcomplex, n));
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

  @Override
  public long precision() throws NumericComputationException {
    return fApcomplex.precision();
  }

  /** {@inheritDoc} */
  @Override
  public IReal re() {
    return F.num(fApcomplex.real());
  }

  @Override
  public ApcomplexNum reciprocal() {
    return valueOf(EvalEngine.getApfloat().inverseRoot(fApcomplex, 1));
  }

  @Override
  public double reDoubleValue() {
    return fApcomplex.real().doubleValue();
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
  public IExpr rootN(int n) {
    return valueOf(EvalEngine.getApfloat().root(fApcomplex, n));
  }

  /** {@inheritDoc} */
  @Override
  public INumber roundExpr() {
    return F.CC(F.ZZ(NumberUtil.round(fApcomplex.real(), Config.ROUNDING_MODE)),
        F.ZZ(NumberUtil.round(fApcomplex.imag(), Config.ROUNDING_MODE)));
  }

  @Override
  public IExpr scalb(int n) {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    return valueOf(h.multiply(fApcomplex, h.pow(new Apfloat(2), n)));
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
  public ApcomplexNum sinc() {
    return valueOf(EvalEngine.getApfloat().sinc(fApcomplex));
  }

  @Override
  public ApcomplexNum sinh() {
    return valueOf(EvalEngine.getApfloat().sinh(fApcomplex));
  }

  @Override
  public IExpr sinhIntegral() {
    return valueOf(EvalEngine.getApfloat().sinhIntegral(fApcomplex));
  }

  @Override
  public IExpr sinIntegral() {
    return valueOf(EvalEngine.getApfloat().sinIntegral(fApcomplex));
  }

  @Override
  public IExpr sqr() {
    return this.multiply(this);
  }

  @Override
  public IExpr sqrt() {
    return valueOf(EvalEngine.getApfloat().sqrt(fApcomplex));
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
  public IExpr subtract(double value) {
    return valueOf(EvalEngine.getApfloat().subtract(fApcomplex, new Apfloat(value)));
  }

  @Override
  public IComplexNum subtract(final IComplexNum val) {
    return valueOf(EvalEngine.getApfloat().subtract(fApcomplex, ((ApcomplexNum) val).fApcomplex));
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
  public IInexactNumber times(final IInexactNumber that) {
    return ApcomplexNum
        .valueOf(EvalEngine.getApfloat().multiply(fApcomplex, that.apcomplexValue()));
  }

  @Override
  public INumber times(final INumber that) {
    return ApcomplexNum
        .valueOf(EvalEngine.getApfloat().multiply(fApcomplex, that.apcomplexValue()));
  }

  @Override
  public IExpr toDegrees() {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    return valueOf(ApfloatNum.toDegrees(fApcomplex.real(), h),
        ApfloatNum.toDegrees(fApcomplex.imag(), h));
  }

  @Override
  public IAST toPolarCoordinates() {
    return F.list(abs(), complexArg());
  }

  @Override
  public IExpr toRadians() {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    return valueOf(ApfloatNum.toRadians(fApcomplex.real(), h),
        ApfloatNum.toRadians(fApcomplex.imag(), h));
  }

  @Override
  public String toString() {
    try {
      StringBuilder sb = new StringBuilder();
      OutputFormFactory.get().convertApcomplex(sb, fApcomplex, Integer.MIN_VALUE,
          OutputFormFactory.NO_PLUS_CALL);
      return sb.toString();
    } catch (Exception e1) {
      Errors.rethrowsInterruptException(e1);
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
  public IExpr ulp() {
    return valueOf(EvalEngine.getApfloat().ulp(Apfloat.ONE));
  }

  @Override
  public IComplexNum zero() {
    return ZERO;
  }
}

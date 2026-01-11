package org.matheclipse.core.expression;

import java.util.function.Function;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatArithmeticException;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.apfloat.InfiniteExpansionException;
import org.apfloat.LossOfPrecisionException;
import org.apfloat.NumericComputationException;
import org.apfloat.OverflowException;
import org.hipparchus.complex.Complex;
import org.hipparchus.exception.NullArgumentException;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.util.SourceCodeProperties;
import org.matheclipse.core.form.DoubleToMMA;
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
import org.matheclipse.core.numerics.functions.BesselJS;
import org.matheclipse.core.numerics.functions.GammaJS;
import org.matheclipse.core.numerics.functions.HypergeometricJS;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;
import org.matheclipse.parser.client.ParserConfig;
import com.google.common.math.DoubleMath;

/**
 * <code>IComplexNum</code> implementation which wraps a {@link Complex} value to represent a
 * numeric complex floating-point number.
 */
public class ComplexNum implements IComplexNum {

  private final static int G = 7;

  private final static org.hipparchus.complex.Complex[] P_COMPLEX =
      new org.hipparchus.complex.Complex[] {new org.hipparchus.complex.Complex(0.99999999999980993), //
          new org.hipparchus.complex.Complex(676.5203681218851), //
          new org.hipparchus.complex.Complex(-1259.1392167224028), //
          new org.hipparchus.complex.Complex(771.32342877765313), //
          new org.hipparchus.complex.Complex(-176.61502916214059), //
          new org.hipparchus.complex.Complex(12.507343278686905), //
          new org.hipparchus.complex.Complex(-0.13857109526572012), //
          new org.hipparchus.complex.Complex(9.9843695780195716e-6), //
          new org.hipparchus.complex.Complex(1.5056327351493116e-7) //
      };
  /** */
  private static final long serialVersionUID = -6033055105824482264L;

  /** The square root of -1. A number representing "0.0 + 1.0i" */
  public static final ComplexNum I = new ComplexNum(Complex.I);

  public static final ComplexNum INF = new ComplexNum(Complex.INF);

  /** A complex number representing "NaN + NaNi" */
  public static final ComplexNum NaN = new ComplexNum(Complex.NaN);

  /** The square root of -1. A number representing "0.0 - 1.0i" */
  public static final ComplexNum NI = new ComplexNum(Complex.MINUS_I);

  /** A complex number representing "-1.0 + 0.0i" */
  public static final ComplexNum MINUS_ONE = new ComplexNum(Complex.MINUS_ONE);

  /** A complex number representing "1.0 + 0.0i" */
  public static final ComplexNum ONE = new ComplexNum(Complex.ONE);

  /** A complex number representing "0.0 + 0.0i" */
  public static final ComplexNum ZERO = new ComplexNum(Complex.ZERO);

  /**
   * Return the absolute value of this complex number. Returns {@code NaN} if either real or
   * imaginary part is {@code NaN} and {@code Double.POSITIVE_INFINITY} if neither part is {@code
   * NaN}, but at least one part is infinite.
   *
   * @return the absolute value.
   */
  public static double dabs(Complex c) {
    if (c.isNaN()) {
      return Double.NaN;
    }

    if (c.isInfinite()) {
      return Double.POSITIVE_INFINITY;
    }

    if (Math.abs(c.getReal()) < Math.abs(c.getImaginary())) {
      if (F.isZero(c.getImaginary())) {
        return Math.abs(c.getReal());
      }
      final double q = c.getReal() / c.getImaginary();
      return Math.abs(c.getImaginary()) * Math.sqrt(1 + q * q);
    } else {
      if (F.isZero(c.getReal())) {
        return Math.abs(c.getImaginary());
      }
      final double q = c.getImaginary() / c.getReal();
      return Math.abs(c.getReal()) * Math.sqrt(1 + q * q);
    }
  }

  /**
   * The Lanczos approximation is a method for computing the {@link S#Gamma} function numerically.
   *
   * <p>
   * See <a href="https://en.wikipedia.org/wiki/Lanczos_approximation">Wikipedia - Lanczos
   * approximation</a>
   *
   * @param z the complex number for which to compute the gamma function
   * @return the gamma function value
   */
  public static org.hipparchus.complex.Complex lanczosApproxGamma(
      org.hipparchus.complex.Complex z) {
    if (z.getReal() < 0.5) {
      // Pi / ( Sin(Pi * z) * Gamma(1 - z) )
      return lanczosApproxGamma(z.negate().add(1.0)).multiply(z.multiply(Math.PI).sin())
          .reciprocal().multiply(Math.PI);
    } else {
      z = z.subtract(1.0);
      org.hipparchus.complex.Complex x = P_COMPLEX[0];
      for (int i = 1; i < G + 2; i++) {
        // x += p[i] / (z+i)
        x = x.add(P_COMPLEX[i].divide(z.add(i)));
      }
      org.hipparchus.complex.Complex t = z.add(G).add(0.5);
      // Sqrt(2 * Pi) * Pow(t, z + 0.5) * Exp(-t) * x
      return t.pow(z.add(0.5)).multiply(t.negate().exp()).multiply(x)
          .multiply(Math.sqrt(2 * Math.PI));
    }
  }

  /**
   * Be cautious with this method, no new internal couble complex is created
   *
   * @param value a double complex numeric value
   * @return
   */
  private static ComplexNum newInstance(final Complex value) {
    return new ComplexNum(value);
  }

  /**
   * Return the quotient and remainder as an array <code>[quotient, remainder]</code> of the
   * division of <code>Complex</code> numbers <code>c1, c2</code>.
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Gaussian_integer">Wikipedia - Gaussian integer</a>
   * <li><a href=
   * "http://fermatslasttheorem.blogspot.com/2005/06/division-algorithm-for-gaussian.html">Division
   * Algorithm for Gaussian Integers </a>
   * </ul>
   *
   * @param c1
   * @param c2
   * @return the quotient and remainder as an array <code>[quotient, remainder]</code>
   */
  public static Complex[] quotientRemainder(Complex c1, Complex c2) {
    // use hipparchus Complex implementation - see:
    // https://github.com/Hipparchus-Math/hipparchus/issues/67
    Complex remainder = c1.remainder(c2);
    Complex quotient = c1.subtract(remainder).divide(c2).rint();
    return new Complex[] {quotient, remainder};
  }

  /**
   * Return the quotient and remainder as an array <code>[quotient, remainder]</code> of the
   * division of <code>Complex</code> numbers <code>c1, c2</code>.
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Gaussian_integer">Wikipedia - Gaussian integer</a>
   * <li><a href=
   * "http://fermatslasttheorem.blogspot.com/2005/06/division-algorithm-for-gaussian.html">Division
   * Algorithm for Gaussian Integers </a>
   * </ul>
   *
   * @param cn1
   * @param cn2
   * @return the quotient and remainder as an array <code>[quotient, remainder]</code>
   */
  public static ComplexNum[] quotientRemainder(ComplexNum cn1, ComplexNum cn2) {
    Complex c1 = cn1.fComplex;
    Complex c2 = cn2.fComplex;
    Complex[] arr = quotientRemainder(c1, c2);
    return new ComplexNum[] {valueOf(arr[0]), valueOf(arr[1])};
  }


  /**
   * Create complex number on unit circle with given argument <code>arg</code>.
   *
   * @param arg angle
   * @return E^(I * angle), i.e. complex number on unit circle with given argument
   */
  public static ComplexNum unitOf(final double arg) {
    return newInstance(new Complex(Math.cos(arg), Math.sin(arg)));
  }

  /**
   * Creates a new instance of a {@link ComplexNum} from the given {@link Complex}. If both the real
   * and imaginary parts of the input are <code>+0.0d</code>, it returns the constant
   * {@link ComplexNum#ZERO}. Otherwise, it creates a new {@link ComplexNum} with the specified real
   * and imaginary parts.
   *
   * @param c the {@link Complex} instance from which to create the {@link ComplexNum}
   * @return a {@link ComplexNum} instance representing the input complex number
   */
  public static ComplexNum valueOf(final Complex c) {
    double real = c.getReal();
    double imaginary = c.getImaginary();
    if (real == 0.0d) {
      if (imaginary == 0.0d) {
        return ZERO;
      }
      return newInstance(new Complex(0.0d, imaginary));
    }
    return newInstance(c);
  }

  /**
   * Creates a new instance of a {@link ComplexNum} with the specified real part and an imaginary
   * part of zero. If the real part is <code>+0.0d</code>, it returns the constant
   * {@link ComplexNum#ZERO}.
   *
   * @param real the real part of the complex number
   * @return a {@link ComplexNum} instance representing the complex number with the given real part
   *         and an imaginary part of zero
   */
  public static ComplexNum valueOf(final double real) {
    if (real == 0.0d) {
      return ZERO;
    }
    return newInstance(new Complex(real, 0.0));
  }

  /**
   * Creates a new instance of a {@link ComplexNum} with the specified real and imaginary parts. If
   * both the real and imaginary parts are <code>+0.0d</code>, it returns the constant
   * {@link ComplexNum#ZERO}.
   *
   * @param real the real part of the complex number
   * @param imaginary the imaginary part of the complex number
   * @return a {@link ComplexNum} instance representing the complex number with the given real and
   *         imaginary parts
   */
  public static ComplexNum valueOf(final double real, final double imaginary) {
    if (real == 0.0d && imaginary == 0.0d) {
      return ZERO;
    }
    return newInstance(new Complex(real, imaginary));
  }

  /**
   * Creates a new instance of a {@link ComplexNum} from the given {@link INum}. If the real part of
   * the input is <code>+0.0d</code>, it returns the constant {@link ComplexNum#ZERO}. Otherwise, it
   * creates a new {@link ComplexNum} with the real part set to the input's real part and the
   * imaginary part set to zero.
   *
   * @param d the {@link INum} instance from which to create the {@link ComplexNum}
   * @return a {@link ComplexNum} instance representing the input number
   */
  public static ComplexNum valueOf(final INum d) {
    double real = d.getRealPart();
    if (real == 0.0d) {
      return ZERO;
    }
    return newInstance(new Complex(real, 0.0));
  }

  Complex fComplex;

  private ComplexNum(Complex complex) {
    fComplex = complex;
  }

  private ComplexNum(final double r, final double i) {
    fComplex = new Complex(r, i);
  }

  /** {@inheritDoc} */
  @Override
  public Num abs() {
    return Num.valueOf(dabs());
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
    return valueOf(fComplex.acos());
  }

  /**
   * Returns a {@code ComplexNum} whose value is {@code (this + addend)}. Uses the definitional
   * formula
   *
   * <p>
   * {@code (a + bi) + (c + di) = (a+c) + (b+d)i} If either {@code this} or {@code addend} has a
   * {@code NaN} value in either part, {@link #NaN} is returned; otherwise {@code Infinite} and
   * {@code NaN} values are returned in the parts of the result according to the rules for
   * {@link java.lang.Double} arithmetic.
   *
   * @param addend Value to be added to this {@code Complex}.
   * @return {@code this + addend}.
   * @throws NullArgumentException if {@code addend} is {@code null}.
   */
  private ComplexNum add(final ComplexNum addend) {
    return newInstance(fComplex.add(addend.fComplex));
  }

  @Override
  public IComplexNum add(final IComplexNum val) {
    if (val instanceof ApcomplexNum) {
      return apcomplexNumValue().add(val);
    }
    return newInstance(fComplex.add(((ComplexNum) val).fComplex));
  }


  @Override
  public IExpr agm(IExpr arg2) {
    if (arg2 instanceof INumber) {
      Apcomplex agm =
          EvalEngine.getApfloatDouble().agm(apcomplexValue(), ((INumber) arg2).apcomplexValue());
      return F.complexNum(agm.real().doubleValue(), agm.imag().doubleValue());
    }
    return IComplexNum.super.agm(arg2);
  }

  @Override
  public IExpr airyAi() {
    Apcomplex airyAi = EvalEngine.getApfloatDouble().airyAi(apcomplexValue());
    return F.complexNum(airyAi.real().doubleValue(), airyAi.imag().doubleValue());
  }

  @Override
  public IExpr airyAiPrime() {
    Apcomplex airyAiPrime = EvalEngine.getApfloatDouble().airyAiPrime(apcomplexValue());
    return F.complexNum(airyAiPrime.real().doubleValue(), airyAiPrime.imag().doubleValue());
  }

  @Override
  public IExpr airyBi() {
    Apcomplex airyBi = EvalEngine.getApfloatDouble().airyBi(apcomplexValue());
    return F.complexNum(airyBi.real().doubleValue(), airyBi.imag().doubleValue());
  }

  @Override
  public IExpr airyBiPrime() {
    Apcomplex airyBiPrime = EvalEngine.getApfloatDouble().airyBiPrime(apcomplexValue());
    return F.complexNum(airyBiPrime.real().doubleValue(), airyBiPrime.imag().doubleValue());
  }

  @Override
  public IExpr angerJ(IExpr arg2) {
    if (arg2 instanceof INumber) {
      try {
        Apcomplex angerJ =
            EvalEngine.getApfloatDouble().angerJ(apcomplexValue(),
                ((INumber) arg2).apcomplexValue());
        return F.complexNum(angerJ.real().doubleValue(), angerJ.imag().doubleValue());
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return IComplexNum.super.angerJ(arg2);
  }

  @Override
  public ApcomplexNum apcomplexNumValue() {
    return ApcomplexNum.valueOf(apcomplexValue());
  }

  @Override
  public Apcomplex apcomplexValue() {
    return new Apcomplex(new Apfloat(fComplex.getReal()), new Apfloat(fComplex.getImaginary()));
  }

  @Override
  public IInexactNumber asin() {
    return valueOf(fComplex.asin());
  }

  @Override
  public IInexactNumber atan() {
    return valueOf(fComplex.atan());
  }

  @Override
  public IExpr besselI(IExpr arg2) {
    if (arg2 instanceof INumber) {
      try {
        Apcomplex besselI = EvalEngine.getApfloatDouble().besselI(apcomplexValue(),
            ((INumber) arg2).apcomplexValue());
        return F.complexNum(besselI.real().doubleValue(), besselI.imag().doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException are) {
        return Errors.printMessage(S.BesselI, are);
      }
    }
    return IComplexNum.super.besselI(arg2);
  }

  @Override
  public IExpr besselJ(IExpr arg2) {
    if (arg2 instanceof INumber) {
      try {
        Apcomplex besselJ = EvalEngine.getApfloatDouble().besselJ(apcomplexValue(),
            ((INumber) arg2).apcomplexValue());
        return F.complexNum(besselJ.real().doubleValue(), besselJ.imag().doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException are) {
        return Errors.printMessage(S.BesselJ, are);
      }
    }
    return IComplexNum.super.besselJ(arg2);
  }

  @Override
  public IExpr besselK(IExpr arg2) {
    if (arg2 instanceof INumber) {
      try {
        Apcomplex besselK = EvalEngine.getApfloatDouble().besselK(apcomplexValue(),
            ((INumber) arg2).apcomplexValue());
        return F.complexNum(besselK.real().doubleValue(), besselK.imag().doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException are) {
        return Errors.printMessage(S.BesselK, are);
      }
    }
    return IComplexNum.super.besselK(arg2);
  }

  @Override
  public IExpr besselY(IExpr arg2) {
    if (arg2 instanceof INumber) {
      try {
        Apcomplex besselY = EvalEngine.getApfloatDouble().besselY(apcomplexValue(),
            ((INumber) arg2).apcomplexValue());
        return F.complexNum(besselY.real().doubleValue(), besselY.imag().doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException are) {
        return Errors.printMessage(S.BesselY, are);
      }
    }
    return IComplexNum.super.besselY(arg2);
  }

  @Override
  public IExpr beta(IExpr b) {
    if (b instanceof INumber) {
      try {
        Apcomplex beta =
            EvalEngine.getApfloatDouble().beta(apcomplexValue(), ((INumber) b).apcomplexValue());
        return F.complexNum(beta.real().doubleValue(), beta.imag().doubleValue());
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
        Apcomplex beta = EvalEngine.getApfloatDouble().beta(apcomplexValue(),
            ((INumber) a).apcomplexValue(), ((INumber) b).apcomplexValue());
        return F.complexNum(beta.real().doubleValue(), beta.imag().doubleValue());
      } catch (ApfloatArithmeticException aaex) {
        if ("divide.byZero".equals(aaex.getLocalizationKey())) {
          return F.CComplexInfinity;
        }
      } catch (ArithmeticException | NumericComputationException aex) {
        if ("Division by zero".equals(aex.getMessage())) {
          return F.CComplexInfinity;
        }
      }
    }
    return IComplexNum.super.beta(a, b);
  }

  @Override
  public IExpr beta(IExpr x2, IExpr a, IExpr b) {
    if (x2 instanceof INumber && a instanceof INumber && b instanceof INumber) {
      try {
        Apcomplex beta =
            EvalEngine.getApfloatDouble().beta(apcomplexValue(), ((INumber) x2).apcomplexValue(),
                ((INumber) a).apcomplexValue(), ((INumber) b).apcomplexValue());
        return F.complexNum(beta.real().doubleValue(), beta.imag().doubleValue());
      } catch (NumericComputationException e) {
        //
      } catch (ApfloatArithmeticException aaex) {
        if ("divide.byZero".equals(aaex.getLocalizationKey())) {
          return F.CComplexInfinity;
        }
      } catch (ArithmeticException aex) {
        if ("Division by zero".equals(aex.getMessage())) {
          return F.CComplexInfinity;
        }
      }
    }
    return IComplexNum.super.beta(x2, a, b);
  }

  @Override
  public INumber ceilFraction() throws ArithmeticException {
    try {
      return F.complex(NumberUtil.toLong(Math.ceil(fComplex.getReal())),
          NumberUtil.toLong(Math.ceil(fComplex.getImaginary())));
    } catch (ArithmeticException ae) {
      ArgumentTypeException.throwArg(this, F.Ceiling(this));
    }
    return null;
  }

  @Override
  public IExpr chebyshevT(IExpr arg2) {
    if (arg2 instanceof INumber) {
      // if (arg2 instanceof IReal) {
      // try {
      // Apcomplex chebyshevT = EvalEngine.getApfloatDouble().chebyshevT(apcomplexValue(),
      // ((IReal) arg2).apfloatValue());
      // return F.complexNum(chebyshevT.real().doubleValue(), chebyshevT.imag().doubleValue());
      // } catch (ArithmeticException | NumericComputationException are) {
      //
      // }
      // }
      try {
        Apcomplex chebyshevT = EvalEngine.getApfloatDouble().chebyshevT(apcomplexValue(),
            ((INumber) arg2).apcomplexValue());
        return F.complexNum(chebyshevT.real().doubleValue(), chebyshevT.imag().doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException are) {
        return Errors.printMessage(S.ChebyshevT, are);
      }
    }
    return IComplexNum.super.chebyshevT(arg2);
  }

  @Override
  public IExpr chebyshevU(IExpr arg2) {
    if (arg2 instanceof INumber) {
      // if (arg2 instanceof IReal) {
      // try {
      // Apcomplex chebyshevU = EvalEngine.getApfloatDouble().chebyshevU(apcomplexValue(),
      // ((IReal) arg2).apfloatValue());
      // return F.complexNum(chebyshevU.real().doubleValue(), chebyshevU.imag().doubleValue());
      // } catch (ArithmeticException | NumericComputationException are) {
      //
      // }
      // }
      try {
        Apcomplex chebyshevU = EvalEngine.getApfloatDouble().chebyshevU(apcomplexValue(),
            ((INumber) arg2).apcomplexValue());
        return F.complexNum(chebyshevU.real().doubleValue(), chebyshevU.imag().doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException are) {
        return Errors.printMessage(S.ChebyshevU, are);
      }
    }
    return IComplexNum.super.chebyshevU(arg2);
  }

  // public static int compare(final Complex c1, final Complex c2) {
  // int c = Double.compare(c1.getReal(), c2.getReal());
  // if (c != 0) {
  // return c;
  // }
  // return Double.compare(c1.getImaginary(), c2.getImaginary());
  // }

  /** {@inheritDoc} */
  @Override
  public int compareAbsValueToOne() {
    double temp = dabs();
    return Double.compare(temp, 1.0);
  }

  public int compareTo(final Complex that) {
    // https://github.com/mtommila/apfloat/issues/27
    int result = Double.compare(fComplex.getReal(), that.getReal());
    if (result == 0) {
      result = Double.compare(Math.abs(fComplex.getImaginary()), Math.abs(that.getImaginary()));
    }
    if (result == 0) {
      result =
          Double.compare(Math.signum(fComplex.getImaginary()), Math.signum(that.getImaginary()));
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
      if (expr instanceof ComplexNum) {
        return compareTo(((ComplexNum) expr).fComplex);
      }
      if (expr instanceof ApcomplexNum) {
        ApcomplexNum apcomplexNum = (ApcomplexNum) expr;
        return -1 * apcomplexNum.compareTo(apcomplexNumValue());
      }
      return compareTo(
          new Complex(((INumber) expr).reDoubleValue(), ((INumber) expr).imDoubleValue()));
    }
    return IExpr.compareHierarchy(this, expr);
    // return IComplexNum.super.compareTo(expr);
  }

  @Override
  public IExpr complexArg() {
    return Num.valueOf(fComplex.getArgument());
  }

  @Override
  public ComplexNum complexNumValue() {
    return this;
  }

  @Override
  public int complexSign() {
    final int signum = (int) Math.signum(fComplex.getReal());
    return (signum == 0) ? (int) Math.signum(fComplex.getImaginary()) : signum;
  }

  @Override
  public Complex complexValue() {
    return fComplex;
  }

  /** {@inheritDoc} */
  @Override
  public IComplexNum conjugate() {
    return newInstance(fComplex.conjugate());
  }

  @Override
  public IExpr copy() {
    return this;
  }

  @Override
  public IExpr copySign(IExpr that) {
    if (that instanceof ComplexNum) {
      return newInstance(fComplex.copySign(((ComplexNum) that).fComplex));
    }
    if (that instanceof Num) {
      return newInstance(fComplex.copySign(((Num) that).value));
    }
    return IComplexNum.super.copySign(that);
  }

  @Override
  public ComplexNum cos() {
    return valueOf(fComplex.cos());
  }

  @Override
  public ComplexNum cosh() {
    return valueOf(fComplex.cosh());
  }

  @Override
  public IExpr coshIntegral() {
    Apcomplex coshIntegral = EvalEngine.getApfloatDouble().coshIntegral(apcomplexValue());
    return F.complexNum(coshIntegral.real().doubleValue(), coshIntegral.imag().doubleValue());
  }

  @Override
  public IExpr cosIntegral() {
    return F.complexNum(GammaJS.cosIntegral(fComplex));
    // Apcomplex cosIntegral = EvalEngine.getApfloatDouble().cosIntegral(apcomplexValue());
    // return F.complexNum(cosIntegral.real().doubleValue(), cosIntegral.imag().doubleValue());
  }

  /** {@inheritDoc} */
  @Override
  public double dabs() {
    return dabs(fComplex);
  }

  /** {@inheritDoc} */
  @Override
  public IExpr dec() {
    return add(MINUS_ONE);
  }

  /** {@inheritDoc} */
  @Override
  public long determinePrecision(boolean postParserProcessing) {
    return ParserConfig.MACHINE_PRECISION;
  }

  @Override
  public IExpr digamma() {
    try {
      Complex complexDigamma = org.hipparchus.special.Gamma.digamma(fComplex);
      if (complexDigamma.isFinite()) {
        return F.complexNum(complexDigamma);
      }
      Apcomplex digamma = EvalEngine.getApfloatDouble().digamma(apcomplexValue());
      return F.complexNum(digamma.real().doubleValue(), digamma.imag().doubleValue());
    } catch (ArithmeticException | NumericComputationException aex) {
    }
    return IComplexNum.super.digamma();
  }

  @Override
  public IComplexNum divide(final IComplexNum that) {
    if (that instanceof ApcomplexNum) {
      return apcomplexNumValue().divide(that);
    }
    return newInstance(fComplex.divide(((ComplexNum) that).fComplex));
  }

  @Override
  public IExpr ellipticE() {
    Apcomplex ellipticE = EvalEngine.getApfloatDouble().ellipticE(apcomplexValue());
    return F.complexNum(ellipticE.real().doubleValue(), ellipticE.imag().doubleValue());
  }

  @Override
  public IExpr ellipticK() {
    Apcomplex ellipticK = EvalEngine.getApfloatDouble().ellipticK(apcomplexValue());
    return F.complexNum(ellipticK.real().doubleValue(), ellipticK.imag().doubleValue());
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof ComplexNum) {
      return fComplex.equals(((ComplexNum) obj).fComplex);
    }
    return false;
  }

  @Override
  public boolean equalsInt(int i) {
    return false;
  }

  @Override
  public IExpr erf() {
    try {
      // TODO depends on // https://github.com/Hipparchus-Math/hipparchus/issues/278
      Complex complexErf = org.hipparchus.special.Erf.erf(fComplex);
      if (complexErf.isFinite()) {
        return F.complexNum(complexErf);
      }
      Apcomplex erf = EvalEngine.getApfloatDouble().erf(apcomplexValue());
      return F.complexNum(erf.real().doubleValue(), erf.imag().doubleValue());
    } catch (OverflowException of) {
      // return Underflow? https://github.com/mtommila/apfloat/issues/38
      return F.Overflow();
    } catch (ArithmeticException | NumericComputationException e) {
      //
    }
    return IComplexNum.super.erf();
  }

  @Override
  public IExpr erfc() {
    try {
      try {
        // TODO depends on // https://github.com/Hipparchus-Math/hipparchus/issues/278
        Complex complexErfc = org.hipparchus.special.Erf.erfc(fComplex);
        if (complexErfc.isFinite()) {
          return F.complexNum(complexErfc);
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
      }
      Apcomplex erfc = EvalEngine.getApfloatDouble().erfc(apcomplexValue());
      return F.complexNum(erfc.real().doubleValue(), erfc.imag().doubleValue());
    } catch (OverflowException of) {
      // return Underflow? https://github.com/mtommila/apfloat/issues/38
      return F.Overflow();
    } catch (ArithmeticException | NumericComputationException e) {
      //
    }
    return IComplexNum.super.erfc();
  }

  @Override
  public IExpr erfi() {
    try {
      // TODO depends on // https://github.com/Hipparchus-Math/hipparchus/issues/278
      // Complex complexErfi = org.hipparchus.special.Erf.erfInv(fComplex);
      // if (complexErfi.isFinite()) {
      // return F.complexNum(complexErfi);
      // }
      Apcomplex erfi = EvalEngine.getApfloatDouble().erfi(apcomplexValue());
      return F.complexNum(erfi.real().doubleValue(), erfi.imag().doubleValue());
    } catch (OverflowException of) {
      // return Underflow? https://github.com/mtommila/apfloat/issues/38
      return F.Overflow();
    } catch (ArithmeticException | NumericComputationException e) {
      //
    }
    return IComplexNum.super.erfi();
  }

  /** {@inheritDoc} */
  @Override
  public Complex evalfc() {
    return fComplex;
  }

  @Override
  public INumber evalNumber() {
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr evaluate(EvalEngine engine) {
    if (fComplex.isInfinite()) {
      return F.CComplexInfinity;
    }
    if (fComplex.isNaN()) {
      return S.Indeterminate;
    }
    if (engine.isNumericMode() && engine.isArbitraryMode()) {
      return ApcomplexNum.valueOf(getRealPart(), getImaginaryPart());
    }
    return F.NIL;
  }

  @Override
  public ComplexNum exp() {
    return valueOf(fComplex.exp());
  }

  @Override
  public IExpr expIntegralE(IExpr z) {
    if (z instanceof INumber) {
      return F.complexNum(GammaJS.expIntegralE(fComplex, ((INumber) z).complexValue()));
      // Apcomplex expIntegralE = EvalEngine.getApfloatDouble().expIntegralE(apcomplexValue(),
      // ((INumber) z).apcomplexValue());
      // return valueOf(expIntegralE.real().doubleValue(), expIntegralE.imag().doubleValue());

    }
    return IComplexNum.super.expIntegralE(z);
  }

  @Override
  public IExpr expIntegralEi() {
    return F.complexNum(GammaJS.expIntegralEi(fComplex));
    // Apcomplex expIntegralEi = EvalEngine.getApfloatDouble().expIntegralEi(apcomplexValue());
    // return valueOf(expIntegralEi.real().doubleValue(), expIntegralEi.imag().doubleValue());
  }

  @Override
  public IExpr factorial() {
    if (isMathematicalIntegerNegative()) {
      return F.CComplexInfinity;
    }
    try {
      return F.complexNum(ComplexNum.lanczosApproxGamma(fComplex.add(Complex.ONE)));
    } catch (ArithmeticException | NumericComputationException e) {
      // try as computation with complex numbers
    }
    return IComplexNum.super.factorial();
  }

  @Override
  public IExpr fibonacci(IExpr arg2) {
    if (arg2 instanceof INumber) {
      try {
        Apcomplex fibonacci = EvalEngine.getApfloatDouble().fibonacci(apcomplexValue(),
            ((INumber) arg2).apcomplexValue());
        return F.complexNum(fibonacci.real().doubleValue(), fibonacci.imag().doubleValue());
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return IComplexNum.super.fibonacci(arg2);
  }

  @Override
  public INumber floorFraction() throws ArithmeticException {
    try {
      return F.complex(NumberUtil.toLong(Math.floor(fComplex.getReal())),
          NumberUtil.toLong(Math.floor(fComplex.getImaginary())));
    } catch (ArithmeticException ae) {
      ArgumentTypeException.throwArg(this, F.Floor(this));
    }
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public INumber fractionalPart() {
    return F.complexNum(getRealPart() % 1, getImaginaryPart() % 1);
  }

  @Override
  public IExpr fresnelC() {
    Apcomplex fresnelC = EvalEngine.getApfloatDouble().fresnelC(apcomplexValue());
    return F.complexNum(fresnelC.real().doubleValue(), fresnelC.imag().doubleValue());
  }

  @Override
  public IExpr fresnelS() {
    Apcomplex fresnelS = EvalEngine.getApfloatDouble().fresnelS(apcomplexValue());
    // Apcomplex fresnelS = ApcomplexNum.fresnelS(apcomplexValue(), EvalEngine.getApfloatDouble());
    return F.complexNum(fresnelS.real().doubleValue(), fresnelS.imag().doubleValue());
  }

  /** {@inheritDoc} */
  @Override
  public String fullFormString() {
    double re = fComplex.getReal();
    double im = fComplex.getImaginary();
    StringBuilder buf = new StringBuilder("Complex");
    if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
      buf.append('(');
    } else {
      buf.append('[');
    }

    String str = Double.toString(re);
    if (!ParserConfig.EXPLICIT_TIMES_OPERATOR) {
      int indx = str.indexOf("E");
      if (indx > 0) {
        str = str.replace("E", "`*^");
      } else {
        str = str + "`";
      }
    }
    buf.append(str);
    buf.append(',');
    str = Double.toString(im);
    if (!ParserConfig.EXPLICIT_TIMES_OPERATOR) {
      int indx = str.indexOf("E");
      // `*^
      if (indx > 0) {
        str = str.replace("E", "`*^");
      } else {
        str = str + "`";
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
      // hipparchus #gamma(Complex) is not as accurate as apfloat implementation
      // Complex complexGamma = org.hipparchus.special.Gamma.gamma(fComplex);
      // if (complexGamma.isFinite()) {
      // return F.complexNum(complexGamma);
      // }
      Complex complexGamma = ComplexNum.lanczosApproxGamma(fComplex);
      if (complexGamma.isFinite()) {
        return F.complexNum(complexGamma);
      }
      Apcomplex gamma = EvalEngine.getApfloatDouble().gamma(apcomplexValue());
      return F.complexNum(gamma.real().doubleValue(), gamma.imag().doubleValue());
    } catch (OverflowException of) {
      return F.Overflow();
    } catch (ArithmeticException | NumericComputationException are) {
      return Errors.printMessage(S.Gamma, are, EvalEngine.get());
    }
  }

  @Override
  public IExpr gamma(IExpr x) {
    if (isZero() && x.isZero()) {
      return F.CInfinity;
    }
    if (x instanceof INumber) {
      try {
        Apcomplex gamma =
            EvalEngine.getApfloatDouble().gamma(apcomplexValue(), ((INumber) x).apcomplexValue());
        return F.complexNum(gamma.real().doubleValue(), gamma.imag().doubleValue());
      } catch (ArithmeticException | NumericComputationException are) {
        // try as computation with complex numbers
        // java.lang.ArithmeticException: Upper gamma with first argument real part nonpositive and
        // second argment zero
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
    if (x0 instanceof INumber && x1 instanceof INumber) {
      try {
        Apcomplex gamma = EvalEngine.getApfloatDouble().gamma(apcomplexValue(),
            ((INumber) x0).apcomplexValue(), ((INumber) x1).apcomplexValue());
        return F.complexNum(gamma.real().doubleValue(), gamma.imag().doubleValue());
      } catch (ArithmeticException | NumericComputationException are) {
        // try as computation with complex numbers
        // java.lang.ArithmeticException: Upper gamma with first argument real part nonpositive and
        // second argment zero
        return Errors.printMessage(S.Gamma, are, EvalEngine.get());
      }
    }
    return IComplexNum.super.gamma(x0, x1);
  }

  @Override
  public IExpr gegenbauerC(IExpr arg2) {
    if (arg2 instanceof INumber) {
      try {
        Apcomplex gegenbauerC = EvalEngine.getApfloatDouble().gegenbauerC(apcomplexValue(),
            ((INumber) arg2).apcomplexValue());
        return F.complexNum(gegenbauerC.real().doubleValue(), gegenbauerC.imag().doubleValue());
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
        Apcomplex gegenbauerC = EvalEngine.getApfloat().gegenbauerC(apcomplexValue(),
            ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue());
        return F.complexNum(gegenbauerC.real().doubleValue(), gegenbauerC.imag().doubleValue());
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return IComplexNum.super.gegenbauerC(arg2, arg3);
  }

  @Override
  public IExpr getAddendum() {
    return F.complexNum(new Complex(0, fComplex.getImaginary()));
  }

  public Complex getCMComplex() {
    return new Complex(fComplex.getReal(), fComplex.getImaginary());
  }

  /** @return */
  @Override
  public double getImaginaryPart() {
    double temp = fComplex.getImaginary();
    if (temp == (-0.0)) {
      temp = 0.0;
    }
    return temp;
  }

  @Override
  public IExpr getPi() {
    return F.num(Math.PI);
  }

  @Override
  public double getRealPart() {
    double temp = fComplex.getReal();
    if (temp == (-0.0)) {
      temp = 0.0;
    }
    return temp;
  }

  @Override
  public IExpr harmonicNumber() {
    Apcomplex harmonicNumber = EvalEngine.getApfloatDouble().harmonicNumber(apcomplexValue());
    return F.complexNum(harmonicNumber);
  }

  @Override
  public IExpr harmonicNumber(IExpr r) {
    if (r instanceof INumber) {
      try {
        FixedPrecisionApfloatHelper h = EvalEngine.getApfloatDouble();
        Apcomplex harmonicNumber =
            h.harmonicNumber(apcomplexValue(), ((INumber) r).apcomplexValue());
        return F.complexNum(harmonicNumber);
      } catch (ArithmeticException | NumericComputationException aex) {

      }
    }
    return IComplexNum.super.harmonicNumber(r);
  }

  @Override
  public final int hashCode() {
    return fComplex.hashCode();
  }

  @Override
  public ISymbol head() {
    return S.Complex;
  }

  @Override
  public IExpr hermiteH(IExpr arg2) {
    if (arg2 instanceof INumber) {
      try {
        Apcomplex hermiteH = EvalEngine.getApfloatDouble().hermiteH(apcomplexValue(),
            ((INumber) arg2).apcomplexValue());
        return F.complexNum(hermiteH.real().doubleValue(), hermiteH.imag().doubleValue());
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
    try {
      return F
          .complexNum(HypergeometricJS.hypergeometric0F1(fComplex, ((ComplexNum) arg2).evalfc()));
    } catch (RuntimeException e) {
      Errors.rethrowsInterruptException(e);
      // try as computation with complex numbers
    }
    return IComplexNum.super.hypergeometric0F1(arg2);
  }

  @Override
  public IExpr hypergeometric0F1Regularized(IExpr arg2) {
    if (arg2 instanceof INumber) {
      Apcomplex hypergeometric0f1Regularized = EvalEngine.getApfloatDouble()
          .hypergeometric0F1Regularized(apcomplexValue(), ((INumber) arg2).apcomplexValue());
      return F.complexNum(hypergeometric0f1Regularized.real().doubleValue(),
          hypergeometric0f1Regularized.imag().doubleValue());
    }
    return IComplexNum.super.hypergeometric0F1Regularized(arg2);
  }

  @Override
  public IExpr hypergeometric1F1(IExpr arg2, IExpr arg3) {
    try {
      return F.complexNum(HypergeometricJS.hypergeometric1F1(fComplex, //
          arg2.evalfc(), //
          arg3.evalfc()));
    } catch (RuntimeException e) {
      Errors.rethrowsInterruptException(e);
      // try as computation with complex numbers
    }
    return IComplexNum.super.hypergeometric1F1(arg2, arg3);
  }

  @Override
  public IExpr hypergeometric1F1Regularized(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      Apcomplex hypergeometric1F1Regularized =
          EvalEngine.getApfloatDouble().hypergeometric1F1Regularized(apcomplexValue(),
              ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue());
      return F.complexNum(hypergeometric1F1Regularized.real().doubleValue(),
          hypergeometric1F1Regularized.imag().doubleValue());
    }
    return IComplexNum.super.hypergeometric1F1Regularized(arg2, arg3);
  }

  @Override
  public IExpr hypergeometric2F1(IExpr arg2, IExpr arg3, IExpr arg4) {
    if (arg2 instanceof INumber && arg3 instanceof INumber && arg4 instanceof INumber) {
      try {
        Apcomplex hypergeometric2f1 = EvalEngine.getApfloatDouble().hypergeometric2F1(
            apcomplexValue(), ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue(),
            ((INumber) arg4).apcomplexValue());
        return F.complexNum(hypergeometric2f1.real().doubleValue(),
            hypergeometric2f1.imag().doubleValue());
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return IComplexNum.super.hypergeometric2F1(arg2, arg3, arg4);
    // try {
    // return F.complexNum(HypergeometricJS.hypergeometric2F1(fComplex, //
    // arg2.evalfc(), //
    // arg3.evalfc(), //
    // arg4.evalfc()));
    // } catch (RuntimeException e) {
    // // try as computation with complex numbers
    // }
    // return IComplexNum.super.hypergeometric2F1(arg2, arg3, arg4);
  }

  @Override
  public IExpr hypergeometric2F1Regularized(IExpr arg2, IExpr arg3, IExpr arg4) {
    if (arg2 instanceof INumber && arg3 instanceof INumber && arg4 instanceof INumber) {
      try {
        Apcomplex hypergeometric2F1Regularized = EvalEngine.getApfloatDouble()
            .hypergeometric2F1Regularized(apcomplexValue(), ((INumber) arg2).apcomplexValue(),
                ((INumber) arg3).apcomplexValue(), ((INumber) arg4).apcomplexValue());
        return F.complexNum(hypergeometric2F1Regularized.real().doubleValue(),
            hypergeometric2F1Regularized.imag().doubleValue());
      } catch (NumericComputationException ex) {
        // org.apfloat.OverflowException: Apfloat disk file storage is disabled
      }
    }
    return IComplexNum.super.hypergeometric2F1Regularized(arg2, arg3, arg4);
  }

  @Override
  public IExpr hypergeometricU(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      Apcomplex hypergeometricU = EvalEngine.getApfloatDouble().hypergeometricU(apcomplexValue(),
          ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue());
      return F.complexNum(hypergeometricU.real().doubleValue(),
          hypergeometricU.imag().doubleValue());
    }
    return IComplexNum.super.hypergeometricU(arg2, arg3);

    // try {
    // return F.complexNum(HypergeometricJS.hypergeometricU(fComplex, //
    // arg2.evalfc(), //
    // arg3.evalfc()));
    // } catch (RuntimeException e) {
    // // try as computation with complex numbers
    // }
    // return IComplexNum.super.hypergeometricU(arg2, arg3);
  }

  /** {@inheritDoc} */
  @Override
  public IReal im() {
    return F.num(getImaginaryPart());
  }

  @Override
  public double imDoubleValue() {
    return fComplex.getImaginary();
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
    double re = fComplex.getReal();
    double im = fComplex.getImaginary();
    IInteger reInt;
    IInteger imInt;
    if (re < 0.0) {
      reInt = F.ZZ(NumberUtil.toLong(Math.ceil(re)));
    } else {
      reInt = F.ZZ(NumberUtil.toLong(Math.floor(re)));
    }
    if (im < 0.0) {
      imInt = F.ZZ(NumberUtil.toLong(Math.ceil(im)));
    } else {
      imInt = F.ZZ(NumberUtil.toLong(Math.floor(im)));
    }
    return F.CC(reInt, imInt);
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
    return new StringBuilder(prefix).append("complexNum(").append(fComplex.getReal()).append(",")
        .append(fComplex.getImaginary()).append(")");
  }

  @Override
  public CharSequence internalScalaString(boolean symbolsAsFactoryMethod, int depth) {
    SourceCodeProperties p = SourceCodeProperties.scalaFormProperties(symbolsAsFactoryMethod);
    return internalJavaString(p, depth, x -> null);
  }

  @Override
  public INumber inverse() {
    if (isOne()) {
      return this;
    }
    return valueOf(fComplex.reciprocal());
    // final double tmp = (fComplex.getReal() * fComplex.getReal())
    // + (fComplex.getImaginary() * fComplex.getImaginary());
    // return valueOf(fComplex.getReal() / tmp, -fComplex.getImaginary() / tmp);
  }

  @Override
  public boolean isFinite() {
    return fComplex.isFinite();
  }

  @Override
  public boolean isImaginaryUnit() {
    return equals(I);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isIndeterminate() {
    return fComplex.isNaN();
  }

  @Override
  public boolean isInexactNumber() {
    return fComplex.isFinite();
  }

  /** @return */
  @Override
  public boolean isInfinite() {
    return fComplex.isInfinite();
  }

  /** @return */
  @Override
  public boolean isMathematicalIntegerNegative() {
    return fComplex.isMathematicalInteger() && fComplex.getReal() < 0.0;
  }

  /** @return */
  @Override
  public boolean isMathematicalIntegerNonNegative() {
    return fComplex.isMathematicalInteger() && fComplex.getReal() >= 0.0;
  }

  @Override
  public boolean isMinusOne() {
    return F.isFuzzyEquals(fComplex.getReal(), -1.0, Config.DOUBLE_TOLERANCE) && //
        F.isZero(fComplex.getImaginary(), Config.DOUBLE_TOLERANCE);
  }

  /** @return */
  @Override
  public boolean isNaN() {
    return fComplex.isNaN();
  }

  @Override
  public boolean isNegativeImaginaryUnit() {
    return equals(NI);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNumIntValue() {
    return F.isZero(fComplex.getImaginary()) && F.isNumIntValue(fComplex.getReal());
  }

  @Override
  public boolean isOne() {
    return F.isFuzzyEquals(fComplex.getReal(), 1.0, Config.DOUBLE_TOLERANCE) && //
        F.isZero(fComplex.getImaginary(), Config.DOUBLE_TOLERANCE);
  }

  @Override
  public boolean isSame(IExpr expression) {
    if (expression instanceof ComplexNum) {
      final ComplexNum c = (ComplexNum) expression;
      return F.isAlmostSame(fComplex.getReal(), c.reDoubleValue()) //
          && F.isAlmostSame(fComplex.getImaginary(), c.imDoubleValue());
    }
    return false;
  }

  @Override
  public boolean isSame(IExpr expression, double epsilon) {
    if (expression instanceof ComplexNum) {
      return F.isZero(fComplex.getReal() - ((ComplexNum) expression).fComplex.getReal(), epsilon)
          && F.isZero(fComplex.getImaginary() - ((ComplexNum) expression).fComplex.getImaginary(),
              epsilon);
    }
    return false;
  }

  @Override
  public boolean isZero() {
    return isZero(Config.DOUBLE_TOLERANCE);
  }

  @Override
  public boolean isZero(double tolerance) {
    return F.isZero(fComplex.getReal(), tolerance) && //
        F.isZero(fComplex.getImaginary(), tolerance);
  }

  @Override
  public IExpr jacobiP(IExpr arg2, IExpr arg3, IExpr arg4) {
    if (arg2 instanceof INumber && arg3 instanceof INumber && arg4 instanceof INumber) {
      try {
        Apcomplex jacobiP = EvalEngine.getApfloatDouble().jacobiP(apcomplexValue(),
            ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue(),
            ((INumber) arg4).apcomplexValue());
        return F.complexNum(jacobiP.real().doubleValue(), jacobiP.imag().doubleValue());
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
        Apcomplex laguerreL = EvalEngine.getApfloatDouble().laguerreL(apcomplexValue(),
            ((INumber) arg2).apcomplexValue());
        return F.complexNum(laguerreL.real().doubleValue(), laguerreL.imag().doubleValue());
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
        Apcomplex laguerreL = EvalEngine.getApfloatDouble().laguerreL(apcomplexValue(),
            ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue());
        return F.complexNum(laguerreL.real().doubleValue(), laguerreL.imag().doubleValue());
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
        Apcomplex legendreP = EvalEngine.getApfloatDouble().legendreP(apcomplexValue(),
            ((INumber) arg2).apcomplexValue());
        return F.complexNum(legendreP.real().doubleValue(), legendreP.imag().doubleValue());
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
        Apcomplex legendreP = EvalEngine.getApfloatDouble().legendreP(apcomplexValue(),
            ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue());
        return F.complexNum(legendreP.real().doubleValue(), legendreP.imag().doubleValue());
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
        Apcomplex legendreQ = EvalEngine.getApfloatDouble().legendreQ(apcomplexValue(),
            ((INumber) arg2).apcomplexValue());
        return F.complexNum(legendreQ.real().doubleValue(), legendreQ.imag().doubleValue());
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
        Apcomplex legendreQ = EvalEngine.getApfloatDouble().legendreQ(apcomplexValue(),
            ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue());
        return F.complexNum(legendreQ.real().doubleValue(), legendreQ.imag().doubleValue());
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return IComplexNum.super.legendreQ(arg2, arg3);
  }

  @Override
  public IComplexNum barnesG() {
    Apcomplex barnesG = EvalEngine.getApfloat().barnesG(apcomplexValue());
    return F.complexNum(barnesG.real().doubleValue(), barnesG.imag().doubleValue());
  }

  @Override
  public IComplexNum logBarnesG() {
    Apcomplex logBarnesG = EvalEngine.getApfloat().logBarnesG(apcomplexValue());
    return F.complexNum(logBarnesG.real().doubleValue(), logBarnesG.imag().doubleValue());
  }

  @Override
  public ComplexNum log() {
    return valueOf(fComplex.log());
  }

  @Override
  public IExpr log(final IExpr base) {
    if (base instanceof INumber) {
      Complex complexBase = ((INumber) base).evalfc();
      return valueOf(fComplex.log().divide(complexBase.log()));
    }
    return IComplexNum.super.log(base);
  }

  @Override
  public IExpr logGamma() {
    try {
      Apcomplex logGamma = EvalEngine.getApfloatDouble().logGamma(apcomplexValue());
      return F.complexNum(logGamma.real().doubleValue(), logGamma.imag().doubleValue());
    } catch (ApfloatArithmeticException aaex) {
      String localizationKey = aaex.getLocalizationKey();
      if ("logGamma.ofZero".equals(localizationKey)
          || "logGamma.ofNegativeInteger".equals(localizationKey)) {
        return F.CInfinity;
      }
      aaex.printStackTrace();
    }
    return IComplexNum.super.logGamma();
  }

  @Override
  public IExpr logIntegral() {
    return F.complexNum(GammaJS.logIntegral(fComplex));
    // Apcomplex logIntegral = EvalEngine.getApfloatDouble().logIntegral(apcomplexValue());
    // return F.complexNum(logIntegral.real().doubleValue(), logIntegral.imag().doubleValue());
  }

  @Override
  public IExpr logisticSigmoid() {
    Apcomplex logisticSigmoid = EvalEngine.getApfloatDouble().logisticSigmoid(apcomplexValue());
    return F.complexNum(logisticSigmoid.real().doubleValue(), logisticSigmoid.imag().doubleValue());
  }

  /**
   * Returns a {@code ComplexNum} whose value is {@code this * factor}. Implements preliminary
   * checks for {@code NaN} and infinity followed by the definitional formula:
   *
   * <p>
   * {@code (a + bi)(c + di) = (ac - bd) + (ad + bc)i} Returns {@link #NaN} if either {@code
   * this} or {@code factor} has one or more {@code NaN} parts.
   *
   * <p>
   * Returns {@link #INF} if neither {@code this} nor {@code factor} has one or more {@code NaN}
   * parts and if either {@code this} or {@code factor} has one or more infinite parts (same result
   * is returned regardless of the sign of the components).
   *
   * <p>
   * Returns finite values in components of the result per the definitional formula in all remaining
   * cases.
   *
   * @param factor value to be multiplied by this {@code ComplexNum}.
   * @return {@code this * factor}.
   * @throws NullArgumentException if {@code factor} is {@code null}.
   */
  public ComplexNum multiply(final ComplexNum factor) {
    return newInstance(fComplex.multiply(factor.fComplex));
  }

  @Override
  public IComplexNum multiply(final IComplexNum val) {
    if (val instanceof ApcomplexNum) {
      return apcomplexNumValue().multiply(val);
    }
    return newInstance(fComplex.multiply(((ComplexNum) val).fComplex));
  }

  /**
   * Returns a {@code ComplexNum} whose value is {@code (-this)}. Returns {@code NaN} if either real
   * or imaginary part of this Complex number is {@code Double.NaN}.
   *
   * @return {@code -this}.
   */
  @Override
  public ComplexNum negate() {
    return newInstance(fComplex.negate());
  }

  @Override
  public IComplexNum one() {
    return ONE;
  }

  /** @return */
  @Override
  public INumber opposite() {
    return newInstance(fComplex.negate());
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
    if (that instanceof IComplexNum) {
      if (that instanceof ApcomplexNum) {
        ApcomplexNum acn = (ApcomplexNum) that;
        return ApcomplexNum.valueOf(getRealPart(), getImaginaryPart()).add(acn);
      }
      return newInstance(fComplex.add(((ComplexNum) that).fComplex));
    }
    if (that instanceof INum) {
      if (that instanceof ApfloatNum) {
        ApfloatNum afn = (ApfloatNum) that;
        return ApcomplexNum.valueOf(getRealPart(), getImaginaryPart())
            .add(ApcomplexNum.valueOf(afn.fApfloat, Apcomplex.ZERO));
      }
      return add(ComplexNum.valueOf(((Num) that).getRealPart()));
    }
    throw new java.lang.ArithmeticException();
  }

  @Override
  public INumber plus(final INumber that) {
    if (that instanceof IInexactNumber) {
      return plus((IInexactNumber) that);
    }
    if (that instanceof IReal) {
      return this.add(F.complexNum(that.evalf()));
    }
    if (that instanceof ComplexSym) {
      return F.complexNum(fComplex.add(that.evalfc()));
    }
    throw new java.lang.ArithmeticException();
  }

  @Override
  public IExpr pochhammer(IExpr arg2) {
    if (arg2 instanceof INumber) {
      if (arg2 instanceof ComplexNum || arg2 instanceof Num) {
        return plus(arg2).gamma().divide(gamma());
      }
      Apcomplex pochhammer = EvalEngine.getApfloatDouble().pochhammer(apcomplexValue(),
          ((INumber) arg2).apcomplexValue());
      return F.complexNum(pochhammer.real().doubleValue(), pochhammer.imag().doubleValue());
    }
    return IComplexNum.super.pochhammer(arg2);
  }

  @Override
  public IExpr polyGamma(long n) {
    try {
      Apcomplex polygamma = EvalEngine.getApfloatDouble().polygamma(n, apcomplexValue());
      return F.complexNum(polygamma.real().doubleValue(), polygamma.imag().doubleValue());
    } catch (ApfloatArithmeticException aaex) {
      if ("polygamma.ofNonpositiveInteger".equals(aaex.getLocalizationKey())) {
        return F.CComplexInfinity;
      }
    } catch (ArithmeticException | NumericComputationException aex) {
      // java.lang.ArithmeticException: Polygamma of nonpositive integer
    }
    return IComplexNum.super.polyGamma(n);
  }

  @Override
  public IExpr polyLog(IExpr arg2) {
    if (arg2 instanceof INumber) {
      try {
        Apcomplex polylog = EvalEngine.getApfloatDouble().polylog(apcomplexValue(),
            ((INumber) arg2).apcomplexValue());
        return F.complexNum(polylog.real().doubleValue(), polylog.imag().doubleValue());
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
  public IComplexNum pow(final IComplexNum val) {
    if (Complex.equals(fComplex, Complex.ZERO, Config.DOUBLE_EPSILON)) {
      IReal sn = val.re();
      if (sn.isNegative()) {
        Errors.printMessage(S.Power, "infy", F.list(F.Power(F.C0, sn)), EvalEngine.get());
        // EvalEngine.get().printMessage("Infinite expression 0^(negative number)");
        return INF;
      }
      if (sn.isZero()) {
        Errors.printMessage(S.Power, "indet", F.list(F.Power(F.C0, F.C0)), EvalEngine.get());
        // EvalEngine.get().printMessage("Infinite expression 0^0.");
        return NaN;
      }
      return ZERO;
    }
    return newInstance(fComplex.pow(((ComplexNum) val).fComplex));
  }

  @Override
  public IExpr pow(int n) {
    if (n == (-1)) {
      return inverse();
    }
    return valueOf(fComplex.pow(n));
  }

  @Override
  public long precision() throws NumericComputationException {
    return 15;
  }

  /** {@inheritDoc} */
  @Override
  public IReal re() {
    return F.num(getRealPart());
  }

  @Override
  public ComplexNum reciprocal() {
    return valueOf(fComplex.reciprocal());
  }

  @Override
  public double reDoubleValue() {
    return fComplex.getReal();
  }

  @Override
  public IExpr rootN(int n) {
    return valueOf(fComplex.rootN(n));
  }

  @Override
  public INumber roundExpr() throws ArithmeticException {
    return F.CC(F.ZZ(DoubleMath.roundToBigInteger(fComplex.getReal(), Config.ROUNDING_MODE)), //
        F.ZZ(DoubleMath.roundToBigInteger(fComplex.getImaginary(), Config.ROUNDING_MODE)));
  }

  @Override
  public IExpr sign() {
    if (isNaN() || isZero()) {
      return this;
    }
    return valueOf(fComplex.sign());
  }

  @Override
  public ComplexNum sin() {
    return valueOf(fComplex.sin());
  }

  @Override
  public ComplexNum sinc() {
    if (isZero()) {
      return ONE;
    }
    return valueOf(fComplex.sin().divide(fComplex));
  }

  @Override
  public ComplexNum sinh() {
    return valueOf(fComplex.sinh());
  }

  @Override
  public IExpr sinhIntegral() {
    return F.complexNum(GammaJS.sinhIntegral(fComplex));
    // Apcomplex sinhIntegral = EvalEngine.getApfloatDouble().sinhIntegral(apcomplexValue());
    // return F.complexNum(sinhIntegral.real().doubleValue(), sinhIntegral.imag().doubleValue());
  }

  @Override
  public IExpr sinIntegral() {
    return F.complexNum(GammaJS.sinIntegral(fComplex));
    // Apcomplex sinIntegral = EvalEngine.getApfloatDouble().sinIntegral(apcomplexValue());
    // return F.complexNum(sinIntegral.real().doubleValue(), sinIntegral.imag().doubleValue());
  }

  @Override
  public IExpr struveH(IExpr arg2) {
    try {
      return F.complexNum(BesselJS.struveH(fComplex, //
          arg2.evalfc()));
    } catch (RuntimeException e) {
      Errors.rethrowsInterruptException(e);
      // try as computation with complex numbers
    }
    return IComplexNum.super.struveH(arg2);
  }

  @Override
  public IExpr struveL(IExpr arg2) {
    try {
      return F.complexNum(BesselJS.struveL(fComplex, //
          arg2.evalfc()));
    } catch (RuntimeException e) {
      Errors.rethrowsInterruptException(e);
      // try as computation with complex numbers
    }
    return IComplexNum.super.struveL(arg2);
  }


  @Override
  public IExpr sqr() {
    return this.multiply(this);
  }

  @Override
  public IExpr sqrt() {
    return valueOf(fComplex.sqrt());
  }

  /**
   * Returns a {@code ComplexNum} whose value is {@code (this - subtrahend)}. Uses the definitional
   * formula
   *
   * <p>
   * {@code (a + bi) - (c + di) = (a-c) + (b-d)i} If either {@code this} or {@code subtrahend} has a
   * {@code NaN]} value in either part, {@link #NaN} is returned; otherwise infinite and {@code NaN}
   * values are returned in the parts of the result according to the rules for
   * {@link java.lang.Double} arithmetic.
   *
   * @param subtrahend value to be subtracted from this {@code ComplexNum}.
   * @return {@code this - subtrahend}.
   * @throws NullArgumentException if {@code subtrahend} is {@code null}.
   */
  @Override
  public IComplexNum subtract(final IComplexNum subtrahend) {
    if (subtrahend instanceof ApcomplexNum) {
      return apcomplexNumValue().subtract(((ApcomplexNum) subtrahend));
    }
    return newInstance(fComplex.subtract(((ComplexNum) subtrahend).fComplex));
  }

  @Override
  public ComplexNum tan() {
    return valueOf(fComplex.tan());
  }

  @Override
  public ComplexNum tanh() {
    return valueOf(fComplex.tanh());
  }

  /** {@inheritDoc} */
  @Override
  public IExpr times(final IExpr that) {
    if (that instanceof INumber) {
      return times((INumber) that);
    }
    return IComplexNum.super.times(that);
  }

  @Override
  public IInexactNumber times(final IInexactNumber that) {
    if (that instanceof IComplexNum) {
      if (that instanceof ApcomplexNum) {
        return apcomplexNumValue().multiply((ApcomplexNum) that);
      }
      return newInstance(fComplex.multiply(((ComplexNum) that).fComplex));
    }
    if (that instanceof INum) {
      if (that instanceof ApfloatNum) {
        return F.complexNum(apcomplexValue().multiply(((ApfloatNum) that).apcomplexValue()));
      }
      return multiply(ComplexNum.valueOf(((Num) that).getRealPart()));
    }
    throw new java.lang.ArithmeticException();
  }

  @Override
  public IInexactNumber times(final INumber that) {
    if (that instanceof IInexactNumber) {
      return times((IInexactNumber) that);
    }
    if (that instanceof IReal) {
      return this.multiply(F.complexNum(that.evalf()));
    }
    if (that instanceof ComplexSym) {
      return F.complexNum(fComplex.multiply(that.evalfc()));
    }
    throw new java.lang.ArithmeticException();
  }

  @Override
  public IExpr toDegrees() {
    // radians * (180 / Pi)
    return valueOf(fComplex.toDegrees());
  }

  @Override
  public int toIntDefault(int defaultValue) {
    if (F.isZero(fComplex.getImaginary())) {
      return F.toIntDefault(fComplex.getReal(), defaultValue);
    }
    return defaultValue;
  }

  @Override
  public IAST toPolarCoordinates() {
    return F.list(abs(), complexArg());
  }

  @Override
  public IExpr toRadians() {
    // degrees * (Pi / 180)
    return valueOf(fComplex.toRadians());
  }

  @Override
  public String toString() {
    if (ParserConfig.EXPLICIT_TIMES_OPERATOR) {
      return fComplex.toString();
    }
    StringBuilder buf = new StringBuilder();
    buf.append("(");
    double realPart = fComplex.getReal();
    double imaginaryPart = fComplex.getImaginary();
    if (realPart != 0.0 || imaginaryPart == 0.0) {
      DoubleToMMA.doubleToMMA(buf, realPart, 5, 7);
    }
    if (imaginaryPart != 0.0) {
      if (imaginaryPart < 0.0) {
        buf.append("-I*");
        imaginaryPart *= (-1);
      } else {
        if (realPart != 0.0) {
          buf.append("+");
        }
        buf.append("I*");
      }
      DoubleToMMA.doubleToMMA(buf, imaginaryPart, 5, 7);
    }
    buf.append(")");
    return buf.toString();
  }

  @Override
  public ComplexNum ulp() {
    return valueOf(fComplex.ulp());
  }

  @Override
  public IComplexNum zero() {
    return ZERO;
  }
}

package org.matheclipse.core.expression;

import java.math.BigInteger;
import java.util.Arrays;
import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatArithmeticException;
// import org.apfloat.ApfloatArithmeticException;
import org.apfloat.ApfloatMath;
import org.apfloat.Apint;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.apfloat.InfiniteExpansionException;
import org.apfloat.LossOfPrecisionException;
import org.apfloat.NumericComputationException;
import org.apfloat.OverflowException;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
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

/**
 * <code>INum</code> implementation which wraps a {@link Apfloat} value to represent a numeric
 * floating-point number.
 */
public class ApfloatNum implements INum {

  /** */
  private static final long serialVersionUID = 2500259920655377884L;

  public static final Apint MINUS_ONE = new Apint(-1);

  public static final Apint TWO = new Apint(2);

  static Apfloat apfloatRint(Apfloat fApfloat) {
    if (fApfloat.scale() > 0) {
      return ApfloatMath.roundToPrecision(fApfloat, fApfloat.scale(), Config.ROUNDING_MODE);
    }
    if (ApfloatMath.abs(fApfloat).compareTo(new Apfloat("0.5")) <= 0) {
      return Apfloat.ZERO;
    }
    return ApfloatMath.copySign(Apfloat.ONE, fApfloat);
  }

  public static String fullFormString(Apfloat apfloat) {
    String str = apfloat.toString();
    long precision = apfloat.precision();
    if (!ParserConfig.EXPLICIT_TIMES_OPERATOR) {
      int indx = str.indexOf("e");
      if (indx > 0) {
        str = str.substring(0, indx) + "`" + precision + "*^" + str.substring(indx + 1);
      } else {
        str = str + "`" + precision;
      }
    }
    return str;
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

  /**
   * Create a new instance.
   *
   * @param value
   * @return
   */
  public static ApfloatNum valueOf(final double value) {
    return valueOf(new Apfloat(value));
  }

  public static ApfloatNum valueOf(final String value, long precision) {
    return new ApfloatNum(value, precision);
  }

  Apfloat fApfloat;

  private ApfloatNum(final Apfloat value) {
    fApfloat = value;
  }

  private ApfloatNum(final BigInteger value) {
    fApfloat = new Apfloat(value, EvalEngine.getApfloat().precision());
  }

  private ApfloatNum(final String value, long precision) {
    fApfloat = new Apfloat(value, precision);
  }

  /** {@inheritDoc} */
  @Override
  public ApfloatNum abs() {
    return valueOf(EvalEngine.getApfloat().abs(fApfloat));
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

  @Override
  public IInexactNumber acos() {
    if (fApfloat.compareTo(Apfloat.ONE) == 1 //
        || fApfloat.compareTo(new Apint(-1)) == -1) {
      return F.complexNum(ApcomplexMath.acos(new Apcomplex(fApfloat)));
    }
    return valueOf(EvalEngine.getApfloat().acos(fApfloat));
  }

  @Override
  public ApfloatNum acosh() {
    return valueOf(EvalEngine.getApfloat().acosh(fApfloat));
  }

  @Override
  public IExpr add(double value) {
    return valueOf(EvalEngine.getApfloat().add(fApfloat, new Apfloat(value)));
  }

  @Override
  public INum add(final INum value) {
    return valueOf(EvalEngine.getApfloat().add(fApfloat, value.apfloatValue()));
  }

  @Override
  public IExpr agm(IExpr arg2) {
    if (arg2 instanceof IReal) {
      try {
        return valueOf(EvalEngine.getApfloat().agm(fApfloat, ((IReal) arg2).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber) {
      return F.complexNum(EvalEngine.getApfloat().agm(fApfloat, ((INumber) arg2).apcomplexValue()));
    }
    return INum.super.agm(arg2);
  }

  @Override
  public IExpr airyAi() {
    return valueOf(EvalEngine.getApfloat().airyAi(fApfloat));
  }

  @Override
  public IExpr airyAiPrime() {
    return valueOf(EvalEngine.getApfloat().airyAiPrime(fApfloat));
  }

  @Override
  public IExpr airyBi() {
    return valueOf(EvalEngine.getApfloat().airyBi(fApfloat));
  }

  @Override
  public IExpr airyBiPrime() {
    return valueOf(EvalEngine.getApfloat().airyBiPrime(fApfloat));
  }

  @Override
  public ApcomplexNum apcomplexNumValue() {
    return ApcomplexNum.valueOf(fApfloat);
  }

  @Override
  public Apcomplex apcomplexValue() {
    return new Apcomplex(fApfloat);
  }

  @Override
  public ApfloatNum apfloatNumValue() {
    return this;
  }

  @Override
  public Apfloat apfloatValue() {
    return fApfloat;
  }

  // private static Apfloat erf(Apfloat x, FixedPrecisionApfloatHelper h) {
  // Apint two = new Apint(2);
  // // 1/2
  // Aprational oneHalf = new Aprational(Apint.ONE, new Apint(2));
  // // 3/2
  // Aprational threeHalf = new Aprational(new Apint(3), new Apint(2));
  // Apfloat erf = h.hypergeometric1F1(oneHalf, threeHalf, h.multiply(x, x).negate()).multiply(two)
  // .multiply(x).divide(h.sqrt(h.pi()));
  // return erf;
  // }

  @Override
  public IInexactNumber asin() {
    if (fApfloat.compareTo(Apfloat.ONE) == 1 //
        || fApfloat.compareTo(new Apint(-1)) == -1) {
      return F.complexNum(ApcomplexMath.asin(new Apcomplex(fApfloat)));
    }
    return valueOf(EvalEngine.getApfloat().asin(fApfloat));
  }

  @Override
  public ApfloatNum asinh() {
    return valueOf(EvalEngine.getApfloat().asinh(fApfloat));
  }

  @Override
  public ApfloatNum atan() {
    return valueOf(EvalEngine.getApfloat().atan(fApfloat));
  }

  @Override
  public ApfloatNum atanh() {
    return valueOf(EvalEngine.getApfloat().atanh(fApfloat));
  }

  @Override
  public IExpr besselI(IExpr arg2) {
    if (arg2 instanceof INumber) {
      if (arg2 instanceof IReal) {
        try {
          Apfloat besselI =
              EvalEngine.getApfloat().besselI(apfloatValue(), ((IReal) arg2).apfloatValue());
          return F.num(besselI);
        } catch (ArithmeticException aex) {
          // result would be complex exception
        }
      }
      Apcomplex besselI =
          EvalEngine.getApfloat().besselI(apfloatValue(), ((INumber) arg2).apcomplexValue());
      return F.complexNum(besselI);
    }
    return INum.super.besselI(arg2);
  }

  @Override
  public IExpr besselJ(IExpr arg2) {
    if (arg2 instanceof INumber) {
      if (arg2 instanceof IReal) {
        try {
          Apfloat besselJ =
              EvalEngine.getApfloat().besselJ(apfloatValue(), ((IReal) arg2).apfloatValue());
          return F.num(besselJ);
        } catch (LossOfPrecisionException lopex) {
          if (lopex.getLocalizationKey().equals("lossOfPrecision")) {
            return F.NIL;
          }
        } catch (ArithmeticException aex) {
          // result would be complex exception
        }
      }
      Apcomplex besselJ =
          EvalEngine.getApfloat().besselJ(apfloatValue(), ((INumber) arg2).apcomplexValue());
      return F.complexNum(besselJ);
    }
    return INum.super.besselJ(arg2);
  }

  @Override
  public IExpr besselK(IExpr arg2) {
    if (arg2 instanceof INumber) {
      if (arg2 instanceof IReal) {
        try {
          Apfloat besselK =
              EvalEngine.getApfloat().besselK(apfloatValue(), ((IReal) arg2).apfloatValue());
          return F.num(besselK);
        } catch (ArithmeticException aex) {
          // result would be complex exception
        }
      }
      Apcomplex besselK =
          EvalEngine.getApfloat().besselK(apfloatValue(), ((INumber) arg2).apcomplexValue());
      return F.complexNum(besselK);
    }
    return INum.super.besselK(arg2);
  }

  @Override
  public IExpr besselY(IExpr arg2) {
    if (arg2 instanceof INumber) {
      if (arg2 instanceof IReal) {
        try {
          Apfloat besselY =
              EvalEngine.getApfloat().besselY(apfloatValue(), ((IReal) arg2).apfloatValue());
          return F.num(besselY);
        } catch (ArithmeticException aex) {
          // result would be complex exception
        }
      }
      Apcomplex besselY =
          EvalEngine.getApfloat().besselY(apfloatValue(), ((INumber) arg2).apcomplexValue());
      return F.complexNum(besselY);
    }
    return INum.super.besselY(arg2);
  }

  @Override
  public IExpr beta(IExpr b) {
    if (b instanceof IReal) {
      try {
        return valueOf(EvalEngine.getApfloat().beta(fApfloat, ((IReal) b).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (b instanceof INumber) {
      try {
        return F.complexNum(EvalEngine.getApfloat().beta(fApfloat, ((INumber) b).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // java.lang.ArithmeticException: Beta is infinite
      }
    }
    return INum.super.beta(b);
  }

  @Override
  public IExpr beta(IExpr a, IExpr b) {
    if (a instanceof IReal && b instanceof IReal) {
      Apfloat af = ((IReal) a).apfloatValue();
      Apfloat bf = ((IReal) b).apfloatValue();
      if (!(fApfloat.signum() == 0 && af.signum() < 0 && !af.isInteger()
          || fApfloat.signum() < 0 && !af.isInteger())) {
        try {
          return valueOf(EvalEngine.getApfloat().beta(fApfloat, af, bf));
        } catch (ApfloatArithmeticException aaex) {
          if ("divide.byZero".equals(aaex.getLocalizationKey())) {
            return F.ComplexInfinity;
          }
        } catch (ArithmeticException | NumericComputationException e) {
          if ("Division by zero".equals(e.getMessage())) {
            return F.ComplexInfinity;
          }
        }
      }
    }
    if (a instanceof INumber && b instanceof INumber) {
      try {
        return F.complexNum(EvalEngine.getApfloat().beta(fApfloat, ((INumber) a).apcomplexValue(),
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
    return INum.super.beta(a, b);
  }

  @Override
  public IExpr beta(IExpr x2, IExpr a, IExpr b) {
    if (x2 instanceof IReal && a instanceof IReal && b instanceof IReal) {
      Apfloat x2f = ((IReal) x2).apfloatValue();
      Apfloat af = ((IReal) a).apfloatValue();
      Apfloat bf = ((IReal) b).apfloatValue();
      if (!((fApfloat.signum() == 0 || x2f.signum() == 0) && af.signum() < 0 && !af.isInteger()
          || (fApfloat.signum() < 0 || x2f.signum() < 0) && !af.isInteger())) {
        try {
          return valueOf(EvalEngine.getApfloat().beta(fApfloat, x2f, af, bf));
        } catch (ArithmeticException | NumericComputationException e) {
          // try as computation with complex numbers
        }
      }
    }
    if (x2 instanceof INumber && a instanceof INumber && b instanceof INumber) {
      try {
        return F.complexNum(EvalEngine.getApfloat().beta(fApfloat, ((INumber) x2).apcomplexValue(),
            ((INumber) a).apcomplexValue(), ((INumber) b).apcomplexValue()));
      } catch (NumericComputationException aex) {
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
    return INum.super.beta(x2, a, b);
  }

  @Override
  public ApfloatNum cbrt() {
    return valueOf(EvalEngine.getApfloat().cbrt(fApfloat));
  }

  @Override
  public IExpr ceil() {
    return valueOf(EvalEngine.getApfloat().ceil(fApfloat));
  }

  /** {@inheritDoc} */
  @Override
  public IInteger ceilFraction() {
    return F.ZZ(ApfloatMath.ceil(fApfloat).toBigInteger());
  }

  @Override
  public IExpr chebyshevT(IExpr arg2) {
    if (arg2 instanceof INumber) {
      if (arg2 instanceof IReal) {
        try {
          Apfloat chebyshevT =
              EvalEngine.getApfloat().chebyshevT(apfloatValue(), ((IReal) arg2).apfloatValue());
          return F.complexNum(chebyshevT);
        } catch (ArithmeticException | NumericComputationException are) {
          // java.lang.ArithmeticException: Result would be complex
        }
      }
      try {
        Apcomplex chebyshevT =
            EvalEngine.getApfloat().chebyshevT(apfloatValue(), ((INumber) arg2).apcomplexValue());
        return F.complexNum(chebyshevT);
      } catch (NumericComputationException are) {

      }
    }
    return INum.super.chebyshevT(arg2);
  }

  @Override
  public IExpr chebyshevU(IExpr arg2) {
    if (arg2 instanceof INumber) {
      if (arg2 instanceof IReal) {
        try {
          Apfloat chebyshevU =
              EvalEngine.getApfloat().chebyshevU(apfloatValue(), ((IReal) arg2).apfloatValue());
          return F.complexNum(chebyshevU);
        } catch (ArithmeticException | NumericComputationException are) {

        }
      }
      try {
        Apcomplex chebyshevU =
            EvalEngine.getApfloat().chebyshevU(apfloatValue(), ((INumber) arg2).apcomplexValue());
        return F.complexNum(chebyshevU);
      } catch (ArithmeticException | NumericComputationException are) {

      }
    }
    return INum.super.chebyshevU(arg2);
  }

  /** {@inheritDoc} */
  @Override
  public int compareAbsValueToOne() {
    return EvalEngine.getApfloat().abs(fApfloat).compareTo(Apfloat.ONE);
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
        try {
          return fApfloat.compareTo(((IReal) expr).apfloatValue());
        } catch (NumberFormatException nfe) {
          return IExpr.compareHierarchy(this, expr);
        }
      }
      int c = this.compareTo(((INumber) expr).re());
      if (c != 0) {
        return c;
      }

    }
    return IExpr.compareHierarchy(this, expr);
  }

  /** {@inheritDoc} */
  @Override
  public IExpr complexArg() {
    try {
      return F.num(EvalEngine.getApfloat().arg(fApfloat));
    } catch (ArithmeticException | NumericComputationException ex) {
      // Indeterminate expression `1` encountered.
      Errors.printMessage(S.Arg, "indet", F.list(F.Arg(this)), EvalEngine.get());
      return S.Indeterminate;
    }
  }

  @Override
  public ComplexNum complexNumValue() {
    return ComplexNum.valueOf(fApfloat.doubleValue());
  }

  /** {@inheritDoc} */
  @Override
  public int complexSign() {
    return fApfloat.signum();
  }

  @Override
  public IExpr copy() {
    return this;
  }

  @Override
  public IExpr copySign(double d) {
    return valueOf(EvalEngine.getApfloat().copySign(fApfloat, new Apfloat(d)));
  }

  @Override
  public ApfloatNum cos() {
    return valueOf(EvalEngine.getApfloat().cos(fApfloat));
  }

  @Override
  public ApfloatNum cosh() {
    return valueOf(EvalEngine.getApfloat().cosh(fApfloat));
  }

  @Override
  public IExpr coshIntegral() {
    try {
      if (isNonNegativeResult()) {
        return valueOf(EvalEngine.getApfloat().coshIntegral(fApfloat));
      }
    } catch (ArithmeticException aex) {
      // java.lang.ArithmeticException: Result would be complex
    }
    Apcomplex coshIntegral = EvalEngine.getApfloat().coshIntegral(apcomplexValue());
    return F.complexNum(coshIntegral);
  }

  @Override
  public IExpr cosIntegral() {
    try {
      if (isNonNegativeResult()) {
        return valueOf(EvalEngine.getApfloat().cosIntegral(fApfloat));
      }
    } catch (ArithmeticException aex) {
      // java.lang.ArithmeticException: Result would be complex
    }
    Apcomplex cosIntegral = EvalEngine.getApfloat().cosIntegral(apcomplexValue());
    return F.complexNum(cosIntegral);
  }

  /** {@inheritDoc} */
  @Override
  public IExpr dec() {
    return add(F.CND1);
  }

  /** {@inheritDoc} */
  @Override
  public long determinePrecision(boolean postParserProcessing) {
    return precision();
  }

  @Override
  public IExpr digamma() {
    try {
      return valueOf(EvalEngine.getApfloat().digamma(fApfloat));
    } catch (ArithmeticException | NumericComputationException aex) {
    }
    return INum.super.digamma();
  }

  // public Apfloat apfloatValue() {
  // return fApfloat;
  // }

  @Override
  public ApfloatNum divide(double value) {
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
  public INum divide(final INum value) {
    return valueOf(EvalEngine.getApfloat().divide(fApfloat, value.apfloatValue()));
  }

  @Override
  public IReal divideBy(IReal that) {
    return valueOf(EvalEngine.getApfloat().divide(fApfloat, that.apfloatValue()));
  }

  /** @return */
  @Override
  public double doubleValue() {
    return fApfloat.doubleValue();
  }

  @Override
  public IExpr ellipticE() {
    try {
      return valueOf(EvalEngine.getApfloat().ellipticE(fApfloat));
    } catch (ArithmeticException aex) {
      // java.lang.ArithmeticException: Result would be complex
    }
    return F.complexNum(EvalEngine.getApfloat().ellipticE(apcomplexValue()));
  }

  @Override
  public IExpr ellipticK() {
    try {
      return valueOf(EvalEngine.getApfloat().ellipticK(fApfloat));
    } catch (ArithmeticException aex) {
      // java.lang.ArithmeticException: Result would be complex
    }
    return F.complexNum(EvalEngine.getApfloat().ellipticE(apcomplexValue()));
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
  public boolean equalsInt(final int i) {
    try {
      return fApfloat.intValueExact() == i;
    } catch (RuntimeException rex) {
      // ArithmeticException
      Errors.rethrowsInterruptException(rex);
    }
    return false;
  }

  @Override
  public IExpr erf() {
    return valueOf(EvalEngine.getApfloat().erf(fApfloat));
    // FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    // try {
    // Apfloat erf = erf(fApfloat, h);
    // return F.num(erf);
    // } catch (Exception ce) {
    // //
    // }
    // return F.NIL;
  }

  @Override
  public IExpr erfc() {
    try {
      return valueOf(EvalEngine.getApfloat().erfc(fApfloat));
    } catch (OverflowException of) {
      // return Underflow? https://github.com/mtommila/apfloat/issues/38
      return F.Overflow();
    } catch (ArithmeticException | NumericComputationException e) {
      e.printStackTrace();
    }
    // FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    // try {
    // Apfloat c = erf(fApfloat, h);
    // return F.num(h.subtract(Apcomplex.ONE, c));
    // } catch (Exception ce) {
    // //
    // }
    return INum.super.erfc();
  }

  @Override
  public IExpr erfi() {
    return valueOf(EvalEngine.getApfloat().erfi(fApfloat));
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
    final long precision = fApfloat.precision();
    if (precision != Apfloat.INFINITE && engine.getNumericPrecision() < precision
        && engine.isNumericMode()) {
      return valueOf(EvalEngine.getApfloat().valueOf(fApfloat));
    }
    return F.NIL;
  }

  @Override
  public ApfloatNum exp() {
    return valueOf(EvalEngine.getApfloat().exp(fApfloat));
  }

  @Override
  public IExpr expIntegralE(IExpr z) {
    if (z instanceof IReal) {
      try {
        return valueOf(EvalEngine.getApfloat().expIntegralE(fApfloat, ((IReal) z).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (z instanceof INumber) {
      return F.complexNum(
          EvalEngine.getApfloat().expIntegralE(fApfloat, ((INumber) z).apcomplexValue()));
    }
    return INum.super.expIntegralE(z);
  }

  @Override
  public IExpr expIntegralEi() {
    return valueOf(EvalEngine.getApfloat().expIntegralEi(fApfloat));
  }

  @Override
  public IExpr expm1() {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    return valueOf(h.subtract(h.exp(fApfloat), Apfloat.ONE));
  }

  @Override
  public IExpr factorial() {
    try {
      FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
      return valueOf(h.gamma(h.add(fApfloat, Apfloat.ONE)));
    } catch (ArithmeticException | NumericComputationException e) {
      // try as computation with complex numbers
    }
    try {
      return F.complexNum(EvalEngine.getApfloat().gamma(apcomplexValue().add(Apfloat.ONE)));
    } catch (ApfloatArithmeticException aaex) {
      if ("gamma.ofZero".equals(aaex.getLocalizationKey())) {
        return F.ComplexInfinity;
      }
      if ("gamma.ofNegativeInteger".equals(aaex.getLocalizationKey())) {
        return F.ComplexInfinity;
      }
    }
    return INum.super.factorial();
  }

  @Override
  public IExpr fibonacci(IExpr arg2) {
    if (arg2 instanceof IReal) {
      try {
        return valueOf(EvalEngine.getApfloat().fibonacci(fApfloat, ((IReal) arg2).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber) {
      try {
        return F.complexNum(
            EvalEngine.getApfloat().fibonacci(fApfloat, ((INumber) arg2).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return INum.super.fibonacci(arg2);
  }

  @Override
  public IExpr floor() {
    return valueOf(EvalEngine.getApfloat().floor(fApfloat));
  }

  /** {@inheritDoc} */
  @Override
  public IInteger floorFraction() {
    return F.ZZ(ApfloatMath.floor(fApfloat).toBigInteger());
  }

  /** {@inheritDoc} */
  @Override
  public IReal fractionalPart() {
    return F.num(fApfloat.frac());
  }

  @Override
  public IExpr fresnelC() {
    return valueOf(EvalEngine.getApfloat().fresnelC(fApfloat));
  }

  @Override
  public IExpr fresnelS() {
    return valueOf(EvalEngine.getApfloat().fresnelS(fApfloat));
  }

  /** {@inheritDoc} */
  @Override
  public String fullFormString() {
    return fullFormString(fApfloat);
  }

  @Override
  public IExpr gamma() {
    if (isZero() || isMathematicalIntegerNegative()) {
      return F.CComplexInfinity;
    }
    try {
      return valueOf(EvalEngine.getApfloat().gamma(fApfloat));
    } catch (OverflowException of) {
      return F.Overflow();
    } catch (ArithmeticException | NumericComputationException are) {
      return Errors.printMessage(S.Gamma, are, EvalEngine.get());
    }
    // return INum.super.gamma();
  }

  @Override
  public IExpr gamma(IExpr x) {
    if (isZero() && x.isZero()) {
      return F.CInfinity;
    }
    if (x instanceof IReal) {
      if (!(x.isNegative() && !(isMathematicalIntegerNonNegative() && !isZero()))) {
        try {
          return valueOf(EvalEngine.getApfloat().gamma(fApfloat, ((IReal) x).apfloatValue()));
        } catch (ArithmeticException | NumericComputationException e) {
          // try as computation with complex numbers
        }
      }
    }
    if (x instanceof INumber) {
      try {
        return F
            .complexNum(EvalEngine.getApfloat().gamma(fApfloat, ((INumber) x).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException are) {
        // try as computation with complex numbers
        // java.lang.ArithmeticException: Upper gamma with first argument real part nonpositive and
        // second argment zero
        return Errors.printMessage(S.Gamma, are, EvalEngine.get());
      }
    }
    return INum.super.gamma(x);
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
    if (x0 instanceof IReal && x1 instanceof IReal) {
      try {
        return valueOf(EvalEngine.getApfloat().gamma(fApfloat, ((IReal) x0).apfloatValue(),
            ((IReal) x1).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (x0 instanceof INumber && x1 instanceof INumber) {
      try {
        return F.complexNum(EvalEngine.getApfloat().gamma(fApfloat, ((INumber) x0).apcomplexValue(),
            ((INumber) x1).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException are) {
        return Errors.printMessage(S.Gamma, are, EvalEngine.get());
      }
    }
    return INum.super.gamma(x0, x1);
  }

  @Override
  public IExpr gegenbauerC(IExpr arg2) {
    if (arg2 instanceof IReal) {
      try {
        return valueOf(
            EvalEngine.getApfloat().gegenbauerC(fApfloat, ((IReal) arg2).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber) {
      try {
        return F.complexNum(
            EvalEngine.getApfloat().gegenbauerC(fApfloat, ((INumber) arg2).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return INum.super.gegenbauerC(arg2);
  }

  @Override
  public IExpr gegenbauerC(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof IReal && arg3 instanceof IReal) {
      try {
        return valueOf(EvalEngine.getApfloat().gegenbauerC(fApfloat, ((IReal) arg2).apfloatValue(),
            ((IReal) arg3).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      try {
        return F.complexNum(EvalEngine.getApfloat().gegenbauerC(fApfloat,
            ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return INum.super.gegenbauerC(arg2, arg3);
  }

  @Override
  public IExpr getAddendum() {
    return isFinite() ? valueOf(fApfloat.subtract(fApfloat.real())) : zero();
  }

  @Override
  public ApfloatNum getPi() {
    return valueOf(EvalEngine.getApfloat().pi());
  }

  @Override
  public double getReal() {
    return fApfloat.doubleValue();
  }

  @Override
  public double getRealPart() {
    double temp = fApfloat.doubleValue();
    if (temp == (-0.0)) {
      temp = 0.0;
    }
    return temp;
  }

  @Override
  public IExpr harmonicNumber() {
    Apfloat harmonicNumber = EvalEngine.getApfloat().harmonicNumber(apfloatValue());
    return F.num(harmonicNumber);
  }

  @Override
  public IExpr harmonicNumber(IExpr r) {
    if (r instanceof INumber) {
      if (r instanceof IReal) {
        if (this.isGE(F.C1) || r.isInteger()) {
          try {
            Apfloat harmonicNumber =
                EvalEngine.getApfloat().harmonicNumber(fApfloat, ((IReal) r).apfloatValue());
            return F.num(harmonicNumber);
          } catch (ArithmeticException | NumericComputationException e) {
            // try as computation with complex numbers
          }
        }
      }
      try {
        Apcomplex harmonicNumber =
            EvalEngine.getApfloat().harmonicNumber(fApfloat, ((INumber) r).apcomplexValue());
        return F.complexNum(harmonicNumber);
      } catch (ArithmeticException | NumericComputationException e) {
      }
    }
    return INum.super.harmonicNumber(r);
  }

  @Override
  public final int hashCode() {
    return fApfloat.hashCode();
  }

  @Override
  public ISymbol head() {
    return S.Real;
  }

  @Override
  public IExpr hermiteH(IExpr arg2) {
    if (arg2 instanceof INumber) {
      if (arg2 instanceof IReal) {
        try {
          Apfloat hermiteH =
              EvalEngine.getApfloat().hermiteH(fApfloat, ((IReal) arg2).apfloatValue());
          return F.num(hermiteH);
        } catch (ArithmeticException | NumericComputationException are) {

        }
      }
      try {
        Apcomplex hermiteH =
            EvalEngine.getApfloat().hermiteH(fApfloat, ((INumber) arg2).apcomplexValue());
        return F.complexNum(hermiteH);
      } catch (ArithmeticException | NumericComputationException are) {

      }
    }
    return INum.super.hermiteH(arg2);
  }

  @Override
  public int hierarchy() {
    return DOUBLEID;
  }

  @Override
  public IExpr hypergeometric0F1(IExpr arg2) {
    if (arg2 instanceof IReal) {
      try {
        return valueOf(
            EvalEngine.getApfloat().hypergeometric0F1(fApfloat, ((IReal) arg2).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber) {
      return F.complexNum(
          EvalEngine.getApfloat().hypergeometric0F1(fApfloat, ((INumber) arg2).apcomplexValue()));
    }
    return INum.super.hypergeometric0F1(arg2);
  }

  @Override
  public IExpr hypergeometric0F1Regularized(IExpr arg2) {
    if (arg2 instanceof IReal) {
      try {
        return valueOf(EvalEngine.getApfloat().hypergeometric0F1Regularized(fApfloat,
            ((IReal) arg2).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber) {
      return F.complexNum(EvalEngine.getApfloat().hypergeometric0F1Regularized(fApfloat,
          ((INumber) arg2).apcomplexValue()));
    }
    return INum.super.hypergeometric0F1Regularized(arg2);
  }


  @Override
  public IExpr hypergeometric1F1(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof IReal && arg3 instanceof IReal) {
      try {
        return valueOf(EvalEngine.getApfloat().hypergeometric1F1(fApfloat,
            ((IReal) arg2).apfloatValue(), ((IReal) arg3).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      return F.complexNum(EvalEngine.getApfloat().hypergeometric1F1(fApfloat,
          ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue()));
    }
    return INum.super.hypergeometric1F1(arg2, arg3);
  }

  @Override
  public IExpr hypergeometric1F1Regularized(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof IReal && arg3 instanceof IReal) {
      try {
        return valueOf(EvalEngine.getApfloat().hypergeometric1F1Regularized(fApfloat,
            ((IReal) arg2).apfloatValue(), ((IReal) arg3).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      return F.complexNum(EvalEngine.getApfloat().hypergeometric1F1Regularized(fApfloat,
          ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue()));
    }
    return INum.super.hypergeometric1F1Regularized(arg2, arg3);
  }

  @Override
  public IExpr hypergeometric2F1(IExpr arg2, IExpr arg3, IExpr arg4) {
    if (arg2 instanceof IReal && arg3 instanceof IReal && arg4 instanceof IReal) {
      try {
        return valueOf(
            EvalEngine.getApfloat().hypergeometric2F1(fApfloat, ((IReal) arg2).apfloatValue(),
                ((IReal) arg3).apfloatValue(), ((IReal) arg4).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber && arg3 instanceof INumber && arg4 instanceof INumber) {
      try {
        return F.complexNum(
            EvalEngine.getApfloat().hypergeometric2F1(fApfloat, ((INumber) arg2).apcomplexValue(),
                ((INumber) arg3).apcomplexValue(), ((INumber) arg4).apcomplexValue()));
      } catch (ApfloatArithmeticException aaex) {
        if ("divide.byZero".equals(aaex.getLocalizationKey())) {
          return F.ComplexInfinity;
        }
      } catch (ArithmeticException | NumericComputationException aex) {
        if (aex.getMessage().equals("Division by zero")) {
          return F.ComplexInfinity;
        }
      }
    }
    return INum.super.hypergeometric2F1(arg2, arg3, arg4);
  }

  @Override
  public IExpr hypergeometric2F1Regularized(IExpr arg2, IExpr arg3, IExpr arg4) {
    if (arg2 instanceof IReal && arg3 instanceof IReal && arg4 instanceof IReal) {
      try {
        return valueOf(EvalEngine.getApfloat().hypergeometric2F1Regularized(fApfloat,
            ((IReal) arg2).apfloatValue(), ((IReal) arg3).apfloatValue(),
            ((IReal) arg4).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber && arg3 instanceof INumber && arg4 instanceof INumber) {
      try {
        return F.complexNum(EvalEngine.getApfloat().hypergeometric2F1Regularized(fApfloat,
            ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue(),
            ((INumber) arg4).apcomplexValue()));
      } catch (NumericComputationException ex) {
        // org.apfloat.OverflowException: Apfloat disk file storage is disabled
      }
    }
    return INum.super.hypergeometric2F1Regularized(arg2, arg3, arg4);
  }

  @Override
  public IExpr hypergeometricU(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof IReal && arg3 instanceof IReal) {
      try {
        return valueOf(EvalEngine.getApfloat().hypergeometricU(fApfloat,
            ((IReal) arg2).apfloatValue(), ((IReal) arg3).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      return F.complexNum(EvalEngine.getApfloat().hypergeometricU(fApfloat,
          ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue()));
    }
    return INum.super.hypergeometricU(arg2, arg3);
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
    return add(F.CD1);
  }

  /** {@inheritDoc} */
  @Override
  public IInteger integerPart() {
    return isNegative() ? ceilFraction() : floorFraction();
  }

  /** @return */
  @Override
  public int intValue() {
    return fApfloat.intValue();
  }

  @Override
  public ApfloatNum inverse() {
    if (isOne()) {
      return this;
    }
    return valueOf(EvalEngine.getApfloat().inverseRoot(fApfloat, 1));
  }

  @Override
  public IExpr inverseErf() {
    if (MINUS_ONE.compareTo(fApfloat) == -1 && fApfloat.compareTo(Apint.ONE) == -1) {
      return valueOf(EvalEngine.getApfloat().inverseErf(fApfloat));
    }
    return INum.super.inverseErf();
  }

  @Override
  public IExpr inverseErfc() {
    if (Apint.ZERO.compareTo(fApfloat) == -1 && fApfloat.compareTo(TWO) == -1) {
      // 0.0 < fApfloat && fApfloat < 2.0
      return valueOf(EvalEngine.getApfloat().inverseErfc(fApfloat));
    }
    return INum.super.inverseErfc();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isE() {
    return fApfloat.compareTo(EvalEngine.getApfloat().exp(Apfloat.ONE)) == 0;
  }

  @Override
  public boolean isGT(IReal that) {
    if (that instanceof ApfloatNum) {
      return fApfloat.compareTo(((ApfloatNum) that).fApfloat) > 0;
    }
    return doubleValue() > that.doubleValue();
  }

  @Override
  public boolean isLT(IReal that) {
    if (that instanceof ApfloatNum) {
      return fApfloat.compareTo(((ApfloatNum) that).fApfloat) < 0;
    }
    return doubleValue() < that.doubleValue();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isMathematicalIntegerNegative() {
    return (fApfloat.isInteger() && fApfloat.signum() < 0);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isMathematicalIntegerNonNegative() {
    return (fApfloat.isInteger() && fApfloat.signum() >= 0);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isMinusOne() {
    return fApfloat.compareTo(ApfloatNum.MINUS_ONE) == 0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNegative() {
    return fApfloat.signum() == -1;
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
    return fApfloat.equals(new Apfloat(value.toBigNumerator(), precision)
        .divide(new Apfloat(value.toBigDenominator(), precision)));
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNumIntValue() {
    return fApfloat.frac().equals(Apfloat.ZERO);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isOne() {
    return fApfloat.compareTo(Apfloat.ONE) == 0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPi() {
    return fApfloat.compareTo(EvalEngine.getApfloat().pi()) == 0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPositive() {
    return fApfloat.signum() > 0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isRationalValue(IRational value) {
    return fApfloat.compareTo(value.apfloatNumValue().fApfloat) == 0;
  }

  @Override
  public boolean isSame(IExpr expression, double epsilon) {
    if (expression instanceof ApfloatNum) {
      return fApfloat.compareTo(((ApfloatNum) expression).fApfloat) == 0;
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isZero() {
    return fApfloat.isZero();
  }

  @Override
  public boolean isZero(double tolerance) {
    return F.isZero(fApfloat.doubleValue(), tolerance);
  }

  @Override
  public IExpr jacobiP(IExpr arg2, IExpr arg3, IExpr arg4) {
    if (arg2 instanceof IReal && arg3 instanceof IReal && arg4 instanceof IReal) {
      try {
        return valueOf(EvalEngine.getApfloat().jacobiP(fApfloat, ((IReal) arg2).apfloatValue(),
            ((IReal) arg3).apfloatValue(), ((IReal) arg4).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber && arg3 instanceof INumber && arg4 instanceof INumber) {
      try {
        return F
            .complexNum(EvalEngine.getApfloat().jacobiP(fApfloat, ((INumber) arg2).apcomplexValue(),
                ((INumber) arg3).apcomplexValue(), ((INumber) arg4).apcomplexValue()));
      } catch (ArithmeticException aex) {
        //
      }
    }
    return INum.super.jacobiP(arg2, arg3, arg4);
  }

  @Override
  public IExpr laguerreL(IExpr arg2) {
    if (arg2 instanceof IReal) {
      try {
        return valueOf(EvalEngine.getApfloat().laguerreL(fApfloat, ((IReal) arg2).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber) {
      try {
        return F.complexNum(
            EvalEngine.getApfloat().laguerreL(fApfloat, ((INumber) arg2).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return INum.super.laguerreL(arg2);
  }

  @Override
  public IExpr laguerreL(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof IReal && arg3 instanceof IReal) {
      try {
        return valueOf(EvalEngine.getApfloat().laguerreL(fApfloat, ((IReal) arg2).apfloatValue(),
            ((IReal) arg3).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      try {
        return F.complexNum(EvalEngine.getApfloat().laguerreL(fApfloat,
            ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return INum.super.laguerreL(arg2, arg3);
  }

  @Override
  public long leafCountSimplify() {
    return 2;
  }

  @Override
  public IExpr legendreP(IExpr arg2) {
    if (arg2 instanceof IReal) {
      try {
        return valueOf(EvalEngine.getApfloat().legendreP(fApfloat, ((IReal) arg2).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber) {
      try {
        return F.complexNum(
            EvalEngine.getApfloat().legendreP(fApfloat, ((INumber) arg2).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return INum.super.legendreP(arg2);
  }

  @Override
  public IExpr legendreP(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof IReal && arg3 instanceof IReal) {
      try {
        return valueOf(EvalEngine.getApfloat().legendreP(fApfloat, ((IReal) arg2).apfloatValue(),
            ((IReal) arg3).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      try {
        return F.complexNum(EvalEngine.getApfloat().legendreP(fApfloat,
            ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return INum.super.legendreP(arg2, arg3);
  }

  @Override
  public IExpr legendreQ(IExpr arg2) {
    if (arg2 instanceof IReal) {
      try {
        return valueOf(EvalEngine.getApfloat().legendreQ(fApfloat, ((IReal) arg2).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber) {
      try {
        return F.complexNum(
            EvalEngine.getApfloat().legendreQ(fApfloat, ((INumber) arg2).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return INum.super.legendreQ(arg2);
  }

  @Override
  public IExpr legendreQ(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof IReal && arg3 instanceof IReal) {
      try {
        return valueOf(EvalEngine.getApfloat().legendreQ(fApfloat, ((IReal) arg2).apfloatValue(),
            ((IReal) arg3).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      try {
        return F.complexNum(EvalEngine.getApfloat().legendreQ(fApfloat,
            ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return INum.super.legendreQ(arg2, arg3);
  }

  @Override
  public IInexactNumber log() {
    if (isNegative()) {
      return ApcomplexNum.valueOf(EvalEngine.getApfloat().log(apcomplexValue()));
    }
    return valueOf(EvalEngine.getApfloat().log(fApfloat));
  }

  @Override
  public IExpr log(final IExpr base) {
    if (base instanceof INumber) {
      if (base.isZero()) {
        return S.Indeterminate;
      }
      try {
        if (base instanceof IReal) {
          if (isNegative()) {
            return ApcomplexNum.valueOf(
                EvalEngine.getApfloat().log(apcomplexValue(), ((INumber) base).apcomplexValue()));
          }
          return valueOf(EvalEngine.getApfloat().log(fApfloat, ((IReal) base).apfloatValue()));
        }
        return ApcomplexNum.valueOf(
            EvalEngine.getApfloat().log(apcomplexValue(), ((INumber) base).apcomplexValue()));
      } catch (ApfloatArithmeticException aex) {
        if (aex.getLocalizationKey().equals("divide.byZero")) {
          // log(x,0) is undefined
          return F.CComplexInfinity;
        }
      }
    }
    return INum.super.log(base);
  }

  @Override
  public ApfloatNum log10() {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    return valueOf(h.log(fApfloat, new Apfloat(10)));
  }

  @Override
  public ApfloatNum log1p() {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    return valueOf(h.log(h.add(fApfloat, Apfloat.ONE)));
  }

  @Override
  public IExpr logGamma() {
    if (isPositive()) {
      try {
        return valueOf(EvalEngine.getApfloat().logGamma(fApfloat));
      } catch (ArithmeticException | NumericComputationException ex) {
      }
    }
    try {
      Apcomplex logGamma = EvalEngine.getApfloat().logGamma(apcomplexValue());
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
    return INum.super.logGamma();
  }

  @Override
  public IExpr logIntegral() {
    try {
      if (isNonNegativeResult()) {
        return valueOf(EvalEngine.getApfloat().logIntegral(fApfloat));
      }
    } catch (ArithmeticException | NumericComputationException ex) {
      // java.lang.ArithmeticException: Result would be complex
    }
    Apcomplex logIntegral = EvalEngine.getApfloat().logIntegral(apcomplexValue());
    return F.complexNum(logIntegral);
  }

  @Override
  public IExpr logisticSigmoid() {
    try {
      return valueOf(EvalEngine.getApfloat().logisticSigmoid(fApfloat));
    } catch (NumericComputationException ex) {
    }
    Apcomplex logisticSigmoid = EvalEngine.getApfloat().logisticSigmoid(apcomplexValue());
    return F.complexNum(logisticSigmoid);
  }

  /** @return */
  public long longValue() {
    return fApfloat.longValue();
  }

  @Override
  public ApfloatNum multiply(double value) {
    return valueOf(EvalEngine.getApfloat().multiply(fApfloat, new Apfloat(value)));
  }

  @Override
  public IExpr multiply(int value) {
    return valueOf(EvalEngine.getApfloat().multiply(fApfloat, new Apfloat(value)));
  }

  @Override
  public INum multiply(final INum value) {
    return valueOf(EvalEngine.getApfloat().multiply(fApfloat, value.apfloatValue()));
  }

  /** @return */
  @Override
  public ApfloatNum negate() {
    return valueOf(EvalEngine.getApfloat().negate(fApfloat));
  }

  @Override
  public ApfloatNum newInstance(double d) {
    return valueOf(d);
  }

  @Override
  public Num numValue() {
    return Num.valueOf(doubleValue());
  }

  @Override
  public ApfloatNum one() {
    return valueOf(Apfloat.ONE);
  }

  /** @return */
  @Override
  public ApfloatNum opposite() {
    return valueOf(EvalEngine.getApfloat().negate(fApfloat));
  }

  @Override
  public IExpr plus(final IExpr that) {
    if (that instanceof INumber) {
      return plus((INumber) that);
    }
    return INum.super.plus(that);
  }

  @Override
  public IInexactNumber plus(final IInexactNumber that) {
    if (that instanceof INum) {
      if (that instanceof ApfloatNum) {
        return add((ApfloatNum) that);
      }
      return add(ApfloatNum.valueOf(((Num) that).getRealPart()));
    }
    if (that instanceof IComplexNum) {
      if (that instanceof ApcomplexNum) {
        return ApcomplexNum.valueOf(fApfloat).add((ApcomplexNum) that);
      }
      ComplexNum cn = (ComplexNum) that;
      return ApcomplexNum.valueOf(fApfloat)
          .add(ApcomplexNum.valueOf(cn.getRealPart(), cn.getImaginaryPart()));
    }
    throw new java.lang.ArithmeticException();
  }

  @Override
  public INumber plus(final INumber that) {
    if (that instanceof IInexactNumber) {
      return plus((IInexactNumber) that);
    }
    if (that instanceof IReal) {
      return ApfloatNum
          .valueOf(EvalEngine.getApfloat().add(fApfloat, ((IReal) that).apfloatValue()));
    }
    if (that instanceof ComplexSym) {
      return F.complexNum(fApfloat.add(that.apcomplexValue()));
    }
    throw new java.lang.ArithmeticException();
  }

  @Override
  public IExpr pochhammer(IExpr arg2) {
    if (arg2 instanceof INumber) {
      if (arg2 instanceof IReal) {
        Apfloat pochhammer =
            EvalEngine.getApfloat().pochhammer(apfloatValue(), ((IReal) arg2).apfloatValue());
        return F.num(pochhammer);
      }
      Apcomplex pochhammer =
          EvalEngine.getApfloat().pochhammer(apfloatValue(), ((INumber) arg2).apcomplexValue());
      return F.complexNum(pochhammer);
    }
    return INum.super.pochhammer(arg2);
  }

  @Override
  public IExpr polyGamma(long n) {
    try {
      Apfloat polygamma = EvalEngine.getApfloat().polygamma(n, fApfloat);
      return F.num(polygamma);
    } catch (ApfloatArithmeticException aaex) {
      if ("polygamma.ofNonpositiveInteger".equals(aaex.getLocalizationKey())) {
        return F.ComplexInfinity;
      }
    } catch (ArithmeticException | NumericComputationException aex) {
      // java.lang.ArithmeticException: Polygamma of non-positive integer
    }
    try {
      Apcomplex polygamma = EvalEngine.getApfloat().polygamma(n, apcomplexValue());
      return F.complexNum(polygamma);
    } catch (ApfloatArithmeticException aaex) {
      if ("polygamma.ofNonpositiveInteger".equals(aaex.getLocalizationKey())) {
        return F.ComplexInfinity;
      }
    } catch (ArithmeticException | NumericComputationException aex) {
      // java.lang.ArithmeticException: Polygamma of nonpositive integer
    }
    return INum.super.polyGamma(n);
  }

  @Override
  public IExpr polyLog(IExpr arg2) {
    if (arg2 instanceof INumber) {
      if (arg2 instanceof IReal && ((IReal) arg2).isLE(F.C1)) {
        try {
          return valueOf(EvalEngine.getApfloat().polylog(fApfloat, ((IReal) arg2).apfloatValue()));
        } catch (ArithmeticException | NumericComputationException e) {
          // java.lang.ArithmeticException: Result would be complex
        }
      }

      try {
        return F.complexNum(
            EvalEngine.getApfloat().polylog(fApfloat, ((INumber) arg2).apcomplexValue()));
      } catch (LossOfPrecisionException lope) {
        // Complete loss of precision
      } catch (InfiniteExpansionException iee) {
        // Cannot calculate power to infinite precision
      } catch (ArithmeticException | NumericComputationException ex) {
      }
    }
    return INum.super.polyLog(arg2);
  }

  @Override
  public ApfloatNum pow(double value) {
    return valueOf(EvalEngine.getApfloat().pow(fApfloat, new Apfloat(value)));
  }

  @Override
  public ApfloatNum pow(int n) {
    if (n == (-1)) {
      return inverse();
    }
    return valueOf(EvalEngine.getApfloat().pow(fApfloat, n));
  }

  @Override
  public ApfloatNum power(long n) {
    if (n == (-1L)) {
      return inverse();
    }
    return valueOf(EvalEngine.getApfloat().pow(fApfloat, n));
  }

  @Override
  public INum pow(final INum value) {
    return valueOf(EvalEngine.getApfloat().pow(fApfloat, value.apfloatValue()));
  }

  @Override
  public IExpr power(final IExpr that) {
    if (that instanceof IComplexNum) {
      return F
          .complexNum(EvalEngine.getApfloat().pow(fApfloat, ((IComplexNum) that).apcomplexValue()));
    }
    if (that instanceof INum) {
      if (fApfloat.compareTo(Apfloat.ZERO) < 0) {
        return F.complexNum(EvalEngine.getApfloat().pow(fApfloat, ((INum) that).apcomplexValue()));
      }
      return valueOf(EvalEngine.getApfloat().pow(fApfloat, ((INum) that).apfloatValue()));
    }
    return INum.super.power(that);
  }

  @Override
  public long precision() throws NumericComputationException {
    return fApfloat.precision();
  }

  /** {@inheritDoc} */
  @Override
  public IReal re() {
    return this;
  }

  @Override
  public ApfloatNum reciprocal() {
    return valueOf(EvalEngine.getApfloat().inverseRoot(fApfloat, 1));
  }

  @Override
  public double reDoubleValue() {
    return doubleValue();
  }

  @Override
  public ApfloatNum remainder(double value) {
    return valueOf(EvalEngine.getApfloat().mod(fApfloat, new Apfloat(value)));
  }

  @Override
  public ApfloatNum rint() {
    return valueOf(apfloatRint(fApfloat));
  }

  @Override
  public ApfloatNum rootN(int n) {
    return valueOf(EvalEngine.getApfloat().root(fApfloat, n));
  }

  @Override
  public IReal roundClosest(IReal multiple) {
    throw new ArithmeticException("Apfloat: Round closest not implemented");
    // final long precision = precision();
    // Apfloat factor = multiple.apfloatNumValue(precision).fApfloat;
    // return F.num(EvalEngine.getApfloat().round(fApfloat.divide(factor), precision,
    // RoundingMode.HALF_EVEN).multiply(factor));
  }

  @Override
  public IInteger roundExpr() {
    BigInteger round = NumberUtil.round(fApfloat, Config.ROUNDING_MODE);
    return F.ZZ(round);
  }

  @Override
  public ApfloatNum scalb(int n) {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    return valueOf(h.multiply(fApfloat, h.pow(new Apfloat(2), n)));
  }

  @Override
  public ApfloatNum sign() {
    if (isNaN() || isZero()) {
      return this;
    }
    return valueOf(EvalEngine.getApfloat().abs(fApfloat));
  }

  @Override
  public ApfloatNum sin() {
    return valueOf(EvalEngine.getApfloat().sin(fApfloat));
  }

  @Override
  public ApfloatNum sinc() {
    return valueOf(EvalEngine.getApfloat().sinc(fApfloat));
  }

  @Override
  public ApfloatNum sinh() {
    return valueOf(EvalEngine.getApfloat().sinh(fApfloat));
  }

  @Override
  public IExpr sinhIntegral() {
    return valueOf(EvalEngine.getApfloat().sinhIntegral(fApfloat));
  }

  @Override
  public IExpr sinIntegral() {
    return valueOf(EvalEngine.getApfloat().sinIntegral(fApfloat));
  }

  @Override
  public IExpr sqr() {
    return this.multiply(this);
  }

  @Override
  public IExpr sqrt() {
    if (isNegative()) {
      return F.complexNum(EvalEngine.getApfloat().sqrt(apcomplexValue()));
    }
    return valueOf(EvalEngine.getApfloat().sqrt(fApfloat));
  }

  @Override
  public ApfloatNum subtract(double value) {
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
  public INum subtract(final INum value) {
    return valueOf(EvalEngine.getApfloat().subtract(fApfloat, value.apfloatValue()));
  }

  @Override
  public IReal subtractFrom(IReal that) {
    return valueOf(EvalEngine.getApfloat().subtract(fApfloat, that.apfloatValue()));
  }

  @Override
  public ApfloatNum tan() {
    return valueOf(EvalEngine.getApfloat().tan(fApfloat));
  }

  @Override
  public ApfloatNum tanh() {
    return valueOf(EvalEngine.getApfloat().tanh(fApfloat));
  }

  @Override
  public IExpr times(final IExpr that) {
    if (that instanceof INumber) {
      return times((INumber) that);
    }
    return INum.super.times(that);
  }

  @Override
  public IInexactNumber times(final IInexactNumber that) {
    if (that instanceof INum) {
      if (that instanceof ApfloatNum) {
        return multiply((ApfloatNum) that);
      }
      return multiply(ApfloatNum.valueOf(((Num) that).getRealPart()));
    }
    if (that instanceof IComplexNum) {
      if (that instanceof ApcomplexNum) {
        return ApcomplexNum.valueOf(fApfloat).multiply((ApcomplexNum) that);
      }
      ComplexNum cn = (ComplexNum) that;
      return ApcomplexNum.valueOf(fApfloat)
          .multiply(ApcomplexNum.valueOf(cn.getRealPart(), cn.getImaginaryPart()));
    }
    throw new java.lang.ArithmeticException();
  }

  @Override
  public IInexactNumber times(final INumber that) {
    if (that instanceof IInexactNumber) {
      return times((IInexactNumber) that);
    }
    if (that instanceof IReal) {
      return ApfloatNum
          .valueOf(EvalEngine.getApfloat().multiply(fApfloat, ((IReal) that).apfloatValue()));
    }
    if (that instanceof ComplexSym) {
      return F.complexNum(fApfloat.multiply(that.apcomplexValue()));
    }
    throw new java.lang.ArithmeticException();
  }

  @Override
  public IExpr timesExpr(final INumber that) {
    if (this.isInfinite() || that.isInfinite()) {
      return F.Times(this, that).eval();
    }
    return times(that);
  }

  @Override
  public ApfloatNum toDegrees() {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    // radians * (180 / Pi)
    return valueOf(toDegrees(fApfloat, h));
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
      Errors.rethrowsInterruptException(rex);
    }
    return defaultValue;
  }

  /** {@inheritDoc} */
  @Override
  public long toLong() throws ArithmeticException {
    return fApfloat.longValueExact();
  }

  /** {@inheritDoc} */
  @Override
  public long toLongDefault(long defaultValue) {
    try {
      return fApfloat.longValueExact();
    } catch (RuntimeException rex) {
      // ArithmeticException
      Errors.rethrowsInterruptException(rex);
    }
    return defaultValue;
  }

  @Override
  public ApfloatNum toRadians() {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    // degrees * (Pi / 180)
    return valueOf(toRadians(fApfloat, h));
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    String str = fApfloat.toString();
    if (ParserConfig.EXPLICIT_TIMES_OPERATOR) {
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

  @Override
  public ApfloatNum ulp() {
    return valueOf(EvalEngine.getApfloat().ulp(Apfloat.ONE));
  }

  @Override
  public ApfloatNum zero() {
    return valueOf(Apfloat.ZERO);
  }

  public static void checkHypergeometric2F1(Apfloat a, Apfloat b, Apfloat x)
      throws ArithmeticException {
    // With real a, b and c the result is real if z <= 1 except if it's a polynomial, in which case
    // it's always real (nb. additional checks might throw an exception later)
    if (x.compareTo(Apfloat.ONE) > 0 && maxNonPositiveInteger(a, b) == null) {
      throw new ApfloatArithmeticException("Result would be complex", "complex");
    }
  }

  private static Apfloat maxNonPositiveInteger(Apcomplex... a) {
    return Arrays.stream(a).filter(Apcomplex::isInteger).map(Apcomplex::real)
        .filter(x -> x.signum() <= 0).reduce(ApfloatMath::max).orElse(null);
  }
}

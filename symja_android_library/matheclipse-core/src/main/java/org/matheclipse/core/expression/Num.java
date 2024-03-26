package org.matheclipse.core.expression;

import java.util.function.Function;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.InfiniteExpansionException;
import org.apfloat.LossOfPrecisionException;
import org.apfloat.OverflowException;
import org.hipparchus.complex.Complex;
import org.hipparchus.util.MathUtils;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.functions.HypergeometricJS;
import org.matheclipse.core.eval.Errors;
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
  public IExpr agm(IExpr arg2) {
    if (arg2 instanceof IReal) {
      try {
        Apfloat agm =
            EvalEngine.getApfloatDouble().agm(apfloatValue(), ((IReal) arg2).apfloatValue());
        return F.num(agm.doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber) {
      Apcomplex agm =
          EvalEngine.getApfloatDouble().agm(apfloatValue(), ((INumber) arg2).apcomplexValue());
      return F.complexNum(agm.real().doubleValue(), agm.imag().doubleValue());
    }
    return INum.super.agm(arg2);
  }

  @Override
  public IExpr airyAi() {
    return F.num(EvalEngine.getApfloatDouble().airyAi(apfloatValue()));
  }

  @Override
  public IExpr airyAiPrime() {
    return F.num(EvalEngine.getApfloatDouble().airyAiPrime(apfloatValue()));
  }

  @Override
  public IExpr airyBi() {
    return F.num(EvalEngine.getApfloatDouble().airyBi(apfloatValue()));
  }

  @Override
  public IExpr airyBiPrime() {
    return F.num(EvalEngine.getApfloatDouble().airyBiPrime(apfloatValue()));
  }

  @Override
  public ApcomplexNum apcomplexNumValue() {
    // because of NumStr#apfloatValue()
    return ApcomplexNum.valueOf(apfloatValue());
  }

  @Override
  public Apcomplex apcomplexValue() {
    // because of NumStr#apfloatValue()
    return new Apcomplex(apfloatValue());
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

  @Override
  public IExpr besselI(IExpr arg2) {
    if (arg2 instanceof INumber) {
      if (arg2 instanceof IReal) {
        Apfloat besselI =
            EvalEngine.getApfloatDouble().besselI(apfloatValue(), ((IReal) arg2).apfloatValue());
        return F.num(besselI.doubleValue());
      }
      Apcomplex besselI =
          EvalEngine.getApfloatDouble().besselI(apfloatValue(), ((INumber) arg2).apcomplexValue());
      return F.complexNum(besselI.real().doubleValue(), besselI.imag().doubleValue());
    }
    return INum.super.besselI(arg2);
  }

  @Override
  public IExpr besselJ(IExpr arg2) {
    if (arg2 instanceof INumber) {
      if (arg2 instanceof IReal) {
        Apfloat besselJ =
            EvalEngine.getApfloatDouble().besselJ(apfloatValue(), ((IReal) arg2).apfloatValue());
        return F.num(besselJ.doubleValue());
      }
      Apcomplex besselJ =
          EvalEngine.getApfloatDouble().besselJ(apfloatValue(), ((INumber) arg2).apcomplexValue());
      return F.complexNum(besselJ.real().doubleValue(), besselJ.imag().doubleValue());
    }
    return INum.super.besselJ(arg2);
  }

  @Override
  public IExpr besselK(IExpr arg2) {
    if (arg2 instanceof INumber) {
      if (arg2 instanceof IReal) {
        Apfloat besselK =
            EvalEngine.getApfloatDouble().besselK(apfloatValue(), ((IReal) arg2).apfloatValue());
        return F.num(besselK.doubleValue());
      }
      Apcomplex besselK =
          EvalEngine.getApfloatDouble().besselK(apfloatValue(), ((INumber) arg2).apcomplexValue());
      return F.complexNum(besselK.real().doubleValue(), besselK.imag().doubleValue());
    }
    return INum.super.besselK(arg2);
  }

  @Override
  public IExpr besselY(IExpr arg2) {
    if (arg2 instanceof INumber) {
      if (arg2 instanceof IReal) {
        Apfloat besselY =
            EvalEngine.getApfloatDouble().besselY(apfloatValue(), ((IReal) arg2).apfloatValue());
        return F.num(besselY.doubleValue());
      }
      Apcomplex besselY =
          EvalEngine.getApfloatDouble().besselY(apfloatValue(), ((INumber) arg2).apcomplexValue());
      return F.complexNum(besselY.real().doubleValue(), besselY.imag().doubleValue());
    }
    return INum.super.besselI(arg2);
  }

  @Override
  public IExpr beta(IExpr b) {
    if (b instanceof IReal) {
      try {
        Apfloat beta =
            EvalEngine.getApfloatDouble().beta(apfloatValue(), ((IReal) b).apfloatValue());
        return F.num(beta.doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException e) {
        // try as computation with complex numbers
      }
    }
    if (b instanceof INumber) {
      try {
        Apcomplex beta =
            EvalEngine.getApfloatDouble().beta(apfloatValue(), ((INumber) b).apcomplexValue());
        return F.complexNum(beta.real().doubleValue(), beta.imag().doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException e) {
        // java.lang.ArithmeticException: Beta is infinite
      }
    }
    return INum.super.beta(b);
  }

  @Override
  public IExpr beta(IExpr a, IExpr b) {
    if (a instanceof IReal && b instanceof IReal) {
      try {
        Apfloat beta = EvalEngine.getApfloatDouble().beta(apfloatValue(),
            ((IReal) a).apfloatValue(), ((IReal) b).apfloatValue());
        return F.num(beta.doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException ex) {
        if (ex.getMessage().equals("Division by zero")) {
          return F.ComplexInfinity;
        }
        // try as computation with complex numbers
      }
    }
    if (a instanceof INumber && b instanceof INumber) {
      try {
        Apcomplex beta = EvalEngine.getApfloatDouble().beta(apcomplexValue(),
            ((INumber) a).apcomplexValue(), ((INumber) b).apcomplexValue());
        return F.complexNum(beta.real().doubleValue(), beta.imag().doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException aex) {
        if (aex.getMessage().equals("Division by zero")) {
          return F.ComplexInfinity;
        } else {
          // aex.printStackTrace();
        }
      }
    }
    return INum.super.beta(a, b);
  }

  @Override
  public IExpr beta(IExpr x2, IExpr a, IExpr b) {
    if (x2 instanceof IReal && a instanceof IReal && b instanceof IReal) {
      try {
        Apfloat beta = EvalEngine.getApfloatDouble().beta(apfloatValue(),
            ((IReal) x2).apfloatValue(), ((IReal) a).apfloatValue(), ((IReal) b).apfloatValue());
        return F.num(beta.doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException ex) {
        if (ex.getMessage().equals("Division by zero")) {
          return F.ComplexInfinity;
        }
        // try as computation with complex numbers
      }
    }
    if (x2 instanceof INumber && a instanceof INumber && b instanceof INumber) {
      try {
        Apcomplex beta =
            EvalEngine.getApfloatDouble().beta(apcomplexValue(), ((INumber) x2).apcomplexValue(),
                ((INumber) a).apcomplexValue(), ((INumber) b).apcomplexValue());
        return F.complexNum(beta.real().doubleValue(), beta.imag().doubleValue());
      } catch (ApfloatRuntimeException aex) {

      } catch (ArithmeticException aex) {
        if (aex.getMessage().equals("Division by zero")) {
          return F.ComplexInfinity;
        } else {
          // aex.printStackTrace();
        }
      }
    }
    return INum.super.beta(x2, a, b);
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

  @Override
  public IExpr chebyshevT(IExpr arg2) {
    if (arg2 instanceof INumber) {
      if (arg2 instanceof IReal) {
        try {
          Apfloat chebyshevT = EvalEngine.getApfloatDouble().chebyshevT(apfloatValue(),
              ((IReal) arg2).apfloatValue());
          return F.num(chebyshevT.doubleValue());
        } catch (ArithmeticException | ApfloatRuntimeException are) {

        }
      }
      try {
        Apcomplex chebyshevT = EvalEngine.getApfloatDouble().chebyshevT(apfloatValue(),
            ((INumber) arg2).apcomplexValue());
        return F.complexNum(chebyshevT.real().doubleValue(), chebyshevT.imag().doubleValue());
      } catch (ApfloatRuntimeException are) {

      }
    }
    return INum.super.chebyshevT(arg2);
  }

  @Override
  public IExpr chebyshevU(IExpr arg2) {
    if (arg2 instanceof INumber) {
      if (arg2 instanceof IReal) {
        try {
          Apfloat chebyshevU = EvalEngine.getApfloatDouble().chebyshevU(apfloatValue(),
              ((IReal) arg2).apfloatValue());
          return F.num(chebyshevU.doubleValue());
        } catch (ArithmeticException | ApfloatRuntimeException are) {

        }
      }
      try {
        Apcomplex chebyshevU = EvalEngine.getApfloatDouble().chebyshevU(apfloatValue(),
            ((INumber) arg2).apcomplexValue());
        return F.complexNum(chebyshevU.real().doubleValue(), chebyshevU.imag().doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException are) {

      }
    }
    return INum.super.chebyshevU(arg2);
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

  @Override
  public IExpr coshIntegral() {
    try {
      Apfloat coshIntegral = EvalEngine.getApfloatDouble().coshIntegral(apfloatValue());
      return F.num(coshIntegral.doubleValue());
    } catch (ArithmeticException aex) {
      // java.lang.ArithmeticException: Result would be complex
    }
    Apcomplex coshIntegral = EvalEngine.getApfloatDouble().coshIntegral(apcomplexValue());
    return F.complexNum(coshIntegral.real().doubleValue(), coshIntegral.imag().doubleValue());
  }

  @Override
  public IExpr cosIntegral() {
    try {
      Apfloat cosIntegral = EvalEngine.getApfloatDouble().cosIntegral(apfloatValue());
      return F.num(cosIntegral.doubleValue());
    } catch (ArithmeticException aex) {
      // java.lang.ArithmeticException: Result would be complex
    }
    Apcomplex cosIntegral = EvalEngine.getApfloatDouble().cosIntegral(apcomplexValue());
    return F.complexNum(cosIntegral.real().doubleValue(), cosIntegral.imag().doubleValue());
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
  public IExpr digamma() {
    try {
      Apfloat digamma = EvalEngine.getApfloatDouble().digamma(apfloatValue());
      return F.num(digamma.doubleValue());
    } catch (ArithmeticException | ApfloatRuntimeException aex) {
    }
    return INum.super.digamma();
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
  public IExpr ellipticE() {
    try {
      Apfloat ellipticE = EvalEngine.getApfloatDouble().ellipticE(apfloatValue());
      return F.num(ellipticE.doubleValue());
    } catch (ArithmeticException aex) {
      //
    }
    Apcomplex ellipticE = EvalEngine.getApfloatDouble().ellipticE(apcomplexValue());
    return F.complexNum(ellipticE.real().doubleValue(), ellipticE.imag().doubleValue());
  }

  @Override
  public IExpr ellipticK() {
    try {
      Apfloat ellipticK = EvalEngine.getApfloatDouble().ellipticK(apfloatValue());
      return F.num(ellipticK.doubleValue());
    } catch (ArithmeticException aex) {
      //
    }
    Apcomplex ellipticK = EvalEngine.getApfloatDouble().ellipticK(apcomplexValue());
    return F.complexNum(ellipticK.real().doubleValue(), ellipticK.imag().doubleValue());
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
    Apfloat erf = EvalEngine.getApfloatDouble().erf(apfloatValue());
    return F.num(erf.doubleValue());
  }

  @Override
  public IExpr erfc() {
    try {
      Apfloat erfc = EvalEngine.getApfloatDouble().erfc(apfloatValue());
      return F.num(erfc.doubleValue());
    } catch (OverflowException of) {
      // return Underflow? https://github.com/mtommila/apfloat/issues/38
      return F.Overflow();
    } catch (ArithmeticException | ApfloatRuntimeException e) {
      e.printStackTrace();
    }
    return INum.super.erfc();
  }

  @Override
  public IExpr erfi() {
    Apfloat erfi = EvalEngine.getApfloatDouble().erfi(apfloatValue());
    return F.num(erfi.doubleValue());
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

  @Override
  public IExpr expIntegralE(IExpr z) {
    if (z instanceof IReal) {
      try {
        return valueOf(EvalEngine.getApfloatDouble()
            .expIntegralE(apfloatValue(), ((IReal) z).apfloatValue()).doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException e) {
        // try as computation with complex numbers
      }
    }
    if (z instanceof INumber) {
      Apcomplex expIntegralE = EvalEngine.getApfloatDouble().expIntegralE(apfloatValue(),
          ((INumber) z).apcomplexValue());
      return F.complexNum(expIntegralE.real().doubleValue(), expIntegralE.imag().doubleValue());
    }
    return INum.super.expIntegralE(z);
  }

  @Override
  public IExpr expIntegralEi() {
    return valueOf(EvalEngine.getApfloatDouble().expIntegralEi(apfloatValue()).doubleValue());
  }

  @Override
  public IExpr fibonacci(IExpr arg2) {
    if (arg2 instanceof IReal) {
      try {
        return valueOf(EvalEngine.getApfloatDouble()
            .fibonacci(apfloatValue(), ((IReal) arg2).apfloatValue()).doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber) {
      try {
        Apcomplex fibonacci = EvalEngine.getApfloatDouble().fibonacci(apfloatValue(),
            ((INumber) arg2).apcomplexValue());
        return F.complexNum(fibonacci.real().doubleValue(), fibonacci.imag().doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException e) {
        // try as computation with complex numbers
      }
    }
    return INum.super.fibonacci(arg2);
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

  @Override
  public IExpr fresnelC() {
    Apfloat fresnelC = EvalEngine.getApfloatDouble().fresnelC(apfloatValue());
    return F.num(fresnelC.doubleValue());
  }

  @Override
  public IExpr fresnelS() {
    Apfloat fresnelS = EvalEngine.getApfloatDouble().fresnelS(apfloatValue());
    return F.num(fresnelS.doubleValue());
  }

  /** {@inheritDoc} */
  @Override
  public String fullFormString() {
    return fullFormString(value);
  }

  @Override
  public IExpr gamma() {
    if (isZero() || isMathematicalIntegerNegative()) {
      return F.CComplexInfinity;
    }
    try {
      Apfloat gamma = EvalEngine.getApfloatDouble().gamma(apfloatValue());
      return F.num(gamma.doubleValue());
    } catch (OverflowException of) {
      return F.Overflow();
    } catch (ArithmeticException | ApfloatRuntimeException are) {
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
      if (isPositive() && x.isPositive()) {
        try {
          Apfloat gamma =
              EvalEngine.getApfloatDouble().gamma(apfloatValue(), ((IReal) x).apfloatValue());
          return F.num(gamma.doubleValue());
        } catch (ArithmeticException | ApfloatRuntimeException e) {
          // try as computation with complex numbers
          if (Config.SHOW_STACKTRACE) {
            e.printStackTrace();
          }
        }
      }
    }
    try {
      if (x instanceof INumber) {
        Apcomplex gamma =
            EvalEngine.getApfloatDouble().gamma(apcomplexValue(), ((INumber) x).apcomplexValue());
        return F.complexNum(gamma.real().doubleValue(), gamma.imag().doubleValue());
      }
    } catch (ArithmeticException | ApfloatRuntimeException are) {
      if (Config.SHOW_STACKTRACE) {
        are.printStackTrace();
      }
      return Errors.printMessage(S.Gamma, are, EvalEngine.get());
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
        Apfloat gamma = EvalEngine.getApfloatDouble().gamma(apfloatValue(),
            ((IReal) x0).apfloatValue(), ((IReal) x1).apfloatValue());
        return F.num(gamma.doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException e) {
        // try as computation with complex numbers
      }
    }
    if (x0 instanceof INumber && x1 instanceof INumber) {
      try {
        Apcomplex gamma = EvalEngine.getApfloatDouble().gamma(apcomplexValue(),
            ((INumber) x0).apcomplexValue(), ((INumber) x1).apcomplexValue());
        return F.complexNum(gamma.real().doubleValue(), gamma.imag().doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException are) {
        return Errors.printMessage(S.Gamma, are, EvalEngine.get());
      }
    }
    return INum.super.gamma(x0, x1);
  }

  @Override
  public IExpr gegenbauerC(IExpr arg2) {
    if (arg2 instanceof IReal) {
      try {
        return valueOf(EvalEngine.getApfloatDouble()
            .gegenbauerC(apfloatValue(), ((IReal) arg2).apfloatValue()).doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber) {
      try {
        Apcomplex gegenbauerC = EvalEngine.getApfloatDouble().gegenbauerC(apfloatValue(),
            ((INumber) arg2).apcomplexValue());
        return F.complexNum(gegenbauerC.real().doubleValue(), gegenbauerC.imag().doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException e) {
        // try as computation with complex numbers
      }
    }
    return INum.super.gegenbauerC(arg2);
  }

  @Override
  public IExpr gegenbauerC(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof IReal && arg3 instanceof IReal) {
      try {
        return valueOf(EvalEngine.getApfloatDouble().gegenbauerC(apfloatValue(),
            ((IReal) arg2).apfloatValue(), ((IReal) arg3).apfloatValue()).doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      try {
        Apcomplex gegenbauerC = EvalEngine.getApfloat().gegenbauerC(apfloatValue(),
            ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue());
        return F.complexNum(gegenbauerC.real().doubleValue(), gegenbauerC.imag().doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException e) {
        // try as computation with complex numbers
      }
    }
    return INum.super.gegenbauerC(arg2, arg3);
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
  public IExpr harmonicNumber() {
    Apfloat harmonicNumber = EvalEngine.getApfloatDouble().harmonicNumber(apfloatValue());
    return F.num(harmonicNumber.doubleValue());
  }

  @Override
  public IExpr harmonicNumber(IExpr r) {
    if (r instanceof INumber) {
      if (r instanceof IReal) {
        try {
          Apfloat harmonicNumber = EvalEngine.getApfloatDouble().harmonicNumber(apfloatValue(),
              ((IReal) r).apfloatValue());
          return F.num(harmonicNumber.doubleValue());
        } catch (ArithmeticException | ApfloatRuntimeException aex) {

        }
      }
      try {
        Apcomplex harmonicNumber = EvalEngine.getApfloatDouble().harmonicNumber(apfloatValue(),
            ((INumber) r).apcomplexValue());
        return F.complexNum(harmonicNumber.real().doubleValue(),
            harmonicNumber.imag().doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException aex) {

      }
    }
    return INum.super.harmonicNumber(r);
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
  public IExpr hermiteH(IExpr arg2) {
    if (arg2 instanceof INumber) {
      if (arg2 instanceof IReal) {
        try {
          Apfloat hermiteH =
              EvalEngine.getApfloatDouble().hermiteH(apfloatValue(), ((IReal) arg2).apfloatValue());
          return F.num(hermiteH.doubleValue());
        } catch (ArithmeticException | ApfloatRuntimeException are) {

        }
      }
      try {
        Apcomplex hermiteH = EvalEngine.getApfloatDouble().hermiteH(apfloatValue(),
            ((INumber) arg2).apcomplexValue());
        return F.complexNum(hermiteH.real().doubleValue(), hermiteH.imag().doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException are) {

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
  public IExpr hypergeometric0F1Regularized(IExpr arg2) {
    if (arg2 instanceof IReal) {
      try {
        Apfloat hypergeometric0f1Regularized = EvalEngine.getApfloatDouble()
            .hypergeometric0F1Regularized(apfloatValue(), ((IReal) arg2).apfloatValue());
        return F.num(hypergeometric0f1Regularized.doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber) {
      Apcomplex hypergeometric0f1Regularized = EvalEngine.getApfloatDouble()
          .hypergeometric0F1Regularized(apfloatValue(), ((INumber) arg2).apcomplexValue());
      return F.complexNum(hypergeometric0f1Regularized.real().doubleValue(),
          hypergeometric0f1Regularized.imag().doubleValue());

    }
    return INum.super.hypergeometric0F1Regularized(arg2);
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
  public IExpr hypergeometric1F1Regularized(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof IReal && arg3 instanceof IReal) {
      try {
        Apfloat hypergeometric1F1Regularized =
            EvalEngine.getApfloatDouble().hypergeometric1F1Regularized(apfloatValue(),
                ((IReal) arg2).apfloatValue(), ((IReal) arg3).apfloatValue());
        return F.num(hypergeometric1F1Regularized.doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      Apcomplex hypergeometric1F1Regularized =
          EvalEngine.getApfloatDouble().hypergeometric1F1Regularized(apcomplexValue(),
              ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue());
      return F.complexNum(hypergeometric1F1Regularized.real().doubleValue(),
          hypergeometric1F1Regularized.imag().doubleValue());
    }
    return INum.super.hypergeometric1F1Regularized(arg2, arg3);
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

  @Override
  public IExpr hypergeometric2F1Regularized(IExpr arg2, IExpr arg3, IExpr arg4) {
    if (arg2 instanceof IReal && arg3 instanceof IReal && arg4 instanceof IReal) {
      try {
        Apfloat hypergeometric2F1Regularized = EvalEngine.getApfloatDouble()
            .hypergeometric2F1Regularized(apfloatValue(), ((IReal) arg2).apfloatValue(),
                ((IReal) arg3).apfloatValue(), ((IReal) arg4).apfloatValue());
        return F.num(hypergeometric2F1Regularized.doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException ex) {
        if (ex.getMessage().equals("Division by zero")) {
          return F.ComplexInfinity;
        }
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber && arg3 instanceof INumber && arg4 instanceof INumber) {
      try {
        Apcomplex hypergeometric2F1Regularized = EvalEngine.getApfloatDouble()
            .hypergeometric2F1Regularized(apcomplexValue(), ((INumber) arg2).apcomplexValue(),
                ((INumber) arg3).apcomplexValue(), ((INumber) arg4).apcomplexValue());
        return F.complexNum(hypergeometric2F1Regularized.real().doubleValue(),
            hypergeometric2F1Regularized.imag().doubleValue());
      } catch (ApfloatRuntimeException ex) {
        // org.apfloat.OverflowException: Apfloat disk file storage is disabled
      }
    }
    return INum.super.hypergeometric2F1Regularized(arg2, arg3, arg4);
  }

  @Override
  public IExpr hypergeometricU(IExpr arg2, IExpr arg3) {
    try {
      Apcomplex hypergeometricU = EvalEngine.getApfloatDouble().hypergeometricU(apcomplexValue(),
          ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue());
      return F.complexNum(hypergeometricU.real().doubleValue(),
          hypergeometricU.imag().doubleValue());
    } catch (ArithmeticException aex) {
      if (aex.getMessage().equals("Division by zero")) {
        return F.ComplexInfinity;
      } else {
        // aex.printStackTrace();
      }
    }
    return INum.super.hypergeometricU(arg2, arg3);

    // try {
    // return F.complexNum(HypergeometricJS.hypergeometricU(new Complex(value), //
    // arg2.evalfc(), //
    // arg3.evalfc()));
    // } catch (RuntimeException e) {
    // // try as computation with complex numbers
    // }
    // return INum.super.hypergeometricU(arg2, arg3);
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

  @Override
  public IExpr inverseErf() {
    if (-1.0 < value && value < 1.0) {
      return Num.valueOf(org.hipparchus.special.Erf.erfInv(value));
      // Apfloat erf = EvalEngine.getApfloatDouble().inverseErf(apfloatValue());
      // return F.num(erf.doubleValue());
    }
    return INum.super.inverseErf();
  }

  @Override
  public IExpr inverseErfc() {
    if (0.0 < value && value < 2.0) {
      return Num.valueOf(org.hipparchus.special.Erf.erfcInv(value));
      // Apfloat erfc = EvalEngine.getApfloatDouble().inverseErfc(apfloatValue());
      // return F.num(erfc.doubleValue());
    }
    return INum.super.inverseErfc();
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

  /** {@inheritDoc} */
  @Override
  public final boolean isIndeterminate() {
    return Double.isNaN(value);
  }

  @Override
  public boolean isInexactNumber() {
    return Double.isFinite(value);
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
  public IExpr laguerreL(IExpr arg2) {
    if (arg2 instanceof IReal) {
      try {
        return valueOf(EvalEngine.getApfloatDouble()
            .laguerreL(apfloatValue(), ((IReal) arg2).apfloatValue()).doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber) {
      try {
        Apcomplex laguerreL = EvalEngine.getApfloatDouble().laguerreL(apfloatValue(),
            ((INumber) arg2).apcomplexValue());
        return F.complexNum(laguerreL.real().doubleValue(), laguerreL.imag().doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException e) {
        // try as computation with complex numbers
      }
    }
    return INum.super.laguerreL(arg2);
  }

  @Override
  public IExpr laguerreL(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof IReal && arg3 instanceof IReal) {
      try {
        return valueOf(EvalEngine.getApfloatDouble()
            .laguerreL(apfloatValue(), ((IReal) arg2).apfloatValue(), ((IReal) arg3).apfloatValue())
            .doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      try {
        Apcomplex laguerreL = EvalEngine.getApfloat().laguerreL(apfloatValue(),
            ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue());
        return F.complexNum(laguerreL.real().doubleValue(), laguerreL.imag().doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException e) {
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
        return valueOf(EvalEngine.getApfloatDouble()
            .legendreP(apfloatValue(), ((IReal) arg2).apfloatValue()).doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber) {
      try {
        Apcomplex legendreP = EvalEngine.getApfloatDouble().legendreP(apfloatValue(),
            ((INumber) arg2).apcomplexValue());
        return F.complexNum(legendreP.real().doubleValue(), legendreP.imag().doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException e) {
        // try as computation with complex numbers
      }
    }
    return INum.super.legendreP(arg2);
  }

  @Override
  public IExpr legendreP(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof IReal && arg3 instanceof IReal) {
      try {
        return valueOf(EvalEngine.getApfloatDouble()
            .legendreP(apfloatValue(), ((IReal) arg2).apfloatValue(), ((IReal) arg3).apfloatValue())
            .doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      try {
        Apcomplex legendreP = EvalEngine.getApfloatDouble().legendreP(apfloatValue(),
            ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue());
        return F.complexNum(legendreP.real().doubleValue(), legendreP.imag().doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException e) {
        // try as computation with complex numbers
      }
    }
    return INum.super.legendreP(arg2, arg3);
  }

  @Override
  public IExpr legendreQ(IExpr arg2) {
    if (arg2 instanceof IReal) {
      try {
        return valueOf(EvalEngine.getApfloatDouble()
            .legendreQ(apfloatValue(), ((IReal) arg2).apfloatValue()).doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber) {
      try {
        Apcomplex legendreQ = EvalEngine.getApfloatDouble().legendreQ(apfloatValue(),
            ((INumber) arg2).apcomplexValue());
        return F.complexNum(legendreQ.real().doubleValue(), legendreQ.imag().doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException e) {
        // try as computation with complex numbers
      }
    }
    return INum.super.legendreQ(arg2);
  }


  @Override
  public IExpr legendreQ(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof IReal && arg3 instanceof IReal) {
      try {
        return valueOf(EvalEngine.getApfloatDouble()
            .legendreQ(apfloatValue(), ((IReal) arg2).apfloatValue(), ((IReal) arg3).apfloatValue())
            .doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      try {
        Apcomplex legendreQ = EvalEngine.getApfloatDouble().legendreQ(apfloatValue(),
            ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue());
        return F.complexNum(legendreQ.real().doubleValue(), legendreQ.imag().doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException e) {
        // try as computation with complex numbers
      }
    }
    return INum.super.legendreQ(arg2, arg3);
  }

  @Override
  public IInexactNumber log() {
    if (isNegative()) {
      return ComplexNum.valueOf(new Complex(value).log());
    }
    return valueOf(Math.log(value));
  }

  @Override
  public IExpr logGamma() {
    if (isPositive()) {
      Apfloat logGamma = EvalEngine.getApfloatDouble().logGamma(apfloatValue());
      return F.num(logGamma.doubleValue());
    }
    return F.CInfinity;
  }

  @Override
  public IExpr logIntegral() {
    try {
      Apfloat logIntegral = EvalEngine.getApfloatDouble().logIntegral(apfloatValue());
      return F.num(logIntegral.doubleValue());
    } catch (ArithmeticException aex) {
      // java.lang.ArithmeticException: Result would be complex
    }
    Apcomplex logIntegral = EvalEngine.getApfloatDouble().logIntegral(apcomplexValue());
    return F.complexNum(logIntegral.real().doubleValue(), logIntegral.imag().doubleValue());
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
  public IExpr pochhammer(IExpr arg2) {
    if (arg2 instanceof INumber) {
      if (arg2 instanceof IReal) {
        Apfloat pochhammer =
            EvalEngine.getApfloatDouble().pochhammer(apfloatValue(), ((IReal) arg2).apfloatValue());
        return F.num(pochhammer.doubleValue());
      }
      Apcomplex pochhammer = EvalEngine.getApfloatDouble().pochhammer(apfloatValue(),
          ((INumber) arg2).apcomplexValue());
      return F.complexNum(pochhammer.real().doubleValue(), pochhammer.imag().doubleValue());
    }
    return INum.super.pochhammer(arg2);
  }

  @Override
  public IExpr polyGamma(long n) {
    try {
      Apfloat polygamma = EvalEngine.getApfloatDouble().polygamma(n, apfloatValue());
      return F.num(polygamma.doubleValue());
    } catch (ArithmeticException | ApfloatRuntimeException aex) {
      // java.lang.ArithmeticException: Polygamma of nonpositive integer
    }
    return INum.super.polyGamma(n);
  }

  @Override
  public IExpr polyLog(IExpr arg2) {
    if (arg2 instanceof INumber) {
      if (arg2 instanceof IReal && ((IReal) arg2).isLE(F.C1)) {
        try {
          Apfloat polylog =
              EvalEngine.getApfloatDouble().polylog(apfloatValue(), ((IReal) arg2).apfloatValue());
          return F.num(polylog.doubleValue());
        } catch (ArithmeticException | ApfloatRuntimeException e) {
          // java.lang.ArithmeticException: Result would be complex
        }
      }
      try {
        Apcomplex polylog = EvalEngine.getApfloatDouble().polylog(apfloatValue(),
            ((INumber) arg2).apcomplexValue());
        return F.complexNum(polylog.real().doubleValue(), polylog.imag().doubleValue());
      } catch (LossOfPrecisionException lope) {
        // Complete loss of precision
      } catch (InfiniteExpansionException iee) {
        // Cannot calculate power to infinite precision
      } catch (ArithmeticException | ApfloatRuntimeException ex) {
      }
    }
    return INum.super.polyLog(arg2);
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
  public Num reciprocal() {
    return inverse();
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
  public IExpr sinhIntegral() {
    Apfloat sinhIntegral = EvalEngine.getApfloatDouble().sinhIntegral(apfloatValue());
    return F.num(sinhIntegral.doubleValue());
  }

  @Override
  public IExpr sinIntegral() {
    Apfloat sinIntegral = EvalEngine.getApfloatDouble().sinIntegral(apfloatValue());
    return F.num(sinIntegral.doubleValue());
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

}

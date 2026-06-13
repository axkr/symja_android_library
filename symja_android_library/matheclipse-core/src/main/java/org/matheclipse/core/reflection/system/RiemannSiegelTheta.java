package org.matheclipse.core.reflection.system;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractArg1;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>RiemannSiegelTheta(t)</code> gives the Riemann-Siegel theta function
 * <code>Im(logGamma(1/4 + I*t/2)) - (t/2)*log(Pi)</code> for real <code>t</code>.
 *
 * <p>
 * See <a href="https://reference.wolfram.com/language/ref/RiemannSiegelTheta.html">RiemannSiegelTheta</a>.
 */
public class RiemannSiegelTheta extends AbstractArg1 {

  public RiemannSiegelTheta() {}

  @Override
  public IExpr e1ObjArg(final IExpr arg1) {
    if (arg1.isZero()) {
      return F.C0;
    }
    return F.NIL;
  }

  @Override
  public IExpr e1DblArg(final double arg1) {
    try {
      FixedPrecisionApfloatHelper h = EvalEngine.getApfloatDouble();
      Apfloat t = new Apfloat(arg1, h.precision());
      Apfloat theta = RiemannSiegelTheta.riemannSiegelTheta(h, t);
      return F.num(theta.doubleValue());
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return F.NIL;
  }

  @Override
  public IExpr e1ComplexArg(final Complex arg1) {
    try {
      FixedPrecisionApfloatHelper h = EvalEngine.getApfloatDouble();
      long precision = h.precision();
      Apcomplex t = new Apcomplex(new Apfloat(arg1.getReal(), precision),
          new Apfloat(arg1.getImaginary(), precision));
      Apcomplex theta = RiemannSiegelTheta.riemannSiegelTheta(h, t);
      return F.complexNum(theta.real().doubleValue(), theta.imag().doubleValue());
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return F.NIL;
  }

  @Override
  public IExpr e1ApfloatArg(final Apfloat arg1) {
    try {
      FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
      return F.num(RiemannSiegelTheta.riemannSiegelTheta(h, arg1));
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return F.NIL;
  }

  @Override
  public IExpr e1ApcomplexArg(final Apcomplex arg1) {
    try {
      FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
      return F.complexNum(RiemannSiegelTheta.riemannSiegelTheta(h, arg1));
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return F.NIL;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
  }

  /** Riemann-Siegel theta function at {@code t}. */
  public static Apfloat zzTheta(FixedPrecisionApfloatHelper h,
      Apfloat t, Apfloat quarter, Apfloat two, Apfloat logPi) {
    Apcomplex s = new Apcomplex(quarter, t.divide(two));
    Apcomplex logGamma = h.logGamma(s);
    Apfloat term = h.divide(t, two).multiply(logPi);
    return h.subtract(logGamma.imag(), term);
  }

  /**
   * The Riemann-Siegel theta function for a real argument {@code t}:
   * <code>theta(t) = Im(logGamma(1/4 + I*t/2)) - (t/2)*log(Pi)</code>.
   *
   * @param h a fixed precision helper configured to the requested numeric precision
   * @param t a real argument
   * @return the (real) value of the Riemann-Siegel theta function
   */
  private static Apfloat riemannSiegelTheta(FixedPrecisionApfloatHelper h, Apfloat t) {
    long precision = h.precision();
    Apfloat two = new Apfloat(2, precision);
    Apfloat quarter = new Apfloat("0.25", precision);
    Apfloat logPi = ApfloatMath.log(ApfloatMath.pi(precision));
    return RiemannSiegelTheta.zzTheta(h, t, quarter, two, logPi);
  }

  /**
   * The analytic continuation of the Riemann-Siegel theta function for a complex argument
   * {@code t}: <code>theta(t) = (I/2)*(logGamma(1/4 - I*t/2) - logGamma(1/4 + I*t/2)) -
   * (t/2)*log(Pi)</code>. For real {@code t} this reduces to
   * <code>Im(logGamma(1/4 + I*t/2)) - (t/2)*log(Pi)</code>.
   *
   * @param h a fixed precision helper configured to the requested numeric precision
   * @param t a complex argument
   * @return the value of the Riemann-Siegel theta function
   */
  private static Apcomplex riemannSiegelTheta(FixedPrecisionApfloatHelper h, Apcomplex t) {
    long precision = h.precision();
    Apfloat two = new Apfloat(2, precision);
    Apfloat half = new Apfloat("0.5", precision);
    Apfloat quarter = new Apfloat("0.25", precision);
    Apfloat logPi = ApfloatMath.log(ApfloatMath.pi(precision));
  
    Apcomplex iOver2 = new Apcomplex(Apfloat.ZERO, half);
    Apcomplex it2 = h.multiply(t, iOver2);
    Apcomplex quarterC = new Apcomplex(quarter);
    Apcomplex logGammaPlus = h.logGamma(h.add(quarterC, it2));
    Apcomplex logGammaMinus = h.logGamma(h.subtract(quarterC, it2));
    Apcomplex term1 = h.multiply(iOver2, h.subtract(logGammaMinus, logGammaPlus));
    Apcomplex term2 = h.multiply(t, new Apcomplex(logPi.divide(two)));
    return h.subtract(term1, term2);
  }
}
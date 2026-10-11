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
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.external.apfloat.ApcomplexZetaMath;

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
      return F.num(ApcomplexZetaMath.riemannSiegelTheta(t).doubleValue());
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
      Apcomplex theta = ApcomplexZetaMath.riemannSiegelTheta(t);
      return F.complexNum(theta.real().doubleValue(), theta.imag().doubleValue());
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return F.NIL;
  }

  @Override
  public IExpr e1ApfloatArg(final Apfloat arg1) {
    try {
      // next to a zero the result has fewer digits than the argument: the ones it determines
      return F.num(ApcomplexZetaMath.riemannSiegelTheta(arg1));
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return F.NIL;
  }

  @Override
  public IExpr e1ApcomplexArg(final Apcomplex arg1) {
    try {
      return F.complexNum(ApcomplexZetaMath.riemannSiegelTheta(arg1));
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
    newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
  }

  /**
   * Riemann-Siegel theta function at {@code t}, at the precision of the helper. Used by
   * {@link ZetaZero}, which brings its own guard digits.
   */
  public static Apfloat zzTheta(FixedPrecisionApfloatHelper h, Apfloat t, Apfloat quarter,
      Apfloat two, Apfloat logPi) {
    Apcomplex s = new Apcomplex(quarter, t.divide(two));
    Apcomplex logGamma = h.logGamma(s);
    Apfloat term = h.divide(t, two).multiply(logPi);
    return h.subtract(logGamma.imag(), term);
  }
}

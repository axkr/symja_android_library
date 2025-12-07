package org.matheclipse.core.expression;

import org.apfloat.Apfloat;
import org.matheclipse.core.eval.EvalEngine;

public class DMath {
  private DMath() {}

  public static double airyAi(double value) {
    return EvalEngine.getApfloatDouble().airyAi(new Apfloat(value)).doubleValue();
  }

  public static double airyAiPrime(double value) {
    return EvalEngine.getApfloatDouble().airyAiPrime(new Apfloat(value)).doubleValue();
  }

  public static double airyBi(double value) {
    return EvalEngine.getApfloatDouble().airyBi(new Apfloat(value)).doubleValue();
  }

  public static double airyBiPrime(double value) {
    return EvalEngine.getApfloatDouble().airyBiPrime(new Apfloat(value)).doubleValue();
  }

  public static double besselI(double v, double x) {
    return EvalEngine.getApfloatDouble().besselI(new Apfloat(v), new Apfloat(x)).doubleValue();
  }

  public static double besselJ(double v, double x) {
    return EvalEngine.getApfloatDouble().besselJ(new Apfloat(v), new Apfloat(x)).doubleValue();
  }

  public static double besselK(double v, double x) {
    return EvalEngine.getApfloatDouble().besselK(new Apfloat(v), new Apfloat(x)).doubleValue();
  }

  public static double besselY(double v, double x) {
    return EvalEngine.getApfloatDouble().besselY(new Apfloat(v), new Apfloat(x)).doubleValue();
  }

  public static double beta(double a, double b) {
    return EvalEngine.getApfloatDouble().beta(new Apfloat(a), new Apfloat(b)).doubleValue();
  }

  public static double beta(double x, double a, double b) {
    return EvalEngine.getApfloatDouble().beta(new Apfloat(x), new Apfloat(a), new Apfloat(b))
        .doubleValue();
  }

  public static double beta(double x1, double x2, double a, double b) {
    return EvalEngine.getApfloatDouble()
        .beta(new Apfloat(x1), new Apfloat(x2), new Apfloat(a), new Apfloat(b)).doubleValue();
  }

  public static double ellipticE(double value) {
    return EvalEngine.getApfloatDouble().ellipticE(new Apfloat(value)).doubleValue();
  }

  public static double ellipticK(double value) {
    return EvalEngine.getApfloatDouble().ellipticK(new Apfloat(value)).doubleValue();
  }

  public static double erf(double value) {
    return org.hipparchus.special.Erf.erf(value);
  }

  public static double erfc(double value) {
    return org.hipparchus.special.Erf.erfc(value);
  }

  public static double erfi(double value) {
    return EvalEngine.getApfloatDouble().erfi(new Apfloat(value)).doubleValue();
  }
}

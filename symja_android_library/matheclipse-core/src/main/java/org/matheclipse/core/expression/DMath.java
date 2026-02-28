package org.matheclipse.core.expression;

import org.apfloat.Apfloat;
import org.matheclipse.core.eval.EvalEngine;

public class DMath {

  // --- Missing Arc- and Hyperbolic Trig Functions ---

  public static double acosh(double x) {
    return Math.log(x + Math.sqrt(x * x - 1.0));
  }

  public static double asinh(double x) {
    return Math.log(x + Math.sqrt(x * x + 1.0));
  }

  public static double atanh(double x) {
    return 0.5 * Math.log((1.0 + x) / (1.0 - x));
  }

  public static double csc(double x) {
    return 1.0 / Math.sin(x);
  }

  public static double sec(double x) {
    return 1.0 / Math.cos(x);
  }

  public static double cot(double x) {
    return 1.0 / Math.tan(x);
  }

  public static double csch(double x) {
    return 1.0 / Math.sinh(x);
  }

  public static double sech(double x) {
    return 1.0 / Math.cosh(x);
  }

  public static double coth(double x) {
    return 1.0 / Math.tanh(x);
  }

  public static double acsc(double x) {
    return Math.asin(1.0 / x);
  }

  public static double asec(double x) {
    return Math.acos(1.0 / x);
  }

  public static double acot(double x) {
    return Math.PI / 2.0 - Math.atan(x);
  }

  public static double acsch(double x) {
    return asinh(1.0 / x);
  }

  public static double asech(double x) {
    return acosh(1.0 / x);
  }

  public static double acoth(double x) {
    return atanh(1.0 / x);
  }

  // --- Existing Special Functions ---

  public static double agm(double a, double b) {
    return EvalEngine.getApfloatDouble().agm(new Apfloat(a), new Apfloat(b)).doubleValue();
  }

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

  public static double angerJ(double v, double z) {
    return EvalEngine.getApfloatDouble().angerJ(new Apfloat(v), new Apfloat(z)).doubleValue();
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

  public static double chebyshevT(double v, double x) {
    return EvalEngine.getApfloatDouble().chebyshevT(new Apfloat(v), new Apfloat(x)).doubleValue();
  }

  public static double chebyshevU(double v, double x) {
    return EvalEngine.getApfloatDouble().chebyshevU(new Apfloat(v), new Apfloat(x)).doubleValue();
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

  public static double fibonacci(double n, double x) {
    return EvalEngine.getApfloatDouble().fibonacci(new Apfloat(n), new Apfloat(x)).doubleValue();
  }

  public static double fresnelC(double value) {
    return EvalEngine.getApfloatDouble().fresnelC(new Apfloat(value)).doubleValue();
  }

  public static double fresnelS(double value) {
    return EvalEngine.getApfloatDouble().fresnelS(new Apfloat(value)).doubleValue();
  }

  public static double gamma(double value) {
    return org.hipparchus.special.Gamma.gamma(value);
  }

  public static double gamma(double a, double x) {
    return EvalEngine.getApfloatDouble().gamma(new Apfloat(a), new Apfloat(x)).doubleValue();
  }

  public static double gamma(double a, double x0, double x1) {
    return EvalEngine.getApfloatDouble().gamma(new Apfloat(a), new Apfloat(x0), new Apfloat(x1))
        .doubleValue();
  }

  public static double gegenbauerC(double n, double x) {
    return EvalEngine.getApfloatDouble().gegenbauerC(new Apfloat(n), new Apfloat(x)).doubleValue();
  }

  public static double gegenbauerC(double n, double m, double x) {
    return EvalEngine.getApfloatDouble().gegenbauerC(new Apfloat(n), new Apfloat(m), new Apfloat(x))
        .doubleValue();
  }

  public static double harmonicNumber(double value) {
    return EvalEngine.getApfloatDouble().harmonicNumber(new Apfloat(value)).doubleValue();
  }

  public static double harmonicNumber(double value, double r) {
    return EvalEngine.getApfloatDouble().harmonicNumber(new Apfloat(value), new Apfloat(r))
        .doubleValue();
  }

  public static double hermiteH(double v, double x) {
    return EvalEngine.getApfloatDouble().hermiteH(new Apfloat(v), new Apfloat(x)).doubleValue();
  }

  public static double hypergeometric0F1Regularized(double a, double b) {
    return EvalEngine.getApfloatDouble()
        .hypergeometric0F1Regularized(new Apfloat(a), new Apfloat(b)).doubleValue();
  }

  public static double hypergeometric1F1Regularized(double a, double b, double c) {
    return EvalEngine.getApfloatDouble()
        .hypergeometric1F1Regularized(new Apfloat(a), new Apfloat(b), new Apfloat(c)).doubleValue();
  }

  public static double hypergeometric2F1(double a, double b, double c, double d) {
    return EvalEngine.getApfloatDouble()
        .hypergeometric2F1(new Apfloat(a), new Apfloat(b), new Apfloat(c), new Apfloat(d))
        .doubleValue();
  }

  public static double hypergeometric2F1Regularized(double a, double b, double c, double d) {
    return EvalEngine.getApfloatDouble().hypergeometric2F1Regularized(new Apfloat(a),
        new Apfloat(b), new Apfloat(c), new Apfloat(d)).doubleValue();
  }

  public static double inverseErf(double value) {
    return org.hipparchus.special.Erf.erfInv(value);
  }

  public static double inverseErfc(double value) {
    return org.hipparchus.special.Erf.erfcInv(value);
  }

  public static double jacobiP(double n, double a, double b, double x) {
    return EvalEngine.getApfloatDouble()
        .jacobiP(new Apfloat(n), new Apfloat(a), new Apfloat(b), new Apfloat(x)).doubleValue();
  }

  public static double laguerreL(double n, double x) {
    return EvalEngine.getApfloatDouble().laguerreL(new Apfloat(n), new Apfloat(x)).doubleValue();
  }

  public static double laguerreL(double n, double a, double x) {
    return EvalEngine.getApfloatDouble().laguerreL(new Apfloat(n), new Apfloat(a), new Apfloat(x))
        .doubleValue();
  }

  public static double legendreP(double v, double x) {
    return EvalEngine.getApfloatDouble().legendreP(new Apfloat(v), new Apfloat(x)).doubleValue();
  }

  public static double legendreP(double v, double m, double x) {
    return EvalEngine.getApfloatDouble().legendreP(new Apfloat(v), new Apfloat(m), new Apfloat(x))
        .doubleValue();
  }

  public static double legendreQ(double v, double x) {
    return EvalEngine.getApfloatDouble().legendreQ(new Apfloat(v), new Apfloat(x)).doubleValue();
  }

  public static double legendreQ(double v, double m, double x) {
    return EvalEngine.getApfloatDouble().legendreQ(new Apfloat(v), new Apfloat(m), new Apfloat(x))
        .doubleValue();
  }

  public static double logGamma(double value) {
    return org.hipparchus.special.Gamma.logGamma(value);
  }

  public static double logisticSigmoid(double value) {
    return EvalEngine.getApfloatDouble().logisticSigmoid(new Apfloat(value)).doubleValue();
  }

  public static double pochhammer(double x, double n) {
    return EvalEngine.getApfloatDouble().pochhammer(new Apfloat(x), new Apfloat(n)).doubleValue();
  }

  public static double polyGamma(double value) {
    return org.hipparchus.special.Gamma.digamma(value);
  }

  public static double polyGamma(long n, double x) {
    return EvalEngine.getApfloatDouble().polygamma(n, new Apfloat(x)).doubleValue();
  }

  public static double polyLog(double n, double x) {
    return EvalEngine.getApfloatDouble().polylog(new Apfloat(n), new Apfloat(x)).doubleValue();
  }

  private DMath() {}
}
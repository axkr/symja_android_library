package org.matheclipse.core.expression;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.numerics.functions.BesselJS;
import org.matheclipse.core.numerics.functions.GammaJS;
import org.matheclipse.core.numerics.functions.HypergeometricJS;

public class CMath {
  public static Apcomplex apcomplex(Complex c) {
    return new Apcomplex(new Apfloat(c.getReal()), new Apfloat(c.getImaginary()));
  }

  public static Complex toComplex(Apcomplex c) {
    return new Complex(c.real().doubleValue(), c.imag().doubleValue());
  }

  // --- Missing Arc- and Hyperbolic Trig Functions ---

  public static Complex acosh(Complex z) {
    // ArcCosh[z] = Log[z + Sqrt[z + 1] * Sqrt[z - 1]]
    return z.add(1.0).sqrt().multiply(z.subtract(1.0).sqrt()).add(z).log();
  }

  public static Complex asinh(Complex z) {
    // ArcSinh[z] = Log[z + Sqrt[z^2 + 1]]
    return z.multiply(z).add(1.0).sqrt().add(z).log();
  }

  public static Complex atanh(Complex z) {
    // ArcTanh[z] = 1/2 * Log[(1 + z) / (1 - z)]
    return z.add(1.0).divide(Complex.ONE.subtract(z)).log().multiply(0.5);
  }

  public static Complex csc(Complex z) {
    return z.sin().reciprocal();
  }

  public static Complex sec(Complex z) {
    return z.cos().reciprocal();
  }

  public static Complex cot(Complex z) {
    return z.tan().reciprocal();
  }

  public static Complex csch(Complex z) {
    return z.sinh().reciprocal();
  }

  public static Complex sech(Complex z) {
    return z.cosh().reciprocal();
  }

  public static Complex coth(Complex z) {
    return z.tanh().reciprocal();
  }

  public static Complex acsc(Complex z) {
    // ArcCsc[z] = ArcSin[1/z]
    return z.reciprocal().asin();
  }

  public static Complex asec(Complex z) {
    // ArcSec[z] = ArcCos[1/z]
    return z.reciprocal().acos();
  }

  public static Complex acot(Complex z) {
    // ArcCot[z] = Pi/2 - ArcTan[z]
    return new Complex(Math.PI / 2.0, 0.0).subtract(z.atan());
  }

  public static Complex acsch(Complex z) {
    // ArcCsch[z] = ArcSinh[1/z]
    return asinh(z.reciprocal());
  }

  public static Complex asech(Complex z) {
    // ArcSech[z] = ArcCosh[1/z]
    return acosh(z.reciprocal());
  }

  public static Complex acoth(Complex z) {
    // ArcCoth[z] = ArcTanh[1/z]
    return atanh(z.reciprocal());
  }

  // --- Existing Special Functions ---

  public static Complex agm(Complex a, Complex b) {
    return toComplex(EvalEngine.getApfloatDouble().agm(apcomplex(a), apcomplex(b)));
  }

  public static Complex airyAi(Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().airyAi(apcomplex(z)));
  }

  public static Complex airyAiPrime(Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().airyAiPrime(apcomplex(z)));
  }

  public static Complex airyBi(Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().airyBi(apcomplex(z)));
  }

  public static Complex airyBiPrime(Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().airyBiPrime(apcomplex(z)));
  }

  public static Complex angerJ(Complex v, Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().angerJ(apcomplex(v), apcomplex(z)));
  }

  public static Complex barnesG(Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().barnesG(apcomplex(z)));
  }

  public static Complex besselI(Complex v, Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().besselI(apcomplex(v), apcomplex(z)));
  }

  public static Complex besselJ(Complex v, Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().besselJ(apcomplex(v), apcomplex(z)));
  }

  public static Complex besselK(Complex v, Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().besselK(apcomplex(v), apcomplex(z)));
  }

  public static Complex besselY(Complex v, Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().besselY(apcomplex(v), apcomplex(z)));
  }

  public static Complex beta(Complex a, Complex b) {
    return toComplex(EvalEngine.getApfloatDouble().beta(apcomplex(a), apcomplex(b)));
  }

  public static Complex beta(Complex z, Complex a, Complex b) {
    return toComplex(EvalEngine.getApfloatDouble().beta(apcomplex(z), apcomplex(a), apcomplex(b)));
  }

  public static Complex beta(Complex x2, Complex a, Complex b, Complex c) {
    return toComplex(EvalEngine.getApfloatDouble().beta(apcomplex(x2), apcomplex(a), apcomplex(b),
        apcomplex(c)));
  }

  public static Complex chebyshevT(Complex v, Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().chebyshevT(apcomplex(v), apcomplex(z)));
  }

  public static Complex chebyshevU(Complex v, Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().chebyshevU(apcomplex(v), apcomplex(z)));
  }

  public static Complex coshIntegral(Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().coshIntegral(apcomplex(z)));
  }

  public static Complex cosIntegral(Complex z) {
    return GammaJS.cosIntegral(z);
  }

  public static Complex digamma(Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().digamma(apcomplex(z)));
  }

  public static Complex ellipticE(Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().ellipticE(apcomplex(z)));
  }

  public static Complex ellipticK(Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().ellipticK(apcomplex(z)));
  }

  public static Complex erf(Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().erf(apcomplex(z)));
  }

  public static Complex erfc(Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().erfc(apcomplex(z)));
  }

  public static Complex erfi(Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().erfi(apcomplex(z)));
  }

  public static Complex expIntegralE(Complex v, Complex z) {
    return GammaJS.expIntegralE(v, z);
  }

  public static Complex expIntegralEi(Complex z) {
    return GammaJS.expIntegralEi(z);
  }

  public static Complex fibonacci(Complex n, Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().fibonacci(apcomplex(n), apcomplex(z)));
  }

  public static Complex fresnelC(Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().fresnelC(apcomplex(z)));
  }

  public static Complex fresnelS(Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().fresnelS(apcomplex(z)));
  }

  public static Complex gamma(Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().gamma(apcomplex(z)));
  }

  public static Complex gamma(Complex a, Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().gamma(apcomplex(a), apcomplex(z)));
  }

  public static Complex gamma(Complex a, Complex z0, Complex z1) {
    return toComplex(
        EvalEngine.getApfloatDouble().gamma(apcomplex(a), apcomplex(z0), apcomplex(z1)));
  }

  public static Complex gegenbauerC(Complex v, Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().gegenbauerC(apcomplex(v), apcomplex(z)));
  }

  public static Complex gegenbauerC(Complex n, Complex v, Complex z) {
    return toComplex(
        EvalEngine.getApfloatDouble().gegenbauerC(apcomplex(n), apcomplex(v), apcomplex(z)));
  }

  public static Complex harmonicNumber(Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().harmonicNumber(apcomplex(z)));
  }

  public static Complex harmonicNumber(Complex z, Complex r) {
    return toComplex(EvalEngine.getApfloatDouble().harmonicNumber(apcomplex(z), apcomplex(r)));
  }

  public static Complex hermiteH(Complex v, Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().hermiteH(apcomplex(v), apcomplex(z)));
  }

  public static Complex hypergeometric0F1(Complex a, Complex z) {
    return HypergeometricJS.hypergeometric0F1(a, z);
  }

  public static Complex hypergeometric0F1Regularized(Complex a, Complex z) {
    return toComplex(
        EvalEngine.getApfloatDouble().hypergeometric0F1Regularized(apcomplex(a), apcomplex(z)));
  }

  public static Complex hypergeometric1F1(Complex a, Complex b, Complex z) {
    return HypergeometricJS.hypergeometric1F1(a, b, z);
  }

  public static Complex hypergeometric1F1Regularized(Complex a, Complex b, Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().hypergeometric1F1Regularized(apcomplex(a),
        apcomplex(b), apcomplex(z)));
  }

  public static Complex hypergeometric2F1(Complex a, Complex b, Complex c, Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().hypergeometric2F1(apcomplex(a), apcomplex(b),
        apcomplex(c), apcomplex(z)));
  }

  public static Complex hypergeometric2F1Regularized(Complex a, Complex b, Complex c, Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().hypergeometric2F1Regularized(apcomplex(a),
        apcomplex(b), apcomplex(c), apcomplex(z)));
  }

  public static Complex hypergeometricU(Complex a, Complex b, Complex z) {
    return toComplex(
        EvalEngine.getApfloatDouble().hypergeometricU(apcomplex(a), apcomplex(b), apcomplex(z)));
  }

  public static Complex jacobiP(Complex n, Complex a, Complex b, Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().jacobiP(apcomplex(n), apcomplex(a), apcomplex(b),
        apcomplex(z)));
  }

  public static Complex laguerreL(Complex v, Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().laguerreL(apcomplex(v), apcomplex(z)));
  }

  public static Complex laguerreL(Complex n, Complex v, Complex z) {
    return toComplex(
        EvalEngine.getApfloatDouble().laguerreL(apcomplex(n), apcomplex(v), apcomplex(z)));
  }

  public static Complex legendreP(Complex v, Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().legendreP(apcomplex(v), apcomplex(z)));
  }

  public static Complex legendreP(Complex v, Complex mu, Complex z) {
    return toComplex(
        EvalEngine.getApfloatDouble().legendreP(apcomplex(v), apcomplex(mu), apcomplex(z)));
  }

  public static Complex legendreQ(Complex v, Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().legendreQ(apcomplex(v), apcomplex(z)));
  }

  public static Complex legendreQ(Complex v, Complex mu, Complex z) {
    return toComplex(
        EvalEngine.getApfloatDouble().legendreQ(apcomplex(v), apcomplex(mu), apcomplex(z)));
  }

  public static Complex logBarnesG(Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().logBarnesG(apcomplex(z)));
  }

  public static Complex logGamma(Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().logGamma(apcomplex(z)));
  }

  public static Complex logIntegral(Complex z) {
    return GammaJS.logIntegral(z);
  }

  public static Complex logisticSigmoid(Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().logisticSigmoid(apcomplex(z)));
  }

  public static Complex pochhammer(Complex a, Complex n) {
    return toComplex(EvalEngine.getApfloatDouble().pochhammer(apcomplex(a), apcomplex(n)));
  }

  public static Complex polyGamma(Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().digamma(apcomplex(z)));
  }

  public static Complex polyGamma(Complex n, Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().polygamma((long) n.getReal(), apcomplex(z)));
  }

  public static Complex polyLog(Complex n, Complex z) {
    return toComplex(EvalEngine.getApfloatDouble().polylog(apcomplex(n), apcomplex(z)));
  }

  public static Complex sinhIntegral(Complex z) {
    return GammaJS.sinhIntegral(z);
  }

  public static Complex sinIntegral(Complex z) {
    return GammaJS.sinIntegral(z);
  }

  public static Complex struveH(Complex v, Complex z) {
    return BesselJS.struveH(v, z);
  }

  public static Complex struveL(Complex v, Complex z) {
    return BesselJS.struveL(v, z);
  }
}

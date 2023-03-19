package org.matheclipse.core.builtin.functions;

import org.hipparchus.analysis.differentiation.DSFactory;
import org.hipparchus.analysis.differentiation.FiniteDifferencesDifferentiator;
import org.hipparchus.analysis.differentiation.UnivariateDifferentiableFunction;
import org.hipparchus.analysis.solvers.BisectionSolver;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Arithmetic;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Ported from JavaScript file
 * <a href="https://github.com/paulmasson/math/blob/master/src/functions/bessel.js">bessel.js</a>
 */
public class BesselJS extends JS {

  private BesselJS() {}

  public static Complex besselJ(double n, double x) {
    // if (F.isNumIntValue(n) && n < 0.0) {
    // return besselJ(-n, x).multiply(new Complex(-1.0).pow(n));
    // }
    if (!F.isNumIntValue(n) && x < 0.0) {
      return besselJ(new Complex(n), new Complex(x));
    }
    return new Complex(besselJDouble(n, x));
    // return new Complex(
    // Math.pow(x / 2.0, n) * HypergeometricJS.hypergeometric0F1(n + 1, -0.25 * x * x) /
    // Gamma.gamma(n + 1));
  }

  public static double besselJDouble(double n, double x) {
    if (F.isNumIntValue(n) && n < 0.0) {
      return besselJDouble(-n, x) * (Math.pow(-1.0, Math.rint(n)));
    }
    if (!F.isNumIntValue(n) && x < 0.0) {
      throw new ArgumentTypeException(x + " is less than 0.0");
    }

    return Math.pow(x / 2.0, n) * HypergeometricJS.hypergeometric0F1(n + 1, -0.25 * x * x)
        / GammaJS.gamma(n + 1);
  }

  public static Complex besselJ(double n, Complex x) {
    return besselJ(new Complex(n), x);
  }

  public static Complex besselJ(Complex n, Complex x) {
    if (n.isMathematicalInteger() && n.getReal() < 0.0) {
      return new Complex(-1.0).pow(n).multiply(besselJ(n.negate(), x));
    }

    Complex product = x.divide(2.0).pow(n).divide(Arithmetic.lanczosApproxGamma(n.add(1.0)));
    Complex sqrX = x.multiply(x);
    return product.multiply(HypergeometricJS.hypergeometric0F1(n.add(1), sqrX.multiply(-0.25)));
  }

  /**
   * @param n a positive value
   * @param m
   * @return
   */
  public static double besselJZero(double n, int m) {
    // if (n < 0.0) {
    // throw new ArgumentTypeException(n + " is less than 0 (negative order)");
    // }
    if (m < 0) {
      throw new ArgumentTypeException("Negative index " + m + " not supported in BesselJZero");
    }

    // approximations from dlmf.nist.gov/10.21#vi
    double delta = Math.PI / 4.0;

    double a = (m + Math.abs(n) / 2.0 - 0.25) * Math.PI;
    double e = a - (4.0 * (n * n) - 1.0) / (8.0 * a);

    if (n < 0.0) {
      e += (n % 1) * Math.PI;
    }

    BisectionSolver solver = new BisectionSolver();
    ISymbol x = F.Dummy("x");
    UnivariateDifferentiableFunction f =
        new UnaryNumerical(F.BesselJ(F.num(n), x), x, EvalEngine.get(), true);
    return solver.solve(100, f, e - delta < 0 ? 0 : e - delta, e + delta);
  }

  public static Complex besselY(double n, double x) {
    // for averaging over integer orders until write code for limit
    // double delta = 1e-5;

    if (x < 0) {
      return besselY(new Complex(n), new Complex(x));
    }
    return new Complex(besselYDouble(n, x));
    // if (F.isNumIntValue(n)) {
    // return (besselY(n + delta, x).add(besselY(n - delta, x))).divide(2.0);
    // }
    // return besselJ(n, x).multiply(Math.cos(n * Math.PI)).subtract(besselJ(-n,
    // x)).divide(Math.sin(n * Math.PI));
  }

  public static double besselYDouble(double n, double x) {
    // for averaging over integer orders until write code for limit
    // double delta = 1e-5;
    if (x < 0) {
      throw new ArgumentTypeException(x + " < 0.0");
    }

    // dlmf.nist.gov/10.2#E3
    if (F.isNumIntValue(n)) {
      FiniteDifferencesDifferentiator differentiator =
          new FiniteDifferencesDifferentiator(15, 0.01);
      ISymbol nSymbol = F.Dummy("n");
      UnivariateDifferentiableFunction f = differentiator.differentiate(
          new UnaryNumerical(F.BesselJ(nSymbol, F.num(x)), nSymbol, EvalEngine.get()));
      DSFactory factory = new DSFactory(1, 1);
      double d1 = f.value(factory.variable(0, n)).getPartialDerivative(1);
      double d2 = f.value(factory.variable(0, -n)).getPartialDerivative(1);

      return (d1 + Math.pow(-1.0, Math.rint(n)) * d2) / Math.PI;
      // return ( diff( n => besselJ(n,x), n ) + (-1)**n * diff( n => besselJ(n,x), -n ) ) / pi;

      // old without differentiation
      // return (besselYDouble(n + delta, x) + besselYDouble(n - delta, x)) / 2.0;
    }
    return (besselJDouble(n, x) * Math.cos(n * Math.PI) - besselJDouble(-n, x))
        / Math.sin(n * Math.PI);
  }

  public static Complex besselY(Complex n, Complex x) {
    if (x.isZero()) {
      double fractionalPart = n.getReal() % 1;
      if (F.isEqual(fractionalPart, -0.5) && F.isZero(n.getImaginary())) {
        return Complex.ZERO;
      }
    }
    // for averaging over integer orders until write code for limit
    if (n.isMathematicalInteger()) {
      EvalEngine engine = EvalEngine.get();
      final int recursionLimit = engine.getRecursionLimit();
      try {
        if (recursionLimit > 0) {
          int counter = engine.incRecursionCounter();
          if (counter > recursionLimit) {
            RecursionLimitExceeded.throwIt(counter, F.BesselY(F.complexNum(n), F.complexNum(x)));
          }
        }

        double delta = 1e-5;
        // TODO use differentiator here
        return besselY(new Complex(n.getReal() + delta), x)
            .add(besselY(new Complex(n.getReal() - delta), x).divide(2.0));
      } finally {
        if (recursionLimit > 0) {
          engine.decRecursionCounter();
        }
      }
    }

    Complex sum =
        n.multiply(Math.PI).cos().multiply(besselJ(n, x)).subtract(besselJ(n.negate(), x));
    return sum.divide(n.multiply(Math.PI).sin());
  }

  /**
   * @param n a positive value
   * @param m
   * @return
   */
  public static double besselYZero(double n, int m) {
    // if (n < 0.0) {
    // throw new ArgumentTypeException(n + " < 0 (negative order)");
    // }
    if (m < 0) {
      throw new ArgumentTypeException("Negative index " + m + " not supported in BesselYZero");
    }
    // approximations from dlmf.nist.gov/10.21#vi
    double delta = Math.PI / 4.0;
    // if (false) {
    // // use derivative
    // double b = (m + n / 2.0 - 0.25) * Math.PI;
    // double e = b - (4.0 * (n * n) + 3.0) / (8.0 * b);
    //
    // // return findRoot( x => diff( x => besselY(n,x), x ), [ e-delta, e+delta ] );
    // BisectionSolver solver = new BisectionSolver();
    // ISymbol x = F.Dummy("x");
    // IExpr function= F.Times(F.C1D2,F.Subtract(F.BesselY(F.num(-1.0 + n), x),F.BesselY(F.num(1.0 +
    // n), x)));
    // UnivariateDifferentiableFunction f = new UnaryNumerical(function, x, EvalEngine.get(), true);
    // return solver.solve(200, f, e - delta, e + delta);
    // } else {
    double a = (m + Math.abs(n) / 2.0 - 0.75) * Math.PI;
    double e = a - (4.0 * (n * n) - 1.0) / (8.0 * a);
    double fractionalPartN = n % 1;

    if (n < 0.0) {
      if (fractionalPartN > -0.5) {
        e += fractionalPartN * Math.PI;
      } else {
        e += (1.0 + fractionalPartN) * Math.PI;
      }
    }

    BisectionSolver solver = new BisectionSolver();
    ISymbol x = F.Dummy("x");
    UnivariateDifferentiableFunction f =
        new UnaryNumerical(F.BesselY(F.num(n), x), x, EvalEngine.get(), true);
    return solver.solve(100, f, (m == 1 && fractionalPartN > -.5) ? 1e-10 : e - delta, e + delta);
    // }
  }

  public static Complex besselI(double n, double x) {
    if (!F.isNumIntValue(n) && x < 0) {
      return besselI(new Complex(n), new Complex(x));
    }
    return new Complex(besselIDouble(n, x));
    // return new Complex(Math.pow(x / 2.0, n) * HypergeometricJS.hypergeometric0F1(n + 1.0, 0.25 *
    // x * x)
    // / Gamma.gamma(n + 1.0));
  }

  public static double besselIDouble(double n, double x) {
    if (F.isNumIntValue(n) && n < 0) {
      return besselIDouble(-n, x);
    }

    if (!F.isNumIntValue(n) && x < 0) {
      throw new ArgumentTypeException(x + " < 0.0");
    }

    return Math.pow(x / 2.0, n) * HypergeometricJS.hypergeometric0F1(n + 1.0, 0.25 * x * x)
        / GammaJS.gamma(n + 1.0);
  }

  public static Complex besselI(double n, Complex x) {
    return besselI(new Complex(n), x);
  }

  public static Complex besselI(Complex n, Complex x) {

    if (n.isMathematicalInteger() && n.getReal() < 0.0) {
      return besselI(n.negate(), x);
    }
    Complex product = div(pow(div(x, 2.0), n), gamma(add(n, 1.0)));
    return mul(product, HypergeometricJS.hypergeometric0F1(add(n, 1.0), mul(0.25, pow(x, 2.0))));
    // Complex sqrX = x.multiply(x);
    // return product.multiply(HypergeometricJS.hypergeometric0F1(n.add(1), sqrX.multiply(.25)));
  }

  public static Complex besselK(double n, double x) {
    if (x < 0) {
      return besselK(new Complex(n), new Complex(x));
    }
    return new Complex(besselKDouble(n, x));
  }

  public static double besselKDouble(double n, double x) {
    final int useAsymptotic = 10;

    if (x > useAsymptotic) {
      return Math.sqrt(0.5 * Math.PI / x) * Math.exp(-x)
          * HypergeometricJS.hypergeometric2F0(n + 0.5, 0.5 - n, -0.5 / x);
    }
    if (x < 0) {
      throw new ArgumentTypeException(x + " < 0.0");
    }
    // based on dlmf.nist.gov/10.2.3
    if (F.isNumIntValue(n)) {
      FiniteDifferencesDifferentiator differentiator =
          new FiniteDifferencesDifferentiator(15, 0.01);
      ISymbol nSymbol = F.Dummy("n");
      UnivariateDifferentiableFunction f = differentiator.differentiate(
          new UnaryNumerical(F.BesselI(nSymbol, F.num(x)), nSymbol, EvalEngine.get()));
      DSFactory factory = new DSFactory(1, 1);
      double d1 = f.value(factory.variable(0, n)).getPartialDerivative(1);
      double d2 = f.value(factory.variable(0, -n)).getPartialDerivative(1);

      return ((d1 + d2) / 2.0) * Math.pow(-1.0, Math.rint(n + 1));
    }
    return (besselIDouble(-n, x) - besselIDouble(n, x)) * Math.PI / (2.0 * Math.sin(n * Math.PI));
  }

  public static Complex besselK(double n, Complex x) {
    return besselK(new Complex(n), x);
  }

  public static Complex besselK(Complex n, Complex x) {
    final int useAsymptotic = 10;

    // for averaging over integer orders until write code for limit
    double delta = 1e-5;

    // asymptotic form as per Johansson
    if (x.isReal() && Math.abs(x.getReal()) > useAsymptotic) {
      Complex t1 = mul(sqrt(div(Math.PI / 2.0, x)), exp(neg(x)));
      Complex t2 = HypergeometricJS.hypergeometric2F0(add(n, 0.5), sub(0.5, n), div(-0.5, x));
      return t1.multiply(t2);
    }

    EvalEngine engine = EvalEngine.get();
    int recursionLimit = engine.getRecursionLimit();
    try {
      if (recursionLimit > 0) {
        int counter = engine.incRecursionCounter();
        if (counter > recursionLimit) {
          RecursionLimitExceeded.throwIt(counter, S.BesselK);
        }
      }
      if (n.isMathematicalInteger()) {
        double nRe = n.getReal();
        // TODO use complex differentiator here
        // see https://github.com/Hipparchus-Math/hipparchus/issues/67
        return besselK(new Complex(nRe + delta), x).add(besselK(new Complex(nRe - delta), x))
            .divide(2.0);
      }
      Complex product = div(Math.PI / 2.0, mul(n, Math.PI).sin());
      return product.multiply(besselI(n.negate(), x).subtract(besselI(n, x)));
    } finally {
      if (recursionLimit > 0) {
        engine.decRecursionCounter();
      }
    }
  }

  public static Complex hankelH1(double n, double x) {
    // not duplicate special case
    if (F.isNumIntValue(n)) {
      return add(besselJ(n, x), mul(Complex.I, besselY(n, x)));
    }

    // one less call to besselJ
    var s = sub(mul(besselJ(n, x), exp(mul(n, new Complex(0, -Math.PI)))), besselJ(-n, x));
    return div(mul(Complex.I, s), Math.sin(n * Math.PI));
  }

  public static Complex hankelH1(Complex n, Complex x) {
    
    int useAsymptotic = 11;

    // dlmf.nist.gov/10.17
    if ( x.getImaginary() > 0 && x.norm() > useAsymptotic ) {

      Complex w = add(-Math.PI / 4.0, x, mul(n, -Math.PI / 2.0));

      Complex s = Complex.ONE;
      Complex plusI = Complex.I;
      Complex p = Complex.ONE;
      int i = 1;

      while ( Math.abs(p.getReal()) > Config.SPECIAL_FUNCTIONS_TOLERANCE //
          || Math.abs(p.getImaginary()) > Config.SPECIAL_FUNCTIONS_TOLERANCE ) {
        p = mul(1.0 / (i * 8.0), p, plusI, sub(mul(4.0, n, n), Math.pow(2 * i - 1, 2)), inv(x));
        s = add( s, p );
        i++;
      }

      return mul(div(Math.sqrt(2.0 / Math.PI), sqrt(x)), exp(mul(plusI, w)), s);

    }
    
    // not duplicate special case
    if (n.isMathematicalInteger()) {
      return add(besselJ(n, x), mul(Complex.I, besselY(n, x)));
    }

    // one less call to besselJ
    Complex s =
        sub(mul(besselJ(n, x), exp(mul(n, new Complex(0, -Math.PI)))), besselJ(n.negate(), x));
    return div(mul(Complex.I, s), mul(n, Math.PI).sin());
  }

  public static Complex hankelH2(double n, double x) {
    // not duplicate special case
    if (F.isNumIntValue(n)) {
      return sub(besselJ(n, x), mul(Complex.I, besselY(n, x)));
    }
    // one less call to besselJ
    Complex s = sub(mul(besselJ(n, x), exp(mul(n, new Complex(0, Math.PI)))), besselJ(-n, x));
    return div(mul(Complex.MINUS_I, s), Math.sin(n * Math.PI));
  }

  public static Complex hankelH2(Complex n, Complex x) {

    int useAsymptotic = 11;

    // dlmf.nist.gov/10.17
    if (x.getImaginary() < 0 && x.norm() > useAsymptotic) {

      Complex w = add(-Math.PI / 4.0, x, mul(n, -Math.PI / 2.0));

      Complex s = Complex.ONE;
      Complex minusI = Complex.MINUS_I;
      Complex p = Complex.ONE;
      int i = 1;

      while (Math.abs(p.getReal()) > Config.SPECIAL_FUNCTIONS_TOLERANCE //
          || Math.abs(p.getImaginary()) > Config.SPECIAL_FUNCTIONS_TOLERANCE) {
        p = mul(1.0 / (i * 8.0), p, minusI, sub(mul(4.0, n, n), Math.pow(2 * i - 1, 2)), inv(x));
        s = add(s, p);
        i++;
      }

      return mul(div(Math.sqrt(2.0 / Math.PI), sqrt(x)), exp(mul(minusI, w)), s);

    }

    // not duplicate special case
    if (n.isMathematicalInteger()) {
      return sub(besselJ(n, x), mul(Complex.I, besselY(n, x)));
    }
    // one less call to besselJ
    Complex s = sub(mul(besselJ(n, x), exp(mul(n, new Complex(0, Math.PI)))), besselJ(neg(n), x));
    return div(mul(Complex.MINUS_I, s), mul(n, Math.PI).sin());
  }

  public static Complex airyAi(double x) {

    if (F.isZero(x)) {
      return new Complex(1.0 / Math.pow(3.0, 2.0 / 3.0) / GammaJS.gamma(2.0 / 3.0));
    }

    if (x < 0) {
      Complex xMinus = new Complex(-x);
      Complex z = xMinus.pow(1.5).multiply(2.0 / 3.0);
      return xMinus.sqrt().divide(3.0).multiply(besselJ(1.0 / 3.0, z).add(besselJ(-1.0 / 3.0, z)));
    }

    Complex xc = new Complex(x);
    Complex z = xc.pow(1.5).multiply(2.0 / 3.0);
    return besselK(1.0 / 3.0, z).multiply(1 / Math.PI).multiply(xc.divide(3.0).sqrt());
  }

  /**
   * See: <a href="http://dlmf.nist.gov/9.2.ii"></a> <a href="http://dlmf.nist.gov/9.6.i"></a>
   *
   * @param x
   * @return
   */
  public static Complex airyAi(Complex x) {
    if (F.isZero(x)) {
      return new Complex(1.0 / Math.pow(3.0, 2.0 / 3.0) / GammaJS.gamma(2.0 / 3.0));
    }

    if (x.getReal() < 0) {
      Complex xMinus = x.negate();
      Complex z = xMinus.pow(1.5).multiply(2.0 / 3.0);
      return xMinus.sqrt().divide(3.0).multiply(besselJ(1.0 / 3.0, z).add(besselJ(-1.0 / 3.0, z)));
    }

    Complex z = x.pow(1.5).multiply(2.0 / 3.0);
    return besselK(1.0 / 3.0, z).multiply(x.divide(3.0).sqrt()).multiply(1 / Math.PI);
  }

  public static Complex airyAiPrime(double x) {
    return airyAiPrime(new Complex(x));
  }

  public static Complex airyAiPrime(Complex x) {
    if (F.isZero(x)) {
      return new Complex(-1.0 / Math.pow(3.0, 1.0 / 3.0) / GammaJS.gamma(1.0 / 3.0));
    }

    if (x.getReal() < 0) {
      Complex xMinus = x.negate();
      Complex z = xMinus.pow(1.5).multiply(2.0 / 3.0);
      return x.divide(3.0).multiply(besselJ(-2.0 / 3.0, z).subtract(besselJ(2.0 / 3.0, z)));
    }

    Complex z = x.pow(1.5).multiply(2.0 / 3.0);
    return besselK(2.0 / 3.0, z).multiply(x).multiply(-1.0 / Math.PI / Math.sqrt(3.0));
  }

  public static Complex airyBi(double x) {
    return airyBi(new Complex(x));
  }

  public static Complex airyBi(Complex x) {
    if (F.isZero(x)) {
      return new Complex(1.0 / Math.pow(3.0, 1.0 / 6.0) / GammaJS.gamma(2.0 / 3.0));
    }

    if (x.getReal() < 0) {
      Complex xMinus = x.negate();
      Complex z = xMinus.pow(1.5).multiply(2.0 / 3.0);
      return xMinus.divide(3.0).sqrt()
          .multiply(besselJ(-1.0 / 3.0, z).subtract(besselJ(1.0 / 3.0, z)));
    }

    Complex z = x.pow(1.5).multiply(2.0 / 3.0);
    return x.divide(3.0).sqrt().multiply(besselI(1.0 / 3.0, z).add(besselI(-1.0 / 3.0, z)));
  }

  public static Complex airyBiPrime(double x) {
    return airyBiPrime(new Complex(x));
  }

  public static Complex airyBiPrime(Complex x) {
    if (F.isZero(x)) {
      return new Complex(Math.pow(3.0, 1.0 / 6.0) / GammaJS.gamma(1.0 / 3.0));
    }

    if (x.getReal() < 0) {
      Complex xMinus = x.negate();
      Complex z = xMinus.pow(1.5).multiply(2.0 / 3.0);
      return xMinus.multiply(1.0 / Math.sqrt(3.0))
          .multiply(besselJ(2.0 / 3.0, z).add(besselJ(-2.0 / 3.0, z)));
    }

    Complex z = x.pow(1.5).multiply(2.0 / 3.0);
    return x.multiply(1.0 / Math.sqrt(3.0))
        .multiply(besselI(2.0 / 3.0, z).add(besselI(-2.0 / 3.0, z)));
  }

  public static Complex sphericalBesselJ(double n, double x) {
    return besselJ(n + 0.5, x).multiply(sqrt(Math.PI / 2.0).divide(new Complex(x).sqrt()));
  }

  public static Complex sphericalBesselJ(Complex n, Complex x) {
    return mul(div(sqrt(Math.PI / 2.0), sqrt(x)), besselJ(add(n, .5), x));
  }

  public static Complex sphericalBesselY(double n, double x) {
    Complex besselY = besselY(n + 0.5, x);
    return new Complex(Math.sqrt(Math.PI / 2.0)).divide(new Complex(x).sqrt()).multiply(besselY);
  }

  public static Complex sphericalBesselY(Complex n, Complex x) {
    return mul(div(sqrt(Math.PI / 2.0), sqrt(x)), besselY(add(n, 0.5), x));
  }

  public static Complex sphericalHankel1(double n, double x) {
    return mul(Math.sqrt(Math.PI / 2.0) / Math.sqrt(x), hankelH1(n + 0.5, x));
    // return add(sphericalBesselJ(n, x), mul(Complex.I, sphericalBesselY(n, x)));
  }

  public static Complex sphericalHankel1(Complex n, Complex x) {
    return mul(div(Math.sqrt(Math.PI / 2.0), sqrt(x)), hankelH1(add(n, 0.5), x));
  }

  public static Complex sphericalHankel2(double n, double x) {
    return mul(Math.sqrt(Math.PI / 2.0) / Math.sqrt(x), hankelH2(n + 0.5, x));
    // return sub(sphericalBesselJ(n, x), mul(Complex.I, sphericalBesselY(n, x)));
  }

  public static Complex sphericalHankel2(Complex n, Complex x) {
    return mul(div(Math.sqrt(Math.PI / 2.0), sqrt(x)), hankelH2(add(n, 0.5), x));
  }

  public static double struveH(double n, double x) {
    // can also evaluate from hypergeometric0F1
    // could use to test hypergeometricPFQ
    return Math.pow(x, n + 1.0) / (Math.pow(2.0, n) * Math.sqrt(Math.PI) * GammaJS.gamma(n + 1.5)) * //
        HypergeometricJS.hypergeometric1F2(1, 1.5, n + 1.5, (-0.25) * x * x);
  }

  public static Complex struveH(Complex n, Complex x) {
    // can also evaluate from hypergeometric0F1
    // could use to test hypergeometricPFQ
    return mul(pow(x, add(n, 1)), inv(mul(pow(2.0, n), sqrt(Math.PI), gamma(add(n, 1.5)))),
        HypergeometricJS.hypergeometric1F2(Complex.ONE, new Complex(1.5), add(1.5, n),
            mul(-0.25, pow(x, 2.0))));
  }

  public static double struveL(double n, double x) {
    // one sign different in 0.25 from struveH
    return Math.pow(x, n + 1.0) * 1
        / (Math.pow(2.0, n) * Math.sqrt(Math.PI) * GammaJS.gamma(n + 1.5)) * //
        HypergeometricJS.hypergeometric1F2(1, 1.5, n + 1.5, 0.25 * x * x);
  }

  public static Complex struveL(Complex n, Complex x) {
    // one sign different in 0.25 from struveH
    return mul(pow(x, add(n, 1)), inv(mul(pow(2.0, n), sqrt(Math.PI), gamma(add(n, 1.5)))),
        HypergeometricJS.hypergeometric1F2(Complex.ONE, new Complex(1.5), add(1.5, n),
            mul(0.25, pow(x, 2.0))));
  }
}

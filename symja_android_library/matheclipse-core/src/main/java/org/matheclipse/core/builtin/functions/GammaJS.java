package org.matheclipse.core.builtin.functions;

import static de.lab4inf.math.util.Accuracy.hasConverged;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static org.matheclipse.core.builtin.functions.HypergeometricJS.hypergeometric1F1;
import org.hipparchus.analysis.differentiation.DSFactory;
import org.hipparchus.analysis.differentiation.FiniteDifferencesDifferentiator;
import org.hipparchus.analysis.differentiation.UnivariateDifferentiableFunction;
import org.hipparchus.complex.Complex;
import org.hipparchus.special.Gamma;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Arithmetic;
import org.matheclipse.core.builtin.ConstantDefinitions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.NumberUtil;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;
import com.google.common.math.DoubleMath;
import de.lab4inf.math.Function;
import de.lab4inf.math.gof.Visitor;
import de.lab4inf.math.util.ContinuedFraction;

/**
 * Ported from JavaScript file <a
 * href="https://github.com/paulmasson/math/blob/master/src/functions/gamma.js">gamma.js</a>
 */
public class GammaJS {

  private GammaJS() {}

  private static final double DEFAULT_EPSILON = 1.E-14;
  private static final int MAX_ITERATIONS = 1500;

  /** Internal helper class for the continued fraction. */
  static class RegularizedGammaFraction extends ContinuedFraction {
    private final double a;

    public RegularizedGammaFraction(final double a) {
      this.a = a;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.util.ContinuedFraction#getA0(double)
     */
    @Override
    protected double getA0(final double x) {
      return getAn(0, x);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.util.ContinuedFraction#getAn(int, double)
     */
    @Override
    protected double getAn(final int n, final double x) {
      return (2.0 * n + 1.0) - a + x;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.util.ContinuedFraction#getBn(int, double)
     */
    @Override
    protected double getBn(final int n, final double x) {
      return n * (a - n);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.gof.Visitable#accept(de.lab4inf.math.gof.Visitor)
     */
    @Override
    public void accept(final Visitor<Function> visitor) {
      visitor.visit(this);
    }
  }

  /**
   * Calculate the regularized gamma function P(a,x), with epsilon precision using maximal max
   * iterations. The algorithm uses series expansion 6.5.29 and formula 6.5.4 from A&amp;ST.
   *
   * @param a the a parameter.
   * @param x the value.
   * @param eps the desired accuracy
   * @param max maximum number of iterations to complete
   * @return the regularized gamma function P(a,x)
   */
  private static double regGammaP(final double a, final double x, final double eps, final int max) {
    double ret = 0;
    if ((a <= 0.0) || (x < 0.0)) {
      throw new ArgumentTypeException(String.format("P(%f,%f)", a, x));
    }
    if (a >= 1 && x > a) {
      ret = 1.0 - regGammaQ(a, x, eps, max);
    } else if (x > 0) {
      // calculate series expansion A&S 6.5.29
      int n = 1;
      final double ea = exp(-x + (a * log(x)) - GammaJS.logGamma(a));
      final double err = eps;
      double an = 1.0 / a, so, sn = an;
      do {
        so = sn;
        an *= x / (a + n);
        sn += an;
      } while (!hasConverged(sn, so, err, ++n, max));
      // do the transformation 6.5.4
      ret = ea * sn;
    }
    return ret;
  }

  /**
   * Calculate the regularized gamma function Q(a,x) = 1 - P(a,x), with epsilon precision using
   * maximal maxIterations. The algorithm uses a continued fraction until convergence is reached.
   *
   * @param a the a parameter.
   * @param x the value.
   * @param epsilon the desired accuracy
   * @param maxIterations maximum number of iterations to complete
   * @return the regularized gamma function Q(a,x)
   */
  private static double regGammaQ(
      final double a, final double x, final double epsilon, final int maxIterations) {
    double ret = 0;

    if ((a <= 0.0) || (x < 0.0)) {
      throw new ArgumentTypeException(String.format("Q(%f,%f)", a, x));
    }
    if (x < a || a < 1.0) {
      ret = 1.0 - regGammaP(a, x, epsilon, maxIterations);
    } else if (x > 0) {
      // create continued fraction analog to A&S 6.5.31 / 26.4.10 ?
      // this implementation is due to Wolfram research
      // http://functions.wolfram.com/GammaBetaErf/GammaRegularized/10/0003/
      final double ea = exp(-x + (a * log(x)) - GammaJS.logGamma(a));
      final double err = epsilon;
      final ContinuedFraction cf = new RegularizedGammaFraction(a);
      ret = 1.0 / cf.evaluate(x, err, maxIterations);
      ret *= ea;
    }
    return ret;
  }

  public static double factorialInt(double n) {
    if (n < 0.0) {
      throw new ArgumentTypeException("Factorial: n<0.0");
    }
    return DoubleMath.factorial(NumberUtil.toInt(n));
    //    double result = 1.0;
    //    for (int i = 2; i <= n; i++) {
    //      result *= i;
    //    }
    //    return result;
  }

  public static Complex beta(Complex x, Complex y) {
    return Arithmetic.lanczosApproxGamma(x)
        .multiply(Arithmetic.lanczosApproxGamma(y))
        .divide(Arithmetic.lanczosApproxGamma(x.add(y)));
  }

  public static Complex beta(Complex x, Complex y, Complex z) {
    return x.pow(y)
        .multiply(
            HypergeometricJS.hypergeometric2F1(y, new Complex(1.0).subtract(z), y.add(1.0), x))
        .divide(y);
  }

  public static Complex beta(Complex x, Complex y, Complex z, Complex w) {
    return beta(y, z, w).subtract(beta(x, z, w));
  }

  public static double beta(double x, double y) {
    return Gamma.gamma(x) * Gamma.gamma(y) / Gamma.gamma(x + y);
  }

  public static double beta(double x, double y, double z) {
    return Math.pow(x, y) * HypergeometricJS.hypergeometric2F1(y, 1.0 - z, y + 1.0, x) / y;
  }

  public static double beta(double x, double y, double z, double w) {
    return beta(y, z, w) - beta(x, z, w);
  }

  public static Complex betaRegularized(Complex x, Complex y, Complex z) {
    return beta(x, y, z).divide(beta(y, z));
  }

  public static Complex betaRegularized(Complex x, Complex y, Complex z, Complex w) {
    return beta(x, y, z, w).divide(beta(z, w));
  }

  public static double betaRegularized(double x, double y, double z) {
    // see github #203
    if (y < 0.0) {
      throw new ArgumentTypeException("y not positiv: " + y);
    }
    if (z < 0.0) {
      throw new ArgumentTypeException("z not positiv: " + z);
    }
    if (x < 0 || x > 1) {
      throw new ArgumentTypeException("x range wrong: " + x);
    }
    return org.hipparchus.special.Beta.regularizedBeta(x, y, z);
  }

  public static double betaRegularized(double x, double y, double z, double w) {
    return beta(x, y, z, w) / beta(z, w);
  }

  public static INumber incompleteBeta(double x, double y, double z) {
    if (x == -1 || x > 1) {
      return F.complexNum(beta(new Complex(x), new Complex(y), new Complex(z)));
    }
    return F.num(beta(x, y, z));
  }

  public static Complex fresnelS(Complex x) {

    Complex m1 =
        HypergeometricJS.hypergeometric1F1(
            new Complex(0.5),
            new Complex(1.5),
            new Complex(0, Math.PI / 2).multiply(x.multiply(x)));
    Complex m2 =
        HypergeometricJS.hypergeometric1F1(
            new Complex(0.5),
            new Complex(1.5),
            new Complex(0, -Math.PI / 2).multiply(x.multiply(x)));

    Complex result = x.multiply(m1.subtract(m2)).multiply(new Complex(0, -0.5));
    return result;
  }

  public static Complex fresnelC(Complex x) {

    Complex m1 =
        HypergeometricJS.hypergeometric1F1(
            new Complex(0.5),
            new Complex(1.5),
            new Complex(0, Math.PI / 2).multiply(x.multiply(x)));
    Complex m2 =
        HypergeometricJS.hypergeometric1F1(
            new Complex(0.5),
            new Complex(1.5),
            new Complex(0, -Math.PI / 2).multiply(x.multiply(x)));

    Complex result = x.multiply(m1.add(m2)).multiply(0.5);
    return result;
  }

  public static Complex gamma(Complex x) {
    return Arithmetic.lanczosApproxGamma(x);
  }

  public static double gamma(double x) {
    return org.hipparchus.special.Gamma.gamma(x);
  }

  /**
   * Incomplete gamma function.
   *
   * @param x
   * @param y
   * @return
   */
  public static double gamma(double x, double y) {
    return org.hipparchus.special.Gamma.gamma(x) * regGammaQ(x, y, DEFAULT_EPSILON, MAX_ITERATIONS);
  }

  /**
   * Incomplete gamma function.
   *
   * @param x
   * @param y
   * @return
   */
  public static Complex gamma(Complex x, Complex y) {
    // patch lower end or evaluate exponential integral independently
    if (F.isZero(x)) {
      if (F.isZero(y)) {
        throw new ArgumentTypeException("Gamma function pole");
      }
      // if (Complex.equals(x, Complex.ZERO, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
      // // taylorSeries => (-EulerGamma - Log(y)) + x - 1/4 * x^2 + 1/18 * x^3 - 1/96 * x^4 + 1/600
      // * x^5
      // Complex result = y.log().add(ConstantDefinitions.EULER_GAMMA).negate();
      // double[] coeff = new double[] { 1.0, -0.25, 1.0 / 18.0, -1.0 / 96.0, 1.0 / 600.0 };
      // Complex yPow = y;
      // for (int i = 0; i < coeff.length; i++) {
      // result = result.add(yPow.multiply(coeff[i]));
      // yPow = yPow.multiply(y);
      // }
      // return result;

      Complex yLogNegate = y.log().negate();
      Complex result =
          expIntegralEi(y.negate())
              .negate()
              .add( //
                  y.negate().log().subtract(y.reciprocal().negate().log()).multiply(0.5))
              .add( //
                  yLogNegate);
      if (F.isZero(y.getImaginary()) && y.getReal() > 0.0) {
        return new Complex(result.getReal());
      }
      return result;
    }

    double delta = 1e-5;
    if (x.norm() < delta) {
      // TODO implement case for abs value near 0

      // return taylorSeries( t => gamma(t,y), mul( x, delta/x.abs( ) ), 2.0)(x);
    }

    // dlmf.nist.gov/8.4.15
    double xRe = x.getReal();
    if (xRe < 0.0 && F.isNumIntValue(xRe) && F.isZero(x.getImaginary())) {
      // x is a negative integer
      final double n = -xRe;
      int iterationLimit = EvalEngine.get().getIterationLimit();
      final Complex t =
          y.negate()
              .exp()
              .multiply( //
                  ZetaJS.complexSummation(
                      k ->
                          new Complex(Math.pow(-1.0, k) * factorialInt(k)).divide(y.pow(k + 1)), //
                      0.0,
                      n - 1.0,
                      iterationLimit));
      // dlmf.nist.gov/8.4.4
      final double plusMinusOne = Math.pow(-1.0, n);
      return gamma(Complex.ZERO, y).subtract(t).multiply(plusMinusOne / factorialInt(n));
    }

    return Arithmetic.lanczosApproxGamma(x).subtract(gamma(x, 0.0, y));
  }

  public static Complex gamma(Complex x, double y, Complex z) {
    if (!F.isZero(y, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
      return gamma(x, 0.0, z).subtract(gamma(x, 0.0, new Complex(y)));
    }

    return z.pow(x).multiply(x.reciprocal()).multiply(hypergeometric1F1(x, x.add(1.0), z.negate()));
  }

  public static double expIntegralEi(double x) {
    double useAsymptotic = 26.0;

    if (x < 0.0) {
      return expIntegralEi(new Complex(x)).getReal();
    }

    if (Math.abs(x) > useAsymptotic) {

      double s = 1;
      double p = 1;
      int i = 1;

      while (Math.abs(p) > Config.SPECIAL_FUNCTIONS_TOLERANCE) {
        p *= i / x;
        s += p;
        i++;
      }

      return s * Math.exp(x) / x;
    }

    double s = 0;
    double p = 1;
    int i = 1;

    while (Math.abs(p / i) > Config.SPECIAL_FUNCTIONS_TOLERANCE) {
      p *= x / i;
      s += p / i;
      i++;
    }

    return s + ConstantDefinitions.EULER_GAMMA + Math.log(x);
  }

  public static Complex expIntegralEi(Complex x) {
    double useAsymptotic = 26.0;
    if (x.norm() > useAsymptotic) {

      Complex s = Complex.ONE;
      Complex p = Complex.ONE;
      int i = 1;

      Complex xInverse = x.reciprocal();
      while (Math.abs(p.getReal()) > Config.SPECIAL_FUNCTIONS_TOLERANCE
          || Math.abs(p.getImaginary()) > Config.SPECIAL_FUNCTIONS_TOLERANCE) {
        p = p.multiply(i).multiply(xInverse);
        s = s.add(p);
        i++;
      }

      // combination of logarithms adds/subtracts Complex(0,Pi)
      int sign = x.getImaginary() > 0 ? 1 : x.getImaginary() < 0 ? -1 : 0;

      return s.multiply(x.exp()).multiply(xInverse).add(new Complex(0.0, sign * Math.PI));
    }

    Complex s = Complex.ZERO;
    Complex p = Complex.ONE;
    int i = 1;
    while (Math.abs(p.getReal() / i) > Config.SPECIAL_FUNCTIONS_TOLERANCE
        || //
        Math.abs(p.getImaginary() / i) > Config.SPECIAL_FUNCTIONS_TOLERANCE) {
      p = p.multiply(x).divide(i);
      s = s.add(p.divide(i));
      i++;
    }

    s = s.add(ConstantDefinitions.EULER_GAMMA).add(x.log());

    // real on negative real axis, set phase explicitly rather than log combo
    if (x.getReal() < 0.0 && F.isZero(x.getImaginary())) {
      return new Complex(s.getReal(), 0.0);
    }

    return s;
  }

  public static double logIntegral(double x) {
    if (x <= 0) {
      throw new ArgumentTypeException("logIntegral: x<=0");
    }
    return expIntegralEi(Math.log(x));
  }

  public static Complex logIntegral(Complex x) {
    return expIntegralEi(x.log());

    // Complex result = gamma(Complex.ZERO, x.log().negate()).negate()
    // .add(x.log().log().subtract(x.log().reciprocal().log()).multiply(0.5))
    // .add(x.log().negate().log().negate());
    //
    // // if ( isComplex(x) ) return result;
    // // return result.re;
    // return result;
  }

  /**
   * The digamma function.
   *
   * @return
   */
  public static double polyGamma(double x) {
    //		  return diff( x => logGamma(x), x );
    ISymbol xSymbol = F.Dummy("x");
    FiniteDifferencesDifferentiator differentiator = new FiniteDifferencesDifferentiator(15, 0.01);
    UnivariateDifferentiableFunction f =
        differentiator.differentiate(
            new UnaryNumerical(F.LogGamma(xSymbol), xSymbol, EvalEngine.get()));
    DSFactory factory = new DSFactory(1, 1);
    return f.value(factory.variable(0, x)).getPartialDerivative(1);
  }

  public static double polyGamma(int n, double x) {
    //		  if ( arguments.length === 1 ) {
    //			  return digamma(x);
    //		  }
    if (n <= 0) {
      throw new ArgumentTypeException("PolyGamma: Unsupported polygamma index");
    }

    return Math.pow(-1, n + 1) * factorialInt(n) * ZetaJS.hurwitzZeta((double) n + 1, x);
  }

  public static Complex sinIntegral(Complex x) {
    if (Complex.equals(x, Complex.ZERO, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
      return Complex.ZERO;
    }

    Complex ix = Complex.I.multiply(x);

    Complex result =
        new Complex(0, 0.5)
            .multiply(
                gamma(Complex.ZERO, ix.negate())
                    .add(gammaZero(ix).negate())
                    .add(ix.negate().log())
                    .add(ix.log().negate()));

    if (F.isZero(x.getImaginary())) {
      return new Complex(result.getReal());
    }
    // if ( isComplex(x) ) return result;
    // return result.re;
    return result;
  }

  public static Complex cosIntegral(Complex x) {
    // complex for negative real argument
    Complex ix = Complex.I.multiply(x);

    Complex result =
        x.log()
            .subtract(
                gammaZero(ix.negate())
                    .add(gammaZero(ix))
                    .add(ix.negate().log())
                    .add(ix.log())
                    .multiply(0.5));
    return result;
  }

  public static Complex sinhIntegral(Complex x) {
    if (Complex.equals(x, Complex.ZERO, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
      return Complex.ZERO;
    }

    Complex result =
        gammaZero(x)
            .add(gammaZero(x.negate()).negate())
            .add(x.log())
            .add(x.negate().log().negate())
            .multiply(0.5);

    if (F.isZero(x.getImaginary())) {
      return new Complex(result.getReal());
    }
    // if ( isComplex(x) ) return result;
    // return result.re;
    return result;
  }

  public static Complex coshIntegral(Complex x) {
    // complex for negative real argument
    Complex xNegate = x.negate();
    Complex gamma1 = gammaZero(x);
    Complex gamma2 = gammaZero(xNegate);
    Complex result = gamma1.add(gamma2.add(x.log().negate()).add(xNegate.log())).multiply(-0.5);

    return result;
  }

  public static Complex gammaRegularized(Complex x, double y, Complex z) {
    return gamma(x, y, z).divide(gamma(x));
  }

  public static Complex gammaRegularized(Complex x, Complex y) {
    return gamma(x, y).divide(gamma(x));
  }

  public static double gammaRegularized(double x, double y, double z) {
    return org.hipparchus.special.Gamma.regularizedGammaQ(x, y)
        - //
        org.hipparchus.special.Gamma.regularizedGammaQ(x, z);
    // Complex cx = new Complex(x);
    // return gamma(cx, y, new Complex(z)).divide(gamma(cx));
  }

  public static double gammaRegularized(double x, double y) {
    return org.hipparchus.special.Gamma.regularizedGammaQ(x, y);
    // return gamma(x, y) / gamma(x);
  }

  /**
   * Incomplete gamma function. Gamma(0, x).
   *
   * @param x
   * @return
   */
  private static Complex gammaZero(Complex x) {
    return gamma(Complex.ZERO, x);
  }

  public static Complex expIntegralE(Complex n, Complex x) {

    if (n.equals(Complex.ZERO)) {
      return x.negate().exp().divide(x);
    }

    final Complex nSubtract1 = n.subtract(1.0);
    if (Complex.equals(x, Complex.ZERO, Config.SPECIAL_FUNCTIONS_TOLERANCE) && n.getReal() > 1.0) {
      return nSubtract1.reciprocal();
    }

    return x.pow(nSubtract1).multiply(gamma(Complex.ONE.subtract(n), x));
  }

  static final double[] c = {
    57.1562356658629235,
    -59.5979603554754912,
    14.1360979747417471,
    -0.491913816097620199,
    .339946499848118887e-4,
    .465236289270485756e-4,
    -.983744753048795646e-4,
    .158088703224912494e-3,
    -.210264441724104883e-3,
    .217439618115212643e-3,
    -.164318106536763890e-3,
    .844182239838527433e-4,
    -.261908384015814087e-4,
    .368991826595316234e-5
  };

  public static Complex logGamma(Complex x) {
    // if ( isComplex(x) ) {

    if (F.isNumIntValue(x.getReal()) && x.getReal() <= 0 && F.isZero(x.getImaginary())) {
      throw new ArgumentTypeException("Gamma function pole");
    }

    // reflection formula with modified Hare correction to imaginary part
    if (x.getReal() < 0.0) {
      Complex t =
          new Complex(Math.PI)
              .divide(x.multiply(Math.PI).sin())
              .log()
              .subtract(logGamma(x.negate().add(1.0)));
      double s = x.getImaginary() < 0.0 ? -1.0 : 1.0;
      double d = F.isZero(x.getImaginary()) ? 0.25 : 0;
      double k = Math.ceil(x.getReal() / 2.0 - 0.75 + d);
      return t.add(new Complex(0.0, 2.0 * s * k * Math.PI));
    }

    Complex t = x.add(5.24218750000000000);
    t = x.add(0.5).multiply(t.log()).subtract(t);
    Complex s = new Complex(0.999999999999997092);
    for (int j = 0; j < 14; j++) {
      s = s.add(x.add(j + 1).reciprocal().multiply(c[j]));
    }
    Complex u = t.add(s.divide(x).multiply(2.5066282746310005).log());

    // adjustment to keep imaginary part on same sheet
    if (s.getReal() < 0.0) {
      if (x.getImaginary() < 0.0 && s.divide(x).getImaginary() < 0) {
        u = u.add(new Complex(0.0, Math.PI + Math.PI));
      }
      if (x.getImaginary() > 0 && s.divide(x).getImaginary() > 0) {
        u = u.add(new Complex(0.0, -Math.PI + Math.PI));
      }
    }

    return u;
  }

  public static double logGamma(double x) {
    return org.hipparchus.special.Gamma.logGamma(x);
    // if (F.isNumIntValue(x) && x <= 0) {
    // throw new ArgumentTypeException("Gamma function pole");
    // }
    //
    // double t = x + 5.24218750000000000;
    // t = (x + 0.5) * Math.log(t) - t;
    // double s = 0.999999999999997092;
    // for (int j = 0; j < 14; j++) {
    // s += c[j] / (x + j + 1);
    // }
    // return t + Math.log(2.5066282746310005 * s / x);

  }
}

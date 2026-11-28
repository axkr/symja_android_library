package org.matheclipse.core.builtin;

import java.math.BigInteger;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.annotation.AllowNIL;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Functions of analytic number theory, computed with machine numbers: <code>RiemannR</code>, and
 * <code>RamanujanTauL</code>, <code>RamanujanTauTheta</code> and <code>RamanujanTauZ</code> of the
 * weight 12 cusp form <code>Delta(q) = q Product((1 - q^n)^24)</code>.
 */
public final class LFunctions {

  /** The number of Ramanujan tau values used by the L-function. */
  private static final int TAU_TERMS = 500;

  private static final double TWO_PI = 2.0 * Math.PI;

  private static class Initializer {

    private static void init() {
      S.RiemannR.setEvaluator(new RiemannR());
      S.RamanujanTauL.setEvaluator(new RamanujanTau(S.RamanujanTauL));
      S.RamanujanTauTheta.setEvaluator(new RamanujanTau(S.RamanujanTauTheta));
      S.RamanujanTauZ.setEvaluator(new RamanujanTau(S.RamanujanTauZ));
    }
  }

  // ---------------------------------------------------------------------------------------------
  // RiemannR

  /**
   * <code>RiemannR(x)</code> - Riemann's prime counting function
   * <code>Sum(MoebiusMu(n)/n LogIntegral(x^(1/n)), {n, 1, Infinity})</code>, computed for
   * <code>x &gt; 0</code> with Gram's series
   * <code>1 + Sum(Log(x)^k/(k k! Zeta(k+1)), {k, 1, Infinity})</code>.
   */
  private static final class RiemannR extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr x = ast.arg1();
      if (x.isOne()) {
        return F.C1;
      }
      if (!x.isInexactNumber() || !x.isReal()) {
        return F.NIL;
      }
      double value = x.evalf();
      if (!(value > 0.0) || Double.isInfinite(value)) {
        return F.NIL;
      }
      return F.num(riemannR(value, engine));
    }

    private static double riemannR(double x, EvalEngine engine) {
      final double logX = Math.log(x);
      if (logX == 0.0) {
        return 1.0;
      }
      final double logAbs = Math.log(Math.abs(logX));
      double sum = 1.0;
      for (int k = 1; k < 100_000; k++) {
        if ((k & 0xFF) == 0) {
          engine.checkTimeLimit();
        }
        double magnitude =
            Math.exp(k * logAbs - org.hipparchus.special.Gamma.logGamma(k + 1.0)) / k;
        double term = magnitude / zeta(k + 1);
        if (logX < 0.0 && (k % 2 == 1)) {
          term = -term;
        }
        sum += term;
        if (k > Math.abs(logX) && Math.abs(term) < 1.0e-17 * Math.abs(sum)) {
          break;
        }
      }
      return sum;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
    }
  }

  /** <code>Zeta(s)</code> for an integer <code>s &gt;= 2</code> with Euler-Maclaurin summation. */
  static double zeta(int s) {
    if (s > 60) {
      return 1.0 + Math.pow(2.0, -s);
    }
    final int n = 100;
    double sum = 0.0;
    for (int k = n - 1; k >= 1; k--) {
      sum += Math.pow(k, -s);
    }
    double nPow = Math.pow(n, -s);
    sum += n * nPow / (s - 1) + nPow / 2.0 + s * nPow / (12.0 * n)
        - s * (s + 1.0) * (s + 2.0) * nPow / (720.0 * n * n * n)
        + s * (s + 1.0) * (s + 2.0) * (s + 3.0) * (s + 4.0) * nPow / (30240.0 * Math.pow(n, 5));
    return sum;
  }

  // ---------------------------------------------------------------------------------------------
  // Ramanujan tau L-function

  /**
   * <code>RamanujanTauL(s)</code> - the L-function <code>Sum(RamanujanTau(n)/n^s)</code>, continued
   * to all <code>s</code> by the smoothed functional equation.
   *
   * <p>
   * <code>RamanujanTauTheta(t)</code> - <code>Im(LogGamma(6 + I t)) - t Log(2 Pi)</code>.
   *
   * <p>
   * <code>RamanujanTauZ(t)</code> - <code>Exp(I RamanujanTauTheta(t)) RamanujanTauL(6 + I t)</code>,
   * which is real for real <code>t</code>.
   *
   * <p>
   * The functions are computed for machine numbers; exact arguments stay unevaluated.
   */
  private static final class RamanujanTau extends AbstractFunctionEvaluator {

    private final ISymbol function;

    RamanujanTau(ISymbol function) {
      this.function = function;
    }

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg = ast.arg1();
      if (!arg.isInexactNumber()) {
        return F.NIL;
      }
      if (function == S.RamanujanTauL) {
        Complex s = arg.evalfc();
        Complex value = tauL(s, engine);
        return arg.isReal() ? F.num(value.getReal())
            : F.complexNum(value.getReal(), value.getImaginary());
      }
      if (!arg.isReal()) {
        return F.NIL;
      }
      double t = arg.evalf();
      if (function == S.RamanujanTauTheta) {
        return F.num(theta(t));
      }
      Complex l = tauL(new Complex(6.0, t), engine);
      double th = theta(t);
      return F.num(Math.cos(th) * l.getReal() - Math.sin(th) * l.getImaginary());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
    }
  }

  /** <code>Im(LogGamma(6 + I t)) - t Log(2 Pi)</code>, continuous in <code>t</code>. */
  static double theta(double t) {
    return logGamma(new Complex(6.0, t)).getImaginary() - t * Math.log(TWO_PI);
  }

  /**
   * <code>L(s) = Sum(tau(n) (n^-s Gamma(s, 2 Pi n) + (2 Pi)^(2s-12) n^(s-12) Gamma(12-s, 2 Pi n)))
   * / Gamma(s)</code>.
   */
  static Complex tauL(Complex s, EvalEngine engine) {
    final long[] tau = Tau.VALUES;
    final Complex s12 = new Complex(12.0 - s.getReal(), -s.getImaginary());
    final Complex twoPiPower = new Complex(TWO_PI).pow(s.multiply(2.0).subtract(12.0));
    Complex sum = Complex.ZERO;
    for (int n = 1; n < tau.length; n++) {
      engine.checkTimeLimit();
      double x = TWO_PI * n;
      Complex nC = new Complex(n);
      Complex first = nC.pow(s.negate()).multiply(upperGamma(s, x));
      Complex second = twoPiPower.multiply(nC.pow(s.subtract(12.0))).multiply(upperGamma(s12, x));
      Complex term = first.add(second).multiply(tau[n]);
      sum = sum.add(term);
      if (n > Math.abs(s.getImaginary()) / TWO_PI + 5.0
          && term.norm() < 1.0e-17 * Math.max(1.0, sum.norm())) {
        break;
      }
    }
    return sum.divide(logGamma(s).exp());
  }

  /**
   * The upper incomplete gamma function <code>Gamma(a, x)</code> for a complex <code>a</code> and
   * <code>x &gt; 0</code>: from the series of the lower one for <code>x &lt; Re(a) + 1</code>,
   * otherwise from the continued fraction.
   */
  static Complex upperGamma(Complex a, double x) {
    Complex prefactor = new Complex(x).pow(a).multiply(Math.exp(-x));
    if (x < a.getReal() + 1.0) {
      Complex term = Complex.ONE.divide(a);
      Complex sum = term;
      for (int n = 1; n < 2000; n++) {
        term = term.multiply(x).divide(a.add(n));
        sum = sum.add(term);
        if (term.norm() < 1.0e-18 * sum.norm()) {
          break;
        }
      }
      return logGamma(a).exp().subtract(prefactor.multiply(sum));
    }
    // modified Lentz
    final double tiny = 1.0e-300;
    Complex b = new Complex(x + 1.0).subtract(a);
    Complex c = new Complex(1.0 / tiny);
    Complex d = Complex.ONE.divide(b);
    Complex h = d;
    for (int i = 1; i < 2000; i++) {
      Complex an = a.subtract(i).multiply(i);
      b = b.add(2.0);
      d = an.multiply(d).add(b);
      if (d.norm() < tiny) {
        d = new Complex(tiny);
      }
      c = b.add(an.divide(c));
      if (c.norm() < tiny) {
        c = new Complex(tiny);
      }
      d = Complex.ONE.divide(d);
      Complex delta = d.multiply(c);
      h = h.multiply(delta);
      if (delta.subtract(1.0).norm() < 1.0e-16) {
        break;
      }
    }
    return prefactor.multiply(h);
  }

  /**
   * <code>LogGamma(z)</code>: continuous along vertical lines for <code>Re(z) &gt; 0</code>, by the
   * recurrence up to <code>Re(z) &gt;= 15</code> and Stirling's series; by reflection otherwise.
   */
  static Complex logGamma(Complex z) {
    if (z.getReal() <= 0.0) {
      // Gamma(z) Gamma(1-z) = Pi / Sin(Pi z)
      return new Complex(Math.log(Math.PI)).subtract(z.multiply(Math.PI).sin().log())
          .subtract(logGamma(Complex.ONE.subtract(z)));
    }
    Complex shift = Complex.ZERO;
    Complex w = z;
    while (w.getReal() < 15.0) {
      shift = shift.add(w.log());
      w = w.add(1.0);
    }
    Complex inverse = Complex.ONE.divide(w);
    Complex inverse2 = inverse.multiply(inverse);
    Complex series = inverse.multiply(
        Complex.ONE.divide(12.0).subtract(inverse2.multiply(Complex.ONE.divide(360.0)
            .subtract(inverse2.multiply(Complex.ONE.divide(1260.0)
                .subtract(inverse2.multiply(1.0 / 1680.0)))))));
    Complex stirling = w.subtract(0.5).multiply(w.log()).subtract(w)
        .add(0.5 * Math.log(TWO_PI)).add(series);
    return stirling.subtract(shift);
  }

  /** The Ramanujan tau values, from the expansion of <code>q Product((1 - q^n)^24)</code>. */
  private static final class Tau {
    /** <code>VALUES[n]</code> is <code>RamanujanTau(n)</code>. */
    static final long[] VALUES = compute(TAU_TERMS);

    private static long[] compute(int count) {
      // Product(1 - q^n) by Euler's pentagonal number theorem
      BigInteger[] euler = new BigInteger[count];
      java.util.Arrays.fill(euler, BigInteger.ZERO);
      for (int m = 0;; m++) {
        int k1 = m * (3 * m - 1) / 2;
        int k2 = m * (3 * m + 1) / 2;
        if (k1 >= count) {
          break;
        }
        BigInteger sign = m % 2 == 0 ? BigInteger.ONE : BigInteger.ONE.negate();
        euler[k1] = sign;
        if (m > 0 && k2 < count) {
          euler[k2] = sign;
        }
      }
      BigInteger[] power2 = multiply(euler, euler);
      BigInteger[] power4 = multiply(power2, power2);
      BigInteger[] power8 = multiply(power4, power4);
      BigInteger[] power16 = multiply(power8, power8);
      BigInteger[] power24 = multiply(power16, power8);
      long[] tau = new long[count];
      for (int n = 1; n < count; n++) {
        tau[n] = power24[n - 1].longValue();
      }
      return tau;
    }

    private static BigInteger[] multiply(BigInteger[] a, BigInteger[] b) {
      int count = a.length;
      BigInteger[] product = new BigInteger[count];
      java.util.Arrays.fill(product, BigInteger.ZERO);
      for (int i = 0; i < count; i++) {
        if (a[i].signum() == 0) {
          continue;
        }
        for (int j = 0; i + j < count; j++) {
          if (b[j].signum() != 0) {
            product[i + j] = product[i + j].add(a[i].multiply(b[j]));
          }
        }
      }
      return product;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private LFunctions() {}
}

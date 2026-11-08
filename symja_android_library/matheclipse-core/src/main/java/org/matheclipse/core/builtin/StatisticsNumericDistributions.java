package org.matheclipse.core.builtin;

import java.util.Random;
import java.util.function.DoubleUnaryOperator;
import org.hipparchus.special.Beta;
import org.hipparchus.special.Erf;
import org.hipparchus.special.Gamma;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.statistics.ICDF;
import org.matheclipse.core.interfaces.statistics.IContinuousDistribution;
import org.matheclipse.core.interfaces.statistics.IDistribution;
import org.matheclipse.core.interfaces.statistics.IPDF;
import org.matheclipse.core.interfaces.statistics.IRandomVariate;
import org.matheclipse.core.interfaces.statistics.IStatistics;

/**
 * Continuous distributions whose distribution functions are computed numerically
 * (<code>BeckmannDistribution</code>, <code>LandauDistribution</code>,
 * <code>NoncentralBetaDistribution</code>, <code>PearsonDistribution</code>), distributions which
 * are reparametrizations of other distributions (<code>RayleighDistribution</code>,
 * <code>InverseChiSquareDistribution</code>) and the
 * <code>MultivariateHypergeometricDistribution</code>.
 */
public class StatisticsNumericDistributions {

  private static class Initializer {
    private static void init() {
      S.BeckmannDistribution.setEvaluator(new NumericDistribution(Kind.BECKMANN));
      S.LandauDistribution.setEvaluator(new NumericDistribution(Kind.LANDAU));
      S.NoncentralBetaDistribution.setEvaluator(new NumericDistribution(Kind.NONCENTRAL_BETA));
      S.PearsonDistribution.setEvaluator(new NumericDistribution(Kind.PEARSON));
      S.RayleighDistribution.setEvaluator(new RayleighDistribution());
      S.InverseChiSquareDistribution.setEvaluator(new InverseChiSquareDistribution());
      S.MultivariateHypergeometricDistribution
          .setEvaluator(new MultivariateHypergeometricDistribution());
    }
  }

  /**
   * A distribution whose location and scale parameters are quantities of the same unit becomes the
   * <code>QuantityDistribution</code> of the distribution of the magnitudes in that unit.
   *
   * @param ast the distribution
   * @param positions the positions of the location and scale parameters; a parameter may also be a
   *        list of such quantities like the interval of the <code>UniformDistribution</code>
   * @return {@link F#NIL} if the parameters are not quantities of one unit or another parameter is
   *         a quantity
   */
  public static IExpr quantityDistribution(IAST ast, int... positions) {
    IExpr[] unit = new IExpr[1];
    IASTAppendable result = ast.copyAppendable();
    for (int i = 1; i < ast.size(); i++) {
      boolean scaled = false;
      for (int p : positions) {
        if (p == i) {
          scaled = true;
        }
      }
      IExpr arg = ast.get(i);
      if (!scaled) {
        if (!arg.isFree(x -> x.isQuantity(), true)) {
          return F.NIL;
        }
        continue;
      }
      IExpr magnitude = quantityMagnitude(arg, unit);
      if (magnitude.isNIL()) {
        return F.NIL;
      }
      result.set(i, magnitude);
    }
    if (unit[0] == null) {
      return F.NIL;
    }
    return F.binaryAST2(S.QuantityDistribution, result, unit[0]);
  }

  /**
   * The magnitude of the quantity <code>arg</code> (or the list of magnitudes of a list of
   * quantities) whose unit is <code>unit[0]</code>, which is set by the first quantity.
   */
  private static IExpr quantityMagnitude(IExpr arg, IExpr[] unit) {
    if (arg.isList()) {
      return mapQuantities((IAST) arg, unit);
    }
    if (arg.isQuantity()) {
      IAST quantity = (IAST) arg;
      if (unit[0] == null) {
        unit[0] = quantity.arg2();
      } else if (!unit[0].equals(quantity.arg2())) {
        return F.NIL;
      }
      return quantity.arg1();
    }
    return F.NIL;
  }

  private static IExpr mapQuantities(IAST list, IExpr[] unit) {
    IASTAppendable result = F.ListAlloc(list.argSize());
    for (int i = 1; i < list.size(); i++) {
      IExpr magnitude = quantityMagnitude(list.get(i), unit);
      if (magnitude.isNIL()) {
        return F.NIL;
      }
      result.append(magnitude);
    }
    return result;
  }

  /** The distributions with numerically computed distribution functions. */
  private enum Kind {
    BECKMANN, LANDAU, NONCENTRAL_BETA, PEARSON
  }

  /** The machine precision model of a distribution: its support, density and distribution. */
  private abstract static class Model {
    /** the lower end of the support */
    double low = Double.NEGATIVE_INFINITY;
    /** the upper end of the support */
    double high = Double.POSITIVE_INFINITY;

    abstract double pdf(double x);

    abstract double cdf(double x);

    /** The <code>q</code> quantile by bisection of the distribution function. */
    double quantile(double q) {
      double start;
      if (Double.isFinite(low) && Double.isFinite(high)) {
        start = 0.5 * (low + high);
      } else if (Double.isFinite(low)) {
        start = low + 1.0;
      } else if (Double.isFinite(high)) {
        start = high - 1.0;
      } else {
        start = 0.0;
      }
      double a = start;
      double b = start;
      double step = 1.0;
      if (cdf(start) < q) {
        while (cdf(b) < q) {
          a = b;
          b = Double.isFinite(high) ? 0.5 * (b + high) : b + step;
          step *= 2.0;
          if (!Double.isFinite(b) || step > 1e300) {
            return Double.NaN;
          }
        }
      } else {
        while (cdf(a) >= q) {
          b = a;
          a = Double.isFinite(low) ? 0.5 * (low + a) : a - step;
          step *= 2.0;
          if (!Double.isFinite(a) || step > 1e300) {
            return Double.NaN;
          }
        }
      }
      for (int i = 0; i < 200; i++) {
        double middle = 0.5 * (a + b);
        if (middle <= a || middle >= b) {
          break;
        }
        if (cdf(middle) < q) {
          a = middle;
        } else {
          b = middle;
        }
      }
      return b;
    }
  }

  /**
   * <code>BeckmannDistribution(m1, m2, s1, s2)</code> - the length of a vector with independent
   * normally distributed components; <code>LandauDistribution(m, s)</code> - the Landau
   * distribution; <code>NoncentralBetaDistribution(a, b, d)</code> - the noncentral beta
   * distribution; <code>PearsonDistribution(a1, a0, b2, b1, b0)</code> - the Pearson distribution
   * with the density <code>f</code> satisfying <code>f'/f = -(a1*x + a0)/(b2*x^2 + b1*x + b0)</code>,
   * optionally with the type as first argument.
   *
   * <p>
   * The density and the distribution function are computed in machine precision if the point or a
   * parameter is an inexact number and stay unevaluated otherwise. The density of the noncentral
   * beta distribution has a closed form.
   */
  private static final class NumericDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF {

    private final Kind kind;

    NumericDistribution(Kind kind) {
      this.kind = kind;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (kind == Kind.LANDAU && ast.isAST0()) {
        return F.binaryAST2(S.LandauDistribution, F.C0, F.C1);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      switch (kind) {
        case BECKMANN:
          return ARGS_4_4;
        case LANDAU:
          return ARGS_0_2;
        case NONCENTRAL_BETA:
          return ARGS_3_3;
        default:
          return new int[] {5, 6};
      }
    }

    /** Test if the distribution has the number of parameters of its kind. */
    private boolean isComplete(IAST dist) {
      switch (kind) {
        case BECKMANN:
          return dist.argSize() == 4;
        case LANDAU:
          return dist.argSize() == 2;
        case NONCENTRAL_BETA:
          return dist.argSize() == 3;
        default:
          return dist.argSize() == 5 || dist.argSize() == 6;
      }
    }

    /**
     * The machine precision model of the distribution.
     *
     * @return <code>null</code> if a parameter is not a real number or the parameters are not
     *         valid
     */
    private Model model(IAST dist) {
      if (!isComplete(dist)) {
        return null;
      }
      double[] p = new double[dist.argSize()];
      for (int i = 0; i < p.length; i++) {
        IExpr arg = dist.get(i + 1);
        if (!arg.isReal()) {
          return null;
        }
        p[i] = arg.evalf();
        if (!Double.isFinite(p[i])) {
          return null;
        }
      }
      switch (kind) {
        case BECKMANN:
          return p[2] > 0.0 && p[3] > 0.0 ? new Beckmann(p[0], p[1], p[2], p[3]) : null;
        case LANDAU:
          return p[1] > 0.0 ? new Landau(p[0], p[1]) : null;
        case NONCENTRAL_BETA:
          return p[0] > 0.0 && p[1] > 0.0 && p[2] >= 0.0 ? new NoncentralBeta(p[0], p[1], p[2])
              : null;
        default:
          int o = p.length - 5;
          return Pearson.of(p[o], p[o + 1], p[o + 2], p[o + 3], p[o + 4]);
      }
    }

    /** Test if the point or a parameter is an inexact number. */
    private static boolean isInexact(IAST dist, IExpr x) {
      return x.isInexactNumber() || dist.exists(IExpr::isInexactNumber);
    }

    /** Apply <code>function</code> to the real <code>x</code> or to each element of a list. */
    private IExpr numericValue(IAST dist, IExpr x, boolean cdf) {
      if (x.isList()) {
        return ((IAST) x).mapThread(F.binaryAST2(cdf ? S.CDF : S.PDF, dist, F.Slot1), 2);
      }
      if (!x.isReal() || !isInexact(dist, x)) {
        return F.NIL;
      }
      Model model = model(dist);
      if (model == null) {
        return F.NIL;
      }
      double xDouble = x.evalf();
      double value = cdf ? model.cdf(xDouble) : model.pdf(xDouble);
      return Double.isFinite(value) ? F.num(value) : F.NIL;
    }

    @Override
    public IExpr cdf(IAST dist, IExpr x, EvalEngine engine) {
      if (x.isNIL()) {
        return F.NIL;
      }
      return numericValue(dist, x, true);
    }

    @Override
    public IExpr pdf(IAST dist, IExpr x, EvalEngine engine) {
      if (kind == Kind.NONCENTRAL_BETA && dist.argSize() == 3
          && !(x.isPresent() && isInexact(dist, x))) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        IExpr d = dist.arg3();
        // Piecewise({{((1-#)^(b-1)*#^(a-1)*Hypergeometric1F1(a+b,a,(d*#)/2))/(E^(d/2)*Beta(a,b)),
        // 0<#<1}}, 0) &
        return callFunction(F.Function(F.Piecewise(F.list(F.list(F.Times(
            F.Power(F.Subtract(F.C1, F.Slot1), F.Plus(F.CN1, b)),
            F.Power(F.Slot1, F.Plus(F.CN1, a)),
            F.Hypergeometric1F1(F.Plus(a, b), a, F.Times(F.C1D2, d, F.Slot1)),
            F.Power(F.Times(F.Exp(F.Times(F.C1D2, d)), F.Beta(a, b)), F.CN1)),
            F.Less(F.C0, F.Slot1, F.C1))), F.C0)), x);
      }
      if (x.isNIL()) {
        return F.NIL;
      }
      return numericValue(dist, x, false);
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr q, EvalEngine engine) {
      if (q.isList()) {
        return ((IAST) q).mapThread(F.binaryAST2(S.InverseCDF, dist, F.Slot1), 2);
      }
      if (!q.isReal()) {
        return F.NIL;
      }
      Model model = model(dist);
      if (model == null) {
        return F.NIL;
      }
      double p = q.evalf();
      if (p == 0.0) {
        return bound(model.low);
      }
      if (p == 1.0) {
        return bound(model.high);
      }
      if (p < 0.0 || p > 1.0 || !isInexact(dist, q)) {
        return F.NIL;
      }
      double value = model.quantile(p);
      return Double.isFinite(value) ? F.num(value) : F.NIL;
    }

    /** An end of the support: an integer if it is one, otherwise a machine number. */
    private static IExpr bound(double b) {
      if (b == Double.POSITIVE_INFINITY) {
        return F.CInfinity;
      }
      if (b == Double.NEGATIVE_INFINITY) {
        return F.CNInfinity;
      }
      if (b == Math.rint(b) && Math.abs(b) < 1e15) {
        return F.ZZ((long) b);
      }
      return F.num(b);
    }

    @Override
    public IExpr mean(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  // ---------------------------------------------------------------------------------------------
  // Quadrature

  private static final double[] GK_X = {0.9914553711208126, 0.9491079123427585,
      0.8648644233597691, 0.7415311855993944, 0.5860872354676911, 0.4058451513773972,
      0.2077849550078985, 0.0};
  private static final double[] GK_WK = {0.02293532201052922, 0.06309209262997855,
      0.10479001032225018, 0.14065325971552592, 0.1690047266392679, 0.1903505780647854,
      0.2044329400752989, 0.20948214108472783};
  private static final double[] GK_WG =
      {0.1294849661688697, 0.2797053914892767, 0.3818300505051189, 0.4179591836734694};

  /**
   * The 15 point Kronrod estimate on <code>[a, b]</code>, its difference from the embedded 7 point
   * Gauss rule and the Kronrod estimate of the integral of <code>|f|</code>.
   */
  private static double[] gk15(DoubleUnaryOperator f, double a, double b) {
    double c = 0.5 * (a + b);
    double h = 0.5 * (b - a);
    double fc = finite(f.applyAsDouble(c));
    double k = GK_WK[7] * fc;
    double g = GK_WG[3] * fc;
    double abs = GK_WK[7] * Math.abs(fc);
    for (int i = 0; i < 7; i++) {
      double d = h * GK_X[i];
      double f1 = finite(f.applyAsDouble(c - d));
      double f2 = finite(f.applyAsDouble(c + d));
      k += GK_WK[i] * (f1 + f2);
      abs += GK_WK[i] * (Math.abs(f1) + Math.abs(f2));
      if (i % 2 == 1) {
        g += GK_WG[i / 2] * (f1 + f2);
      }
    }
    return new double[] {k * h, Math.abs((k - g) * h), abs * Math.abs(h)};
  }

  private static double finite(double v) {
    return Double.isFinite(v) ? v : 0.0;
  }

  /**
   * Globally adaptive Gauss-Kronrod quadrature of <code>f</code> over the finite
   * <code>[a, b]</code>, which is first split into <code>pieces</code> intervals, to a relative
   * tolerance on the integral of <code>|f|</code>.
   */
  private static double adaptive(DoubleUnaryOperator f, double a, double b, int pieces) {
    if (a == b) {
      return 0.0;
    }
    java.util.ArrayList<double[]> intervals = new java.util.ArrayList<double[]>();
    int n = Math.max(pieces, 1);
    for (int i = 0; i < n; i++) {
      double lo = a + (b - a) * i / n;
      double hi = i + 1 == n ? b : a + (b - a) * (i + 1) / n;
      double[] r = gk15(f, lo, hi);
      intervals.add(new double[] {lo, hi, r[0], r[1], r[2]});
    }
    EvalEngine engine = EvalEngine.get();
    for (int iteration = 0; iteration < 4000; iteration++) {
      if ((iteration & 0x3F) == 0) {
        engine.checkTimeLimit();
      }
      double total = 0.0;
      double abs = 0.0;
      double error = 0.0;
      int worst = 0;
      for (int i = 0; i < intervals.size(); i++) {
        double[] interval = intervals.get(i);
        total += interval[2];
        error += interval[3];
        abs += interval[4];
        if (interval[3] > intervals.get(worst)[3]) {
          worst = i;
        }
      }
      if (error <= 1e-14 * Math.max(Math.abs(total), 1e-3 * abs) || abs == 0.0) {
        break;
      }
      double[] interval = intervals.get(worst);
      double lo = interval[0];
      double hi = interval[1];
      double middle = 0.5 * (lo + hi);
      if (middle <= lo || middle >= hi) {
        break;
      }
      double[] r1 = gk15(f, lo, middle);
      double[] r2 = gk15(f, middle, hi);
      intervals.set(worst, new double[] {lo, middle, r1[0], r1[1], r1[2]});
      intervals.add(new double[] {middle, hi, r2[0], r2[1], r2[2]});
    }
    // compensated summation
    double sum = 0.0;
    double compensation = 0.0;
    for (double[] interval : intervals) {
      double v = interval[2];
      double t = sum + v;
      compensation += Math.abs(sum) >= Math.abs(v) ? (sum - t) + v : (v - t) + sum;
      sum = t;
    }
    return sum + compensation;
  }

  /**
   * The integral of <code>exp(logf(x))</code> over <code>[a, b]</code>, where an end may be
   * infinite and the density may be singular at an end, by a double exponential change of the
   * variable and adaptive quadrature. <code>scale</code> is the width of the peak at the finite end
   * of a half infinite interval.
   */
  private static double deIntegral(DoubleUnaryOperator logf, double a, double b, double scale) {
    final double halfPi = Math.PI / 2.0;
    if (a >= b) {
      return 0.0;
    }
    boolean finiteA = Double.isFinite(a);
    boolean finiteB = Double.isFinite(b);
    if (finiteA && finiteB) {
      // tanh-sinh: each node is placed by its distance from the nearer end
      double half = 0.5 * (b - a);
      return adaptive(t -> {
        double s = halfPi * Math.sinh(t);
        double e = Math.exp(-2.0 * Math.abs(s));
        double distance = 2.0 * e / (1.0 + e);
        double x = t >= 0.0 ? b - half * distance : a + half * distance;
        double logWeight = Math.log(half * halfPi * Math.cosh(t) * 4.0 * e / ((1.0 + e) * (1.0 + e)));
        return Math.exp(logf.applyAsDouble(x) + logWeight);
      }, -4.5, 4.5, 16);
    }
    if (finiteA) {
      return adaptive(t -> {
        double s = halfPi * Math.sinh(t);
        double x = a + scale * Math.exp(s);
        return Math
            .exp(logf.applyAsDouble(x) + Math.log(scale) + s + Math.log(halfPi * Math.cosh(t)));
      }, -6.5, 6.5, 24);
    }
    if (finiteB) {
      return adaptive(t -> {
        double s = halfPi * Math.sinh(t);
        double x = b - scale * Math.exp(s);
        return Math
            .exp(logf.applyAsDouble(x) + Math.log(scale) + s + Math.log(halfPi * Math.cosh(t)));
      }, -6.5, 6.5, 24);
    }
    return deIntegral(logf, Double.NEGATIVE_INFINITY, 0.0, scale)
        + deIntegral(logf, 0.0, Double.POSITIVE_INFINITY, scale);
  }

  // ---------------------------------------------------------------------------------------------
  // The models

  /** The length of a vector with the independent components N(m1, s1) and N(m2, s2). */
  private static final class Beckmann extends Model {
    final double m1, m2, s1, s2;

    Beckmann(double m1, double m2, double s1, double s2) {
      this.m1 = m1;
      this.m2 = m2;
      this.s1 = s1;
      this.s2 = s2;
      this.low = 0.0;
    }

    @Override
    double pdf(double r) {
      if (r <= 0.0) {
        return 0.0;
      }
      DoubleUnaryOperator exponent = theta -> {
        double u = (r * Math.cos(theta) - m1) / s1;
        double v = (r * Math.sin(theta) - m2) / s2;
        return -0.5 * (u * u + v * v);
      };
      // the largest exponent is factored out, so that a far tail keeps its relative accuracy
      double peak = Double.NEGATIVE_INFINITY;
      for (int k = 0; k < 256; k++) {
        peak = Math.max(peak, exponent.applyAsDouble(2.0 * Math.PI * k / 256.0));
      }
      final double max = peak;
      double integral = adaptive(theta -> Math.exp(exponent.applyAsDouble(theta) - max), 0.0,
          2.0 * Math.PI, 8);
      return r / (2.0 * Math.PI * s1 * s2) * integral * Math.exp(max);
    }

    @Override
    double cdf(double x) {
      if (x <= 0.0) {
        return 0.0;
      }
      // P(X^2 + Y^2 <= x^2) = Integrate(pdfX(u) * P(|Y| <= Sqrt(x^2 - u^2)), {u, -x, x})
      DoubleUnaryOperator logIntegrand = u -> {
        double w = Math.sqrt(Math.max((x - u) * (x + u), 0.0));
        double mass = normalMass((-w - m2) / s2, (w - m2) / s2);
        double z = (u - m1) / s1;
        return -0.5 * z * z - Math.log(s1 * Math.sqrt(2.0 * Math.PI)) + Math.log(mass);
      };
      return Math.min(deIntegral(logIntegrand, -x, x, 1.0), 1.0);
    }

    /** <code>Phi(hi) - Phi(lo)</code> of the standard normal distribution without cancellation. */
    private static double normalMass(double lo, double hi) {
      double c = 1.0 / Math.sqrt(2.0);
      if (hi <= 0.0) {
        return 0.5 * (Erf.erfc(-hi * c) - Erf.erfc(-lo * c));
      }
      if (lo >= 0.0) {
        return 0.5 * (Erf.erfc(lo * c) - Erf.erfc(hi * c));
      }
      return 1.0 - 0.5 * (Erf.erfc(hi * c) + Erf.erfc(-lo * c));
    }
  }

  /**
   * <code>LandauDistribution(m, s)</code> has the characteristic function
   * <code>Exp(I*t*(m + 2*s*Log(s)/Pi) - Abs(s*t)*(1 + 2*I*Sign(t)*Log(Abs(s*t))/Pi))</code>, so it
   * is <code>m + 2*s*Log(s)/Pi + s*Y</code> for the standard variable <code>Y</code> with the
   * characteristic function <code>Exp(-Abs(t) - 2*I*t*Log(Abs(t))/Pi)</code>.
   */
  private static final class Landau extends Model {
    final double m, s;

    Landau(double m, double s) {
      this.m = m;
      this.s = s;
    }

    private double standardize(double x) {
      return (x - m - 2.0 * s * Math.log(s) / Math.PI) / s;
    }

    /** <code>z</code> with <code>Y = (2/Pi)*(z - Log(Pi/2))</code>. */
    private static double z(double y) {
      return Math.PI * y / 2.0 + Math.log(Math.PI / 2.0);
    }

    /** Where <code>Exp(-t*Log(t) - z*t)</code> has fallen below <code>1e-300</code>. */
    private static double upperLimit(double z) {
      double t = 1.0;
      while (t * Math.log(t) + z * t < 700.0 && t < 1e6) {
        t *= 1.5;
      }
      return t;
    }

    @Override
    double pdf(double x) {
      double y = standardize(x);
      double z = z(y);
      double value;
      if (z >= -1.0) {
        value = 0.5 * adaptive(
            t -> t == 0.0 ? 0.0 : Math.exp(-t * Math.log(t) - z * t) * Math.sin(Math.PI * t), 0.0,
            upperLimit(z), 32);
      } else {
        value = adaptive(t -> t == 0.0 ? 1.0
            : Math.exp(-t) * Math.cos(t * y + 2.0 / Math.PI * t * Math.log(t)), 0.0, 50.0, 64)
            / Math.PI;
      }
      return value / s;
    }

    @Override
    double cdf(double x) {
      double y = standardize(x);
      double z = z(y);
      if (z >= -1.0) {
        // the upper tail
        return 1.0 - adaptive(t -> t == 0.0 ? Math.PI
            : Math.exp(-t * Math.log(t) - z * t) * Math.sin(Math.PI * t) / t, 0.0, upperLimit(z),
            32) / Math.PI;
      }
      // Gil-Pelaez inversion
      return Math.max(0.0, 0.5 + adaptive(t -> t == 0.0 ? y
          : Math.exp(-t) * Math.sin(t * y + 2.0 / Math.PI * t * Math.log(t)) / t, 0.0, 50.0, 64)
          / Math.PI);
    }
  }

  /** A Poisson mixture with the mean <code>d/2</code> of the beta distributions (a + j, b). */
  private static final class NoncentralBeta extends Model {
    final double a, b, d;

    NoncentralBeta(double a, double b, double d) {
      this.a = a;
      this.b = b;
      this.d = d;
      this.low = 0.0;
      this.high = 1.0;
    }

    @Override
    double pdf(double x) {
      if (x <= 0.0 || x >= 1.0) {
        return 0.0;
      }
      // the Poisson mixture of the beta densities
      double lambda = d / 2.0;
      double sum = 0.0;
      double weightTotal = 0.0;
      for (int j = 0; j <= 100_000; j++) {
        double w = poissonWeight(lambda, j);
        double logBeta = Gamma.logGamma(a + j) + Gamma.logGamma(b) - Gamma.logGamma(a + j + b);
        sum += w * Math.exp((a + j - 1.0) * Math.log(x) + (b - 1.0) * Math.log1p(-x) - logBeta);
        weightTotal += w;
        if (lambda == 0.0 || (j > lambda && 1.0 - weightTotal < 1e-17)) {
          break;
        }
      }
      return sum;
    }

    @Override
    double cdf(double x) {
      if (x <= 0.0) {
        return 0.0;
      }
      if (x >= 1.0) {
        return 1.0;
      }
      double lambda = d / 2.0;
      double sum = 0.0;
      double weightTotal = 0.0;
      for (int j = 0; j <= 100_000; j++) {
        double w = poissonWeight(lambda, j);
        sum += w * Beta.regularizedBeta(x, a + j, b);
        weightTotal += w;
        if ((j > lambda && w < 1e-17 * Math.max(sum, 1e-300)) || lambda == 0.0
            || (j > lambda && 1.0 - weightTotal < 1e-17)) {
          break;
        }
      }
      return Math.min(sum, 1.0);
    }

    private static double poissonWeight(double lambda, int j) {
      if (lambda == 0.0) {
        return j == 0 ? 1.0 : 0.0;
      }
      return Math.exp(-lambda + j * Math.log(lambda) - Gamma.logGamma(j + 1.0));
    }
  }

  /**
   * A Pearson density <code>Exp(-G(x))</code> with <code>G' = (a1*x + a0)/Q(x)</code> and
   * <code>Q(x) = b2*x^2 + b1*x + b0</code> on the interval of its support.
   */
  private static final class Pearson extends Model {
    /** the shapes of <code>Q</code> */
    private static final int CONSTANT = 0, LINEAR = 1, NO_REAL_ROOTS = 2, DOUBLE_ROOT = 3,
        TWO_ROOTS = 4;

    final double a1, a0, b2, b1, b0;
    int shape;
    /** a root, the roots or the negative discriminant of <code>Q</code> */
    double r1, r2, disc;
    /** the coefficients of the logarithms for two roots */
    double ca, cb;
    /** where the density peaks (moved into the support), the width of the peak */
    double mode, scale = 1.0;
    /** <code>G(mode)</code>, which is subtracted in each evaluation */
    double gMode;
    /** the integral of the unnormalized density */
    double norm = 1.0;

    private Pearson(double a1, double a0, double b2, double b1, double b0) {
      this.a1 = a1;
      this.a0 = a0;
      this.b2 = b2;
      this.b1 = b1;
      this.b0 = b0;
    }

    /** @return <code>null</code> if the parameters don't define a distribution */
    static Pearson of(double a1, double a0, double b2, double b1, double b0) {
      Pearson p = new Pearson(a1, a0, b2, b1, b0);
      final double inf = Double.POSITIVE_INFINITY;
      if (b2 == 0.0 && b1 == 0.0) {
        if (b0 == 0.0 || a1 / b0 <= 0.0) {
          return null;
        }
        p.shape = CONSTANT;
        p.low = -inf;
        p.high = inf;
      } else if (b2 == 0.0) {
        double r = -b0 / b1;
        // the density decays on the side a1/b1 points to and must be integrable at r
        if (a1 == 0.0 || (a1 * r + a0) / b1 >= 1.0) {
          return null;
        }
        p.shape = LINEAR;
        p.r1 = r;
        if (a1 / b1 > 0.0) {
          p.low = r;
          p.high = inf;
        } else {
          p.low = -inf;
          p.high = r;
        }
      } else {
        double discriminant = b1 * b1 - 4.0 * b2 * b0;
        if (discriminant < 0.0) {
          if (a1 / b2 <= 1.0) {
            return null;
          }
          p.shape = NO_REAL_ROOTS;
          p.disc = -discriminant;
          p.low = -inf;
          p.high = inf;
        } else if (discriminant == 0.0) {
          double r = -b1 / (2.0 * b2);
          if (a1 / b2 <= 1.0) {
            return null;
          }
          double c = (a1 * r + a0) / b2;
          p.shape = DOUBLE_ROOT;
          p.r1 = r;
          if (c < 0.0) {
            p.low = r;
            p.high = inf;
          } else if (c > 0.0) {
            p.low = -inf;
            p.high = r;
          } else {
            return null;
          }
        } else {
          double sq = Math.sqrt(discriminant);
          double root1 = (-b1 - sq) / (2.0 * b2);
          double root2 = (-b1 + sq) / (2.0 * b2);
          p.r1 = Math.min(root1, root2);
          p.r2 = Math.max(root1, root2);
          p.ca = (a1 * p.r1 + a0) / (b2 * (p.r1 - p.r2));
          p.cb = (a1 * p.r2 + a0) / (b2 * (p.r2 - p.r1));
          p.shape = TWO_ROOTS;
          if (p.ca < 1.0 && p.cb < 1.0) {
            p.low = p.r1;
            p.high = p.r2;
          } else if (p.cb < 1.0 && p.ca + p.cb > 1.0) {
            p.low = p.r2;
            p.high = inf;
          } else if (p.ca < 1.0 && p.ca + p.cb > 1.0) {
            p.low = -inf;
            p.high = p.r1;
          } else {
            return null;
          }
        }
      }
      // the density peaks where a1*x + a0 == 0; move that into the support
      double mode = -a0 / a1;
      if (!(mode > p.low && mode < p.high)) {
        if (Double.isFinite(p.low) && Double.isFinite(p.high)) {
          mode = 0.5 * (p.low + p.high);
        } else if (Double.isFinite(p.low)) {
          mode = p.low + 1.0;
        } else {
          mode = p.high - 1.0;
        }
      }
      double q = b2 * mode * mode + b1 * mode + b0;
      double scale = Math.sqrt(Math.abs(q) / Math.abs(a1));
      p.mode = mode;
      p.scale = Double.isFinite(scale) && scale > 0.0 ? scale : 1.0;
      p.gMode = p.g(mode);
      double norm = p.integral(p.low, mode) + p.integral(mode, p.high);
      if (!(Double.isFinite(norm) && norm > 0.0)) {
        return null;
      }
      p.norm = norm;
      return p;
    }

    /** <code>G(x)</code>, an antiderivative of <code>(a1*x + a0)/Q(x)</code>. */
    double g(double x) {
      switch (shape) {
        case CONSTANT:
          return (a1 * x * x / 2.0 + a0 * x) / b0;
        case LINEAR:
          return (a1 / b1) * x + ((a1 * r1 + a0) / b1) * Math.log(Math.abs(x - r1));
        case NO_REAL_ROOTS: {
          double logQ;
          if (Math.abs(x) > 1e100) {
            logQ = Math.log(Math.abs(b2)) + 2.0 * Math.log(Math.abs(x))
                + Math.log(Math.abs(1.0 + b1 / (b2 * x) + b0 / (b2 * x * x)));
          } else {
            logQ = Math.log(Math.abs(b2 * x * x + b1 * x + b0));
          }
          double sq = Math.sqrt(disc);
          return (a1 / (2.0 * b2)) * logQ
              + (a0 - a1 * b1 / (2.0 * b2)) * (2.0 / sq) * Math.atan((2.0 * b2 * x + b1) / sq);
        }
        case DOUBLE_ROOT:
          return (a1 / b2) * Math.log(Math.abs(x - r1)) - (a1 * r1 + a0) / (b2 * (x - r1));
        default:
          return ca * Math.log(Math.abs(x - r1)) + cb * Math.log(Math.abs(x - r2));
      }
    }

    double logDensity(double x) {
      if (x <= low || x >= high) {
        return Double.NEGATIVE_INFINITY;
      }
      return gMode - g(x);
    }

    /** The unnormalized mass on <code>[a, b]</code>. */
    double integral(double a, double b) {
      return deIntegral(this::logDensity, a, b, scale);
    }

    @Override
    double pdf(double x) {
      return Math.exp(logDensity(x)) / norm;
    }

    @Override
    double cdf(double x) {
      if (x <= low) {
        return 0.0;
      }
      if (x >= high) {
        return 1.0;
      }
      if (x <= mode) {
        return Math.min(integral(low, x) / norm, 1.0);
      }
      return Math.max(1.0 - integral(x, high) / norm, 0.0);
    }
  }

  // ---------------------------------------------------------------------------------------------

  /**
   * <code>RayleighDistribution(s)</code> - the Rayleigh distribution, the length of a vector with
   * two independent components which are normally distributed with mean <code>0</code> and
   * standard deviation <code>s</code>.
   */
  private static final class RayleighDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics, IRandomVariate {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return quantityDistribution(ast, 1);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr cdf(IAST dist, IExpr x, EvalEngine engine) {
      if (!dist.isAST1()) {
        return F.NIL;
      }
      IExpr s = dist.arg1();
      // Piecewise({{1 - E^(-#^2/(2*s^2)), # > 0}}, 0) &
      return callFunction(F.Function(F.Piecewise(F.list(F.list(
          F.Subtract(F.C1, F.Exp(F.Times(F.CN1D2, F.Sqr(F.Slot1), F.Power(s, F.CN2)))),
          F.Greater(F.Slot1, F.C0))), F.C0)), x);
    }

    @Override
    public IExpr pdf(IAST dist, IExpr x, EvalEngine engine) {
      if (!dist.isAST1()) {
        return F.NIL;
      }
      IExpr s = dist.arg1();
      // Piecewise({{#/(E^(#^2/(2*s^2))*s^2), # > 0}}, 0) &
      return callFunction(F.Function(F.Piecewise(F.list(F.list(
          F.Times(F.Slot1, F.Power(s, F.CN2),
              F.Exp(F.Times(F.CN1D2, F.Sqr(F.Slot1), F.Power(s, F.CN2)))),
          F.Greater(F.Slot1, F.C0))), F.C0)), x);
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr q, EvalEngine engine) {
      if (!dist.isAST1()) {
        return F.NIL;
      }
      IExpr s = dist.arg1();
      // ConditionalExpression(Piecewise({{s*Sqrt(Log(1/(1-#)^2)), 0 < # < 1}, {0, # <= 0}},
      // Infinity), 0 <= # <= 1) &
      return callFunction(F.Function(F.ConditionalExpression(F.Piecewise(F.list(
          F.list(F.Times(s, F.Sqrt(F.Log(F.Power(F.Subtract(F.C1, F.Slot1), F.CN2)))),
              F.Less(F.C0, F.Slot1, F.C1)),
          F.list(F.C0, F.LessEqual(F.Slot1, F.C0))), F.CInfinity),
          F.LessEqual(F.C0, F.Slot1, F.C1))), q);
    }

    @Override
    public IExpr mean(IAST dist) {
      // Sqrt(Pi/2)*s
      return dist.isAST1() ? F.Times(F.Sqrt(F.Times(F.C1D2, S.Pi)), dist.arg1()) : F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      // s*Sqrt(Log(4))
      return dist.isAST1() ? F.Times(dist.arg1(), F.Sqrt(F.Log(F.C4))) : F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      // (2 - Pi/2)*s^2
      return dist.isAST1() ? F.Times(F.Subtract(F.C2, F.Times(F.C1D2, S.Pi)), F.Sqr(dist.arg1()))
          : F.NIL;
    }

    @Override
    public IExpr skewness(IAST dist) {
      // (2*(-3 + Pi)*Sqrt(Pi))/(4 - Pi)^(3/2)
      return dist.isAST1() ? F.Times(F.C2, F.Plus(F.CN3, S.Pi), F.Sqrt(S.Pi),
          F.Power(F.Subtract(F.C4, S.Pi), F.QQ(-3, 2))) : F.NIL;
    }

    @Override
    public IExpr parameterAssumptions(IAST dist) {
      return dist.isAST1() ? F.Greater(dist.arg1(), F.C0) : F.NIL;
    }

    @Override
    public IExpr randomVariate(Random random, IAST dist, int size) {
      if (!dist.isAST1()) {
        return F.NIL;
      }
      double s = dist.arg1().evalfNaN();
      if (!(s > 0.0)) {
        return F.NIL;
      }
      double[] vector = new double[size];
      for (int i = 0; i < size; i++) {
        vector[i] = s * Math.sqrt(-2.0 * Math.log1p(-random.nextDouble()));
      }
      return new ASTRealVector(vector, false);
    }
  }

  /**
   * <code>InverseChiSquareDistribution(n)</code> and the scaled
   * <code>InverseChiSquareDistribution(n, xi)</code> - the distribution of <code>1/X</code>
   * respectively <code>n*xi/X</code> for a <code>ChiSquareDistribution(n)</code> distributed
   * <code>X</code>, which are the inverse gamma distributions with the shape <code>n/2</code> and
   * the scales <code>1/2</code> respectively <code>n*xi/2</code>.
   */
  private static final class InverseChiSquareDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics, IRandomVariate {

    /** The equal inverse gamma distribution. */
    private static IAST inverseGamma(IAST dist) {
      IExpr n = dist.arg1();
      if (dist.isAST1()) {
        return F.binaryAST2(S.InverseGammaDistribution, F.Times(F.C1D2, n), F.C1D2);
      }
      return F.binaryAST2(S.InverseGammaDistribution, F.Times(F.C1D2, n),
          F.Times(F.C1D2, n, dist.arg2()));
    }

    /** Evaluate <code>function(InverseGammaDistribution(...), args)</code>. */
    private static IExpr delegate(IBuiltInSymbol function, IAST dist, IExpr... args) {
      if (!dist.isAST1() && !dist.isAST2()) {
        return F.NIL;
      }
      IASTAppendable ast = F.ast(function, 1 + args.length);
      ast.append(inverseGamma(dist));
      for (IExpr arg : args) {
        ast.append(arg);
      }
      IExpr result = EvalEngine.get().evaluate(ast);
      return result.isAST(function) ? F.NIL : result;
    }

    /** Bring the values of a <code>Piecewise</code> moment to a common denominator. */
    private static IExpr together(IExpr moment) {
      if (moment.isAST(S.Piecewise, 3) && moment.first().isList()) {
        IAST rows = ((IAST) moment.first())
            .map(row -> row.isList2() ? F.list(F.Together(row.first()), row.second()) : row);
        return EvalEngine.get().evaluate(F.Piecewise(rows, moment.second()));
      }
      return moment.isPresent() ? EvalEngine.get().evaluate(F.Together(moment)) : F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr cdf(IAST dist, IExpr x, EvalEngine engine) {
      return x.isNIL() ? delegate(S.CDF, dist) : delegate(S.CDF, dist, x);
    }

    @Override
    public IExpr pdf(IAST dist, IExpr x, EvalEngine engine) {
      return x.isNIL() ? delegate(S.PDF, dist) : delegate(S.PDF, dist, x);
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr q, EvalEngine engine) {
      return q.isNIL() ? delegate(S.InverseCDF, dist) : delegate(S.InverseCDF, dist, q);
    }

    @Override
    public IExpr mean(IAST dist) {
      return together(delegate(S.Mean, dist));
    }

    @Override
    public IExpr median(IAST dist) {
      return delegate(S.Median, dist);
    }

    @Override
    public IExpr variance(IAST dist) {
      return together(delegate(S.Variance, dist));
    }

    @Override
    public IExpr skewness(IAST dist) {
      return together(delegate(S.Skewness, dist));
    }

    @Override
    public IExpr parameterAssumptions(IAST dist) {
      if (dist.isAST1()) {
        return F.Greater(dist.arg1(), F.C0);
      }
      return dist.isAST2() ? F.And(F.Greater(dist.arg1(), F.C0), F.Greater(dist.arg2(), F.C0))
          : F.NIL;
    }

    @Override
    public IExpr randomVariate(Random random, IAST dist, int size) {
      if (!dist.isAST1() && !dist.isAST2()) {
        return F.NIL;
      }
      IAST gamma = inverseGamma(dist);
      IExpr distribution = EvalEngine.get().evaluate(gamma);
      if (!distribution.isAST(S.InverseGammaDistribution)) {
        return F.NIL;
      }
      IRandomVariate variate = ((IAST) distribution).headInstanceOf(IRandomVariate.class);
      return variate == null ? F.NIL : variate.randomVariate(random, (IAST) distribution, size);
    }
  }

  /**
   * <code>MultivariateHypergeometricDistribution(n, {m1, m2, ...})</code> - the numbers of the
   * objects of each of the classes with <code>m1, m2, ...</code> objects among <code>n</code>
   * objects drawn without replacement.
   */
  private static final class MultivariateHypergeometricDistribution extends AbstractEvaluator
      implements IDistribution, IPDF, IStatistics {

    private static boolean isValid(IAST dist) {
      return dist.isAST2() && dist.arg2().isList() && dist.arg2().argSize() > 0;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr mean(IAST dist) {
      if (!isValid(dist)) {
        return F.NIL;
      }
      IAST m = (IAST) dist.arg2();
      IExpr total = m.apply(S.Plus);
      // n*m_i/Total(m)
      return m.map(mi -> F.Times(dist.arg1(), mi, F.Power(total, F.CN1)));
    }

    @Override
    public IExpr median(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      if (!isValid(dist)) {
        return F.NIL;
      }
      IAST m = (IAST) dist.arg2();
      IExpr n = dist.arg1();
      IExpr total = m.apply(S.Plus);
      // n*m_i*(M - m_i)*(M - n)/(M^2*(M - 1))
      return m.map(mi -> F.Times(n, mi, F.Subtract(total, mi), F.Subtract(total, n),
          F.Power(F.Times(F.Sqr(total), F.Plus(F.CN1, total)), F.CN1)));
    }

    @Override
    public IExpr skewness(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr x, EvalEngine engine) {
      if (!isValid(dist) || !x.isList() || x.argSize() != dist.arg2().argSize()) {
        return F.NIL;
      }
      IAST m = (IAST) dist.arg2();
      IAST k = (IAST) x;
      if (!k.forAll(IExpr::isInteger) || !m.forAll(IExpr::isInteger)
          || !dist.arg1().isInteger()) {
        return F.NIL;
      }
      IASTAppendable product = F.TimesAlloc(m.argSize() + 1);
      for (int i = 1; i < m.size(); i++) {
        product.append(F.Binomial(m.get(i), k.get(i)));
      }
      product.append(F.Power(F.Binomial(m.apply(S.Plus), dist.arg1()), F.CN1));
      // the numbers must add up to n
      return F.If(F.Equal(k.apply(S.Plus), dist.arg1()), product, F.C0);
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private StatisticsNumericDistributions() {}
}

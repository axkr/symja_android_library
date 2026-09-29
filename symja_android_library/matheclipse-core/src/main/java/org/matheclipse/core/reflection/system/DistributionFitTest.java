package org.matheclipse.core.reflection.system;

import java.util.Arrays;
import org.hipparchus.special.Gamma;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>DistributionFitTest(data, dist)</code> - goodness-of-fit tests of <code>data</code> against
 * the fully specified continuous distribution <code>dist</code>.
 *
 * <p>
 * Every test reads the data through the distribution's own <code>CDF</code>: under the hypothesis
 * the values <code>u_i = CDF(dist, x_i)</code> are uniform on <code>[0, 1]</code>, and each test
 * measures a different distance of their empirical distribution from the uniform one:
 * <ul>
 * <li>Anderson-Darling <code>A^2</code>, p-value by Marsaglia and Marsaglia (2004),</li>
 * <li>Cramér-von Mises <code>W^2</code>, p-value from the limiting distribution of Anderson and
 * Darling (1952),</li>
 * <li>Kolmogorov-Smirnov <code>D</code>, the exact p-value for small samples,</li>
 * <li>Kuiper <code>V = D+ + D-</code>, p-value by Stephens' modified asymptotic series,</li>
 * <li>Pearson <code>chi^2</code> over equiprobable bins,</li>
 * <li>Watson <code>U^2</code>, p-value by Stephens' modified asymptotic series.</li>
 * </ul>
 *
 * <p>
 * <code>DistributionFitTest(data, dist, "property")</code> gives one property of the automatic
 * test, a test's name gives that test's p-value, and <code>"HypothesisTestData"</code> gives all of
 * them as a {@link HypothesisTestData} object.
 */
public class DistributionFitTest extends AbstractFunctionEvaluator {

  /** The tests, in the order <code>"AllTests"</code> lists them. */
  static final String[] TESTS = {"AndersonDarling", "CramerVonMises", "KolmogorovSmirnov", "Kuiper",
      "PearsonChiSquare", "WatsonUSquare"};

  /** The test a property without a test name refers to. */
  static final String AUTOMATIC_TEST = "KolmogorovSmirnov";

  /** The smallest sample the Cramér-von Mises test is valid for. */
  private static final int CRAMER_VON_MISES_MINIMUM = 7;

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr data = engine.evaluate(ast.arg1());
    double[] values = data.isList() ? data.toDoubleVector() : null;
    if (values == null || values.length == 0) {
      return F.NIL;
    }
    double[] u = cdfValues(values, ast.arg2(), engine);
    if (u == null) {
      return F.NIL;
    }
    IAST object = hypothesisTestData(u, ast.arg2());
    if (u.length < CRAMER_VON_MISES_MINIMUM
        && (ast.isAST3() && ast.arg3().isFree(x -> x.isString("CramerVonMises"), false) == false)) {
      // The `1` test is only valid for sample sizes between `2` and `3`.
      Errors.printMessage(S.DistributionFitTest, "htdrng",
          F.List(F.stringx("CramerVonMises"), F.ZZ(CRAMER_VON_MISES_MINIMUM), F.CInfinity), engine);
    }
    if (ast.isAST2()) {
      return HypothesisTestData.property(object, F.stringx("PValue"), F.NIL);
    }
    IExpr property = ast.arg3();
    if (property.isString("HypothesisTestData")) {
      return object;
    }
    if (property.isString() && Arrays.asList(TESTS).contains(property.toString())) {
      // a test's name is that test's p-value
      return HypothesisTestData.property(object, F.stringx("PValue"), property);
    }
    if (property.isList()) {
      // a list of properties, a test's name among them standing for its p-value:
      // {"Kuiper", "TestData"} is {Kuiper p-value, TestData of the automatic test}
      for (IExpr p : (IAST) property) {
        if (!isTest(p) && !HypothesisTestData.isProperty(p)) {
          return invalidProperty(p, engine);
        }
      }
      return ((IAST) property).map(p -> isTest(p)
          ? HypothesisTestData.property(object, F.stringx("PValue"), p)
          : HypothesisTestData.property(object, p, F.NIL), 1);
    }
    if (!HypothesisTestData.isProperty(property)) {
      return invalidProperty(property, engine);
    }
    return HypothesisTestData.property(object, property, F.NIL);
  }

  private static boolean isTest(IExpr name) {
    return name.isString() && Arrays.asList(TESTS).contains(name.toString());
  }

  /** Mathematica reports an unknown property and leaves the call unevaluated. */
  private static IExpr invalidProperty(IExpr property, EvalEngine engine) {
    // The argument `1` is not a valid property. Specify "Properties" to obtain a list of valid
    // properties.
    Errors.printMessage(S.DistributionFitTest, "invprp", F.list(property), engine);
    return F.NIL;
  }

  /**
   * The sorted values <code>CDF(dist, x_i)</code>, or <code>null</code> when the distribution has
   * no numeric CDF at one of the data points.
   */
  public static double[] cdfValues(double[] data, IExpr distribution, EvalEngine engine) {
    double[] u = new double[data.length];
    for (int i = 0; i < data.length; i++) {
      double value = engine.evalN(F.CDF(distribution, F.num(data[i]))).evalfNaN();
      if (!Double.isFinite(value) || value < 0.0 || value > 1.0) {
        return null;
      }
      u[i] = value;
    }
    Arrays.sort(u);
    return u;
  }

  /**
   * <code>HypothesisTestData(&lt;|"FittedDistribution" -&gt; dist, "AutomaticTest" -&gt; test,
   * "TestData" -&gt; &lt;|test -&gt; {statistic, pValue}, ...|&gt;|&gt;)</code>
   */
  static IAST hypothesisTestData(double[] u, IExpr distribution) {
    IASTAppendable testData = F.ListAlloc(TESTS.length);
    for (String test : TESTS) {
      double[] result = test(test, u);
      testData.append(F.Rule(F.stringx(test), F.List(F.num(result[0]), F.num(clamp(result[1])))));
    }
    IASTAppendable fields = F.ListAlloc(4);
    fields.append(F.Rule(F.stringx("FittedDistribution"), distribution));
    fields.append(F.Rule(F.stringx("SampleSize"), F.ZZ(u.length)));
    fields.append(F.Rule(F.stringx("AutomaticTest"), F.stringx(AUTOMATIC_TEST)));
    fields.append(F.Rule(F.stringx("TestData"), F.assoc(testData)));
    return F.unaryAST1(S.HypothesisTestData, F.assoc(fields));
  }

  private static double clamp(double p) {
    return Math.max(0.0, Math.min(1.0, p));
  }

  /** <code>{statistic, pValue}</code> of one test for the sorted CDF values <code>u</code>. */
  static double[] test(String test, double[] u) {
    switch (test) {
      case "AndersonDarling":
        return andersonDarling(u);
      case "CramerVonMises":
        return cramerVonMises(u);
      case "KolmogorovSmirnov":
        return kolmogorovSmirnov(u);
      case "Kuiper":
        return kuiper(u);
      case "PearsonChiSquare":
        return pearsonChiSquare(u);
      default:
        return watsonUSquare(u);
    }
  }

  /** <code>{D+, D-}</code>, the largest distances above and below the empirical CDF. */
  private static double[] distances(double[] u) {
    int n = u.length;
    double plus = 0.0;
    double minus = 0.0;
    for (int i = 0; i < n; i++) {
      plus = Math.max(plus, (i + 1.0) / n - u[i]);
      minus = Math.max(minus, u[i] - (double) i / n);
    }
    return new double[] {plus, minus};
  }

  public static double[] kolmogorovSmirnov(double[] u) {
    double[] d = distances(u);
    double statistic = Math.max(d[0], d[1]);
    // the same p-value KolmogorovSmirnovTest gives
    double p =
        1.0 - new org.hipparchus.stat.inference.KolmogorovSmirnovTest().cdf(statistic, u.length);
    return new double[] {statistic, p};
  }

  private static double[] kuiper(double[] u) {
    double[] d = distances(u);
    // the statistic is D+ + D- - 1/n; its p-value is that of D+ + D-
    double statistic = d[0] + d[1] - 1.0 / u.length;
    double v = d[0] + d[1];
    double root = Math.sqrt(u.length);
    double lambda = (root + 0.155 + 0.24 / root) * v;
    double p = 1.0;
    if (lambda >= 0.4) {
      p = 0.0;
      for (int j = 1; j <= 100; j++) {
        double term =
            (4.0 * j * j * lambda * lambda - 1.0) * Math.exp(-2.0 * j * j * lambda * lambda);
        p += term;
        if (Math.abs(term) < 1e-16) {
          break;
        }
      }
      p *= 2.0;
    }
    return new double[] {statistic, p};
  }

  private static double[] andersonDarling(double[] u) {
    int n = u.length;
    double sum = 0.0;
    for (int i = 0; i < n; i++) {
      double lower = Math.max(u[i], 1e-300);
      double upper = Math.max(1.0 - u[n - 1 - i], 1e-300);
      sum += (2.0 * i + 1.0) * (Math.log(lower) + Math.log(upper));
    }
    double statistic = -n - sum / n;
    return new double[] {statistic, 1.0 - andersonDarlingCDF(n, statistic)};
  }

  /** Marsaglia and Marsaglia (2004), "Evaluating the Anderson-Darling Distribution". */
  private static double andersonDarlingCDF(int n, double z) {
    double x = adInfinity(z);
    return x + adErrorFix(n, x);
  }

  private static double adInfinity(double z) {
    if (z <= 0.0) {
      return 0.0;
    }
    if (z < 2.0) {
      return Math.exp(-1.2337141 / z) / Math.sqrt(z) * (2.00012
          + (.247105 - (.0649821 - (.0347962 - (.011672 - .00168691 * z) * z) * z) * z) * z);
    }
    return Math.exp(-Math
        .exp(1.0776 - (2.30695 - (.43424 - (.082433 - (.008056 - .0003146 * z) * z) * z) * z) * z));
  }

  private static double adErrorFix(int n, double x) {
    double c = .01265 + .1757 / n;
    if (x < c) {
      double t = x / c;
      t = Math.sqrt(t) * (1.0 - t) * (49.0 * t - 102.0);
      return t * (.0037 / (n * n) + .00078 / n + .00006) / n;
    }
    if (x < .8) {
      double t = (x - c) / (.8 - c);
      t = -.00022633 + (6.54034 - (14.6538 - (14.458 - (8.259 - 1.91864 * t) * t) * t) * t) * t;
      return t * (.04213 / n + .01365 / (n * n)) / n;
    }
    return (-130.2137
        + (745.2337 - (1705.091 - (1950.646 - (1116.360 - 255.7844 * x) * x) * x) * x) * x) / n;
  }

  private static double cramerVonMisesStatistic(double[] u) {
    int n = u.length;
    double statistic = 1.0 / (12.0 * n);
    for (int i = 0; i < n; i++) {
      double d = u[i] - (2.0 * i + 1.0) / (2.0 * n);
      statistic += d * d;
    }
    return statistic;
  }

  private static double[] cramerVonMises(double[] u) {
    double statistic = cramerVonMisesStatistic(u);
    return new double[] {statistic, 1.0 - cramerVonMisesCDF(statistic)};
  }

  /**
   * The limiting distribution of <code>W^2</code> (Anderson and Darling 1952):
   * <code>1/(Pi Sqrt(x)) Sum_k Gamma(k+1/2)/(Gamma(1/2) k!) Sqrt(4k+1) Exp(-y_k) K_{1/4}(y_k)</code>
   * with <code>y_k = (4k+1)^2/(16x)</code>.
   */
  private static double cramerVonMisesCDF(double x) {
    if (x <= 0.0) {
      return 0.0;
    }
    double sum = 0.0;
    double coefficient = 1.0; // Gamma(k+1/2)/(Gamma(1/2) k!)
    for (int k = 0; k < 200; k++) {
      double y = (4.0 * k + 1.0) * (4.0 * k + 1.0) / (16.0 * x);
      if (y > 700.0) {
        break;
      }
      double term = coefficient * Math.sqrt(4.0 * k + 1.0) * Math.exp(-y) * besselK(0.25, y);
      sum += term;
      if (term < 1e-17) {
        break;
      }
      coefficient *= (k + 0.5) / (k + 1.0);
    }
    return sum / (Math.PI * Math.sqrt(x));
  }

  /**
   * <code>K_nu(y)</code> by the trapezoidal rule on
   * <code>Int(Exp(-y Cosh(t)) Cosh(nu t), t)</code>.
   */
  private static double besselK(double nu, double y) {
    double upper = acosh(700.0 / y + 1.0);
    int steps = 2000;
    double h = upper / steps;
    double sum = 0.5 * Math.exp(-y);
    for (int i = 1; i <= steps; i++) {
      double t = i * h;
      sum += Math.exp(-y * Math.cosh(t)) * Math.cosh(nu * t);
    }
    return sum * h;
  }

  private static double acosh(double x) {
    return Math.log(x + Math.sqrt(x * x - 1.0));
  }

  private static double[] watsonUSquare(double[] u) {
    int n = u.length;
    double mean = 0.0;
    for (double value : u) {
      mean += value;
    }
    mean /= n;
    double statistic = cramerVonMisesStatistic(u) - n * (mean - 0.5) * (mean - 0.5);
    // Stephens' modification for finite samples
    double modified = (statistic - 0.1 / n + 0.1 / (n * n)) * (1.0 + 0.8 / n);
    double p = 0.0;
    for (int k = 1; k <= 100; k++) {
      double term = Math.exp(-2.0 * k * k * Math.PI * Math.PI * modified);
      p += (k % 2 == 1) ? term : -term;
      if (term < 1e-17) {
        break;
      }
    }
    return new double[] {statistic, modified <= 0.0 ? 1.0 : 2.0 * p};
  }

  private static double[] pearsonChiSquare(double[] u) {
    int n = u.length;
    int bins = Math.max(2, (int) Math.ceil(2.0 * Math.pow(n, 0.4)));
    int[] counts = new int[bins];
    for (double value : u) {
      counts[Math.min(bins - 1, (int) (value * bins))]++;
    }
    double expected = (double) n / bins;
    double statistic = 0.0;
    for (int count : counts) {
      statistic += (count - expected) * (count - expected) / expected;
    }
    double p = Gamma.regularizedGammaQ(0.5 * (bins - 1), 0.5 * statistic);
    return new double[] {statistic, p};
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_3;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  /** The association field of a <code>HypothesisTestData</code> object. */
  static IAssociation fields(IAST object) {
    return (IAssociation) object.arg1();
  }
}

package org.matheclipse.core.reflection.system;

import java.util.Arrays;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>MannWhitneyTest({x, y})</code>, <code>MannWhitneyTest({x, y}, mu0)</code> and
 * <code>MannWhitneyTest({x, y}, mu0, property)</code> - the rank test whether the median
 * difference of the samples <code>x</code> and <code>y</code> is <code>mu0</code> (0 by default).
 *
 * <p>
 * The statistic is <code>U</code>, the number of pairs with <code>x_i - mu0 &gt; y_j</code>, a tie
 * counting one half; the two-sided test reports the smaller of the <code>U</code> of the two
 * samples. Up to {@value #EXACT_LIMIT} values in all the p-value comes from the exact distribution
 * of <code>U</code> for untied samples and counts only the values strictly beyond the observed one:
 * <code>P(U &lt;= u - 1)</code> for the alternative <code>"Less"</code>,
 * <code>P(U &gt;= u + 1)</code> for <code>"Greater"</code> and twice the smaller tail for the
 * two-sided test. For larger samples it is the normal approximation with the variance corrected
 * for ties and a shift by one half, capped at 1.
 *
 * <p>
 * The options are <code>AlternativeHypothesis -&gt; "Unequal" | "Less" | "Greater"</code> and
 * <code>SignificanceLevel -&gt; 1/20</code>, which decides <code>"TestConclusion"</code> and
 * <code>"ShortTestConclusion"</code>.
 */
public class MannWhitneyTest extends AbstractFunctionEvaluator {

  /** Up to this many values in both samples together the p-value is exact. */
  static final int EXACT_LIMIT = 40;

  private static final String[] PROPERTIES = {"HypothesisTestData", "MannWhitney", "Properties",
      "PValue", "PValueTable", "ShortTestConclusion", "TestConclusion", "TestData",
      "TestDataTable", "TestStatistic", "TestStatisticTable"};

  private static final String NAME = "Mann‐Whitney";

  private static final int UNEQUAL = 0;
  private static final int LESS = 1;
  private static final int GREATER = 2;

  /** The statistic and p-value of one test. */
  private static final class Result {
    final IExpr mu0;
    final int alternative;
    final IExpr level;
    /** the statistic */
    final double u;
    /** the p-value, a machine number or an exact 1 */
    final IExpr pValue;

    Result(double[] x, double[] y, IExpr mu0, double mu, int alternative, IExpr level) {
      this.mu0 = mu0;
      this.alternative = alternative;
      this.level = level;
      final int n1 = x.length;
      final int n2 = y.length;
      // twice U, an integer
      long u1Twice = 0;
      for (double xi : x) {
        double shifted = xi - mu;
        for (double yj : y) {
          if (shifted > yj) {
            u1Twice += 2;
          } else if (shifted == yj) {
            u1Twice++;
          }
        }
      }
      long product = (long) n1 * n2;
      long u2Twice = 2 * product - u1Twice;
      long uMinTwice = Math.min(u1Twice, u2Twice);
      this.u = (alternative == UNEQUAL ? uMinTwice : u1Twice) / 2.0;
      if (n1 + n2 <= EXACT_LIMIT) {
        long[] counts = untiedCounts(n1, n2);
        double total = 0.0;
        for (long count : counts) {
          total += count;
        }
        switch (alternative) {
          case LESS:
            pValue = F.num(atMost(counts, u1Twice - 2) / total);
            break;
          case GREATER:
            // P(U >= u + 1) is P(U <= n1*n2 - u - 1) by symmetry
            pValue = F.num(atMost(counts, u2Twice - 2) / total);
            break;
          default:
            pValue = F.num(2.0 * atMost(counts, uMinTwice - 2) / total);
        }
      } else {
        double mean = product / 2.0;
        double sigma = Math.sqrt(product / 12.0 * (n1 + n2 + 1.0 - tieSum(x, y, mu)
            / ((double) (n1 + n2) * (n1 + n2 - 1.0))));
        double shift = n1 >= n2 ? -0.5 : 0.5;
        switch (alternative) {
          case LESS:
            pValue = F.num(normalCDF((u1Twice / 2.0 - mean + shift) / sigma));
            break;
          case GREATER:
            pValue = F.num(1.0 - normalCDF((u1Twice / 2.0 - mean + shift) / sigma));
            break;
          default:
            double p = 2.0 * normalCDF((uMinTwice / 2.0 - mean + shift) / sigma);
            pValue = p >= 1.0 ? F.C1 : F.num(p);
        }
      }
    }

    boolean rejected() {
      return pValue.evalf() < level.evalf();
    }
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    int positional = ast.argSize();
    int alternative = UNEQUAL;
    IExpr level = F.QQ(1, 20);
    while (positional > 1 && ast.get(positional).isRuleAST()) {
      IExpr option = ast.get(positional--);
      if (option.first() == S.AlternativeHypothesis) {
        if (option.second().isString("Less")) {
          alternative = LESS;
        } else if (option.second().isString("Greater")) {
          alternative = GREATER;
        } else if (!option.second().isString("Unequal")) {
          return F.NIL;
        }
      } else if (option.first() == S.SignificanceLevel) {
        double value = option.second().evalfNaN();
        if (!(value > 0.0 && value < 1.0)) {
          return F.NIL;
        }
        level = option.second();
      } else {
        return F.NIL;
      }
    }
    if (positional > 3) {
      return F.NIL;
    }
    IExpr data = ast.arg1();
    double[] x = null;
    double[] y = null;
    if (data.isList2() && data.first().isList() && data.second().isList()) {
      x = data.first().toDoubleVector();
      y = data.second().toDoubleVector();
    }
    if (x == null || y == null || x.length < 2 || y.length < 2) {
      // The argument `1` at position `2` should be a list containing two rectangular arrays of
      // real numbers where each array has length greater than its dimension.
      return Errors.printMessage(S.MannWhitneyTest, "rctndmln", F.list(data, F.C1), engine);
    }
    IExpr mu0 = positional >= 2 && ast.arg2() != S.Automatic ? ast.arg2() : F.C0;
    double mu = mu0.evalfNaN();
    if (Double.isNaN(mu)) {
      return F.NIL;
    }
    IExpr property = positional == 3 ? ast.arg3() : F.stringx("PValue");
    if (property.isList()) {
      for (IExpr p : (IAST) property) {
        if (!isProperty(p)) {
          return invalidProperty(p, engine);
        }
      }
    } else if (!isProperty(property)) {
      return invalidProperty(property, engine);
    }
    if (property.isString("HypothesisTestData")) {
      return hypothesisTestData(x, y, mu0, alternative, level);
    }
    Result result = new Result(x, y, mu0, mu, alternative, level);
    if (property.isList()) {
      return ((IAST) property).map(p -> property(result, p, engine), 1);
    }
    return property(result, property, engine);
  }

  private static boolean isProperty(IExpr property) {
    return property.isString() && Arrays.asList(PROPERTIES).contains(property.toString());
  }

  private static final String[] ALTERNATIVES = {"Unequal", "Less", "Greater"};

  /**
   * <code>HypothesisTestData(MannWhitneyTest, {data, "SampleToSample", 1, level, {mu0,
   * alternative, Automatic, Automatic}, Automatic, {}}, assumptions)</code>, which answers the
   * properties of the test.
   */
  private static IExpr hypothesisTestData(double[] x, double[] y, IExpr mu0, int alternative,
      IExpr level) {
    IAST data = F.List(F.mapRange(0, x.length, i -> F.num(x[i])),
        F.mapRange(0, y.length, i -> F.num(y[i])));
    IAST specification = F.List(data, F.stringx("SampleToSample"), F.C1, level,
        F.List(mu0, F.stringx(ALTERNATIVES[alternative]), S.Automatic, S.Automatic), S.Automatic,
        F.CEmptyList);
    IAST assumptions = F.List(F.Rule(F.stringx("Normality"), F.C0),
        F.Rule(F.stringx("EqualVariance"), F.C0), F.Rule(F.stringx("Symmetry"), F.C0));
    return F.function(S.HypothesisTestData, S.MannWhitneyTest, specification, assumptions);
  }

  /**
   * <code>h(property)</code> of a <code>HypothesisTestData</code> object <code>h</code> made by
   * this test: the test run again with the data, <code>mu0</code> and options it holds.
   *
   * @return {@link F#NIL} if <code>object</code> doesn't have the layout of one
   */
  static IExpr property(IAST object, IExpr property, EvalEngine engine) {
    IExpr specification = object.arg2();
    if (!specification.isList() || specification.argSize() < 5 || !specification.get(5).isList()
        || specification.get(5).argSize() < 2) {
      return F.NIL;
    }
    IExpr hypothesis = specification.get(5);
    return engine.evaluate(F.function(S.MannWhitneyTest, specification.first(),
        hypothesis.first(), property, F.Rule(S.AlternativeHypothesis, hypothesis.second()),
        F.Rule(S.SignificanceLevel, specification.get(4))));
  }

  private static IExpr invalidProperty(IExpr property, EvalEngine engine) {
    // The argument `1` is not a valid property. Specify "Properties" to obtain a list of valid
    // properties.
    Errors.printMessage(S.MannWhitneyTest, "invprp", F.list(property), engine);
    return F.NIL;
  }

  private static IExpr property(Result result, IExpr property, EvalEngine engine) {
    IExpr statistic = F.num(result.u);
    switch (property.toString()) {
      case "Properties":
        return F.mapRange(0, PROPERTIES.length, i -> F.stringx(PROPERTIES[i]));
      case "PValue":
      case "MannWhitney":
        return result.pValue;
      case "TestStatistic":
        return statistic;
      case "TestData":
        return F.List(statistic, result.pValue);
      case "TestDataTable":
        return HypothesisTestData.grid(F.List(
            F.List(F.stringx(""), F.stringx("Statistic"), F.stringx("P‐Value")),
            F.List(F.stringx(NAME), statistic, result.pValue)));
      case "TestStatisticTable":
        return HypothesisTestData.grid(F.List(F.List(F.stringx(""), F.stringx("Statistic")),
            F.List(F.stringx(NAME), statistic)));
      case "PValueTable":
        return HypothesisTestData.grid(F.List(F.List(F.stringx(""), F.stringx("P‐Value")),
            F.List(F.stringx(NAME), result.pValue)));
      case "ShortTestConclusion":
        return F.stringx(result.rejected() ? "Reject" : "Do not reject");
      case "TestConclusion":
        return F.stringx(conclusion(result, engine));
      default:
        return F.Missing(S.NotAvailable, property);
    }
  }

  /** The sentence which says whether the null hypothesis is rejected. */
  private static String conclusion(Result result, EvalEngine engine) {
    String hypothesis;
    switch (result.alternative) {
      case LESS:
        hypothesis = "greater than or equal to ";
        break;
      case GREATER:
        hypothesis = "less than or equal to ";
        break;
      default:
        hypothesis = "";
    }
    IExpr percent = engine.evaluate(F.Times(F.C100, result.level));
    return "The null hypothesis that the median difference is " + hypothesis
        + numberText(result.mu0) + " is " + (result.rejected() ? "rejected" : "not rejected")
        + " at the " + numberText(percent) + " percent level based on the " + NAME + " test.";
  }

  /** A number in a sentence: a machine number with an integer value is written <code>1.</code>. */
  private static String numberText(IExpr number) {
    String text = number.toString();
    return number.isReal() && !number.isRational() && text.endsWith(".0")
        ? text.substring(0, text.length() - 1)
        : text;
  }

  /** The number of the values <code>U</code> with <code>2 U &lt;= twiceBound</code>. */
  private static double atMost(long[] counts, long twiceBound) {
    long bound = Math.floorDiv(twiceBound, 2);
    double sum = 0.0;
    for (int k = 0; k <= bound && k < counts.length; k++) {
      sum += counts[k];
    }
    return sum;
  }

  /**
   * The number of arrangements of <code>n1 + n2</code> distinct values which give the statistic
   * <code>U = 0, 1, ..., n1*n2</code>: <code>c(m, n, u) = c(m - 1, n, u - n) + c(m, n - 1, u)</code>.
   */
  static long[] untiedCounts(int n1, int n2) {
    // row[n][u] for the current m
    long[][] previous = new long[n2 + 1][];
    for (int n = 0; n <= n2; n++) {
      previous[n] = new long[] {1};
    }
    for (int m = 1; m <= n1; m++) {
      long[][] current = new long[n2 + 1][];
      current[0] = new long[] {1};
      for (int n = 1; n <= n2; n++) {
        long[] counts = new long[m * n + 1];
        long[] fewerX = previous[n];
        for (int u = 0; u < fewerX.length; u++) {
          counts[u + n] += fewerX[u];
        }
        long[] fewerY = current[n - 1];
        for (int u = 0; u < fewerY.length; u++) {
          counts[u] += fewerY[u];
        }
        current[n] = counts;
      }
      previous = current;
    }
    return previous[n2];
  }

  /** <code>sum(t^3 - t)</code> over the groups of <code>t</code> equal values of both samples. */
  private static double tieSum(double[] x, double[] y, double mu) {
    double[] all = new double[x.length + y.length];
    for (int i = 0; i < x.length; i++) {
      all[i] = x[i] - mu;
    }
    System.arraycopy(y, 0, all, x.length, y.length);
    Arrays.sort(all);
    double sum = 0.0;
    int i = 0;
    while (i < all.length) {
      int j = i;
      while (j < all.length && all[j] == all[i]) {
        j++;
      }
      double t = j - i;
      sum += t * t * t - t;
      i = j;
    }
    return sum;
  }

  private static double normalCDF(double z) {
    return 0.5 * org.hipparchus.special.Erf.erfc(-z / Math.sqrt(2.0));
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_INFINITY;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }
}

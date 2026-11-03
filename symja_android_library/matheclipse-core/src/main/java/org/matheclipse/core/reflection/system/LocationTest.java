package org.matheclipse.core.reflection.system;

import org.hipparchus.distribution.continuous.FDistribution;
import org.hipparchus.distribution.continuous.TDistribution;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>LocationTest(data)</code>, <code>LocationTest(data, mu0)</code> and
 * <code>LocationTest(data, mu0, property)</code> - the t test whether the mean of the vector
 * <code>data</code> is <code>mu0</code>, or whether the means of the two vectors of
 * <code>data = {data1, data2}</code> differ by <code>mu0</code>. <code>mu0</code> is 0 by default.
 * Two samples use the pooled variance unless an F test at the level 0.05 rejects equal variances;
 * <code>VerifyTestAssumptions -> None</code> assumes them without the F test.
 *
 * <p>
 * The property is <code>"PValue"</code> (the default), <code>"T"</code> for the p-value of that
 * test, <code>"TestStatistic"</code>, <code>"TestData"</code>, <code>"DegreesOfFreedom"</code>,
 * <code>"TestDataTable"</code> or a list of properties.
 */
public class LocationTest extends AbstractFunctionEvaluator {

  private static final double SIGNIFICANCE_LEVEL = 0.05;

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    int positional = ast.argSize();
    boolean verify = true;
    while (positional > 1 && ast.get(positional).isRuleAST()) {
      IExpr option = ast.get(positional--);
      if (option.first().equals(S.VerifyTestAssumptions) && option.second() == S.None) {
        verify = false;
      }
    }
    if (positional > 3) {
      return F.NIL;
    }
    IExpr mu0 = positional >= 2 ? ast.arg2() : F.C0;
    double mu = mu0 == S.Automatic ? 0.0 : mu0.evalfNaN();
    if (Double.isNaN(mu)) {
      return F.NIL;
    }
    IExpr property = positional == 3 ? ast.arg3() : F.stringx("PValue");
    double[] statistic = statistic(ast.arg1(), mu, verify);
    if (statistic == null) {
      return F.NIL;
    }
    if (property.isList()) {
      IAST properties = (IAST) property;
      return properties.exists(x -> property(x, statistic).isNIL()) ? F.NIL
          : properties.map(x -> property(x, statistic));
    }
    return property(property, statistic);
  }

  /** <code>{t, degrees of freedom}</code>, or <code>null</code> if the data can't be tested. */
  private static double[] statistic(IExpr data, double mu, boolean verify) {
    if (data.isList2() && data.first().isList() && data.second().isList()) {
      double[] x = data.first().toDoubleVector();
      double[] y = data.second().toDoubleVector();
      if (x == null || y == null || x.length < 2 || y.length < 2) {
        return null;
      }
      double difference = mean(x) - mean(y) - mu;
      double varianceX = variance(x);
      double varianceY = variance(y);
      if (!verify || equalVariances(varianceX, x.length, varianceY, y.length)) {
        double degrees = x.length + y.length - 2.0;
        double pooled = ((x.length - 1.0) * varianceX + (y.length - 1.0) * varianceY) / degrees;
        return new double[] {difference / Math.sqrt(pooled / x.length + pooled / y.length),
            degrees};
      }
      double a = varianceX / x.length;
      double b = varianceY / y.length;
      return new double[] {difference / Math.sqrt(a + b),
          (a + b) * (a + b) / (a * a / (x.length - 1.0) + b * b / (y.length - 1.0))};
    }
    double[] x = data.toDoubleVector();
    if (x == null || x.length < 2) {
      return null;
    }
    return new double[] {(mean(x) - mu) / Math.sqrt(variance(x) / x.length), x.length - 1.0};
  }

  /** Whether the two-sided F test doesn't reject equal variances at the level 0.05. */
  private static boolean equalVariances(double varianceX, int n, double varianceY, int m) {
    double ratio = varianceX / varianceY;
    if (Double.isNaN(ratio) || Double.isInfinite(ratio)) {
      return false;
    }
    double lower = new FDistribution(n - 1.0, m - 1.0).cumulativeProbability(ratio);
    return 2.0 * Math.min(lower, 1.0 - lower) > SIGNIFICANCE_LEVEL;
  }

  private static double mean(double[] x) {
    double sum = 0.0;
    for (double value : x) {
      sum += value;
    }
    return sum / x.length;
  }

  private static double variance(double[] x) {
    double mean = mean(x);
    double sum = 0.0;
    for (double value : x) {
      sum += (value - mean) * (value - mean);
    }
    return sum / (x.length - 1.0);
  }

  private static double pValue(double[] statistic) {
    double t = Math.abs(statistic[0]);
    if (Double.isNaN(t)) {
      return Double.NaN;
    }
    return 2.0 * new TDistribution(statistic[1]).cumulativeProbability(-t);
  }

  /** The value of the property, {@link F#NIL} if it is unknown. */
  private static IExpr property(IExpr property, double[] statistic) {
    if (!property.isString()) {
      return F.NIL;
    }
    switch (property.toString()) {
      case "PValue":
      case "T":
        return F.num(pValue(statistic));
      case "TestStatistic":
        return F.num(statistic[0]);
      case "TestData":
        return F.List(F.num(statistic[0]), F.num(pValue(statistic)));
      case "DegreesOfFreedom":
        return F.num(statistic[1]);
      case "TestDataTable":
        return F.unaryAST1(S.Grid,
            F.List(F.List(F.stringx(""), F.stringx("Statistic"), F.stringx("P\u2010Value")),
                F.List(F.stringx("T"), F.num(statistic[0]), F.num(pValue(statistic)))));
      default:
        return F.NIL;
    }
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_INFINITY;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}

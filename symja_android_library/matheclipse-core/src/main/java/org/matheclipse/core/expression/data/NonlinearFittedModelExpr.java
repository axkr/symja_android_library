package org.matheclipse.core.expression.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import org.hipparchus.distribution.continuous.TDistribution;
import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.LUDecomposition;
import org.hipparchus.linear.RealMatrix;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The <code>FittedModel</code> <code>NonlinearModelFit</code> returns.
 *
 * <p>
 * Its goodness of fit follows the reference implementation, which is not quite the linear one:
 * R-squared is measured against the mean of the data as for a linear fit, but the adjusted value
 * scales by <code>n/(n-p)</code> where a linear fit with a constant term uses <code>(n-1)/(n-p)</code>
 * - a nonlinear model has no constant term it can be assumed to carry. For the data
 * <code>{{1,2.1},{2,3.9},{3,6.2},{4,7.8}}</code> and <code>a x + b</code> that is 0.99566 and 0.99132.
 */
public class NonlinearFittedModelExpr extends AbstractFittedModelExpr<NonlinearFittedModelExpr.Fit>
    implements Externalizable {

  private static final long serialVersionUID = -5412690383154712077L;

  private static final String[] PROPERTIES = {"AdjustedRSquared", "BestFit", "BestFitParameters",
      "EstimatedVariance", "FitResiduals", "ParameterConfidenceIntervals", "ParameterErrors",
      "ParameterPValues", "ParameterTStatistics", "ParameterTable", "ParameterTableEntries",
      "PredictedResponse", "RSquared"};

  /** A finished fit: the model, its parameters and their values, and the data it was fitted to. */
  public static final class Fit {
    final IExpr function;
    final IAST parameters;
    final IExpr variable;
    final double[] values;
    final double[] x;
    final double[] y;

    public Fit(IExpr function, IAST parameters, IExpr variable, double[] values, double[] x,
        double[] y) {
      this.function = function;
      this.parameters = parameters;
      this.variable = variable;
      this.values = values;
      this.x = x;
      this.y = y;
    }
  }

  /** No-argument constructor required for {@link Externalizable} deserialization. */
  public NonlinearFittedModelExpr() {
    super(null);
  }

  public NonlinearFittedModelExpr(Fit fit) {
    super(fit);
  }

  /** <code>{a -> 1.94, b -> 0.15}</code> - rules, where a linear fit lists bare coefficients. */
  private IAST parameterRules() {
    Fit fit = toData();
    return F.mapList(fit.parameters, (symbol, i) -> F.Rule(symbol, F.num(fit.values[i - 1])));
  }

  @Override
  public IAST normal(boolean nilIfUnevaluated) {
    IExpr bestFit = EvalEngine.get().evaluate(F.subst(toData().function, parameterRules()));
    return bestFit.isAST() ? (IAST) bestFit : F.Plus(bestFit);
  }

  private double[] predicted(EvalEngine engine) {
    Fit fit = toData();
    IExpr bestFit = normal(false);
    double[] predicted = new double[fit.x.length];
    for (int i = 0; i < fit.x.length; i++) {
      predicted[i] = engine.evalDouble(F.subst(bestFit, F.Rule(fit.variable, F.num(fit.x[i]))));
    }
    return predicted;
  }

  /** R-squared against the mean of the data. */
  private double rSquared(double[] predicted) {
    double[] y = toData().y;
    double mean = Arrays.stream(y).average().orElse(0.0);
    double total = 0.0;
    double residual = 0.0;
    for (int i = 0; i < y.length; i++) {
      total += (y[i] - mean) * (y[i] - mean);
      residual += (y[i] - predicted[i]) * (y[i] - predicted[i]);
    }
    return 1.0 - residual / total;
  }

  @Override
  protected String[] propertyNames() {
    return PROPERTIES;
  }

  @Override
  protected IExpr property(String name, EvalEngine engine) {
    Fit fit = toData();
    switch (name) {
      case "BestFit":
        return normal(false);
      case "BestFitParameters":
        return parameterRules();
      case "PredictedResponse":
        return F.List(predicted(engine));
      case "FitResiduals": {
        double[] predicted = predicted(engine);
        IASTAppendable residuals = F.ListAlloc(predicted.length);
        for (int i = 0; i < predicted.length; i++) {
          residuals.append(F.num(fit.y[i] - predicted[i]));
        }
        return residuals;
      }
      case "RSquared":
        return F.num(rSquared(predicted(engine)));
      case "AdjustedRSquared": {
        double n = fit.y.length;
        double p = fit.parameters.argSize();
        return F.num(1.0 - (1.0 - rSquared(predicted(engine))) * n / (n - p));
      }
      case "EstimatedVariance":
        return F.num(estimatedVariance(engine));
      case "ParameterErrors":
        return parameterColumn(engine, 1);
      case "ParameterTStatistics":
        return parameterColumn(engine, 2);
      case "ParameterPValues":
        return parameterColumn(engine, 3);
      case "ParameterTableEntries": {
        IExpr[][] rows = parameterStatistics(engine);
        return F.mapRange(0, rows.length, i -> F.List(rows[i]));
      }
      case "ParameterConfidenceIntervals":
        return confidenceIntervals(engine, 0.95);
      case "ParameterTable":
        return parameterTable(engine);
      default:
        return F.NIL;
    }
  }

  /** The residual degrees of freedom: data points less parameters. */
  private int degreesOfFreedom() {
    return toData().y.length - toData().parameters.argSize();
  }

  /** The residual sum of squares over the residual degrees of freedom. */
  private double estimatedVariance(EvalEngine engine) {
    Fit fit = toData();
    int df = degreesOfFreedom();
    if (df <= 0) {
      return Double.NaN;
    }
    double[] predicted = predicted(engine);
    double sum = 0.0;
    for (int i = 0; i < predicted.length; i++) {
      sum += (fit.y[i] - predicted[i]) * (fit.y[i] - predicted[i]);
    }
    return sum / df;
  }

  /**
   * The asymptotic standard errors of the parameters: the square roots of the diagonal of
   * <code>s^2 * Inverse(Transpose(J).J)</code>, with <code>J</code> the derivatives of the model
   * by its parameters at the data points and <code>s^2</code> the estimated variance.
   *
   * @return <code>null</code> if they cannot be computed
   */
  private double[] standardErrors(EvalEngine engine) {
    Fit fit = toData();
    int p = fit.parameters.argSize();
    int n = fit.x.length;
    double variance = estimatedVariance(engine);
    if (Double.isNaN(variance)) {
      return null;
    }
    try {
      IAST rules = parameterRules();
      double[][] jacobian = new double[n][p];
      for (int j = 0; j < p; j++) {
        IExpr derivative =
            engine.evaluate(F.subst(engine.evaluate(F.D(fit.function, fit.parameters.get(j + 1))),
                rules));
        for (int i = 0; i < n; i++) {
          jacobian[i][j] =
              engine.evalDouble(F.subst(derivative, F.Rule(fit.variable, F.num(fit.x[i]))));
        }
      }
      RealMatrix j = new Array2DRowRealMatrix(jacobian, false);
      RealMatrix covariance =
          new LUDecomposition(j.transpose().multiply(j)).getSolver().getInverse();
      double[] errors = new double[p];
      for (int k = 0; k < p; k++) {
        errors[k] = Math.sqrt(variance * covariance.getEntry(k, k));
      }
      return errors;
    } catch (RuntimeException rex) {
      // a singular matrix, or a derivative with no number at a data point
      return null;
    }
  }

  /**
   * One row <code>{estimate, standard error, t, p}</code> per parameter, with the two-tailed
   * p-value of Student's t distribution; what cannot be computed is <code>Indeterminate</code>.
   */
  private IExpr[][] parameterStatistics(EvalEngine engine) {
    Fit fit = toData();
    double[] errors = standardErrors(engine);
    int df = degreesOfFreedom();
    TDistribution t = df > 0 ? new TDistribution(df) : null;
    IExpr[][] rows = new IExpr[fit.values.length][4];
    for (int i = 0; i < rows.length; i++) {
      double estimate = fit.values[i];
      rows[i][0] = F.num(estimate);
      double error = errors == null ? Double.NaN : errors[i];
      if (!(error > 0.0) || Double.isNaN(estimate) || t == null) {
        rows[i][1] = rows[i][2] = rows[i][3] = S.Indeterminate;
        continue;
      }
      double statistic = estimate / error;
      rows[i][1] = F.num(error);
      rows[i][2] = F.num(statistic);
      rows[i][3] = F.num(2.0 * t.cumulativeProbability(-Math.abs(statistic)));
    }
    return rows;
  }

  /** One column of the parameter table, for every parameter. */
  private IAST parameterColumn(EvalEngine engine, int column) {
    IExpr[][] rows = parameterStatistics(engine);
    return F.mapRange(0, rows.length, i -> rows[i][column]);
  }

  /** <code>{estimate - q se, estimate + q se}</code> with q the Student t quantile. */
  private IAST confidenceIntervals(EvalEngine engine, double level) {
    IExpr[][] rows = parameterStatistics(engine);
    int df = degreesOfFreedom();
    double q =
        df > 0 ? new TDistribution(df).inverseCumulativeProbability(0.5 + 0.5 * level) : Double.NaN;
    return F.mapRange(0, rows.length, i -> {
      if (!rows[i][1].isReal()) {
        return F.List(S.Indeterminate, S.Indeterminate);
      }
      double estimate = rows[i][0].evalf();
      double halfWidth = q * rows[i][1].evalf();
      return F.List(F.num(estimate - halfWidth), F.num(estimate + halfWidth));
    });
  }

  /** The parameter table, one row per parameter. */
  private IExpr parameterTable(EvalEngine engine) {
    IExpr[][] rows = parameterStatistics(engine);
    IAST parameters = toData().parameters;
    IASTAppendable grid = F.ListAlloc(rows.length + 1);
    grid.append(F.List(F.stringx(""), F.stringx("Estimate"), F.stringx("Standard Error"),
        F.stringx("t\u2010Statistic"), F.stringx("P\u2010Value")));
    for (int i = 0; i < rows.length; i++) {
      IASTAppendable row = F.ListAlloc(5);
      row.append(parameters.get(i + 1));
      row.appendAll(rows[i], 0, 4);
      grid.append(row);
    }
    IExpr gray = F.GrayLevel(F.num(0.7));
    return F.binaryAST2(S.Style,
        F.function(S.Grid, grid, F.Rule(S.Alignment, F.List(S.Left, S.Automatic)),
            F.Rule(S.Dividers, F.List(F.List(F.Rule(F.C2, gray)), F.List(F.Rule(F.C2, gray)))),
            F.Rule(S.Spacings,
                F.List(F.List(F.Rule(F.C2, F.C1)), F.List(F.Rule(F.C2, F.num(0.75)))))),
        F.stringx("DialogStyle"));
  }

  @Override
  protected IAST fitVariables() {
    return F.list(toData().variable);
  }

  @Override
  public IExpr copy() {
    return new NonlinearFittedModelExpr(fData);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof NonlinearFittedModelExpr) {
      Fit a = toData();
      Fit b = ((NonlinearFittedModelExpr) obj).toData();
      return a.function.equals(b.function) && a.parameters.equals(b.parameters)
          && a.variable.equals(b.variable) && Arrays.equals(a.values, b.values)
          && Arrays.equals(a.x, b.x) && Arrays.equals(a.y, b.y);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return fData == null ? 463 : 463 + Arrays.hashCode(fData.values);
  }

  @Override
  public String toString() {
    return "FittedModel[" + EvalEngine.get().evaluate(normal(false)) + "]";
  }

  @Override
  public void writeExternal(ObjectOutput output) throws IOException {
    Fit fit = toData();
    output.writeObject(fit.function);
    output.writeObject(fit.parameters);
    output.writeObject(fit.variable);
    output.writeObject(fit.values);
    output.writeObject(fit.x);
    output.writeObject(fit.y);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    IExpr function = (IExpr) in.readObject();
    IAST parameters = (IAST) in.readObject();
    IExpr variable = (IExpr) in.readObject();
    double[] values = (double[]) in.readObject();
    double[] x = (double[]) in.readObject();
    double[] y = (double[]) in.readObject();
    fData = new Fit(function, parameters, variable, values, x, y);
  }
}

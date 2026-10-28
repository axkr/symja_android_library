package org.matheclipse.core.expression.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import org.hipparchus.distribution.continuous.FDistribution;
import org.hipparchus.distribution.continuous.TDistribution;
import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.RealMatrix;
import org.matheclipse.core.builtin.LinearAlgebra;
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

  private static final String[] PROPERTIES = {"AdjustedRSquared", "ANOVATable",
      "ANOVATableDegreesOfFreedom", "ANOVATableEntries", "ANOVATableMeanSquares",
      "ANOVATableSumsOfSquares", "BestFit", "BestFitParameters", "CovarianceMatrix",
      "EstimatedVariance", "FitResiduals", "MeanPredictionBands", "ParameterConfidenceIntervals",
      "ParameterConfidenceRegion", "ParameterErrors", "ParameterPValues", "ParameterTStatistics",
      "ParameterTable", "ParameterTableEntries", "PredictedResponse", "RSquared",
      "SinglePredictionBands"};

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
      case "ANOVATable":
        return anovaTable(engine);
      case "ANOVATableDegreesOfFreedom":
        return anovaColumn(engine, 0);
      case "ANOVATableEntries": {
        double[][] rows = anovaRows(engine);
        return F.mapRange(0, rows.length, i -> anovaRow(rows[i]));
      }
      case "ANOVATableMeanSquares":
        return anovaColumn(engine, 2);
      case "ANOVATableSumsOfSquares":
        return anovaColumn(engine, 1);
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
      case "CovarianceMatrix": {
        double[][] covariance = covarianceMatrix(engine);
        return covariance == null ? F.NIL : matrixList(covariance);
      }
      case "EstimatedVariance":
        return F.num(estimatedVariance(engine));
      case "MeanPredictionBands":
        return predictionBands(false, engine);
      case "ParameterConfidenceRegion": {
        double[][] covariance = covarianceMatrix(engine);
        if (covariance == null) {
          return F.NIL;
        }
        final int p = fit.parameters.argSize();
        double quantile =
            p * new FDistribution(p, degreesOfFreedom()).inverseCumulativeProbability(0.95);
        return parameterEllipsoid(fit.values, covariance, quantile, engine);
      }
      case "SinglePredictionBands":
        return predictionBands(true, engine);
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

  /**
   * The analysis of variance: <code>{DF, SS, MS}</code> of the model and of the error, then
   * <code>{DF, SS}</code> of the uncorrected total and of the total corrected for the mean. The sum
   * of squares of the model is the sum of the squared predicted values.
   */
  private double[][] anovaRows(EvalEngine engine) {
    Fit fit = toData();
    double[] predicted = predicted(engine);
    int n = fit.y.length;
    int p = fit.parameters.argSize();
    double mean = Arrays.stream(fit.y).average().orElse(0.0);
    double model = 0.0;
    double error = 0.0;
    double uncorrected = 0.0;
    double corrected = 0.0;
    for (int i = 0; i < n; i++) {
      model += predicted[i] * predicted[i];
      error += (fit.y[i] - predicted[i]) * (fit.y[i] - predicted[i]);
      uncorrected += fit.y[i] * fit.y[i];
      corrected += (fit.y[i] - mean) * (fit.y[i] - mean);
    }
    return new double[][] {{p, model, model / p}, //
        {n - p, error, n > p ? error / (n - p) : Double.NaN}, //
        {n, uncorrected}, //
        {n - 1, corrected}};
  }
  /** One column of the analysis of variance, for each row that has it. */
  private IExpr anovaColumn(EvalEngine engine, int column) {
    return anovaColumn(anovaRows(engine), column);
  }

  /** The analysis of variance as a table. */
  private IExpr anovaTable(EvalEngine engine) {
    double[][] rows = anovaRows(engine);
    String[] labels = {"Model", "Error", "Uncorrected Total", "Corrected Total"};
    IASTAppendable grid = F.ListAlloc(rows.length + 1);
    grid.append(F.List(F.stringx(""), F.stringx("DF"), F.stringx("SS"), F.stringx("MS")));
    for (int i = 0; i < rows.length; i++) {
      IASTAppendable row = F.ListAlloc(4);
      row.append(F.stringx(labels[i]));
      row.appendArgs(anovaRow(rows[i]));
      grid.append(row);
    }
    return dialogGrid(grid);
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
    double[][] covariance = covarianceMatrix(engine);
    if (covariance == null) {
      return null;
    }
    double[] errors = new double[covariance.length];
    for (int k = 0; k < errors.length; k++) {
      errors[k] = Math.sqrt(covariance[k][k]);
    }
    return errors;
  }

  /** The derivatives of the model by its parameters at the best fit, functions of the variable. */
  private IAST gradient(EvalEngine engine) {
    Fit fit = toData();
    IAST rules = parameterRules();
    return F.mapRange(1, fit.parameters.size(), j -> engine
        .evaluate(F.subst(engine.evaluate(F.D(fit.function, fit.parameters.get(j))), rules)));
  }

  /**
   * The asymptotic covariance matrix <code>s^2 * Inverse(Transpose(J).J)</code> of the parameters,
   * with <code>J</code> the derivatives of the model by its parameters at the data points and
   * <code>s^2</code> the estimated variance. If the parameters cannot all be identified from the
   * data, <code>Transpose(J).J</code> is singular and its pseudo-inverse is taken.
   *
   * @return <code>null</code> if it cannot be computed
   */
  private double[][] covarianceMatrix(EvalEngine engine) {
    Fit fit = toData();
    int p = fit.parameters.argSize();
    int n = fit.x.length;
    double variance = estimatedVariance(engine);
    if (Double.isNaN(variance)) {
      return null;
    }
    try {
      IAST gradient = gradient(engine);
      double[][] jacobian = new double[n][p];
      for (int j = 0; j < p; j++) {
        for (int i = 0; i < n; i++) {
          jacobian[i][j] =
              engine.evalDouble(F.subst(gradient.get(j + 1), F.Rule(fit.variable, F.num(fit.x[i]))));
        }
      }
      RealMatrix inverse =
          LinearAlgebra.machinePseudoInverse(new Array2DRowRealMatrix(jacobian, false));
      return inverse.multiply(inverse.transpose()).scalarMultiply(variance).getData();
    } catch (RuntimeException rex) {
      // a derivative with no number at a data point
      return null;
    }
  }

  /**
   * The 95% confidence band of a single prediction, or of the mean prediction, as a function of the
   * variable.
   */
  private IExpr predictionBands(boolean single, EvalEngine engine) {
    double[][] covariance = covarianceMatrix(engine);
    if (covariance == null) {
      return F.List(S.Indeterminate, S.Indeterminate);
    }
    return predictionBands(normal(false), gradient(engine), covariance,
        single ? estimatedVariance(engine) : 0.0, degreesOfFreedom(), engine);
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
    IExpr[][] rows = new IExpr[fit.values.length][];
    for (int i = 0; i < rows.length; i++) {
      rows[i] = statisticsRow(fit.values[i], errors == null ? Double.NaN : errors[i], t);
    }
    return rows;
  }

  /** One column of the parameter table, for every parameter. */
  private IAST parameterColumn(EvalEngine engine, int column) {
    return parameterColumn(parameterStatistics(engine), column);
  }

  /** <code>{estimate - q se, estimate + q se}</code> with q the Student t quantile. */
  private IAST confidenceIntervals(EvalEngine engine, double level) {
    return confidenceIntervals(parameterStatistics(engine), degreesOfFreedom(), level);
  }

  /** The parameter table, one row per parameter. */
  private IExpr parameterTable(EvalEngine engine) {
    IAST parameters = toData().parameters;
    return parameterTable(parameterStatistics(engine), i -> parameters.get(i + 1));
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

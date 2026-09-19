package org.matheclipse.core.expression.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
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
      "FitResiduals", "PredictedResponse", "RSquared"};

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
      default:
        return F.NIL;
    }
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

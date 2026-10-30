package org.matheclipse.core.expression.data;

import java.util.Arrays;
import java.util.function.IntFunction;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.hipparchus.distribution.continuous.TDistribution;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.ContextPath;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * What every <code>FittedModel</code> answers, whichever kind of fit produced it.
 *
 * <p>
 * A model is asked for a property by name - <code>model["RSquared"]</code> - or evaluated at a
 * point - <code>model[2.5]</code>, which is its best fit function at that point. A name the model
 * does not know prints <code>FittedModel::elmntavs</code> with the nearest name it does know, as
 * the reference implementation does, and leaves the call unevaluated.
 *
 * @param <T> the object holding the fit
 */
public abstract class AbstractFittedModelExpr<T> extends DataExpr<T> {

  private static final long serialVersionUID = 4386241931412806219L;

  protected AbstractFittedModelExpr(T data) {
    super(S.FittedModel, data);
  }

  /** The names {@link #property(String, EvalEngine)} answers for, sorted. */
  protected abstract String[] propertyNames();

  /** The value of one property, or {@link F#NIL} when this model has no such property. */
  protected abstract IExpr property(String name, EvalEngine engine);

  /**
   * The independent variables, in the order an evaluation at a point takes its arguments, or
   * {@link F#NIL} when the model cannot be evaluated at a point.
   */
  protected abstract IAST fitVariables();

  public IExpr evaluate(IAST ast, EvalEngine engine) {
    if (!(ast.head() instanceof AbstractFittedModelExpr)) {
      return F.NIL;
    }
    if (ast.isAST1() && ast.arg1().isString()) {
      String name = ast.arg1().toString();
      if ("Properties".equals(name)) {
        return F.mapRange(0, propertyNames().length, i -> F.stringx(propertyNames()[i]));
      }
      IExpr value = property(name, engine);
      if (value.isPresent()) {
        return value;
      }
      String nearest = nearestProperty(name);
      if (nearest == null) {
        // `1` is not a known property for FittedModel[...].
        return Errors.printMessage(S.FittedModel, "notprop", F.list(ast.arg1()), engine);
      }
      // "`1`" is not an available property. Did you mean "`2`" instead?
      return Errors.printMessage(S.FittedModel, "elmntavs",
          F.list(ast.arg1(), F.stringx(nearest)), engine);
    }
    if (ast.isAST1() && ast.arg1().isList() && ((IAST) ast.arg1()).argSize() > 0
        && ((IAST) ast.arg1()).forAll(x -> x.isString())) {
      // a list of properties; one which is not known leaves the whole query unevaluated
      IAST names = (IAST) ast.arg1();
      IASTAppendable values = F.ListAlloc(names.argSize());
      for (int i = 1; i <= names.argSize(); i++) {
        String name = names.get(i).toString();
        IExpr value = "Properties".equals(name) ? evaluate(F.unaryAST1(ast.head(), names.get(i)), engine)
            : property(name, engine);
        if (value.isNIL()) {
          // "`1`" is not an available property.
          return Errors.printMessage(S.FittedModel, "elmntav", F.list(names.get(i)), engine);
        }
        values.append(value);
      }
      return values;
    }
    IAST variables = fitVariables();
    if (variables.isPresent() && !ast.arg1().isString()) {
      if (ast.exists(x -> x.isList())) {
        // a list of values is not threaded over
        return F.NIL;
      }
      if (ast.argSize() != variables.argSize()) {
        // `1` called with `2` arguments; `3` arguments are expected.
        return Errors.printMessage(S.FittedModel, "argrx", F.list(F.stringx("FittedModel[…]"),
            F.ZZ(ast.argSize()), F.ZZ(variables.argSize())), engine);
      }
      // the best fit function at a point
      IASTAppendable rules = F.ListAlloc(variables.argSize());
      for (int i = 1; i <= variables.argSize(); i++) {
        rules.append(F.Rule(variables.get(i), ast.get(i)));
      }
      return engine.evaluate(F.subst(normal(false), rules));
    }
    return F.NIL;
  }

  /**
   * The property name closest to one that was not recognised - <code>"Properties"</code> itself
   * included, which is what a misspelt request for the list of names most likely was - or
   * <code>null</code> if more than a third of its letters would have to change.
   */
  private String nearestProperty(String name) {
    String[] names = Arrays.copyOf(propertyNames(), propertyNames().length + 1);
    names[names.length - 1] = "Properties";
    LevenshteinDistance distance = LevenshteinDistance.getDefaultInstance();
    String nearest = names[0];
    int best = Integer.MAX_VALUE;
    for (String candidate : names) {
      int d = distance.apply(name, candidate);
      if (d < best) {
        best = d;
        nearest = candidate;
      }
    }
    return 3 * best <= name.length() ? nearest : null;
  }

  /** A machine matrix as a nested list. */
  protected static IAST matrixList(double[][] matrix) {
    return F.mapRange(0, matrix.length,
        i -> F.mapRange(0, matrix[i].length, j -> F.num(matrix[i][j])));
  }

  /**
   * The confidence region of the parameters: <code>FittedModels`ParameterEllipsoid(center, radii,
   * directions)</code>, the axes of the ellipsoid along the eigenvectors of the covariance matrix
   * and the radii the square roots of its eigenvalues times <code>quantile</code>.
   *
   * @param center the estimates of the parameters
   * @param covariance the covariance matrix of the estimates
   * @param quantile the square of the radius of the region in standard deviations
   */
  protected static IExpr parameterEllipsoid(double[] center, double[][] covariance,
      double quantile, EvalEngine engine) {
    IExpr system = S.Eigensystem.of(engine, matrixList(covariance));
    if (!system.isList2() || !system.first().isList()) {
      return F.NIL;
    }
    IAST values = (IAST) system.first();
    IAST radii = F.mapRange(1, values.size(),
        i -> F.num(Math.sqrt(Math.max(0.0, values.get(i).evalf()) * quantile)));
    ISymbol ellipsoid = ContextPath.getSymbol("ParameterEllipsoid",
        engine.getContextPath().getContext("FittedModels`"), false);
    return F.ternaryAST3(ellipsoid, F.List(center), radii, system.second());
  }

  /**
   * The confidence band <code>{f - q Sqrt(v), f + q Sqrt(v)}</code> of a prediction, where the
   * variance <code>v = s2 + g.covariance.g</code> of a single prediction (or of the mean prediction
   * without <code>s2</code>) is a function of the independent variables through the gradient
   * <code>g</code> of the model by its parameters.
   *
   * @param bestFit the best fit function <code>f</code>
   * @param gradient the derivatives of the model by its parameters, at the best fit
   * @param covariance the covariance matrix of the parameters
   * @param variance <code>s2</code>, or <code>0</code> for the mean prediction
   * @param df the residual degrees of freedom for the quantile <code>q</code> of Student's t
   *        distribution at the 95% level
   */
  protected static IExpr predictionBands(IExpr bestFit, IAST gradient, double[][] covariance,
      double variance, int df, EvalEngine engine) {
    if (df <= 0) {
      return F.List(S.Indeterminate, S.Indeterminate);
    }
    IASTAppendable sum = F.PlusAlloc(gradient.argSize() * gradient.argSize() + 1);
    if (variance != 0.0) {
      sum.append(F.num(variance));
    }
    for (int i = 0; i < gradient.argSize(); i++) {
      for (int j = 0; j < gradient.argSize(); j++) {
        sum.append(F.Times(F.num(covariance[i][j]), gradient.get(i + 1), gradient.get(j + 1)));
      }
    }
    double q = new TDistribution(df).inverseCumulativeProbability(0.975);
    IExpr halfWidth = engine.evaluate(F.Times(F.num(q), F.Sqrt(engine.evaluate(sum))));
    return engine.evaluate(F.List(F.Subtract(bestFit, halfWidth), F.Plus(bestFit, halfWidth)));
  }

  @Override
  public int hierarchy() {
    return FITTEDMODELID;
  }

  /**
   * A table of the fitted model in a dialog style: the first column left-aligned, gray lines below
   * the header row and after the first column.
   *
   * @param grid the rows of the table, the header row first
   */
  protected static IExpr dialogGrid(IAST grid) {
    IExpr gray = F.GrayLevel(F.num(0.7));
    return F.binaryAST2(S.Style,
        F.function(S.Grid, grid, F.Rule(S.Alignment, F.List(S.Left, S.Automatic)),
            F.Rule(S.Dividers, F.List(F.List(F.Rule(F.C2, gray)), F.List(F.Rule(F.C2, gray)))),
            F.Rule(S.Spacings,
                F.List(F.List(F.Rule(F.C2, F.C1)), F.List(F.Rule(F.C2, F.num(0.75)))))),
        F.stringx("DialogStyle"));
  }

  /**
   * One row <code>{DF, SS, ...}</code> of an analysis of variance. The degrees of freedom are whole
   * numbers; a value which cannot be computed is <code>Indeterminate</code>, and an infinite one -
   * the F-statistic of an exact fit - is <code>ComplexInfinity</code>.
   */
  protected static IAST anovaRow(double[] row) {
    IASTAppendable list = F.ListAlloc(row.length);
    // the degrees of freedom are whole numbers
    list.append(F.ZZ((int) row[0]));
    for (int j = 1; j < row.length; j++) {
      // an exact fit leaves no error, and its F-statistic is infinite
      list.append(Double.isNaN(row[j]) ? S.Indeterminate
          : Double.isInfinite(row[j]) ? F.CInfinity : F.num(row[j]));
    }
    return list;
  }

  /** One column of the analysis of variance <code>rows</code>, for each row that has it. */
  protected static IExpr anovaColumn(double[][] rows, int column) {
    IASTAppendable list = F.ListAlloc(rows.length);
    for (double[] row : rows) {
      if (column < row.length) {
        list.append(anovaRow(row).get(column + 1));
      }
    }
    return list;
  }

  /**
   * The row <code>{estimate, standard error, t, p}</code> of one parameter, with the two-tailed
   * p-value of Student's t distribution <code>t</code>; without a positive standard error, a
   * numeric estimate or a distribution everything but the estimate is <code>Indeterminate</code>.
   */
  protected static IExpr[] statisticsRow(double estimate, double error, TDistribution t) {
    IExpr[] row = new IExpr[4];
    row[0] = F.num(estimate);
    if (!(error > 0.0) || Double.isNaN(estimate) || t == null) {
      row[1] = row[2] = row[3] = S.Indeterminate;
      return row;
    }
    double statistic = estimate / error;
    row[1] = F.num(error);
    row[2] = F.num(statistic);
    row[3] = F.num(2.0 * t.cumulativeProbability(-Math.abs(statistic)));
    return row;
  }

  /**
   * The parameter table: a header row and one row per parameter, its label followed by its
   * statistics.
   *
   * @param rows the parameter statistics, one row <code>{estimate, standard error, t, p}</code> per
   *        parameter
   * @param label the label of the parameter with the given 0-based index
   */
  protected static IExpr parameterTable(IExpr[][] rows, IntFunction<IExpr> label) {
    return parameterTable(rows, label, "t\u2010Statistic");
  }

  /**
   * The parameter table with the given header of the test statistic column.
   *
   * @see #parameterTable(IExpr[][], IntFunction)
   */
  protected static IExpr parameterTable(IExpr[][] rows, IntFunction<IExpr> label,
      String statistic) {
    IASTAppendable grid = F.ListAlloc(rows.length + 1);
    grid.append(F.List(F.stringx(""), F.stringx("Estimate"), F.stringx("Standard Error"),
        F.stringx(statistic), F.stringx("P\u2010Value")));
    for (int i = 0; i < rows.length; i++) {
      IASTAppendable row = F.ListAlloc(5);
      row.append(label.apply(i));
      row.appendAll(rows[i], 0, 4);
      grid.append(row);
    }
    return dialogGrid(grid);
  }

  /**
   * One column of the parameter statistics <code>rows</code>, one row
   * <code>{estimate, standard error, t, p}</code> per parameter.
   */
  protected static IAST parameterColumn(IExpr[][] rows, int column) {
    return F.mapRange(0, rows.length, i -> rows[i][column]);
  }

  /**
   * <code>{estimate - q se, estimate + q se}</code> for each row of the parameter statistics
   * <code>rows</code>, with q the quantile of Student's t distribution with <code>df</code> degrees
   * of freedom; <code>{Indeterminate, Indeterminate}</code> where the standard error is unknown.
   */
  protected static IAST confidenceIntervals(IExpr[][] rows, int df, double level) {
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
}

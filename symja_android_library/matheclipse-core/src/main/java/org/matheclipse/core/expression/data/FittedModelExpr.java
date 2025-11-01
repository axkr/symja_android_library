package org.matheclipse.core.expression.data;

import java.util.Arrays;
import org.hipparchus.linear.Array2DRowFieldMatrix;
import org.hipparchus.linear.ArrayFieldVector;
import org.hipparchus.linear.FieldMatrix;
import org.hipparchus.linear.FieldVector;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.ExprOLSLinearRegression;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

public class FittedModelExpr extends DataExpr<ExprOLSLinearRegression> {

  /** */
  private static final long serialVersionUID = -2779698690575246663L;

  private static double[][] designMatrixNumeric(double[][] matrix, IAST basisFunctions,
      IAST variables, org.matheclipse.core.eval.EvalEngine engine) {
    int numberOfPoints = matrix.length;
    int numberOfFunctions = basisFunctions.argSize();
    int numberOfVariables = variables.argSize();
    double[][] x = new double[numberOfPoints][numberOfFunctions];

    // --- data transfer (create matrix x and vector y) ---
    for (int i = 0; i < numberOfPoints; i++) {
      double[] valuesList = matrix[i];
      double[] xValuesList = Arrays.copyOf(valuesList, valuesList.length - 1);

      if (xValuesList.length != numberOfVariables) {
        return null;
      }

      IASTAppendable substitutionRules = F.ListAlloc(numberOfVariables);
      for (int varIdx = 0; varIdx < numberOfVariables; varIdx++) {
        substitutionRules.append(F.Rule(variables.get(varIdx + 1), F.num(valuesList[varIdx])));
      }

      for (int j = 0; j < numberOfFunctions; j++) {
        IExpr basisFunction = basisFunctions.get(j + 1);
        IExpr substitutedFunction = F.subst(basisFunction, substitutionRules);
        IExpr value = engine.evaluate(substitutedFunction);

        try {
          x[i][j] = value.evalf();
        } catch (Exception e) {
          Errors.printMessage(S.LinearModelFit, e, engine);
          return null;
        }
      }
    }
    return x;
  }

  public static FieldMatrix<IExpr> designMatrixSymbolic(FieldMatrix<IExpr> matrix,
      FieldVector<IExpr> basisFunctions, FieldVector<IExpr> vars,
      org.matheclipse.core.eval.EvalEngine engine) {
    int nobs = matrix.getRowDimension();
    int nvars = basisFunctions.getDimension();
    final int cols = nvars;
    FieldMatrix<IExpr> resultMatrix = new Array2DRowFieldMatrix<IExpr>(F.EXPR_FIELD, nobs, cols);
    for (int i = 0; i < nobs; i++) {
      FieldVector<IExpr> matrixRow = matrix.getRowVector(i);
      FieldVector<IExpr> row = new ArrayFieldVector<IExpr>(F.EXPR_FIELD, cols);
      IASTAppendable substitutionRules = F.ListAlloc(nvars);
      for (int varIdx = 0; varIdx < vars.getDimension(); varIdx++) {
        substitutionRules.append(F.Rule(vars.getEntry(varIdx), matrixRow.getEntry(varIdx)));
      }
      for (int j = 0; j < cols; j++) {
        IExpr basisFunction = basisFunctions.getEntry(j);
        IExpr substitutedFunction = F.subst(basisFunction, substitutionRules);
        IExpr value = engine.evaluate(substitutedFunction);
        row.setEntry(j, value);
      }
      resultMatrix.setRowVector(i, row);
    }
    return resultMatrix;
  }

  public static IAST designMatrixSymbolic(IExpr matrix, IAST basisFunctions, IAST variables,
      EvalEngine engine) {
    FieldMatrix<IExpr> m = Convert.list2Matrix(matrix);
    FieldVector<IExpr> basis = Convert.list2Vector(basisFunctions);
    FieldVector<IExpr> vars = Convert.list2Vector(variables);
    FieldMatrix<IExpr> designMatrixSymbolic = designMatrixSymbolic(m, basis, vars, engine);
    return Convert.matrix2List(designMatrixSymbolic, false);
  }

  /**
   * Perform linear model fitting using Hipparchus OLS regression.
   * 
   * @param matrix the data-points in matrix form; last entry in each row is the y-value; the
   *        entries before are the x-value data points
   * @param basisFunctions the basis functions as a list of IExpr
   * @param variables the variables as a list of IExpr
   * @param noIntercept whether to include an intercept term
   * @param engine the EvalEngine for evaluation
   * @return a FittedModelExpr containing the regression results or an error message
   */
  public static IExpr linearModelFit(double[][] matrix, IAST basisFunctions, IAST variables,
      boolean noIntercept, EvalEngine engine) {
    try {
      int numberOfPoints = matrix.length;
      double[] y = new double[numberOfPoints];
      for (int i = 0; i < numberOfPoints; i++) {
        double[] valuesList = matrix[i];
        // extract y-value
        y[i] = valuesList[valuesList.length - 1];
      }

      double[][] x = designMatrixNumeric(matrix, basisFunctions, variables, engine);
      if (x != null) {
        ExprOLSLinearRegression regression = new ExprOLSLinearRegression();
        regression.setNoIntercept(noIntercept);
        FieldVector<IExpr> yv = Convert.list2Vector(y);
        FieldMatrix<IExpr> xm = Convert.list2Matrix(x);
        regression.newSampleData(yv, xm);
        FieldVector<IExpr> basis = Convert.list2Vector(basisFunctions);
        return FittedModelExpr.newInstance(regression, basis);
      }
      return F.NIL;
    } catch (Exception e) {
      if (Config.SHOW_STACKTRACE) {
        e.printStackTrace();
      }
      return Errors.printMessage(S.LinearModelFit, e, engine);
    }
  }

  /**
   * Perform linear model fitting using Hipparchus OLS regression.
   * 
   * @param matrix the data-points in matrix form; last entry in each row is the y-value; the
   *        entries before are the x-value data points
   * @param basisFunctions the basis functions as a list of IExpr
   * @param variables the variables as a list of IExpr
   * @param noIntercept whether to include an intercept term
   * @param engine the EvalEngine for evaluation
   * @return a FittedModelExpr containing the regression results or an error message
   */
  public static IExpr linearModelFit(FieldMatrix<IExpr> matrix, FieldVector<IExpr> basisFunctions,
      FieldVector<IExpr> variables, boolean noIntercept,
      org.matheclipse.core.eval.EvalEngine engine) {
    try {
      int numberOfPoints = matrix.getRowDimension();
      FieldVector<IExpr> y = new ArrayFieldVector<IExpr>(F.EXPR_FIELD, numberOfPoints);
      for (int i = 0; i < numberOfPoints; i++) {
        FieldVector<IExpr> valuesList = matrix.getRowVector(i);
        // extract y-value
        y.setEntry(i, valuesList.getEntry(valuesList.getDimension() - 1));
      }
      FieldMatrix<IExpr> designMatrix =
          designMatrixSymbolic(matrix, basisFunctions, variables, engine);
      ExprOLSLinearRegression regression = new ExprOLSLinearRegression();
      regression.setNoIntercept(noIntercept);
      regression.newSampleData(y, designMatrix);
      return newInstance(regression, basisFunctions);
      // double[][] x = designMatrixNumeric(matrix, basisFunctions, variables, numberOfPoints,
      // engine);
      // if (x != null) {
      // OLSSymbolicLinearRegression regression = new OLSSymbolicLinearRegression();
      // regression.setNoIntercept(noIntercept);
      // regression.newSampleData(n, x);
      // return newInstance(regression, basisFunctions);
      // }
      // return F.NIL;
    } catch (Exception e) {
      if (Config.SHOW_STACKTRACE) {
        e.printStackTrace();
      }
      return Errors.printMessage(S.LinearModelFit, e, engine);
    }
  }

  public static IExpr linearModelFit(IExpr designMatrix, IExpr responseVector) {
    int[] dims = designMatrix.isMatrix(false);
    int vLength = responseVector.isVector();
    if (vLength > 0 && dims != null && dims[0] == vLength) {
      double[][] doubleMatrix = designMatrix.toDoubleMatrix(false);
      double[] doubleVector = responseVector.toDoubleVector();
      if (doubleMatrix != null && doubleVector != null) {
        // numeric solution
        ExprOLSLinearRegression regression = new ExprOLSLinearRegression();
        regression.setNoIntercept(true);
        FieldMatrix<IExpr> designFieldMatrix = Convert.list2Matrix(doubleMatrix);
        FieldVector<IExpr> responseFieldVector = Convert.list2Vector(doubleVector);
        FittedModelExpr model = newInstance(designFieldMatrix, responseFieldVector, dims);
        if (model != null) {
          return model;
        }
      }
      // symbolic solution
      FieldMatrix<IExpr> designFieldMatrix = Convert.list2Matrix(designMatrix);
      FieldVector<IExpr> responseFieldVector = Convert.list2Vector(responseVector);
      FittedModelExpr model = newInstance(designFieldMatrix, responseFieldVector, dims);
      if (model != null) {
        return model;
      }
    }
    return F.NIL;
  }

  public static FittedModelExpr newInstance(final FieldMatrix<IExpr> designMatrix,
      final FieldVector<IExpr> responseVector, int[] matrixDimension) {
    ExprOLSLinearRegression regression = new ExprOLSLinearRegression();
    regression.setNoIntercept(true);
    regression.newSampleData(responseVector, designMatrix);
    // use slots as basis functions placeholder
    FieldVector<IExpr> basisFunctions =
        new ArrayFieldVector<IExpr>(F.EXPR_FIELD, matrixDimension[1]);
    for (int i = 0; i < matrixDimension[1]; i++) {
      basisFunctions.setEntry(i, F.Slot(i + 1));
    }
    return newInstance(regression, basisFunctions);
  }

  public static FittedModelExpr newInstance(final ExprOLSLinearRegression value,
      FieldVector<IExpr> basisFunctions) {
    return new FittedModelExpr(value, basisFunctions);
  }

  private final FieldVector<IExpr> basisFunctions;

  protected FittedModelExpr(final ExprOLSLinearRegression function,
      FieldVector<IExpr> basisFunctions) {
    super(S.FittedModel, function);
    this.basisFunctions = basisFunctions;
  }

  @Override
  public IExpr copy() {
    return new FittedModelExpr(fData, basisFunctions);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof FittedModelExpr) {
      return fData.equals(((FittedModelExpr) obj).fData);
    }
    return false;
  }

  public IExpr evaluate(IAST ast, EvalEngine engine) {
    if (ast.head() instanceof FittedModelExpr && ast.isAST1()) {
      IExpr arg1 = ast.arg1();
      String property = arg1.toString();
      if (arg1.isString()) {
        ExprOLSLinearRegression regression = toData();
        switch (property) {
          case "AdjustedRSquared":
            return F.num(regression.calculateAdjustedRSquared());
          case "BestFit":
            return normal(false);
          case "BestFitParameters":
            return F.List(regression.estimateRegressionParameters());
          case "EstimatedVariance":
            return regression.estimateErrorVariance();
          case "FitResiduals":
            return F.List(regression.estimateResiduals());
          case "ParameterErrors":
            return F.List(regression.estimateRegressionParametersStandardErrors());
          case "RSquared":
            return F.num(regression.calculateRSquared());
          default:
            // Fall through to return F.NIL for unhandled properties
            break;
        }
      }
    }
    return F.NIL;
  }

  @Override
  public int hashCode() {
    return (fData == null) ? 461 : 461 + fData.hashCode();
  }

  @Override
  public int hierarchy() {
    return FITTEDMODELID;
  }

  @Override
  public IExpr normal(boolean nilIfUnevaluated) {
    ExprOLSLinearRegression model = toData();
    int numberOfFunctions = basisFunctions.getDimension();
    IExpr[] coefficients = model.estimateRegressionParameters();
    IASTAppendable fittedFunction = F.PlusAlloc(numberOfFunctions);
    for (int j = 0; j < numberOfFunctions; j++) {
      fittedFunction.append(F.Times(coefficients[j], basisFunctions.getEntry(j)));
    }
    // simplify function by evaluation (ex: 2.0*1 + 3.0*x -> 2.0 + 3.0*x)
    return EvalEngine.get().evaluate(fittedFunction);
  }

  @Override
  public String toString() {
    return "FittedModel[" + normal(false) + "]";
  }
}

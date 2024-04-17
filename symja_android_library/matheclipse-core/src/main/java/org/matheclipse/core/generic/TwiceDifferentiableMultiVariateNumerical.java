package org.matheclipse.core.generic;

import java.util.function.Function;
import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.ArrayRealVector;
import org.hipparchus.linear.FieldMatrix;
import org.hipparchus.linear.RealMatrix;
import org.hipparchus.linear.RealVector;
import org.hipparchus.optim.nonlinear.vector.constrained.TwiceDifferentiableFunction;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class TwiceDifferentiableMultiVariateNumerical extends TwiceDifferentiableFunction {
  final IExpr fFunction;
  final IAST fGradientFunctions;
  final FieldMatrix<IExpr> fHessianMatrix;
  final IAST fVariableList;
  final EvalEngine fEngine;
  final Object2IntOpenHashMap<IExpr> fVariableIndex;

  /**
   * Create a multivariate twice differentiable vectorial function.
   * 
   * @param function the (multivariate-) function
   * @param variablesList
   * @param useAbsReal substitute {@link S#Abs} with {@link S#AbsReal} function, because of assuming
   *        real input values
   */
  public TwiceDifferentiableMultiVariateNumerical(final IExpr function, final IAST variablesList,
      boolean useAbsReal) {
    this(function, variablesList, useAbsReal, EvalEngine.get());
  }

  /**
   * Create a multivariate twice differentiable vectorial function.
   * 
   * @param function the (multivariate-) function
   * @param variablesList
   * @param useAbsReal substitute {@link S#Abs} with {@link S#AbsReal} function, because of assuming
   *        real input values
   * @param engine
   */
  public TwiceDifferentiableMultiVariateNumerical(final IExpr function, final IAST variablesList,
      boolean useAbsReal, final EvalEngine engine) {
    if (useAbsReal) {
      fFunction = F.subst(function, x -> x == S.Abs ? S.RealAbs : F.NIL);
    } else {
      fFunction = function;
    }
    fVariableList = variablesList;
    fVariableIndex = new Object2IntOpenHashMap<IExpr>(fVariableList.argSize());
    for (int i = 1; i < fVariableList.size(); i++) {
      fVariableIndex.put(variablesList.get(i), i);
    }
    fEngine = engine;

    IExpr gradientList = S.Grad.of(engine, fFunction, fVariableList);
    if (gradientList.isList() && gradientList.size() >= variablesList.size()) {
      fGradientFunctions = (IAST) gradientList;
    } else {
      // Cannot assign to raw object `1`.
      throw new ArgumentTypeException(
          Errors.getMessage("setraw", F.list(gradientList), EvalEngine.get()));
    }
    IExpr hessianMatrix = S.HessianMatrix.of(engine, fFunction, fVariableList);
    int[] dimensions = hessianMatrix.isMatrix();

    if (dimensions != null) {
      fHessianMatrix = Convert.list2Matrix(hessianMatrix);
    } else {
      fHessianMatrix = null;
    }
    if (dimensions == null || fHessianMatrix == null) {
      // Cannot assign to raw object `1`.
      throw new ArgumentTypeException(
          Errors.getMessage("setraw", F.list(hessianMatrix), EvalEngine.get()));
    }
    variablesList.exists(x -> {
      if (!x.isVariable() || x.isBuiltInSymbol()) {
        // Cannot assign to raw object `1`.
        throw new ArgumentTypeException(Errors.getMessage("setraw", F.list(x), EvalEngine.get()));
      }
      return false;
    });


    variablesList.exists(x -> {
      if (!x.isVariable() || x.isBuiltInSymbol()) {
        // Cannot assign to raw object `1`.
        throw new ArgumentTypeException(Errors.getMessage("setraw", F.list(x), EvalEngine.get()));
      }
      return false;
    });
  }

  @Override
  public int dim() {
    return fVariableList.argSize();
  }

  @Override
  public RealVector gradient(RealVector v) {
    Function<IExpr, IExpr> function = x -> {
      int indx = fVariableIndex.getInt(x);
      if (indx > 0) {
        return F.num(v.getEntry(indx - 1));
      }
      return F.NIL;
    };
    double[] result = new double[fGradientFunctions.argSize()];
    for (int i = 1; i < fGradientFunctions.size(); i++) {
      result[i - 1] = fGradientFunctions.get(i).evalf(function);
    }
    return new ArrayRealVector(result, false);
  }

  @Override
  public RealMatrix hessian(RealVector v) {
    Function<IExpr, IExpr> function = x -> {
      int indx = fVariableIndex.getInt(x);
      if (indx > 0) {
        return F.num(v.getEntry(indx - 1));
      }
      return F.NIL;
    };
    final int rowDimension = fHessianMatrix.getRowDimension();
    final int columnDimension = fHessianMatrix.getColumnDimension();
    double[][] result = new double[rowDimension][columnDimension];
    for (int i = 0; i < rowDimension; i++) {
      for (int j = 0; j < columnDimension; j++) {
        result[i][j] = fHessianMatrix.getEntry(i, j).evalf(function);
      }
    }
    return new Array2DRowRealMatrix(result, false);
  }

  @Override
  public double value(RealVector v) {
    try {
      Function<IExpr, IExpr> function = x -> {
        int i = fVariableIndex.getInt(x);
        if (i > 0) {
          return F.num(v.getEntry(i - 1));
        }
        return F.NIL;
      };
      return fFunction.evalf(function);
    } catch (RuntimeException rex) {
      return Double.NaN;
    }
  }

}

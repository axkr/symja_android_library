package org.matheclipse.core.generic;

import java.util.function.Function;
import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.ArrayRealVector;
import org.hipparchus.linear.RealMatrix;
import org.hipparchus.linear.RealVector;
import org.hipparchus.optim.nonlinear.vector.leastsquares.MultivariateJacobianFunction;
import org.hipparchus.util.Pair;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

/**
 * A class for functions that compute a vector of values and can compute their derivatives (Jacobian
 * marix).
 *
 * <p>
 * See:
 * <ul>
 * <a href="https://www.hipparchus.org/hipparchus-optim/leastsquares.html">Hipparchus -
 * Optimization</a>
 * <ul>
 * <a href="https://en.wikipedia.org/wiki/Jacobian_matrix_and_determinant">Wikipedia - Jacobian
 * matrix</a>
 */
public class MultivariateJacobianGradient implements MultivariateJacobianFunction {
  final IAST fFunction;
  final IAST fGradientFunctions;
  final IAST fVariableList;
  final EvalEngine fEngine;
  final Object2IntOpenHashMap<IExpr> fIndexMap;

  public MultivariateJacobianGradient(final IAST function, final IAST variablesList) {
    this(function, variablesList, EvalEngine.get());
  }

  public MultivariateJacobianGradient(final IAST function, final IAST variablesList,
      final EvalEngine engine) {
    fVariableList = variablesList;
    fIndexMap = new Object2IntOpenHashMap<IExpr>(fVariableList.argSize());
    for (int i = 1; i < fVariableList.size(); i++) {
      fIndexMap.put(variablesList.get(i), i);
    }
    fEngine = engine;
    fFunction = function;
    IExpr gradientList = S.Grad.of(engine, function, fVariableList);
    if (gradientList.isList() && gradientList.size() >= variablesList.size()) {
      fGradientFunctions = (IAST) gradientList;
    } else {
      // Cannot assign to raw object `1`.
      throw new ArgumentTypeException(
          IOFunctions.getMessage("setraw", F.list(gradientList), EvalEngine.get()));
    }
    variablesList.exists(x -> {
      if (!x.isVariable() || x.isBuiltInSymbol()) {
        // Cannot assign to raw object `1`.
        throw new ArgumentTypeException(
            IOFunctions.getMessage("setraw", F.list(x), EvalEngine.get()));
      }
      return false;
    });

  }

  public IAST getFunction() {
    return fFunction;
  }

  public IAST getJacobianMatrix() {
    return fGradientFunctions;
  }

  /**
   * Compute the function value and its Jacobian.
   *
   * @param point point at which the function and the Jacobian matrix must be evaluated
   * @return the function values and their Jacobian of this vector valued function.
   */
  @Override
  public Pair<RealVector, RealMatrix> value(RealVector point) {
    final int rowLength = fGradientFunctions.argSize();
    final int columnLength = fVariableList.argSize();

    RealVector value = new ArrayRealVector(rowLength);
    RealMatrix jacobian = new Array2DRowRealMatrix(rowLength, columnLength);
    for (int i = 1; i < rowLength + 1; i++) {
      IAST row = (IAST) fGradientFunctions.get(i);
      Function<IExpr, IExpr> function = x -> {
        int intValue = fIndexMap.getInt(x);
        if (intValue > 0) {
          return F.num(point.getEntry(intValue - 1));
        }
        return F.NIL;
      };
      value.setEntry(i - 1, fFunction.get(i).evalf(function));
      for (int j = 1; j < row.size(); j++) {
        try {
          double entry = row.get(j).evalf(function);
          jacobian.setEntry(i - 1, j - 1, entry);
        } catch (RuntimeException rex) {
          jacobian.setEntry(i - 1, j - 1, Double.NaN);
        }
      }
    }
    return new Pair<RealVector, RealMatrix>(value, jacobian);
  }


}

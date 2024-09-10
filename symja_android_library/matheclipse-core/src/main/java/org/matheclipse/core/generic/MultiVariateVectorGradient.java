package org.matheclipse.core.generic;

import java.util.function.Function;
import org.hipparchus.analysis.MultivariateVectorFunction;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

/**
 * A class representing a multivariate vectorial function.
 */
public final class MultiVariateVectorGradient implements MultivariateVectorFunction {
  final IExpr fFunction;
  final IAST fGradientFunctions;
  final IAST fVariableList;
  final EvalEngine fEngine;
  final Object2IntOpenHashMap<IExpr> fIndexMap;

  /**
   * Create a multivariate vectorial function.
   * 
   * @param function the (multivariate-) function
   * @param variablesList
   * @param useAbsReal substitute {@link S#Abs} with {@link S#AbsReal} function, because of assuming
   *        real input values
   */
  public MultiVariateVectorGradient(final IExpr function, final IAST variablesList,
      boolean useAbsReal) {
    this(function, variablesList, useAbsReal, EvalEngine.get());
  }

  /**
   * Create a multivariate vectorial function.
   * 
   * @param function the (multivariate-) function
   * @param variablesList
   * @param useAbsReal substitute {@link S#Abs} with {@link S#AbsReal} function, because of assuming
   *        real input values
   * @param engine
   */
  public MultiVariateVectorGradient(final IExpr function, final IAST variablesList,
      boolean useAbsReal, final EvalEngine engine) {

    fVariableList = variablesList;
    fIndexMap = new Object2IntOpenHashMap<IExpr>(fVariableList.argSize());
    for (int i = 1; i < fVariableList.size(); i++) {
      fIndexMap.put(variablesList.get(i), i);
    }
    fEngine = engine;
    if (useAbsReal) {
      fFunction = F.subst(function, x -> x == S.Abs, S.RealAbs);
    } else {
      fFunction = function;
    }
    IExpr gradientList = S.Grad.of(engine, fFunction, fVariableList);
    if (gradientList.isList() && gradientList.size() == variablesList.size()) {
      fGradientFunctions = (IAST) gradientList;
    } else {
      // Cannot assign to raw object `1`.
      throw new ArgumentTypeException(
          Errors.getMessage("setraw", F.list(gradientList), EvalEngine.get()));
    }
    variablesList.exists(x -> {
      if (!x.isVariable() || x.isBuiltInSymbol()) {
        // Cannot assign to raw object `1`.
        throw new ArgumentTypeException(Errors.getMessage("setraw", F.list(x), EvalEngine.get()));
      }
      return false;
    });

  }

  /**
   * Compute the gradient value for the function at the given point.
   * 
   * @param point point at which the function must be evaluated
   * @return function gradient value for the given point
   */
  @Override
  public double[] value(double[] point) throws IllegalArgumentException {
    double result[] = new double[point.length];
    Function<IExpr, IExpr> function = x -> {
      int i = fIndexMap.getInt(x);
      if (i > 0) {
        return F.num(point[i - 1]);
      }
      return F.NIL;
    };
    for (int i = 1; i < fGradientFunctions.size(); i++) {
      try {
        result[i - 1] = fGradientFunctions.get(i).evalf(function);
      } catch (RuntimeException rex) {
        result[i - 1] = Double.NaN;
      }
    }

    return result;
  }

}

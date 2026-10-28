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
import org.matheclipse.external.fastutil.objects.Object2IntOpenHashMap;

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
   * @param useAbsReal substitute {@link S#Abs} with {@link S#RealAbs} function, because of assuming
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
   * @param useAbsReal substitute {@link S#Abs} with {@link S#RealAbs} function, because of assuming
   *        real input values
   * @param engine
   */
  public MultiVariateVectorGradient(final IExpr function, final IAST variablesList,
      boolean useAbsReal, final EvalEngine engine) {

    fVariableList = variablesList;
    fIndexMap = variableIndexMap(variablesList);
    fEngine = engine;
    if (useAbsReal) {
      fFunction = F.substAbs(function);
    } else {
      fFunction = function;
    }
    IExpr gradientList = S.Grad.of(engine, fFunction, fVariableList);
    if (gradientList.isList() && gradientList.size() == variablesList.size()) {
      fGradientFunctions = (IAST) gradientList;
    } else {
      throw rawObjectException(gradientList);
    }
    checkVariables(variablesList);
  }

  /**
   * Map each variable of <code>variablesList</code> to its (1-based) position.
   *
   * @param variablesList the list of variables
   * @return the map from variable to index
   */
  static Object2IntOpenHashMap<IExpr> variableIndexMap(final IAST variablesList) {
    Object2IntOpenHashMap<IExpr> indexMap =
        new Object2IntOpenHashMap<IExpr>(variablesList.argSize());
    for (int i = 1; i < variablesList.size(); i++) {
      indexMap.put(variablesList.get(i), i);
    }
    return indexMap;
  }

  /**
   * Check that every entry of <code>variablesList</code> is a variable and not a built-in symbol.
   *
   * @param variablesList the list of variables
   * @throws ArgumentTypeException for the first entry which is not an assignable variable
   */
  static void checkVariables(final IAST variablesList) {
    variablesList.exists(x -> {
      if (!x.isVariable() || x.isBuiltInSymbol()) {
        throw rawObjectException(x);
      }
      return false;
    });
  }

  /**
   * The <code>setraw</code> exception for <code>expr</code>.
   *
   * @param expr the expression which cannot be used as a variable or gradient list
   * @return the exception to throw
   */
  static ArgumentTypeException rawObjectException(final IExpr expr) {
    // Cannot assign to raw object `1`.
    return new ArgumentTypeException(Errors.getMessage("setraw", F.list(expr), EvalEngine.get()));
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
      result[i - 1] = fGradientFunctions.get(i).evalfNaN(function);
    }

    return result;
  }

}

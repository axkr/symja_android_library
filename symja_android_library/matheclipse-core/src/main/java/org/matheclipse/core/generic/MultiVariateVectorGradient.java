package org.matheclipse.core.generic;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.hipparchus.analysis.MultivariateVectorFunction;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class MultiVariateVectorGradient implements MultivariateVectorFunction {
  final IExpr fFunction;
  final IAST fGradientFunctions;
  final IAST fVariableList;
  final EvalEngine fEngine;
  final Map<IExpr, Integer> indexMap;

  public MultiVariateVectorGradient(final IExpr function, final IAST variablesList) {
    this(function, variablesList, EvalEngine.get());
  }

  public MultiVariateVectorGradient(final IExpr function, final IAST variablesList,
      final EvalEngine engine) {

    fVariableList = variablesList;
    indexMap = new HashMap<IExpr, Integer>();
    for (int i = 1; i < fVariableList.size(); i++) {
      indexMap.put(variablesList.get(i), i);
    }
    fEngine = engine;
    fFunction = function;
    IExpr gradientList = S.Grad.of(engine, function, fVariableList);
    if (gradientList.isList() && gradientList.size() == variablesList.size()) {
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

  @Override
  public double[] value(double[] point) throws IllegalArgumentException {
    double result[] = new double[point.length];
    Function<IExpr, IExpr> function = x -> {
      Integer i = indexMap.get(x);
      if (i != null) {
        return F.num(point[i - 1]);
      }
      return F.NIL;
    };
    // IASTAppendable rules =
    // F.mapRange(0, point.length, i -> F.Rule(fVariableList.get(i + 1), F.num(point[i])));
    for (int i = 1; i < fGradientFunctions.size(); i++) {
      try {
        // IExpr temp = F.subst(fGradientFunctions.get(i), rules);
        result[i - 1] = fGradientFunctions.get(i).evalf(function);
      } catch (RuntimeException rex) {
        result[i - 1] = Double.NaN;
      }
    }

    return result;
  }

}

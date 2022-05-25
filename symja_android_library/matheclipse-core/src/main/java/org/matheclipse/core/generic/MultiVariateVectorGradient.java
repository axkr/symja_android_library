package org.matheclipse.core.generic;

import org.hipparchus.analysis.MultivariateVectorFunction;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.DoubleStackEvaluator;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

public class MultiVariateVectorGradient implements MultivariateVectorFunction {
  final IExpr fFunction;
  final IAST fGradientFunctions;
  final IAST fVariableList;
  final EvalEngine fEngine;

  public MultiVariateVectorGradient(final IExpr function, final IAST variablesList) {
    this(function, variablesList, EvalEngine.get());
  }

  public MultiVariateVectorGradient(final IExpr function, final IAST variablesList,
      final EvalEngine engine) {

    fVariableList = variablesList;
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
    final double[] stack = new double[10];
    try {
      IASTAppendable rules =
          F.mapRange(0, point.length, i -> F.Rule(fVariableList.get(i + 1), F.num(point[i])));
      for (int i = 1; i < fGradientFunctions.size(); i++) {
        IExpr temp = F.subst(fGradientFunctions.get(i), rules);
        double d = DoubleStackEvaluator.eval(stack, 0, temp);
        result[i - 1] = d;
      }
    } catch (RuntimeException rex) {
      return null;
    }
    return result;
  }

}

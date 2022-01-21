package org.matheclipse.core.generic;

import org.hipparchus.analysis.MultivariateFunction;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.DoubleStackEvaluator;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

public class MultiVariateFunction implements MultivariateFunction {
  final IExpr fFunction;
  final IAST fVariableList;
  final EvalEngine fEngine;

  public MultiVariateFunction(final IExpr function, final IAST variablesList) {
    this(function, variablesList, EvalEngine.get());
  }

  public MultiVariateFunction(final IExpr function, final IAST variablesList,
      final EvalEngine engine) {
    fFunction = function;
    fVariableList = variablesList;
    fEngine = engine;
    variablesList.exists(x -> {
      if (!x.isVariable() || x.isBuiltInSymbol()) {
        // Cannot assign to raw object `1`.
        throw new ArgumentTypeException(
            IOFunctions.getMessage("setraw", F.List(x), EvalEngine.get()));
      }
      return false;
    });
  }

  @Override
  public double value(double[] point) {
    double result = 0.0;
    final double[] stack = new double[10];
    try {
      // substitution is more thread safe than direct value assigning to global variable
      IASTAppendable list = F.ListAlloc();
      for (int i = 0; i < point.length; i++) {
        list.append(F.Rule(fVariableList.get(i + 1), F.num(point[i])));
      }
      IExpr temp = F.subst(fFunction, list);
      result = DoubleStackEvaluator.eval(stack, 0, temp);
    } catch (RuntimeException rex) {
      return Double.NaN;
    }
    return result;
  }

}

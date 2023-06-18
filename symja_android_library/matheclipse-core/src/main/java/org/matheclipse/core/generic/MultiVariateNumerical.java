package org.matheclipse.core.generic;

import java.util.function.Function;
import org.hipparchus.analysis.MultivariateFunction;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class MultiVariateNumerical implements MultivariateFunction {
  final IExpr fFunction;
  final IAST fVariableList;
  final EvalEngine fEngine;
  final Object2IntOpenHashMap<IExpr> fIndexMap;

  public MultiVariateNumerical(final IExpr function, final IAST variablesList) {
    this(function, variablesList, EvalEngine.get());
  }

  public MultiVariateNumerical(final IExpr function, final IAST variablesList,
      final EvalEngine engine) {
    fFunction = function;
    fVariableList = variablesList;
    fIndexMap = new Object2IntOpenHashMap<IExpr>(fVariableList.argSize());
    for (int i = 1; i < fVariableList.size(); i++) {
      fIndexMap.put(variablesList.get(i), i);
    }
    fEngine = engine;
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
  public double value(double[] point) {
    try {
      Function<IExpr, IExpr> function = x -> {
        int i = fIndexMap.getInt(x);
        if (i > 0) {
          return F.num(point[i - 1]);
        }
        return F.NIL;
      };
      return fFunction.evalf(function);
    } catch (RuntimeException rex) {
      return Double.NaN;
    }
  }

}

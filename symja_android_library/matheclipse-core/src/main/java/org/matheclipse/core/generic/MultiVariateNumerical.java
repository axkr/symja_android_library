package org.matheclipse.core.generic;

import java.util.ArrayList;
import org.hipparchus.analysis.MultivariateFunction;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class MultiVariateNumerical implements MultivariateFunction {
  final IExpr fFunction;
  final IAST fVariableList;
  final ArrayList<ISymbol> fDummyVariables;

  public MultiVariateNumerical(final IExpr function, final IAST variablesList) {
    variablesList.exists(x -> {
      if (!x.isVariable() || x.isBuiltInSymbol()) {
        // Cannot assign to raw object `1`.
        throw new ArgumentTypeException(Errors.getMessage("setraw", F.list(x), EvalEngine.get()));
      }
      return false;
    });

    fVariableList = variablesList;
    ArrayList<ISymbol> dummyVariables = new ArrayList<ISymbol>(fVariableList.argSize());
    for (int i = 1; i < fVariableList.size(); i++) {
      IExpr variable = variablesList.get(i);
      dummyVariables.add(F.Dummy("$" + variable.toString()));
    }
    fDummyVariables = dummyVariables;
    fFunction = F.subst(function, x -> {
      int indexOf = variablesList.indexOf(x);
      if (indexOf > 0) {
        return fDummyVariables.get(indexOf - 1);
      }
      return F.NIL;
    });
  }

  @Override
  public double value(double[] point) {
    try {
      for (int i = 0; i < fDummyVariables.size(); i++) {
        fDummyVariables.get(i).assignValue(F.num(point[i]));
      }
      return fFunction.evalf();
    } catch (RuntimeException rex) {
      return Double.NaN;
    }
  }

}

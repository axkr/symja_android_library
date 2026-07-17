package org.matheclipse.core.reflection.system;

import java.util.LinkedHashMap;
import java.util.Map;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;

public class CountsBy extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.isAST2()) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();

      if (arg1.isList()) {
        IAST list = (IAST) arg1;
        Map<IExpr, Integer> map = new LinkedHashMap<>();

        for (int i = 1; i <= list.argSize(); i++) {
          IExpr mappedKey = engine.evaluate(F.unaryAST1(arg2, list.get(i)));
          Integer count = map.get(mappedKey);
          map.put(mappedKey, count == null ? 1 : count + 1);
        }

        return buildAssociation(map);
      } else if (arg1.isAssociation()) {
        IAssociation assoc = (IAssociation) arg1;
        Map<IExpr, Integer> map = new LinkedHashMap<>();

        // Functions that consume a collection operate on an association's values
        for (int i = 1; i <= assoc.argSize(); i++) {
          IExpr mappedKey = engine.evaluate(F.unaryAST1(arg2, assoc.getValue(i)));
          Integer count = map.get(mappedKey);
          map.put(mappedKey, count == null ? 1 : count + 1);
        }

        return buildAssociation(map);
      }
    }
    return F.NIL;
  }

  /**
   * Helper method to build an IAssociation from the counted map.
   */
  private IAssociation buildAssociation(Map<IExpr, Integer> map) {
    IAssociation result = F.assoc();
    for (Map.Entry<IExpr, Integer> entry : map.entrySet()) {
      result.appendRule(F.Rule(entry.getKey(), F.ZZ(entry.getValue())));
    }
    return result;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }
}
package org.matheclipse.core.reflection.system;

import java.util.LinkedHashMap;
import java.util.Map;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;

public class PositionIndex extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.isAST1()) {
      IExpr arg1 = ast.arg1();

      if (arg1.isList()) {
        IAST list = (IAST) arg1;
        Map<IExpr, IASTAppendable> map = new LinkedHashMap<>();

        for (int i = 1; i <= list.argSize(); i++) {
          IExpr elem = list.get(i);
          IASTAppendable positions = map.get(elem);
          if (positions == null) {
            positions = F.ListAlloc();
            map.put(elem, positions);
          }
          positions.append(F.ZZ(i));
        }

        return buildAssociation(map);
      } else if (arg1.isAssociation()) {
        IAssociation assoc = (IAssociation) arg1;
        Map<IExpr, IASTAppendable> map = new LinkedHashMap<>();

        // For associations, it maps distinct values to a list of their keys
        for (int i = 1; i <= assoc.argSize(); i++) {
          IExpr key = assoc.getKey(i);
          IExpr value = assoc.getValue(i);

          IASTAppendable positions = map.get(value);
          if (positions == null) {
            positions = F.ListAlloc();
            map.put(value, positions);
          }
          positions.append(key);
        }

        return buildAssociation(map);
      }
    }
    return F.NIL;
  }

  /**
   * Helper method to build an IAssociation from the mapped positions.
   */
  private IAssociation buildAssociation(Map<IExpr, IASTAppendable> map) {
    IAssociation result = F.assoc();
    for (Map.Entry<IExpr, IASTAppendable> entry : map.entrySet()) {
      result.appendRule(F.Rule(entry.getKey(), entry.getValue()));
    }
    return result;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}
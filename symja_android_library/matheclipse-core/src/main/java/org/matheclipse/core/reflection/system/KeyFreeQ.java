package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;

public class KeyFreeQ extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    if (ast.isAST2()) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();

      if (arg1.isAssociation()) {
        IAssociation assoc = (IAssociation) arg1;
        for (int i = 1; i <= assoc.argSize(); i++) {
          if (assoc.getRule(i).arg1().equals(arg2)) {
            return S.False;
          }
        }
        return S.True;
      } else if (arg1.isListOfRules(false)) {
        IAST list = (IAST) arg1;
        for (int i = 1; i <= list.argSize(); i++) {
          if (((IAST) list.get(i)).arg1().equals(arg2)) {
            return S.False;
          }
        }
        return S.True;
      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }
}
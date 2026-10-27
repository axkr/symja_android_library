package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class KeyFreeQ extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    if (ast.isAST2()) {
      // the key is a pattern: KeyFreeQ(<|x->1,y->2|>, _) is False
      IExpr hasKey = KeyMemberQ.hasKey(ast, engine);
      return hasKey.isPresent() ? F.bool(!hasKey.isTrue()) : F.NIL;
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2_1;
  }
}

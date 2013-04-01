package org.matheclipse.script.reflection;

import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.AST;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Calculate the depth of an expression (i.e. <code>{x,{y}} --> 3</code>
 */
public class MyDepth extends AbstractFunctionEvaluator {

  public MyDepth() {
  }

  @Override
  public IExpr evaluate(final IAST functionList) {
    if (functionList.size() != 2) {
      return null;
    }
    if (!(functionList.get(1) instanceof IAST)) {
      return F.C1;
    }
    return F.integer(AST.COPY.depth((IAST) functionList.get(1), 1));
  }

}

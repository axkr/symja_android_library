package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/** Plot a list of Points in 3 dimesnsions */
public class ListPointPlot3D extends AbstractEvaluator {
  public ListPointPlot3D() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (Config.USE_MANIPULATE_JS) {
      IExpr temp = S.Manipulate.of(engine, ast);
      if (temp.headID() == ID.JSFormData) {
        return temp;
      }
    }
    return F.NIL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}

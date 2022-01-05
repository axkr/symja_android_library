package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/** Plot a list of Points in 3 dimesnsions */
public class ListPointPlot3D extends AbstractEvaluator {
  public ListPointPlot3D() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    return F.Graphics3D(F.Point((IAST) ast.arg1()));
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}

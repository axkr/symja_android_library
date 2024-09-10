package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The <code>HeavisidePi</code> function returns <code>1</code> for all <code>Abs(x)</code> less
 * than <code>1/2</code> and <code>0</code> for all <code>Abs(x)</code> greater than
 * <code>1/2</code>,
 */
public class HeavisidePi extends AbstractEvaluator {

  public HeavisidePi() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    int size = ast.size();
    if (size > 1) {
      for (int i = 1; i < size; i++) {
        IExpr expr = ast.get(i);
        IReal temp = expr.abs().evalReal();
        if (temp != null) {
          if (temp.isGT(F.C1D2)) {
            return F.C0;
          } else if (temp.isLT(F.C1D2)) {
            continue;
          }
        }
        return F.NIL;
      }
    }
    return F.C1;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.ORDERLESS | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
  }
}

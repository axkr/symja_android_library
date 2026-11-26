package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.CompareUtil;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * HeavisideTheta function returns <code>1</code> for all x greater than <code>0</code> and <code>0
 * </code> for all x less than <code>0</code>,
 */
public class HeavisideTheta extends AbstractEvaluator {

  // CPD-OFF UnitStep and HeavisideTheta differ at 0
  public HeavisideTheta() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    int size = ast.size();
    if (size > 1) {
      for (int i = 1; i < size; i++) {
        IExpr expr = ast.get(i);
        if (!expr.isNumber() && expr.isNumericFunction(true)
            && expr.isFree(x -> x.isInexactNumber(), false)) {
          // an exact value: the sign is not the one of a rounding error of its machine value
          int sign = CompareUtil.numericSignOrExactZero(expr, engine);
          if (sign == 1) {
            continue;
          }
          if (sign == -1) {
            return F.C0;
          }
          if (sign == 0 || sign == CompareUtil.UNCERTAIN) {
            return F.NIL;
          }
        }
        IReal temp = expr.evalReal();
        if (temp != null) {
          if (temp.complexSign() < 0) {
            return F.C0;
          } else if (temp.complexSign() > 0) {
            continue;
          }
        } else {
          if (expr.isNegativeResult()) {
          // CPD-ON
            return F.C0;
          }
          if (expr.isPositiveResult()) {
            continue;
          }
          if (expr.isNegativeInfinity()) {
            return F.C0;
          }
          if (expr.isInfinity()) {
            continue;
          }
        }
        return F.NIL;
      }
    }
    return F.C1;
  }

  @Override
  public void setUp(ISymbol newSymbol) {
    newSymbol.setAttributes(Attribute.ORDERLESS, Attribute.LISTABLE);
  }
}

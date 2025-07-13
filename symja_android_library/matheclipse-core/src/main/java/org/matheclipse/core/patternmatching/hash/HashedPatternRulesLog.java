package org.matheclipse.core.patternmatching.hash;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.AbstractIntegerSym;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

/**
 * Evaluate <code>Log(x) * Log(y)</code> combinations. For example evaluate <code>
 * Log(1000) / Log(10)</code> to <code>3</code>
 */
public class HashedPatternRulesLog extends HashedPatternRules {
  public HashedPatternRulesLog(IExpr lhsPattern1, IExpr lhsPattern2) {
    super(lhsPattern1, lhsPattern2, S.Null, false, null, true);
  }

  @Override
  public IExpr evalDownRule(IExpr arg1, IExpr num1, IExpr arg2, IExpr num2, EvalEngine engine) {
    if (num1.isOne() && num2.isMinusOne()) {
      IExpr temp = getRulesData().evalDownRule(F.list(arg1, arg2), engine);
      if (temp.isPresent()) {
        IExpr i1 = arg1.first();
        IExpr i2 = arg2.first();
        if (i1.isInteger() && i2.isInteger()) {
          return AbstractIntegerSym.baseBLog((IInteger) i2, (IInteger) i1);
        }
      }
    }
    return F.NIL;
  }
}

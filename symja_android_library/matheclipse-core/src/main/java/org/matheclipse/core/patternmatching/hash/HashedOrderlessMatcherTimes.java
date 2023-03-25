package org.matheclipse.core.patternmatching.hash;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.visit.HashValueVisitor;

/**
 * Match two arguments of an <code>Orderless</code> <code>Times(... )</code> AST into a new
 * resulting expression.
 */
public class HashedOrderlessMatcherTimes extends HashedOrderlessMatcher {

  public HashedOrderlessMatcherTimes() {
    super();
  }

  @Override
  protected void createHashValues(final IAST orderlessAST, int[] hashValues) {
    for (int i = 0; i < hashValues.length; i++) {
      IExpr temp = orderlessAST.get(i + 1);
      if (temp.isPower()) { // && temp.exponent().isInteger()) {
        hashValues[i] = temp.base().head().hashCode();
      } else {
        hashValues[i] = temp.head().hashCode();
      }
    }
  }

  @Override
  protected void createSpecialHashValues(final IAST orderlessAST, int[] hashValues) {
    for (int i = 0; i < hashValues.length; i++) {
      IExpr temp = orderlessAST.get(i + 1);
      if (temp.isPower() && temp.exponent().isInteger()) {
        hashValues[i] = temp.base().accept(HashValueVisitor.HASH_VALUE_VISITOR);
      } else {
        hashValues[i] = temp.accept(HashValueVisitor.HASH_VALUE_VISITOR);
      }
    }
  }

  @Override
  protected boolean updateHashValues(IASTAppendable result, final IAST orderlessAST,
      AbstractHashedPatternRules hashRule, int[] hashValues, int i, int j, EvalEngine engine) {
    if (hashRule instanceof HashedPatternRulesTimesPower) {
      IExpr temp = hashRule.evalDownRule(orderlessAST.get(i + 1), null, orderlessAST.get(j + 1),
          null, engine);
      if (temp.isPresent()) {
        hashValues[i] = 0;
        hashValues[j] = 0;
        result.append(temp);
        return true;
      }
      return false;
    }
    IExpr temp;
    IExpr arg1 = orderlessAST.get(i + 1);
    IReal num1 = F.C1;
    if (arg1.isPower() && arg1.exponent().isInteger()) {
      num1 = (IReal) arg1.exponent();
      arg1 = arg1.base();
    }
    IExpr arg2 = orderlessAST.get(j + 1);
    IReal num2 = F.C1;
    if (arg2.isPower() && arg2.exponent().isInteger()) {
      num2 = (IReal) arg2.exponent();
      arg2 = arg2.base();
    }
    if ((temp = hashRule.evalDownRule(arg1, num1, arg2, num2, engine)).isPresent()) {
      hashValues[i] = 0;
      hashValues[j] = 0;
      result.append(temp);
      return true;
    }
    return false;
  }
}

package org.matheclipse.core.patternmatching.hash;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.visit.HashValueVisitor;

/**
 * Match two arguments of an <code>Orderless</code> <code>Plus(... )</code> AST into a new resulting
 * expression.
 */
public class HashedOrderlessMatcherPlus extends HashedOrderlessMatcher {

  public HashedOrderlessMatcherPlus() {
    super();
  }

  @Override
  protected void createHashValues(final IAST orderlessAST, int[] hashValues) {
    for (int i = 0; i < hashValues.length; i++) {
      IExpr temp = orderlessAST.get(i + 1);
      if (temp.isTimes2() && temp.first().isInteger()) {
        hashValues[i] = temp.second().head().hashCode();
      } else {
        if (temp.isPresent()) {
          hashValues[i] = temp.head().hashCode();
        } else {
          hashValues[i] = 0;
        }
      }
    }
  }

  @Override
  protected void createSpecialHashValues(final IAST orderlessAST, int[] hashValues) {
    for (int i = 0; i < hashValues.length; i++) {
      IExpr temp = orderlessAST.get(i + 1);
      if (temp.isTimes2() && temp.first().isInteger()) {
        hashValues[i] = temp.second().accept(HashValueVisitor.HASH_VALUE_VISITOR);
      } else {
        hashValues[i] = temp.accept(HashValueVisitor.HASH_VALUE_VISITOR);
      }
    }
  }

  @Override
  protected boolean updateHashValues(IASTAppendable result, final IAST orderlessAST,
      AbstractHashedPatternRules hashRule, int[] hashValues, int i, int j, EvalEngine engine) {
    IExpr temp;
    IExpr arg1 = orderlessAST.get(i + 1);
    IReal intFactor1 = F.C1;
    if (arg1.isTimes2() && arg1.first().isInteger()) {
      intFactor1 = (IReal) ((IAST) arg1).arg1();
      arg1 = ((IAST) arg1).arg2();
    }
    IExpr arg2 = orderlessAST.get(j + 1);
    IReal intFactor2 = F.C1;
    if (arg2.isTimes2() && arg2.first().isInteger()) {
      intFactor2 = (IReal) arg2.first();
      arg2 = arg2.second();
    }
    if ((temp = hashRule.evalDownRule(arg1, null, arg2, null, engine)).isPresent()) {
      hashValues[i] = 0;
      hashValues[j] = 0;

      IInteger plusMinusOne = F.C1;

      if (hashRule.isLHS2Negate()) {
        IReal intFactor2Negated = intFactor2.negate();
        if (intFactor1.equals(intFactor2Negated)) {
          result.append(F.Times(intFactor1, temp));
          return true;
        }
        if (intFactor1.isNegative() && intFactor2.isPositive()) {
          intFactor1 = intFactor1.negate();
          intFactor2 = intFactor2Negated;
          plusMinusOne = F.CN1;
        }
        if (intFactor1.isPositive() && intFactor2.isNegative()) {
          intFactor2 = intFactor2.negate();
          IReal diff = intFactor1.subtractFrom(intFactor2);
          if (diff.isPositive()) {
            // num1 > num2
            result.append(
                F.Times(plusMinusOne, F.Plus(F.Times(diff, arg1), F.Times(intFactor2, temp))));
            return true;
          } else {
            // num1 < num2
            result.append(
                F.Times(plusMinusOne, F.Plus(F.Times(diff, arg2), F.Times(intFactor1, temp))));
            return true;
          }
        }
      } else {
        if (intFactor1.equals(intFactor2)) {
          result.append(F.Times(intFactor1, temp));
          return true;
        }
        if (intFactor1.isNegative() && intFactor2.isNegative()) {
          intFactor1 = intFactor1.negate();
          intFactor2 = intFactor2.negate();
          plusMinusOne = F.CN1;
        }
        if (intFactor1.isPositive() && intFactor2.isPositive()) {
          IReal diff = intFactor1.subtractFrom(intFactor2);
          if (diff.isPositive()) {
            // num1 > num2
            result.append(
                F.Times(plusMinusOne, F.Plus(F.Times(diff, arg1), F.Times(intFactor2, temp))));
            return true;
          } else {
            // num1 < num2
            diff = diff.negate();
            result.append(
                F.Times(plusMinusOne, F.Plus(F.Times(diff, arg2), F.Times(intFactor1, temp))));
            return true;
          }
        }
      }
    }
    return false;
  }
}

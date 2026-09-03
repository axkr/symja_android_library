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
        hashValues[i] = temp.head().hashCode();
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
    IExpr arg1 = orderlessAST.get(i + 1);
    IReal intFactor1 = F.C1;
    if (arg1.isTimes2() && arg1.first().isInteger()) {
      intFactor1 = (IReal) arg1.first();
      arg1 = arg1.second();
    }
    IExpr arg2 = orderlessAST.get(j + 1);
    IReal intFactor2 = F.C1;
    if (arg2.isTimes2() && arg2.first().isInteger()) {
      intFactor2 = (IReal) arg2.first();
      arg2 = arg2.second();
    }
    IExpr temp = hashRule.evalDownRule(arg1, null, arg2, null, engine);
    if (temp.isPresent()) {
      IExpr rewritten = rewriteWithFactors(hashRule.isLHS2Negate(), arg1, intFactor1, arg2,
          intFactor2, temp);
      if (rewritten.isPresent()) {
        // only a successful rewrite may consume the two arguments; an argument whose hash value
        // is 0 is not appended to the result after the scan, so zeroing the slots before this
        // point silently dropped both summands from the result
        hashValues[i] = 0;
        hashValues[j] = 0;
        result.append(rewritten);
        return true;
      }
    }
    return false;
  }

  /**
   * Combine <code>intFactor1*arg1 + intFactor2*arg2</code>, where <code>arg1 + arg2</code> (or
   * <code>arg1 - arg2</code> for a negate rule) was rewritten to <code>temp</code>.
   *
   * @return the rewritten sum or {@link F#NIL} if the integer factors do not allow the rewrite
   */
  private static IExpr rewriteWithFactors(boolean lhs2Negate, IExpr arg1, IReal intFactor1,
      IExpr arg2, IReal intFactor2, IExpr temp) {
    IInteger plusMinusOne = F.C1;
    if (lhs2Negate) {
      // the rule expects arg1 - arg2
      IReal intFactor2Negated = intFactor2.negate();
      if (intFactor1.equals(intFactor2Negated)) {
        return F.Times(intFactor1, temp);
      }
      if (intFactor1.isNegative() && intFactor2.isPositive()) {
        intFactor1 = intFactor1.negate();
        intFactor2 = intFactor2Negated;
        plusMinusOne = F.CN1;
      }
      if (intFactor1.isPositive() && intFactor2.isNegative()) {
        intFactor2 = intFactor2.negate();
        // f1*arg1 - f2*arg2 with positive f1, f2
        IReal diff = intFactor1.subtractFrom(intFactor2);
        if (diff.isPositive()) {
          // f1 > f2: f2*(arg1-arg2) + (f1-f2)*arg1
          return F.Times(plusMinusOne,
              F.Plus(F.Times(diff, arg1), F.Times(intFactor2, temp)));
        }
        // f1 < f2: f1*(arg1-arg2) + (f1-f2)*arg2, with a negative difference
        return F.Times(plusMinusOne, F.Plus(F.Times(diff, arg2), F.Times(intFactor1, temp)));
      }
      return F.NIL;
    }
    if (intFactor1.equals(intFactor2)) {
      return F.Times(intFactor1, temp);
    }
    if (intFactor1.isNegative() && intFactor2.isNegative()) {
      intFactor1 = intFactor1.negate();
      intFactor2 = intFactor2.negate();
      plusMinusOne = F.CN1;
    }
    if (intFactor1.isPositive() && intFactor2.isPositive()) {
      IReal diff = intFactor1.subtractFrom(intFactor2);
      if (diff.isPositive()) {
        // f1 > f2: f2*(arg1+arg2) + (f1-f2)*arg1
        return F.Times(plusMinusOne, F.Plus(F.Times(diff, arg1), F.Times(intFactor2, temp)));
      }
      // f1 < f2: f1*(arg1+arg2) + (f2-f1)*arg2
      diff = diff.negate();
      return F.Times(plusMinusOne, F.Plus(F.Times(diff, arg2), F.Times(intFactor1, temp)));
    }
    return F.NIL;
  }
}

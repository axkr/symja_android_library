package org.matheclipse.core.patternmatching.hash;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.RulesData;

/**
 * Data structure for <code>HashedOrderlessMatcher</code>.
 *
 * <p>To set up a rule like<br>
 * <code>Sin[x]^2+Cos[x]^2 -> 1</code> <br>
 * use the method:<br>
 * <code>HashedPatternRules(F.Sin(F.x_)^F.C2, F.Cos(F.x_)^F.C2, F.C1)</code>
 */
public class HashedPatternRules extends AbstractHashedPatternRules {

  final IExpr fCondition;

  final IExpr fRHS;

  final boolean fLHS2Negate;

  /**
   * Define a combined pattern rule from the two left-hand-sides.
   *
   * @param lhsPattern1 first left-hand-side pattern
   * @param lhsPattern2 second left-hand-side pattern
   * @param rhsResult the right-hand-side result
   * @param lhs2Negate if <code>true</code> this rule needs a negative integer factor to be true
   * @param condition a condition test
   * @param defaultHashCode use the default hash code of {@link IExpr}
   */
  public HashedPatternRules(
      IExpr lhsPattern1,
      IExpr lhsPattern2,
      IExpr rhsResult,
      boolean lhs2Negate,
      IExpr condition,
      boolean defaultHashCode) {
    super(lhsPattern1, lhsPattern2, defaultHashCode);
    fCondition = condition;
    fRHS = rhsResult;
    fLHS2Negate = lhs2Negate;
  }

  @Override
  public int hashCode() {
    // use the symmetric hash code.
    return super.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    boolean test = super.equals(obj);
    if (test && obj instanceof HashedPatternRules) {
      HashedPatternRules other = (HashedPatternRules) obj;
      if (fCondition == null) {
        if (other.fCondition != null) {
          return false;
        }
      } else if (!fCondition.equals(other.fCondition)) {
        return false;
      }
      if (fRHS == null) {
        if (other.fRHS != null) {
          return false;
        }
      } else if (!fRHS.equals(other.fRHS)) {
        return false;
      }
      return true;
    }
    return false;
  }

  /** @return the right-hand-side result */
  public IExpr getRHS() {
    return IExpr.ofNullable(fRHS);
  }

  /**
   * Get the Condition for this rule.
   *
   * @return may return <code>null</code>.
   */
  public IExpr getCondition() {
    return fCondition;
  }

  /**
   * Get (or create) the rule <code>
   * {&lt;first-left-hand-side&gt;, &lt;second-left-hand-side&gt;}:=&lt;right-hand-side&gt;</code>
   *
   * @return
   */
  public RulesData getRulesData() {
    if (fRulesData == null) {
      fRulesData = new RulesData();
      if (fCondition != null) {
        fRulesData.putDownRule(
            IPatternMatcher.SET_DELAYED,
            false,
            F.List(fLHSPattern1, fLHSPattern2),
            F.Condition(fRHS, fCondition));
      } else {
        fRulesData.putDownRule(
            IPatternMatcher.SET_DELAYED, false, F.List(fLHSPattern1, fLHSPattern2), fRHS);
      }
    }
    return fRulesData;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr evalDownRule(IExpr arg1, IExpr num1, IExpr arg2, IExpr num2, EvalEngine engine) {
    return getRulesData().evalDownRule(F.List(arg1, arg2), engine);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isLHS2Negate() {
    return fLHS2Negate;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "[" + fLHSPattern1 + "," + fLHSPattern2 + "] => [" + fRHS + " /; " + fCondition + "]";
  }
}

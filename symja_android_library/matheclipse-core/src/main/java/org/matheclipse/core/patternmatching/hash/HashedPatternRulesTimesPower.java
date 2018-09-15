package org.matheclipse.core.patternmatching.hash;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

public class HashedPatternRulesTimesPower extends HashedPatternRulesTimes {

	/**
	 * If <code>lhsPattern1.isPower()</code> or <code>lhsPattern2.isPower()</code> take the hashcode from the head of
	 * <code>base()</code>, otherwise take the hashcode form the head of expression.
	 * 
	 * @param lhsPattern1
	 * @param lhsPattern2
	 * @param rhsResult
	 */
	public HashedPatternRulesTimesPower(IExpr lhsPattern1, IExpr lhsPattern2, IExpr rhsResult) {
		this(lhsPattern1, lhsPattern2, rhsResult, false);
	}

	/**
	 * 
	 * @param lhsPattern1
	 * @param lhsPattern2
	 * @param rhsResult
	 * @param useOnlyEqualFactors
	 *            If <code>true</code> use only rules where both factors are equal, <b>HACK, to avoid stack overflow in
	 *            integration rules.</b>
	 */
	public HashedPatternRulesTimesPower(IExpr lhsPattern1, IExpr lhsPattern2, IExpr rhsResult,
			boolean useOnlyEqualFactors) {
		super(lhsPattern1, lhsPattern2, rhsResult, useOnlyEqualFactors);
	}

	/**
	 * If <code>lhsPattern1.isPower()</code> or <code>lhsPattern2.isPower()</code> get the hashcode from the head of
	 * <code>base().head()</code>, otherwise get the hashcode form the <code>head()</code> of expression.
	 * 
	 * @param lhsPattern1
	 * @param lhsPattern2
	 * @param defaultHashCode
	 */
	@Override
	public void hashValues(IExpr lhsPattern1, IExpr lhsPattern2, boolean defaultHashCode) {
		if (lhsPattern1.isPower()) {
			hash1 = lhsPattern1.base().head().hashCode();
		} else {
			hash1 = lhsPattern1.head().hashCode();
		}
		if (lhsPattern2.isPower()) {
			hash2 = lhsPattern2.base().head().hashCode();
		} else {
			hash2 = lhsPattern2.head().hashCode();
		}
	}

	@Override
	public IExpr evalDownRule(IExpr e1, IExpr num1, IExpr e2, IExpr num2, EvalEngine engine) {
		return getRulesData().evalDownRule(F.List(e1, e2), engine);
	}
}

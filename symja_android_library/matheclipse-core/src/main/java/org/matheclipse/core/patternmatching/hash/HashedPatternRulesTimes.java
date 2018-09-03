package org.matheclipse.core.patternmatching.hash;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

public class HashedPatternRulesTimes extends HashedPatternRules {
	/**
	 * If <code>true</code> use only rules where both factors are equal, <b>HACK, to
	 * avoid stack overflow in integration rules.</b>
	 */
	boolean useOnlyEqualFactors = false;

	public HashedPatternRulesTimes(IExpr lhsPattern1, IExpr lhsPattern2, IExpr rhsResult) {
		this(lhsPattern1, lhsPattern2, rhsResult, false);
	}

	/**
	 * 
	 * @param lhsPattern1
	 * @param lhsPattern2
	 * @param rhsResult
	 * @param useOnlyEqualFactors
	 *            If <code>true</code> use only rules where both factors are equal,
	 *            <b>HACK, to avoid stack overflow in integration rules.</b>
	 */
	public HashedPatternRulesTimes(IExpr lhsPattern1, IExpr lhsPattern2, IExpr rhsResult, boolean useOnlyEqualFactors) {
		super(lhsPattern1, lhsPattern2, rhsResult, null, true);
		this.useOnlyEqualFactors = useOnlyEqualFactors;
	}

	public HashedPatternRulesTimes(IExpr lhsPattern1, IExpr lhsPattern2, IExpr rhsResult, IExpr condition,
			boolean defaultHashCode) {
		super(lhsPattern1, lhsPattern2, rhsResult, condition, defaultHashCode);
	}

	@Override
	public IExpr evalDownRule(IExpr e1, IExpr num1, IExpr e2, IExpr num2, EvalEngine engine) {
		IExpr temp = getRulesData().evalDownRule(F.List(e1, e2), engine);
		if (temp.isPresent()) {
			if (num1.equals(num2)) {
				return F.Power(temp, num2);
			}
			if (useOnlyEqualFactors) {
				return F.NIL;
			}
			if (num1.isPositive() && num2.isPositive()) {
				IExpr diff = num1.subtract(num2);
				if (diff.isPositive()) {
					// num1 > num2
					return F.Times(e1.power(diff), temp.power(num2));
				} else {
					// num1 < num2
					diff = diff.negate();
					return F.Times(e2.power(diff), temp.power(num1));
				}
			}
		}
		return F.NIL;
	}
}

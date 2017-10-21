package org.matheclipse.core.patternmatching.hash;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

public class HashedPatternRulesTimes extends HashedPatternRules {
	public HashedPatternRulesTimes(IExpr lhsPattern1, IExpr lhsPattern2, IExpr rhsResult) {
		super(lhsPattern1, lhsPattern2, rhsResult, null, true);
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
			// IInteger plusMinusOne = F.C1;
			// if (num1.isNegative() && num2.isNegative()) {
			// num1 = num1.negate();
			// num2 = num2.negate();
			// plusMinusOne = F.CN1;
			// }
			if (num1.isPositive() && num2.isPositive()) {
				IExpr diff = num1.subtract(num2);
				if (diff.isPositive()) {
					// num1 > num2
					return F.Times(e1.power(diff), temp.power(num2));
				} else {
					// num1 < num2
					diff = diff.negate();
					return F.Times(F.Power(e2, diff), F.Power(temp, num1));
				}
			}
		}
		return F.NIL;
	}
}

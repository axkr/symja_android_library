package org.matheclipse.core.patternmatching.hash;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.visit.HashValueVisitor;

/**
 * Match two arguments of an <code>Orderless</code> <code>Times(... )</code> AST
 * into a new resulting expression.
 * 
 */
public class HashedOrderlessMatcherTimes extends HashedOrderlessMatcher {

	public HashedOrderlessMatcherTimes() {
		super();
	}

	@Override
	protected void createHashValues(final IAST orderlessAST, int[] hashValues) {
		for (int i = 0; i < hashValues.length; i++) {
			IExpr temp = orderlessAST.get(i + 1);
			if (temp.isPower() && ((IAST) temp).arg2().isInteger()) {
				hashValues[i] = ((IAST) temp).arg1().head().hashCode();
			} else {
				hashValues[i] = temp.head().hashCode();
			}
		}
	}

	@Override
	protected void createSpecialHashValues(final IAST orderlessAST, int[] hashValues) {
		for (int i = 0; i < hashValues.length; i++) {
			IExpr temp = orderlessAST.get(i + 1);
			if (temp.isPower() && ((IAST) temp).arg2().isInteger()) {
				hashValues[i] = ((IAST) temp).arg1().accept(HashValueVisitor.HASH_VALUE_VISITOR);
			} else {
				hashValues[i] = temp.accept(HashValueVisitor.HASH_VALUE_VISITOR);
			}
		}
	}

	@Override
	protected boolean updateHashValues(IAST result, final IAST orderlessAST, AbstractHashedPatternRules hashRule,
			int[] hashValues, int i, int j, EvalEngine engine) {
		IExpr temp;
		IExpr arg1 = orderlessAST.get(i + 1);
		ISignedNumber num1 = F.C1;
		if (arg1.isPower() && ((IAST) arg1).arg2().isInteger()) {
			num1 = (ISignedNumber) ((IAST) arg1).arg2();
			arg1 = ((IAST) arg1).arg1();
		}
		IExpr arg2 = orderlessAST.get(j + 1);
		ISignedNumber num2 = F.C1;
		if (arg2.isPower() && ((IAST) arg2).arg2().isInteger()) {
			num2 = (ISignedNumber) ((IAST) arg2).arg2();
			arg2 = ((IAST) arg2).arg1();
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

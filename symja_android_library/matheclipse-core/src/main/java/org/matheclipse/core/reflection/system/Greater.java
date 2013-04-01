package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.ITernaryComparator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Greater extends AbstractFunctionEvaluator implements ITernaryComparator<IExpr> {
	public final static Greater CONST = new Greater();

	public Greater() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3);

		COMPARE_RESULT b = COMPARE_RESULT.UNDEFINED;
		boolean evaled = false;
		IAST result = ast.clone();
		COMPARE_RESULT[] cResult = new COMPARE_RESULT[ast.size()];
		cResult[0] = COMPARE_RESULT.TRUE;
		for (int i = 1; i < ast.size() - 1; i++) {
			b = prepareCompare(result.get(i), result.get(i + 1));
			if (b == COMPARE_RESULT.FALSE) {
				return F.False;
			}
			if (b == COMPARE_RESULT.TRUE) {
				evaled = true;
			}
			cResult[i] = b;
		}
		cResult[ast.size() - 1] = COMPARE_RESULT.TRUE;
		if (!evaled) {
			// expression doesn't change
			return null;
		}
		int i = 2;
		evaled = false;
		for (int j = 1; j < ast.size(); j++) {
			if (cResult[j - 1] == COMPARE_RESULT.TRUE && cResult[j] == COMPARE_RESULT.TRUE) {
				evaled = true;
				result.remove(i - 1);
			} else {
				i++;
			}
		}

		if (evaled) {
			if (result.size() <= 2) {
				return F.True;
			}
			return result;
		}

		return null;
	}

	public COMPARE_RESULT prepareCompare(final IExpr o0, final IExpr o1) {
		// don't compare strings
		IExpr a0 = o0;
		IExpr a1 = o1;
		if (!(a0.isSignedNumber()) && NumericQ.CONST.apply(a0)) {
			a0 = F.evaln(a0);
		}
		if (!(a1.isSignedNumber()) && NumericQ.CONST.apply(a1)) {
			a1 = F.evaln(a1);
		}
		if (a0.isNumeric() && a1.isRational()) {
			a1 = F.evaln(a1);
		}
		if (a1.isNumeric() && a0.isRational()) {
			a0 = F.evaln(a0);
		}
		return compare(a0, a1);
	}

	public COMPARE_RESULT compare(final IExpr a0, final IExpr a1) {
		// don't compare strings
		if (a0.isSignedNumber() && a1.isSignedNumber()) {
			if (a1.isLTOrdered(a0)) {
				return COMPARE_RESULT.TRUE;
			}
			return COMPARE_RESULT.FALSE;
		}
		return COMPARE_RESULT.UNDEFINED;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.FLAT);
	}
}

package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.ITernaryComparator.COMPARE_RESULT;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.list.algorithms.EvaluationSupport;

public class Min extends AbstractFunctionEvaluator {
	public Min() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 2);

		IAST list = ast;
		IAST resultList = list.copyHead();
		if (EvaluationSupport.flatten(F.List, list, resultList)) {
			list = resultList;
		}
		IExpr min1;
		IExpr min2;
		min1 = list.arg1();
		IAST f = list.copyHead();
		COMPARE_RESULT comp;
		for (int i = 2; i < list.size(); i++) {
			min2 = list.get(i);
			if (min1.equals(min2)) {
				continue;
			}
			comp = Greater.CONST.prepareCompare(min1, min2);

			if (comp == COMPARE_RESULT.TRUE) {
				min1 = min2;
			} else {
				if (comp == COMPARE_RESULT.UNDEFINED) {
					// undetermined
					if (min1.isNumber()) {
						f.add(min2);
					} else {
						f.add(min1);
						min1 = min2;
					}
				}
			}
		}
		if (f.size() > 1) {
			f.add(1, min1);
			if (f.equals(list)) {
				return null;
			}
			return f;
		} else {
			return min1;
		}
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.ORDERLESS | ISymbol.FLAT | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}

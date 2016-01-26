package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.ITernaryComparator.COMPARE_RESULT;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Min extends AbstractFunctionEvaluator {
	public Min() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 1);

		if (ast.size() == 1) {
			return F.CInfinity;
		}
		IAST list = ast;
		IAST resultList = list.copyHead();
		boolean evaled = false;
		if (EvalAttributes.flatten(F.List, list, resultList)) {
			list = resultList;
			evaled = true;
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
				evaled = true;
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
			if (!evaled) {
				return F.UNEVALED;
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

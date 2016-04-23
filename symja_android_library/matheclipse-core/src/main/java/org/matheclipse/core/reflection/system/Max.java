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

public class Max extends AbstractFunctionEvaluator {
	public Max() {
		// default ctor
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 1);

		if (ast.size() == 1) {
			return F.CNInfinity;
		}
		IAST resultList = EvalAttributes.flatten(F.List, ast);
		if (resultList.isPresent()) {
			return maximum(resultList, true);
		}

		return maximum(ast, false);
	}

	private IExpr maximum(IAST list, boolean flattenedList) {
		IExpr max1;
		IExpr max2;
		boolean evaled = flattenedList;
		max1 = list.arg1();
		IAST f = list.copyHead();
		COMPARE_RESULT comp;
		for (int i = 2; i < list.size(); i++) {
			max2 = list.get(i);
			if (max1.equals(max2)) {
				continue;
			}
			comp = Less.CONST.prepareCompare(max1, max2);

			if (comp == COMPARE_RESULT.TRUE) {
				max1 = max2;
				evaled = true;
			} else {
				if (comp == COMPARE_RESULT.UNDEFINED) {
					// undetermined
					if (max1.isNumber()) {
						f.add(max2);
					} else {
						f.add(max1);
						max1 = max2;
					}
				}
			}
		}
		if (f.size() > 1) {
			f.add(1, max1);
			if (!evaled) {
				return F.NIL;
			}
			return f;
		} else {
			return max1;
		}
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.ORDERLESS | ISymbol.FLAT | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}

package org.matheclipse.core.reflection.system;

import org.matheclipse.core.builtin.BooleanFunctions;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.Lambda;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

public class Min extends AbstractFunctionEvaluator {
	public Min() {
		// default ctor
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 1);

		if (ast.isAST0()) {
			return F.CInfinity;
		}

		if (ast.arg1().isInterval1()) {
			IAST list = (IAST) ast.arg1().getAt(1);
			try {
				return (ISignedNumber) list.arg1();
			} catch (ClassCastException cca) {
				// do nothing
			}
		}

		IAST resultList = EvalAttributes.flatten(F.List, ast);
		if (resultList.isPresent()) {
			return minimum(resultList, true);
		}

		return minimum(ast, false);
	}

	private IExpr minimum(IAST list, final boolean flattenedList) {
		boolean evaled = false;
		IAST f = Lambda.remove(list, x -> x.isInfinity());
		if (f.isPresent()) {
			if (f.isAST0()) {
				return F.CNInfinity;
			}
			list = f;
			evaled = true;
		}
		if (!evaled) {
			evaled = flattenedList;
		}

		IExpr min1;
		IExpr min2;
		min1 = list.arg1();
		f = list.copyHead();
		IExpr.COMPARE_TERNARY comp;
		for (int i = 2; i < list.size(); i++) {
			min2 = list.get(i);
			if (min2.isInfinity()) {
				evaled = true;
				continue;
			}

			if (min1.equals(min2)) {
				continue;
			}
			comp = BooleanFunctions.CONST_GREATER.prepareCompare(min1, min2);

			if (comp == IExpr.COMPARE_TERNARY.TRUE) {
				min1 = min2;
				evaled = true;
			} else if (comp == IExpr.COMPARE_TERNARY.FALSE) {
				evaled = true;
			} else {
				if (comp == IExpr.COMPARE_TERNARY.UNDEFINED) {
					// undetermined
					if (min1.isNumber()) {
						f.append(min2);
					} else {
						f.append(min1);
						min1 = min2;
					}
				}
			}
		}
		if (f.size() > 1) {
			f.append(1, min1);
			if (!evaled) {
				return F.NIL;
			}
			return f;
		} else {
			return min1;
		}
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.ORDERLESS | ISymbol.FLAT | ISymbol.NUMERICFUNCTION);
	}
}

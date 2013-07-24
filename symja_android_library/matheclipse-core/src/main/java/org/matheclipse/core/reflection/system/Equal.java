package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.StringX;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Equal extends AbstractFunctionEvaluator {

	public Equal() {
	}

	@Override
	public IExpr evaluate(final IAST lst) {
		if (lst.size() > 1) {
			int b = 0;
			boolean evaled = false;
			IAST result = lst.clone();
			int i = 2;
			while (i < result.size()) {
				b = compare(result.get(i - 1), result.get(i));
				if (b == (-1)) {
					return F.False;
				}
				if (b == 1) {
					evaled = true;
					result.remove(i - 1);
				} else {
					i++;
				}
			}
			if (evaled) {
				if (result.size() == 2) {
					return F.True;
				}
				return result;
			}

		}
		return null;
	}

	/**
	 * 1->True -1 ->False 0->Otherwise
	 * 
	 * @param o0
	 * @param o1
	 * @return
	 */
	public int compare(final IExpr o0, final IExpr o1) {
		if (o0.isSame(o1)) {
			return 1;
		}

		if (o0.isConstant() && o1.isConstant()) {
			return -1;
		}

		if (o0.isNumber() && o1.isNumber()) {
			return -1;
		}

		if ((o0 instanceof StringX) && (o1 instanceof StringX)) {

			if (o0.isSymbol() || o1.isSymbol()) {
				return 0;
			}

			return 1;
		}

		return 0;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.FLAT);
	}
}
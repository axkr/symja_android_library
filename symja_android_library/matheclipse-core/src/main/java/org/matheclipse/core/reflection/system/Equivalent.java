package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * 
 * 
 * 
 */
public class Equivalent extends AbstractFunctionEvaluator {
	public Equivalent() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		if (ast.size() == 1 || ast.size() == 2) {
			return F.True;
		}
		IAST result = ast.copyHead();
		IExpr last = null;
		IExpr boole = null;
		boolean evaled = false;

		for (int i = 1; i < ast.size(); i++) {
			IExpr temp = ast.get(i);
			if (temp.isFalse()) {
				if (boole == null) {
					boole = F.False;
					evaled = true;
				} else if (boole.isTrue()) {
					return F.False;
				} else {
					evaled = true;
				}
			} else if (temp.isTrue()) {
				if (boole == null) {
					boole = F.True;
					evaled = true;
				} else if (boole.isFalse()) {
					return F.False;
				} else {
					evaled = true;
				}
			} else {
				if (last == null || !last.equals(temp)) {
					result.add(temp);
				} else {
					evaled = true;
				}

				last = temp;
			}
		}
		if (evaled) {
			if (result.size() == 1) {
				if (boole != null) {
					return F.True;
				}
			} else if (result.size() == 2 && boole == null) {
				return F.True;
			}
			if (boole != null) {
				result = result.apply(F.And);
				if (boole.isTrue()) {
					return result;
				} else {
					return result.mapAt(F.Not(null), 1);
				}
			}
			return result;
		}
		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.ORDERLESS);
	}
}

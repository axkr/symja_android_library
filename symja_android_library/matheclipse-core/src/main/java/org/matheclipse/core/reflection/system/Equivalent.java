package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
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
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.isAST0() || ast.isAST1()) {
			return F.True;
		}
		IAST result = ast.copyHead();
		IExpr last = F.NIL;
		IExpr boole = F.NIL;
		boolean evaled = false;

		for (int i = 1; i < ast.size(); i++) {
			IExpr temp = ast.get(i);
			if (temp.isFalse()) {
				if (!boole.isPresent()) {
					boole = F.False;
					evaled = true;
				} else if (boole.isTrue()) {
					return F.False;
				} else {
					evaled = true;
				}
			} else if (temp.isTrue()) {
				if (!boole.isPresent()) {
					boole = F.True;
					evaled = true;
				} else if (boole.isFalse()) {
					return F.False;
				} else {
					evaled = true;
				}
			} else {
				if (!last.equals(temp)) {
					result.add(temp);
				} else {
					evaled = true;
				}

				last = temp;
			}
		}
		if (evaled) {
			if (result.isAST0()) {
				if (boole.isPresent()) {
					return F.True;
				}
			} else if (result.isAST1() && !boole.isPresent()) {
				return F.True;
			}
			if (boole.isPresent()) {
				result = result.apply(F.And);
				if (boole.isTrue()) {
					return result;
				} else {
					return result.mapAt(F.Not(null), 1);
				}
			}
			return result;
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.ORDERLESS);
	}
}

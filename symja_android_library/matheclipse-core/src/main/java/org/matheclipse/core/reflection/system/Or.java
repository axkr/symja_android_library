package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * 
 * See <a href="http://en.wikipedia.org/wiki/Logical_disjunction">Logical
 * disjunction</a>
 * 
 */
public class Or extends AbstractFunctionEvaluator {

	public Or() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3);
		boolean evaled = false;
		IAST result = ast.clone();
		int index = 1;
		for (int i = 1; i < ast.size(); i++) {
			if (ast.get(i).isTrue()) {
				return F.True;
			}
			if (ast.get(i).isFalse()) {
				result.remove(index);
				evaled = true;
				continue;
			}
			index++;
		}
		if (evaled) {
			if (result.size() == 1) {
				return F.False;
			}
			return result;
		}
		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.ONEIDENTITY | ISymbol.ORDERLESS | ISymbol.FLAT);
	}
}
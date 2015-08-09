package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

/**
 * IntegerExponent(n, base) gets the highest exponent of base (greater than 1) that divides n.
 * 
 */
public class IntegerExponent extends AbstractFunctionEvaluator {

	public IntegerExponent() {
	}

	@Override
	public IExpr evaluate(IAST ast) {
		Validate.checkRange(ast, 2, 3);
		IInteger base = F.C10;
		if (ast.size() == 3) {
			IExpr arg2 = ast.arg2();
			if (arg2.isInteger() && ((IInteger) arg2).isGreaterThan(F.C1)) {
				base = (IInteger) arg2;
			} else {
				return null;
			}
		}
		IExpr arg1 = ast.arg1();
		if (arg1.isInteger()) {
			return ((IInteger) arg1).exponent(base);
		}
		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE);
		super.setUp(symbol);
	}
}

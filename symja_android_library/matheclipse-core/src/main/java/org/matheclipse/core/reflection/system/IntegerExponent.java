package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

/**
 * IntegerExponent(n, base) gets the highest exponent of base (greater than 1)
 * that divides n.
 * 
 */
public class IntegerExponent extends AbstractFunctionEvaluator {

	public IntegerExponent() {
	}

	@Override
	public IExpr evaluate(IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);
		IInteger base = F.C10;
		if (ast.isAST2()) {
			IExpr arg2 = ast.arg2();
			if (arg2.isInteger() && ((IInteger) arg2).compareInt(1) > 0) {
				base = (IInteger) arg2;
			} else {
				return F.NIL;
			}
		}
		IExpr arg1 = ast.arg1();
		if (arg1.isInteger()) {
			return ((IInteger) arg1).exponent(base);
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE);
		super.setUp(symbol);
	}
}

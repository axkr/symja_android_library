package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

/**
 * The integers a and b are said to be <i>coprime</i> or <i>relatively prime</i> if they have no common factor other than 1.
 * 
 * See <a href="http://en.wikipedia.org/wiki/Coprime">Wikipedia:Coprime</a>
 */
public class CoprimeQ extends AbstractFunctionEvaluator {
	/**
	 * Constructor for the CoprimeQ object
	 */
	public CoprimeQ() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3);

		int size = ast.size();
		IExpr expr;
		for (int i = 1; i < size - 1; i++) {
			expr = ast.get(i);
			for (int j = i + 1; j < size; j++) {
				if (!F.eval(F.GCD(expr, ast.get(j))).isOne()) {
					return F.False;
				}
			}
		}
		return F.True;
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE);
		super.setUp(symbol);
	}
}

package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

import com.google.common.base.Function;

/**
 * Try to simplify a given expression
 */
public class PowerExpand extends AbstractFunctionEvaluator {
	private static String[] REPLACE_STRINGS = { 
		"Power[x_ ^ y_, z_] :>x ^(y*z)", 
		"Power[x_ * y_, z_] :> x^z*y^z",
		"Log[x_ ^ y_] :> y*Log[x]" };
	
	private static Function<IExpr, IExpr> REPLACE_RULES;

	public PowerExpand() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);
		if (ast.get(1).isAST()) {
			return ast.get(1).replaceRepeated(REPLACE_RULES);
		}
		return ast.get(1);
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE);
		REPLACE_RULES = Functors.rules(REPLACE_STRINGS);
		super.setUp(symbol);
	}
}

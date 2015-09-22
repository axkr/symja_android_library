package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Try to simplify a given expression
 * 
 * 
 */
public class FullSimplify extends Simplify {

	// TODO currently FullSimplify simply calls Simplify
	public FullSimplify() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);
		return super.evaluate(ast, engine);
	}

}

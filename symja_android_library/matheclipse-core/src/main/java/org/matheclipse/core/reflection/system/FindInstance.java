package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Try to find at least one solution for a set of equations (i.e.
 * <code>Equal[...]</code> expressions).
 */
public class FindInstance extends Solve {

	public FindInstance() {
		// empty constructor
	}

	/**
	 * Try to find at least one solution for a set of equations (i.e.
	 * <code>Equal[...]</code> expressions).
	 */
	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);

		IAST vars = Validate.checkSymbolOrSymbolList(ast, 2);
		IAST termsEqualZeroList = Validate.checkEquations(ast, 1);

		return solveEquations(termsEqualZeroList, vars, 1, engine);
	}
}

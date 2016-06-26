package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.expression.F;
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
		Validate.checkRange(ast, 3, 4);

		IAST vars = Validate.checkSymbolOrSymbolList(ast, 2);
		if (ast.isAST3()) {
			if (ast.arg3().equals(F.Booleans)) {
				IAST resultList = F.List();
				booleansSolve(ast.arg1(), vars, 1, 1, resultList);
				return resultList;
			}
			throw new WrongArgumentType(ast, ast.arg3(), 3, "Booleans expected!");
		}
		IAST termsEqualZeroList = Validate.checkEquations(ast, 1);

		return solveEquations(termsEqualZeroList, vars, 1, engine);
	}
}

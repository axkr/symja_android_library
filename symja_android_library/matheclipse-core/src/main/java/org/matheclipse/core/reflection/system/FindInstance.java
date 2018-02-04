package org.matheclipse.core.reflection.system;

import org.matheclipse.core.builtin.BooleanFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <pre>
 * FindInstance(equations, vars)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * attempts to find one instance which solves the <code>equations</code> for the variables <code>vars</code>.
 * </p>
 * </blockquote>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt; FindInstance({x^2==4,x+y^2==6}, {x,y})
 * {{x-&gt;-2,y-&gt;-2*Sqrt(2)}}
 * </pre>
 * 
 * <h3>Related terms</h3>
 * <p>
 * <a href="Solve.md">Solve</a>
 * </p>
 */
public class FindInstance extends Solve {

	public FindInstance() {
		// empty constructor
	}

	/**
	 * Try to find at least one solution for a set of equations (i.e. <code>Equal[...]</code> expressions).
	 */
	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3, 4);

		IAST vars = Validate.checkSymbolOrSymbolList(ast, 2);
		if (ast.isAST3()) {
			if (ast.arg3().equals(F.Booleans)) {
				return BooleanFunctions.solveInstances(ast.arg1(), vars, 1);
			}
			throw new WrongArgumentType(ast, ast.arg3(), 3, "Booleans expected!");
		}
		IAST termsEqualZeroList = Validate.checkEquations(ast, 1);

		return solveEquations(termsEqualZeroList, F.List(), vars, 1, engine);
	}
}

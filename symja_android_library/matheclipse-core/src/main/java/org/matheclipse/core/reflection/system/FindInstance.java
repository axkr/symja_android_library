package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.BooleanFunctions;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
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
		IAST vars = Validate.checkSymbolOrSymbolList(ast, 2, engine);
		if (!vars.isPresent()) {
			return F.NIL;
		}
		boolean formula = false;
		int maxChoices = 1;
		if (ast.size() == 4) {
			maxChoices = ast.arg3().toIntDefault(Integer.MIN_VALUE);
			if (maxChoices < 0) {
				maxChoices = 1;
			}
		} else if (ast.size() >= 3) {
			maxChoices = ast.arg2().toIntDefault(Integer.MIN_VALUE);
			if (maxChoices < 0) {
				maxChoices = 1;
			}
		}
		if (ast.size() > 2) {
			try {
				if (ast.arg1().isBooleanFormula()) {
					formula = ast.arg1().isBooleanFormula();
					if (ast.isAST2()) {
						return BooleanFunctions.solveInstances(ast.arg1(), vars, maxChoices);
					}
				}
			} catch (RuntimeException rex) {
			}
		}
		if (ast.isAST3()) {
			if (ast.arg3().equals(F.Booleans) || formula) {
				return BooleanFunctions.solveInstances(ast.arg1(), vars, maxChoices);
			}
			throw new WrongArgumentType(ast, ast.arg3(), 3, "Booleans expected!");
		}
		IASTMutable termsEqualZeroList = Validate.checkEquations(ast, 1);

		try {
			return solveEquations(termsEqualZeroList, F.List(), vars, maxChoices, engine);
		} catch (RuntimeException rex) {
			if (Config.SHOW_STACKTRACE) {
				rex.printStackTrace();
			}
		}

		return F.NIL;
	}

	public int[] expectedArgSize() {
		return IOFunctions.ARGS_2_3;
	}
}

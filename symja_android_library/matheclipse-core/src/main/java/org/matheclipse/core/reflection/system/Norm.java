package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Norm of a given argument
 */
public class Norm implements IFunctionEvaluator {

	public Norm() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 2, 3);
		IExpr arg = ast.arg1();
		if (arg.isNumber()) {
			// absolute Value of a number
			return ((INumber) arg).eabs();
		}
		int dim = arg.isVector();
		if (dim > (-1)) {
			return F.Sqrt(((IAST) arg).map(F.Plus, Functors.replaceAll(F.Sqr(F.Abs(F.Null)), F.Null)));
		}
		return null;
	}

	public IExpr numericEval(final IAST ast) {
		return evaluate(ast);
	}

	public void setUp(ISymbol symbol) {

	}

}

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
		IExpr arg1 = ast.arg1();
		
		int dim = arg1.isVector();
		if (dim > (-1)) {
			return F.Sqrt(((IAST) arg1).map(F.Plus, Functors.replaceAll(F.Sqr(F.Abs(F.Null)), F.Null)));
		}
		if (arg1.isNumber()) {
			// absolute Value of a number
			return ((INumber) arg1).eabs();
		}
		if (arg1.isNumericFunction()) {
			// absolute Value 
			return F.Abs(arg1);
		}
		return null;
	}

	public IExpr numericEval(final IAST ast) {
		return evaluate(ast);
	}

	public void setUp(ISymbol symbol) {

	}

}

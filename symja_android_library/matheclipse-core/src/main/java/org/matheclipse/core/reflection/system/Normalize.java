package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>Normalize[vector]</code> calculates the normalized <code>vector</code>.
 */
public class Normalize extends AbstractEvaluator {

	public Normalize() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 2, 3);
		IExpr normFunction = F.Norm;
		if (ast.size() == 3) {
			normFunction = ast.arg2();
		}
		IExpr arg1 = ast.arg1();
		IExpr norm = F.eval(F.unaryAST1(normFunction, ast.arg1()));
		if (norm.isZero()) {
			return arg1;
		}
		return F.Divide(ast.arg1(), norm);
	}
 
}

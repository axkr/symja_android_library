package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Get the real part of an expression
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Real_part">Real part</a>
 */
public class Re implements IFunctionEvaluator {

	public Re() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);
	 
		IExpr expr = ast.get(1);
		if (expr.isSignedNumber()) {
			return expr;
		}
		if (expr.isComplex()) {
			return ((IComplex) expr).getRe();
		}
		if (expr instanceof IComplexNum) {
			return F.num(((IComplexNum) expr).getRealPart());
		}
		
		return null;
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE);
	}

}

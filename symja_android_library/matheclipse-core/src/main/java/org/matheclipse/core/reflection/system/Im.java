package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.WrongNumberOfArguments;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Get the imaginary part of an expression
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Imaginary_part">Imaginary part</a>
 */
public class Im implements IFunctionEvaluator {

	public Im() {
	}

	public IExpr evaluate(final IAST functionList) {
		if (functionList.size() != 2) {
			throw new WrongNumberOfArguments(functionList, 1, functionList.size() - 1);
		}
		IExpr expr = functionList.get(1);
		if (expr.isSignedNumber()) {
			return F.C0;
		}
		if (expr.isComplex()) {
			return ((IComplex) expr).getIm();
		}
		if (expr instanceof IComplexNum) {
			return F.num(((IComplexNum) expr).getImaginaryPart());
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

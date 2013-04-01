package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Conjugate the given argument.
 * 
 * See <a
 * href="http://en.wikipedia.org/wiki/Complex_conjugation">Wikipedia:Complex
 * conjugation</a>
 */
public class Conjugate implements IFunctionEvaluator, INumeric {

	public Conjugate() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);
		if (ast.get(1).isSignedNumber()) {
			return ast.get(1);
		}
		if (ast.get(1).isComplex()) {
			return ((IComplex) ast.get(1)).conjugate();
		}
		if (ast.get(1) instanceof IComplexNum) {
			return ((IComplexNum) ast.get(1)).conjugate();
		}
		return null;
	}

	public IExpr numericEval(final IAST ast) {
		return evaluate(ast);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE);
	}

	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		return stack[top];
	}
}

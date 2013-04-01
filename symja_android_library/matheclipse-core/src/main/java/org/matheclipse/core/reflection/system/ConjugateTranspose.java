package org.matheclipse.core.reflection.system;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Conjugate and transpose a matrix.
 * 
 * See <a
 * href="http://en.wikipedia.org/wiki/Complex_conjugation">Wikipedia:Complex
 * conjugation</a> and <a
 * href="http://en.wikipedia.org/wiki/Transpose">Transpose</a>
 */
public class ConjugateTranspose extends Transpose {

	public ConjugateTranspose() {

	}

	@Override
	protected IExpr transform(final IExpr expr) {
		if (expr.isSignedNumber()) {
			return expr;
		}
		if (expr.isComplex()) {
			return ((IComplex) expr).conjugate();
		}
		if (expr instanceof IComplexNum) {
			return ((IComplexNum) expr).conjugate();
		}
		return F.Conjugate(expr);
	}

}

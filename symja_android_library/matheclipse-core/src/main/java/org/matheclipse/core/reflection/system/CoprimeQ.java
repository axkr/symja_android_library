package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The integers a and b are said to be <i>coprime</i> or <i>relatively prime</i>
 * if they have no common factor other than 1.
 * 
 * See <a href="http://en.wikipedia.org/wiki/Coprime">Wikipedia:Coprime</a>
 */
public class CoprimeQ extends AbstractFunctionEvaluator {
	/**
	 * Constructor for the CoprimeQ object
	 */
	public CoprimeQ() {
	}

	public IExpr evaluate(final IAST list) {
		if (list.size() > 2) {
			int size = list.size();
			IExpr expr;
			for (int i = 1; i < size - 1; i++) {
				expr = list.get(i);
				for (int j = i + 1; j < size; j++) {
					if (!F.eval(F.GCD(expr, list.get(j))).equals(F.C1)) {
						return F.False;
					}
				}
			}

			return F.True;
		}
		return null;
	}
}

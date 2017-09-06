package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractArgMultiple;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Greatest common divisor
 * 
 * See <a href="http://en.wikipedia.org/wiki/Greatest_common_divisor">Wikipedia:
 * Greatest common divisor</a>
 */
public class GCD extends AbstractArgMultiple {
	/**
	 * Constructor for the GCD object
	 */
	public GCD() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.isAST0()) {
			return F.C0;
		} else if (ast.isAST1()) {
			if (ast.arg1().isExactNumber()) {
				return ast.arg1().abs();
			}
		}
		return super.evaluate(ast, engine);
	}

	@Override
	public IExpr e2FraArg(IFraction f0, IFraction f1) {
		return f0.gcd(f1);
	}

	/**
	 * Compute gcd of 2 integer numbers
	 * 
	 */
	@Override
	public IExpr e2IntArg(final IInteger i0, final IInteger i1) {
		return i0.gcd(i1);
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.ONEIDENTITY | ISymbol.ORDERLESS | ISymbol.FLAT | ISymbol.LISTABLE);
	}

}

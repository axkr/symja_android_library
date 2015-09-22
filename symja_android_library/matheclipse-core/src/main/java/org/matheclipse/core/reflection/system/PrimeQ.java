package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.base.Predicate;

/**
 * Test if a number is prime. See: <a href="http://en.wikipedia.org/wiki/Prime_number">Wikipedia:Prime number</a>
 * 
 * @see org.matheclipse.core.reflection.system.NextPrime
 */
public class PrimeQ extends AbstractFunctionEvaluator implements Predicate<IExpr> {

	public PrimeQ() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);
		if (!ast.arg1().isInteger()) {
			return F.False;
		}
		return F.bool(apply(ast.arg1()));
	}

	@Override
	public boolean apply(final IExpr obj) {
		try {
			return ((IInteger) obj).isProbablePrime();
		} catch (final Exception e) {
			if (Config.DEBUG) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE);
	}
}

package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractArgMultiple;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Least common multiple
 * 
 * See
 * <a href="http://en.wikipedia.org/wiki/Least_common_multiple">Wikipedia:Least
 * common multiple</a>
 */
public class LCM extends AbstractArgMultiple {
	/**
	 * Constructor for the LCM object
	 */
	public LCM() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2);

		if (ast.isAST1()) {
			if (ast.arg1().isExactNumber()) {
				return ast.arg1().abs();
			}
		}
		return super.evaluate(ast, engine);
	}

	/**
	 * Compute lcm of 2 integer numbers
	 * 
	 */
	@Override
	public IExpr e2IntArg(final IInteger i0, final IInteger i1) {
		return i0.lcm(i1);
	}

	@Override
	public IExpr e2ObjArg(final IExpr o0, final IExpr o1) {
		if (o0.isZero()) {
			return o0;
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.ORDERLESS | ISymbol.FLAT | ISymbol.LISTABLE);
	}

}

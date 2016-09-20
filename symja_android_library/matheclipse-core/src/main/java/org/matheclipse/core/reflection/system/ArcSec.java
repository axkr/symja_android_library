package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.rules.ArcSecRules;

/**
 * Inverse hyperbolic tangent
 * 
 * See
 * <a href="http://en.wikipedia.org/wiki/Inverse_hyperbolic_function"> Inverse
 * hyperbolic functions</a>
 */
public class ArcSec extends AbstractTrigArg1 implements ArcSecRules {

	@Override
	public IAST getRuleAST() {
		return RULES;
	}

	public ArcSec() {
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(newSymbol);
	}
}

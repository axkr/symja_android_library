package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.ArcSec;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.CComplexInfinity;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Set;

import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.rules.ArcSecRules;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Inverse hyperbolic tangent
 * 
 * See <a href="http://en.wikipedia.org/wiki/Inverse_hyperbolic_function"> Inverse hyperbolic functions</a>
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
		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}

package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.*;

import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Arccotangent
 * 
 * See <a href="http://en.wikipedia.org/wiki/Inverse_trigonometric functions">
 * Inverse_trigonometric functions</a>
 */
public class ArcCot extends AbstractTrigArg1 {
	/*
{
ArcCot[0]=0,
ArcCot[1]=1/4*Pi,
ArcCot[ComplexInfinity]=0
}
	 */
	final static IAST RULES = List(
			Set(ArcCot(C0),C0),
			Set(ArcCot(C1),Times(C1D4,Pi)),
			Set(ArcCot(CComplexInfinity),C0)
			);
	
	@Override
	public IAST getRuleAST() {
		return RULES;
	}
	
	public ArcCot() {
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (AbstractFunctionEvaluator.isNegativeExpression(arg1)) {
			return Plus(Times(CN1, Pi), ArcCot(Times(CN1, arg1)));
		}
		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}

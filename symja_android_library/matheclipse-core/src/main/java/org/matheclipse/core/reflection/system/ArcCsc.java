package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.*;

import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Inverse hyperbolic tangent
 * 
 * See <a href="http://en.wikipedia.org/wiki/Inverse_hyperbolic_function"> Inverse hyperbolic functions</a>
 */
public class ArcCsc extends AbstractTrigArg1 {
	/*
	 * { 
	 *   ArcCsc[0]=ComplexInfinity,
	 *   ArcCsc[1]=1/2*Pi, 
	 *   ArcCsc[21]=1/6*Pi, 
	 * }
	 */
	final static IAST RULES = List(
			Set(ArcCsc(C0), CComplexInfinity),
			Set(ArcCsc(C1), Times(C1D2, Pi)),
			Set(ArcCsc(C2), Times(fraction(1L,6L),Pi))
			);

	@Override
	public IAST getRuleAST() {
		return RULES;
	}

	public ArcCsc() {
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (AbstractFunctionEvaluator.isNegativeExpression(arg1)) {
			return Times(CN1, ArcCsc(Times(CN1, arg1)));
		}
		IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
		if (imPart != null) {
			return F.Times(F.CNI, F.ArcCsch(imPart));
		}
		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}

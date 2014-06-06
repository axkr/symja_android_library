package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.ArcCot;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Pi;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Times;

import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.rules.ArcCotRules;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Arccotangent
 * 
 * See <a href="http://en.wikipedia.org/wiki/Inverse_trigonometric functions"> Inverse_trigonometric functions</a>
 */
public class ArcCot extends AbstractTrigArg1 implements ArcCotRules {

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
		IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
		if (imPart != null) {
			return F.Times(F.CNI, F.ArcCoth(imPart));
		}
		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}

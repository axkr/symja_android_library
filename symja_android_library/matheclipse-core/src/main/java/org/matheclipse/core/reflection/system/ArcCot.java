package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.*;

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
	/**
	 * <pre>
	 *   ArcCot[0]=0,
	 *   ArcCot[1]=1/4*Pi
	 * </pre>
	 */
	final static IAST RULES = List(
			Set(ArcCot(C0), Times(C1D2, Pi)),
			Set(ArcCot(C1), Times(C1D4, Pi))
	);
	
	@Override
	public IAST getRuleAST() {
		return RULES;
	}
	
	public ArcCot() {
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (isNegativeExpression(arg1)) {
			return Times(CN1, ArcCot(Times(CN1, arg1)));
		}
		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}

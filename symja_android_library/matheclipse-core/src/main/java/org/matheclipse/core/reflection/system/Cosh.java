package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.Cosh;

import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.ComplexUtils;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Hyperbolic cosine
 * 
 * See <a href="http://en.wikipedia.org/wiki/Hyperbolic_function">Hyperbolic
 * function</a>
 */
public class Cosh extends AbstractTrigArg1 implements INumeric {

	public Cosh() {
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (isNegativeExpression(arg1)) {
			return Cosh(Times(CN1, arg1));
		}
		return null;
	}
	
	@Override
	public IExpr numericEvalD1(final Num arg1) {
		return F.num(Math.cosh(arg1.getRealPart()));
	}

	@Override
	public IExpr numericEvalDC1(final ComplexNum arg1) {
		return ComplexUtils.cosh(arg1);
	}

	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		return Math.cosh(stack[top]);
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}

package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

public class FresnelC extends AbstractTrigArg1 implements INumeric {

	public FresnelC() {
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		return F.NIL;
	}

	@Override
	public IExpr e1DblArg(final double arg1) {
		return F.num(de.lab4inf.math.functions.FresnelC.fresnelC(arg1));
	}

	@Override
	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		return de.lab4inf.math.functions.FresnelC.fresnelC(stack[top]);
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}

package org.matheclipse.core.reflection.system;

import com.duy.lambda.DoubleUnaryOperator;

import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class CosIntegral extends AbstractTrigArg1 implements INumeric, DoubleUnaryOperator {

	public CosIntegral() {
	}

	@Override
	public double applyAsDouble(double operand) {
		return de.lab4inf.math.functions.CosineIntegral.ci(operand);
	}

	@Override
	public IExpr e1DblArg(final double arg1) {
		return F.num(de.lab4inf.math.functions.CosineIntegral.ci(arg1));
	}

	@Override
	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		return de.lab4inf.math.functions.CosineIntegral.ci(stack[top]);
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

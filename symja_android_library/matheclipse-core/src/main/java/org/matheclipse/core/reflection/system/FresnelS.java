package org.matheclipse.core.reflection.system;

import java.util.function.DoubleUnaryOperator;

import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class FresnelS extends AbstractTrigArg1 implements INumeric, DoubleUnaryOperator {

	public FresnelS() {
	}

	@Override
	public double applyAsDouble(double operand) {
		return de.lab4inf.math.functions.FresnelS.fresnelS(operand);
	}

	@Override
	public IExpr e1DblArg(final double arg1) {
		return F.num(de.lab4inf.math.functions.FresnelS.fresnelS(arg1));
	}

	@Override
	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		return de.lab4inf.math.functions.FresnelS.fresnelS(stack[top]);
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (arg1.isNumber()) {
			if (arg1.isZero()) {
				return F.C0;
			}
		}
		if (arg1.isInfinity()) {
			return F.C1D2;
		}
		if (arg1.isNegativeInfinity()) {
			return F.CN1D2;
		}
		if (arg1.equals(F.CIInfinity)) {
			return F.Divide(F.CNI, F.C2);
		}
		if (arg1.equals(F.CNIInfinity)) {
			return F.Divide(F.CI, F.C2);
		}
		IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
		if (negExpr.isPresent()) {
			return F.Negate(F.FresnelS(negExpr));
		}
		IExpr restExpr = AbstractFunctionEvaluator.extractFactorFromExpression(arg1, F.CI);
		if (restExpr.isPresent()) {
			return F.Times(F.CNI, F.FresnelS(restExpr));
		}

		return F.NIL;
	}
	
	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(newSymbol);
	}
}

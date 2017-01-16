package org.matheclipse.core.reflection.system;

import java.util.function.DoubleUnaryOperator;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.eval.interfaces.ISignedNumberConstant;
import org.matheclipse.core.eval.util.AbstractAssumptions;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * 
 * See <a href="http://en.wikipedia.org/wiki/Argument_%28complex_analysis%29">
 * Wikipedia - Argument (complex_analysis)</a>
 * 
 */
public class Arg extends AbstractFunctionEvaluator implements INumeric, DoubleUnaryOperator {

	public Arg() {
	}

	@Override
	public double applyAsDouble(double operand) {
		if (operand < 0) {
			return Math.PI;
		}
		return 0.0;
	}

	@Override
	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		if (stack[top] < 0) {
			return Math.PI;
		} else if (stack[top] >= 0) {
			return 0;
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		final IExpr arg1 = ast.arg1();
		if (arg1.isIndeterminate()) {
			return F.Indeterminate;
		}
		if (arg1.isDirectedInfinity()) {
			IAST directedInfininty = (IAST) arg1;
			if (directedInfininty.isAST1()) {
				if (directedInfininty.isInfinity()) {
					return F.C0;
				}
				return F.Arg(directedInfininty.arg1());
			} else if (arg1.isComplexInfinity()) {
				return F.Interval(F.List( F.Pi.negate(), F.Pi));
			}
		} else if (arg1.isNumber()) {
			return ((INumber) arg1).complexArg();
		}
		if (arg1.isNumericFunction()) {
			IExpr temp = engine.evalN(arg1);
			if (temp.isSignedNumber()) {
				if (temp.isNegative()) {
					return F.Pi;
				}
				return F.C0;
			}
		}

		if (arg1.isConstant()) {
			ISymbol sym = (ISymbol) arg1;
			IEvaluator eval = sym.getEvaluator();
			if (eval instanceof ISignedNumberConstant) {
				double val = ((ISignedNumberConstant) eval).evalReal();
				if (val < 0.0) {
					return F.Pi;
				} else if (val > 0.0) {
					return F.C0;
				}
			}
		}

		if (AbstractAssumptions.assumeNegative(arg1)) {
			return F.Pi;
		}
		if (AbstractAssumptions.assumePositive(arg1)) {
			return F.C0;
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION | ISymbol.NHOLDFIRST);
		super.setUp(newSymbol);
	}
}

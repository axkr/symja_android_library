package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.CInfinity;
import static org.matheclipse.core.expression.F.CNInfinity;
import static org.matheclipse.core.expression.F.Erf;
import static org.matheclipse.core.expression.F.Negate;

import java.util.function.DoubleUnaryOperator;

import org.hipparchus.exception.MathIllegalStateException;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class SpecialFunctions {
	static {
		F.Erf.setEvaluator(new Erf());
		F.InverseErf.setEvaluator(new InverseErf());
	}

	/**
	 * Returns the error function.
	 * 
	 * @see org.matheclipse.core.reflection.system.InverseErf
	 */
	private final static class Erf extends AbstractTrigArg1 implements INumeric, DoubleUnaryOperator {

		@Override
		public double applyAsDouble(double operand) {
			return org.hipparchus.special.Erf.erf(operand);
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			try {
				return Num.valueOf(org.hipparchus.special.Erf.erf(arg1));
			} catch (final MathIllegalStateException e) {
			}
			return F.NIL;
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			try {
				return org.hipparchus.special.Erf.erf(stack[top]);
			} catch (final MathIllegalStateException e) {
			}
			throw new UnsupportedOperationException();
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			if (arg1.isZero()) {
				return F.C0;
			}
			if (arg1.equals(CInfinity)) {
				return F.C1;
			}
			if (arg1.equals(CNInfinity)) {
				return F.CN1;
			}
			if (arg1.isComplexInfinity()) {
				return F.Indeterminate;
			}
			if (arg1.isDirectedInfinity(F.CI) || arg1.isDirectedInfinity(F.CNI)) {
				return arg1;
			}
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(Erf(negExpr));
			}
			return F.NIL;
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() == 3) {
				return F.Subtract(F.Erf(ast.arg2()), F.Erf(ast.arg1()));
			}
			return super.evaluate(ast, engine);
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	/**
	 * Returns the inverse erf.
	 * 
	 * @see org.matheclipse.core.reflection.system.Erf
	 */
	private final static class InverseErf extends AbstractTrigArg1 implements INumeric {

		@Override
		public IExpr e1DblArg(final double arg1) {
			try {
				if (arg1 >= -1.0 && arg1 <= 1.0) {
					return Num.valueOf(org.hipparchus.special.Erf.erfInv(arg1));
				}
			} catch (final MathIllegalStateException e) {
			}
			return F.NIL;
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			try {
				double arg1 = stack[top];
				if (arg1 >= -1.0 && arg1 <= 1.0) {
					return org.hipparchus.special.Erf.erfInv(arg1);
				}
			} catch (final MathIllegalStateException e) {
			}
			throw new UnsupportedOperationException();
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			if (arg1.isSignedNumber()) {
				if (arg1.isZero()) {
					return F.C0;
				}
				if (arg1.isOne()) {
					return F.CInfinity;
				}
				if (arg1.isMinusOne()) {
					return F.CNInfinity;
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	final static SpecialFunctions CONST = new SpecialFunctions();

	public static SpecialFunctions initialize() {
		return CONST;
	}

	private SpecialFunctions() {

	}

}

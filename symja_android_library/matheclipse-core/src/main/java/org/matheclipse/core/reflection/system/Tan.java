package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.Cot;
import static org.matheclipse.core.expression.F.Divide;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Pi;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Tan;
import static org.matheclipse.core.expression.F.Times;

import java.util.function.DoubleUnaryOperator;

import org.apache.commons.math4.complex.Complex;
import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.eval.util.AbstractAssumptions;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.rules.TanRules;

/**
 * Tan
 * 
 * See
 * <a href="http://en.wikipedia.org/wiki/Trigonometric_functions">Trigonometric
 * functions</a>
 */
public class Tan extends AbstractTrigArg1 implements INumeric, TanRules, DoubleUnaryOperator {

	public Tan() {
	}

	@Override
	public double applyAsDouble(double operand) {
		return Math.tan(operand);
	}

	@Override
	public IExpr e1ApcomplexArg(Apcomplex arg1) {
		return F.complexNum(ApcomplexMath.tan(arg1));
	}

	@Override
	public IExpr e1ApfloatArg(Apfloat arg1) {
		return F.num(ApfloatMath.tan(arg1));
	}

	@Override
	public IExpr e1ComplexArg(final Complex arg1) {
		return F.complexNum(arg1.tan());
	}
	
	@Override
	public IExpr e1DblArg(final double arg1) {
		return F.num(Math.tan(arg1));
	}

	@Override
	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		return Math.tan(stack[top]);
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
		if (negExpr.isPresent()) {
			return Negate(Tan(negExpr));
		}
		IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
		if (imPart.isPresent()) {
			return F.Times(F.CI, F.Tanh(imPart));
		}
		IAST parts = AbstractFunctionEvaluator.getPeriodicParts(arg1, F.Pi);
		if (parts.isPresent()) {
			if (parts.arg2().isInteger()) {
				// period Pi
				return F.Tan(parts.arg1());
			}

			if (parts.arg2().isFraction()) {
				// period (n/m)*Pi
				IFraction f = (IFraction) parts.arg2();
				IInteger[] divRem = f.divideAndRemainder();
				IFraction rest = F.fraction(divRem[1], f.getDenominator());
				if (!divRem[0].isZero()) {
					return Tan(Plus(parts.arg1(), Times(rest, Pi)));
				}

				if (rest.equals(C1D2)) {
					// Tan(z) == Cot(Pi/2 - z)
					return Cot(Subtract(Divide(Pi, C2), arg1));
				}
			}

			if (F.True.equals(AbstractAssumptions.assumeInteger(parts.arg2()))) {
				// period Pi
				return F.Tan(parts.arg1());
			}
		}
		return F.NIL;
	}

	@Override
	public IAST getRuleAST() {
		return RULES;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(newSymbol);
	}
}

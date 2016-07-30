package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Csc;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Pi;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sec;
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
import org.matheclipse.core.reflection.system.rules.SecRules;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Secant function
 * 
 * See
 * <a href="http://en.wikipedia.org/wiki/Trigonometric_functions">Trigonometric
 * functions</a>
 */
public class Sec extends AbstractTrigArg1 implements INumeric, SecRules, DoubleUnaryOperator {

	public Sec() {
	}

	@Override
	public double applyAsDouble(double operand) {
		return 1.0D / Math.cos(operand);
	}

	@Override
	public IExpr e1ApcomplexArg(Apcomplex arg1) {
		return F.complexNum(ApcomplexMath.cos(arg1).inverse());
	}

	@Override
	public IExpr e1ApfloatArg(Apfloat arg1) {
		return F.num(ApfloatMath.cos(arg1).inverse());
	}

	@Override
	public IExpr e1ComplexArg(final Complex arg1) {
		return F.complexNum(arg1.cos().reciprocal());
	}
	
	@Override
	public IExpr e1DblArg(final double arg1) {
		return F.num(1.0D / Math.cos(arg1));
	}

	@Override
	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		return 1.0D / Math.cos(stack[top]);
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
		if (negExpr.isPresent()) {
			return Sec(negExpr);
		}
		IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
		if (imPart.isPresent()) {
			return F.Sech(imPart);
		}
		IAST parts = AbstractFunctionEvaluator.getPeriodicParts(arg1, Pi);
		if (parts.isPresent()) {
			if (parts.arg2().isInteger()) {
				// period 2*Pi
				IInteger i = (IInteger) parts.arg2();
				if (i.isEven()) {
					return Sec(parts.arg1());
				} else {
					return Negate(Sec(parts.arg1()));
				}
			}

			if (parts.arg2().isFraction()) {
				// period (n/m)*Pi
				IFraction f = (IFraction) parts.arg2();
				IInteger[] divRem = f.divideAndRemainder();
				if (!divRem[0].isZero()) {
					IFraction rest = F.fraction(divRem[1], f.getDenominator());
					if (divRem[0].isEven()) {
						return Sec(Plus(parts.arg1(), Times(rest, Pi)));
					} else {
						return Times(CN1, Sec(Plus(parts.arg1(), Times(rest, Pi))));
					}
				}

				if (f.equals(C1D2)) {
					return Times(CN1, Csc(parts.arg1()));
				}
			}

			if (F.True.equals(AbstractAssumptions.assumeInteger(parts.arg2()))) {
				// period n*Pi
				return Times(Power(CN1, parts.arg2()), Sec(parts.arg1()));
			}
		}
		return F.NIL;
	}

	@Override
	public IAST getRuleAST() {
		return RULES;
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}

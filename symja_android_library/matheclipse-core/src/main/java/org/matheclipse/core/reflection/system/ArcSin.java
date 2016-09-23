package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.ArcSin;
import static org.matheclipse.core.expression.F.Negate;

import java.util.function.DoubleUnaryOperator;

import org.apache.commons.math3.complex.Complex;
import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.matheclipse.core.eval.exception.ComplexResultException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.rules.ArcSinRules;

/**
 * Arcsine
 * 
 * See <a href="http://en.wikipedia.org/wiki/Inverse_trigonometric functions" >
 * Inverse_trigonometric functions</a>
 */
public class ArcSin extends AbstractTrigArg1 implements INumeric, ArcSinRules, DoubleUnaryOperator {

	public ArcSin() {
	}

	@Override
	public double applyAsDouble(double operand) {
		double val = Math.asin(operand);
		if (Double.isNaN(val)) {
			throw new ComplexResultException("");
		}
		return val;
	}

	@Override
	public IExpr e1ApcomplexArg(Apcomplex arg1) {
		return F.complexNum(ApcomplexMath.asin(arg1));
	}

	@Override
	public IExpr e1ApfloatArg(Apfloat arg1) {
		return F.num(ApfloatMath.asin(arg1));
	}

	@Override
	public IExpr e1ComplexArg(final Complex arg1) {
		return F.complexNum(arg1.asin());
	}

	@Override
	public IExpr e1DblArg(final double arg1) {
		double val = Math.asin(arg1);
		if (Double.isNaN(val)) {
			return F.complexNum(Complex.valueOf(arg1).asin());
		}
		return F.num(val);
	}

	@Override
	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		return Math.asin(stack[top]);
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
		if (negExpr.isPresent()) {
			return Negate(ArcSin(negExpr));
		}
		IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
		if (imPart.isPresent()) {
			return F.Times(F.CI, F.ArcSinh(imPart));
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

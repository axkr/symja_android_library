package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Sech;

import org.hipparchus.complex.Complex;
import java.util.function.DoubleUnaryOperator;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.rules.SechRules;

/**
 * Hyperbolic Secant function
 * 
 * See <a href="http://en.wikipedia.org/wiki/Hyperbolic_function">Hyperbolic
 * functions</a>
 */
public class Sech extends AbstractTrigArg1 implements INumeric, SechRules, DoubleUnaryOperator {
	public Sech() {
	}

	@Override
	public double applyAsDouble(double operand) {
		return 1.0D / Math.cosh(operand);
	}

	@Override
	public IExpr e1ApcomplexArg(Apcomplex arg1) {
		return F.complexNum(ApcomplexMath.inverseRoot(ApcomplexMath.cosh(arg1), 1));
	}

	@Override
	public IExpr e1ApfloatArg(Apfloat arg1) {
		return F.num(ApfloatMath.inverseRoot(ApfloatMath.cosh(arg1), 1));
	}

	@Override
	public IExpr e1ComplexArg(final Complex arg1) {
		return F.complexNum(arg1.cosh().reciprocal());
	}

	@Override
	public IExpr e1DblArg(final double arg1) {
		return F.num(1.0D / Math.cosh(arg1));
	}

	@Override
	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		return 1.0D / Math.cosh(stack[top]);
	}

	@Override
	public IExpr evaluateArg1(IExpr arg1) {
		IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
		if (negExpr.isPresent()) {
			return Sech(negExpr);
		}
		IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
		if (imPart.isPresent()) {
			return F.Sec(imPart);
		}
		if (arg1.isZero()) {
			return F.C0;
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

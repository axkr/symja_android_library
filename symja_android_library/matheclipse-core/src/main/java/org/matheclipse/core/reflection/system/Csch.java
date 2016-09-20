package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Csch;
import static org.matheclipse.core.expression.F.Negate;

import java.util.function.DoubleUnaryOperator;

import org.apache.commons.math4.complex.Complex;
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
import org.matheclipse.core.reflection.system.rules.CschRules;

/**
 * Hyperbolic Cosecant function
 * 
 * See <a href="http://en.wikipedia.org/wiki/Hyperbolic_function">Hyperbolic
 * functions</a>
 */
public class Csch extends AbstractTrigArg1 implements INumeric, CschRules, DoubleUnaryOperator {

	public Csch() {
	}

	@Override
	public double applyAsDouble(double operand) {
		return 1.0D / Math.sinh(operand);
	}

	@Override
	public IExpr e1ApcomplexArg(Apcomplex arg1) {
		return F.complexNum(ApcomplexMath.inverseRoot(ApcomplexMath.sinh(arg1), 1));
	}

	@Override
	public IExpr e1ApfloatArg(Apfloat arg1) {
		return F.num(ApfloatMath.inverseRoot(ApfloatMath.sinh(arg1), 1));
	}

	@Override
	public IExpr e1ComplexArg(final Complex arg1) {
		return F.complexNum(arg1.sinh().reciprocal());
	}

	@Override
	public IExpr e1DblArg(final double arg1) {
		return F.num(1.0D / Math.sinh(arg1));
	}

	@Override
	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		return 1.0D / Math.sinh(stack[top]);
	}

	@Override
	public IExpr evaluateArg1(IExpr arg1) {
		IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
		if (negExpr.isPresent()) {
			return Negate(Csch(negExpr));
		}
		IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
		if (imPart.isPresent()) {
			return F.Times(F.CNI, F.Csc(imPart));
		}
		if (arg1.isZero()) {
			return F.CComplexInfinity;
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

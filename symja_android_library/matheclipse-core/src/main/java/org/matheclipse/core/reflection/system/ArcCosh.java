package org.matheclipse.core.reflection.system;

import java.util.function.DoubleUnaryOperator;

import org.apache.commons.math4.util.FastMath;
import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.rules.ArcCoshRules;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Inverse hyperbolic cosine
 * 
 * See
 * <a href="http://en.wikipedia.org/wiki/Inverse_hyperbolic_function"> Inverse
 * hyperbolic functions</a>
 */
public class ArcCosh extends AbstractTrigArg1 implements INumeric, ArcCoshRules, DoubleUnaryOperator {

	public ArcCosh() {
	}

	@Override
	public double applyAsDouble(double operand) {
		return FastMath.acosh(operand);
	}

	@Override
	public IExpr e1ApcomplexArg(Apcomplex arg1) {
		return F.complexNum(ApcomplexMath.acosh(arg1));
	}
	
	@Override
	public IExpr e1ApfloatArg(Apfloat arg1) {
		return F.num(ApfloatMath.acosh(arg1));
	}

	@Override
	public IExpr e1DblArg(final double arg1) {
		double val = FastMath.acosh(arg1);
		if (Double.isNaN(val)) {
		}
		return F.num(val);

	}

	@Override
	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		return FastMath.acosh(stack[top]);
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
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

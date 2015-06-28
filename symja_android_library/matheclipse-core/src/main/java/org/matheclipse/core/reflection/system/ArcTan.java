package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.ArcTan;
import static org.matheclipse.core.expression.F.Negate;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.matheclipse.core.eval.interfaces.AbstractArg12;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.ComplexUtils;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.rules.ArcTanRules;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Arctangent
 * 
 * See <a href="http://en.wikipedia.org/wiki/Inverse_trigonometric functions"> Inverse_trigonometric functions</a>
 */
public class ArcTan extends AbstractArg12 implements INumeric, ArcTanRules {

	@Override
	public IAST getRuleAST() {
		return RULES;
	}

	public ArcTan() {
	}

	@Override
	public IExpr e1ObjArg(final IExpr arg1) {
		IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
		if (negExpr != null) {
			return Negate( ArcTan(negExpr));
		}
		IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
		if (imPart != null) {
			return F.Times(F.CI, F.ArcTanh(imPart));
		}
		return null;
	}

	@Override
	public IExpr e1DblArg(INum arg1) {
		return F.num(Math.atan(arg1.getRealPart()));
	}

	@Override
	public IExpr e1DblComArg(final IComplexNum c) {
		return ComplexUtils.atan((ComplexNum) c);
	}

	@Override
	public IExpr e2DblArg(final INum d0, final INum d1) {
		return F.num(Math.atan2(d0.getRealPart(), d1.getRealPart()));
	}

	@Override
	public IExpr e1ApfloatArg(Apfloat arg1) {
		return F.num(ApfloatMath.atan(arg1));
	}

	@Override
	public IExpr e1ApcomplexArg(Apcomplex arg1) {
		return F.complexNum(ApcomplexMath.atan(arg1));
	}

	@Override
	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1 && size != 2) {
			throw new UnsupportedOperationException();
		}
		if (size == 2) {
			return Math.atan2(stack[top - 1], stack[top]);
		}

		return Math.atan(stack[top]);
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}

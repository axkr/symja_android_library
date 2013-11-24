package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.ArcTan;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C1D3;
import static org.matheclipse.core.expression.F.C1D4;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C5;
import static org.matheclipse.core.expression.F.CInfinity;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Pi;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.fraction;
import static org.matheclipse.core.expression.F.integer;

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
import org.matheclipse.parser.client.SyntaxError;

/**
 * Arctangent
 * 
 * See <a href="http://en.wikipedia.org/wiki/Inverse_trigonometric functions">
 * Inverse_trigonometric functions</a>
 */
public class ArcTan extends AbstractArg12 implements INumeric {
	// final static String[] RULES = { "ArcTan[0]=0", "ArcTan[0, 0]=0",
	// "ArcTan[1]=Pi/4", "ArcTan[3^(1/2)]=Pi/3",
	// "ArcTan[3^(1/2)/3]=Pi/6", "ArcTan[Infinity]=Pi/2",
	// "ArcTan[2^(1/2)-1]=1/8*Pi",
	// "ArcTan[1/5*5^(1/2)*(5-2*5^(1/2))^(1/2)]=1/10*Pi",
	// "ArcTan[2-3^(1/2)]=1/12*Pi",
	// "ArcTan[1/5*5^(1/2)*(5+2*5^(1/2))^(1/2)]=3/10*Pi",
	// "ArcTan[2+3^(1/2)]=5/12*Pi", "ArcTan[2^(1/2)+1]=3/8*Pi",
	// "ArcTan[(5+2*5^(1/2))^(1/2)]=2/5*Pi",
	// "ArcTan[(5-2*5^(1/2))^(1/2)]=1/5*Pi",
	// "ArcTan[x_NumberQ]:= -ArcTan[-x] /; SignCmp[x]<0",
	// "ArcTan[x_NumberQ*y_]:= -ArcTan[-x*y] /; SignCmp[x]<0" };

	/*
	 * { ArcTan[(5-2*5^(1/2))^(1/2)*1/5*5^(1/2)]=1/10*Pi,
	 * ArcTan[(5+2*5^(1/2))^(1/2)*1/5*5^(1/2)]=3/10*Pi, ArcTan[3^(1/2)]=1/3*Pi,
	 * ArcTan[DirectedInfinity[1]]=1/2*Pi, ArcTan[2-3^(1/2)]=1/12*Pi,
	 * ArcTan[2+3^(1/2)]=5/12*Pi, ArcTan[1+2^(1/2)]=3/8*Pi, ArcTan[0]=0,
	 * ArcTan[(5-2*5^(1/2))^(1/2)]=1/5*Pi, ArcTan[0,0]=0, ArcTan[1]=1/4*Pi,
	 * ArcTan[(5+2*5^(1/2))^(1/2)]=2/5*Pi, ArcTan[1/3*3^(1/2)]=1/6*Pi,
	 * ArcTan[2^(1/2)-1]=1/8*Pi, ArcTan[1,1]=1/4*Pi, ArcTan[-1,-1]=-3/4*Pi }
	 */
	final static IAST RULES = List(
			Set(ArcTan(Times(Times(Power(Plus(C5, Times(CN1, Times(C2, Power(C5, C1D2)))), C1D2), fraction(1L, 5L)),
					Power(C5, C1D2))), Times(fraction(1L, 10L), Pi)),
			Set(ArcTan(Times(Times(Power(Plus(C5, Times(C2, Power(C5, C1D2))), C1D2), fraction(1L, 5L)), Power(C5, C1D2))),
					Times(fraction(3L, 10L), Pi)), Set(ArcTan(Power(C3, C1D2)), Times(C1D3, Pi)),
			Set(ArcTan(CInfinity), Times(C1D2, Pi)),
			Set(ArcTan(Plus(C2, Times(CN1, Power(C3, C1D2)))), Times(fraction(1L, 12L), Pi)),
			Set(ArcTan(Plus(C2, Power(C3, C1D2))), Times(fraction(5L, 12L), Pi)),
			Set(ArcTan(Plus(C1, Power(C2, C1D2))), Times(fraction(3L, 8L), Pi)), Set(ArcTan(C0), C0),
			Set(ArcTan(Power(Plus(C5, Times(CN1, Times(C2, Power(C5, C1D2)))), C1D2)), Times(fraction(1L, 5L), Pi)),
			Set(ArcTan(C0, C0), C0), Set(ArcTan(C1), Times(C1D4, Pi)),
			Set(ArcTan(Power(Plus(C5, Times(C2, Power(C5, C1D2))), C1D2)), Times(fraction(2L, 5L), Pi)),
			Set(ArcTan(Times(C1D3, Power(C3, C1D2))), Times(fraction(1L, 6L), Pi)),
			Set(ArcTan(Plus(Power(C2, C1D2), Times(CN1, C1))), Times(fraction(1L, 8L), Pi)), Set(ArcTan(C1, C1), Times(C1D4, Pi)),
			Set(ArcTan(CN1, CN1), Times(fraction(-3L, 4L), Pi)));

	@Override
	public IAST getRuleAST() {
		return RULES;
	}

	public ArcTan() {
	}

	@Override
	public IExpr e1ObjArg(final IExpr arg1) {
		if (isNegativeExpression(arg1)) {
			return Times(CN1, ArcTan(Times(CN1, arg1)));
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

	public IExpr e2DblArg(final INum d0, final INum d1) {
		return F.num(Math.atan2(d0.getRealPart(), d1.getRealPart()));
	}

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

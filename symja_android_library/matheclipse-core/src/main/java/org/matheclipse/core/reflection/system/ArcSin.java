package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.*;

import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.ComplexUtils;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Arcsine
 * 
 * See <a href="http://en.wikipedia.org/wiki/Inverse_trigonometric functions">
 * Inverse_trigonometric functions</a>
 */
public class ArcSin extends AbstractTrigArg1 implements INumeric {
	// final static String[] RULES = { "ArcSin[0]=0", "ArcSin[1/2]=Pi/6",
	// "ArcSin[2^(1/2)/2]=Pi/4", "ArcSin[3^(1/2)/2]=Pi/3",
	// "ArcSin[1]=Pi/2", "ArcSin[I]=I*ArcSinh[1]",
	// "ArcSin[Infinity]=(-I)*Infinity", "ArcSin[1/2*3^(1/2)]=1/3*Pi",
	// "ArcSin[1/2*(2+2^(1/2))^(1/2)]=3/8*Pi",
	// "ArcSin[1/2*(2-2^(1/2))^(1/2)]=1/8*Pi", "ArcSin[1/2*2^(1/2)]=1/4*Pi",
	// "ArcSin[1/4*5^(1/2)+1/4]=3/10*Pi",
	// "ArcSin[1/4*6^(1/2)*(1+1/3*3^(1/2))]=5/12*Pi",
	// "ArcSin[1/4*2^(1/2)*(5-5^(1/2))^(1/2)]=1/5*Pi",
	// "ArcSin[1/4*6^(1/2)*(1-1/3*3^(1/2))]=1/12*Pi",
	// "ArcSin[1/4*5^(1/2)-1/4]=1/10*Pi",
	// "ArcSin[1/4*2^(1/2)*(5+5^(1/2))^(1/2)]=2/5*Pi",
	// "ArcSin[x_NumberQ]:= -ArcSin[-x] /; SignCmp[x]<0",
	// "ArcSin[x_NumberQ*y_]:= -ArcSin[-x*y] /; SignCmp[x]<0" };

	/**
	 *<pre>
	 * ArcSin[(5+5^(1/2))^(1/2)*1/4*2^(1/2)]=2/5*Pi,
	 *   ArcSin[(5-5^(1/2))^(1/2)*1/4*2^(1/2)]=1/5*Pi,
	 *   ArcSin[1/4+1/4*5^(1/2)]=3/10*Pi,
	 *   ArcSin[I]=I*ArcSinh[1],
	 *   ArcSin[1/2]=1/6*Pi,
	 *   ArcSin[1/2*2^(1/2)]=1/4*Pi,
	 *   ArcSin[1/2*3^(1/2)]=1/3*Pi,
	 *   ArcSin[1/2*(2+2^(1/2))^(1/2)]=3/8*Pi,
	 *   ArcSin[1/2*(2-2^(1/2))^(1/2)]=1/8*Pi,
	 *   ArcSin[-1/4+1/4*5^(1/2)]=1/10*Pi,
	 *   ArcSin[(1+1/3*3^(1/2))*1/4*6^(1/2)]=5/12*Pi,
	 *   ArcSin[(1-1/3*3^(1/2))*1/4*6^(1/2)]=1/12*Pi,
	 *   ArcSin[DirectedInfinity[1]]=-I*Infinity,
	 *   ArcSin[1]=1/2*Pi,
	 *   ArcSin[0]=0,
	 *   ArcSin[x_NumberQ*y_]:=(-1)*ArcSin[(-1)*x*y]/;SignCmp[x]<0,
	 *   ArcSin[x_NumberQ]:=(-1)*ArcSin[(-1)*x]/;SignCmp[x]<0
	 * </pre>
	 */
	final static IAST RULES = List(Set(ArcSin(Plus(Times(C1D4, Power(C5, C1D2)), C1D4)), Times(fraction(3L, 10L), Pi)), Set(
			ArcSin(Plus(Times(C1D4, Power(C5, C1D2)), Times(CN1, C1D4))), Times(fraction(1L, 10L), Pi)), Set(ArcSin(CInfinity), Times(
			Times(CN1, CI), CInfinity)), Set(ArcSin(Times(Times(C1D4, Power(C2, C1D2)), Power(Plus(Power(C5, C1D2), C5), C1D2))), Times(
			fraction(2L, 5L), Pi)), Set(ArcSin(Times(Times(C1D4, Power(C2, C1D2)), Power(Plus(Times(CN1, Power(C5, C1D2)), C5), C1D2))),
			Times(fraction(1L, 5L), Pi)), Set(
			ArcSin(Times(Times(C1D4, Plus(Times(C1D3, Power(C3, C1D2)), C1)), Power(integer(6L), C1D2))), Times(fraction(5L, 12L), Pi)),
			Set(ArcSin(Times(Times(C1D4, Plus(Times(CN1D3, Power(C3, C1D2)), C1)), Power(integer(6L), C1D2))), Times(fraction(1L, 12L),
					Pi)), Set(ArcSin(CI), Times(CI, ArcSinh(C1))), Set(ArcSin(C1D2), Times(fraction(1L, 6L), Pi)), Set(ArcSin(Times(C1D2,
					Power(C2, C1D2))), Times(C1D4, Pi)), Set(ArcSin(Times(C1D2, Power(C3, C1D2))), Times(C1D3, Pi)), Set(ArcSin(Times(C1D2,
					Power(Plus(Power(C2, C1D2), C2), C1D2))), Times(fraction(3L, 8L), Pi)), Set(ArcSin(Times(C1D2, Power(Plus(Times(CN1,
					Power(C2, C1D2)), C2), C1D2))), Times(fraction(1L, 8L), Pi)), Set(ArcSin(C1), Times(C1D2, Pi)), Set(ArcSin(C0), C0)
	// SetDelayed(ArcSin(Times($p("x",$s("NumberQ")),$p("y"))),Condition(Times(CN1,ArcSin(Times(Times(CN1,$s("x")),$s("y")))),Less(SignCmp($s("x")),C0))),
	// SetDelayed(ArcSin($p("x",$s("NumberQ"))),Condition(Times(CN1,ArcSin(Times(CN1,$s("x")))),Less(SignCmp($s("x")),C0)))
	);

	@Override
	public IAST getRuleAST() {
		return RULES;
	}

	public ArcSin() {
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (isNegativeExpression(arg1)) {
			return Times(CN1, ArcSin(Times(CN1, arg1)));
		}
		return null;
	}

	@Override
	public IExpr numericEvalD1(final Num arg1) {
		return F.num(Math.asin(arg1.getRealPart()));
	}

	@Override
	public IExpr numericEvalDC1(final ComplexNum arg1) {
		return ComplexUtils.asin(arg1);
	}

	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		return Math.asin(stack[top]);
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}

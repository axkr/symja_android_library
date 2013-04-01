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
 * Arctangent
 * 
 * See <a href="http://en.wikipedia.org/wiki/Inverse_trigonometric functions">
 * Inverse_trigonometric functions</a>
 */
public class ArcTan extends AbstractTrigArg1 implements INumeric {
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

	/**
	 * <pre>
	 * ArcTan[(5-2*5^(1/2))^(1/2)*1/5*5^(1/2)]=1/10*Pi,
	 *   ArcTan[(5+2*5^(1/2))^(1/2)*1/5*5^(1/2)]=3/10*Pi,
	 *   ArcTan[3^(1/2)]=1/3*Pi,
	 *   ArcTan[DirectedInfinity[1]]=1/2*Pi,
	 *   ArcTan[2-3^(1/2)]=1/12*Pi,
	 *   ArcTan[2+3^(1/2)]=5/12*Pi,
	 *   ArcTan[1+2^(1/2)]=3/8*Pi,
	 *   ArcTan[0]=0,
	 *   ArcTan[(5-2*5^(1/2))^(1/2)]=1/5*Pi,
	 *   ArcTan[0,0]=0,
	 *   ArcTan[1]=1/4*Pi,
	 *   ArcTan[(5+2*5^(1/2))^(1/2)]=2/5*Pi,
	 *   ArcTan[1/3*3^(1/2)]=1/6*Pi,
	 *   ArcTan[2^(1/2)-1]=1/8*Pi,
	 *   ArcTan[x_NumberQ*y_]:=(-1)*ArcTan[(-1)*x*y]/;SignCmp[x]<0,
	 *   ArcTan[x_NumberQ]:=(-1)*ArcTan[(-1)*x]/;SignCmp[x]<0
	 * </pre>
	 */
	final static IAST RULES = List(Set(ArcTan(CInfinity), Times(C1D2, Pi)), Set(ArcTan(Power(C3, C1D2)), Times(C1D3, Pi)), Set(
			ArcTan(Plus(Times(CN1, Power(C3, C1D2)), C2)), Times(fraction(1L, 12L), Pi)), Set(ArcTan(Plus(Power(C3, C1D2), C2)), Times(
			fraction(5L, 12L), Pi)), Set(ArcTan(Plus(Power(C2, C1D2), Times(CN1, C1))), Times(fraction(1L, 8L), Pi)), Set(ArcTan(Plus(
			Power(C2, C1D2), C1)), Times(fraction(3L, 8L), Pi)), Set(ArcTan(C0), C0), Set(ArcTan(Power(Plus(Times(integer(-2L), Power(C5,
			C1D2)), C5), C1D2)), Times(fraction(1L, 5L), Pi)), Set(ArcTan(C0, C0), C0), Set(ArcTan(C1), Times(C1D4, Pi)), Set(
			ArcTan(Power(Plus(Times(C2, Power(C5, C1D2)), C5), C1D2)), Times(fraction(2L, 5L), Pi)), Set(ArcTan(Times(C1D3, Power(C3,
			C1D2))), Times(fraction(1L, 6L), Pi)), Set(ArcTan(Times(Times(fraction(1L, 5L), Power(C5, C1D2)), Power(Plus(Times(
			integer(-2L), Power(C5, C1D2)), C5), C1D2))), Times(fraction(1L, 10L), Pi)), Set(ArcTan(Times(Times(fraction(1L, 5L), Power(
			C5, C1D2)), Power(Plus(Times(C2, Power(C5, C1D2)), C5), C1D2))), Times(fraction(3L, 10L), Pi))
	// SetDelayed(ArcTan(Times($p("x",$s("NumberQ")),$p("y"))),Condition(Times(CN1,ArcTan(Times(Times(CN1,$s("x")),$s("y")))),Less(SignCmp($s("x")),C0))),
	// SetDelayed(ArcTan($p("x",$s("NumberQ"))),Condition(Times(CN1,ArcTan(Times(CN1,$s("x")))),Less(SignCmp($s("x")),C0)))
	);

	@Override
	public IAST getRuleAST() {
		return RULES;
	}

	public ArcTan() {
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (isNegativeExpression(arg1)) {
			return Times(CN1, ArcTan(Times(CN1, arg1)));
		}
		return null;
	}
	
	@Override
	public IExpr numericEvalD1(final Num arg1) {
		return F.num(Math.atan(arg1.getRealPart()));
	}

	@Override
	public IExpr numericEvalDC1(final ComplexNum arg1) {
		return ComplexUtils.atan(arg1);
	}

	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		return Math.atan(stack[top]);
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}

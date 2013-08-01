package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.ComplexUtils;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.Num;
import static org.matheclipse.core.expression.F.*;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Sine function.
 * 
 * See <a
 * href="http://en.wikipedia.org/wiki/Trigonometric_functions">Trigonometric
 * functions</a>
 */
public class Sin extends AbstractTrigArg1 implements INumeric {

	// final static String[] RULES = {
	// "Sin[0]=0",
	// "Sin[1/6*Pi]=1/2",
	// "Sin[1/4*Pi]=2^(1/2)/2",
	// "Sin[1/3*Pi]=3^(1/2)/2",
	// "Sin[1/2*Pi]=1",
	// "Sin[Pi]=0",
	// "Sin[5/12*Pi]=1/4*6^(1/2)*(1+1/3*3^(1/2))",
	// "Sin[Pi/5]=1/4*2^(1/2)*(5-5^(1/2))^(1/2)",
	// "Sin[Pi/12]=1/4*6^(1/2)*(1-1/3*3^(1/2))",
	// "Sin[Pi/10]=1/4*5^(1/2)-1/4",
	// "Sin[2/5*Pi]=1/4*2^(1/2)*(5+5^(1/2))^(1/2)",
	// "Sin[3/10*Pi]=1/4*5^(1/2)+1/4",
	// "Sin[3/8*Pi]=1/2*(2+2^(1/2))^(1/2)",
	// "Sin[1/8*Pi]=1/2*(2-2^(1/2))^(1/2)",
	// "Sin[I]=I*Sinh[1]",
	// "Sin[ArcSin[x_]]:=x",
	// "Sin[ArcCos[x_]]:=(1-x^2)^(1/2)",
	// "Sin[ArcTan[x_]]:=x/(1+x^2)^(1/2)",
	// "Sin[x_NumberQ]:= -Sin[-x] /; SignCmp[x]<0",
	// "Sin[x_NumberQ*y_]:=-Sin[-x*y] /; SignCmp[x]<0",
	// "Sin[x_NumberQ*Pi]:=If[x<1, Sin[(1-x)*Pi],If[x<2,-Sin[(2-x)*Pi],
	// Sin[(x-2*Quotient[Trunc[x],2])*Pi] ] ] /; x>=1/2"
	// };

	final static IAST RULES = 
		List(
				Set(Sin(Pi),C0),
				Set(Sin(Times(fraction(1L,12L),Pi)),Times(Times(C1D4,Plus(Times(CN1D3,Power(C3,C1D2)),C1)),Power(integer(6L),C1D2))),
				Set(Sin(Times(fraction(1L,10L),Pi)),Plus(Times(C1D4,Power(C5,C1D2)),Times(CN1,C1D4))),
				Set(Sin(Times(C1D2,Pi)),C1),
				Set(Sin(Times(C1D4,Pi)),Times(C1D2,Power(C2,C1D2))),
				Set(Sin(Times(fraction(2L,5L),Pi)),Times(Times(C1D4,Power(C2,C1D2)),Power(Plus(Power(C5,C1D2),C5),C1D2))),
				Set(Sin(Times(C1D3,Pi)),Times(C1D2,Power(C3,C1D2))),
				Set(Sin(Times(fraction(1L,6L),Pi)),C1D2),
				Set(Sin(Times(fraction(3L,8L),Pi)),Times(C1D2,Power(Plus(Power(C2,C1D2),C2),C1D2))),
				Set(Sin(Times(fraction(1L,5L),Pi)),Times(Times(C1D4,Power(C2,C1D2)),Power(Plus(Times(CN1,Power(C5,C1D2)),C5),C1D2))),
				Set(Sin(Times(fraction(3L,10L),Pi)),Plus(Times(C1D4,Power(C5,C1D2)),C1D4)),
				Set(Sin(Times(fraction(1L,8L),Pi)),Times(C1D2,Power(Plus(Times(CN1,Power(C2,C1D2)),C2),C1D2))),
				Set(Sin(Times(fraction(5L,12L),Pi)),Times(Times(C1D4,Plus(Times(C1D3,Power(C3,C1D2)),C1)),Power(integer(6L),C1D2))),
				Set(Sin(CI),Times(CI,Sinh(C1))),
				Set(Sin(C0),C0),
				SetDelayed(Sin(ArcSin($p("x"))),$s("x")),
				SetDelayed(Sin(ArcCos($p("x"))),Power(Plus(C1,Times(CN1,Power($s("x"),C2))),C1D2)),
				SetDelayed(Sin(ArcTan($p("x"))),Times($s("x"),Power(Plus(C1,Power($s("x"),C2)),Power(C1D2,CN1)))),
//				SetDelayed(Sin(Times($p("x",$s("NumberQ")),$p("y"))),Condition(Times(CN1,Sin(Times(Times(CN1,$s("x")),$s("y")))),Less(SignCmp($s("x")),C0))),
				SetDelayed(Sin(Times(Pi,$p("x",$s("NumberQ")))),Condition(If(Less($s("x"),C1),Sin(Times(Plus(C1,Times(CN1,$s("x"))),Pi)),If(Less($s("x"),C2),Times(CN1,Sin(Times(Plus(C2,Times(CN1,$s("x"))),Pi))),Sin(Times(Plus($s("x"),Times(CN1,Times(C2,Quotient(Trunc($s("x")),C2)))),Pi)))),GreaterEqual($s("x"),C1D2)))
//				SetDelayed(Sin($p("x",$s("NumberQ"))),Condition(Times(CN1,Sin(Times(CN1,$s("x")))),Less(SignCmp($s("x")),C0)))
				);
				
	public Sin() {
	}

	// @Override
	// public String[] getRules() {
	// return RULES;
	// }

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (AbstractFunctionEvaluator.isNegativeExpression(arg1)) {
			return Times(CN1, Sin(Times(CN1, arg1)));
		}
		return null;
	}

	@Override
	public IAST getRuleAST() {
		return RULES;
	}

	@Override
	public IExpr numericEvalD1(final Num arg1) {
		return num(Math.sin(arg1.getRealPart()));
	}

	@Override
	public IExpr numericEvalDC1(final ComplexNum arg1) {
		return ComplexUtils.sin(arg1);
	}

	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		return Math.sin(stack[top]);
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}

package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.*; 

import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.ComplexUtils;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Cosine function
 * 
 * See <a href="http://en.wikipedia.org/wiki/Trigonometric_functions">Trigonometric functions</a> and <a href="http://en.wikipedia.org/wiki/Exact_trigonometric_constants">Wikipedia - Exact trigonometric constants</a>
 */
public class Cos extends AbstractTrigArg1 implements INumeric {
 
//{
//  Cos[1/10*Pi]=1/4*2^(1/2)*(5+5^(1/2))^(1/2),
//  Cos[1/12*Pi]=1/4*(1+1/3*3^(1/2))*6^(1/2),
//  Cos[1/6*Pi]=1/2*3^(1/2),
//  Cos[3/8*Pi]=1/2*(2-2^(1/2))^(1/2),
//  Cos[1/8*Pi]=1/2*(2+2^(1/2))^(1/2),
//  Cos[Pi]=-1,
//  Cos[5/12*Pi]=1/4*(1-1/3*3^(1/2))*6^(1/2),
//  Cos[3/10*Pi]=1/4*2^(1/2)*(5-5^(1/2))^(1/2),
//  Cos[1/2*Pi]=0,
//  Cos[1/3*Pi]=1/2,
//  Cos[1/4*Pi]=1/2*2^(1/2),
//  Cos[2/5*Pi]=-1/4+1/4*5^(1/2),
//  Cos[1/5*Pi]=1/4+1/4*5^(1/2),
//  Cos[0]=1,
//  Cos[I]=Cosh[1],
//  Cos[ArcTan[x_]]:=(1+x^2)^(-1/2),
//  Cos[Pi*x_NumberQ]:=If[x<1,(-1)*Cos[(1-x)*Pi],If[x<2,Cos[(2-x)*Pi],Cos[(x-2*Quotient[IntegerPart[x],2])*Pi]]]/;x>=1/2,
//  Cos[ArcCos[x_]]:=x,
//  Cos[ArcSin[x_]]:=(1-x^2)^(1/2)
//  }
	  
	final static IAST RULES = List(
			Set(Cos(Times(fraction(1L,10L),Pi)),Times(Times(C1D4,Power(C2,C1D2)),Power(Plus(C5,Power(C5,C1D2)),C1D2))),
			Set(Cos(Times(fraction(1L,12L),Pi)),Times(Times(C1D4,Plus(C1,Times(C1D3,Power(C3,C1D2)))),Power(integer(6L),C1D2))),
			Set(Cos(Times(fraction(1L,6L),Pi)),Times(C1D2,Power(C3,C1D2))),
			Set(Cos(Times(fraction(3L,8L),Pi)),Times(C1D2,Power(Plus(C2,Times(CN1,Power(C2,C1D2))),C1D2))),
			Set(Cos(Times(fraction(1L,8L),Pi)),Times(C1D2,Power(Plus(C2,Power(C2,C1D2)),C1D2))),
			Set(Cos(Pi),CN1),
			Set(Cos(Times(fraction(5L,12L),Pi)),Times(Times(C1D4,Plus(C1,Times(CN1,Times(C1D3,Power(C3,C1D2))))),Power(integer(6L),C1D2))),
			Set(Cos(Times(fraction(3L,10L),Pi)),Times(Times(C1D4,Power(C2,C1D2)),Power(Plus(C5,Times(CN1,Power(C5,C1D2))),C1D2))),
			Set(Cos(Times(C1D2,Pi)),C0),
			Set(Cos(Times(C1D3,Pi)),C1D2),
			Set(Cos(Times(C1D4,Pi)),Times(C1D2,Power(C2,C1D2))),
			Set(Cos(Times(fraction(2L,5L),Pi)),Plus(CN1D4,Times(C1D4,Power(C5,C1D2)))),
			Set(Cos(Times(fraction(1L,5L),Pi)),Plus(C1D4,Times(C1D4,Power(C5,C1D2)))),
			Set(Cos(C0),C1),
			Set(Cos(CI),Cosh(C1)),
			SetDelayed(Cos(ArcTan($p(x))),Power(Plus(C1,Power(x,C2)),CN1D2)),
			SetDelayed(Cos(Times(Pi,$p(x,$s("NumberQ")))),Condition(If(Less(x,C1),Times(CN1,Cos(Times(Plus(C1,Times(CN1,x)),Pi))),If(Less(x,C2),Cos(Times(Plus(C2,Times(CN1,x)),Pi)),Cos(Times(Plus(x,Times(CN1,Times(C2,Quotient(IntegerPart(x),C2)))),Pi)))),GreaterEqual(x,C1D2))),
			SetDelayed(Cos(ArcCos($p(x))),x),
			SetDelayed(Cos(ArcSin($p(x))),Power(Plus(C1,Times(CN1,Power(x,C2))),C1D2))
			);

	@Override
	public IAST getRuleAST() {
		return RULES;
	}

	public Cos() {
	}
	
	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (AbstractFunctionEvaluator.isNegativeExpression(arg1)) {
			return Cos(Times(CN1, arg1));
		}
		return null;
	}
	
	@Override
	public IExpr numericEvalD1(final Num arg1) {
		return F.num(Math.cos(arg1.getRealPart()));
	}

	@Override
	public IExpr numericEvalDC1(final ComplexNum arg1) {
		return ComplexUtils.cos(arg1);
	}
 
	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		return Math.cos(stack[top]);
	}

//	public IExpr evalInteger(IInteger i) {
//		if (i.equals(F.C0)) {
//			return F.C1;
//		}
//		return null;
//	}

//	public void setUp(ISymbol symbol) throws SyntaxError {
//		super.setUp(symbol);
//		createRuleFromMethodName(symbol, "Cos[x_Integer]", "evalInteger");
//	}
	@Override
  public void setUp(final ISymbol symbol) throws SyntaxError {
    symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    super.setUp(symbol);
  }
}

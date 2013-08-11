package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.*; 

import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
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
 * Tan
 * 
 * See <a href="http://en.wikipedia.org/wiki/Trigonometric_functions">Trigonometric functions</a>
 */
public class Tan extends AbstractTrigArg1 implements INumeric {
 
//{
//  Tan[0]=0,
//  Tan[1/3*Pi]=3^(1/2),
//  Tan[1/5*Pi]=(5-2*5^(1/2))^(1/2),
//  Tan[1/4*Pi]=1,
//  Tan[2/5*Pi]=(5+2*5^(1/2))^(1/2),
//  Tan[1/6*Pi]=1/3*3^(1/2),
//  Tan[3/8*Pi]=1+2^(1/2),
//  Tan[1/8*Pi]=-1+2^(1/2),
//  Tan[1/12*Pi]=2-3^(1/2),
//  Tan[5/12*Pi]=2+3^(1/2),
//  Tan[1/10*Pi]=1/5*Sqrt[25-10*Sqrt[5]],
//  Tan[3/10*Pi]=1/5*Sqrt[25+10*Sqrt[5]],
//  Tan[Pi]=0,
//  Tan[Pi/2]=ComplexInfinity,
//  Tan[I]=I*Tanh[1],
//  Tan[ArcSin[x_]]:=x*(1-x^2)^(1/2)^(-1),
//  Tan[Pi*x_NumberQ]:=If[x<1,(-1)*Tan[(1-x)*Pi],If[x<2,Tan[(x-1)*Pi],Tan[(x-2*Quotient[IntegerPart[x],2])*Pi]]]/;x>1/2,
//  Tan[ArcTan[x_]]:=x,
//  Tan[ArcCos[x_]]:=(1-x^2)^(1/2)*x^(-1),
//  Tan[ArcCot[x_]]:=x^(-1)
//}
	  
	final static IAST RULES = List(
			Set(Tan(C0),C0),
			Set(Tan(Times(C1D3,Pi)),Power(C3,C1D2)),
			Set(Tan(Times(fraction(1L,5L),Pi)),Power(Plus(C5,Times(CN1,Times(C2,Power(C5,C1D2)))),C1D2)),
			Set(Tan(Times(C1D4,Pi)),C1),
			Set(Tan(Times(fraction(2L,5L),Pi)),Power(Plus(C5,Times(C2,Power(C5,C1D2))),C1D2)),
			Set(Tan(Times(fraction(1L,6L),Pi)),Times(C1D3,Power(C3,C1D2))),
			Set(Tan(Times(fraction(3L,8L),Pi)),Plus(C1,Power(C2,C1D2))),
			Set(Tan(Times(fraction(1L,8L),Pi)),Plus(CN1,Power(C2,C1D2))),
			Set(Tan(Times(fraction(1L,12L),Pi)),Plus(C2,Times(CN1,Power(C3,C1D2)))),
			Set(Tan(Times(fraction(5L,12L),Pi)),Plus(C2,Power(C3,C1D2))),
			Set(Tan(Times(fraction(1L,10L),Pi)),Times(fraction(1L,5L),Sqrt(Plus(integer(25L),Times(CN1,Times(integer(10L),Sqrt(C5))))))),
			Set(Tan(Times(fraction(3L,10L),Pi)),Times(fraction(1L,5L),Sqrt(Plus(integer(25L),Times(integer(10L),Sqrt(C5)))))),
			Set(Tan(Pi),C0),
			Set(Tan(Times(C1D2,Pi)),CComplexInfinity),
			Set(Tan(CI),Times(CI,Tanh(C1))),
			SetDelayed(Tan(ArcSin($p(x))),Times(x,Power(Plus(C1,Times(CN1,Power(x,C2))),Power(C1D2,CN1)))),
			SetDelayed(Tan(Times(Pi,$p(x,$s("NumberQ")))),Condition(If(Less(x,C1),Times(CN1,Tan(Times(Plus(C1,Times(CN1,x)),Pi))),If(Less(x,C2),Tan(Times(Plus(x,Times(CN1,C1)),Pi)),Tan(Times(Plus(x,Times(CN1,Times(C2,Quotient(IntegerPart(x),C2)))),Pi)))),Greater(x,C1D2))),
			SetDelayed(Tan(ArcTan($p(x))),x),
			SetDelayed(Tan(ArcCos($p(x))),Times(Power(Plus(C1,Times(CN1,Power(x,C2))),C1D2),Power(x,CN1))),
			SetDelayed(Tan(ArcCot($p(x))),Power(x,CN1))
			);

	@Override
	public IAST getRuleAST() {
		return RULES;
	}
	
	public Tan() {
	}
	
	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (AbstractFunctionEvaluator.isNegativeExpression(arg1)) {
			return Times(CN1, Tan(Times(CN1, arg1)));
		}
		return null;
	}
	
	@Override
	public IExpr numericEvalD1(final Num arg1) {
		return F.num(Math.tan(arg1.getRealPart()));
	}

	@Override
	public IExpr numericEvalDC1(final ComplexNum arg1) {
		return ComplexUtils.tan(arg1);
	}

	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		return Math.tan(stack[top]);
	}
	
	@Override
  public void setUp(final ISymbol symbol) throws SyntaxError {
    symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    super.setUp(symbol);
  }
}

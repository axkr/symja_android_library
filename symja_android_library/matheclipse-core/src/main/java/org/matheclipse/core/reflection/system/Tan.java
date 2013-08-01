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
	// final static String[] RULES = { "Tan[Pi]=0", "Tan[I]=I*Tanh[1]",
	// "Tan[0]=0", "Tan[Pi/6]=3^(1/2)/3", "Tan[Pi/4]=1",
	// "Tan[Pi/3]=3^(1/2)",
	// // "Tan[Pi/2]:=Throw(SingularityError,Tan)",
	// "Tan[5/12*Pi]=2+3^(1/2)", "Tan[1/5*Pi]=(5-2*5^(1/2))^(1/2)",
	// "Tan[1/12*Pi]=2-3^(1/2)",
	// "Tan[1/10*Pi]=1/5*5^(1/2)*(5-2*5^(1/2))^(1/2)",
	// "Tan[2/5*Pi]=(5+2*5^(1/2))^(1/2)",
	// "Tan[3/10*Pi]=1/5*5^(1/2)*(5+2*5^(1/2))^(1/2)", "Tan[1/3*Pi]=3^(1/2)",
	// "Tan[3/8*Pi]=2^(1/2)+1", "Tan[1/8*Pi]=2^(1/2)-1",
	// "Tan[ArcSin[x_]]:=x/(1-x^2)^(1/2)", "Tan[ArcCos[x_]]:=(1-x^2)^(1/2)/x",
	// "Tan[ArcTan[x_]]:=x", "Tan[ACot[x_]]:=1/x",
	// "Tan[x_NumberQ]:= -Tan[-x] /; SignCmp[x]<0", "Tan[x_NumberQ*y_]:=
	// -Tan[-x*y] /; SignCmp[x]<0",
	// "Tan[x_NumberQ*Pi]:=If[x<1,-Tan[(1-x)*Pi],If[x<2,Tan[(x-1)*Pi],Tan[(x-2*Quotient[Trunc[x],2])*Pi]]]
	// /; x>1/2"
	//
	// };


	/**
	 * <pre>
	 *   Tan[0]=0,
  Tan[1/3*Pi]=3^(1/2),
  Tan[1/5*Pi]=(5-2*5^(1/2))^(1/2),
  Tan[1/4*Pi]=1,
  Tan[2/5*Pi]=(5+2*5^(1/2))^(1/2),
  Tan[1/6*Pi]=1/3*3^(1/2),
  Tan[3/8*Pi]=1+2^(1/2),
  Tan[1/8*Pi]=-1+2^(1/2),
  Tan[5/12*Pi]=2+3^(1/2),
  Tan[3/10*Pi]=1/5*5^(1/2)*(5+2*5^(1/2))^(1/2),
  Tan[1/10*Pi]=1/5*5^(1/2)*(5-2*5^(1/2))^(1/2),
  Tan[1/12*Pi]=2-3^(1/2),
  Tan[Pi]=0,
  Tan[I]=I*Tanh[1],
  Tan[ArcSin[x_]]:=x*(1-x^2)^(1/2)^(-1),
  Tan[x_NumberQ*y_]:=(-1)*Tan[(-1)*x*y]/;SignCmp[x]<0,
  Tan[Pi*x_NumberQ]:=If[x<1,(-1)*Tan[(1-x)*Pi],If[x<2,Tan[(x-1)*Pi],Tan[(x-2*Quotient[Trunc[x],2])*Pi]]]/;x>1/2,
  Tan[ArcTan[x_]]:=x,
  Tan[ArcCos[x_]]:=(1-x^2)^(1/2)*x^(-1),
  Tan[ArcCot[x_]]:=x^(-1),
  Tan[x_NumberQ]:=(-1)*Tan[(-1)*x]/;SignCmp[x]<0
	 </pre>
	 */
	final static IAST RULES = List(
			Set(Tan(CI),Times(CI,Tanh(C1))),
			Set(Tan(C0),C0),
			Set(Tan(Times(C1D3,Pi)),Power(C3,C1D2)),
			Set(Tan(Times(fraction(1L,5L),Pi)),Power(Plus(Times(integer(-2L),Power(C5,C1D2)),C5),C1D2)),
			Set(Tan(Times(C1D4,Pi)),C1),
			Set(Tan(Times(fraction(2L,5L),Pi)),Power(Plus(Times(C2,Power(C5,C1D2)),C5),C1D2)),
			Set(Tan(Times(fraction(1L,6L),Pi)),Times(C1D3,Power(C3,C1D2))),
			Set(Tan(Times(fraction(3L,8L),Pi)),Plus(Power(C2,C1D2),C1)),
			Set(Tan(Times(fraction(1L,8L),Pi)),Plus(Power(C2,C1D2),CN1)),
			Set(Tan(Times(fraction(5L,12L),Pi)),Plus(Power(C3,C1D2),C2)),
			Set(Tan(Times(fraction(3L,10L),Pi)),Times(Times(fraction(1L,5L),Power(C5,C1D2)),Power(Plus(Times(C2,Power(C5,C1D2)),C5),C1D2))),
			Set(Tan(Times(fraction(1L,10L),Pi)),Times(Times(fraction(1L,5L),Power(C5,C1D2)),Power(Plus(Times(integer(-2L),Power(C5,C1D2)),C5),C1D2))),
			Set(Tan(Times(fraction(1L,12L),Pi)),Plus(Times(CN1,Power(C3,C1D2)),C2)),
			Set(Tan(Pi),C0),
			SetDelayed(Tan(ArcSin($p("x"))),Times($s("x"),Power(Plus(Times(CN1,Power($s("x"),C2)),C1),Power(C1D2,CN1)))),
//			SetDelayed(Tan(Times($p("x",$s("NumberQ")),$p("y"))),Condition(Times(CN1,Tan(Times(Times(CN1,$s("x")),$s("y")))),Less(SignCmp($s("x")),C0))),
			SetDelayed(Tan(Times(Pi,$p("x",$s("NumberQ")))),Condition(If(Less($s("x"),C1),Times(CN1,Tan(Times(Plus(Times(CN1,$s("x")),C1),Pi))),If(Less($s("x"),C2),Tan(Times(Plus(CN1,$s("x")),Pi)),Tan(Times(Plus(Times(integer(-2L),Quotient(Trunc($s("x")),C2)),$s("x")),Pi)))),Greater($s("x"),C1D2))),
			SetDelayed(Tan(ArcTan($p("x"))),$s("x")),
			SetDelayed(Tan(ArcCos($p("x"))),Times(Power(Plus(Times(CN1,Power($s("x"),C2)),C1),C1D2),Power($s("x"),CN1))),
			SetDelayed(Tan(ArcCot($p("x"))),Power($s("x"),CN1))
//			SetDelayed(Tan($p("x",$s("NumberQ"))),Condition(Times(CN1,Tan(Times(CN1,$s("x")))),Less(SignCmp($s("x")),C0)))
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

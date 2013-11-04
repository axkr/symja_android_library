package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.CComplexInfinity;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Less;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Sec;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.SetDelayed;
import static org.matheclipse.core.expression.F.SignCmp;
import static org.matheclipse.core.expression.F.Times;

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
 * Secant function
 * 
 * See <a
 * href="http://en.wikipedia.org/wiki/Trigonometric_functions">Trigonometric
 * functions</a>
 */
public class Sec extends AbstractTrigArg1 implements INumeric {

//	 {
//   Sec[0]=1,
//	 Sec[Pi/2]=ComplexInfinity,
//	 Sec[Pi/3]=2,
//	 Sec[Pi/4]=Sqrt[2],
//   Sec[Pi/5]=Sqrt[5]-1,
//	 Sec[Pi/6]=2/3*Sqrt[3],
//	 Sec[Pi/8]=Sqrt[4-2*Sqrt[2]],
//	 Sec[Pi/10]=1/5*Sqrt[50-10*Sqrt[5]],
//   Sec[Pi/12]=Sqrt[6]-Sqrt[2],
//	 Sec[3/8*Pi]=Sqrt[4+2*Sqrt[2]],	
//	 Sec[3/10*Pi]=1/5*Sqrt[50+10*Sqrt[5]],
//	 Sec[2/5*Pi]=1+Sqrt[5],
//	 Sec[5/12*Pi]=Sqrt[6]+Sqrt[2]
//	 }

	final static IAST RULES = List(
			Set(Sec(C0),C1),
			Set(Sec(Times(C1D2,Pi)),CComplexInfinity),
			Set(Sec(Times(C1D3,Pi)),C2),
			Set(Sec(Times(C1D4,Pi)),Sqrt(C2)),
			Set(Sec(Times(fraction(1L,5L),Pi)),Plus(Sqrt(C5),Times(CN1,C1))),
			Set(Sec(Times(fraction(1L,6L),Pi)),Times(fraction(2L,3L),Sqrt(C3))),
			Set(Sec(Times(fraction(1L,8L),Pi)),Sqrt(Plus(C4,Times(CN1,Times(C2,Sqrt(C2)))))),
			Set(Sec(Times(fraction(1L,10L),Pi)),Times(fraction(1L,5L),Sqrt(Plus(integer(50L),Times(CN1,Times(integer(10L),Sqrt(C5))))))),
			Set(Sec(Times(fraction(1L,12L),Pi)),Plus(Sqrt(integer(6L)),Times(CN1,Sqrt(C2)))),
			Set(Sec(Times(fraction(3L,8L),Pi)),Sqrt(Plus(C4,Times(C2,Sqrt(C2))))),
			Set(Sec(Times(fraction(3L,10L),Pi)),Times(fraction(1L,5L),Sqrt(Plus(integer(50L),Times(integer(10L),Sqrt(C5)))))),
			Set(Sec(Times(fraction(2L,5L),Pi)),Plus(C1,Sqrt(C5))),
			Set(Sec(Times(fraction(5L,12L),Pi)),Plus(Sqrt(integer(6L)),Sqrt(C2)))
			);

	@Override
	public IAST getRuleAST() {
		return RULES;
	}

	public Sec() {
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (AbstractFunctionEvaluator.isNegativeExpression(arg1)) {
			return Sec(Times(CN1, arg1));
		}
		return null;
	}

	@Override
	public IExpr numericEvalD1(final Num arg1) {
		return F.num(1.0D / Math.cos(arg1.getRealPart()));
	}

	@Override
	public IExpr numericEvalDC1(final ComplexNum arg1) {
		return ComplexUtils.cos(arg1).inverse();
	}

	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		return 1.0D / Math.cos(stack[top]);
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}

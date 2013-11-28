package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Less;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Sec;
import static org.matheclipse.core.expression.F.SetDelayed;
import static org.matheclipse.core.expression.F.SignCmp;
import static org.matheclipse.core.expression.F.Tan;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.$s;

import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.ComplexUtils;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Cosecant function
 * 
 * See <a
 * href="http://en.wikipedia.org/wiki/Trigonometric_functions">Trigonometric
 * functions</a>
 */
public class Csc extends AbstractTrigArg1 implements INumeric {
 
//	 {
//	        Csc[0]=ComplexInfinity,
//      	Csc[Pi/2]=1,
//	        Csc[Pi/3]=2/3*Sqrt[3],
//	        Csc[Pi/4]=Sqrt[2],
//          Csc[Pi/5]=1/5*Sqrt[50+10*Sqrt[5]],
//	        Csc[Pi/6]=2,
//	        Csc[Pi/8]=Sqrt[4+2*Sqrt[2]],
//	        Csc[Pi/10]=1+Sqrt[5],
//	        Csc[Pi/12]=Sqrt[6]+Sqrt[2],
//	        Csc[3/8*Pi]=Sqrt[4-2*Sqrt[2]],
//	        Csc[3/10*Pi]=Sqrt[5]-1,
//	        Csc[2/5*Pi]=1/5*Sqrt[50-10*Sqrt[5]],
//			Csc[5/12*Pi]=Sqrt[6]-Sqrt[2]
//	 }
	 
	final static IAST RULES = List(
			Set(Csc(C0),CComplexInfinity),
			Set(Csc(Times(C1D2,Pi)),C1),
			Set(Csc(Times(C1D3,Pi)),Times(fraction(2L,3L),Sqrt(C3))),
			Set(Csc(Times(C1D4,Pi)),Sqrt(C2)),
			Set(Csc(Times(fraction(1L,5L),Pi)),Times(fraction(1L,5L),Sqrt(Plus(integer(50L),Times(integer(10L),Sqrt(C5)))))),
			Set(Csc(Times(fraction(1L,6L),Pi)),C2),
			Set(Csc(Times(fraction(1L,8L),Pi)),Sqrt(Plus(C4,Times(C2,Sqrt(C2))))),
			Set(Csc(Times(fraction(1L,10L),Pi)),Plus(C1,Sqrt(C5))),
			Set(Csc(Times(fraction(1L,12L),Pi)),Plus(Sqrt(integer(6L)),Sqrt(C2))),
			Set(Csc(Times(fraction(3L,8L),Pi)),Sqrt(Plus(C4,Times(CN1,Times(C2,Sqrt(C2)))))),
			Set(Csc(Times(fraction(3L,10L),Pi)),Plus(Sqrt(C5),Times(CN1,C1))),
			Set(Csc(Times(fraction(2L,5L),Pi)),Times(fraction(1L,5L),Sqrt(Plus(integer(50L),Times(CN1,Times(integer(10L),Sqrt(C5))))))),
			Set(Csc(Times(fraction(5L,12L),Pi)),Plus(Sqrt(integer(6L)),Times(CN1,Sqrt(C2))))
			);

	@Override
	public IAST getRuleAST() {
		return RULES;
	}
	
	public Csc() {
	}
	
	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (AbstractFunctionEvaluator.isNegativeExpression(arg1)) {
			return Times(CN1, Csc(Times(CN1, arg1)));
		}
		IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
		if (imPart != null) {
			return F.Times(F.CNI, F.Csch(imPart));
		}
		IExpr[] parts = AbstractFunctionEvaluator.getPeriodicParts(arg1);
		if (parts != null) {
			if (parts[1].isInteger()) {
				// period 2*Pi
				IInteger i = (IInteger) parts[1];
				if (i.isEven()) {
					return F.Csc(parts[0]);
				} else {
					return F.Times(F.CN1, F.Csc(parts[0]));
				}
			}
		}
		return null;
	}

	@Override
	public IExpr numericEvalD1(final Num arg1) {
		return F.num(1.0D / Math.sin(arg1.getRealPart()));
	}

	@Override
	public IExpr numericEvalDC1(final ComplexNum arg1) {
		return ComplexUtils.sin(arg1).inverse();
	}

	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		return 1.0D / Math.sin(stack[top]);
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}

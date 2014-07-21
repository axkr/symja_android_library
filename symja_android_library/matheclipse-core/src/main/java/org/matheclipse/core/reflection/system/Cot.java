package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Cot;
import static org.matheclipse.core.expression.F.Times;

import org.apache.commons.math3.complex.Complex;
import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.rules.CotRules;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Cotangent function
 * 
 * See <a href="http://en.wikipedia.org/wiki/Trigonometric_functions">Trigonometric functions</a>
 */
public class Cot extends AbstractTrigArg1 implements INumeric, CotRules {

	// {
	// Cot[Pi/4]=1,
	// Cot[Pi/5]=1/5*Sqrt[25+10*Sqrt[5]],
	// Cot[Pi/6]=3^(1/2),
	// Cot[Pi/8]=Sqrt[2]+1,
	// Cot[Pi/10]=Sqrt[5+2*Sqrt[5]],
	// Cot[Pi/12]=2+3^(1/2),
	// Cot[0]=ComplexInfinity,
	// Cot[5/12*Pi]=2-Sqrt[3],
	// Cot[2/5*Pi]=1/5*Sqrt[25-10*Sqrt[5]],
	// Cot[3/10*Pi]=Sqrt[5-2*Sqrt[5]]
	// }

	// final static IAST RULES = List(
	// Set(Cot(Times(C1D4,Pi)),C1),
	// Set(Cot(Times(fraction(1L,5L),Pi)),Times(fraction(1L,5L),Sqrt(Plus(integer(25L),Times(integer(10L),Sqrt(C5)))))),
	// Set(Cot(Times(fraction(1L,6L),Pi)),Power(C3,C1D2)),
	// Set(Cot(Times(fraction(1L,8L),Pi)),Plus(Sqrt(C2),C1)),
	// Set(Cot(Times(fraction(1L,10L),Pi)),Sqrt(Plus(C5,Times(C2,Sqrt(C5))))),
	// Set(Cot(Times(fraction(1L,12L),Pi)),Plus(C2,Power(C3,C1D2))),
	// Set(Cot(C0),CComplexInfinity),
	// Set(Cot(Times(fraction(5L,12L),Pi)),Plus(C2,Times(CN1,Sqrt(C3)))),
	// Set(Cot(Times(fraction(2L,5L),Pi)),Times(fraction(1L,5L),Sqrt(Plus(integer(25L),Times(CN1,Times(integer(10L),Sqrt(C5))))))),
	// Set(Cot(Times(fraction(3L,10L),Pi)),Sqrt(Plus(C5,Times(CN1,Times(C2,Sqrt(C5))))))
	// );

	@Override
	public IAST getRuleAST() {
		return RULES;
	}

	public Cot() {
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (AbstractFunctionEvaluator.isNegativeExpression(arg1)) {
			return Times(CN1, Cot(Times(CN1, arg1)));
		}
		IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
		if (imPart != null) {
			return F.Times(F.CNI, F.Coth(imPart));
		}
		IExpr[] parts = AbstractFunctionEvaluator.getPeriodicParts(arg1);
		if (parts != null) {
			if (parts[1].isInteger()) {
				// period Pi
				return F.Cot(parts[0]);
			}
		}
		return null;
	}

	@Override
	public IExpr e1DblArg(final double arg1) {
		return F.num(1.0D / Math.tan(arg1));
	}

	@Override
	public IExpr e1ComplexArg(final Complex arg1) {
		return F.complexNum(arg1.tan().reciprocal());
	}
	
	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		return 1.0D / Math.tan(stack[top]);
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}

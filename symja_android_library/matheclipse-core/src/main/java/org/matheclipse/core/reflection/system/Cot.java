package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.ArcSin;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C1D4;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.ComplexInfinity;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cot;
import static org.matheclipse.core.expression.F.Less;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Pi;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.SetDelayed;
import static org.matheclipse.core.expression.F.SignCmp;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.fraction;

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
 * Cotangent function
 * 
 * See <a
 * href="http://en.wikipedia.org/wiki/Trigonometric_functions">Trigonometric
 * functions</a>
 */
public class Cot extends AbstractTrigArg1 implements INumeric {
	/**
	 * <pre>
	 *      Cot[Pi/4]=1,
	 *      Cot[Pi/6]=3^(1/2),
	 *      Cot[Pi/8]=Sqrt[2]+1,
	 *      Cot[Pi/12]=2+3^(1/2),
	 *      Cot[0]=ComplexInfinity,
	 *      Cot[x_NumberQ*y_]:=(-1)*Cot[(-1)*x*y]/;SignCmp[x]<0,
	 *      Cot[x_NumberQ]:=(-1)*Cot[(-1)*x]/;SignCmp[x]<0
	 * </pre>
	 */
	final static IAST RULES = List(
			Set(Cot(Times(C1D4, Pi)), C1), 
			Set(Cot(Times(fraction(1L,6L),Pi)),Power(C3,C1D2)),
			Set(Cot(Times(fraction(1L, 8L), Pi)), Plus(Power(C2, C1D2), C1)),
			Set(Cot(Times(fraction(1L,12L),Pi)),Plus(Power(C3,C1D2),C2)),
			Set(Cot(C0), ComplexInfinity)
//			SetDelayed(Cot($p("x",$s("NumberQ"))),Condition(Times(CN1,Cot(Times(CN1,$s("x")))),Less(SignCmp($s("x")),C0))),
//			SetDelayed(Cot(Times($p("x",$s("NumberQ")),$p("y"))),Condition(Times(CN1,Cot(Times(Times(CN1,$s("x")),$s("y")))),Less(SignCmp($s("x")),C0)))
			);

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
		return null;
	}
	
	@Override
	public IExpr numericEvalD1(final Num arg1) {
		return F.num(1.0D / Math.tan(arg1.getRealPart()));
	}

	@Override
	public IExpr numericEvalDC1(final ComplexNum arg1) {
		return ComplexUtils.tan(arg1).inverse();
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

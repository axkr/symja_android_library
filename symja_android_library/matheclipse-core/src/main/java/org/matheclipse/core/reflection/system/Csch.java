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
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Hyperbolic Cosecant function
 * 
 * See <a
 * href="http://en.wikipedia.org/wiki/Hyperbolic_function">Hyperbolic
 * functions</a>
 */
public class Csch extends AbstractTrigArg1 implements INumeric {

	/*
 {
Csch[x_NumberQ*y_]:=(-1)*Csch[(-1)*x*y]/;SignCmp[x]<0,
Csch[x_NumberQ]:=(-1)*Csch[(-1)*x]/;SignCmp[x]<0
}
	 */
//	final static IAST RULES = List(
//			SetDelayed(Csch(Times($p(x,$s("NumberQ")),$p(y))),Condition(Times(CN1,Csch(Times(Times(CN1,x),y))),Less(SignCmp(x),C0))),
//			SetDelayed(Csch($p(x,$s("NumberQ"))),Condition(Times(CN1,Csch(Times(CN1,x))),Less(SignCmp(x),C0)))
//			);

	 
	
	public Csch() {
	}

	@Override
	public IExpr evaluateArg1(IExpr arg1) {
		if (AbstractFunctionEvaluator.isNegativeExpression(arg1)) {
			return Times(CN1, Csch(Times(CN1, arg1)));
		}
		IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
		if (imPart != null) {
			return F.Times(F.CNI, F.Csc(imPart));
		}
		if (arg1.isZero()){
			return F.CComplexInfinity;
		}
		return null;
	}
	
	@Override
	public IExpr numericEvalD1(final Num arg1) {
		return F.num(1.0D / Math.sinh(arg1.getRealPart()));
	}

	@Override
	public IExpr numericEvalDC1(final ComplexNum arg1) {
		return ComplexUtils.sinh(arg1).inverse();
	}

	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		return 1.0D / Math.sinh(stack[top]);
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}

package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C1D4;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.ComplexInfinity;
import static org.matheclipse.core.expression.F.Cot;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Pi;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.fraction;

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
 * href="http://en.wikipedia.org/wiki/Hyperbolic_function">Hyperbolic
 * functions</a>
 */
public class Sech extends AbstractTrigArg1 implements INumeric {
	/*
 {
Sech[x_NumberQ*y_]:=Sech[(-1)*x*y]/;SignCmp[x]<0,
Sech[x_NumberQ]:=Sech[(-1)*x]/;SignCmp[x]<0
}
	 */
	final static IAST RULES = List(
			SetDelayed(Sech(Times($p(x,$s("NumberQ")),$p(y))),Condition(Sech(Times(Times(CN1,x),y)),Less(SignCmp(x),C0))),
			SetDelayed(Sech($p(x,$s("NumberQ"))),Condition(Sech(Times(CN1,x)),Less(SignCmp(x),C0)))
			);

	@Override
	public IAST getRuleAST() {
		return RULES;
	}
	
	public Sech() {
	}

	@Override
	public IExpr numericEvalD1(final Num arg1) {
		return F.num(1.0D / Math.cosh(arg1.getRealPart()));
	}

	@Override
	public IExpr numericEvalDC1(final ComplexNum arg1) {
		return ComplexUtils.cosh(arg1).inverse();
	}

	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		return 1.0D / Math.cosh(stack[top]);
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}

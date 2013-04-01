package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.*; 

import org.matheclipse.core.eval.interfaces.AbstractArg12;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.ComplexUtils;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

public class Log extends AbstractArg12 implements INumeric {
	
	/**
	 * <pre>
	 Log[1]=0,
  Log[E]=1,
  Log[E^(x_Integer)]:=x,
  Log[E^(x_Rational)]:=x,
  Log[E^(I)]=I,
  Log[Exp[-I]]=(-I),
  Log[0]=(-Infinity)
	 </pre>
	 */
	final static IAST RULES = List(
			Set(Log(Power(E,Times(CN1,CI))),Times(CN1,CI)),
			Set(Log(Power(E,CI)),CI),
			Set(Log(C0),Times(CN1,CInfinity)),
			Set(Log(C1),C0),
			Set(Log(E),C1),
			SetDelayed(Log(Power(E,$p("x",$s("Integer")))),$s("x")),
			SetDelayed(Log(Power(E,$p("x",$s("Rational")))),$s("x"))
			);

	@Override
	public IAST getRuleAST() {
		return RULES;
	}
	
	public Log() {
	}

	@Override
	public IExpr e1DblArg(final INum arg1) {
		return Num.valueOf(Math.log(arg1.getRealPart()));
	}

	@Override
	public IExpr e1DblComArg(final IComplexNum arg1) {
		return ComplexUtils.log((ComplexNum) arg1);
	}

	@Override
	public IExpr e2DblArg(final INum arg1, final INum arg2) {
		return null;
	}

	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		return Math.log(stack[top]);
	}

	// @Override
	// public String[] getRules() {
	// return RULES;
	// }

	@Override
	public void setUp(ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}

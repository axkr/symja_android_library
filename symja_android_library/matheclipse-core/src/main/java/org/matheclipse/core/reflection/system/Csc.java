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
 * Cosecant function
 * 
 * See <a
 * href="http://en.wikipedia.org/wiki/Trigonometric_functions">Trigonometric
 * functions</a>
 */
public class Csc extends AbstractTrigArg1 implements INumeric {
	/**
	 * <pre>
	 *      Csc[x_NumberQ*y_]:=(-1)*Csc[(-1)*x*y]/;SignCmp[x]<0,
	 *      Csc[x_NumberQ]:=(-1)*Csc[(-1)*x]/;SignCmp[x]<0
	 * </pre>
	 */
	final static IAST RULES = List(
			SetDelayed(Csc($p("x",$s("NumberQ"))),Condition(Times(CN1,Csc(Times(CN1,$s("x")))),Less(SignCmp($s("x")),C0))),
			SetDelayed(Csc(Times($p("x",$s("NumberQ")),$p("y"))),Condition(Times(CN1,Csc(Times(Times(CN1,$s("x")),$s("y")))),Less(SignCmp($s("x")),C0)))
	);

	@Override
	public IAST getRuleAST() {
		return RULES;
	}
	
	public Csc() {
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

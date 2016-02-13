package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Get the denominator part of an expression
 * 
 * See <a href="http://en.wikipedia.org/wiki/Fraction_(mathematics)">Wikipedia: Fraction (mathematics)</a>
 * 
 * @see org.matheclipse.core.reflection.system.Numerator
 */
public class Denominator extends AbstractEvaluator {

	static ISymbol[] NUMERATOR_SYMBOLS = { F.Csc, F.Cot, F.Sec };
	static ISymbol[] DENOMINATOR_SYMBOLS = { F.Sin, F.Tan, F.Cos };

	public Denominator() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		IExpr expr = ast.arg1();
		if (expr.isRational()) {
			return ((IRational) expr).getDenominator();
		}
		IExpr[] parts = Apart.getFractionalParts(expr);
		if (parts == null) {
			return F.C1;
		}
		return parts[1];
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE);
	}

	/**
	 * Get the &quot;denominator form&quot; of the given function. Example: <code>Csc[x]</code> gives <code>Sin[x]</code>.
	 * 
	 * @param function
	 *            the function which should be transformed to &quot;denominator form&quot;
	 * @return
	 */
	public static IAST getDenominatorForm(IAST function) {
		if (function.size() == 2) {
			for (int i = 0; i < Denominator.NUMERATOR_SYMBOLS.length; i++) {
				ISymbol sym = Denominator.NUMERATOR_SYMBOLS[i];
				if (function.head().equals(sym)) {
					return F.unaryAST1(Denominator.DENOMINATOR_SYMBOLS[i], function.arg1());
				}
			}
		}
		return F.NIL;
	}

}

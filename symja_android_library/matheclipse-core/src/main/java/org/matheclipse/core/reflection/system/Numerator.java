package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Get the numerator part of an expression
 * 
 * See <a href="http://en.wikipedia.org/wiki/Fraction_(mathematics)">Wikipedia:
 * Fraction (mathematics)</a>
 * 
 * @see org.matheclipse.core.reflection.system.Denominator
 */
public class Numerator extends AbstractEvaluator {

	static ISymbol[] NUMERATOR_SYMBOLS = null;// { F.Sin, F.Cos, F.Tan, F.Csc,
												// F.Sec, F.Cot };
	static IExpr[] TRIG_TRUE_EXPRS = null; // { F.Sin, F.Cos, F.Sin, F.C1, F.C1,
											// F.Cos };

	public Numerator() {
		// default ctor
		NUMERATOR_SYMBOLS = new ISymbol[6];
		NUMERATOR_SYMBOLS[0] = F.Sin;
		NUMERATOR_SYMBOLS[1] = F.Cos;
		NUMERATOR_SYMBOLS[2] = F.Tan;
		NUMERATOR_SYMBOLS[3] = F.Csc;
		NUMERATOR_SYMBOLS[4] = F.Sec;
		NUMERATOR_SYMBOLS[5] = F.Cot;
		TRIG_TRUE_EXPRS = new IExpr[6];
		TRIG_TRUE_EXPRS[0] = F.Sin;
		TRIG_TRUE_EXPRS[1] = F.Cos;
		TRIG_TRUE_EXPRS[2] = F.Sin;
		TRIG_TRUE_EXPRS[3] = F.C1;
		TRIG_TRUE_EXPRS[4] = F.C1;
		TRIG_TRUE_EXPRS[5] = F.Cos;
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);

		boolean trig = false;
		if (ast.isAST2()) {
			final Options options = new Options(ast.topHead(), ast, 2, engine);
			IExpr option = options.getOption("Trig");

			if (option.isTrue()) {
				trig = true;
			} else if (!option.isPresent()) {
				throw new WrongArgumentType(ast, ast.get(2), 2, "Option expected!");
			}
		}

		IExpr arg = ast.arg1();
		if (arg.isRational()) {
			return ((IRational) arg).getNumerator();
		}
		IExpr[] parts = Apart.getFractionalParts(arg, trig);
		if (parts == null) {
			return arg;
		}
		return parts[0];
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE);
	}

	/**
	 * Get the &quot;numerator form&quot; of the given function. Example:
	 * <code>Csc[x]</code> gives <code>Sin[x]</code>.
	 * 
	 * @param function
	 *            the function which should be transformed to &quot;denominator
	 *            form&quot; determine the denominator by splitting up functions
	 *            like <code>Tan[9,Cot[], Csc[],...</code>
	 * @param trig
	 * @return
	 */
	public static IExpr getTrigForm(IAST function, boolean trig) {
		if (trig) {
			if (function.isAST1()) {
				for (int i = 0; i < Denominator.NUMERATOR_SYMBOLS.length; i++) {
					ISymbol sym = Denominator.NUMERATOR_SYMBOLS[i];
					if (function.head().equals(sym)) {
						IExpr result = TRIG_TRUE_EXPRS[i];
						if (result.isSymbol()) {
							return F.unaryAST1(result, function.arg1());
						}
						return result;
					}
				}
			}
		}
		return F.NIL;
	}
}

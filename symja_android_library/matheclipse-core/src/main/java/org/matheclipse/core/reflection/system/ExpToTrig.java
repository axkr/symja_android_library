package org.matheclipse.core.reflection.system;

import org.matheclipse.core.builtin.Structure;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * ExpToTrig(expr)
 * </pre>
 *
 */
public class ExpToTrig extends AbstractEvaluator {

	public ExpToTrig() {
	}

	/**
	 * Exponential definitions for trigonometric functions
	 * 
	 * See <a href= "http://en.wikipedia.org/wiki/List_of_trigonometric_identities#Exponential_definitions"> List of
	 * trigonometric identities - Exponential definitions</a>,<br/>
	 * <a href="http://en.wikipedia.org/wiki/Hyperbolic_function">Hyperbolic function</a>
	 */
	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {

		if (ast.size() == 2) {
			IExpr temp = Structure.threadLogicEquationOperators(ast.arg1(), ast, 1);
			if (temp.isPresent()) {
				return temp;
			}

			IExpr arg1 = ast.arg1();
			temp = arg1.replaceAll(x -> {
				if (x.isPower() && x.base().equals(F.E)) {
					IExpr exponent = x.exponent();
					return F.Plus(F.Cosh(exponent), F.Sinh(exponent));
				}
				return F.NIL;
			});
			if (temp.isPresent()) {
				return F.evalExpandAll(temp, engine);
			}
			return arg1;
		}
		Validate.checkSize(ast, 2);
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE);
	}

}

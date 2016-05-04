package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCorePredicateEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Predicate function
 * 
 * Returns <code>True</code> if the 1st argument is <code>0</code>;
 * <code>False</code> otherwise
 */
public class PossibleZeroQ extends AbstractCorePredicateEvaluator {

	public PossibleZeroQ() {
	}

	@Override
	public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
		return possibleZeroQ(arg1, engine);
	}
	
	public static boolean possibleZeroQ(IExpr expr, EvalEngine engine) {
		if (expr.isNumber()) {
			return expr.isZero();
		}
		if (expr.isAST()) {
			expr = F.expandAll(expr, true, true);
			if (expr.isZero()) {
				return true;
			}
			if (expr.isPlus() || expr.isPower() || expr.isTimes()) {
				expr = engine.evaluate(expr);
				if (expr.isZero()) {
					return true;
				}
				if (expr.isPlus() || expr.isPower() || expr.isTimes()) {
					expr = engine.evaluate(F.Together(expr));
					if (expr.isZero()) {
						return true;
					}
				}
			}
		}
		if (expr.isNumericFunction()) {
			IExpr temp = engine.evalN(expr);
			return temp.isZero();
		}

		return expr.isZero();
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE);
	}

}

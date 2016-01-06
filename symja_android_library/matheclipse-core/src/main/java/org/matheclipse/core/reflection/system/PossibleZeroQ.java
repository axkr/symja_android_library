package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Predicate function
 * 
 * Returns <code>True</code> if the 1st argument is <code>0</code>;
 * <code>False</code> otherwise
 */
public class PossibleZeroQ extends AbstractFunctionEvaluator {

	public PossibleZeroQ() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		return F.bool(possibleZeroQ(ast.arg1(), engine));
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
			expr = engine.evaluate(F.Simplify(expr));
			if (expr.isZero()) {
				return true;
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

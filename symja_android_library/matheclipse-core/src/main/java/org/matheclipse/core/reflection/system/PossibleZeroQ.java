package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.WrongNumberOfArguments;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Predicate function
 * 
 * Returns <code>True</code> if the 1st argument is <code>0</code>
 * <code>False</code> otherwise
 */
public class PossibleZeroQ extends AbstractFunctionEvaluator {

	public PossibleZeroQ() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		if (ast.size() != 2) {
			throw new WrongNumberOfArguments(ast, 1, ast.size() - 1);
		}
		return F.bool(possibleZeroQ(ast.get(1)));
	}

	public static boolean possibleZeroQ(IExpr expr) {
		// TODO implement more tests if expr could be 0
		if (expr.isAST()) {
			expr = F.evalExpandAll(expr);
		}
		return expr.isZero();
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE);
	}

}

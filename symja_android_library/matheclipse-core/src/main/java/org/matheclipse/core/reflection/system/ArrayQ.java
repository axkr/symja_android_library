package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMatcher;

import com.google.common.base.Predicate;

/**
 * ArrayQ tests whether an expression is a full array.
 */
public class ArrayQ implements IFunctionEvaluator {

	public ArrayQ() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 2, 4);

		Predicate<IExpr> pred = null;
		if ((ast.size() >= 4)) {
			pred = Predicates.isTrue(ast.arg3());
		}
		int depth = determineDepth(ast.arg1(), 0, pred);
		if (depth >= 0) {
			if ((ast.size() >= 3)) {
				// Match the depth with the second argumnt
				final PatternMatcher matcher = new PatternMatcher(ast.arg2());
				if (!matcher.apply(F.integer(depth))) {
					return F.False;
				}
			}
			return F.True;
		}
		return F.False;

	}

	/**
	 * Determine the depth of the given expression <code>expr</code> which should be a full array of (possibly nested) lists. Return
	 * <code>-1</code> if the expression isn't a full array.
	 * 
	 * @param expr
	 * @param depth
	 *            start depth of the full array
	 * @param predicate
	 *            an optional <code>Predicate</code> which would be applied to all elements which aren't lists.
	 * @return <code>-1</code> if the expression isn't a full array.
	 */
	private static int determineDepth(final IExpr expr, int depth, Predicate<IExpr> predicate) {
		int resultDepth = depth;
		if (expr.isList()) {
			IAST ast = (IAST) expr;
			int size = ast.size();
			IExpr arg1AST = ast.arg1();
			boolean isList = arg1AST.isList();
			int arg1Size = 0;
			if (isList) {
				arg1Size = ((IAST) ast.arg1()).size();
			}
			resultDepth = determineDepth(arg1AST, depth + 1, predicate);
			if (resultDepth < 0) {
				return -1;
			}
			int tempDepth;
			for (int i = 2; i < size; i++) {
				if (isList) {
					if (!ast.get(i).isList()) {
						return -1;
					}
					if (arg1Size != ((IAST) ast.get(i)).size()) {
						return -1;
					}
					tempDepth = determineDepth(ast.get(i), depth + 1, predicate);
					if (tempDepth < 0 || tempDepth != resultDepth) {
						return -1;
					}
				} else {
					if (ast.get(i).isList()) {
						return -1;
					}
					if (predicate != null) {
						if (!predicate.apply(ast.get(i))) {
							return -1;
						}
					}
				}
			}
			return resultDepth;
		}
		if (predicate != null) {
			if (!predicate.apply(expr)) {
				return -1;
			}
		}
		return resultDepth;
	}

	public IExpr numericEval(final IAST ast) {
		return evaluate(ast);
	}

	public void setUp(final ISymbol symbol) {
	}

}

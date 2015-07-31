package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class ExpandAll extends AbstractFunctionEvaluator {
	public ExpandAll() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 2, 3);

		IExpr arg1 = ast.arg1();
		IExpr patt = null;
		if (ast.size() > 2) {
			patt = ast.arg2();
		}
		if (arg1.isAST()) {
			return arg1.optional(expandAll((IAST) arg1, patt, true, false));
		}
		return arg1;
	}

	/**
	 * Expand the given <code>ast</code> expression.
	 * 
	 * @param patt
	 * @param expandNegativePowers
	 *            TODO
	 * @param distributePlus
	 *            TODO
	 * @param ast
	 * @return <code>null</code> if the expression couldn't be expanded.
	 */
	public static IExpr expandAll(final IAST expr, IExpr patt, boolean expandNegativePowers, boolean distributePlus) {
		if (patt != null && expr.isFree(patt, true)) {
			return null;
		}
		IAST ast = expr;
		if (ast.isAST()) {
			if ((ast.getEvalFlags() & IAST.IS_SORTED) != IAST.IS_SORTED) {
				ast = EvalEngine.get().evalFlatOrderlessAttributesRecursive(ast);
			}
		}
		if (expr.isAllExpanded()) {
			return expr;
		}
		IAST result = null;
		IExpr temp = null;
		for (int i = 1; i < ast.size(); i++) {
			if (ast.get(i).isAST()) {
				temp = expandAll((IAST) ast.get(i), patt, expandNegativePowers, distributePlus);
				if (temp != null) {
					if (result == null) {
						result = ast.setAtClone(i, temp);
					} else {
						result.set(i, temp);
					}
				}
			}
		}
		if (result == null) {
			return setAllExpanded(Expand.expand(ast, patt, expandNegativePowers, distributePlus), expandNegativePowers,
					distributePlus);
		}
		temp = Expand.expand(result, patt, expandNegativePowers, distributePlus);
		if (temp != null) {
			return setAllExpanded(temp, expandNegativePowers, distributePlus);
		}
		return setAllExpanded(result, expandNegativePowers, distributePlus);
	}

	private static IExpr setAllExpanded(IExpr expr, boolean expandNegativePowers, boolean distributePlus) {
		if (expr != null && expandNegativePowers && !distributePlus && expr.isAST()) {
			((IAST) expr).setEvalFlags(IAST.IS_ALL_EXPANDED);
		}
		return expr;
	}

}
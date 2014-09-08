package org.matheclipse.core.reflection.system;

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
			IExpr temp = expandAll((IAST) arg1, patt);
			if (temp != null) {
				return temp;
			}
		}
		return arg1;
	}

	/**
	 * Expand the given <code>ast</code> expression.
	 * 
	 * @param ast
	 * @param patt
	 * @return <code>null</code> if the expression couldn't be expanded.
	 */
	public static IExpr expandAll(final IAST ast, IExpr patt) {
		if (patt != null && ast.isFree(patt, true)) {
			return null;
		}
		IAST result = null;
		IExpr temp = null;
		for (int i = 1; i < ast.size(); i++) {
			if (ast.get(i).isAST()) {
				temp = expandAll((IAST) ast.get(i), patt);
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
			return Expand.expand(ast, patt);
		}
		temp = Expand.expand(result, patt);
		if (temp != null) {
			return temp;
		}
		return result;
	}

}
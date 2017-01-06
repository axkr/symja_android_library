package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class ExpandAll extends AbstractFunctionEvaluator {
	public final static ExpandAll CONST = new ExpandAll();

	public ExpandAll() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);

		IExpr arg1 = ast.arg1();
		IExpr patt = null;
		if (ast.size() > 2) {
			patt = ast.arg2();
		}
		if (arg1.isAST()) {
			return expandAll((IAST) arg1, patt, true, false).orElse(arg1);
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
	 * @return <code>F.NIL</code> if the expression couldn't be expanded.
	 */
	public static IExpr expandAll(final IAST expr, IExpr patt, boolean expandNegativePowers, boolean distributePlus) {
		if (patt != null && expr.isFree(patt, true)) {
			return F.NIL;
		}
		IAST ast = expr;
		IAST tempAST = F.NIL;
		if (ast.isAST()) {
			if ((ast.getEvalFlags() & IAST.IS_SORTED) != IAST.IS_SORTED) {
				tempAST = EvalEngine.get().evalFlatOrderlessAttributesRecursive(ast);
				if (tempAST.isPresent()) {
					ast = tempAST;
				}
			}
		}
		if (ast.isAllExpanded()) {
			if (ast != expr) {
				return ast;
			}
			return F.NIL;
		}
		IAST result = F.NIL;
		IExpr temp = F.NIL;
		for (int i = 1; i < ast.size(); i++) {
			if (ast.get(i).isAST()) {
				temp = expandAll((IAST) ast.get(i), patt, expandNegativePowers, distributePlus);
				if (temp.isPresent()) {
					if (!result.isPresent()) {
						result = ast.copyUntil(i);
					}
					result.appendPlus(temp);
					// if (temp.isPlus() && result.isPlus()) {
					// result.appendArgs((IAST) temp);
					// } else {
					// result.append(temp);
					// }
					continue;
				}
			}
			if (result.isPresent()) {
				result.append(ast.get(i));
			}
		}
		if (!result.isPresent()) {
			temp = Expand.expand(ast, patt, expandNegativePowers, distributePlus);
			if (temp.isPresent()) {
				setAllExpanded(temp, expandNegativePowers, distributePlus);
				return temp;
			} else {
				if (ast != expr) {
					setAllExpanded(ast, expandNegativePowers, distributePlus);
					return ast;
				}
			}
			setAllExpanded(expr, expandNegativePowers, distributePlus);
			return F.NIL;
		}
		temp = Expand.expand(result, patt, expandNegativePowers, distributePlus);
		if (temp.isPresent()) {
			return setAllExpanded(temp, expandNegativePowers, distributePlus);
		}
		return setAllExpanded(result, expandNegativePowers, distributePlus);
	}

	private static IExpr setAllExpanded(IExpr expr, boolean expandNegativePowers, boolean distributePlus) {
		if (expr != null && expandNegativePowers && !distributePlus && expr.isAST()) {
			((IAST) expr).addEvalFlags(IAST.IS_ALL_EXPANDED);
		}
		return expr;
	}

}
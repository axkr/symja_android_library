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
	 * @param ast
	 * @param patt
	 * @param expandNegativePowers
	 * @param distributePlus
	 * 
	 * @return <code>F.NIL</code> if the expression couldn't be expanded.
	 */
	public static IExpr expandAll(final IAST ast, IExpr patt, boolean expandNegativePowers, boolean distributePlus) {
		if (patt != null && ast.isFree(patt, true)) {
			return F.NIL;
		}
		IAST localAST = ast;
		IAST tempAST = F.NIL;
		if (localAST.isAST()) {
			if ((localAST.getEvalFlags() & IAST.IS_SORTED) != IAST.IS_SORTED) {
				tempAST = EvalEngine.get().evalFlatOrderlessAttributesRecursive(localAST);
				if (tempAST.isPresent()) {
					localAST = tempAST;
				}
			}
		}
		if (localAST.isAllExpanded()) {
			if (localAST != ast) {
				return localAST;
			}
			return F.NIL;
		}
		IAST result = F.NIL;
		IExpr temp = F.NIL;
		for (int i = 1; i < localAST.size(); i++) {
			if (localAST.get(i).isAST()) {
				temp = expandAll((IAST) localAST.get(i), patt, expandNegativePowers, distributePlus);
				if (temp.isPresent()) {
					if (!result.isPresent()) {
						int size = localAST.size();
						if (temp.isAST()) {
							size += ((IAST) temp).size();
						}
						result = F.ast(localAST.head(), size, false);
						result.appendArgs(localAST, i);
					}
					result.appendPlus(temp);
					continue;
				}
			}
			if (result.isPresent()) {
				result.append(localAST.get(i));
			}
		}
		if (!result.isPresent()) {
			temp = Expand.expand(localAST, patt, expandNegativePowers, distributePlus);
			if (temp.isPresent()) {
				setAllExpanded(temp, expandNegativePowers, distributePlus);
				return temp;
			} else {
				if (localAST != ast) {
					setAllExpanded(localAST, expandNegativePowers, distributePlus);
					return localAST;
				}
			}
			setAllExpanded(ast, expandNegativePowers, distributePlus);
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
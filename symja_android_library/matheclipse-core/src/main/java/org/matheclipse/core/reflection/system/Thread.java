package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 */
public class Thread extends AbstractFunctionEvaluator {

	public Thread() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 2, 3);

		if (!(ast.arg1().isAST())) {
			return null;
		}
		// LevelSpec level = null;
		// if (functionList.size() == 4) {
		// level = new LevelSpecification(functionList.arg3());
		// } else {
		// level = new LevelSpec(1);
		// }
		IExpr head = F.List;
		if (ast.size() == 3) {
			head = ast.arg2();
		}
		final IAST list = (IAST) ast.arg1();
		if (list.size() > 1) {
			return threadList(list, head, list.head());
		}
		return null;
	}

	/**
	 * Thread through all lists in the arguments of the IAST [i.e. the list header has the attribute ISymbol.LISTABLE] example:
	 * Sin[{2,x,Pi}] ==> {Sin[2],Sin[x],Sin[Pi]}
	 * 
	 * @param list
	 * @param head
	 *            the head over which
	 * @param listLength
	 *            the length of the list
	 * @return
	 */
	public static IAST threadList(final IAST list, IExpr head, IExpr mapHead) {

		int listLength = 0;

		for (int i = 1; i < list.size(); i++) {
			if ((list.get(i).isAST()) && (((IAST) list.get(i)).head().equals(head))) {
				if (listLength == 0) {
					listLength = ((IAST) list.get(i)).size() - 1;
				} else {
					if (listLength != ((IAST) list.get(i)).size() - 1) {
						listLength = 0;
						return null;
						// for loop
					}
				}
			}
		}

		return EvalAttributes.threadList(list, head, mapHead, listLength);

	}
}

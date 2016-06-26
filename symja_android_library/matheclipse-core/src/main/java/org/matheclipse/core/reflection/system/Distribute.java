package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 *  
 */
public class Distribute extends AbstractFunctionEvaluator {

	public Distribute() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 6);

		IExpr arg1 = ast.arg1();
		IExpr head = F.Plus;
		if (ast.size() >= 3) {
			head = ast.arg2();
		}
		if (ast.isAST3()) {
			if (!arg1.head().equals(ast.arg3())) {
				return arg1;
			}
		}

		if (arg1.isAST()) {
			IAST resultCollector;
			if (ast.size() >= 5) {
				resultCollector = F.ast(ast.arg4());
			} else {
				resultCollector = F.ast(head);
			}
			IAST stepResult;
			if (ast.size() >= 6) {
				stepResult = F.ast(ast.arg5());
			} else {
				stepResult = F.ast(arg1.head());
			}
			distributePosition(resultCollector, stepResult, head, (IAST) arg1, 1);
			return resultCollector;
		}
		return arg1;
	}

	private void distributePosition(IAST resultCollector, IAST stepResult, IExpr head, IAST arg1, int position) {
		if (arg1.size() == position) {
			resultCollector.add(stepResult);
			return;
		}
		if (arg1.size() < position) {
			return;
		}
		if (arg1.get(position).head().equals(head) && arg1.get(position).isAST()) {
			IAST temp = (IAST) arg1.get(position);
			for (int i = 1; i < temp.size(); i++) {
				IAST res2 = stepResult.clone();
				res2.add(temp.get(i));
				distributePosition(resultCollector, res2, head, arg1, position + 1);
			}
		} else {
			IAST res2 = stepResult.clone();
			res2.add(arg1.get(position));
			distributePosition(resultCollector, res2, head, arg1, position + 1);
		}

	}
}
package org.matheclipse.core.reflection.system;

import java.util.function.BiPredicate;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Delete duplicate values from a list.
 */
public class DeleteDuplicates extends AbstractFunctionEvaluator {

	public DeleteDuplicates() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);

		IExpr test = F.Equal;
		if (ast.isAST2()) {
			test = ast.arg2();
		}
		if (ast.arg1().isList()) {
			IAST list = (IAST) ast.arg1();
			final IAST result = F.List();
			IExpr temp;
			boolean evaledTrue;
			BiPredicate<IExpr, IExpr> biPredicate = Predicates.isBinaryTrue(test);
			for (int i = 1; i < list.size(); i++) {
				temp = list.get(i);
				evaledTrue = false;
				for (int j = 1; j < result.size(); j++) {
					if (biPredicate.test(result.get(j), temp)) {
						evaledTrue = true;
						break;
					}
				}
				if (evaledTrue) {
					continue;
				}
				result.append(temp);
			}
			return result;
		}
		return F.NIL;
	}

}

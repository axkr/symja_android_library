package org.matheclipse.core.reflection.system;

import org.apache.commons.math4.util.MathArrays;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Create a random shuffled list.
 * 
 */
public class RandomSample extends AbstractFunctionEvaluator {

	public RandomSample() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		if (ast.arg1().isAST()) {
			return shuffle((IAST) ast.arg1());
		}

		return null;
	}

	public static IAST shuffle(IAST list) {
		final int len = list.size() - 1;

		// Shuffle indices.
		final int[] indexList = MathArrays.natural(len);
		MathArrays.shuffle(indexList);

		// Create shuffled list.
		final IAST out = list.clone();
		for (int i = 0; i < len; i++) {
			out.set(i + 1, list.get(indexList[i] + 1));
		}
		return out;
	}
}

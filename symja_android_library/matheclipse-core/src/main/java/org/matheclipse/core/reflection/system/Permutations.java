package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.combinatoric.KPermutationsList;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Generate a list of permutations
 * 
 * See <a href=" http://en.wikipedia.org/wiki/Permutation">Permutation</a>
 * 
 * @see Partition
 * @see Subsets
 */
public class Permutations extends AbstractFunctionEvaluator {

	public Permutations() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 2, 3);

		if (ast.arg1().isAST()) {
			final IAST f = (IAST) ast.arg1();
			final IAST result = F.ast(f.head());
			if (f.size() <= 2) {
				if (f.size() == 2) {
					result.add(f);
				}
				return result;
			}

			int k = f.size() - 1;
			if (ast.size() == 3) {
				if (!ast.arg2().isInteger()) {
					return null;
				}
				k = Validate.checkIntType(ast, 2);
				if (k > f.size() - 1) {
					return null;
				}
			}
			final KPermutationsList perm = new KPermutationsList(f, k, F.ast(f.head()));
			for (IAST temp : perm) {
				result.add(temp);
			}
			return result;

		}
		return null;
	}

}

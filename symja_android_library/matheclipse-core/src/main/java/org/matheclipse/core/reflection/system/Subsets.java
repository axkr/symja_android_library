package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.LevelSpecification;
import org.matheclipse.core.expression.AST;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.combinatoric.KSubsetsList;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Generate a list of all k-combinations from a given list
 * 
 * See <a href="http://en.wikipedia.org/wiki/Combination">Combination</a>
 */
public class Subsets extends AbstractFunctionEvaluator {

	public Subsets() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 2, 3);
		if (ast.get(1).isAST()) {
			final IAST f = (IAST) ast.get(1);
			final int n = f.size() - 1;
			final LevelSpecification level;
			if (ast.size() == 3) {
				level = new LevelSpecification(ast.get(2), false);
			} else {
				level = new LevelSpecification(0, n);
			}

			int k;
			final IAST result = F.ast(f.head());
			level.setFromLevelAsCurrent();
			while (level.isInRange()) {
				k = level.getCurrentLevel();
				final KSubsetsList iter = KSubsetsList.createKSubsets(f, k, F.ast(F.List), 1);
				int i = 0;
				for (IAST part : iter) {
					if (part == null) {
						break;
					}
					result.add(part);
					i++;
				}
				level.incCurrentLevel();
			}

			return result;
		}
		return null;
	}

}

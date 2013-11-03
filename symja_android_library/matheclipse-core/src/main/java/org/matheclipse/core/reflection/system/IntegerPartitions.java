package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.combinatoric.NumberPartitionsIterable;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

/**
 * Generate all integer partitions for a given integer number. See <a
 * href="http://en.wikipedia.org/wiki/Integer_partition">Wikipedia - Integer
 * partition</a>
 * 
 */
public class IntegerPartitions extends AbstractFunctionEvaluator {

	public IntegerPartitions() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		if (ast.arg1().isInteger()) {
			final int n = ((IInteger) ast.arg1()).getBigNumerator().intValue();
			final IAST result = F.List();
			IAST temp;
			final NumberPartitionsIterable comb = new NumberPartitionsIterable(n);
			for (int j[] : comb) {
				temp = F.List();
				for (int i = 0; i < j.length; i++) {
					if (j[i] != 0) {
						temp.add(F.integer(j[i]));
					}
				}
				result.add(temp);
			}
			return result;
		}
		return null;
	}

}

package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.combinatoric.KPartitionsList;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class KPartitions extends AbstractFunctionEvaluator {

	public KPartitions() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);
		if (ast.get(1).isAST() && ast.get(2).isInteger()) {
			final IAST listArg0 = (IAST) ast.get(1);
			final int k = Validate.checkIntType(ast, 2);
			final IAST result = F.List();
			final KPartitionsList iter = new KPartitionsList(listArg0, k, F.ast(F.List), 1);
			for (IAST part : iter) {
				result.add(part);
			}
			return result;
		}
		return null;
	}
}

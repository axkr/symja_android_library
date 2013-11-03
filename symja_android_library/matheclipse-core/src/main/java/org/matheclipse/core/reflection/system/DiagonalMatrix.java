package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Create a diagonal matrix from a list
 */
public class DiagonalMatrix extends AbstractFunctionEvaluator {

	public DiagonalMatrix() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 2, 3);

		if (ast.arg1().isAST()) {
			IAST list = (IAST) ast.arg1();
			int m = list.size();
			IAST res = F.List();
			int offset = 0;
			if ((ast.size() == 3)) {
				offset = Validate.checkIntType(ast, 2, Integer.MIN_VALUE);
			}
			for (int i = 1; i < m; i++) {
				IAST row = F.List();
				for (int j = 1; j < m; j++) {
					if (i + offset == j) {
						row.add(list.get(i));
					} else {
						row.add(F.C0);
					}
				}

				res.add(row);
			}

			return res;

		}

		return null;

	}

	@Override
	public void setUp(final ISymbol symbol) {
	}

}

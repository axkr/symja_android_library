package org.matheclipse.core.reflection.system;

import java.util.ArrayList;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 *  
 */
public class ArrayDepth extends AbstractFunctionEvaluator {

	public ArrayDepth() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 2, 3);

		int n = Integer.MAX_VALUE;
		if (ast.size() == 3 && ast.arg2().isInteger()) {
			n = Validate.checkIntType(ast, 2);
		}
		if (ast.arg1().isAST()) {
			IAST res = F.List();
			if (n > 0) {
				IAST list = (IAST) ast.arg1();
				IExpr header = list.head();
				ArrayList<Integer> dims = Dimensions.getDimensions(list, header, n - 1);
				return F.integer(dims.size());
			}
			return res;
		}

		return F.List();

	}

	@Override
	public void setUp(final ISymbol symbol) {
	}

}

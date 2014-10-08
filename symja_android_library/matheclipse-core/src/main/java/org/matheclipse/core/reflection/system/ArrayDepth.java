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
		Validate.checkSize(ast, 2);

		if (ast.arg1().isAST()) {
			IAST list = (IAST) ast.arg1();
			IExpr header = list.head();
			ArrayList<Integer> dims = Dimensions.getDimensions(list, header, Integer.MAX_VALUE);
			return F.integer(dims.size());
		}

		return F.C0;

	}

	@Override
	public void setUp(final ISymbol symbol) {
	}

}

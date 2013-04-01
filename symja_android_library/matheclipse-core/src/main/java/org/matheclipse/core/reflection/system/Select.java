package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Select implements IFunctionEvaluator {

	public Select() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3, 4);
		
		if (ast.get(1).isAST()) {
			if (ast.size() == 3) {
				return select((IAST) ast.get(1), ast.get(2));
			} else if ((ast.size() == 4) && ast.get(3).isInteger()) {
				final int resultLimit = Validate.checkIntType(ast, 3);
				return select((IAST) ast.get(1), ast.get(2), resultLimit);
			}
		}
		return null;
	}

	public static IAST select(final IAST ast, final IExpr head) {
		return ast.filter(ast.copyHead(), Predicates.isTrue(head));
	}

	public static IAST select(final IAST ast, final IExpr head, final int resultLimit) {
		return ast.args().filter(ast.copyHead(), Predicates.isTrue(head), resultLimit);
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(final ISymbol symbol) {
	}

}

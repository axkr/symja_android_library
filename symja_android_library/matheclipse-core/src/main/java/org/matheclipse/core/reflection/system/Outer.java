package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.AST;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.generic.nested.Generating;

public class Outer extends AbstractFunctionEvaluator {

	public Outer() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 4);
		if (ast.get(1).isSymbol() && ast.get(2).isAST() && ast.get(3).isAST()) {
			Generating<IExpr, IAST> gen = new Generating<IExpr, IAST>(F.List(), F.ast(ast.get(1)), 1, AST.COPY);
			return gen.outer((IAST) ast.get(2), (IAST) ast.get(3));
		}
		return null;
	}

}

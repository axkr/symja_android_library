package org.matheclipse.core.reflection.system;

import org.matheclipse.core.convert.ExprVariables;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.HornerScheme;

/**
 * Generate the horner scheme for univariate polynomials. See: <a
 * href="http://en.wikipedia.org/wiki/Horner_scheme">Wikipedia:Horner scheme</a>
 */
public class Horner extends AbstractFunctionEvaluator {

	public Horner() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		if (ast.get(1).isAST()) {

			IAST poly = (IAST) ast.get(1);
			ExprVariables eVar = new ExprVariables(ast.get(1));
			IAST variables = eVar.getVarList();
			if (variables.size() >= 2) {
				ISymbol sym = (ISymbol) variables.get(1);
				if (poly.isASTSizeGE(F.Plus, 2)) {
					HornerScheme scheme = new HornerScheme();
					return scheme.generate(EvalEngine.get().isNumericMode(), poly, sym);
				}
			}

		}
		return ast.get(1);
	}

	@Override
	public void setUp(final ISymbol symbol) {
	}

}

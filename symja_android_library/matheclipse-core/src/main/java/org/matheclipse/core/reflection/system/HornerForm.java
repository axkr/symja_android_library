package org.matheclipse.core.reflection.system;

import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.HornerScheme;

/**
 * Generate the horner scheme for univariate polynomials. See:
 * <a href="http://en.wikipedia.org/wiki/Horner_scheme">Wikipedia:Horner
 * scheme</a> or <a href=
 * "https://rosettacode.org/wiki/Horner's_rule_for_polynomial_evaluation">
 * Rosetta Code - Horner's rule for polynomial evaluation</a>.
 */
public class HornerForm extends AbstractFunctionEvaluator {

	public HornerForm() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);

		IExpr arg1 = ast.arg1();
		if (arg1.isAST()) {

			IAST poly = (IAST) arg1;
			VariablesSet eVar;
			IAST variables;
			if (ast.isAST2()) {
				variables = Validate.checkSymbolOrSymbolList(ast, 2);
			} else {
				eVar = new VariablesSet(ast.arg1());
				variables = eVar.getVarList();
			}

			if (variables.size() >= 2) {
				ISymbol sym = (ISymbol) variables.arg1();
				if (poly.isASTSizeGE(F.Plus, 2)) {
					HornerScheme scheme = new HornerScheme();
					return scheme.generate(engine.isNumericMode(), poly, sym);
				}
			}

		}
		return arg1;
	}

	@Override
	public void setUp(final ISymbol symbol) {
	}

}

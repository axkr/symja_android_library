package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.boole.QuineMcCluskyFormula;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.BooleanFunctionConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Minimize a boolean function with the <a
 * href="http://en.wikipedia.org/wiki/Quine%E2%80%93McCluskey_algorithm">Quine
 * McCluskey algorithm</a>.
 */
public class BooleanMinimize extends AbstractFunctionEvaluator {

	public BooleanMinimize() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		if (ast.arg1().isASTSizeGE(F.Or, 3)) {
			try {
				// System.out.println(ast.arg1());
				QuineMcCluskyFormula f = QuineMcCluskyFormula.read((IAST) ast.arg1());
				// System.out.println(f);
				f.reduceToPrimeImplicants();
				// System.out.println(f);
				f.reducePrimeImplicantsToSubset();
				// System.out.println(f);
				return f.toExpr();
			} catch (BooleanFunctionConversionException bfc) {
				if (Config.DEBUG) {
					bfc.printStackTrace();
				}
				return F.NIL;
			}
		}

		return F.NIL;
	}
}
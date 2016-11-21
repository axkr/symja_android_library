package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Power series expansion
 */
public class SeriesData extends AbstractFunctionEvaluator {
	public SeriesData() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (!ToggleFeature.SERIES) {
			return F.NIL;
		}
		if (ast.size() == 7) {
			if (ast.arg1().isNumber()) {
				return F.Indeterminate;
			}
		}
		return F.NIL;
	}
}

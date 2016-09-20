package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Convert from polar coordinates to cartesian coordinates
 */
public class FromPolarCoordinates extends AbstractEvaluator {

	public FromPolarCoordinates() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);
		int dim = ast.arg1().isVector();
		if (dim > 0) {
			IAST list = (IAST) ast.arg1();
			if (dim == 2) {
				IExpr r = list.arg1();
				IExpr theta = list.arg2();
				return F.List(F.Times(r, F.Cos(theta)), F.Times(r, F.Sin(theta)));
			} else if (dim == 3) {
				IExpr r = list.arg1();
				IExpr theta = list.arg2();
				IExpr phi = list.arg3();
				return F.List(F.Times(r, F.Cos(theta)), F.Times(r, F.Cos(phi), F.Sin(theta)),
						F.Times(r, F.Sin(theta), F.Sin(phi)));
			}
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
	}

}

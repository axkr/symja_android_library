package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Convert from cartesian coordinates to polar coordinates
 */
public class ToPolarCoordinates extends AbstractEvaluator {

	public ToPolarCoordinates() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);
		int dim = ast.arg1().isVector();
		if (dim > 0) {
			IAST list = (IAST) ast.arg1();
			if (dim == 2) {
				IExpr x = list.arg1();
				IExpr y = list.arg2();
				return F.List(F.Sqrt(F.Plus(F.Sqr(x), F.Sqr(y))), F.ArcTan(x, y));
			} else if (dim == 3) {
				IExpr x = list.arg1();
				IExpr y = list.arg2();
				IExpr z = list.arg3();
				IAST sqrtExpr = F.Sqrt(F.Plus(F.Sqr(x), F.Sqr(y), F.Sqr(z)));
				return F.List(sqrtExpr, F.ArcCos(F.Divide(x, sqrtExpr)), F.ArcTan(y, z));
			}
		} else if (ast.arg1().isList()) {
			IAST list = (IAST) ast.arg1();
			return list.mapThread(F.ListAlloc(list.size()), ast, 1);
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
	}

}

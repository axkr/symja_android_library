package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Times;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Calculate the cross product of 2 vectors with dimension 3.
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Cross_product">Wikipedia:Cross product</a>
 */
public class Cross extends AbstractFunctionEvaluator {

	public Cross() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 2, 3);
		IExpr arg1 = ast.arg1();
		if (ast.size() == 3) {
			IExpr arg2 = ast.arg2();
			int dim1 = arg1.isVector();
			int dim2 = arg2.isVector();
			if (dim1 == 3 && dim2 == 3) {
				final IAST v1 = (IAST) arg1;
				final IAST v2 = (IAST) arg2;
				if ((v1.size() == 4) || (v2.size() == 4)) {
					return List(Plus(Times(v1.arg2(), v2.arg3()), Times(CN1, v1.arg3(), v2.arg2())),
							Plus(Times(v1.arg3(), v2.arg1()), Times(CN1, v1.arg1(), v2.arg3())),
							Plus(Times(v1.arg1(), v2.arg2()), Times(CN1, v1.arg2(), v2.arg1())));
				}
			}
		} else if (ast.size() == 2) {
			int dim1 = arg1.isVector();
			if (dim1 == 2) {
				final IAST v1 = (IAST) arg1;
				return List(Negate(v1.arg2()), v1.arg1());
			}
		}
		return null;
	}

}
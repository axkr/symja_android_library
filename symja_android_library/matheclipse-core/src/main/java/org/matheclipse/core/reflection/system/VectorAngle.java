package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.ArcCos;
import static org.matheclipse.core.expression.F.Divide;
import static org.matheclipse.core.expression.F.Dot;
import static org.matheclipse.core.expression.F.Norm;
import static org.matheclipse.core.expression.F.Times;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <p>
 * VectorAngle(arg1, arg2) calculates the angle between vectors arg1 and arg2.
 * </p>
 * 
 * See: <a href="https://en.wikipedia.org/wiki/Angle#Dot_product_and_generalisation">Wikipedia - Angle - Dot product and
 * generalisation</a>
 */
public class VectorAngle extends AbstractFunctionEvaluator {

	public VectorAngle() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);
		IExpr arg1 = ast.arg1();
		IExpr arg2 = ast.arg2();

		int dim1 = arg1.isVector();
		int dim2 = arg2.isVector();
		if (dim1 > (-1) && dim2 > (-1)) {
			return ArcCos(Divide(Dot(arg1, arg2), Times(Norm(arg1), Norm(arg2))));
		}
		return null;
	}

}

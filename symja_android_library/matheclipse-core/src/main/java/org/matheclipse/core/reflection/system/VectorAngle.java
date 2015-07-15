package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;

import static org.matheclipse.core.expression.F.*;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <p>
 * VectorAngle(arg1, arg2) calculates the angle between vectors arg1 and arg2.
 * </p>
 * 
 * See: <a href="https://en.wikipedia.org/wiki/Angle#Dot_product_and_generalisation">Wikipedia - Angle - Dot product and
 * generalisation</a>
 */
public class VectorAngle implements IFunctionEvaluator {

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

	@Override
	public IExpr numericEval(final IAST ast) {
		return evaluate(ast);
	}

	@Override
	public void setUp(ISymbol symbol) {

	}

}

package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * ToPolarCoordinates({x, y})
 * </pre>
 * 
 * <blockquote>
 * <p>
 * return the polar coordinates for the cartesian coordinates <code>{x, y}</code>.
 * </p>
 * </blockquote>
 * 
 * <pre>
 * ToPolarCoordinates({x, y, z})
 * </pre>
 * 
 * <blockquote>
 * <p>
 * return the polar coordinates for the cartesian coordinates <code>{x, y, z}</code>.
 * </p>
 * </blockquote>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt; ToPolarCoordinates({x, y})
 * {Sqrt(x^2+y^2),ArcTan(x,y)}
 * 
 * &gt;&gt; ToPolarCoordinates({x, y, z})
 * {Sqrt(x^2+y^2+z^2),ArcCos(x/Sqrt(x^2+y^2+z^2)),ArcTan(y,z)}
 * </pre>
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

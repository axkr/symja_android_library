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
 * FromPolarCoordinates({r, t})
 * </pre>
 * 
 * <blockquote>
 * <p>
 * return the cartesian coordinates for the polar coordinates <code>{r, t}</code>.
 * </p>
 * </blockquote>
 * 
 * <pre>
 * FromPolarCoordinates({r, t, p})
 * </pre>
 * 
 * <blockquote>
 * <p>
 * return the cartesian coordinates for the polar coordinates <code>{r, t, p}</code>.
 * </p>
 * </blockquote>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt; FromPolarCoordinates({r, t})
 * {r*Cos(t),r*Sin(t)}
 * 
 * &gt;&gt; FromPolarCoordinates({r, t, p})
 * {r*Cos(t),r*Cos(p)*Sin(t),r*Sin(p)*Sin(t)}
 * </pre>
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
		} else if (ast.arg1().isList()) {
			return ((IAST) ast.arg1()).mapThread(F.ListAlloc(ast.size()), ast, 1);
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
	}

}

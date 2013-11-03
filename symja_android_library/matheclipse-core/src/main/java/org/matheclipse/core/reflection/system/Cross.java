package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Times;

import org.matheclipse.core.eval.interfaces.AbstractArg2;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Calculate the cross product of 2 vectors with dimension 3.
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Cross_product">Wikipedia:Cross product</a>
 */
public class Cross extends AbstractArg2 {

	public Cross() {
	}

	@Override
	public IExpr e2ObjArg(final IExpr o0, final IExpr o1) {
		if (o0.isList() && o1.isList()) {
			final IAST v0 = (IAST) o0;
			final IAST v1 = (IAST) o1;
			if ((v0.size() == 4) || (v1.size() == 4)) {
				return List(Plus(Times(v0.arg2(), v1.arg3()), Times(F.CN1, v0.arg3(), v1.arg2())),
						Plus(Times(v0.arg3(), v1.arg1()), Times(F.CN1, v0.arg1(), v1.arg3())),
						Plus(Times(v0.arg1(), v1.arg2()), Times(F.CN1, v0.arg2(), v1.arg1())));
			}
		}
		return null;
	}

}
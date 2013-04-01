package org.matheclipse.core.generic;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class BinaryMap extends BinaryFunctorImpl<IExpr> {
	IAST fConstant;

	public BinaryMap(final IAST constant) {
		fConstant = constant;
	}

	public IExpr apply(final IExpr firstArg, final IExpr secondArg) {
		final IAST ast = fConstant.clone();
		ast.add(firstArg);
		ast.add(secondArg);
		return ast;
	}

}

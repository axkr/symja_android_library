package org.matheclipse.core.generic;


import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class BinaryApply extends BinaryFunctorImpl<IExpr> {
	IAST fConstant;

	public BinaryApply(final IAST constant) {
		fConstant = constant;
	}

	public IExpr apply(final IExpr firstArg, final IExpr secondArg) {
		final IAST ast = fConstant.cloneSet(0, secondArg);
		ast.add(firstArg);
		return ast;
	}

}

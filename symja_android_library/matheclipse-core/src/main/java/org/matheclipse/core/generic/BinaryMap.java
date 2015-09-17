package org.matheclipse.core.generic;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

public class BinaryMap extends BinaryFunctorImpl<IExpr> {
	final IExpr fHeader;
	
	public BinaryMap(final IExpr head) {
		fHeader = head;
	}
	
	public IExpr apply(final IExpr firstArg, final IExpr secondArg) {
		return F.binaryAST2(fHeader, firstArg, secondArg);
	}

}

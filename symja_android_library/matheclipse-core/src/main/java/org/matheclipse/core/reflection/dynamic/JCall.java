package org.matheclipse.core.reflection.dynamic;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

public class JCall {
	public JCall() {
	}

	public static IExpr test1(IExpr a1) {
		return F.stringx(a1.toString());
	}

	public static IExpr test2() {
		return F.stringx("called test2");
	}
}

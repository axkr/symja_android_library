package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class Superscript extends AbstractConverter {

	public Superscript() {
	}

	public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
		if (f.size() != 3) {
			return false;
		}
		IExpr arg1 = f.arg1();
		IExpr arg2 = f.arg2();
		
		fFactory.convertSubExpr(buf, arg1, 0);
		buf.append("^");
		fFactory.convertSubExpr(buf, arg2, 0);
		return true;
	}
}

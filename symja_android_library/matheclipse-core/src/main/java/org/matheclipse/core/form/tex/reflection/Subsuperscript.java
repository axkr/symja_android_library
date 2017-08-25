package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class Subsuperscript extends AbstractConverter {

	public Subsuperscript() {
	}

	public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
		if (f.size() != 4) {
			return false;
		}
		IExpr arg1 = f.arg1();
		IExpr arg2 = f.arg2();
		IExpr arg3 = f.arg3();
		
		fFactory.convert(buf, arg1, Integer.MAX_VALUE);
		buf.append("_");
		fFactory.convert(buf, arg2, Integer.MAX_VALUE);
		buf.append("^");
		fFactory.convert(buf, arg3, Integer.MAX_VALUE);
		return true;
	}
}

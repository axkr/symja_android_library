package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;

public class HarmonicNumber extends AbstractConverter {

	public HarmonicNumber() {
	}

	/** {@inheritDoc} */
	public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
		if (f.isAST1()) {
			buf.append("H_");
			fFactory.convert(buf, f.arg1(), 0);
			return true;
		} else if (f.isAST2()) {
			buf.append("H_");
			fFactory.convert(buf, f.arg1(), 0);

			buf.append("^{(");
			fFactory.convert(buf, f.arg2(), 0);
			buf.append(")}");
			return true;
		}
		return false;
	}
}
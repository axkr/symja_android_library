package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;

public class Ceiling extends AbstractConverter {

	/** constructor will be called by reflection */
	public Ceiling() {
	}

	/** {@inheritDoc} */
	public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
		if (f.size() != 2) {
			return false;
		}
		buf.append(" \\left \\lceil ");
    fFactory.convert(buf, f.arg1(), 0);
    buf.append(" \\right \\rceil ");
		return true;
	}
}
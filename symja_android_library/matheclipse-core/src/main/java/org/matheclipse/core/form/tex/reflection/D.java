package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;

public class D extends AbstractConverter {

	public D() {
	}

	/** {@inheritDoc} */
	public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
		if (f.size() == 3) {

			buf.append("\\frac{d}{{d");
			fFactory.convert(buf, f.get(2), 0);
			buf.append("}}");
			fFactory.convert(buf, f.get(1), 0);

			return true;
		}
		return false;
	}
}
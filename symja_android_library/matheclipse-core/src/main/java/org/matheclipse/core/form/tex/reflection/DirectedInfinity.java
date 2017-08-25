package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;

public class DirectedInfinity extends AbstractConverter {

	public DirectedInfinity() {
	}

	/** {@inheritDoc} */
	public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
		if (f.isAST1()) {
			if (f.arg1().isOne()) {
				buf.append("\\infty");
				return true;
			} else if (f.arg1().isMinusOne()) {
				buf.append("- \\infty");
				return true;
			}

			return true;
		}
		return false;
	}
}
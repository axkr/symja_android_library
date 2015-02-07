package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.tex.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;

public class DirectedInfinity extends AbstractConverter {

	public DirectedInfinity() {
	}

	/** {@inheritDoc} */
	public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
		if (f.size() == 2) {
			if (f.get(1).isOne()) {
				buf.append("\\infty");
				return true;
			} else if (f.get(1).isMinusOne()) {
				buf.append("- \\infty");
				return true;
			}

			return true;
		}
		return false;
	}
}
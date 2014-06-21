package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;

public class Factorial2 extends AbstractConverter {

	/** constructor will be called by reflection */
	public Factorial2() {
	}

	/** {@inheritDoc} */
	public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
		if (f.size() != 2) {
			return false;
		}
		fFactory.convert(buf, f.get(0), 0);
		buf.append('!');
		return true;
	}
}
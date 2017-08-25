package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;

/**
 * 
 */
public class Zeta extends AbstractConverter {
	public Zeta() {
	}

	/** {@inheritDoc} */
	public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
		fFactory.convertAST(buf, f, "zeta ");
		return true;
	}
}

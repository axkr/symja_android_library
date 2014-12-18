package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;

/**
 * 
 */
public class HurwitzZeta extends AbstractConverter {
	public HurwitzZeta() {
	}

	/** {@inheritDoc} */
	public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
		fFactory.convertAST(buf, f, "zeta ");
		return true;
	}
}

package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;

public class Ceiling extends AbstractConverter {

	/** constructor will be called by reflection */
	public Ceiling() {
	}

	/**
	 * Converts a given function into the corresponding MathML output
	 * 
	 * @param buf
	 *          StringBuffer for MathML output
	 * @param f
	 *          The math function which should be converted to MathML
	 */
	public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
		if (f.size() != 2) {
			return false;
		}
		buf.append(" \\left \\lceil ");
    fFactory.convert(buf, f.get(1), 0);
    buf.append(" \\right \\rceil ");
		return true;
	}
}
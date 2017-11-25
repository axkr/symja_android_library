package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.form.mathml.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;

/**
 * Operator function conversions
 */
public class Abs extends AbstractConverter {
	public Abs() {
	}

	/**
	 * Converts a given function into the corresponding MathML output
	 *
	 * @param buf
	 *            StringBuilder for MathML output
	 * @param f
	 *            The math function which should be converted to MathML
	 */
	@Override
	public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
		if (f.size() != 2) {
			return false;
		}
		fFactory.tagStart(buf, "mrow");
		// fFactory.tag(buf, "mo", "&LeftBracketingBar;");
		fFactory.tag(buf, "mo", "&#10072;");
		fFactory.convert(buf, f.arg1(), Integer.MIN_VALUE, false);
		// fFactory.tag(buf, "mo", "&RightBracketingBar;");
		fFactory.tag(buf, "mo", "&#10072;");
		fFactory.tagEnd(buf, "mrow");

		return true;
	}
}

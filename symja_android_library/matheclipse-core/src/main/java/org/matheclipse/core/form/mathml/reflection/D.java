package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.form.mathml.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;

public class D extends AbstractConverter {

	public D() {
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
		if (f.isAST2()) {
			fFactory.tagStart(buf, "mfrac");
			fFactory.tagStart(buf, "mrow");
			// &PartialD; &x02202;
			fFactory.tag(buf, "mo", "&#x2202;");

			fFactory.convert(buf, f.arg1(), Integer.MIN_VALUE, false);
			fFactory.tagEnd(buf, "mrow");
			fFactory.tagStart(buf, "mrow");
			// &PartialD; &x02202
			fFactory.tag(buf, "mo", "&#x2202;");
			fFactory.convert(buf, f.arg2(), Integer.MIN_VALUE, false);

			fFactory.tagEnd(buf, "mrow");
			fFactory.tagEnd(buf, "mfrac");
			return true;
		}
		return false;
	}
}
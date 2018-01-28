package org.matheclipse.core.form.mathml;

import org.matheclipse.core.interfaces.IAST;

/**
 *
 */
public class MMLOperator extends AbstractConverter {
	protected int fPrecedence;
	protected String fFirstTag;
	protected String fOperator;

	public MMLOperator(final int precedence, final String firstTag, final String oper) {
		fPrecedence = precedence;
		fFirstTag = firstTag;
		fOperator = oper;
	}

	public MMLOperator(final int precedence, final String oper) {
		this(precedence, "mrow", oper);
	}

	public void precedenceOpen(final StringBuilder buf, final int precedence) {
		if (precedence > fPrecedence) {
			fFactory.tagStart(buf, "mrow");
			fFactory.tag(buf, "mo", "(");
		}
	}

	public void precedenceClose(final StringBuilder buf, final int precedence) {
		if (precedence > fPrecedence) {
			fFactory.tag(buf, "mo", ")");
			fFactory.tagEnd(buf, "mrow");
		}
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
		fFactory.tagStart(buf, fFirstTag);
		precedenceOpen(buf, precedence);
		for (int i = 1; i < f.size(); i++) {
			fFactory.convert(buf, f.get(i), fPrecedence, false);
			if (i < f.argSize()) {
				if (fOperator.compareTo("") != 0) {
					fFactory.tag(buf, "mo", fOperator);
				}
			}
		}
		precedenceClose(buf, precedence);
		fFactory.tagEnd(buf, fFirstTag);
		return true;
	}

}

package org.matheclipse.core.form.tex;

import org.matheclipse.core.form.tex.AbstractConverter;
import org.matheclipse.core.form.tex.TeXFormFactory;
import org.matheclipse.core.interfaces.IAST;

public class PreOperator extends AbstractConverter {
	protected int fPrecedence;
	protected String fOperator;

	public PreOperator(final int precedence, final String oper) {
		fPrecedence = precedence;
		fOperator = oper;
	}

	public PreOperator(final TeXFormFactory factory, final int precedence, final String oper) {
		super(factory);
		fPrecedence = precedence;
		fOperator = oper;
	}

	public void precedenceOpen(final StringBuffer buf, final int precedence) {
		if (precedence > fPrecedence) {
			buf.append("\\left( ");
		}
	}

	public void precedenceClose(final StringBuffer buf, final int precedence) {
		if (precedence > fPrecedence) {
			buf.append("\\right) ");
		}
	}

	/** {@inheritDoc} */
	public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
		if (f.size() != 2) {
			return false;
		}
		precedenceOpen(buf, precedence);
		buf.append(fOperator);
		fFactory.convert(buf, f.arg1(), fPrecedence);
		precedenceClose(buf, precedence);
		return true;
	}

}
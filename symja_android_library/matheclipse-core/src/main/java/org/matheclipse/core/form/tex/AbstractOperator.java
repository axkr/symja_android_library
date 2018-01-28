package org.matheclipse.core.form.tex;

import org.matheclipse.core.interfaces.IAST;

/**
 *
 */
public class AbstractOperator extends AbstractConverter {
	protected int fPrecedence;
	protected String fOperator;

	public AbstractOperator(final int precedence, final String oper) {
		fPrecedence = precedence;
		fOperator = oper;
	}

	public AbstractOperator(final TeXFormFactory factory, final int precedence, final String oper) {
		super(factory);
		fPrecedence = precedence;
		fOperator = oper;
	}
	
	public void precedenceOpen(final StringBuilder buf, final int precedence) {
		if (precedence > fPrecedence) {
			buf.append("\\left( ");
		}
	}

	public void precedenceClose(final StringBuilder buf, final int precedence) {
		if (precedence > fPrecedence) {
			buf.append("\\right) ");
		}
	} 

	/** {@inheritDoc} */
	@Override
	public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
		precedenceOpen(buf, precedence);
		for (int i = 1; i < f.size(); i++) {
			fFactory.convert(buf, f.get(i), fPrecedence);
			if (i < f.argSize()) {
				if (fOperator.compareTo("") != 0) {
					buf.append(fOperator);
				}
			}
		}
		precedenceClose(buf, precedence);
		return true;
	}

}

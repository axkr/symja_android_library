package org.matheclipse.core.form.tex;

import org.matheclipse.core.interfaces.IAST;

/**
 *
 */
public abstract class AbstractOperator extends AbstractConverter {
  protected int fPrecedence;
  protected String fOperator;

  public AbstractOperator(final int precedence, final String oper) {
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
    precedenceOpen(buf, precedence);
    for (int i = 1; i < f.size(); i++) {
			fFactory.convert(buf, f.get(i), fPrecedence);
      if (i < f.size() - 1) {
        if (fOperator.compareTo("") != 0) {
          buf.append(fOperator);
        }
      }
    }
    precedenceClose(buf, precedence);
    return true;
  }

}

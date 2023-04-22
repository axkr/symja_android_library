package org.matheclipse.core.form.tex;

import org.matheclipse.core.interfaces.IAST;

public class TeXFormOperator extends AbstractTeXConverter {
  protected int fPrecedence;
  protected String fOperator;

  public TeXFormOperator(final int precedence, final String oper) {
    fPrecedence = precedence;
    fOperator = oper;
  }

  public TeXFormOperator(final TeXFormFactory factory, final int precedence, final String oper) {
    super(factory);
    fPrecedence = precedence;
    fOperator = oper;
  }

  /** {@inheritDoc} */
  @Override
  public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
    final boolean isOr = f.isOr();
    precedenceOpen(buffer, precedence);
    for (int i = 1; i < f.size(); i++) {
      if (isOr && f.get(i).isAnd()) {
        buffer.append("\\left( ");
      }
      fFactory.convertInternal(buffer, f.get(i), fPrecedence, TeXFormFactory.NO_PLUS_CALL);
      if (isOr && f.get(i).isAnd()) {
        buffer.append("\\right) ");
      }
      if (i < f.argSize()) {
        if (fOperator.compareTo("") != 0) {
          buffer.append(fOperator);
        }
      }
    }
    precedenceClose(buffer, precedence);
    return true;
  }

  public void precedenceClose(final StringBuilder buf, final int precedence) {
    if (precedence > fPrecedence) {
      buf.append("\\right) ");
    }
  }

  public void precedenceOpen(final StringBuilder buf, final int precedence) {
    if (precedence > fPrecedence) {
      buf.append("\\left( ");
    }
  }
}

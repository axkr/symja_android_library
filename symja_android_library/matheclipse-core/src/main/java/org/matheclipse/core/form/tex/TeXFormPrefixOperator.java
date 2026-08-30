package org.matheclipse.core.form.tex;

import org.matheclipse.core.interfaces.IAST;

/**
 * A prefix operator - <code>Del(f)</code> as <code>\nabla f</code> - the counterpart of
 * {@link TeXFormOperator}, which handles the infix ones.
 *
 * <p>
 * Only a one-argument call is an operator form. A call with any other number of arguments returns
 * <code>false</code>, which is how the factory is told to fall back to the function form:
 * <code>Del(a,b)</code> has no prefix reading and must print as itself.
 */
public class TeXFormPrefixOperator extends AbstractTeXConverter {
  private final int fPrecedence;
  private final String fOperator;

  public TeXFormPrefixOperator(final TeXFormFactory factory, final int precedence,
      final String oper) {
    super(factory);
    fPrecedence = precedence;
    fOperator = oper;
  }

  /** {@inheritDoc} */
  @Override
  public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
    if (!f.isAST1()) {
      return false;
    }
    final boolean parenthesize = precedence > fPrecedence;
    if (parenthesize) {
      buffer.append("\\left( ");
    }
    buffer.append(fOperator);
    fFactory.convertInternal(buffer, f.arg1(), fPrecedence, TeXFormFactory.NO_PLUS_CALL);
    if (parenthesize) {
      buffer.append("\\right) ");
    }
    return true;
  }
}

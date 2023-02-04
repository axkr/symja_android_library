package org.matheclipse.core.form.tex;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.parser.client.operator.Precedence;

public class UnaryBinaryFunction extends AbstractTeXConverter {

  final String first;
  final String middle;
  final String last;

  public UnaryBinaryFunction(String first, String middle, String last) {
    super();
    this.first = first;
    this.middle = middle;
    this.last = last;
  }

  /** {@inheritDoc} */
  @Override
  public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
    if (f.size() != 2 && f.size() != 3) {
      return false;
    }
    buffer.append(first);
    fFactory.convertInternal(buffer, f.arg1(), Precedence.NO_PRECEDENCE,
        TeXFormFactory.NO_PLUS_CALL);
    if (f.size() == 3) {
      buffer.append(middle);
      fFactory.convertInternal(buffer, f.arg2(), Precedence.NO_PRECEDENCE,
          TeXFormFactory.NO_PLUS_CALL);
    }
    buffer.append(last);
    return true;
  }
}

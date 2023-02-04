package org.matheclipse.core.form.tex;

import org.matheclipse.core.interfaces.IAST;

public class QuadrupleFunction extends AbstractTeXConverter {

  final String first;
  final String middle1;
  final String middle2;
  final String middle3;
  final String last;

  public QuadrupleFunction(String first, String middle1, String middle2, String middle3,
      String last) {
    super();
    this.first = first;
    this.middle1 = middle1;
    this.middle2 = middle2;
    this.middle3 = middle3;
    this.last = last;
  }

  /** {@inheritDoc} */
  @Override
  public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
    if (f.size() != 5) {
      return false;
    }
    buffer.append(first);
    fFactory.convertInternal(buffer, f.arg1(), 0);
    buffer.append(middle1);
    fFactory.convertInternal(buffer, f.arg2(), 0);
    buffer.append(middle2);
    fFactory.convertInternal(buffer, f.arg3(), 0);
    buffer.append(middle3);
    fFactory.convertInternal(buffer, f.arg4(), 0);
    buffer.append(last);
    return true;
  }
}
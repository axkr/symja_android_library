package org.matheclipse.core.form.tex;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.parser.client.operator.Precedence;

public class BinaryTernaryFunction extends AbstractTeXConverter {
  final boolean takePenultimate;
  final String first;
  final String middle1;
  final String middle2;
  final String last;

  public BinaryTernaryFunction(String first, String middle1, String middle2, String last,
      boolean takePenultimate) {
    super();
    this.first = first;
    this.middle1 = middle1;
    this.middle2 = middle2;
    this.last = last;
    this.takePenultimate = takePenultimate;
  }

  /** {@inheritDoc} */
  @Override
  public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
    if (f.size() != 3 && f.size() != 4) {
      return false;
    }
    buffer.append(first);
    fFactory.convertInternal(buffer, f.arg1(), Precedence.NO_PRECEDENCE,
        TeXFormFactory.NO_PLUS_CALL);
    if (f.size() == 3) {
      if (takePenultimate) {
        buffer.append(middle2);
      } else {
        buffer.append(middle1);
      }
      fFactory.convertInternal(buffer, f.arg2(), Precedence.NO_PRECEDENCE,
          TeXFormFactory.NO_PLUS_CALL);
    } else {
      buffer.append(middle1);
      fFactory.convertInternal(buffer, f.arg2(), Precedence.NO_PRECEDENCE,
          TeXFormFactory.NO_PLUS_CALL);
      buffer.append(middle2);
      fFactory.convertInternal(buffer, f.arg3(), Precedence.NO_PRECEDENCE,
          TeXFormFactory.NO_PLUS_CALL);
    }
    buffer.append(last);
    return true;
  }
}

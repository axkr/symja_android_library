package org.matheclipse.core.form.tex;

import org.matheclipse.core.interfaces.IAST;

public class QuadrupleFunction extends AbstractTeXConverter {

  final String[] parts;

  public QuadrupleFunction(String first, String middle1, String middle2, String middle3,
      String last) {
    super();
    this.parts = new String[] {first, middle1, middle2, middle3, last};
  }

  /** {@inheritDoc} */
  @Override
  public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
    return convertSeparated(buffer, f, parts);
  }
}

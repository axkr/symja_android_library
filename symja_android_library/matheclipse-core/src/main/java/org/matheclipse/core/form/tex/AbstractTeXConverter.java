package org.matheclipse.core.form.tex;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.parser.client.operator.Precedence;

/**
 * Abstract converter for a TeX conversion in TeXFormFactory.
 */
public abstract class AbstractTeXConverter {
  protected TeXFormFactory fFactory;

  public AbstractTeXConverter() {
    fFactory = null;
  }

  /* package private */ AbstractTeXConverter(final TeXFormFactory factory) {
    fFactory = factory;
  }

  /**
   * Convert the {@link IAST} function into the TeX string presentation by appending the string
   * output to the buffer.
   * 
   * @param buffer
   * @param f the function which should be converted to a string in the buffer.
   * @param precedence
   * @return
   */
  public abstract boolean convert(final StringBuilder buffer, final IAST f, final int precedence);

  /**
   * Append the rows of <code>matrix</code>, the entries separated by <code>&amp;</code> and each
   * row ended by <code>\\\\</code> and a new line.
   */
  protected final void appendMatrixRows(final StringBuilder buffer, final IAST matrix) {
    for (int i = 1; i < matrix.size(); i++) {
      IAST row = (IAST) matrix.get(i);
      for (int j = 1; j < row.size(); j++) {
        buffer.append(' ');
        fFactory.convertInternal(buffer, row.get(j), Precedence.NO_PRECEDENCE,
            TeXFormFactory.NO_PLUS_CALL);
        buffer.append(' ');
        if (j < row.argSize()) {
          buffer.append('&');
        }
      }
      buffer.append("\\\\\n");
    }
  }

  /**
   * Set the factory for internal processes.
   * 
   * @param factory
   */
  /* package private */ void setFactory(final TeXFormFactory factory) {
    fFactory = factory;
  }
}

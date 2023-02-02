package org.matheclipse.core.form.tex;

import org.matheclipse.core.interfaces.IAST;

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
   * Set the factory for internal processes.
   * 
   * @param factory
   */
  /* package private */ void setFactory(final TeXFormFactory factory) {
    fFactory = factory;
  }
}

package org.matheclipse.core.form.tex;


/**
 *
 */
public abstract class AbstractConverter implements IConverter {
  protected AbstractTeXFormFactory fFactory;
  
  public AbstractConverter() {
    fFactory = null;
  }

  public AbstractConverter(final TeXFormFactory factory) {
    fFactory = factory;
  }
  /**
   * @return
   */
  public AbstractTeXFormFactory getFactory() {
    return fFactory;
  }

  /**
   * @param factory
   */
  public void setFactory(final AbstractTeXFormFactory factory) {
    fFactory = factory;
  }

}

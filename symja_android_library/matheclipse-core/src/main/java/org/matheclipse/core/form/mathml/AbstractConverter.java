package org.matheclipse.core.form.mathml;


/**
 *
 */
public abstract class AbstractConverter implements IConverter {
  protected AbstractMathMLFormFactory fFactory;
  
  public AbstractConverter() {
//    fFactory = null;
  }

//  public AbstractConverter() {
//    fFactory = factory;
//  }
  /**
   * @return
   */
  public AbstractMathMLFormFactory getFactory() {
    return fFactory;
  }

  /**
   * @param factory
   */
  public void setFactory(final AbstractMathMLFormFactory factory) {
    fFactory = factory;
  }

  /**
   * @return
   */
//  public ExprFactory getExpressionFactory() {
//    return fExpressionFactory;
//  }

  /**
   * @param factory
   */
//  public void setExpressionFactory(final ExprFactory factory) {
//    fExpressionFactory = factory;
//  }

}

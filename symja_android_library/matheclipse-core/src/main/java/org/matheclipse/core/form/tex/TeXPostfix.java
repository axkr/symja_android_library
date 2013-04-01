package org.matheclipse.core.form.tex;

import org.matheclipse.core.interfaces.IAST;

public class TeXPostfix extends AbstractConverter {
  
  final String fOperator;
  public TeXPostfix(final TeXFormFactory factory, final String operator) {
    super(factory);
    fOperator = operator;
  }

  /**
   * Converts a given function into the corresponding MathML output
   * 
   *@param  buf  StringBuffer for MathML output
   *@param  f    The math function which should be converted to MathML
   */
  public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
    if (f.size() == 2) {
      fFactory.convert(buf, f.get(1), 0);
      buf.append(fOperator);
      return true;
    }
    return false;
  }
}
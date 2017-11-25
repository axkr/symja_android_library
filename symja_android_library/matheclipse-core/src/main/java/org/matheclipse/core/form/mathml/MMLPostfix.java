package org.matheclipse.core.form.mathml;

import org.matheclipse.core.interfaces.IAST;

public class MMLPostfix extends AbstractConverter {
  
  final String fOperator;
  public MMLPostfix(final MathMLFormFactory factory, final String operator) {
    super();
    fOperator = operator;
  }

  /**
   * Converts a given function into the corresponding MathML output
   * 
   *@param  buf  StringBuilder for MathML output
   *@param  f    The math function which should be converted to MathML
   */
  @Override
public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
    if (f.isAST1()) {
      fFactory.tagStart(buf, "mrow");
      fFactory.convert(buf, f.arg1(), Integer.MIN_VALUE, false);
      fFactory.tag(buf, "mo", fOperator);
      fFactory.tagEnd(buf, "mrow");
      return true;
    }
    return false;
  }
}
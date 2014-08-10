package org.matheclipse.core.form.mathml;

import org.matheclipse.core.interfaces.IAST;

public class MMLContentFunction extends AbstractConverter {

  String fFunctionName;

	public MMLContentFunction(final MathMLContentFormFactory factory, final String functionName) {
    super();
    fFunctionName = functionName;
  }
 
  /**
   * Converts a given function into the corresponding MathML output
   *
   *@param  buf  StringBuffer for MathML output
   *@param  f    The math function which should be converted to MathML
   */
  public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {

    fFactory.tagStart(buf, "apply");
    fFactory.tagStartEnd(buf, fFunctionName);
    for (int i = 1; i < f.size(); i++) {
			fFactory.convert(buf, f.get(i), 0);
    }
    fFactory.tagEnd(buf, "apply");
    return true;
  }
}
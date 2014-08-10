package org.matheclipse.core.form.mathml;

import org.matheclipse.core.interfaces.IAST;

public class MMLFunction extends AbstractConverter {

  String fFunctionName;

  public MMLFunction(final MathMLFormFactory factory, final String functionName) {
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

    fFactory.tagStart(buf, "mrow");
    fFactory.tag(buf, "mi", fFunctionName);
    // &af; &#x2061;
    fFactory.tag(buf, "mo", "&#x2061;");

    fFactory.tag(buf, "mo", "(");
    for (int i = 1; i < f.size(); i++) {
			fFactory.convert(buf, f.get(i), 0);
      if (i < f.size() - 1) {
        fFactory.tag(buf, "mo", ",");
      }
    }
    fFactory.tag(buf, "mo", ")");
    fFactory.tagEnd(buf, "mrow");
    return true;
  }
}
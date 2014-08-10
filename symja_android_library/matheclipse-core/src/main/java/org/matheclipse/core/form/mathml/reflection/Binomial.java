package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.form.mathml.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;

public class Binomial extends AbstractConverter {

  public Binomial() {
  }

  /**
   * Converts a given function into the corresponding MathML output
   * 
   *@param  buf  StringBuffer for MathML output
   *@param  f    The math function which should be converted to MathML
   */
  public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
    // "<mrow><mo>(</mo><mfrac linethickness=\"0\">{0}{1}</mfrac><mo>)</mo></mrow>"
    if (f.size() != 3) {
      return false;
    }
    fFactory.tagStart(buf, "mrow");
    fFactory.tag(buf, "mo", "(");
    fFactory.tagStart(buf, "mfrac", "linethickness=\"0\"");
    fFactory.convert(buf, f.get(1), 0);
    fFactory.convert(buf, f.get(2), 0); 
    fFactory.tagEnd(buf, "mfrac");
    fFactory.tag(buf, "mo", ")");
    fFactory.tagEnd(buf, "mrow");
    return true;
  }
}
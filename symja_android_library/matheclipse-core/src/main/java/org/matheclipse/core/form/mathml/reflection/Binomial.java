package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.form.mathml.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;

public class Binomial extends AbstractConverter {

  public Binomial() {
  }

  /**
   * Converts a given function into the corresponding MathML output
   * 
   *@param  buf  StringBuilder for MathML output
   *@param  f    The math function which should be converted to MathML
   */
  @Override
public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
    // "<mrow><mo>(</mo><mfrac linethickness=\"0\">{0}{1}</mfrac><mo>)</mo></mrow>"
    if (f.size() != 3) {
      return false;
    }
    fFactory.tagStart(buf, "mrow");
    fFactory.tag(buf, "mo", "(");
    fFactory.tagStart(buf, "mfrac", "linethickness=\"0\"");
    fFactory.convert(buf, f.arg1(), Integer.MIN_VALUE, false);
    fFactory.convert(buf, f.arg2(), Integer.MIN_VALUE, false); 
    fFactory.tagEnd(buf, "mfrac");
    fFactory.tag(buf, "mo", ")");
    fFactory.tagEnd(buf, "mrow");
    return true;
  }
}
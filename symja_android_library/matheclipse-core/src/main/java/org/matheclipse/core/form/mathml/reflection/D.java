package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.form.mathml.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;



public class D extends AbstractConverter {

  public D() {
  }

  /**
   * Converts a given function into the corresponding MathML output
   *
   *@param  buf  StringBuffer for MathML output
   *@param  f    The math function which should be converted to MathML
   */
  public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
    if (f.size() == 3) {
      fFactory.tagStart(buf, "mfrac");
      fFactory.tagStart(buf, "mrow");
//    &PartialD;  &x02202;
      fFactory.tag(buf, "mo", "&#x2202;");

      fFactory.convert(buf, f.get(1), 0);
      fFactory.tagEnd(buf, "mrow");
      fFactory.tagStart(buf, "mrow");
      // &PartialD;  &x02202
      fFactory.tag(buf, "mo", "&#x2202;");
      fFactory.convert(buf, f.get(2), 0);

      fFactory.tagEnd(buf, "mrow");
      fFactory.tagEnd(buf, "mfrac");
      return true;
    }
    return false;
  }
}
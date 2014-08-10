package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.form.mathml.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;

public class List extends AbstractConverter {

  /** constructor will be called by reflection */
  public List() {
  }

  /**
   * Converts a given function into the corresponding MathML output
   * 
   *@param  buf  StringBuffer for MathML output
   *@param  f    The math function which should be converted to MathML
   */
  public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
    fFactory.tagStart(buf, "mrow");
    fFactory.tag(buf, "mo", "{");
    if (f.size() > 1) {
      fFactory.tagStart(buf, "mrow");
      fFactory.convert(buf, f.get(1), 0);
      for (int i = 2; i < f.size(); i++) {
				fFactory.tag(buf, "mo", ",");
        fFactory.convert(buf, f.get(i), 0);
      }
      fFactory.tagEnd(buf, "mrow");
    }
    fFactory.tag(buf, "mo", "}");
    fFactory.tagEnd(buf, "mrow");
    return true;
  }
}
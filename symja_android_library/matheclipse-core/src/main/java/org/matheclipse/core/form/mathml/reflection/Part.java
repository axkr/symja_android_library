package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.form.mathml.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;

public class Part extends AbstractConverter {

  /** constructor will be called by reflection */
  public Part() {
  }

  /**
   * Converts a given function into the corresponding MathML output
   *
   *@param  buf  StringBuffer for MathML output
   *@param  f    The math function which should be converted to MathML
   */
  public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
    if (f.size() < 3) {
      return false;
    }
    fFactory.tagStart(buf, "mrow");
    fFactory.convert(buf, f.get(1), 0);
    // &LeftDoubleBracket;   &#x301A;
    fFactory.tag(buf, "mo", "&#x301A;");
    if (f.size() > 2) {
      fFactory.tagStart(buf, "mrow");
      fFactory.convert(buf, f.get(2), 0);
      for (int i = 3; i < f.size(); i++) {
				fFactory.tag(buf, "mo", ",");
        fFactory.convert(buf, f.get(i), 0);
      }
      fFactory.tagEnd(buf, "mrow");
    }
    //&RightDoubleBracket; &#x301B;
    fFactory.tag(buf, "mo", "&#x301B;");
    fFactory.tagEnd(buf, "mrow");
    return true;
  }
}
package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.form.mathml.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;

public class Floor extends AbstractConverter {

  /** constructor will be called by reflection */
  public Floor() {
  }

  /**
   * Converts a given function into the corresponding MathML output
   *
   *@param  buf  StringBuilder for MathML output
   *@param  f    The math function which should be converted to MathML
   */
  @Override
public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
    if (f.size() != 2) {
      return false;
    }
    fFactory.tagStart(buf, "mrow");
    // &LeftFloor; &x0230A;
    fFactory.tag(buf, "mo", "&#x230A;");
    fFactory.convert(buf, f.arg1(), Integer.MIN_VALUE, false);
    // &RightFloor; &#0230B;
    fFactory.tag(buf, "mo", "&#230B;");
    fFactory.tagEnd(buf, "mrow");
    return true;
  }
}
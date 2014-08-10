package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.form.mathml.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;

public class Factorial extends AbstractConverter {

  /** constructor will be called by reflection */
  public Factorial() {
  }

  /**
   * Converts a given function into the corresponding MathML output
   * 
   *@param  buf  StringBuffer for MathML output
   *@param  f    The math function which should be converted to MathML
   */
  public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
    //    "<mrow>{0}<mo>!</mo></mrow>"
    if (f.size() != 2) {
      return false;
    }
    fFactory.tagStart(buf, "mrow");
    fFactory.convert(buf, f.get(1), 0);
    fFactory.tag(buf, "mo", "!");
    fFactory.tagEnd(buf, "mrow");
    return true;
  }
}
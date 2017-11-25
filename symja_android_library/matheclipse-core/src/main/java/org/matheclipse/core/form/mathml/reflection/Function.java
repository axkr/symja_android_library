package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.form.mathml.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;

public class Function extends AbstractConverter {

  /** constructor will be called by reflection */
  public Function() {
  }

  /**
   * Converts a given function into the corresponding MathML output
   * 
   *@param  buf  StringBuilder for MathML output
   *@param  f    The math function which should be converted to MathML
   */
  @Override
public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
    //    "<mrow>{0}<mo>!</mo></mrow>"
    if (f.size() != 2) {
      return false;
    }
    fFactory.tagStart(buf, "mrow");
    fFactory.convert(buf, f.arg1(), Integer.MIN_VALUE, false);
    fFactory.tag(buf, "mo", "&amp;");
    fFactory.tagEnd(buf, "mrow");
    return true;
  }
}
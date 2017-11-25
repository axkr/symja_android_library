package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.form.mathml.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;

public class Ceiling extends AbstractConverter {

  /** constructor will be called by reflection */
  public Ceiling() {
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
    // LeftCeiling &#002308;
    fFactory.tag(buf, "mo", "&#x2308;");
    fFactory.convert(buf, f.arg1(), Integer.MIN_VALUE, false);
    // RightCeiling &#x02309;
    fFactory.tag(buf, "mo", "&#x2309;");
    fFactory.tagEnd(buf, "mrow");
    return true;
  }
}
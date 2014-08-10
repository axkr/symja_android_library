package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.form.mathml.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;

/**
 * Operator function conversions
 */
public class Abs extends AbstractConverter {
  public Abs() {
  }

  /**
    * Converts a given function into the corresponding MathML output
    *
    *@param  buf  StringBuffer for MathML output
    *@param  f    The math function which should be converted to MathML
    */
  public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
    if (f.size() != 2) {
      return false;
    }
    fFactory.tagStart(buf, "mrow");
    fFactory.tag(buf, "mo", "&LeftBracketingBar;");
    fFactory.convert(buf, f.get(1), 0);
    fFactory.tag(buf, "mo", "&RightBracketingBar;");
    fFactory.tagEnd(buf, "mrow");

    return true;
  }
}

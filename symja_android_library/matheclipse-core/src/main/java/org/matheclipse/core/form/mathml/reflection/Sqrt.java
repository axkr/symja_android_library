package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.form.mathml.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;

/** 
 * Operator function conversions 
 */
public class Sqrt extends AbstractConverter {
  public Sqrt() {
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
    fFactory.tagStart(buf, "msqrt");
    fFactory.convert(buf, f.arg1(), Integer.MIN_VALUE, false);
    fFactory.tagEnd(buf, "msqrt");
    return true;
  }
}

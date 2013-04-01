package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;

/** 
 * Operator function conversions 
 */
public class Sqrt extends AbstractConverter {
  public Sqrt() {
  }

  /**
    * Converts a given function into the corresponding TeX output
    *
    *@param  buf  StringBuffer for MathML output
    *@param  f    The math function which should be converted to MathML
    */
  public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
    if (f.size() != 2) {
      return false;
    }
    buf.append("\\sqrt{");
    fFactory.convert(buf, f.get(1), 0);
    buf.append('}');
    return true;  
  }
}

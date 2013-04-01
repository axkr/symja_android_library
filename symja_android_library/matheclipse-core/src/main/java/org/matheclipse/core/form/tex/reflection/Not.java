package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractConverter;
import org.matheclipse.core.form.tex.TeXFormFactory;
import org.matheclipse.core.interfaces.IAST;

public class Not extends AbstractConverter {

  public Not(final TeXFormFactory factory) {
    super(factory);
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
    buf.append(" \\neg ");
    fFactory.convert(buf, f.get(1), 0);
    return true;
  }
}
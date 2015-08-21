package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;

public class Binomial extends AbstractConverter {

  public Binomial() {
  }

  /** {@inheritDoc} */
  public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
    // "<mrow><mo>(</mo><mfrac linethickness=\"0\">{0}{1}</mfrac><mo>)</mo></mrow>"
    if (f.size() != 3) {
      return false;
    }
    buf.append('{');
    fFactory.convert(buf, f.arg1(), 0);
    buf.append("\\choose ");
    fFactory.convert(buf, f.arg2(), 0); 
    buf.append('}');
    return true;
  }
}
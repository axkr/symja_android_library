package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.expression.IConstantHeaders;
import org.matheclipse.core.form.tex.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;

public class Product extends AbstractConverter {

  public Product() {

  }

  /**
   * Converts a given function into the corresponding MathML output
   *
   *@param  buf  StringBuffer for MathML output
   *@param  f    The math function which should be converted to MathML
   */
  public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
    if (f.size() >= 3) {
      for (int i = 2; i < f.size(); i++) {
				if ((f.get(i) instanceof IAST) && ((IAST) f.get(i)).head().toString().equals(IConstantHeaders.List)) {
        	final IAST list = (IAST) f.get(i);
        	buf.append("\\prod_{");

          if (list.size() > 1) {
            fFactory.convert(buf, list.get(1), 0);
          }
          if (list.size() > 2) {
          	buf.append(" = ");
            fFactory.convert(buf, list.get(2), 0);
          }
          if (list.size() > 3) {
          	buf.append("}^{");
            fFactory.convert(buf, list.get(3), 0);
          }
          buf.append('}');
        } else {
          return false;
        }
      }
      fFactory.convert(buf, f.get(1), 0);
      return true;
    }
    return false;
  }

}
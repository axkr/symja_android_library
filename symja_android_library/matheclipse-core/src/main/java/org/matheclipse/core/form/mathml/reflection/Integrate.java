package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.form.mathml.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;

public class Integrate extends AbstractConverter {

  public Integrate() {
  }

  /**
   * Converts a given function into the corresponding MathML output
   *
   *@param  buf  StringBuffer for MathML output
   *@param  f    The math function which should be converted to MathML
   */
  public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
    if (f.size() == 3) {
      fFactory.tagStart(buf, "mrow");
      final Object obj = f.get(2);
      IAST list = null;
      if (obj instanceof IAST) {
        list = (IAST) obj;
      }
      if ((list != null) && (list.size() == 4) && list.head().toString().equals(AST2Expr.LIST_STRING)) {
        fFactory.tagStart(buf, "msubsup");
        // &Integral;  &#x222B;
        fFactory.tag(buf, "mo", "&#x222B;");
        fFactory.convert(buf, list.get(2), 0);
        fFactory.convert(buf, list.get(3), 0);
        fFactory.tagEnd(buf, "msubsup");
      } else {
        list = null;
        fFactory.tag(buf, "mo", "&#x222B;");
      }

      fFactory.tagStart(buf, "mrow");
      fFactory.convert(buf, f.get(1), 0);
      fFactory.tagStart(buf, "mrow");
      // &dd; &#x2146;
      fFactory.tag(buf, "mo", "&#x2146;");
      if (list != null) {
        fFactory.convert(buf, list.get(1), 0);
      } else {
        fFactory.convert(buf, f.get(2), 0);
      }
      fFactory.tagEnd(buf, "mrow");
      fFactory.tagEnd(buf, "mrow");
      fFactory.tagEnd(buf, "mrow");
      return true;
    }
    return false;
  }
}
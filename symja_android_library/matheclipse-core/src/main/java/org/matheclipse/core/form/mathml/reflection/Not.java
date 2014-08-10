package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.form.mathml.AbstractConverter;
import org.matheclipse.core.form.mathml.MathMLFormFactory;
import org.matheclipse.core.interfaces.IAST;

public class Not extends AbstractConverter {

  public Not(final MathMLFormFactory factory) {
    super();
  }

  /**
   * Converts a given function into the corresponding MathML output
   *
   *@param  buf  StringBuffer for MathML output
   *@param  f    The math function which should be converted to MathML
   */
  public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
    // <mrow><mo>&not;</mo>{0}</mrow>
    if (f.size() != 2) {
      return false;
    }
    fFactory.tagStart(buf, "mrow");
    // &not;  &#x00AC;
    fFactory.tag(buf, "mo", "&#x00AC;");
    fFactory.convert(buf, f.get(1), 0);
    fFactory.tagEnd(buf, "mrow");
    return true;
  }
}
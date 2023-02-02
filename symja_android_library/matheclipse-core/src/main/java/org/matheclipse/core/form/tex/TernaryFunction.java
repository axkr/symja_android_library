package org.matheclipse.core.form.tex;

import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;

public class TernaryFunction extends AbstractTeXConverter {

  final String first;
  final String middle1;
  final String middle2;
  final String last;

  /**
   * <p>
   * Convert a ternary function into it's TeX string representation
   * 
   * <p>
   * Example for the {@link S.BetaRegularized} function:
   * 
   * <pre>
   * new TernaryFunction("I_", "(", ",", ")")
   * </pre>
   * 
   * @param prefixStr
   * @param infix1Str
   * @param infix2Str
   * @param postfixStr
   */
  public TernaryFunction(String prefixStr, String infix1Str, String infix2Str, String postfixStr) {
    super();
    this.first = prefixStr;
    this.middle1 = infix1Str;
    this.middle2 = infix2Str;
    this.last = postfixStr;
  }

  /** {@inheritDoc} */
  @Override
  public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
    if (f.size() != 4) {
      return false;
    }
    buffer.append(first);
    fFactory.convertInternal(buffer, f.arg1(), 0);
    buffer.append(middle1);
    fFactory.convertInternal(buffer, f.arg2(), 0);
    buffer.append(middle2);
    fFactory.convertInternal(buffer, f.arg3(), 0);
    buffer.append(last);
    return true;
  }
}

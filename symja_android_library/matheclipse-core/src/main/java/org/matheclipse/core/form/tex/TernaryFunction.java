package org.matheclipse.core.form.tex;

import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;

public class TernaryFunction extends AbstractTeXConverter {

  final String[] parts;

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
    this.parts = new String[] {prefixStr, infix1Str, infix2Str, postfixStr};
  }

  /** {@inheritDoc} */
  @Override
  public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
    return convertSeparated(buffer, f, parts);
  }
}

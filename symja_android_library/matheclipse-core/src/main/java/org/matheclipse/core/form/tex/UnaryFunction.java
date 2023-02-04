package org.matheclipse.core.form.tex;

import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;

public class UnaryFunction extends AbstractTeXConverter {
  String pre;
  String post;

  /**
   * <p>
   * Convert a unary function into it's TeX string representation
   * 
   * <p>
   * Example for the {@link S.Abs} function:
   * 
   * <pre>
   * new UnaryFunction("|", "|")
   * </pre>
   * 
   * @param prefixStr
   * @param postfixStr
   */
  public UnaryFunction(String prefixStr, String postfixStr) {
    this.pre = prefixStr;
    this.post = postfixStr;
  }

  /** {@inheritDoc} */
  @Override
  public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
    if (f.size() != 2) {
      return false;
    }
    buffer.append(pre);
    fFactory.convertInternal(buffer, f.arg1(), 0, TeXFormFactory.NO_PLUS_CALL);
    buffer.append(post);
    return true;
  }
}

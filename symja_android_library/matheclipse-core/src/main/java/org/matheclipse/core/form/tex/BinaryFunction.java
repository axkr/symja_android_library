package org.matheclipse.core.form.tex;

import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;

/**
 * Used for: <code>HermiteH(a,b)) ==> H_a(b)</code>, <code>LaguerreL(a,b)) ==> L_a(b)</code>
 *
 */
public class BinaryFunction extends AbstractTeXConverter {

  final String first;
  final String middle;
  final String last;

  /**
   * <p>
   * Convert a binary function into it's TeX string representation
   * 
   * <p>
   * Example for the {@link S.ChebyshevT} function:
   * 
   * <pre>
   *   new BinaryFunction("T_", "(", ")"))
   * </pre>
   * 
   * @param prefixStr
   * @param infixStr
   * @param postfixStr
   */
  public BinaryFunction(String prefixStr, String infixStr, String postfixStr) {
    super();
    this.first = prefixStr;
    this.middle = infixStr;
    this.last = postfixStr;
  }

  /** {@inheritDoc} */
  @Override
  public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
    if (f.size() != 3) {
      return false;
    }
    buffer.append(first);
    fFactory.convertInternal(buffer, f.arg1(), 0);
    buffer.append(middle);
    fFactory.convertInternal(buffer, f.arg2(), 0);
    buffer.append(last);
    return true;
  }
}

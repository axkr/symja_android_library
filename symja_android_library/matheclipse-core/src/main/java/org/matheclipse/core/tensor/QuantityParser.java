// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.tensor.qty.IQuantity;
import org.matheclipse.core.tensor.qty.IUnit;

public class QuantityParser {

  /**
   * Example: "9.81[m*s^-2]" -> Quantity.of(9.81, "m*s^-2")
   *
   * @param string
   */
  public static IExpr of(final String string) {
    final int index = string.indexOf(IQuantity.UNIT_OPENING_BRACKET);
    if (0 < index) {
      final int last = string.indexOf(IQuantity.UNIT_CLOSING_BRACKET);
      if (index < last && string.substring(last + 1).trim().isEmpty())
        return IQuantity.of( //
            QuantityParser.fromString(string.substring(0, index)), //
            IUnit.ofPutIfAbsent(string.substring(index + 1, last)));
      throw new IllegalArgumentException(string);
    }
    try {
      EvalEngine engine = new EvalEngine(true);
      return engine.evaluate(string, true);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      Errors.printMessage(S.Quantity, rex, EvalEngine.get());
      throw new IllegalArgumentException(string, rex);
    }
  }

  /**
   * Parses a given string to an instance of {@link IExpr}
   *
   * <p>
   * Examples:
   *
   * <pre>
   * "7/9" -> RationalScalar.of(7, 9)
   * "3.14" -> DoubleScalar.of(3.14)
   * "(3+2)*I/(-1+4)+8-I" -> ComplexScalar.of(8, 2/3) == "8+2/3*I"
   * "9.81[m*s^-2]" -> Quantity.of(9.81, "m*s^-2")
   * </pre>
   *
   * If the parsing logic encounters an inconsistency, the return type is a {@link IStringX} that
   * holds the input string.
   *
   * <p>
   * Scalar types that are not supported include {@link GaussScalar}.
   *
   * @param string
   * @return scalar
   */
  public static IExpr fromString(String string) {
    try {
      return of(string);
    } catch (Exception exception) {
      Errors.rethrowsInterruptException(exception);
  
    }
    return F.stringx(string);
  }
}

// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.qty.IQuantity;
import org.matheclipse.core.tensor.qty.IUnit;

public class QuantityParser {
  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * Example: "9.81[m*s^-2]" -> Quantity.of(9.81, "m*s^-2")
   *
   * @param string
   * @return
   */
  public static IExpr of(final String string) {
    final int index = string.indexOf(IQuantity.UNIT_OPENING_BRACKET);
    if (0 < index) {
      final int last = string.indexOf(IQuantity.UNIT_CLOSING_BRACKET);
      if (index < last && string.substring(last + 1).trim().isEmpty())
        return IQuantity.of( //
            F.fromString(string.substring(0, index)), //
            IUnit.ofPutIfAbsent(string.substring(index + 1, last)));
      throw new IllegalArgumentException(string);
    }
    try {
      EvalEngine engine = new EvalEngine(true);
      return engine.evaluate(string, true);
    } catch (RuntimeException rex) {
      LOGGER.error("QuantityParser.of() failed", rex);
      throw new IllegalArgumentException(string, rex);
    }
  }
}

// code by jph
package ch.ethz.idsc.tensor;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

import ch.ethz.idsc.tensor.qty.IQuantity;
import ch.ethz.idsc.tensor.qty.IUnit;

public enum QuantityParser {
	;
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
						IUnit.of(string.substring(index + 1, last)));
			throw new RuntimeException(string);
		}
		try {
			EvalEngine engine = new EvalEngine(true);
			return engine.evaluate(string, true);
		} catch (RuntimeException rex) {
			rex.printStackTrace();
			throw new RuntimeException(string);
		}
	}

}

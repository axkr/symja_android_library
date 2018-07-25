package ch.ethz.idsc.tensor.qty;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.parser.client.math.MathException;

/**
 * {@link IQuantity} represents a magnitude and unit.
 * 
 * <p>
 * The sum of two quantities is well defined whenever the units are identical. Two quantities with different units are
 * added if one of the values equals to zero. In that case the result carries the unit of the non-zero input quantity.
 * 
 * <p>
 * For export and import of tensors with scalars of type {@link IQuantity} use {@link ObjectFormat} and
 * {@link CsvFormat}.
 * 
 * <p>
 * Two quantities are comparable only if they have the same unit. Otherwise an exception is thrown.
 * 
 * <p>
 * Different units should mapped to a common unit system before carrying out operations.
 * 
 * <pre>
 * Scalar a = Quantity.of(200, "g");
 * Scalar b = Quantity.of(1, "kg");
 * Total.of(Tensors.of(a, b).map(UnitSystem.SI())) == 6/5[kg]
 * </pre>
 * 
 * whereas <code>a.add(b)</code> throws an Exception.
 */
public interface IQuantity extends IExpr, Comparable<IExpr> {
	static final char UNIT_OPENING_BRACKET = '[';
	static final char UNIT_CLOSING_BRACKET = ']';

	/**
	 * Hint: function does not check parameters for null, although null as input is likely to cause problems
	 * subsequently.
	 * 
	 * @param value
	 * @param unit
	 *            for instance Unit.of("m*s^-1")
	 * @return
	 * @throws Exception
	 *             if value is instance of {@code Quantity}
	 */
	static IExpr of(IExpr value, IUnit unit) {
		if (value instanceof IQuantity) {
			throw MathException.of(value);
		}
		return QuantityImpl.of(value, unit);
	}

	/**
	 * Hint: function does not check parameters for null, although null as input is likely to cause problems
	 * subsequently.
	 * 
	 * @param value
	 * @param string
	 *            for instance "m*s^-2"
	 * @return
	 * @throws Exception
	 *             if value is instance of {@code Quantity}
	 */
	static IExpr of(IExpr value, String string) {
		if (value instanceof IQuantity)
			throw MathException.of(value);
		return QuantityImpl.of(value, IUnit.of(string));
	}

	/**
	 * creates quantity with number encoded as {@link ISignedNumber}
	 * 
	 * Hint: function does not check parameters for null, although null as input is likely to cause problems
	 * subsequently.
	 * 
	 * @param number
	 *            non-null
	 * @param unit
	 * @return
	 * @throws Exception
	 *             if parameter number equals null
	 */
	static IExpr of(Number number, IUnit unit) {
		return QuantityImpl.of(F.expr(number), unit);
	}

	/**
	 * creates quantity with number encoded as {@link ISignedNumber}
	 * 
	 * @param number
	 * @param string
	 *            for instance "kg^3*m*s^-2"
	 * @return
	 * @throws Exception
	 *             if either parameter equals null
	 */
	static IExpr of(Number number, String string) {
		return QuantityImpl.of(F.expr(number), IUnit.of(string));
	}

	public IQuantity ofUnit(IExpr scalar);

	/** @return unit of quantity without value */
	IUnit unit();

	/**
	 * 
	 * @return value of quantity without unit
	 */
	IExpr value();
}

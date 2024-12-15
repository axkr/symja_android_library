// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.qty;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAtomicConstant;
import org.matheclipse.core.interfaces.IAtomicEvaluate;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.parser.client.math.MathException;
import com.univocity.parsers.csv.CsvFormat;

/**
 * {@link IQuantity} represents a magnitude and unit.
 *
 * <p>
 * The sum of two quantities is well defined whenever the units are identical. Two quantities with
 * different units are added if one of the values equals to zero. In that case the result carries
 * the unit of the non-zero input quantity.
 *
 * <p>
 * For export and import of tensors with scalars of type {@link IQuantity} use {@link ObjectFormat}
 * and {@link CsvFormat}.
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
@SuppressWarnings("ComparableType")
public interface IQuantity extends IExpr, IAtomicConstant, IAtomicEvaluate, Comparable<IExpr> {
  static final char UNIT_OPENING_BRACKET = '[';
  static final char UNIT_CLOSING_BRACKET = ']';

  /**
   * Hint: function does not check parameters for null, although null as input is likely to cause
   * problems subsequently.
   *
   * @param value
   * @param unit for instance Unit.of("m*s^-1")
   * @return
   * @throws Exception if value is instance of {@code Quantity}
   */
  static IExpr of(IExpr value, IUnit unit) {
    if (value instanceof IQuantity) {
      throw MathException.of(value);
    }
    return QuantityImpl.of(value, unit);
  }

  static IQuantity of(INumber value, IUnit unit) {
    return new QuantityImpl(value, unit);
  }

  /**
   * Hint: function does not check parameters for null, although null as input is likely to cause
   * problems subsequently.
   *
   * @param value
   * @param unit for instance "m*s^-2"
   * @return
   * @throws Exception if value is instance of {@code Quantity}
   */
  static IExpr of(IExpr value, String unit) {
    return of(value, IUnit.ofPutIfAbsent(unit));
  }

  /**
   * creates quantity with number encoded as {@link IReal}
   *
   * <p>
   * Hint: function does not check parameters for null, although null as input is likely to cause
   * problems subsequently.
   *
   * @param number non-null
   * @param unit
   * @return
   * @throws Exception if parameter number equals null
   */
  static IExpr of(Number number, IUnit unit) {
    return QuantityImpl.of(F.expr(number), unit);
  }

  /**
   * creates quantity with number encoded as {@link IReal}
   *
   * @param number
   * @param unit for instance "kg^3*m*s^-2"
   * @return
   * @throws Exception if either parameter equals null
   */
  static IExpr of(Number number, String unit) {
    return of(F.expr(number), unit);
  }

  public IQuantity ofUnit(IExpr scalar);

  /**
   * Add this quantity with a scalar.
   *
   * @param scalar
   * @param nilIfUnevaluated if <code>true</code>, return {@link F#NIL} if unevaluated
   * @return
   */
  public IExpr plus(final IExpr scalar, boolean nilIfUnevaluated);

  @Override
  public IExpr roundExpr();

  /**
   * Multiply this quantity with a scalar.
   *
   * @param scalar
   * @param nilIfUnevaluated if <code>true</code>, return {@link F#NIL} if unevaluated
   * @return
   */
  public IExpr times(IExpr scalar, boolean nilIfUnevaluated);

  /** @return unit of quantity without value */
  IUnit unit();

  String unitString();

  /** @return value of quantity without unit */
  IExpr value();
}

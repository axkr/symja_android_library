// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.qty;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.tensor.QuantityParser;
import org.matheclipse.parser.client.math.MathException;

/**
 * Reference implementation of {@link UnitSystem} with emphasis on simplicity
 */
public class SimpleUnitSystem implements UnitSystem {

  /**
   * given properties map a unit expression to a {@link IQuantity}
   *
   * <p>
   * Example from the built-in file "/unit/si.properties":
   *
   * <pre>
   * rad=1
   * Hz=1[s^-1]
   * N=1[m*kg*s^-2]
   * Pa=1[m^-1*kg*s^-2]
   * ...
   * </pre>
   *
   * @param properties
   * @return
   * @throws Exception if keys do not define unit conversions
   */
  public static UnitSystem from(Properties properties) {
    return new SimpleUnitSystem(properties.stringPropertyNames().stream().collect(Collectors.toMap( //
        UnitHelper::requireValid,
        key -> requireNumeric(QuantityParser.fromString(properties.getProperty(key))))));
  }

  /**
   * @param map
   * @return unit system
   */
  public static UnitSystem from(Map<String, IExpr> map) {
    return new SimpleUnitSystem(map.entrySet().stream().collect(Collectors.toMap( //
        entry -> UnitHelper.requireValid(entry.getKey()),
        entry -> requireNumeric(entry.getValue()))));
  }

  // ---
  private final Map<String, IExpr> map;

  private SimpleUnitSystem(Map<String, IExpr> map) {
    this.map = map;
  }

  @Override
  public IExpr apply(IExpr scalar) {
    if (scalar instanceof IQuantity) {
      final IQuantity quantity = (IQuantity) scalar;
      IExpr value = quantity.value();
      IExpr v1;
      for (Entry<String, IExpr> entry : quantity.unit().map().entrySet()) {
        IExpr lookup = map.get(entry.getKey());
        IExpr entryValue = entry.getValue();
        IExpr temp = Objects.isNull(lookup) //
            ? IQuantity.of(F.C1, format(entry)) //
            : lookup.isQuantity() //
                ? ((IQuantity) lookup).power(entryValue) //
                : S.Power.of(lookup, entryValue);
        if (temp.isQuantity()) {
          v1 = ((IQuantity) temp).times(value, true);
        } else {
          v1 = value.times(temp);
        }
        if (v1.isPresent()) {
          value = v1;
        } else {
          if (temp.isQuantity()) {
            value = F.Times(temp, value);
          } else {
            value = F.Times(value, temp);
          }
        }
      }
      return value;
    }
    return Objects.requireNonNull(scalar);
  }

  @Override // from UnitSystem
  public Map<String, IExpr> map() {
    return Collections.unmodifiableMap(map);
  }

  // helper function
  private static IExpr requireNumeric(IExpr scalar) {
    if (scalar instanceof IStringX) {
      throw MathException.of(scalar);
    }
    if (scalar instanceof IQuantity) {
      IQuantity quantity = (IQuantity) scalar;
      if (quantity.value() instanceof IStringX) {
        throw MathException.of(scalar);
      }
    }
    return scalar;
  }

  // helper function
  private static String format(Entry<String, IExpr> entry) {
    return entry.getKey() + IUnit.POWER_DELIMITER + entry.getValue();
  }
}

package ch.ethz.idsc.tensor.qty;

import java.util.Map;
import java.util.function.UnaryOperator;

import org.matheclipse.core.interfaces.IExpr;


/** UnitSystem is an operator that maps a given {@link IQuantity} to a {@link IQuantity}
 * that makes use only of standard units defined by the unit system.
 * 
 * <p>Example when using the built-in SI definitions:
 * <pre>
 * UnitSystem.SI().apply(Quantity.of(1, "V")) == 1[A^-1*kg*m^2*s^-3]
 * UnitSystem.SI().apply(Quantity.of(125, "mi")) == 201168[m]
 * </pre>
 * 
 * <p>{@link SimpleUnitSystem} is a reference implementation of the interface
 * provided by the tensor library.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/UnitSystem.html">UnitSystem</a> */
public interface UnitSystem extends  UnaryOperator<IExpr> {
  /** @return international system of units, metric system */
  static UnitSystem SI() {
    return BuiltIn.SI.unitSystem;
  }

  /** @return unmodifiable view on map that defines the unit system */
  Map<String, IExpr> map();
}

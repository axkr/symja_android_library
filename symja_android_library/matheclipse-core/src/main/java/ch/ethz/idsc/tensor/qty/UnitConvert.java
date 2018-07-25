package ch.ethz.idsc.tensor.qty;

import java.util.Objects;
import java.util.function.UnaryOperator;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/UnitConvert.html">UnitConvert</a> */
public class UnitConvert {
  /** @return instance of UnitConvert that uses the built-in SI convention */
  public static UnitConvert SI() {
    return BuiltIn.SI.unitConvert;
  }
  // ---

  private final UnitSystem unitSystem;

  /** @param unitSystem
   * @throws Exception if given {@link UnitSystem} is null */
  public UnitConvert(UnitSystem unitSystem) {
    this.unitSystem = Objects.requireNonNull(unitSystem);
  }

  /** Example:
   * <code>
   * UnitConvert.SI().to(Unit.of("N")).apply(Quantity.of(981, "cm*kg*s^-2"))
   * == Quantity.fromString("981/100[N]")
   * </code>
   * 
   * @param unit
   * @return operator that maps a quantity to the quantity of given unit */
  public UnaryOperator<IExpr> to(IUnit unit) {
    IExpr base = unitSystem.apply(IQuantity.of(F.C1, unit));
    return scalar -> IQuantity.of(unitSystem.apply(scalar).divide(base), unit);
  }
}

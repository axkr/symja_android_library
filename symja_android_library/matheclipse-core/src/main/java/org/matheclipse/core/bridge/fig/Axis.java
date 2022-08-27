// code adapted from https://github.com/datahaki/bridge
package org.matheclipse.core.bridge.fig;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;
import org.matheclipse.core.bridge.lang.Unicode;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.qty.IUnit;
import org.matheclipse.core.tensor.qty.QuantityMagnitude;
import org.matheclipse.core.tensor.qty.QuantityUnit;
import org.matheclipse.core.tensor.qty.UnitConvert;
import org.matheclipse.core.tensor.sca.Clip;
import org.matheclipse.core.tensor.sca.Clips;

public class Axis implements Serializable {

  public enum Type {
    LINEAR, //
    LOGARITHMIC, //
  }

  private String label = "";
  private IUnit unit = null;
  private Clip clip = null;
  private Type type = Type.LINEAR;

  public Axis() {
    // ---
  }

  public Axis(Clip clip) {
    setClip(clip);
  }

  /** @param label of axis */
  public void setLabel(String string) {
    label = string;
  }

  /** @return label of axis */
  public String getLabel() {
    return label;
  }

  public void setUnit(IUnit unit) {
    this.unit = unit;
  }

  public IUnit getUnit() {
    return unit;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public Type getType() {
    return type;
  }

  // ---
  /** @param clip with non-zero width */
  public void setClip(Clip clip) {
    this.clip = clip;
    if (Objects.nonNull(clip)) {
      if (clip.width().isZero()) {
        System.err.println("empty axis range is not supported");
      }
      if (Objects.isNull(unit)) {
        setUnit(QuantityUnit.of(clip));
      }
    }
  }

  /**
   * @return
   * @throws Exception if clip was not defined for this axis
   */
  public Clip getClip() {
    return Objects.requireNonNull(clip);
  }

  public Optional<Clip> getOptionalClip() {
    return Objects.isNull(clip) //
        ? Optional.empty()
        : Optional.of(slash(clip, UnitConvert.SI().to(unit)));
  }

  /**
   * @param clip
   * @param monotonousOperator
   * @return Clip[monotonousOperator[clip.min], monotonousOperator[clip.max]]
   */
  static Clip slash(Clip clip, UnaryOperator<IExpr> monotonousOperator) {
    return Clips.interval( //
        monotonousOperator.apply(clip.min()), //
        monotonousOperator.apply(clip.max()));
  }

  // ---
  /** @return label combined with unit */
  /* package */ String getAxisLabel() {
    return (getLabel() + " " + getUnitString()).strip();
  }

  /* package */ boolean hasUnit() {
    return Objects.nonNull(unit);
  }

  /**
   * 
   * @return operator that maps an expression value to instance of {@link Num}.
   */
  /* package */ UnaryOperator<IExpr> toReals() {
    return QuantityMagnitude.SI().in(getAxisUnit());
  }

  /* package */ String getUnitString() {
    IUnit unit = getAxisUnit();
    return IUnit.ONE.equals(unit) ? "" : '[' + Unicode.valueOf(unit) + ']';
  }

  private IUnit getAxisUnit() {
    return Objects.isNull(unit) ? IUnit.ONE : unit;
  }
}

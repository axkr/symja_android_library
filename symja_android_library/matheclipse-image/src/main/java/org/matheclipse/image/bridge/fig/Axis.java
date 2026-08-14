// code adapted from https://github.com/datahaki/bridge
package org.matheclipse.image.bridge.fig;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.sca.Clip;
import org.matheclipse.core.tensor.sca.Clips;
import org.matheclipse.core.units.Units;

public class Axis implements Serializable {

  public enum Type {
    LINEAR, //
    LOGARITHMIC, //
  }

  private String label = "";
  /** canonical unit expression (string atoms), or null if the axis carries no unit */
  private IExpr unit = null;
  private Clip clip = null;
  private Type type = Type.LINEAR;

  public Axis() {
    // ---
  }

  public Axis(Clip clip) {
    setClip(clip);
  }

  /** @param string of axis */
  public void setLabel(String string) {
    label = string;
  }

  /** @return label of axis */
  public String getLabel() {
    return label;
  }

  public void setUnit(IExpr unit) {
    this.unit = unit;
  }

  public IExpr getUnit() {
    return unit;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public Type getType() {
    return type;
  }

  /** @return the unit of the given expression: the quantity's unit or {@code 1} for scalars */
  static IExpr unitOf(IExpr expr) {
    return expr.isQuantity() ? ((IAST) expr).arg2() : F.C1;
  }

  /** @return operator converting a quantity to the given unit (identity for non-quantities) */
  static UnaryOperator<IExpr> convertTo(IExpr unit) {
    return expr -> {
      if (expr.isQuantity() && unit != null && !unit.isOne()) {
        IAST quantity = (IAST) expr;
        IExpr magnitude = Units.convertMagnitude(quantity.arg1(), quantity.arg2(), unit,
            EvalEngine.get());
        if (magnitude.isPresent()) {
          return F.Quantity(magnitude, unit);
        }
      }
      return expr;
    };
  }

  /** @return operator extracting the magnitude of a quantity in the given unit */
  static UnaryOperator<IExpr> magnitudeIn(IExpr unit) {
    return expr -> {
      if (expr.isQuantity()) {
        IAST quantity = (IAST) expr;
        if (unit == null || unit.isOne() || quantity.arg2().equals(unit)) {
          return quantity.arg1();
        }
        IExpr magnitude = Units.convertMagnitude(quantity.arg1(), quantity.arg2(), unit,
            EvalEngine.get());
        return magnitude.isPresent() ? magnitude : quantity.arg1();
      }
      return expr;
    };
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
        IExpr clipUnit = unitOf(clip.min());
        setUnit(clipUnit.isOne() ? null : clipUnit);
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
        : Optional.of(slash(clip, convertTo(getAxisUnit())));
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
   * @return operator that maps an expression value to its magnitude in this axis' unit.
   */
  /* package */ UnaryOperator<IExpr> toReals() {
    return magnitudeIn(getAxisUnit());
  }

  /* package */ String getUnitString() {
    IExpr axisUnit = getAxisUnit();
    return axisUnit.isOne() ? "" : '[' + axisUnit.toString() + ']';
  }

  private IExpr getAxisUnit() {
    return Objects.isNull(unit) ? F.C1 : unit;
  }
}

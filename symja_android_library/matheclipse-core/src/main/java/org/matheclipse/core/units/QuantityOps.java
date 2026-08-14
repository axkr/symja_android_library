package org.matheclipse.core.units;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Arithmetic on {@code Quantity[magnitude, unit]} ASTs. All methods assume the arguments are
 * already-evaluated quantities (canonical units); they return {@code F.NIL} when an operation does
 * not apply, so callers leave the expression unevaluated.
 */
public final class QuantityOps {

  /** Sentinel for {@link #compare}: quantities with incompatible units. */
  public static final int INCOMPARABLE = Integer.MIN_VALUE;

  private QuantityOps() {}

  public static IExpr magnitude(IAST quantity) {
    return quantity.arg1();
  }

  public static IExpr unit(IAST quantity) {
    return quantity.arg2();
  }

  /**
   * Adds two quantities. The result carries the left (first) argument's unit — WMA behavior; the
   * right magnitude is converted. Returns {@code F.NIL} on incompatible units (caller reports
   * {@code Quantity::compat}).
   */
  public static IExpr plus(IAST q1, IAST q2, EvalEngine engine) {
    IExpr u1 = q1.arg2();
    IExpr u2 = q2.arg2();
    // WMA temperature rules apply even for identical units: 20 degC + 5 degC = 571.3 K
    IExpr temperatureSum = plusTemperature(q1, q2, engine);
    if (temperatureSum.isPresent()) {
      return temperatureSum;
    }
    IExpr m2 = q2.arg1();
    if (!u1.equals(u2)) {
      m2 = Units.convertMagnitude(m2, u2, u1, engine);
      if (m2.isNIL()) {
        return F.NIL;
      }
    }
    return F.Quantity(engine.evaluate(F.Plus(q1.arg1(), m2)), u1);
  }

  /**
   * WMA temperature arithmetic: absolute + absolute converts both to Kelvins and sums there;
   * absolute + difference gives an absolute temperature in the absolute argument's unit (the
   * difference converts by pure scale). Difference + difference falls through to the ordinary
   * conversion path. Returns {@code F.NIL} when not a special temperature case.
   */
  private static IExpr plusTemperature(IAST q1, IAST q2, EvalEngine engine) {
    org.matheclipse.core.units.UnitEntry e1 = Units.singleEntry(q1.arg2());
    org.matheclipse.core.units.UnitEntry e2 = Units.singleEntry(q2.arg2());
    if (e1 == null || e2 == null || (e1.temperature == null && e2.temperature == null)) {
      return F.NIL;
    }
    boolean absolute1 = e1.isAbsoluteTemperature();
    boolean absolute2 = e2.isAbsoluteTemperature();
    if (absolute1 && absolute2) {
      IExpr kelvins = F.stringx("Kelvins");
      IExpr k1 = Units.convertMagnitude(q1.arg1(), q1.arg2(), kelvins, engine);
      IExpr k2 = Units.convertMagnitude(q2.arg1(), q2.arg2(), kelvins, engine);
      if (k1.isPresent() && k2.isPresent()) {
        return F.Quantity(engine.evaluate(F.Plus(k1, k2)), kelvins);
      }
      return F.NIL;
    }
    if (absolute1 != absolute2) {
      IAST absolute = absolute1 ? q1 : q2;
      IAST difference = absolute1 ? q2 : q1;
      org.matheclipse.core.units.UnitEntry absoluteEntry = absolute1 ? e1 : e2;
      org.matheclipse.core.units.UnitEntry differenceEntry = absolute1 ? e2 : e1;
      if (!absoluteEntry.factors.equals(differenceEntry.factors)) {
        return F.NIL;
      }
      // the difference converts to the absolute unit's scale purely multiplicatively
      IExpr scaled =
          engine.evaluate(F.Divide(F.Times(differenceEntry.coefficient, difference.arg1()),
              absoluteEntry.coefficient));
      return F.Quantity(engine.evaluate(F.Plus(absolute.arg1(), scaled)), absolute.arg2());
    }
    return F.NIL;
  }

  /**
   * Multiplies two quantities: magnitudes multiply, unit expressions multiply and engine-cancel
   * (identical strings merge; a fully-canceled unit collapses to the bare magnitude).
   */
  public static IExpr times(IAST q1, IAST q2, EvalEngine engine) {
    IExpr unit = Units.timesFlat(engine, q1.arg2(), q2.arg2());
    IExpr magnitude = Units.timesFlat(engine, q1.arg1(), q2.arg1());
    if (unit.isOne()) {
      return magnitude;
    }
    if (unit.isNumber()) {
      // e.g. "Percent"/"Percent" is 1, but a numeric unit remainder folds into the magnitude
      return engine.evaluate(F.Times(magnitude, unit));
    }
    return F.Quantity(magnitude, unit);
  }

  /**
   * Absorbs a non-quantity factor into the magnitude: {@code a*Quantity(m,u) -> Quantity(a*m,u)}.
   */
  public static IExpr timesScalar(IAST quantity, IExpr scalar, EvalEngine engine) {
    if (!scalar.isOne() && isAbsoluteTemperature(quantity)) {
      // an absolute temperature has no absolute zero at its origin, so a scalar factor must not
      // be pushed into the magnitude: -Quantity(3, "DegreesCelsius") keeps the sign outside
      return F.NIL;
    }
    return F.Quantity(Units.timesFlat(engine, scalar, quantity.arg1()), quantity.arg2());
  }

  /** True if the quantity's unit is a single absolute temperature unit. */
  public static boolean isAbsoluteTemperature(IAST quantity) {
    UnitEntry entry = Units.singleEntry(quantity.arg2());
    return entry != null && entry.isAbsoluteTemperature() && entry.offset != null;
  }

  /**
   * WMA temperature subtraction: {@code T1 - T2} of two absolute temperatures is a temperature
   * <em>difference</em> expressed in the difference unit of the first temperature. Rewrites the
   * first such pair found in a {@code Plus(...)} expression; {@code F.NIL} if none applies.
   */
  public static IExpr temperatureSubtraction(IAST plus, EvalEngine engine) {
    for (int i = 1; i < plus.size(); i++) {
      IExpr positive = plus.get(i);
      if (!positive.isQuantity() || !isAbsoluteTemperature((IAST) positive)) {
        continue;
      }
      for (int j = 1; j < plus.size(); j++) {
        if (i == j) {
          continue;
        }
        IExpr negated = plus.get(j);
        if (!negated.isTimes() || !negated.isAST2() || !negated.first().isMinusOne()) {
          continue;
        }
        IExpr subtrahend = ((IAST) negated).arg2();
        if (!subtrahend.isQuantity() || !isAbsoluteTemperature((IAST) subtrahend)) {
          continue;
        }
        IExpr difference = temperatureDifference((IAST) positive, (IAST) subtrahend, engine);
        if (difference.isPresent()) {
          IASTAppendable result = F.PlusAlloc(plus.argSize());
          for (int k = 1; k < plus.size(); k++) {
            if (k == i) {
              result.append(difference);
            } else if (k != j) {
              result.append(plus.get(k));
            }
          }
          return result.argSize() == 1 ? result.arg1() : result;
        }
      }
    }
    return F.NIL;
  }

  /** {@code T1 - T2} as a quantity in the difference unit belonging to {@code T1}'s unit. */
  private static IExpr temperatureDifference(IAST minuend, IAST subtrahend, EvalEngine engine) {
    UnitEntry e1 = Units.singleEntry(minuend.arg2());
    UnitEntry e2 = Units.singleEntry(subtrahend.arg2());
    if (e1 == null || e2 == null || !e1.factors.equals(e2.factors)) {
      return F.NIL;
    }
    IExpr kelvin1 =
        F.Plus(F.Times(e1.coefficient, minuend.arg1()), e1.offset == null ? F.C0 : e1.offset);
    IExpr kelvin2 =
        F.Plus(F.Times(e2.coefficient, subtrahend.arg1()), e2.offset == null ? F.C0 : e2.offset);
    IExpr magnitude = engine.evaluate(F.Divide(F.Subtract(kelvin1, kelvin2), e1.coefficient));
    return F.Quantity(magnitude, Units.differenceUnit(e1));
  }

  /**
   * Raises a quantity to a rational power: {@code Quantity(m,u)^p -> Quantity(m^p, u^p)}. Exponent
   * {@code 0} gives {@code 1}; non-rational exponents return {@code F.NIL}.
   */
  public static IExpr power(IAST quantity, IExpr exponent, EvalEngine engine) {
    if (exponent.isZero()) {
      return F.C1;
    }
    if (!exponent.isRational()) {
      return F.NIL;
    }
    // units are positive by definition, so Sqrt("Meters"^2) may expand to "Meters"
    IExpr unit = engine.evaluate(F.PowerExpand(F.Power(quantity.arg2(), exponent)));
    IExpr magnitude = engine.evaluate(F.Power(quantity.arg1(), exponent));
    if (unit.isOne()) {
      return magnitude;
    }
    return F.Quantity(magnitude, unit);
  }

  /** Applies a magnitude-level operation, keeping the unit: Ceiling, Floor, Round, Re, Im, ... */
  public static IExpr mapMagnitude(IAST quantity, ISymbol head, EvalEngine engine) {
    return F.Quantity(engine.evaluate(F.unaryAST1(head, quantity.arg1())), quantity.arg2());
  }

  /**
   * The magnitude of {@code q2} expressed in {@code q1}'s unit, or {@code F.NIL} if incompatible.
   */
  public static IExpr magnitudeInFirstUnit(IAST q1, IAST q2, EvalEngine engine) {
    if (q1.arg2().equals(q2.arg2())) {
      return q2.arg1();
    }
    return Units.convertMagnitude(q2.arg1(), q2.arg2(), q1.arg2(), engine);
  }

  /**
   * Numeric comparison after unit conversion: negative/zero/positive like
   * {@link Comparable#compareTo}, or {@link #INCOMPARABLE} when units are incompatible or the
   * magnitudes are not comparable reals.
   */
  public static int compare(IAST q1, IAST q2, EvalEngine engine) {
    IExpr m2 = magnitudeInFirstUnit(q1, q2, engine);
    if (m2.isNIL()) {
      return INCOMPARABLE;
    }
    IExpr m1 = q1.arg1();
    if (m1.isReal() && m2.isReal()) {
      return ((IReal) m1).compareTo((IReal) m2);
    }
    if (m1.equals(m2)) {
      return 0;
    }
    IExpr difference = engine.evaluate(F.Subtract(m1, m2));
    if (difference.isReal()) {
      return ((IReal) difference).complexSign();
    }
    return INCOMPARABLE;
  }

  /** Equality after unit conversion; {@code F.NIL} when incompatible or undecidable. */
  public static IExpr equalsExpr(IAST q1, IAST q2, EvalEngine engine) {
    IExpr m2 = magnitudeInFirstUnit(q1, q2, engine);
    if (m2.isNIL()) {
      return F.NIL;
    }
    if (q1.arg1().equals(m2)) {
      return S.True;
    }
    IExpr difference = engine.evaluate(F.Subtract(q1.arg1(), m2));
    if (difference.isNumber()) {
      return difference.isZero() ? S.True : S.False;
    }
    return F.NIL;
  }

  /** The magnitude of the quantity converted to SI base units (replaces the old valueSI()). */
  public static IExpr toBaseMagnitude(IAST quantity, EvalEngine engine) {
    IExpr evaluated = Units.timesFlat(engine, quantity.arg1(), Units.expand(quantity.arg2()));
    return Units.splitUnitAtoms(evaluated)[0];
  }
}

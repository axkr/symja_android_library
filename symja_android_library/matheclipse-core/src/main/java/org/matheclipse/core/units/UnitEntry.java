package org.matheclipse.core.units;

import java.util.Collections;
import java.util.Map;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;

/**
 * Immutable definition of one canonical unit: its exact expansion into Symja's base units with the
 * semantics <code>valueInBase = coefficient * x + offset</code>.
 *
 * <p>
 * The base units (which double as the dimension basis) are Meters, Kilograms, Seconds, Amperes,
 * Kelvins, Moles, Candelas, Radians, Steradians, Bits, USDollars.
 */
public final class UnitEntry {

  /** Canonical WMA-style name, e.g. {@code "Feet"} or the synthetic {@code "Kilometers"}. */
  public final String name;

  /** Exact scale factor as an evaluated Symja expression, e.g. {@code 381/1250} or Pi/180. */
  public final IExpr coefficient;

  /** Base-unit name to rational exponent, e.g. {@code {Meters=1, Seconds=-2}}. Unmodifiable. */
  public final Map<String, IRational> factors;

  /** Affine offset in base units (absolute temperature units only), otherwise {@code null}. */
  public final IExpr offset;

  /** {@code null}, {@code "absolute"} or {@code "difference"}. */
  public final String temperature;

  /** Display abbreviation like {@code "km"}, may be {@code null}. */
  public final String abbrev;

  /** Whether SI prefixes may be applied to this unit. */
  public final boolean prefixable;

  UnitEntry(String name, IExpr coefficient, Map<String, IRational> factors, IExpr offset,
      String temperature, String abbrev, boolean prefixable) {
    this.name = name;
    this.coefficient = coefficient;
    this.factors = Collections.unmodifiableMap(factors);
    this.offset = offset;
    this.temperature = temperature;
    this.abbrev = abbrev;
    this.prefixable = prefixable;
  }

  public boolean isAbsoluteTemperature() {
    return "absolute".equals(temperature);
  }

  public boolean isTemperatureDifference() {
    return "difference".equals(temperature);
  }

  @Override
  public String toString() {
    return name + "=" + coefficient + "*" + factors + (offset != null ? "+" + offset : "");
  }
}

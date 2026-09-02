package org.matheclipse.core.units;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;

/**
 * Algebraic operations on unit expressions. A unit expression is ordinary Symja algebra over
 * {@code IStringX} atoms — {@code "Meters"}, {@code Times("Meters", Power("Seconds", -2))} — plus
 * opaque {@code IndependentUnit["name"]} factors.
 *
 * <p>
 * Conversion is substitution-based: {@link #expand(IExpr)} replaces every unit string by its exact
 * base expansion, and the evaluation engine's {@code Times}/{@code Power} canonicalization cancels
 * the base-unit string atoms. A conversion is valid exactly when the ratio of two expansions
 * evaluates to a string-free expression. Absolute temperature units take a separate affine branch
 * (registry entries carry {@code valueInBase = coefficient * x + offset}).
 */
public final class Units {

  /** Base unit name to WMA dimension name. */
  public static final Map<String, String> BASE_DIMENSIONS =
      Map.ofEntries(Map.entry("Meters", "LengthUnit"), //
          Map.entry("Kilograms", "MassUnit"), //
          Map.entry("Seconds", "TimeUnit"), //
          Map.entry("Amperes", "ElectricCurrentUnit"), //
          Map.entry("Kelvins", "TemperatureUnit"), //
          Map.entry("Moles", "AmountUnit"), //
          Map.entry("Candelas", "LuminousIntensityUnit"), //
          Map.entry("Radians", "AngleUnit"), //
          Map.entry("Steradians", "SolidAngleUnit"), //
          Map.entry("Bits", "InformationUnit"), //
          Map.entry("USDollars", "MoneyUnit"));

  /** WMA dimension name to base unit name (reverse of {@link #BASE_DIMENSIONS}). */
  public static final Map<String, String> DIMENSION_BASE_UNITS = buildDimensionBaseUnits();

  private static Map<String, String> buildDimensionBaseUnits() {
    Map<String, String> map = new TreeMap<>();
    for (Map.Entry<String, String> e : BASE_DIMENSIONS.entrySet()) {
      map.put(e.getValue(), e.getKey());
    }
    // the QuantityVariable layer distinguishes temperature differences; both reduce to Kelvins
    map.put("TemperatureDifferenceUnit", "Kelvins");
    return Collections.unmodifiableMap(map);
  }

  /**
   * The unit a simple quantity takes in each unit system, keyed by the SI base unit of its
   * dimension. The two entries per row are {@code {Metric, Imperial}}.
   *
   * <p>
   * Pinned to Mathematica: {@code UnitConvert[Quantity[3,"Meters"],"Imperial"]} is
   * {@code Quantity[1250/381,"Yards"]} - the Imperial length is the YARD, not the foot -
   * {@code UnitConvert[Quantity[1,"Kilograms"],"Imperial"]} is
   * {@code Quantity[100000000/45359237,"Pounds"]}, and
   * {@code UnitConvert[Quantity[300,"Kelvins"],"Imperial"]} is
   * {@code Quantity[8033/100,"DegreesFahrenheit"]} - the affine Fahrenheit, not Rankine.
   *
   * <p>
   * A dimension absent from this table has no system preference, and a quantity of that dimension
   * is returned unchanged. The Metric temperature entry is the one row NOT verified.
   */
  private static final Map<String, String[]> SYSTEM_UNITS =
      Map.ofEntries(Map.entry("Meters", new String[] {"Meters", "Yards"}), //
          Map.entry("Kilograms", new String[] {"Kilograms", "Pounds"}), //
          Map.entry("Kelvins", new String[] {"DegreesCelsius", "DegreesFahrenheit"}));

  /**
   * The unit that {@code canonicalUnit} takes in {@code system}.
   *
   * <p>
   * Only a SIMPLE quantity - one base dimension, to the first power - has a system unit. A
   * compound unit is left alone, which is what Mathematica does:
   * {@code UnitConvert[Quantity[3,"Meters"]/Quantity[1,"Seconds"], "Imperial"]} comes back as
   * {@code Quantity[3,"Meters"/"Seconds"]}, unconverted. So this is a lookup of a preferred unit
   * per quantity kind, NOT a substitution of base units inside a monomial.
   *
   * @param system {@code "Metric"} or {@code "Imperial"}
   * @return the target unit, or {@link F#NIL} when that dimension has no unit in that system
   */
  public static IExpr systemUnit(IExpr canonicalUnit, String system) {
    Map<String, IRational> dims = dimensions(canonicalUnit);
    if (dims == null || dims.size() != 1) {
      return F.NIL;
    }
    Map.Entry<String, IRational> dimension = dims.entrySet().iterator().next();
    if (!dimension.getValue().isOne()) {
      return F.NIL;
    }
    String[] units = SYSTEM_UNITS.get(dimension.getKey());
    if (units == null) {
      return F.NIL;
    }
    return F.stringx("Imperial".equals(system) ? units[1] : units[0]);
  }

  private Units() {}

  // ------------------------------------------------- physical quantities / dimension specs

  /**
   * The canonical unit expression of a physical-quantity specification: a name string
   * ({@code "ElectricPotential"}), a product/power of such names ({@code "Mass"*"Distance"}), an
   * {@code IndependentPhysicalQuantity["name"]}, or an explicit dimension list
   * ({@code {{"LengthUnit",2},{"MassUnit",-1}}}). Returns {@code F.NIL} if unrecognized.
   */
  public static IExpr physicalQuantityUnit(IExpr spec) {
    if (spec.isString()) {
      IExpr unit = PhysicalQuantities.get().canonicalUnit(spec.toString());
      return unit == null ? F.NIL : unit;
    }
    if (spec.isAST(S.IndependentPhysicalQuantity, 2) && spec.first().isString()) {
      return F.IndependentUnit(spec.first());
    }
    if (spec.isPower()) {
      IAST power = (IAST) spec;
      if (!power.exponent().isRational()) {
        return F.NIL;
      }
      IExpr base = physicalQuantityUnit(power.base());
      return base.isNIL() ? F.NIL : F.Power(base, power.exponent());
    }
    if (spec.isTimes()) {
      IAST times = (IAST) spec;
      IASTAppendable product = F.TimesAlloc(times.argSize());
      for (int i = 1; i < times.size(); i++) {
        IExpr factor = physicalQuantityUnit(times.get(i));
        if (factor.isNIL()) {
          return F.NIL;
        }
        product.append(factor);
      }
      return product;
    }
    if (isDimensionList(spec)) {
      return dimensionListUnit((IAST) spec);
    }
    return F.NIL;
  }

  /** True for an explicit dimension list like {@code {{"LengthUnit",1},{"TimeUnit",-1}}}. */
  private static boolean isDimensionList(IExpr spec) {
    if (!spec.isList() || spec.isEmptyList()) {
      return false;
    }
    IAST list = (IAST) spec;
    for (int i = 1; i < list.size(); i++) {
      IExpr entry = list.get(i);
      if (!entry.isList2() || !entry.first().isString() || !entry.second().isRational()) {
        return false;
      }
      String name = entry.first().toString();
      if (!DIMENSION_BASE_UNITS.containsKey(name)
          && !entry.first().isAST(S.IndependentUnitDimension, 2)) {
        return false;
      }
    }
    return true;
  }

  /** Builds the base-unit monomial of an explicit dimension list. */
  private static IExpr dimensionListUnit(IAST list) {
    IASTAppendable product = F.TimesAlloc(list.argSize());
    for (int i = 1; i < list.size(); i++) {
      IAST entry = (IAST) list.get(i);
      String base = DIMENSION_BASE_UNITS.get(entry.first().toString());
      if (base == null) {
        return F.NIL;
      }
      IExpr exponent = entry.second();
      product.append(exponent.isOne() ? F.stringx(base) : F.Power(F.stringx(base), exponent));
    }
    return product.argSize() == 1 ? product.arg1() : product;
  }

  /**
   * The base-unit dimension map of a dimension specification, as accepted by the two-argument forms
   * of {@code KnownUnitQ}/{@code QuantityQ}: an explicit dimension list, a physical quantity name
   * (or product/power of names), a {@code Quantity}, or a {@code QuantityVariable}. Returns
   * {@code null} if the specification is not recognized.
   */
  public static Map<String, IRational> dimensionSpec(IExpr spec) {
    if (spec.isQuantity()) {
      return dimensions(((IAST) spec).arg2());
    }
    if (spec.isAST(S.QuantityVariable, 2) || spec.isAST(S.QuantityVariable, 3)) {
      IExpr unit = quantityVariableUnit((IAST) spec);
      return unit.isNIL() ? null : dimensions(unit);
    }
    IExpr unit = physicalQuantityUnit(spec);
    if (unit.isPresent()) {
      return dimensions(unit);
    }
    // a bare dimension name like "LengthUnit"
    if (spec.isString()) {
      String base = DIMENSION_BASE_UNITS.get(spec.toString());
      if (base != null) {
        return dimensions(F.stringx(base));
      }
    }
    return null;
  }

  /**
   * The canonical unit of a {@code QuantityVariable[var, pq]} or {@code QuantityVariable[pq]}
   * expression (also handles products, powers and derivatives of quantity variables).
   */
  public static IExpr quantityVariableUnit(IExpr expr) {
    if (expr.isAST(S.QuantityVariable, 2)) {
      return physicalQuantityUnit(expr.first());
    }
    if (expr.isAST(S.QuantityVariable, 3)) {
      return physicalQuantityUnit(((IAST) expr).arg2());
    }
    if (expr.isPower()) {
      IAST power = (IAST) expr;
      if (!power.exponent().isRational()) {
        return F.NIL;
      }
      IExpr base = quantityVariableUnit(power.base());
      return base.isNIL() ? F.NIL : F.Power(base, power.exponent());
    }
    if (expr.isTimes()) {
      IAST times = (IAST) expr;
      IASTAppendable product = F.TimesAlloc(times.argSize());
      for (int i = 1; i < times.size(); i++) {
        IExpr factor = quantityVariableUnit(times.get(i));
        if (factor.isNIL()) {
          return F.NIL;
        }
        product.append(factor);
      }
      return product;
    }
    // Derivative(n)[qv][arg] - the unit divides by the argument's unit per order
    if (expr.isAST1() && expr.head().isAST1() && expr.head().head().isAST(S.Derivative, 2)) {
      IExpr order = expr.head().head().first();
      if (order.isRational()) {
        IExpr functionUnit = quantityVariableUnit(expr.head().first());
        IExpr argumentUnit = quantityVariableUnit(expr.first());
        if (functionUnit.isPresent() && argumentUnit.isPresent()) {
          return F.Times(functionUnit, F.Power(argumentUnit, order.negate()));
        }
      }
    }
    return F.NIL;
  }

  /**
   * Validates and canonicalizes a unit expression: every string atom is replaced by its canonical
   * registry name; {@code Times}/{@code Power} (rational exponents) structures and
   * {@code IndependentUnit["name"]} are accepted. Returns {@code F.NIL} for invalid units; returns
   * the identical instance when nothing changed (fixed-point detection).
   */
  public static IExpr normalize(IExpr unitExpr) {
    if (unitExpr.isString()) {
      String name = unitExpr.toString();
      UnitEntry entry = UnitRegistry.get().resolve(name);
      if (entry == null) {
        // legacy compound unit strings like "m/s^2", "Hz^(-2)*N*m^(-1)", "kW*h"
        return parseCompoundUnitString(name);
      }
      return entry.name.equals(name) ? unitExpr : F.stringx(entry.name);
    }
    if (unitExpr.isPower()) {
      IAST power = (IAST) unitExpr;
      IExpr exponent = power.exponent();
      if (!exponent.isRational() || exponent.isZero()) {
        return F.NIL;
      }
      IExpr base = normalize(power.base());
      if (base.isNIL()) {
        return F.NIL;
      }
      return base == power.base() ? unitExpr : F.Power(base, exponent);
    }
    if (unitExpr.isTimes()) {
      IAST times = (IAST) unitExpr;
      IASTAppendable result = F.NIL;
      for (int i = 1; i < times.size(); i++) {
        IExpr normalized = normalize(times.get(i));
        if (normalized.isNIL()) {
          return F.NIL;
        }
        if (normalized != times.get(i) && result.isNIL()) {
          result = times.copyAppendable();
        }
        if (result.isPresent()) {
          result.set(i, normalized);
        }
      }
      return result.isPresent() ? result : unitExpr;
    }
    if (unitExpr.isAST(S.IndependentUnit, 2) && unitExpr.first().isString()) {
      return unitExpr;
    }
    if (unitExpr.isAST(S.MixedUnit, 2) && unitExpr.first().isList()) {
      IAST list = (IAST) unitExpr.first();
      if (list.argSize() < 2) {
        return F.NIL;
      }
      IASTAppendable normalized = F.NIL;
      Map<String, IRational> commonDimensions = null;
      for (int i = 1; i < list.size(); i++) {
        IExpr component = normalize(list.get(i));
        if (component.isNIL() || component.isAST(S.MixedUnit, 2)) {
          return F.NIL;
        }
        Map<String, IRational> dims = dimensions(component);
        if (dims == null) {
          return F.NIL;
        }
        if (commonDimensions == null) {
          commonDimensions = dims;
        } else if (!commonDimensions.equals(dims)) {
          return F.NIL; // all components of a MixedUnit must share the dimension
        }
        if (component != list.get(i) && normalized.isNIL()) {
          normalized = list.copyAppendable();
        }
        if (normalized.isPresent()) {
          normalized.set(i, component);
        }
      }
      return normalized.isPresent() ? F.MixedUnit(normalized) : unitExpr;
    }
    return F.NIL;
  }

  /** True if the expression is a valid (canonicalizable) unit expression. */
  public static boolean isKnownUnit(IExpr unitExpr) {
    return normalize(unitExpr).isPresent();
  }

  /**
   * Parses a legacy compound unit string ({@code "m/s^2"}, {@code "Hz^(-2)*N*m^(-1)"},
   * {@code "m *rad"}) into a canonical unit expression; {@code F.NIL} if any part is unknown.
   * Grammar: {@code name ('^' exponent)? (('*'|'/'|' ') ...)*} with integer or {@code (p/q)}
   * exponents.
   */
  private static IExpr parseCompoundUnitString(String text) {
    if (text.indexOf('*') < 0 && text.indexOf('/') < 0 && text.indexOf('^') < 0
        && text.indexOf(' ') < 0) {
      return F.NIL;
    }
    IASTAppendable product = F.TimesAlloc(4);
    int i = 0;
    int n = text.length();
    boolean divide = false;
    while (i < n) {
      char c = text.charAt(i);
      if (Character.isWhitespace(c) || c == '*') {
        i++;
        continue;
      }
      if (c == '/') {
        divide = true;
        i++;
        continue;
      }
      // unit name: letters (and inner digits are not expected in short names)
      int start = i;
      while (i < n && (Character.isLetter(text.charAt(i)) || text.charAt(i) == '_'
          || text.charAt(i) == '%' || text.charAt(i) == '$')) {
        i++;
      }
      if (start == i) {
        return F.NIL; // unexpected character
      }
      UnitEntry entry = UnitRegistry.get().resolve(text.substring(start, i));
      if (entry == null) {
        return F.NIL;
      }
      long numerator = 1;
      long denominator = 1;
      boolean negative = false;
      boolean hasExponent = false;
      if (i < n && text.charAt(i) == '^') {
        hasExponent = true;
        i++;
        boolean parenthesized = i < n && text.charAt(i) == '(';
        if (parenthesized) {
          i++;
        }
        if (i < n && (text.charAt(i) == '-' || text.charAt(i) == '+')) {
          negative = text.charAt(i) == '-';
          i++;
        }
        int digitStart = i;
        while (i < n && Character.isDigit(text.charAt(i))) {
          i++;
        }
        if (digitStart == i) {
          return F.NIL;
        }
        numerator = Long.parseLong(text.substring(digitStart, i));
        if (i < n && text.charAt(i) == '/') {
          i++;
          digitStart = i;
          while (i < n && Character.isDigit(text.charAt(i))) {
            i++;
          }
          if (digitStart == i) {
            return F.NIL;
          }
          denominator = Long.parseLong(text.substring(digitStart, i));
        }
        if (parenthesized) {
          if (i >= n || text.charAt(i) != ')') {
            return F.NIL;
          }
          i++;
        }
      }
      if (negative != divide) { // exactly one of the two negations applies
        numerator = -numerator;
      }
      divide = false;
      IExpr factor = F.stringx(entry.name);
      if (hasExponent || numerator != 1 || denominator != 1) {
        IExpr exponent = denominator == 1 ? F.ZZ(numerator) : F.QQ(numerator, denominator);
        if (!exponent.isOne()) {
          factor = F.Power(factor, exponent);
        }
      }
      product.append(factor);
    }
    if (product.argSize() == 0) {
      return F.NIL;
    }
    return product.argSize() == 1 ? product.arg1() : product;
  }

  /** Registry entry when the unit is a single string atom, else {@code null}. */
  public static UnitEntry singleEntry(IExpr unit) {
    if (unit.isString()) {
      return UnitRegistry.get().resolve(unit.toString());
    }
    return null;
  }

  /**
   * Recursively replaces every unit string by {@code coefficient * PROD baseString^exp}.
   * {@code IndependentUnit[...]} factors stay opaque. The result is intentionally unevaluated.
   */
  public static IExpr expand(IExpr canonicalUnit) {
    if (canonicalUnit.isString()) {
      UnitEntry entry = UnitRegistry.get().resolve(canonicalUnit.toString());
      if (entry == null) {
        return canonicalUnit; // unknown strings survive and poison the ratio - desired
      }
      IASTAppendable product = F.TimesAlloc(entry.factors.size() + 1);
      product.append(entry.coefficient);
      for (Map.Entry<String, IRational> factor : entry.factors.entrySet()) {
        product.append(factor.getValue().isOne() ? F.stringx(factor.getKey())
            : F.Power(F.stringx(factor.getKey()), factor.getValue()));
      }
      return product;
    }
    if (canonicalUnit.isPower()) {
      IAST power = (IAST) canonicalUnit;
      return F.Power(expand(power.base()), power.exponent());
    }
    if (canonicalUnit.isTimes()) {
      IAST times = (IAST) canonicalUnit;
      IASTAppendable product = F.TimesAlloc(times.argSize());
      for (int i = 1; i < times.size(); i++) {
        product.append(expand(times.get(i)));
      }
      return product;
    }
    return canonicalUnit; // IndependentUnit[...] and anything opaque
  }

  /** True if the (evaluated) expression still contains unit atoms. */
  public static boolean containsUnitAtoms(IExpr expr) {
    return !expr.isFree(x -> x.isString() || x.isAST(S.IndependentUnit, 2), true);
  }

  /**
   * Multiplies the factors after flattening nested {@code Times} manually, then evaluates once.
   * Necessary because evaluating {@code Times(a, Times(b, c))} does not reliably merge common bases
   * between {@code a} and the nested product ({@code Pi*(180/Pi)} stayed unmerged).
   */
  public static IExpr timesFlat(EvalEngine engine, IExpr... factors) {
    IASTAppendable times = F.TimesAlloc(factors.length + 4);
    for (IExpr factor : factors) {
      appendFlattened(times, factor);
    }
    if (times.argSize() == 0) {
      return F.C1;
    }
    if (times.argSize() == 1) {
      return engine.evaluate(times.arg1());
    }
    return engine.evaluate(times);
  }

  private static void appendFlattened(IASTAppendable times, IExpr factor) {
    if (factor.isTimes()) {
      IAST ast = (IAST) factor;
      for (int i = 1; i < ast.size(); i++) {
        appendFlattened(times, ast.get(i));
      }
    } else {
      times.append(factor);
    }
  }

  /**
   * Expands and evaluates the unit, splitting the result into {@code [coefficient, baseMonomial]}
   * where the monomial is a product of base-unit strings (or {@code 1} for dimensionless units).
   */
  public static IExpr[] toBaseSplit(IExpr canonicalUnit, EvalEngine engine) {
    return splitUnitAtoms(engine.evaluate(expand(canonicalUnit)));
  }

  private static void classify(IExpr factor, IASTAppendable coefficient, IASTAppendable monomial) {
    IExpr base = factor.isPower() ? factor.base() : factor;
    if (base.isString() || base.isAST(S.IndependentUnit, 2)) {
      monomial.append(factor);
    } else {
      coefficient.append(factor);
    }
  }

  /**
   * The dimension-exponent map of a canonical unit expression, keyed by base unit name (plus
   * {@code IndependentUnit[...]} full-form strings for independent units); {@code null} if the
   * expression is not a valid unit.
   */
  public static Map<String, IRational> dimensions(IExpr canonicalUnit) {
    TreeMap<String, IRational> map = new TreeMap<>();
    if (!accumulateDimensions(canonicalUnit, F.C1, map)) {
      return null;
    }
    map.values().removeIf(IRational::isZero);
    return map;
  }

  private static boolean accumulateDimensions(IExpr unit, IRational multiplier,
      Map<String, IRational> map) {
    if (unit.isString()) {
      UnitEntry entry = UnitRegistry.get().resolve(unit.toString());
      if (entry == null) {
        return false;
      }
      for (Map.Entry<String, IRational> factor : entry.factors.entrySet()) {
        map.merge(factor.getKey(), factor.getValue().multiply(multiplier), IRational::add);
      }
      return true;
    }
    if (unit.isPower()) {
      IAST power = (IAST) unit;
      if (!power.exponent().isRational()) {
        return false;
      }
      return accumulateDimensions(power.base(), multiplier.multiply((IRational) power.exponent()),
          map);
    }
    if (unit.isTimes()) {
      IAST times = (IAST) unit;
      for (int i = 1; i < times.size(); i++) {
        if (!accumulateDimensions(times.get(i), multiplier, map)) {
          return false;
        }
      }
      return true;
    }
    if (unit.isAST(S.IndependentUnit, 2) && unit.first().isString()) {
      map.merge(unit.toString(), multiplier, IRational::add);
      return true;
    }
    if (unit.isAST(S.MixedUnit, 2) && unit.first().isList()
        && ((IAST) unit.first()).argSize() > 0) {
      // all components share the dimension; use the first
      return accumulateDimensions(((IAST) unit.first()).arg1(), multiplier, map);
    }
    return false;
  }

  /** True if both unit expressions have identical dimension-exponent maps. */
  public static boolean compatibleUnits(IExpr unit1, IExpr unit2) {
    Map<String, IRational> d1 = dimensions(unit1);
    Map<String, IRational> d2 = dimensions(unit2);
    return d1 != null && d1.equals(d2);
  }

  /**
   * Converts a magnitude from one canonical unit to another. Returns {@code F.NIL} when the units
   * are incompatible (or on the forbidden temperature-difference to absolute-temperature
   * direction). Handles affine absolute-temperature conversion exactly.
   */
  public static IExpr convertMagnitude(IExpr magnitude, IExpr fromUnit, IExpr toUnit,
      EvalEngine engine) {
    if (fromUnit.equals(toUnit)) {
      return magnitude;
    }
    UnitEntry from = singleEntry(fromUnit);
    UnitEntry to = singleEntry(toUnit);
    if (from != null && to != null && (from.temperature != null || to.temperature != null)) {
      if (!from.factors.equals(to.factors)) {
        return F.NIL;
      }
      boolean fromAbsolute = from.isAbsoluteTemperature();
      boolean toAbsolute = to.isAbsoluteTemperature();
      if (fromAbsolute || toAbsolute) {
        if (!fromAbsolute && to.offset != null) {
          // temperature difference -> offset-bearing absolute temperature is not permitted
          // (difference -> Kelvins/Rankine is fine: their zero is absolute zero)
          return F.NIL;
        }
        IExpr fromOffset = fromAbsolute && from.offset != null ? from.offset : F.C0;
        IExpr inBase = F.Plus(F.Times(from.coefficient, magnitude), fromOffset);
        IExpr toOffset = toAbsolute && to.offset != null ? to.offset : F.C0;
        return engine.evaluate(F.Divide(F.Subtract(inBase, toOffset), to.coefficient));
      }
      // difference <-> difference is purely multiplicative; fall through
    }
    IExpr ratio = engine.evaluate(F.Divide(expand(fromUnit), expand(toUnit)));
    if (containsUnitAtoms(ratio)) {
      return F.NIL;
    }
    return timesFlat(engine, magnitude, ratio);
  }

  /**
   * Treats the named dimensions as unity: expands the unit, folds the contributions of all base
   * units belonging to those dimensions into the magnitude and strikes them from the unit. Returns
   * {@code [newMagnitude, newUnit]}, or {@code null} if nothing was stripped.
   */
  public static IExpr[] stripDimensions(IExpr magnitude, IExpr unit, IAST dimensionNames,
      EvalEngine engine) {
    java.util.Set<String> strip = new java.util.HashSet<>();
    for (int i = 1; i < dimensionNames.size(); i++) {
      if (!dimensionNames.get(i).isString()) {
        continue;
      }
      String base = DIMENSION_BASE_UNITS.get(dimensionNames.get(i).toString());
      if (base != null) {
        strip.add(base);
      }
    }
    if (strip.isEmpty()) {
      return null;
    }
    IExpr evaluated = timesFlat(engine, magnitude, expand(unit));
    IASTAppendable coefficient = F.TimesAlloc(4);
    IASTAppendable monomial = F.TimesAlloc(4);
    IASTAppendable factors = F.TimesAlloc(4);
    if (evaluated.isTimes()) {
      IAST times = (IAST) evaluated;
      for (int i = 1; i < times.size(); i++) {
        factors.append(times.get(i));
      }
    } else {
      factors.append(evaluated);
    }
    boolean stripped = false;
    for (int i = 1; i < factors.size(); i++) {
      IExpr factor = factors.get(i);
      IExpr base = factor.isPower() ? factor.base() : factor;
      if (base.isString() && strip.contains(base.toString())) {
        stripped = true; // dimension treated as unity: contributes the factor 1
        continue;
      }
      if (base.isString() || base.isAST(S.IndependentUnit, 2)) {
        monomial.append(factor);
      } else {
        coefficient.append(factor);
      }
    }
    if (!stripped) {
      return null;
    }
    IExpr newMagnitude = coefficient.argSize() == 0 ? F.C1
        : engine.evaluate(coefficient.argSize() == 1 ? coefficient.arg1() : coefficient);
    IExpr newUnit =
        monomial.argSize() == 0 ? F.C1 : monomial.argSize() == 1 ? monomial.arg1() : monomial;
    return new IExpr[] {newMagnitude, newUnit};
  }

  /**
   * The temperature-difference unit belonging to an absolute temperature unit
   * ({@code "DegreesCelsius"} to {@code "DegreesCelsiusDifference"}); Kelvins and Rankine are their
   * own difference units.
   */
  public static IExpr differenceUnit(UnitEntry absolute) {
    UnitEntry difference = UnitRegistry.get().resolve(absolute.name + "Difference");
    if (difference != null && difference.isTemperatureDifference()) {
      return F.stringx(difference.name);
    }
    return F.stringx(absolute.name);
  }

  /**
   * The total magnitude of {@code Quantity(MixedMagnitude({...}), MixedUnit({...}))} expressed in
   * {@code targetUnit}, or {@code F.NIL} for invalid/incompatible input.
   */
  public static IExpr mixedTotalIn(IAST mixedMagnitude, IAST mixedUnit, IExpr targetUnit,
      EvalEngine engine) {
    if (!mixedMagnitude.isAST(S.MixedMagnitude, 2) || !mixedMagnitude.first().isList()
        || !mixedUnit.isAST(S.MixedUnit, 2) || !mixedUnit.first().isList()) {
      return F.NIL;
    }
    IAST magnitudes = (IAST) mixedMagnitude.first();
    IAST units = (IAST) mixedUnit.first();
    if (magnitudes.argSize() != units.argSize()) {
      return F.NIL;
    }
    IASTAppendable sum = F.PlusAlloc(magnitudes.argSize());
    for (int i = 1; i < magnitudes.size(); i++) {
      IExpr converted = convertMagnitude(magnitudes.get(i), units.get(i), targetUnit, engine);
      if (converted.isNIL()) {
        return F.NIL;
      }
      sum.append(converted);
    }
    return engine.evaluate(sum);
  }

  /**
   * Splits {@code Quantity(magnitude, unit)} into the given mixed-unit components: the components
   * are used largest-first with integer parts, the last component carries the exact remainder -
   * {@code UnitConvert(Quantity(50000,"Seconds"), MixedUnit({"Hours","Minutes","Seconds"}))} gives
   * {@code 13 h, 53 min, 20 s}.
   */
  public static IExpr toMixedQuantity(IExpr magnitude, IExpr unit, IAST mixedUnit,
      EvalEngine engine) {
    if (!mixedUnit.first().isList()) {
      return F.NIL;
    }
    IAST components = (IAST) mixedUnit.first();
    int n = components.argSize();
    if (n < 2) {
      return F.NIL;
    }
    // order components largest-first by their base scale factor
    IExpr[] ordered = new IExpr[n];
    double[] scales = new double[n];
    for (int i = 0; i < n; i++) {
      ordered[i] = components.get(i + 1);
      IExpr[] split = toBaseSplit(ordered[i], engine);
      try {
        scales[i] = engine.evalDouble(split[0]);
      } catch (RuntimeException e) {
        return F.NIL;
      }
    }
    for (int i = 0; i < n; i++) { // simple selection sort, n is tiny
      for (int j = i + 1; j < n; j++) {
        if (scales[j] > scales[i]) {
          double s = scales[i];
          scales[i] = scales[j];
          scales[j] = s;
          IExpr t = ordered[i];
          ordered[i] = ordered[j];
          ordered[j] = t;
        }
      }
    }
    IASTAppendable magnitudes = F.ListAlloc(n);
    IASTAppendable units = F.ListAlloc(n);
    IExpr remainingMagnitude = magnitude;
    IExpr remainingUnit = unit;
    for (int i = 0; i < n; i++) {
      IExpr converted = convertMagnitude(remainingMagnitude, remainingUnit, ordered[i], engine);
      if (converted.isNIL()) {
        return F.NIL;
      }
      if (i == n - 1) {
        magnitudes.append(converted);
      } else {
        IExpr whole = engine.evaluate(F.Floor(converted));
        magnitudes.append(whole);
        remainingMagnitude = engine.evaluate(F.Subtract(converted, whole));
        remainingUnit = ordered[i];
      }
      units.append(ordered[i]);
    }
    return F.Quantity(F.MixedMagnitude(magnitudes), F.MixedUnit(units));
  }

  // ------------------------------------------------------------------ display

  /** Long-form singulars that plain "drop the trailing s" would get wrong. */
  private static final Map<String, String> IRREGULAR_SINGULARS = Map.ofEntries(
      Map.entry("feet", "foot"), Map.entry("inches", "inch"), Map.entry("hertz", "hertz"),
      Map.entry("siemens", "siemens"), Map.entry("lux", "lux"), Map.entry("henries", "henry"),
      Map.entry("gauss", "gauss"), Map.entry("degreescelsius", "degree Celsius"),
      Map.entry("degreesfahrenheit", "degree Fahrenheit"));

  /**
   * Renders a unit expression for {@code QuantityForm}. {@code form} is {@code "Abbreviation"}
   * ({@code m/s^2}), {@code "LongForm"} ({@code meters per second squared}) or
   * {@code "SingularForm"} (the long form in the singular).
   */
  public static String renderUnit(IExpr unit, String form) {
    boolean longForm = !"Abbreviation".equals(form);
    boolean singular = "SingularForm".equals(form);
    IASTAppendable numerator = F.ListAlloc(4);
    IASTAppendable denominator = F.ListAlloc(4);
    if (!collectFactors(unit, F.C1, numerator, denominator)) {
      return unit.toString();
    }
    StringBuilder b = new StringBuilder();
    if (numerator.argSize() == 0) {
      b.append('1');
    } else {
      appendFactors(b, numerator, longForm, singular, false);
    }
    if (denominator.argSize() > 0) {
      b.append(longForm ? " per " : "/");
      // "per second", never "per seconds"
      appendFactors(b, denominator, longForm, longForm, true);
    }
    return b.toString();
  }

  /** Splits a unit expression into {name, exponent} pairs with positive/negative exponents. */
  private static boolean collectFactors(IExpr unit, IRational multiplier, IASTAppendable numerator,
      IASTAppendable denominator) {
    if (unit.isString() || unit.isAST(S.IndependentUnit, 2)) {
      (multiplier.isNegative() ? denominator : numerator).append(F.list(unit, multiplier.abs()));
      return true;
    }
    if (unit.isPower() && unit.exponent().isRational()) {
      return collectFactors(unit.base(), multiplier.multiply((IRational) unit.exponent()),
          numerator, denominator);
    }
    if (unit.isTimes()) {
      IAST times = (IAST) unit;
      for (int i = 1; i < times.size(); i++) {
        if (!collectFactors(times.get(i), multiplier, numerator, denominator)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  private static void appendFactors(StringBuilder b, IAST factors, boolean longForm,
      boolean singular, boolean denominatorSide) {
    for (int i = 1; i < factors.size(); i++) {
      if (i > 1) {
        b.append(longForm ? " " : "*");
      }
      IAST pair = (IAST) factors.get(i);
      b.append(unitName(pair.arg1(), longForm, singular));
      IExpr exponent = pair.arg2();
      if (!exponent.isOne()) {
        if (longForm && exponent.equals(F.C2)) {
          b.append(" squared");
        } else if (longForm && exponent.equals(F.C3)) {
          b.append(" cubed");
        } else {
          b.append('^').append(exponent.toString());
        }
      }
    }
  }

  /** The display name of a single unit atom. */
  private static String unitName(IExpr atom, boolean longForm, boolean singular) {
    if (atom.isAST(S.IndependentUnit, 2)) {
      return atom.first().toString();
    }
    String name = atom.toString();
    UnitEntry entry = UnitRegistry.get().resolve(name);
    if (!longForm) {
      return entry != null && entry.abbrev != null ? entry.abbrev : name;
    }
    String canonical = entry != null ? entry.name : name;
    String lower = canonical.toLowerCase(java.util.Locale.US);
    if (!singular) {
      return lower;
    }
    String irregular = IRREGULAR_SINGULARS.get(lower);
    if (irregular != null) {
      return irregular;
    }
    return lower.endsWith("s") && !lower.endsWith("ss") ? lower.substring(0, lower.length() - 1)
        : lower;
  }

  /**
   * Ordered preference list for {@link #simplifyUnit(IExpr)}: the first named unit whose dimension
   * vector matches wins. Base units come first so {@code "Newtons"*"Seconds"^2/"Meters"} collapses
   * to {@code "Kilograms"}.
   */
  private static final String[] PREFERRED_UNITS = {"Meters", "Kilograms", "Seconds", "Amperes",
      "Kelvins", "Moles", "Candelas", "Radians", "Steradians", "Bits", "USDollars", "Newtons",
      "Joules", "Watts", "Pascals", "Hertz", "Coulombs", "Volts", "Ohms", "Siemens", "Farads",
      "Henries", "Teslas", "Webers", "Lumens", "Lux", "Katals"};

  private static final class PreferredIndex {
    private static final Map<Map<String, IRational>, String> INSTANCE = build();

    private static Map<Map<String, IRational>, String> build() {
      Map<Map<String, IRational>, String> index = new java.util.HashMap<>();
      for (String name : PREFERRED_UNITS) {
        Map<String, IRational> dims = dimensions(F.stringx(name));
        if (dims != null) {
          index.putIfAbsent(dims, name);
        }
      }
      return index;
    }
  }

  /**
   * The preferred named unit for the dimension vector of the given canonical unit expression
   * ({@code "Joules"/"Seconds"} has the dimensions of {@code "Watts"}), or {@code null} when no
   * named unit matches.
   */
  public static String simplifyUnit(IExpr canonicalUnit) {
    Map<String, IRational> dims = dimensions(canonicalUnit);
    if (dims == null || dims.isEmpty()) {
      return null;
    }
    return PreferredIndex.INSTANCE.get(dims);
  }

  /**
   * Converts {@code Quantity(magnitude, unit)} to base units: {@code Quantity[newMag, monomial]},
   * or a {@code "PureUnities"} quantity for dimensionless units. The magnitude is multiplied in the
   * same evaluation as the expansion so common symbolic factors merge.
   */
  public static IExpr toBaseQuantity(IExpr magnitude, IExpr canonicalUnit, EvalEngine engine) {
    IExpr evaluated = timesFlat(engine, magnitude, expand(canonicalUnit));
    IExpr[] split = splitUnitAtoms(evaluated);
    if (split[1].isOne()) {
      return F.Quantity(split[0], F.stringx("PureUnities"));
    }
    return F.Quantity(split[0], split[1]);
  }

  /** Splits an evaluated product into {@code [nonUnitFactors, unitMonomial]}. */
  public static IExpr[] splitUnitAtoms(IExpr evaluated) {
    IASTAppendable coefficient = F.TimesAlloc(4);
    IASTAppendable monomial = F.TimesAlloc(4);
    if (evaluated.isTimes()) {
      IAST times = (IAST) evaluated;
      for (int i = 1; i < times.size(); i++) {
        classify(times.get(i), coefficient, monomial);
      }
    } else {
      classify(evaluated, coefficient, monomial);
    }
    IExpr coeff = coefficient.argSize() == 0 ? F.C1
        : coefficient.argSize() == 1 ? coefficient.arg1() : coefficient;
    IExpr mono =
        monomial.argSize() == 0 ? F.C1 : monomial.argSize() == 1 ? monomial.arg1() : monomial;
    return new IExpr[] {coeff, mono};
  }
}

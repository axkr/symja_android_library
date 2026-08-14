package org.matheclipse.core.units;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The physical-quantity table (loaded from <code>/units/physical-quantities.json</code>): maps
 * names like {@code "ElectricPotential"} to their canonical unit expression ({@code "Volts"}). Used
 * by the {@code QuantityVariable} family and by the two-argument forms of
 * {@code KnownUnitQ}/{@code QuantityQ}.
 */
public final class PhysicalQuantities {

  private static final class Holder {
    private static final PhysicalQuantities INSTANCE = load();
  }

  public static PhysicalQuantities get() {
    return Holder.INSTANCE;
  }

  /** canonical physical quantity name to canonical unit expression */
  private final Map<String, IExpr> canonicalUnits;
  /** lower-cased name to canonical name */
  private final Map<String, String> namesLower;

  private PhysicalQuantities(Map<String, IExpr> canonicalUnits, Map<String, String> namesLower) {
    this.canonicalUnits = Collections.unmodifiableMap(canonicalUnits);
    this.namesLower = Collections.unmodifiableMap(namesLower);
  }

  /** The canonical physical quantity name for any accepted spelling, or {@code null}. */
  public String canonicalName(String name) {
    if (name == null || name.isEmpty()) {
      return null;
    }
    if (canonicalUnits.containsKey(name)) {
      return name;
    }
    return namesLower.get(name.toLowerCase(Locale.US));
  }

  /** The canonical unit expression of the named physical quantity, or {@code null}. */
  public IExpr canonicalUnit(String name) {
    String canonical = canonicalName(name);
    return canonical == null ? null : canonicalUnits.get(canonical);
  }

  public Map<String, IExpr> canonicalUnits() {
    return canonicalUnits;
  }

  @SuppressWarnings("unchecked")
  private static PhysicalQuantities load() {
    String json;
    try (InputStream is =
        PhysicalQuantities.class.getResourceAsStream("/units/physical-quantities.json")) {
      if (is == null) {
        throw new IllegalStateException(
            "resource /units/physical-quantities.json not found on classpath");
      }
      json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new IllegalStateException("cannot read /units/physical-quantities.json", e);
    }
    Map<String, Object> root = JsonMini.parseObject(json);
    Map<String, Object> quantities = (Map<String, Object>) root.get("quantities");
    Map<String, IExpr> canonicalUnits = new LinkedHashMap<>();
    Map<String, String> namesLower = new LinkedHashMap<>();
    for (Map.Entry<String, Object> e : quantities.entrySet()) {
      IExpr unit =
          Units.normalize(org.matheclipse.core.expression.F.stringx((String) e.getValue()));
      if (unit.isNIL()) {
        continue; // unknown unit spelling: skip rather than fail the whole table
      }
      canonicalUnits.put(e.getKey(), unit);
      namesLower.putIfAbsent(e.getKey().toLowerCase(Locale.US), e.getKey());
    }
    return new PhysicalQuantities(canonicalUnits, namesLower);
  }
}

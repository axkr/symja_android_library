package org.matheclipse.core.units;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;

/**
 * The unit database, loaded lazily from the generated resource <code>/units/units.json</code>
 * (created by <code>org.matheclipse.tools.units.UnitsJsonGenerator</code> in the tools module).
 *
 * <p>
 * {@link #resolve(String)} maps any accepted unit spelling — canonical WMA names
 * ({@code "Meters"}), aliases and legacy short names ({@code "m"}, {@code "kW"}), case-insensitive
 * and plural variants, and dynamically prefix-split names ({@code "Kilometers"},
 * {@code "Microfarads"}) — to an immutable {@link UnitEntry}. Prefixed entries are synthesized on
 * demand and cached.
 */
public final class UnitRegistry {

  private static final class Holder {
    private static final UnitRegistry INSTANCE = load();
  }

  public static UnitRegistry get() {
    return Holder.INSTANCE;
  }

  private static final class Prefix {
    final String wlName;
    final IRational factor;
    final List<String> spellings = new ArrayList<>();
    /** the prefix symbol used for composed abbreviations, e.g. "k" for Kilo */
    String abbrev;

    Prefix(String wlName, IRational factor) {
      this.wlName = wlName;
      this.factor = factor;
    }
  }

  private final Map<String, UnitEntry> byName = new HashMap<>();
  /** exact alias (case-sensitive) to canonical name */
  private final Map<String, String> aliasIndex = new HashMap<>();
  /** lower-cased alias or canonical name to canonical name */
  private final Map<String, String> aliasIndexLower = new HashMap<>();
  /** (spelling, prefix) pairs, longest spelling first */
  private final List<Object[]> prefixSpellings = new ArrayList<>();
  /** cache of dynamically created prefixed entries, keyed by query string and canonical name */
  private final Map<String, UnitEntry> synthetic = new ConcurrentHashMap<>();
  private final List<String> baseUnits = new ArrayList<>();

  private UnitRegistry() {}

  public List<String> baseUnits() {
    return baseUnits;
  }

  /** Returns the canonical entry known under {@code name}, or {@code null}. */
  public UnitEntry lookupCanonical(String name) {
    UnitEntry entry = byName.get(name);
    return entry != null ? entry : synthetic.get(name);
  }

  /**
   * Resolves any accepted spelling to a unit entry, or {@code null} if unknown. Resolution order:
   * canonical name, cached synthetic name, exact alias, case-insensitive alias, plural-stripped
   * alias, prefix-split (longest prefix spelling wins; the remainder must be a prefixable unit).
   */
  public UnitEntry resolve(String name) {
    if (name == null || name.isEmpty()) {
      return null;
    }
    UnitEntry entry = byName.get(name);
    if (entry != null) {
      return entry;
    }
    entry = synthetic.get(name);
    if (entry != null) {
      return entry;
    }
    entry = lookupSimple(name);
    if (entry != null) {
      return entry;
    }
    entry = prefixAsUnit(name);
    if (entry != null) {
      return entry;
    }
    return resolvePrefixed(name);
  }

  /**
   * A prefix name used on its own is a dimensionless unit - WMA accepts {@code "Kilo"} as a unit,
   * which is what makes {@code "Kilo"*IndependentUnit("Coins")} work.
   */
  private UnitEntry prefixAsUnit(String name) {
    for (Object[] pair : prefixSpellings) {
      Prefix prefix = (Prefix) pair[1];
      if (prefix.wlName.equals(name)) {
        UnitEntry synth = synthetic.get(name);
        if (synth == null) {
          synth =
              new UnitEntry(prefix.wlName, prefix.factor, new TreeMap<>(), null, null, null, false);
          synthetic.putIfAbsent(name, synth);
          synth = synthetic.get(name);
        }
        return synth;
      }
    }
    return null;
  }

  /** Alias/case/plural lookup without prefix splitting. */
  private UnitEntry lookupSimple(String name) {
    UnitEntry entry = byName.get(name);
    if (entry != null) {
      return entry;
    }
    String canonical = aliasIndex.get(name);
    if (canonical == null) {
      canonical = aliasIndexLower.get(name.toLowerCase(Locale.US));
    }
    if (canonical == null && name.length() > 2 && (name.endsWith("s") || name.endsWith("S"))) {
      String singular = name.substring(0, name.length() - 1);
      canonical = aliasIndex.get(singular);
      if (canonical == null) {
        canonical = aliasIndexLower.get(singular.toLowerCase(Locale.US));
      }
    }
    return canonical != null ? byName.get(canonical) : null;
  }

  private UnitEntry resolvePrefixed(String name) {
    for (Object[] pair : prefixSpellings) {
      String spelling = (String) pair[0];
      if (name.length() > spelling.length() && name.startsWith(spelling)) {
        Prefix prefix = (Prefix) pair[1];
        UnitEntry base = lookupSimple(name.substring(spelling.length()));
        if (base != null && base.prefixable && base.offset == null) {
          UnitEntry synth = synthesizePrefixed(prefix, base);
          synthetic.putIfAbsent(synth.name, synth);
          synthetic.putIfAbsent(name, synth);
          return synth;
        }
      }
    }
    return null;
  }

  private static UnitEntry synthesizePrefixed(Prefix prefix, UnitEntry base) {
    String canonicalName =
        prefix.wlName + Character.toLowerCase(base.name.charAt(0)) + base.name.substring(1);
    IExpr coefficient;
    if (base.coefficient.isRational()) {
      coefficient = prefix.factor.multiply((IRational) base.coefficient);
    } else {
      coefficient = F.Times(prefix.factor, base.coefficient);
    }
    String abbrev =
        prefix.abbrev != null && base.abbrev != null ? prefix.abbrev + base.abbrev : null;
    Map<String, IRational> factors = new TreeMap<>(base.factors);
    return new UnitEntry(canonicalName, coefficient, factors, null, base.temperature, abbrev,
        false);
  }

  // ---------------------------------------------------------------- loading

  private static UnitRegistry load() {
    String json;
    try (InputStream is = UnitRegistry.class.getResourceAsStream("/units/units.json")) {
      if (is == null) {
        throw new IllegalStateException("resource /units/units.json not found on classpath");
      }
      json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new IllegalStateException("cannot read /units/units.json", e);
    }
    Map<String, Object> root = JsonMini.parseObject(json);
    UnitRegistry registry = new UnitRegistry();
    registry.loadBaseUnits(root);
    registry.loadPrefixes(root);
    registry.loadUnits(root);
    return registry;
  }

  @SuppressWarnings("unchecked")
  private void loadBaseUnits(Map<String, Object> root) {
    for (Object base : (List<Object>) root.get("baseUnits")) {
      baseUnits.add((String) base);
    }
  }

  @SuppressWarnings("unchecked")
  private void loadPrefixes(Map<String, Object> root) {
    Map<String, Object> prefixes = (Map<String, Object>) root.get("prefixes");
    for (Map.Entry<String, Object> e : prefixes.entrySet()) {
      Map<String, Object> spec = (Map<String, Object>) e.getValue();
      IExpr factor = parseExact((String) spec.get("factor"));
      if (!factor.isRational()) {
        continue;
      }
      Prefix prefix = new Prefix(e.getKey(), (IRational) factor);
      prefix.spellings.add(e.getKey());
      Object pintName = spec.get("pintName");
      if (pintName != null) {
        prefix.spellings.add((String) pintName);
      }
      for (Object alias : (List<Object>) spec.getOrDefault("aliases", List.of())) {
        String spelling = (String) alias;
        prefix.spellings.add(spelling);
        if (prefix.abbrev == null) {
          prefix.abbrev = spelling; // the first alias is the prefix symbol
        }
      }
      for (String spelling : prefix.spellings) {
        prefixSpellings.add(new Object[] {spelling, prefix});
      }
    }
    prefixSpellings.sort(Comparator.comparingInt(pair -> -((String) pair[0]).length()));
  }

  @SuppressWarnings("unchecked")
  private void loadUnits(Map<String, Object> root) {
    for (Object o : (List<Object>) root.get("units")) {
      Map<String, Object> spec = (Map<String, Object>) o;
      String name = (String) spec.get("name");
      IExpr coefficient = parseExact((String) spec.get("coefficient"));
      Map<String, IRational> factors = new TreeMap<>();
      for (Map.Entry<String, Object> f : ((Map<String, Object>) spec.get("factors")).entrySet()) {
        factors.put(f.getKey(), exponentOf(f.getValue()));
      }
      String offsetText = (String) spec.get("offset");
      IExpr offset = offsetText == null ? null : parseExact(offsetText);
      String temperature = (String) spec.get("temperature");
      String abbrev = (String) spec.get("abbrev");
      boolean prefixable = Boolean.TRUE.equals(spec.getOrDefault("prefixable", Boolean.TRUE));
      UnitEntry entry =
          new UnitEntry(name, coefficient, factors, offset, temperature, abbrev, prefixable);
      byName.put(name, entry);
      aliasIndexLower.putIfAbsent(name.toLowerCase(Locale.US), name);
      if (abbrev != null) {
        aliasIndex.putIfAbsent(abbrev, name);
      }
      for (Object alias : (List<Object>) spec.getOrDefault("aliases", List.of())) {
        String a = (String) alias;
        aliasIndex.putIfAbsent(a, name);
        aliasIndexLower.putIfAbsent(a.toLowerCase(Locale.US), name);
      }
    }
  }

  /**
   * Structural parser for the exact-coefficient grammar the generator emits
   * ({@code Coefficient.render()}): {@code factor ('*' factor)*} with
   * {@code factor = rational | Pi | Pi^exp | (rational)^exp} and {@code exp = int | (rational)}.
   * Deliberately does NOT use the expression parser: symbol resolution there depends on parser
   * configuration and could produce a {@code Pi} symbol different from the builtin {@link S#Pi},
   * which would never cancel in conversions.
   */
  private static IExpr parseExact(String text) {
    String[] parts = text.split("\\*");
    if (parts.length == 1) {
      return parseFactor(parts[0].trim());
    }
    org.matheclipse.core.interfaces.IASTAppendable times = F.TimesAlloc(parts.length);
    for (String part : parts) {
      times.append(parseFactor(part.trim()));
    }
    return times;
  }

  private static IExpr parseFactor(String s) {
    if (s.startsWith("Pi")) {
      if (s.length() == 2) {
        return S.Pi;
      }
      if (s.charAt(2) != '^') {
        throw new IllegalArgumentException("invalid coefficient factor: " + s);
      }
      return F.Power(S.Pi, parseExponent(s.substring(3)));
    }
    if (s.startsWith("(")) {
      int close = s.indexOf(')');
      IExpr base = parseRational(s.substring(1, close));
      String rest = s.substring(close + 1);
      if (rest.isEmpty()) {
        return base;
      }
      if (rest.charAt(0) != '^') {
        throw new IllegalArgumentException("invalid coefficient factor: " + s);
      }
      return F.Power(base, parseExponent(rest.substring(1)));
    }
    return parseRational(s);
  }

  private static IExpr parseExponent(String s) {
    if (s.startsWith("(") && s.endsWith(")")) {
      s = s.substring(1, s.length() - 1);
    }
    return parseRational(s);
  }

  private static IExpr parseRational(String s) {
    s = s.trim();
    int slash = s.indexOf('/');
    if (slash < 0) {
      return F.ZZ(new java.math.BigInteger(s));
    }
    return F.QQ(F.ZZ(new java.math.BigInteger(s.substring(0, slash).trim())),
        F.ZZ(new java.math.BigInteger(s.substring(slash + 1).trim())));
  }

  private static IRational exponentOf(Object value) {
    if (value instanceof Long) {
      return F.ZZ((Long) value);
    }
    String text = ((String) value).trim();
    int slash = text.indexOf('/');
    if (slash < 0) {
      return F.ZZ(Long.parseLong(text));
    }
    return F.QQ(Long.parseLong(text.substring(0, slash).trim()),
        Long.parseLong(text.substring(slash + 1).trim()));
  }
}

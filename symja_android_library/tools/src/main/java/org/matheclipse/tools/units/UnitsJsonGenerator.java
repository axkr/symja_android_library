package org.matheclipse.tools.units;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Generates the Symja unit database {@code units.json} from pint's {@code default_en.txt} (+
 * {@code constants_en.txt}), the legacy {@code si.properties} short names (imported as aliases),
 * and the hand-curated {@code units_overrides.json}.
 *
 * <p>Every unit is resolved to a flat base expansion {@code coefficient * PROD baseUnit^exp} over
 * the Symja/WMA base units (Meters, Kilograms, Seconds, Amperes, Kelvins, Moles, Candelas,
 * Radians, Steradians, Bits, USDollars) with exact coefficients (rationals, Pi powers, radicals).
 * Affine temperature units carry an additional exact {@code offset} with the semantics
 * {@code valueInBase = coefficient * x + offset}.
 *
 * <p>Usage: {@code java org.matheclipse.tools.units.UnitsJsonGenerator [--repo <repoRoot>]
 * [--out <units.json>] [--report <report.txt>]} - paths default to the repository layout.
 */
public final class UnitsJsonGenerator {

  private static final List<String> BASE_UNITS = List.of("Meters", "Kilograms", "Seconds",
      "Amperes", "Kelvins", "Moles", "Candelas", "Radians", "Steradians", "Bits", "USDollars");

  private static final String PINT_PATH =
      "symja_android_library/tools/src/main/resources/org/matheclipse/tools/units/default_en.txt";
  private static final String OVERRIDES_PATH =
      "symja_android_library/tools/src/main/resources/org/matheclipse/tools/units/units_overrides.json";
  private static final String SI_PROPERTIES_PATH =
      "symja_android_library/matheclipse-core/src/main/resources/unit/si.properties";
  private static final String OUT_PATH =
      "symja_android_library/matheclipse-core/src/main/resources/units/units.json";
  private static final String REPORT_PATH = "symja_android_library/tools/target/units-report.txt";

  private static final class ResolveException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    ResolveException(String message) {
      super(message);
    }
  }

  private static final class ResolvedUnit {
    final ExprParser.Term term;
    final BigRational offset; // nullable; non-null only for affine (absolute temperature) units
    final boolean absoluteTemperature;

    ResolvedUnit(ExprParser.Term term, BigRational offset, boolean absoluteTemperature) {
      this.term = term;
      this.offset = offset;
      this.absoluteTemperature = absoluteTemperature;
    }
  }

  private static final class Entry {
    String name;
    String pintName; // null for override-added entries
    boolean provisional;
    String coefficient;
    TreeMap<String, BigRational> factors = new TreeMap<>();
    String offset; // nullable
    String temperature; // "absolute" | "difference" | null
    String abbrev; // nullable
    boolean prefixable = true;
    LinkedHashSet<String> aliases = new LinkedHashSet<>();

    String matchKey() {
      return coefficient + "|" + factors;
    }
  }

  private static final class Prefix {
    final String pintName;
    final String wlName;
    final BigRational factor;
    final List<String> spellings = new ArrayList<>(); // pint name + pint aliases

    Prefix(String pintName, String wlName, BigRational factor) {
      this.pintName = pintName;
      this.wlName = wlName;
      this.factor = factor;
    }
  }

  // ---------------------------------------------------------------- state

  private final PintParser.Model model;
  private final Map<String, Object> overrides;
  private final List<Prefix> prefixes = new ArrayList<>();
  private final Map<String, String> aliasToCanonical = new LinkedHashMap<>();
  private final Map<String, ResolvedUnit> resolved = new LinkedHashMap<>();
  private final Set<String> inProgress = new LinkedHashSet<>();
  private final Set<String> skipNames = new LinkedHashSet<>();
  private final Set<String> noEmitNames = new LinkedHashSet<>();
  private final Map<String, String> forceBase = new LinkedHashMap<>();
  private final Map<String, String> confirmedNames = new LinkedHashMap<>();
  private final List<String> report = new ArrayList<>();

  private UnitsJsonGenerator(PintParser.Model model, Map<String, Object> overrides) {
    this.model = model;
    this.overrides = overrides;
  }

  @SuppressWarnings("unchecked")
  private List<String> overrideList(String key) {
    Object v = overrides.get(key);
    return v == null ? List.of() : (List<String>) v;
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> overrideMap(String key) {
    Object v = overrides.get(key);
    return v == null ? Map.of() : (Map<String, Object>) v;
  }

  // ---------------------------------------------------------------- main

  public static void main(String[] args) throws IOException {
    Path repo = null;
    Path out = null;
    Path reportPath = null;
    for (int i = 0; i < args.length - 1; i += 2) {
      switch (args[i]) {
        case "--repo":
          repo = Paths.get(args[i + 1]);
          break;
        case "--out":
          out = Paths.get(args[i + 1]);
          break;
        case "--report":
          reportPath = Paths.get(args[i + 1]);
          break;
        default:
          throw new IllegalArgumentException("unknown option " + args[i]);
      }
    }
    if (repo == null) {
      repo = findRepoRoot();
    }
    Path pintFile = repo.resolve(PINT_PATH);
    Path overridesFile = repo.resolve(OVERRIDES_PATH);
    Path siFile = repo.resolve(SI_PROPERTIES_PATH);
    if (out == null) {
      out = repo.resolve(OUT_PATH);
    }
    if (reportPath == null) {
      reportPath = repo.resolve(REPORT_PATH);
    }

    PintParser.Model model = PintParser.parse(pintFile);
    Map<String, Object> overrides =
        Json.parseObject(Files.readString(overridesFile, StandardCharsets.UTF_8));
    UnitsJsonGenerator generator = new UnitsJsonGenerator(model, overrides);
    generator.run(siFile, out, reportPath);
  }

  private static Path findRepoRoot() {
    Path dir = Paths.get("").toAbsolutePath();
    while (dir != null) {
      if (Files.exists(dir.resolve(PINT_PATH))) {
        return dir;
      }
      dir = dir.getParent();
    }
    throw new IllegalArgumentException(
        "repository root not found from working directory; pass --repo <path>");
  }

  private void run(Path siFile, Path out, Path reportPath) throws IOException {
    skipNames.addAll(overrideList("skip"));
    noEmitNames.addAll(overrideList("noEmit"));
    overrideMap("forceBase").forEach((k, v) -> forceBase.put(k, (String) v));
    overrideMap("names").forEach((k, v) -> confirmedNames.put(k, (String) v));

    buildPrefixes();
    buildAliasIndex();

    // resolve + emit pint units
    List<Entry> entries = new ArrayList<>();
    Map<String, Entry> byName = new LinkedHashMap<>();
    List<String> unresolved = new ArrayList<>();
    for (PintParser.UnitDef def : model.units.values()) {
      if (skipNames.contains(def.name) || noEmitNames.contains(def.name)
          || isPiName(def.name)) {
        continue;
      }
      ResolvedUnit r;
      try {
        r = resolve(def.name);
      } catch (RuntimeException e) {
        unresolved.add(def.name + " (" + def.sourceFile + ":" + def.line + "): " + e.getMessage());
        continue;
      }
      Entry entry = toEntry(def, r);
      Entry previous = byName.put(entry.name, entry);
      if (previous != null) {
        report.add("NAME COLLISION: " + entry.name + " from pint '" + def.name
            + "' replaces entry from pint '" + previous.pintName + "'");
        entries.remove(previous);
      }
      entries.add(entry);
    }

    addKilogramsBase(entries, byName);
    applyAddedEntries(entries, byName);
    applyPatches(byName);
    applyExtraAliases(byName);
    importSiProperties(siFile, entries, byName);
    checkAliasCollisions(entries);

    entries.sort(Comparator.comparing(e -> e.name));
    Map<String, Object> json = assembleJson(entries);
    Files.createDirectories(out.getParent());
    Files.writeString(out, Json.write(json), StandardCharsets.UTF_8);
    Files.createDirectories(reportPath.getParent());
    Files.writeString(reportPath, buildReport(entries, unresolved), StandardCharsets.UTF_8);

    long provisional = entries.stream().filter(e -> e.provisional).count();
    System.out.println("units.json:  " + out);
    System.out.println("report:      " + reportPath);
    System.out.println("prefixes:    " + prefixes.size());
    System.out.println("units:       " + entries.size() + " (" + provisional
        + " provisional names needing curation)");
    System.out.println("unresolved:  " + unresolved.size());
    System.out.println("report notes: " + report.size());
  }

  // ---------------------------------------------------------------- prefixes

  private void buildPrefixes() {
    Set<String> skipPrefixes = new LinkedHashSet<>(overrideList("skipPrefixes"));
    Map<String, Object> prefixNames = overrideMap("prefixNames");
    for (PintParser.PrefixDef def : model.prefixes) {
      if (skipPrefixes.contains(def.name)) {
        report.add("prefix skipped (not a WL prefix): " + def.name);
        continue;
      }
      ExprParser.Term term = ExprParser.parseMonomial(def.valueText);
      if (!term.isPureNumber() || !term.coeff.isRational()) {
        report.add("prefix with non-rational factor skipped: " + def.name);
        continue;
      }
      String wlName = prefixNames.containsKey(def.name) ? (String) prefixNames.get(def.name)
          : Character.toUpperCase(def.name.charAt(0)) + def.name.substring(1);
      Prefix prefix = new Prefix(def.name, wlName, term.coeff.rationalValue());
      prefix.spellings.add(def.name);
      prefix.spellings.addAll(def.aliases);
      prefixes.add(prefix);
    }
    // longest spelling first for prefix-splitting
    prefixes.sort((a, b) -> Integer.compare(maxSpelling(b), maxSpelling(a)));
  }

  private static int maxSpelling(Prefix p) {
    return p.spellings.stream().mapToInt(String::length).max().orElse(0);
  }

  // ---------------------------------------------------------------- alias index

  private void buildAliasIndex() {
    for (PintParser.UnitDef def : model.units.values()) {
      for (String alias : def.aliases) {
        addAlias(alias, def.name);
      }
    }
    for (List<String> directive : model.aliasDirectives) {
      String target = directive.get(0);
      String canonical = aliasToCanonical.getOrDefault(target, target);
      for (int i = 1; i < directive.size(); i++) {
        addAlias(directive.get(i), canonical);
      }
    }
  }

  private void addAlias(String alias, String canonical) {
    if (alias.equals(canonical) || model.units.containsKey(alias)) {
      return;
    }
    String existing = aliasToCanonical.putIfAbsent(alias, canonical);
    if (existing != null && !existing.equals(canonical)) {
      report.add("pint alias collision: '" + alias + "' -> " + existing + " and " + canonical
          + " (kept first)");
    }
  }

  // ---------------------------------------------------------------- resolver

  private static boolean isPiName(String name) {
    return name.equals("pi") || name.equals("π");
  }

  private ResolvedUnit resolve(String name) {
    if (isPiName(name)) {
      ExprParser.Term term = new ExprParser.Term();
      term.coeff = Coefficient.pi();
      return new ResolvedUnit(term, null, false);
    }
    String canonical = model.units.containsKey(name) ? name : aliasToCanonical.get(name);
    if (canonical == null) {
      return resolveWithPrefixSplit(name);
    }
    if (skipNames.contains(canonical)) {
      throw new ResolveException("references skipped unit '" + canonical + "'");
    }
    ResolvedUnit cached = resolved.get(canonical);
    if (cached != null) {
      return cached;
    }
    if (!inProgress.add(canonical)) {
      throw new ResolveException("definition cycle at '" + canonical + "'");
    }
    try {
      ResolvedUnit r = resolveDefinition(model.units.get(canonical));
      resolved.put(canonical, r);
      return r;
    } finally {
      inProgress.remove(canonical);
    }
  }

  private ResolvedUnit resolveDefinition(PintParser.UnitDef def) {
    BigRational offset = null;
    boolean absoluteTemperature = false;
    if (def.offsetText != null) {
      absoluteTemperature = true;
      BigRational value = ExprParser.parseNumber(def.offsetText);
      if (!value.isZero()) {
        offset = value;
      }
    }
    String forcedBase = forceBase.get(def.name);
    if (forcedBase != null) {
      return new ResolvedUnit(baseTerm(forcedBase), offset, absoluteTemperature);
    }
    if (def.dimName != null) {
      return new ResolvedUnit(resolveBaseDeclaration(def), offset, absoluteTemperature);
    }
    ExprParser.Term parsed = ExprParser.parseMonomial(def.exprText);
    ExprParser.Term result = new ExprParser.Term();
    result.coeff = parsed.coeff;
    for (Map.Entry<String, BigRational> factor : parsed.factors.entrySet()) {
      ResolvedUnit sub = resolve(factor.getKey());
      if (sub.offset != null) {
        throw new ResolveException("cannot compose with affine unit '" + factor.getKey() + "'");
      }
      ExprParser.Term powered = sub.term.pow(factor.getValue());
      result = result.multiply(powered);
    }
    return new ResolvedUnit(result, offset, absoluteTemperature);
  }

  private ExprParser.Term resolveBaseDeclaration(PintParser.UnitDef def) {
    switch (def.name) {
      case "meter":
        return baseTerm("Meters");
      case "second":
        return baseTerm("Seconds");
      case "ampere":
        return baseTerm("Amperes");
      case "candela":
        return baseTerm("Candelas");
      case "kelvin":
        return baseTerm("Kelvins");
      case "mole":
        return baseTerm("Moles");
      case "radian":
        return baseTerm("Radians");
      case "bit":
        return baseTerm("Bits");
      case "gram": {
        ExprParser.Term term = baseTerm("Kilograms");
        term.coeff = Coefficient.of(BigRational.of(1, 1000));
        return term;
      }
      default:
        if (def.dimName.equals("[]")) {
          return new ExprParser.Term(); // dimensionless pseudo-base (count, RIU, AU)
        }
        throw new ResolveException(
            "unmapped base dimension " + def.dimName + " - add to forceBase or skip overrides");
    }
  }

  private static ExprParser.Term baseTerm(String wlBase) {
    ExprParser.Term term = new ExprParser.Term();
    term.addFactor(wlBase, BigRational.ONE);
    return term;
  }

  private ResolvedUnit resolveWithPrefixSplit(String name) {
    for (Prefix prefix : prefixes) {
      for (String spelling : prefix.spellings) {
        if (name.length() > spelling.length() && name.startsWith(spelling)) {
          String rest = name.substring(spelling.length());
          try {
            ResolvedUnit sub = resolve(rest);
            ExprParser.Term term = new ExprParser.Term();
            term.coeff = Coefficient.of(prefix.factor).multiply(sub.term.coeff);
            term.factors.putAll(sub.term.factors);
            BigRational offset = sub.offset; // unchanged: prefix scales the unit, not the offset
            if (offset != null) {
              throw new ResolveException("prefixed affine unit '" + name + "' not supported");
            }
            return new ResolvedUnit(term, null, sub.absoluteTemperature);
          } catch (ResolveException e) {
            // try next prefix spelling
          }
        }
      }
    }
    throw new ResolveException("unknown unit name '" + name + "'");
  }

  // ---------------------------------------------------------------- entries

  private Entry toEntry(PintParser.UnitDef def, ResolvedUnit r) {
    Entry entry = new Entry();
    entry.pintName = def.name;
    String confirmed = confirmedNames.get(def.name);
    if (confirmed != null) {
      entry.name = confirmed;
    } else {
      entry.name = autoName(def.name);
      entry.provisional = true;
    }
    entry.coefficient = r.term.coeff.render();
    entry.factors.putAll(r.term.factors);
    if (r.offset != null) {
      entry.offset = r.offset.toString();
    }
    if (r.absoluteTemperature) {
      entry.temperature = "absolute";
    }
    if (!def.aliases.isEmpty()) {
      entry.abbrev = def.aliases.get(0);
    }
    entry.aliases.add(def.name);
    entry.aliases.addAll(def.aliases);
    for (Map.Entry<String, String> alias : aliasToCanonical.entrySet()) {
      if (alias.getValue().equals(def.name)) {
        entry.aliases.add(alias.getKey());
      }
    }
    entry.aliases.remove(entry.name);
    return entry;
  }

  private void addKilogramsBase(List<Entry> entries, Map<String, Entry> byName) {
    Entry kg = new Entry();
    kg.name = "Kilograms";
    kg.coefficient = "1";
    kg.factors.put("Kilograms", BigRational.ONE);
    kg.abbrev = "kg";
    kg.prefixable = false;
    kg.aliases.add("kilogram");
    kg.aliases.add("kg");
    entries.add(kg);
    byName.put(kg.name, kg);
  }

  @SuppressWarnings("unchecked")
  private void applyAddedEntries(List<Entry> entries, Map<String, Entry> byName) {
    Object added = overrides.get("add");
    if (added == null) {
      return;
    }
    for (Object o : (List<Object>) added) {
      Map<String, Object> spec = (Map<String, Object>) o;
      Entry entry = new Entry();
      entry.name = (String) spec.get("name");
      entry.coefficient = (String) spec.getOrDefault("coefficient", "1");
      Map<String, Object> factors = (Map<String, Object>) spec.getOrDefault("factors", Map.of());
      for (Map.Entry<String, Object> f : factors.entrySet()) {
        entry.factors.put(f.getKey(), exponentOf(f.getValue()));
      }
      entry.offset = (String) spec.get("offset");
      entry.temperature = (String) spec.get("temperature");
      entry.abbrev = (String) spec.get("abbrev");
      entry.prefixable = Boolean.TRUE.equals(spec.getOrDefault("prefixable", Boolean.TRUE));
      for (Object alias : (List<Object>) spec.getOrDefault("aliases", List.of())) {
        entry.aliases.add((String) alias);
      }
      Entry previous = byName.put(entry.name, entry);
      if (previous != null) {
        entries.remove(previous);
        report.add("override 'add' replaces generated entry: " + entry.name + " (was pint '"
            + previous.pintName + "')");
      }
      entries.add(entry);
    }
  }

  private static BigRational exponentOf(Object value) {
    if (value instanceof Long) {
      return BigRational.of((Long) value);
    }
    return BigRational.parse((String) value);
  }

  @SuppressWarnings("unchecked")
  private void applyPatches(Map<String, Entry> byName) {
    for (Map.Entry<String, Object> patch : overrideMap("patch").entrySet()) {
      if (patch.getKey().startsWith("$")) {
        continue;
      }
      Entry entry = byName.get(patch.getKey());
      if (entry == null) {
        report.add("patch target not found: " + patch.getKey());
        continue;
      }
      for (Map.Entry<String, Object> field : ((Map<String, Object>) patch.getValue()).entrySet()) {
        switch (field.getKey()) {
          case "prefixable":
            entry.prefixable = Boolean.TRUE.equals(field.getValue());
            break;
          case "abbrev":
            entry.abbrev = (String) field.getValue();
            break;
          case "temperature":
            entry.temperature = (String) field.getValue();
            break;
          case "coefficient":
            entry.coefficient = (String) field.getValue();
            break;
          case "offset":
            entry.offset = (String) field.getValue();
            break;
          case "factors": {
            entry.factors.clear();
            Map<String, Object> factors = (Map<String, Object>) field.getValue();
            for (Map.Entry<String, Object> f : factors.entrySet()) {
              entry.factors.put(f.getKey(), exponentOf(f.getValue()));
            }
            break;
          }
          default:
            report.add("unknown patch field '" + field.getKey() + "' for " + patch.getKey());
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void applyExtraAliases(Map<String, Entry> byName) {
    for (Map.Entry<String, Object> extra : overrideMap("aliases").entrySet()) {
      Entry entry = byName.get(extra.getKey());
      if (entry == null) {
        report.add("aliases target not found: " + extra.getKey());
        continue;
      }
      for (Object alias : (List<Object>) extra.getValue()) {
        entry.aliases.add((String) alias);
      }
    }
    for (Map.Entry<String, Object> removal : overrideMap("removeAliases").entrySet()) {
      if (removal.getKey().startsWith("$")) {
        continue;
      }
      Entry entry = byName.get(removal.getKey());
      if (entry == null) {
        report.add("removeAliases target not found: " + removal.getKey());
        continue;
      }
      for (Object alias : (List<Object>) removal.getValue()) {
        if (!entry.aliases.remove((String) alias)) {
          report.add("removeAliases: '" + alias + "' was not an alias of " + removal.getKey());
        }
      }
    }
  }

  // ---------------------------------------------------------------- si.properties import

  private void importSiProperties(Path siFile, List<Entry> entries, Map<String, Entry> byName)
      throws IOException {
    if (!Files.exists(siFile)) {
      report.add("si.properties not found: " + siFile);
      return;
    }
    Map<String, Entry> byMatchKey = new LinkedHashMap<>();
    for (Entry entry : entries) {
      if (entry.offset == null) {
        byMatchKey.putIfAbsent(entry.matchKey(), entry);
      }
    }
    int matched = 0;
    int prefixCovered = 0;
    List<String> unmatched = new ArrayList<>();
    for (String raw : Files.readAllLines(siFile, StandardCharsets.UTF_8)) {
      String line = raw.trim();
      if (line.isEmpty() || line.startsWith("#")) {
        continue;
      }
      int eq = line.indexOf('=');
      if (eq < 0) {
        continue;
      }
      String key = line.substring(0, eq).trim();
      String value = line.substring(eq + 1).trim();
      ExprParser.Term monomial;
      try {
        monomial = parseSiValue(value);
      } catch (RuntimeException e) {
        unmatched.add(key + " (unparseable: " + e.getMessage() + ")");
        continue;
      }
      // prefixed forms like cm, kW, mA are covered by the runtime prefix-split and must NOT be
      // value-matched: 'cm' would otherwise attach to any unit that happens to equal 1/100
      // Meters (pint's Gaussian statfarad!)
      try {
        ResolvedUnit viaSplit = resolve(key);
        if (siMatchKey(viaSplit.term).equals(siMatchKey(monomial))) {
          prefixCovered++;
        } else {
          unmatched.add(key + " (resolves to " + viaSplit.term.coeff.render() + "*"
              + viaSplit.term.factors + ", si.properties says " + monomial.coeff.render() + "*"
              + monomial.factors + ")");
        }
        continue;
      } catch (RuntimeException e) {
        // not resolvable via aliases/prefix-split - fall through to value matching
      }
      Entry target = byMatchKey.get(siMatchKey(monomial));
      if (target != null) {
        // an si key that pint already uses for a DIFFERENT unit must not be re-attached by
        // value: this is how the legacy grad=Pi/180 bug (and Bq->Hertz etc.) gets rejected
        String pintOwner = aliasToCanonical.get(key);
        if (pintOwner != null && !pintOwner.equals(target.pintName)) {
          unmatched.add(key + " (value matches " + target.name + ", but '" + key
              + "' is the pint alias of '" + pintOwner + "' - legacy si.properties value is"
              + " likely wrong; NOT attached)");
          continue;
        }
        if (target.aliases.add(key)) {
          matched++;
        }
        // register with the resolver too, so later prefixed keys (kOhm) can split over it
        if (target.pintName != null) {
          aliasToCanonical.putIfAbsent(key, target.pintName);
        }
        continue;
      }
      unmatched.add(key + " (= " + value + ")");
    }
    report.add("si.properties: " + matched + " keys attached as aliases, " + prefixCovered
        + " covered by prefix-split, " + unmatched.size() + " unmatched");
    for (String u : unmatched) {
      report.add("si.properties UNMATCHED: " + u);
    }
  }

  private static String siMatchKey(ExprParser.Term term) {
    return term.coeff.render() + "|" + new TreeMap<>(term.factors);
  }

  private ExprParser.Term parseSiValue(String value) {
    int open = value.indexOf('[');
    String coefficientText = open < 0 ? value : value.substring(0, open).trim();
    String unitText =
        open < 0 ? "" : value.substring(open + 1, value.lastIndexOf(']')).trim();
    ExprParser.Term result = resolveTermNames(ExprParser.parseMonomial(coefficientText));
    if (!unitText.isEmpty()) {
      result = result.multiply(resolveTermNames(ExprParser.parseMonomial(unitText)));
    }
    return result;
  }

  /** Resolves the named factors of a parsed term ({@code Pi} symbolic, unit names via pint). */
  private ExprParser.Term resolveTermNames(ExprParser.Term parsed) {
    ExprParser.Term result = new ExprParser.Term();
    result.coeff = parsed.coeff;
    for (Map.Entry<String, BigRational> factor : parsed.factors.entrySet()) {
      if (factor.getKey().equals("Pi") || isPiName(factor.getKey())) {
        result.coeff = result.coeff.multiply(Coefficient.pi().pow(factor.getValue()));
      } else {
        result = result.multiply(resolve(factor.getKey()).term.pow(factor.getValue()));
      }
    }
    return result;
  }

  // ---------------------------------------------------------------- checks, naming, output

  private void checkAliasCollisions(List<Entry> entries) {
    Map<String, String> owner = new LinkedHashMap<>();
    for (Entry entry : entries) {
      owner.put(entry.name, entry.name);
    }
    for (Entry entry : entries) {
      for (String alias : new ArrayList<>(entry.aliases)) {
        String existing = owner.putIfAbsent(alias, entry.name);
        if (existing != null && !existing.equals(entry.name)) {
          entry.aliases.remove(alias);
          report.add("alias collision: '" + alias + "' kept on " + existing + ", removed from "
              + entry.name);
        }
      }
    }
  }

  private static String autoName(String pintName) {
    String[] parts = pintName.split("_");
    StringBuilder b = new StringBuilder();
    if (parts.length > 1 && parts[0].equals("degree")) {
      b.append("Degrees");
      for (int i = 1; i < parts.length; i++) {
        b.append(capitalize(parts[i]));
      }
      return b.toString();
    }
    for (int i = 0; i < parts.length; i++) {
      String word = capitalize(parts[i]);
      if (i == parts.length - 1) {
        word = pluralize(word);
      }
      b.append(word);
    }
    return b.toString();
  }

  private static String capitalize(String word) {
    return word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1);
  }

  private static String pluralize(String word) {
    if (word.isEmpty()) {
      return word;
    }
    if (word.endsWith("Foot") || word.equals("Foot")) {
      return word.substring(0, word.length() - 4) + "Feet";
    }
    if (word.endsWith("Inch") || word.equals("Inch")) {
      return word + "es";
    }
    char last = Character.toLowerCase(word.charAt(word.length() - 1));
    if (last == 's' || last == 'x' || last == 'z') {
      return word; // hertz, siemens, lux, gauss - invariant
    }
    if (word.length() >= 2) {
      String lastTwo = word.substring(word.length() - 2).toLowerCase();
      if (lastTwo.equals("ch") || lastTwo.equals("sh")) {
        return word;
      }
    }
    if (last == 'y' && word.length() >= 2 && "aeiou".indexOf(
        Character.toLowerCase(word.charAt(word.length() - 2))) < 0) {
      return word.substring(0, word.length() - 1) + "ies"; // century -> Centuries
    }
    return word + "s";
  }

  private Map<String, Object> assembleJson(List<Entry> entries) {
    Map<String, Object> root = new LinkedHashMap<>();
    root.put("$comment",
        "GENERATED by org.matheclipse.tools.units.UnitsJsonGenerator from pint default_en.txt"
            + " + constants_en.txt + legacy si.properties + units_overrides.json. Do not edit"
            + " by hand - edit units_overrides.json and re-run the generator. Semantics:"
            + " valueInBase = coefficient * x + offset; coefficients are exact Symja input"
            + " strings; 'provisional': true marks auto-generated WL names awaiting curation.");
    Map<String, Object> prefixJson = new LinkedHashMap<>();
    List<Prefix> ordered = new ArrayList<>(prefixes);
    ordered.sort(Comparator.comparing(p -> p.pintName));
    for (Prefix prefix : ordered) {
      Map<String, Object> p = new LinkedHashMap<>();
      p.put("factor", prefix.factor.toString());
      p.put("pintName", prefix.pintName);
      p.put("aliases", new ArrayList<>(prefix.spellings.subList(1, prefix.spellings.size())));
      prefixJson.put(prefix.wlName, p);
    }
    root.put("prefixes", prefixJson);
    root.put("baseUnits", new ArrayList<>(BASE_UNITS));
    List<Object> unitList = new ArrayList<>();
    for (Entry entry : entries) {
      for (String factor : entry.factors.keySet()) {
        if (!BASE_UNITS.contains(factor)) {
          throw new IllegalStateException(
              "entry " + entry.name + " uses non-base factor " + factor);
        }
      }
      Map<String, Object> u = new LinkedHashMap<>();
      u.put("name", entry.name);
      if (entry.pintName != null) {
        u.put("pintName", entry.pintName);
      }
      if (entry.provisional) {
        u.put("provisional", Boolean.TRUE);
      }
      u.put("coefficient", entry.coefficient);
      Map<String, Object> factors = new TreeMap<>();
      for (Map.Entry<String, BigRational> f : entry.factors.entrySet()) {
        factors.put(f.getKey(),
            f.getValue().isInteger() ? (Object) f.getValue().numerator().longValueExact()
                : f.getValue().toString());
      }
      u.put("factors", factors);
      if (entry.offset != null) {
        u.put("offset", entry.offset);
      }
      if (entry.temperature != null) {
        u.put("temperature", entry.temperature);
      }
      if (entry.abbrev != null) {
        u.put("abbrev", entry.abbrev);
      }
      u.put("prefixable", entry.prefixable);
      u.put("aliases", new ArrayList<>(entry.aliases));
      unitList.add(u);
    }
    root.put("units", unitList);
    return root;
  }

  private String buildReport(List<Entry> entries, List<String> unresolved) {
    StringBuilder b = new StringBuilder();
    b.append("units.json generation report\n============================\n\n");
    b.append("entries: ").append(entries.size()).append(", prefixes: ").append(prefixes.size())
        .append("\n\n");
    b.append("## Unresolved pint units (skipped)\n");
    unresolved.forEach(u -> b.append("  - ").append(u).append('\n'));
    b.append("\n## Provisional WL names (curate in units_overrides.json 'names')\n");
    entries.stream().filter(e -> e.provisional)
        .forEach(e -> b.append("  - ").append(e.pintName).append(" -> ").append(e.name)
            .append('\n'));
    b.append("\n## Parser skips\n");
    model.skipped.forEach(s -> b.append("  - ").append(s).append('\n'));
    b.append("\n## Parser warnings\n");
    model.warnings.forEach(w -> b.append("  - ").append(w).append('\n'));
    b.append("\n## Generator notes\n");
    report.forEach(r -> b.append("  - ").append(r).append('\n'));
    return b.toString();
  }
}

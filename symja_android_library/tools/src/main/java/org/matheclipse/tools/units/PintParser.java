package org.matheclipse.tools.units;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Parses pint's declarative unit-definition format ({@code default_en.txt} /
 * {@code constants_en.txt}): prefixes ({@code kilo- = 1e3 = k-}), base units
 * ({@code meter = [length] = m = metre}), derived units with alias lists, affine offsets
 * ({@code degree_Celsius = kelvin; offset: 273.15 = ...}), {@code @group} blocks (contents kept),
 * {@code @context}/{@code @system}/{@code @defaults} blocks (skipped), {@code @alias} lines and
 * {@code @import} includes. Logarithmic units ({@code ; logbase:}) and derived-dimension lines
 * ({@code [area] = [length] ** 2}) are recorded as skipped.
 */
public final class PintParser {

  public static final class PrefixDef {
    public final String name;
    public final String valueText;
    public final List<String> aliases;
    public final int line;

    PrefixDef(String name, String valueText, List<String> aliases, int line) {
      this.name = name;
      this.valueText = valueText;
      this.aliases = aliases;
      this.line = line;
    }
  }

  public static final class UnitDef {
    public final String name;
    /** Expression text, or {@code null} for a base-unit declaration. */
    public final String exprText;
    /** Dimension name for base declarations: {@code "[length]"} or {@code "[]"}; else null. */
    public final String dimName;
    /** Offset expression text (affine units), or null. */
    public final String offsetText;
    public final List<String> aliases;
    public final String group;
    public final String sourceFile;
    public final int line;

    UnitDef(String name, String exprText, String dimName, String offsetText, List<String> aliases,
        String group, String sourceFile, int line) {
      this.name = name;
      this.exprText = exprText;
      this.dimName = dimName;
      this.offsetText = offsetText;
      this.aliases = aliases;
      this.group = group;
      this.sourceFile = sourceFile;
      this.line = line;
    }
  }

  public static final class Model {
    public final List<PrefixDef> prefixes = new ArrayList<>();
    public final LinkedHashMap<String, UnitDef> units = new LinkedHashMap<>();
    /** Each entry: [canonicalOrAlias, alias1, alias2, ...] from {@code @alias} lines. */
    public final List<List<String>> aliasDirectives = new ArrayList<>();
    public final List<String> skipped = new ArrayList<>();
    public final List<String> warnings = new ArrayList<>();
  }

  private PintParser() {}

  public static Model parse(Path file) throws IOException {
    Model model = new Model();
    parseInto(file, model);
    return model;
  }

  private static void parseInto(Path file, Model model) throws IOException {
    List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
    String fileName = file.getFileName().toString();
    String currentGroup = null;
    String skipBlock = null; // inside @context/@system/@defaults
    for (int i = 0; i < lines.size(); i++) {
      int lineNo = i + 1;
      String line = stripComment(lines.get(i)).trim();
      if (line.isEmpty()) {
        continue;
      }
      if (line.startsWith("@")) {
        if (line.startsWith("@end")) {
          if (skipBlock != null) {
            skipBlock = null;
          } else if (currentGroup != null) {
            currentGroup = null;
          } else {
            model.warnings.add(fileName + ":" + lineNo + " stray @end");
          }
        } else if (skipBlock != null) {
          model.warnings.add(fileName + ":" + lineNo + " nested directive inside @" + skipBlock);
        } else if (line.startsWith("@group")) {
          currentGroup = line.substring("@group".length()).trim();
        } else if (line.startsWith("@context") || line.startsWith("@system")
            || line.startsWith("@defaults")) {
          skipBlock = line.substring(1).split("[\\s(]", 2)[0];
        } else if (line.startsWith("@import")) {
          String imported = line.substring("@import".length()).trim();
          parseInto(file.resolveSibling(imported), model);
        } else if (line.startsWith("@alias")) {
          List<String> parts = splitEquals(line.substring("@alias".length()));
          if (parts.size() >= 2) {
            model.aliasDirectives.add(parts);
          } else {
            model.warnings.add(fileName + ":" + lineNo + " unparseable @alias: " + line);
          }
        } else {
          model.skipped.add(fileName + ":" + lineNo + " unknown directive: " + line);
        }
        continue;
      }
      if (skipBlock != null) {
        continue; // content of @context/@system/@defaults
      }
      if (line.startsWith("[")) {
        continue; // derived dimension definition - not needed
      }
      List<String> parts = splitEquals(line);
      if (parts.size() < 2) {
        model.skipped.add(fileName + ":" + lineNo + " unparseable line: " + line);
        continue;
      }
      String name = parts.get(0);
      if (name.endsWith("-")) {
        // prefix definition
        String prefixName = name.substring(0, name.length() - 1);
        List<String> aliases = new ArrayList<>();
        for (int a = 2; a < parts.size(); a++) {
          String alias = parts.get(a);
          if (alias.endsWith("-")) {
            alias = alias.substring(0, alias.length() - 1);
          }
          if (!alias.isEmpty() && !alias.equals("_")) {
            aliases.add(alias);
          }
        }
        model.prefixes.add(new PrefixDef(prefixName, parts.get(1), aliases, lineNo));
        continue;
      }
      // unit definition; parts.get(1) may carry ";"-separated modifiers
      String[] segments = parts.get(1).split(";");
      String expr = segments[0].trim();
      String offsetText = null;
      boolean logarithmic = false;
      for (int s = 1; s < segments.length; s++) {
        String seg = segments[s].trim();
        if (seg.startsWith("offset:")) {
          offsetText = seg.substring("offset:".length()).trim();
        } else if (seg.startsWith("logbase:") || seg.startsWith("logfactor:")) {
          logarithmic = true;
        } else if (!seg.isEmpty()) {
          model.warnings.add(fileName + ":" + lineNo + " unknown modifier: " + seg);
        }
      }
      if (logarithmic) {
        model.skipped.add(fileName + ":" + lineNo + " logarithmic unit: " + name);
        continue;
      }
      List<String> aliases = new ArrayList<>();
      for (int a = 2; a < parts.size(); a++) {
        String alias = parts.get(a);
        if (!alias.isEmpty() && !alias.equals("_")) {
          aliases.add(alias);
        }
      }
      String dimName = null;
      String exprText = null;
      if (expr.startsWith("[") && expr.endsWith("]")) {
        dimName = expr;
      } else {
        exprText = expr;
      }
      UnitDef def =
          new UnitDef(name, exprText, dimName, offsetText, aliases, currentGroup, fileName, lineNo);
      if (model.units.containsKey(name)) {
        model.warnings.add(fileName + ":" + lineNo + " duplicate definition of " + name
            + " replaces " + model.units.get(name).sourceFile + ":" + model.units.get(name).line);
      }
      model.units.put(name, def);
    }
    if (skipBlock != null || currentGroup != null) {
      model.warnings.add(fileName + ": unterminated block at end of file");
    }
  }

  private static String stripComment(String line) {
    int hash = line.indexOf('#');
    return hash < 0 ? line : line.substring(0, hash);
  }

  private static List<String> splitEquals(String line) {
    List<String> parts = new ArrayList<>();
    for (String p : line.split("=", -1)) {
      parts.add(p.trim());
    }
    return parts;
  }
}

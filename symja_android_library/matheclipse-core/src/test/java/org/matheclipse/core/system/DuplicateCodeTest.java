package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.cpd.CPDConfiguration;
import net.sourceforge.pmd.cpd.Mark;
import net.sourceforge.pmd.cpd.Match;

/**
 * Copy-paste detection (PMD CPD) over the main sources of {@code matheclipse-core}.
 *
 * <p>
 * Every duplicated block of at least {@link #MINIMUM_TOKENS} tokens is keyed by the sorted set of
 * files it occurs in. The keys of today's known duplicates are listed in
 * {@code src/test/resources/cpd-baseline.txt}, one line per block. The test fails when a file group
 * has more duplicated blocks than the baseline lists for it, and prints the baseline lines that are
 * no longer needed.
 *
 * <p>
 * The same check runs in the build as the {@code cpd-check} goal of {@code maven-pmd-plugin} (bound
 * to {@code verify}, counting blocks against {@code cpd.maxAllowedViolations}); this test names the
 * files instead of only a count. Settings and excludes must match the plugin configuration in the
 * module's {@code pom.xml}. Deliberate copies are wrapped in {@code // CPD-OFF} ... {@code // CPD-ON}
 * comments in the source.
 *
 * <p>
 * Accept the current state with {@code -Dcpd.updateBaseline=true}, which rewrites the baseline file.
 */
@Tag(TestTags.SLOW)
public class DuplicateCodeTest {

  private static final int MINIMUM_TOKENS = 70;

  private static final Path SOURCE_ROOT = Paths.get("src/main/java");

  private static final String PACKAGE_PREFIX = "org/matheclipse/core/";

  private static final Path BASELINE = Paths.get("src/test/resources/cpd-baseline.txt");

  /**
   * Generated sources: Rubi IntRules, the rules/*Rules and rulesets/*Rules classes, the ID/S/J
   * symbol tables and the element data tables.
   */
  private static final Pattern EXCLUDES = Pattern.compile(
      "integrate/rubi/.*|reflection/system/rules/.*|reflection/system/rulesets/.*"
          + "|expression/(ID|S|J)\\.java"
          + "|data/ElementData[12]\\.java");

  @Test
  public void testNoNewDuplicates() throws IOException {
    List<Match> matches = findDuplicates();

    Map<String, List<Match>> current = new TreeMap<>();
    for (Match match : matches) {
      current.computeIfAbsent(key(match), k -> new ArrayList<>()).add(match);
    }

    if (Boolean.getBoolean("cpd.updateBaseline")) {
      List<String> lines = new ArrayList<>();
      lines.add("# Known duplicated blocks for DuplicateCodeTest, one line per block: the files it");
      lines.add("# occurs in. Regenerate with -Dcpd.updateBaseline=true and keep");
      lines.add("# cpd.maxAllowedViolations in pom.xml at the number of lines below.");
      current.forEach((key, list) -> list.forEach(m -> lines.add(key)));
      Files.write(BASELINE, lines, StandardCharsets.UTF_8);
      System.out.println("cpd-baseline.txt rewritten with " + matches.size() + " duplicates");
      return;
    }

    Map<String, Integer> baseline = readBaseline();
    StringBuilder newDuplicates = new StringBuilder();
    for (Map.Entry<String, List<Match>> entry : current.entrySet()) {
      int allowed = baseline.getOrDefault(entry.getKey(), 0);
      List<Match> list = entry.getValue();
      if (list.size() > allowed) {
        newDuplicates.append(entry.getKey()).append(" has ").append(list.size())
            .append(" duplicated block(s), baseline allows ").append(allowed).append('\n');
        for (Match match : list) {
          newDuplicates.append(describe(match));
        }
      }
    }
    for (Map.Entry<String, Integer> entry : baseline.entrySet()) {
      int found = current.getOrDefault(entry.getKey(), List.of()).size();
      if (found < entry.getValue()) {
        System.out.println("cpd-baseline.txt can drop " + (entry.getValue() - found) + " line(s): "
            + entry.getKey());
      }
    }
    if (newDuplicates.length() > 0) {
      fail("New duplicated code (CPD, >= " + MINIMUM_TOKENS + " tokens). Extract the common code, "
          + "or wrap a deliberate copy in // CPD-OFF ... // CPD-ON.\n" + newDuplicates);
    }
  }

  private static List<Match> findDuplicates() throws IOException {
    assertTrue(Files.isDirectory(SOURCE_ROOT),
        "run from the matheclipse-core module directory: " + SOURCE_ROOT.toAbsolutePath());
    CPDConfiguration config = new CPDConfiguration();
    config.setLanguage(CPDConfiguration.getLanguageFromString("java"));
    config.setMinimumTileSize(MINIMUM_TOKENS);
    config.setEncoding(StandardCharsets.UTF_8.name());
    config.setIgnoreLiterals(false);
    config.setIgnoreIdentifiers(false);
    config.setIgnoreAnnotations(true);
    CPDConfiguration.setSystemProperties(config);

    List<File> files;
    try (Stream<Path> stream = Files.walk(SOURCE_ROOT)) {
      files = stream.filter(p -> p.toString().endsWith(".java"))
          .filter(p -> !EXCLUDES.matcher(relative(p.toString())).matches()).map(Path::toFile)
          .sorted().collect(Collectors.toList());
    }
    CPD cpd = new CPD(config);
    cpd.add(files);
    cpd.go();
    List<Match> result = new ArrayList<>();
    for (Iterator<Match> it = cpd.getMatches(); it.hasNext();) {
      result.add(it.next());
    }
    return result;
  }

  private static Map<String, Integer> readBaseline() throws IOException {
    Map<String, Integer> result = new TreeMap<>();
    if (Files.exists(BASELINE)) {
      for (String line : Files.readAllLines(BASELINE, StandardCharsets.UTF_8)) {
        line = line.trim();
        if (!line.isEmpty() && !line.startsWith("#")) {
          result.merge(line, 1, Integer::sum);
        }
      }
    }
    return result;
  }

  /** The sorted files of a duplicated block, relative to {@code org/matheclipse/core}. */
  private static String key(Match match) {
    TreeSet<String> files = new TreeSet<>();
    for (Mark mark : match) {
      files.add(relative(mark.getFilename()));
    }
    return String.join(" | ", files);
  }

  private static String describe(Match match) {
    StringBuilder buf = new StringBuilder();
    buf.append("  ").append(match.getTokenCount()).append(" tokens, ")
        .append(match.getLineCount()).append(" lines:\n");
    for (Mark mark : match) {
      buf.append("    ").append(relative(mark.getFilename())).append(':')
          .append(mark.getBeginLine()).append('\n');
    }
    return buf.toString();
  }

  private static String relative(String fileName) {
    String path = fileName.replace('\\', '/');
    int index = path.indexOf(PACKAGE_PREFIX);
    return index >= 0 ? path.substring(index + PACKAGE_PREFIX.length()) : path;
  }
}

package org.matheclipse.core.parser.golden;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.parser.ExprParserFactory;
import org.matheclipse.parser.client.operator.Operator;

/**
 * Regenerates the parser corpus and the golden recordings under
 * <code>src/test/resources/parser</code>.
 *
 * <p>
 * This is a developer tool, not a test. Run it when a parser change is <em>intended</em> to alter
 * the parse of some input, review the resulting diff line by line, and commit it together with the
 * change. Running it to make a red {@link ParserGoldenTest} go green defeats the point of having
 * the file.
 *
 * <pre>
 * mvn -pl matheclipse-core test-compile
 * mvn -pl matheclipse-core exec:java -Dexec.classpathScope=test \
 *     -Dexec.mainClass=org.matheclipse.core.parser.golden.ParserCorpusGenerator
 * </pre>
 *
 * <p>
 * Two of the three corpora are generated, the third is hand-maintained:
 * <ul>
 * <li><b>operators</b> - every operator token in the live table, put through a fixed set of
 * syntactic shapes, plus a precedence matrix over the common infix operators. Mechanically
 * exhaustive, which is what a precedence refactoring needs.
 * <li><b>doc</b> - the <code>&gt;&gt;</code> example inputs from <code>doc/functions/*.md</code>.
 * Real-world syntax, written by hand over many years.
 * <li><b>edge-cases</b> - hand-written, checked in directly and never rewritten by this tool.
 * </ul>
 */
public final class ParserCorpusGenerator {

  /** Corpus name for the generated operator-shape and precedence-matrix entries. */
  public static final String CORPUS_OPERATORS = "operators";

  /** Corpus name for the inputs harvested from the function documentation. */
  public static final String CORPUS_DOC = "doc";

  /** Corpus name for the hand-written edge cases. */
  public static final String CORPUS_EDGE_CASES = "edge-cases";

  /** All corpora the golden test runs over. */
  public static final List<String> ALL_CORPORA =
      Arrays.asList(CORPUS_OPERATORS, CORPUS_DOC, CORPUS_EDGE_CASES);

  /**
   * Documentation examples longer than this are dropped. Beyond a few hundred characters these are
   * printed matrices and long numeric literals rather than interesting syntax, and they dominate
   * the size of the golden files without adding grammar coverage.
   */
  private static final int MAX_DOC_INPUT_LENGTH = 400;

  /**
   * The tokens the pairwise precedence matrix is built over: one representative token for every
   * distinct precedence in the live operator table.
   *
   * <p>
   * This used to be a hand-picked list of common ASCII operators, and it under-reported twice.
   * Moving TensorProduct across Dot and moving the edge arrows across the whole relational band
   * both produced far smaller golden diffs than the change really had, because the pairs that would
   * have shown it were not in the matrix - the hand-picked list contained no unicode tokens at all,
   * so nothing ever probed <code></code> against <code>&amp;&amp;</code>.
   *
   * <p>
   * Deriving one token per precedence keeps the matrix quadratic in the number of distinct
   * precedence <em>levels</em> rather than in the number of tokens, which is what actually has to
   * be covered: two operators can only reorder if a precedence boundary between them moves.
   */
  private static List<String> matrixOperators() {
    Map<Integer, String> byPrecedence = new TreeMap<>();
    for (Map.Entry<String, ArrayList<Operator>> entry : ExprParserFactory.MMA_STYLE_FACTORY
        .getOperator2ListMap().entrySet()) {
      for (Operator operator : entry.getValue()) {
        // Lowest token alphabetically, so the choice does not depend on map iteration order
        byPrecedence.merge(operator.getPrecedence(), entry.getKey(),
            (a, b) -> a.compareTo(b) <= 0 ? a : b);
      }
    }
    return new ArrayList<>(byPrecedence.values());
  }

  /** Operand placeholders used to instantiate the shape templates. */
  private static final String LHS = "a";
  private static final String RHS = "b";
  private static final String THIRD = "c";

  private ParserCorpusGenerator() {}

  public static void main(String[] args) throws InterruptedException {
    Locale.setDefault(Locale.US);
    Config.FILESYSTEM_ENABLED = false;
    F.await();
    ExprParserFactory.initialize();

    Path moduleDir = Paths.get(args.length > 0 ? args[0] : ".").toAbsolutePath().normalize();
    Path resourceDir = moduleDir.resolve("src/test/resources/parser");
    Path docDir = moduleDir.resolve("../doc/functions").normalize();

    List<String> operators = generateOperatorCorpus();
    ParserGolden.writeLines(resourceDir.resolve(CORPUS_OPERATORS + ".txt"),
        withHeader("operator shapes and pairwise precedence matrix, generated by "
            + ParserCorpusGenerator.class.getSimpleName(), operators));
    System.out.println(CORPUS_OPERATORS + ": " + operators.size() + " entries");

    List<String> doc = harvestDocCorpus(docDir);
    ParserGolden.writeLines(resourceDir.resolve(CORPUS_DOC + ".txt"),
        withHeader("'>>' example inputs harvested from doc/functions/*.md", doc));
    System.out.println(CORPUS_DOC + ": " + doc.size() + " entries");

    // The edge-case corpus is hand-written; only its golden files are regenerated here.
    List<String> edgeCases = ParserGolden.readCorpus(CORPUS_EDGE_CASES);
    assertCorpusIsCurrent(resourceDir, CORPUS_EDGE_CASES, edgeCases);
    System.out.println(CORPUS_EDGE_CASES + ": " + edgeCases.size() + " entries (hand-written)");

    recordGolden(resourceDir, CORPUS_OPERATORS, operators);
    recordGolden(resourceDir, CORPUS_DOC, doc);
    recordGolden(resourceDir, CORPUS_EDGE_CASES, edgeCases);
  }

  /**
   * Fail if the corpus on the classpath is not the corpus in the source tree.
   *
   * <p>
   * {@link ParserGolden#readCorpus(String)} reads through the classloader, so it sees
   * {@code target/test-classes} - the copy Maven made at the last {@code test-compile}. Editing
   * {@code src/test/resources} and regenerating without recompiling therefore rewrites the golden
   * files from the <em>old</em> corpus, reports success, and silently drops the entries just added.
   * That has happened; the symptom is an unchanged entry count and no diff where a diff was
   * expected.
   */
  private static void assertCorpusIsCurrent(Path resourceDir, String corpusName,
      List<String> fromClasspath) {
    Path source = resourceDir.resolve(corpusName + ".txt");
    List<String> fromSource = new ArrayList<>();
    try {
      for (String line : Files.readAllLines(source, StandardCharsets.UTF_8)) {
        if (!line.isEmpty() && !line.startsWith(ParserGolden.COMMENT_PREFIX)) {
          fromSource.add(ParserGolden.unescape(line));
        }
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    if (!fromSource.equals(fromClasspath)) {
      throw new IllegalStateException(String.format(
          "%s.txt on the classpath is stale: the source has %d entries, the classpath copy has %d."
              + " Run 'mvn -pl matheclipse-core test-compile' first, or the golden files are"
              + " regenerated from the previous corpus.",
          corpusName, fromSource.size(), fromClasspath.size()));
    }
  }

  private static void recordGolden(Path resourceDir, String corpusName, List<String> corpus) {
    for (ParserFlavour flavour : ParserFlavour.values()) {
      for (ParserMode mode : ParserMode.values()) {
        List<String> golden = ParserGolden.recordAll(corpus, flavour, mode);
        Path file = resourceDir.resolve("golden")
            .resolve(ParserGolden.goldenFileName(corpusName, flavour, mode));
        ParserGolden.writeLines(file, golden);
        System.out.println("  wrote " + file.getFileName() + " (" + golden.size() + " lines)");
      }
    }
  }

  private static List<String> withHeader(String description, List<String> entries) {
    List<String> lines = new ArrayList<>(entries.size() + 2);
    lines.add(ParserGolden.COMMENT_PREFIX + " " + description);
    lines.add(ParserGolden.COMMENT_PREFIX + " DO NOT EDIT - regenerate with "
        + ParserCorpusGenerator.class.getSimpleName());
    for (String entry : entries) {
      lines.add(ParserGolden.escape(entry));
    }
    return lines;
  }

  // --------------------------------------------------------- operator corpus

  /**
   * Every operator token in the live table put through {@link #operatorShapes}, followed by the
   * pairwise precedence matrix.
   *
   * <p>
   * Each token is instantiated in prefix, infix <em>and</em> postfix position regardless of which
   * of those it actually is. The shapes which do not apply raise a syntax error, and recording that
   * error is the point: "<code>&amp;&amp;</code> is not a prefix operator" is part of the grammar,
   * and a refactoring which starts accepting it has changed the language.
   */
  private static List<String> generateOperatorCorpus() {
    Set<String> tokens =
        new TreeSet<>(ExprParserFactory.MMA_STYLE_FACTORY.getOperator2ListMap().keySet());
    Set<String> entries = new LinkedHashSet<>();
    for (String token : tokens) {
      entries.addAll(operatorShapes(token));
    }
    List<String> matrix = matrixOperators();
    for (String left : matrix) {
      for (String right : matrix) {
        entries.add(LHS + left + RHS + right + THIRD);
      }
    }
    return new ArrayList<>(entries);
  }

  /**
   * The syntactic shapes a single operator token is recorded in.
   *
   * <p>
   * Both a spaced and an unspaced form of the basic infix shape are emitted, because whitespace is
   * not neutral in this grammar: <code>a.b</code> and <code>a . b</code> reach different branches
   * of the scanner, and <code>1/.2</code> is a division by <code>0.2</code> rather than a
   * <code>ReplaceAll</code>. The digit operands exist for the same reason.
   */
  private static List<String> operatorShapes(String t) {
    return Arrays.asList(//
        // infix
        LHS + t + RHS, //
        LHS + " " + t + " " + RHS, //
        "2" + t + "3", //
        LHS + t + RHS + t + THIRD, //
        LHS + "+" + RHS + t + THIRD, //
        LHS + t + RHS + "+" + THIRD, //
        LHS + "*" + RHS + t + THIRD, //
        LHS + t + RHS + "*" + THIRD, //
        // Dot, so that a token's precedence relative to it is recorded. Without these two shapes
        // the corpus could not see TensorProduct move from one side of Dot to the other, because
        // the other mixes here are all Plus/Times/Power, which sit far from that boundary.
        LHS + "." + RHS + t + THIRD, //
        LHS + t + RHS + "." + THIRD, //
        // A comparison, which is where the relational band of the table lives
        LHS + "==" + RHS + t + THIRD, //
        LHS + t + RHS + "==" + THIRD, //
        "(" + LHS + t + RHS + ")" + t + THIRD, //
        LHS + t + "(" + RHS + t + THIRD + ")", //
        LHS + t + "-" + RHS, //
        LHS + t + RHS + "^2", //
        // prefix
        t + LHS, //
        t + LHS + "+" + RHS, //
        t + "(" + LHS + "+" + RHS + ")", //
        // postfix
        LHS + t, //
        LHS + t + "+" + RHS, //
        "(" + LHS + "+" + RHS + ")" + t);
  }

  // -------------------------------------------------------------- doc corpus

  /** The {@code >>} example inputs from the function documentation, deduplicated and sorted. */
  private static List<String> harvestDocCorpus(Path docDir) {
    if (!Files.isDirectory(docDir)) {
      throw new IllegalStateException("documentation directory not found: " + docDir
          + " - pass the matheclipse-core module directory as the first argument");
    }
    Set<String> inputs = new TreeSet<>();
    try (Stream<Path> files = Files.list(docDir)) {
      List<Path> markdown = files.filter(p -> p.getFileName().toString().endsWith(".md")).sorted()
          .collect(Collectors.toList());
      for (Path file : markdown) {
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
          if (!line.startsWith(">> ")) {
            continue;
          }
          String input = line.substring(3).trim();
          if (!input.isEmpty() && input.length() <= MAX_DOC_INPUT_LENGTH) {
            inputs.add(input);
          }
        }
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return new ArrayList<>(inputs);
  }
}

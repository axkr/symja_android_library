package org.matheclipse.core.parser.golden;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.F;
import org.matheclipse.parser.client.ParserConfig;

/**
 * Pins down the tree the parser produces for every corpus entry, in every parser mode.
 *
 * <p>
 * This test does not encode anyone's opinion of what the right parse is - it records what the
 * parser did at the time the golden files were generated. Its value is entirely in the diff: a
 * refactoring which is meant to preserve behaviour has to leave all twelve files byte-identical,
 * and a change which is meant to alter a parse has to show exactly which inputs it altered.
 *
 * <p>
 * When this test fails, the question to ask is "did I mean to change this input?" - not "how do I
 * make it green?". Regenerate with {@link ParserCorpusGenerator} only after reading the diff.
 */
public class ParserGoldenTest {

  /** How many differing entries are quoted per mode before the report is truncated. */
  private static final int MAX_REPORTED_DIFFERENCES = 15;

  static {
    Config.FILESYSTEM_ENABLED = false;
    try {
      F.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("interrupted while initializing Symja", e);
    }
  }

  @Test
  public void operatorCorpusRecordingIsUnchanged() {
    assertGoldenUnchanged(ParserCorpusGenerator.CORPUS_OPERATORS);
  }

  @Test
  public void documentationCorpusRecordingIsUnchanged() {
    assertGoldenUnchanged(ParserCorpusGenerator.CORPUS_DOC);
  }

  @Test
  public void edgeCaseCorpusRecordingIsUnchanged() {
    assertGoldenUnchanged(ParserCorpusGenerator.CORPUS_EDGE_CASES);
  }

  /** Compare the recording of one corpus against its golden file, for every parser and mode. */
  private static void assertGoldenUnchanged(String corpusName) {
    List<String> corpus = ParserGolden.readCorpus(corpusName);
    StringBuilder report = new StringBuilder();
    for (ParserFlavour flavour : ParserFlavour.values()) {
      for (ParserMode mode : ParserMode.values()) {
        appendDifferences(report, corpusName, flavour, mode, corpus);
      }
    }
    if (report.length() > 0) {
      report.append("\nIf these changes are intended, regenerate the golden files with "
          + "ParserCorpusGenerator and commit the diff together with the parser change.");
      fail(report.toString());
    }
  }

  private static void appendDifferences(StringBuilder report, String corpusName,
      ParserFlavour flavour, ParserMode mode, List<String> corpus) {
    List<String> expected = ParserGolden.readGolden(corpusName, flavour, mode);
    List<String> actual = ParserGolden.recordAll(corpus, flavour, mode);
    if (expected.equals(actual)) {
      return;
    }

    report.append('\n').append(ParserGolden.goldenFileName(corpusName, flavour, mode))
        .append(" is out of date.\n");
    if (expected.size() != actual.size()) {
      report.append("  golden has ").append(expected.size()).append(" lines, the corpus has ")
          .append(actual.size()).append(" entries - regenerate with ParserCorpusGenerator\n");
    }

    int differences = 0;
    int limit = Math.min(expected.size(), actual.size());
    for (int i = 0; i < limit; i++) {
      if (expected.get(i).equals(actual.get(i))) {
        continue;
      }
      differences++;
      if (differences <= MAX_REPORTED_DIFFERENCES) {
        report.append("\n  input:    ").append(inputOf(actual.get(i)));
        report.append("\n  expected: ").append(resultOf(expected.get(i)));
        report.append("\n  actual:   ").append(resultOf(actual.get(i))).append('\n');
      }
    }
    if (differences > MAX_REPORTED_DIFFERENCES) {
      report.append("\n  ... and ").append(differences - MAX_REPORTED_DIFFERENCES)
          .append(" more differing entries\n");
    }
  }

  /**
   * Every corpus entry has to survive the escape/unescape round trip, otherwise a hand-written
   * entry silently tests something other than what it looks like.
   */
  @Test
  public void corpusEncodingRoundTrips() {
    for (String corpusName : ParserCorpusGenerator.ALL_CORPORA) {
      for (String input : ParserGolden.readCorpus(corpusName)) {
        assertEquals(input, ParserGolden.unescape(ParserGolden.escape(input)),
            corpusName + ": corpus entry does not survive the escape round trip");
      }
    }
  }

  @Test
  public void escapeRoundTripsControlCharacters() {
    String[] samples = {"", "a", "a\\b", "a\\\\b", "a\nb", "a\r\nb", "a\tb", "\\[Alpha]",
        "\"a\\\"b\"", "#|not a comment"};
    for (String sample : samples) {
      String escaped = ParserGolden.escape(sample);
      assertEquals(-1, escaped.indexOf('\n'), "escaped form must be a single line: " + escaped);
      assertEquals(-1, escaped.indexOf('\t'), "escaped form must not contain a tab: " + escaped);
      assertEquals(sample, ParserGolden.unescape(escaped));
    }
  }

  /**
   * The empty input cannot be a corpus entry - a blank line is skipped by the reader - so it is
   * checked here instead. It is a real case: the console hands the parser an empty string whenever
   * the user submits a blank line or a line holding only a comment.
   */
  @Test
  public void emptyInputParsesToNull() {
    for (ParserFlavour flavour : ParserFlavour.values()) {
      for (ParserMode mode : ParserMode.values()) {
        mode.run(() -> {
          ParserFlavour.Recorder recorder = flavour.newRecorder(mode);
          assertEquals("Null", recorder.parse(""), "empty input in " + flavour + "/" + mode);
          assertEquals("Null", recorder.parse("(* only a comment *)"),
              "comment-only input in " + flavour + "/" + mode);
        });
      }
    }
  }

  /** The corpora have to actually contain something, or the comparisons pass vacuously. */
  @Test
  public void corporaAreNotEmpty() {
    for (String corpusName : ParserCorpusGenerator.ALL_CORPORA) {
      assertTrue(ParserGolden.readCorpus(corpusName).size() > 100,
          corpusName + " corpus is suspiciously small");
    }
  }

  /**
   * The mode switches are global mutable state; leaking one would silently change every test that
   * runs after this class.
   */
  @Test
  public void modesRestoreGlobalParserConfiguration() {
    boolean explicitTimes = ParserConfig.EXPLICIT_TIMES_OPERATOR;
    boolean dominantTimes = ParserConfig.DOMINANT_IMPLICIT_TIMES;
    for (ParserFlavour flavour : ParserFlavour.values()) {
      for (ParserMode mode : ParserMode.values()) {
        // "2*x" rather than "2x": implicit multiplication is a syntax error in EXPLICIT_TIMES, and
        // this test is about the restore, not about what parses
        mode.run(() -> flavour.newRecorder(mode).parse("2*x"));
      }
    }
    assertEquals(explicitTimes, ParserConfig.EXPLICIT_TIMES_OPERATOR);
    assertEquals(dominantTimes, ParserConfig.DOMINANT_IMPLICIT_TIMES);
  }

  private static String inputOf(String goldenLine) {
    int tab = goldenLine.indexOf('\t');
    return tab < 0 ? goldenLine : goldenLine.substring(0, tab);
  }

  private static String resultOf(String goldenLine) {
    int tab = goldenLine.indexOf('\t');
    return tab < 0 ? goldenLine : goldenLine.substring(tab + 1);
  }
}

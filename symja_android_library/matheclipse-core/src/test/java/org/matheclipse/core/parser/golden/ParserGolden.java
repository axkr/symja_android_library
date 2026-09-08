package org.matheclipse.core.parser.golden;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Reads the parser corpus, records what the parser makes of each entry, and reads and writes the
 * golden files those recordings are compared against.
 *
 * <p>
 * A recording is deliberately taken at <em>parse</em> level - {@link IExpr#fullFormString()} of the
 * unevaluated result - not at evaluation level. Evaluation hides most of what a parser refactoring
 * can break: <code>2*x</code> and <code>x*2</code> evaluate alike, associativity of a flat operator
 * stops being observable once the arguments are sorted, and a wrong precedence between two
 * operators of equal precedence class often still produces a numerically equal answer. The existing
 * end-to-end tests already cover evaluation; this corpus exists to pin down the shape of the tree
 * the parser hands over.
 *
 * <p>
 * Failures are recorded too, and are as much part of the contract as the successes: a syntax error
 * which stops being raised is a grammar change.
 */
public final class ParserGolden {

  /** Classpath directory holding the corpus files. */
  private static final String CORPUS_RESOURCE_DIR = "/parser/";

  /** Classpath directory holding the recorded golden files. */
  private static final String GOLDEN_RESOURCE_DIR = "/parser/golden/";

  private ParserGolden() {}

  // ---------------------------------------------------------------- recording

  /**
   * Parse <code>input</code> and encode the outcome as a single golden line.
   *
   * @return the recorded form of the parsed expression, or a <code>!</code>-prefixed marker
   *         describing the failure
   */
  public static String record(String input, ParserFlavour.Recorder recorder) {
    try {
      String parsed = recorder.parse(input);
      if (parsed == null) {
        return "!null";
      }
      return parsed;
    } catch (SyntaxError e) {
      // getError() is the bare message; getMessage() additionally renders the offending line and a
      // caret under it, which would make the golden files depend on line layout for no extra
      // signal. The column is kept because "the error moved" is a real regression.
      return "!syntax@" + e.getColumnIndex() + ": " + e.getError();
    } catch (StackOverflowError e) {
      return "!StackOverflowError";
    } catch (RuntimeException e) {
      return "!" + e.getClass().getSimpleName() + ": " + e.getMessage();
    }
  }

  /** Record every corpus entry with the given parser in the given mode, one line per entry. */
  public static List<String> recordAll(List<String> corpus, ParserFlavour flavour,
      ParserMode mode) {
    List<String> golden = new ArrayList<>(corpus.size());
    mode.run(() -> {
      ParserFlavour.Recorder recorder = flavour.newRecorder(mode);
      for (String input : corpus) {
        golden.add(escape(input) + '\t' + escape(record(input, recorder)));
      }
    });
    return golden;
  }

  // ------------------------------------------------------------------ file IO

  /** Name of the golden file for a corpus/parser/mode combination. */
  public static String goldenFileName(String corpusName, ParserFlavour flavour, ParserMode mode) {
    return corpusName + "." + flavour.id() + "." + mode.id() + ".txt";
  }

  /**
   * A corpus comment line. Not {@code #}, which is the {@code Slot} token and therefore a corpus
   * entry in its own right - as are {@code #1}, {@code ##} and {@code # + 1}. {@code #|} is a
   * syntax error in Symja, so no entry can begin with it.
   */
  public static final String COMMENT_PREFIX = "#|";

  /**
   * Read a corpus file from the test classpath and unescape each entry. Blank lines and
   * {@link #COMMENT_PREFIX} lines are skipped; the empty input is covered by a dedicated test
   * rather than by a corpus entry no line-based format could represent unambiguously.
   */
  public static List<String> readCorpus(String corpusName) {
    return readResourceLines(CORPUS_RESOURCE_DIR + corpusName + ".txt", true);
  }

  /** Read a golden file from the test classpath. Every line is significant. */
  public static List<String> readGolden(String corpusName, ParserFlavour flavour, ParserMode mode) {
    return readResourceLines(GOLDEN_RESOURCE_DIR + goldenFileName(corpusName, flavour, mode),
        false);
  }

  private static List<String> readResourceLines(String resource, boolean isCorpus) {
    InputStream in = ParserGolden.class.getResourceAsStream(resource);
    if (in == null) {
      throw new IllegalStateException(
          "missing test resource: " + resource + " - run ParserCorpusGenerator to create it");
    }
    List<String> lines = new ArrayList<>();
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (!isCorpus) {
          lines.add(line);
          continue;
        }
        if (line.isEmpty() || line.startsWith(COMMENT_PREFIX)) {
          continue;
        }
        lines.add(unescape(line));
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return lines;
  }

  /** Write one line per list entry, LF-terminated, UTF-8, creating parent directories. */
  public static void writeLines(Path file, List<String> lines) {
    try {
      Files.createDirectories(file.getParent());
      StringBuilder buf = new StringBuilder(lines.size() * 80);
      for (String line : lines) {
        buf.append(line).append('\n');
      }
      Files.write(file, buf.toString().getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  // ------------------------------------------------------------------ escaping

  /**
   * Encode a string so that it occupies exactly one line and contains no tab. Inputs legitimately
   * contain newlines (multi-line expressions, line continuations) and tabs, and error messages
   * contain both, so neither can be used as a separator unescaped.
   */
  public static String escape(String s) {
    StringBuilder buf = new StringBuilder(s.length() + 8);
    for (int i = 0; i < s.length(); i++) {
      char ch = s.charAt(i);
      switch (ch) {
        case '\\':
          buf.append("\\\\");
          break;
        case '\n':
          buf.append("\\n");
          break;
        case '\r':
          buf.append("\\r");
          break;
        case '\t':
          buf.append("\\t");
          break;
        default:
          buf.append(ch);
          break;
      }
    }
    return buf.toString();
  }

  /** Inverse of {@link #escape(String)}. */
  public static String unescape(String s) {
    if (s.indexOf('\\') < 0) {
      return s;
    }
    StringBuilder buf = new StringBuilder(s.length());
    for (int i = 0; i < s.length(); i++) {
      char ch = s.charAt(i);
      if (ch != '\\' || i + 1 >= s.length()) {
        buf.append(ch);
        continue;
      }
      char next = s.charAt(++i);
      switch (next) {
        case '\\':
          buf.append('\\');
          break;
        case 'n':
          buf.append('\n');
          break;
        case 'r':
          buf.append('\r');
          break;
        case 't':
          buf.append('\t');
          break;
        default:
          // not an escape we produced - keep it verbatim, so that a Symja string literal such as
          // "\[Alpha]" survives a round trip through the corpus file
          buf.append(ch).append(next);
          break;
      }
    }
    return buf.toString();
  }
}

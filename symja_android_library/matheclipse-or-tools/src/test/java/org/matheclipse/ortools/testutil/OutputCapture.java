package org.matheclipse.ortools.testutil;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Runs the <code>main</code> method of a ported OR-Tools example or sample and returns everything
 * it printed: the text written to <code>System.out</code> and <code>System.err</code> and the
 * messages logged through <code>java.util.logging</code>, which most of the routing samples use.
 */
public final class OutputCapture {

  /** A <code>main</code> method or any other code which prints. */
  @FunctionalInterface
  public interface Program {
    void run() throws Exception;
  }

  private static final Object LOCK = new Object();

  private OutputCapture() {}

  /**
   * Run <code>program</code> with <code>System.out</code>, <code>System.err</code> and the root
   * logger redirected.
   *
   * @return the captured text, lines separated by <code>\n</code>
   */
  public static String run(Program program) {
    // one capture at a time: the redirected streams are global
    synchronized (LOCK) {
      return runCaptured(program);
    }
  }

  private static String runCaptured(Program program) {
    final PrintStream oldOut = System.out;
    final PrintStream oldErr = System.err;
    final Locale oldLocale = Locale.getDefault();
    final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    final PrintStream capture = new PrintStream(buffer, true, StandardCharsets.UTF_8);
    final Logger root = Logger.getLogger("");
    final SimpleFormatter formatter = new SimpleFormatter();
    final Handler handler = new Handler() {
      @Override
      public void publish(LogRecord record) {
        if (record.getLevel().intValue() >= Level.INFO.intValue()) {
          capture.println(formatter.formatMessage(record));
        }
      }

      @Override
      public void flush() {
        capture.flush();
      }

      @Override
      public void close() {}
    };
    // the console handler of the root logger would duplicate every message on the real stderr
    final Handler[] oldHandlers = root.getHandlers();
    try {
      Locale.setDefault(Locale.US);
      for (Handler h : oldHandlers) {
        root.removeHandler(h);
      }
      root.addHandler(handler);
      System.setOut(capture);
      System.setErr(capture);
      program.run();
    } catch (Exception e) {
      capture.flush();
      throw new AssertionError(
          "program failed: " + e + "\noutput:\n" + buffer.toString(StandardCharsets.UTF_8), e);
    } finally {
      capture.flush();
      System.setOut(oldOut);
      System.setErr(oldErr);
      root.removeHandler(handler);
      for (Handler h : oldHandlers) {
        root.addHandler(h);
      }
      Locale.setDefault(oldLocale);
    }
    return buffer.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
  }

  /** Assert that <code>output</code> contains <code>expected</code>. */
  public static void assertContains(String output, String expected) {
    assertTrue(output.contains(expected),
        () -> "expected <" + expected + "> in output:\n" + output);
  }

  /** Assert that <code>output</code> doesn't contain <code>unexpected</code>. */
  public static void assertNotContains(String output, String unexpected) {
    assertTrue(!output.contains(unexpected),
        () -> "unexpected <" + unexpected + "> in output:\n" + output);
  }

  /** Assert that a part of <code>output</code> matches <code>regex</code> and return the match. */
  public static Matcher assertFind(String output, String regex) {
    Matcher m = Pattern.compile(regex, Pattern.MULTILINE).matcher(output);
    if (!m.find()) {
      fail("expected a match of <" + regex + "> in output:\n" + output);
    }
    return m;
  }

  /** The number of non-overlapping matches of <code>regex</code> in <code>output</code>. */
  public static int count(String output, String regex) {
    Matcher m = Pattern.compile(regex, Pattern.MULTILINE).matcher(output);
    int n = 0;
    while (m.find()) {
      n++;
    }
    return n;
  }

  /** The numbers following each occurrence of <code>prefix</code>, in order. */
  public static List<Double> numbersAfter(String output, String prefix) {
    Matcher m = Pattern.compile(Pattern.quote(prefix) + "\\s*(-?[0-9]+(?:\\.[0-9]+)?(?:[eE][-+]?[0-9]+)?)")
        .matcher(output);
    List<Double> result = new ArrayList<>();
    while (m.find()) {
      result.add(Double.parseDouble(m.group(1)));
    }
    return result;
  }

  /** The first number following <code>prefix</code>; fails if there is none. */
  public static double numberAfter(String output, String prefix) {
    List<Double> numbers = numbersAfter(output, prefix);
    if (numbers.isEmpty()) {
      fail("expected a number after <" + prefix + "> in output:\n" + output);
    }
    return numbers.get(0);
  }
}

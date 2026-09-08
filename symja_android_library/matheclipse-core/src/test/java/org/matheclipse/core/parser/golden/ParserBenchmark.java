package org.matheclipse.core.parser.golden;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;

/**
 * Measures parser throughput over the same corpora the golden files are recorded from.
 *
 * <p>
 * This is a plain timing harness, not JMH - it deliberately adds no dependency and no annotation
 * processor to the shared build. It warms up, runs several measured repetitions and reports the
 * median, which is enough to size the effect of an allocation or lookup change (those move
 * throughput by tens of percent). It is <em>not</em> enough to defend a claim of a few percent:
 * there is no forking, no dead-code-elimination barrier beyond the checksum below, and no control
 * over JIT compilation between repetitions. If a change lands in that range, measure it with JMH
 * before believing it.
 *
 * <p>
 * Two workloads are timed for each corpus. <b>engine</b> goes through
 * {@link EvalEngine#parse(String)}, which allocates a fresh {@link ExprParser} per call - this is
 * what the console and every embedding actually do. <b>reused</b> keeps one parser and only
 * measures parsing, which isolates the scanning and tree building from the per-call setup.
 *
 * <pre>
 * mvn -pl matheclipse-core test-compile
 * java -cp &lt;test classpath&gt; org.matheclipse.core.parser.golden.ParserBenchmark
 * </pre>
 */
public final class ParserBenchmark {

  private static final int WARMUP_REPETITIONS = 10;
  private static final int MEASURED_REPETITIONS = 21;

  /**
   * Target duration of a single timed sample. Each sample runs the workload's inputs as many times
   * as needed to fill this, so that a workload holding one big expression is not timed over a
   * single parse - which is what made the first version of this harness report differences that
   * were pure noise.
   */
  private static final long TARGET_SAMPLE_NANOS = 100_000_000L;

  /**
   * Accumulates something derived from every parse result. Without this the JIT is entitled to
   * notice that the results are unused and delete the work being measured.
   */
  private static long checksum;

  private ParserBenchmark() {}

  public static void main(String[] args) throws InterruptedException {
    Locale.setDefault(Locale.US);
    Config.FILESYSTEM_ENABLED = false;
    F.await();

    // Optional name filter. Running one workload per JVM matters: every workload JIT-compiles the
    // same parser methods, so whichever runs first decides the profile the rest inherit. A
    // difference seen in a full run has to be reproduced in an isolated run before it is believed.
    List<String> only = Arrays.asList(args);
    List<Workload> workloads = new ArrayList<>();
    for (String corpusName : ParserCorpusGenerator.ALL_CORPORA) {
      addIfSelected(workloads, only, corpusName, parseableEntries(corpusName));
    }
    addIfSelected(workloads, only, "flat-sum-5000", Arrays.asList(flatSum(5000)));
    addIfSelected(workloads, only, "flat-product-5000", Arrays.asList(flatProduct(5000)));
    addIfSelected(workloads, only, "nested-500", Arrays.asList(nested(500)));

    System.out.printf("%-20s %8s %20s %20s%n", "workload", "entries", "engine us/op",
        "reused us/op");
    System.out.println("-".repeat(72));
    for (Workload workload : workloads) {
      double[] engine = measure(workload, true);
      double[] reused = measure(workload, false);
      System.out.printf("%-20s %8d %13.4f +-%-5.4f %13.4f +-%-5.4f%n", workload.name,
          workload.inputs.size(), median(engine), halfInterquartileRange(engine), median(reused),
          halfInterquartileRange(reused));
    }
    // printed so that the checksum cannot be optimized away as unused
    System.out.println("\nchecksum: " + checksum);
  }

  private static void addIfSelected(List<Workload> workloads, List<String> only, String name,
      List<String> inputs) {
    if (only.isEmpty() || only.contains(name)) {
      workloads.add(new Workload(name, inputs));
    }
  }

  /**
   * Corpus entries which parse without error in the default mode.
   *
   * <p>
   * The corpora intentionally contain syntax errors, but throwing an exception per entry would
   * measure the cost of filling in stack traces rather than the cost of parsing.
   */
  private static List<String> parseableEntries(String corpusName) {
    EvalEngine engine = ParserMode.RELAXED.newEngine();
    ExprParser parser = ParserMode.RELAXED.newParser(engine);
    List<String> parseable = new ArrayList<>();
    for (String input : ParserGolden.readCorpus(corpusName)) {
      try {
        parser.parse(input);
        parseable.add(input);
      } catch (RuntimeException | StackOverflowError e) {
        // not a benchmark input; SyntaxError is itself a RuntimeException
      }
    }
    return parseable;
  }

  private static double[] measure(Workload workload, boolean freshParserPerCall) {
    EvalEngine engine = ParserMode.RELAXED.newEngine();
    EvalEngine.set(engine);
    ExprParser shared = freshParserPerCall ? null : ParserMode.RELAXED.newParser(engine);

    // One pass to size the sample, then warm up at the real size so the JIT sees the shape of the
    // measured loop.
    long start = System.nanoTime();
    runPasses(workload, engine, shared, 1);
    long nanosPerPass = Math.max(1L, System.nanoTime() - start);
    int passes = (int) Math.max(1L, Math.min(100_000L, TARGET_SAMPLE_NANOS / nanosPerPass));

    for (int i = 0; i < WARMUP_REPETITIONS; i++) {
      runPasses(workload, engine, shared, passes);
    }
    double[] microsecondsPerOp = new double[MEASURED_REPETITIONS];
    for (int i = 0; i < MEASURED_REPETITIONS; i++) {
      long sampleStart = System.nanoTime();
      int operations = runPasses(workload, engine, shared, passes);
      long elapsed = System.nanoTime() - sampleStart;
      microsecondsPerOp[i] = elapsed / 1_000.0 / operations;
    }
    return microsecondsPerOp;
  }

  private static int runPasses(Workload workload, EvalEngine engine, ExprParser shared,
      int passes) {
    int operations = 0;
    for (int pass = 0; pass < passes; pass++) {
      for (String input : workload.inputs) {
        IExpr result = shared == null ? engine.parse(input) : shared.parse(input);
        checksum += result.hashCode();
        operations++;
      }
    }
    return operations;
  }

  /**
   * Half the distance between the 25th and 75th percentile, reported so that a difference between
   * two runs can be read against the spread within a run rather than taken at face value.
   */
  private static double halfInterquartileRange(double[] values) {
    double[] sorted = values.clone();
    Arrays.sort(sorted);
    return (sorted[sorted.length * 3 / 4] - sorted[sorted.length / 4]) / 2.0;
  }

  private static double median(double[] values) {
    double[] sorted = values.clone();
    Arrays.sort(sorted);
    return sorted[sorted.length / 2];
  }

  /** {@code a1+a2+...+aN} - a single flat sum, the shape that makes operator recursion deep. */
  private static String flatSum(int terms) {
    StringBuilder buf = new StringBuilder(terms * 4);
    for (int i = 1; i <= terms; i++) {
      if (i > 1) {
        buf.append('+');
      }
      buf.append('a').append(i);
    }
    return buf.toString();
  }

  /** {@code a1*a2*...*aN}, to separate flat Times from flat Plus. */
  private static String flatProduct(int terms) {
    return flatSum(terms).replace('+', '*');
  }

  /** {@code f(f(f(...a...)))} - bracket nesting, which stays recursive in any design. */
  private static String nested(int depth) {
    StringBuilder buf = new StringBuilder(depth * 3);
    for (int i = 0; i < depth; i++) {
      buf.append("f(");
    }
    buf.append('a');
    for (int i = 0; i < depth; i++) {
      buf.append(')');
    }
    return buf.toString();
  }

  private static final class Workload {
    final String name;
    final List<String> inputs;

    Workload(String name, List<String> inputs) {
      this.name = name;
      this.inputs = inputs;
    }
  }
}

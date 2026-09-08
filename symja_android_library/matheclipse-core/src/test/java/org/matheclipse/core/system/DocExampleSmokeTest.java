package org.matheclipse.core.system;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Evaluates every <code>&gt;&gt;</code> example in <code>doc/functions/*.md</code> and writes
 * <code>input TAB result</code> lines to the file named by the <code>docExampleOut</code> system
 * property.
 *
 * <p>
 * This is a differential harness, not an assertion suite: run it once per revision and diff the two
 * output files. It is skipped unless <code>docExampleOut</code> is set, so it costs nothing in a
 * normal build.
 */
public class DocExampleSmokeTest {

  @BeforeEach
  public void setUp() throws Exception {
    F.await();
  }

  @Test
  public void dumpDocExampleResults() throws IOException {
    String out = System.getProperty("docExampleOut");
    if (out == null) {
      return;
    }
    Path docDir = Paths.get("..", "doc", "functions");
    if (!Files.isDirectory(docDir)) {
      throw new IOException("doc/functions not found at " + docDir.toAbsolutePath());
    }
    List<String> inputs = new ArrayList<>();
    try (Stream<Path> files = Files.list(docDir)) {
      files.filter(p -> p.toString().endsWith(".md")).sorted().forEach(p -> {
        try {
          for (String line : Files.readAllLines(p, StandardCharsets.UTF_8)) {
            String trimmed = line.trim();
            if (trimmed.startsWith(">> ")) {
              inputs.add(trimmed.substring(3).trim());
            }
          }
        } catch (IOException | RuntimeException rex) {
          // unreadable file - skip
        }
      });
    }

    PrintStream originalOut = System.out;
    PrintStream originalErr = System.err;
    PrintStream sink =
        new PrintStream(java.io.OutputStream.nullOutputStream(), true, StandardCharsets.UTF_8);
    StringBuilder buf = new StringBuilder();
    try {
      System.setOut(sink);
      System.setErr(sink);
      for (String input : inputs) {
        String result;
        try {
          EvalEngine engine = new EvalEngine(true);
          engine.setQuietMode(true);
          ExprEvaluator evaluator = new ExprEvaluator(engine, true, (short) 20);
          IExpr expr = evaluator.eval(input);
          result = expr == null ? "<null>" : expr.toString();
        } catch (Throwable t) {
          result = "<" + t.getClass().getSimpleName() + ">";
        }
        if (result.length() > 400) {
          result = result.substring(0, 400) + "...";
        }
        buf.append(input.replace('\t', ' ')).append('\t')
            .append(result.replace('\n', ' ').replace('\t', ' ')).append('\n');
      }
    } finally {
      System.setOut(originalOut);
      System.setErr(originalErr);
    }
    Files.write(Paths.get(out), buf.toString().getBytes(StandardCharsets.UTF_8));
    originalOut.println("DocExampleSmokeTest: wrote " + inputs.size() + " results to " + out);
  }
}

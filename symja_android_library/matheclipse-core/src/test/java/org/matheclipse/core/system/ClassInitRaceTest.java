package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.File;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

/**
 * Two threads which start Symja at the same time must not wait for each other. The static
 * initializers of {@code F} and {@code ContextPath} each needed the other: F's created an
 * EvalEngine, which needs ContextPath, and ContextPath's built a table of F's functions. Every run
 * of {@link ClassInitRace} hung before; the classes can only be initialized once per JVM, so each
 * run gets a new one.
 */
public class ClassInitRaceTest {

  @Test
  public void testTwoThreadsStartSymjaAtTheSameTime() throws Exception {
    String java = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
    String classPath = System.getProperty("java.class.path");
    for (int i = 0; i < 3; i++) {
      Process process =
          new ProcessBuilder(java, "-cp", classPath, ClassInitRace.class.getName())
              .redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.DISCARD).start();
      boolean finished = process.waitFor(90, TimeUnit.SECONDS);
      if (!finished) {
        process.destroyForcibly();
      }
      assertTrue(finished, "the child JVM did not end");
      assertEquals(0, process.exitValue(), "the two threads waited for each other");
    }
  }
}

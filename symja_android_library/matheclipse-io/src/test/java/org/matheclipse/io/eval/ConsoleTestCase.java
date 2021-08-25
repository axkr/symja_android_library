package org.matheclipse.io.eval;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import junit.framework.TestCase;

/** Test <code>org.matheclipse.core.eval.Console</code> app. */
public class ConsoleTestCase extends TestCase {
  Console console;

  public ConsoleTestCase(String name) {
    super(name);
  }

  /** The JUnit setup method */
  @Override
  protected void setUp() {
    try {
      console = new Console();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void check(String[] args, String result) {
    StringWriter outWriter = new StringWriter();
    // writer for standard outrput
    PrintWriter stdout = new PrintWriter(outWriter, true);
    // writer for errors
    PrintWriter stderr =
        new PrintWriter(new OutputStreamWriter(System.err, StandardCharsets.UTF_8), true);

    Console.runConsole(args, stdout, stderr);

    assertEquals(
        outWriter.toString(), //
        result);
    stdout.close();
    stderr.close();
  }

  public void test001() {
    String[] args = new String[] {"-c", "D(sin(x)^3,x)"};
    check(args, "3*Cos(x)*Sin(x)^2");
  }

  public void test002() {
    String[] args = new String[] {"-f", "Factorial", "-a", "20"};
    check(args, "2432902008176640000");
  }

  public void test003() {
    String[] args = new String[] {"-c", "f(x_,y_):={x,y}; f(a,b)"};
    check(args, "{a,b}");
  }

  public void testDoc() {
    assertEquals(
        "Sin, Sinc, SingularValueDecomposition, Sinh, SinIntegral, SinhIntegral\n",
        console.interpreter("?Sin*"));
  }

  public void testMissingDoc() {
    assertEquals(
        "Sin, Sinc, SingularValueDecomposition, Sinh, SinIntegral, SinhIntegral\n",
        console.interpreter("?sin*"));
  }
}

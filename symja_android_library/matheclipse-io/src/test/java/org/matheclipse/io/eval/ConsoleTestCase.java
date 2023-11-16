package org.matheclipse.io.eval;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/** Test <code>org.matheclipse.core.eval.Console</code> app. */
public class ConsoleTestCase  {
  Console console;

  /** The JUnit setup method */
  @Before
  public void setUp() {
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

    assertEquals(outWriter.toString(), //
        result);
    stdout.close();
    stderr.close();
  }

  @Test
  public void test001() {
    String[] args = new String[] {"-c", "D(sin(x)^3,x)"};
    check(args, "3*Cos(x)*Sin(x)^2");
  }

  @Test
  public void test002() {
    String[] args = new String[] {"-f", "Factorial", "-a", "20"};
    check(args, "2432902008176640000");
  }

  @Test
  public void test003() {
    String[] args = new String[] {"-c", "f(x_,y_):={x,y}; f(a,b)"};
    check(args, "{a,b}");
  }

  // @Test
  // public void testDoc() {
  // assertEquals(
  // "Sin, Sinc, SingularValueDecomposition, SingularValueList, Sinh, SinIntegral, SinhIntegral\n",
  // console.interpreter("?Sin*"));
  // }
  //
  // @Test
  // public void testMissingDoc() {
  // assertEquals(
  // "Sin, Sinc, SingularValueDecomposition, SingularValueList, Sinh, SinIntegral, SinhIntegral\n",
  // console.interpreter("?sin*"));
  // }
}

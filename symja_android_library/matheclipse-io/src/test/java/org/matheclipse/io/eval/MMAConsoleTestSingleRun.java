package org.matheclipse.io.eval;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.matheclipse.parser.client.ParserConfig;
import junit.framework.TestCase;

import static org.junit.Assert.assertEquals;

/**
 * Test <code>org.matheclipse.core.eval.MMAConsole</code> app.
 *
 * <p>
 * Configure <code>org.matheclipse.parser.client.ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = false;
 * </code> in the sources for running Symja in MMA mode, before running this test!
 */
@RunWith(JUnit4.class)
public class MMAConsoleTestSingleRun  {
  MMAConsole console;

  public MMAConsoleTestSingleRun() {
    if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
      System.out.println(
          "MMAConsoleTestSingleRun - configure org.matheclipse.parser.client.ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = false"
              + //
              " in the sources for running Symja in MMA mode, before running this test!");
    }
  }

  private void check(String[] args, String result) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(baos);
    PrintStream old = System.out;
    System.setOut(ps);

    MMAConsole.main(args);

    System.out.flush();
    System.setOut(old);
    assertEquals(baos.toString(), //
        result);
  }

  /** The JUnit setup method */
  @Before
  public void setUp() {
    try {
      console = new MMAConsole();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void test001() {
    String[] args = new String[] {"-c", "D[Sin[x]^3,x]"};
    check(args, "3*Cos[x]*Sin[x]^2");
  }

  @Test
  public void test002() {
    String[] args = new String[] {"-f", "Factorial", "-a", "20"};
    check(args, "2432902008176640000");
  }

  @Test
  public void test003() {
    String[] args = new String[] {"-c", "f[x_,y_]:={x,y}; f[a,b]"};
    check(args, "{a,b}");
  }

  @Test
  public void test004() {
    String[] args = new String[] {"-c", "Integrate[Sin[x]^2,x]"};
    check(args, "x/2-1/2*Cos[x]*Sin[x]");
  }

  @Test
  public void test005() {
    String[] args = new String[] {"-c", "FullSimplify[Cosh[x]+Sinh[x]]"};
    check(args, "E^x");
  }

  @Test
  public void testInput() {
    assertEquals("f[x]", console.interpreter("f[x]"));
    assertEquals("x/2-1/2*Cos[x]*Sin[x]", console.interpreter("Integrate[Sin[x]^2,x]"));
  }

  @Test
  public void testDoc() {
    assertEquals("Sin, Sinc, SingularValueDecomposition, Sinh, SinIntegral, SinhIntegral",
        console.interpreter("?Sin*"));
  }

  @Test
  public void testMissingDoc() {
    assertEquals("Missing[UnknownSymbol,?sin*]", console.interpreter("?sin*"));
  }
}

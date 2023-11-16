package org.matheclipse.core.system;

import java.io.PrintStream;
import java.io.StringWriter;

import org.junit.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.util.WriterOutputStream;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

import static org.junit.Assert.assertEquals;

/** Test F.symjify() */
public class SymjifyTestCase extends ExprEvaluatorTestCase {
   @Test
   public void test787_1() {
    // issue #787
    IExpr minus = F.symjify("y").minus(F.ZZ(1));
     assertEquals("-1+y", //
     minus.toString());
    IExpr power = F.symjify("z").power(minus);
     assertEquals("z^(-1+y)", //
         power.toString());
     assertEquals("Times(x, Power(z, Plus(-1, y)))", //
     F.symjify("x").multiply(power).fullFormString());

     assertEquals("x/z^(1-y)", //
        F.symjify("x").multiply(power).toString());
     assertEquals("Times(x, Power(z, Plus(-1, y)))", //
         F.symjify("x").multiply(power).fullFormString());
  }

   @Test
   public void test787_2() {
    // issue #787
    IExpr minus = F.symjify("y").minus(F.ZZ(1));
    assertEquals("-1+y", //
        minus.toString());
    IExpr power = F.symjify("z").power(minus);
    assertEquals("z^(-1+y)", //
        power.toString());
    assertEquals("Times(x, Power(z, Plus(-1, y)))", //
        F.symjify("x").multiply(power).fullFormString());

    assertEquals("x*z^(1-y)", //
        F.symjify("x").divide(power).toString());
    assertEquals("Times(x, Power(z, Plus(1, Times(-1, y))))", //
        F.symjify("x").divide(power).fullFormString());
  }

   @Test
   public void test001() {
    IExpr expr = F.symjify("(a+(b+c))");
    assertEquals(expr.fullFormString(), "Plus(a, b, c)");
    assertEquals(expr.toString(), "a+b+c");
  }

   @Test
   public void test002() {
    IExpr expr = F.symjify(new int[] {1, 2, 3});
    assertEquals(expr.fullFormString(), "List(1, 2, 3)");
    assertEquals(expr.toString(), "{1,2,3}");
  }

   @Test
   public void test003() {
    IExpr expr = F.symjify(new double[] {1.0, 2.1, 3.5});
    assertEquals(expr.fullFormString(), "List(1.0`, 2.1`, 3.5`)");
    assertEquals(expr.toString(), "{1.0,2.1,3.5}");
  }

   @Test
   public void test004() {
    IExpr expr = F.symjify(new double[][] {{1.0, 2.1, 3.5}, {1.1, 2.2, 3.6}});
    assertEquals(expr.fullFormString(), //
        "List(List(1.0`, 2.1`, 3.5`), List(1.1`, 2.2`, 3.6`))");
    assertEquals(expr.toString(), //
        "\n{{1.0,2.1,3.5},\n" + " {1.1,2.2,3.6}}");
  }

   @Test
   public void test005() {
    IExpr expr = F.symjify(new boolean[][] {{true, false}, {false, true}});
    assertEquals(expr.fullFormString(), "List(List(True, False), List(False, True))");
    assertEquals(expr.toString(), "{{True,False},{False,True}}");
  }

   @Test
   public void testEvalQuiet() {
    final StringWriter outWriter = new StringWriter();
    WriterOutputStream wouts = new WriterOutputStream(outWriter);
    final StringWriter errorWriter = new StringWriter();
    WriterOutputStream werrors = new WriterOutputStream(errorWriter);
    PrintStream outs = new PrintStream(wouts);
    PrintStream errors = new PrintStream(werrors);
    EvalEngine engine = new EvalEngine("", 256, 256, outs, errors, true);
    EvalEngine.set(engine);
    IExpr result =
        F.eval(F.Quiet(F.Solve(F.List(F.Equal(F.symjify("x").multiply(F.symjify("y")), F.ZZ(1))),
            F.List(F.symjify("x"), F.symjify("y")))));
    try (PrintStream errorPrintStream = engine.getErrorPrintStream()) {
      String message = errorWriter.toString();
      // don't get message: "Solve: The system cannot be solved with the methods available to
      // Solve."
      assertEquals("", message);
    }
    assertEquals("Solve({x*y==1},{x,y})", //
        result.toString());
  }
}

package org.matheclipse.core.system;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/** Test F.symjify() */
public class SymjifyTestCase extends ExprEvaluatorTestCase {
  public SymjifyTestCase(String name) {
    super(name);
  }

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

  public void test001() {
    IExpr expr = F.symjify("(a+(b+c))");
    assertEquals(expr.fullFormString(), "Plus(a, b, c)");
    assertEquals(expr.toString(), "a+b+c");
  }

  public void test002() {
    IExpr expr = F.symjify(new int[] {1, 2, 3});
    assertEquals(expr.fullFormString(), "List(1, 2, 3)");
    assertEquals(expr.toString(), "{1,2,3}");
  }

  public void test003() {
    IExpr expr = F.symjify(new double[] {1.0, 2.1, 3.5});
    assertEquals(expr.fullFormString(), "List(1.0`, 2.1`, 3.5`)");
    assertEquals(expr.toString(), "{1.0,2.1,3.5}");
  }

  public void test004() {
    IExpr expr = F.symjify(new double[][] {{1.0, 2.1, 3.5}, {1.1, 2.2, 3.6}});
    assertEquals(expr.fullFormString(), //
        "List(List(1.0`, 2.1`, 3.5`), List(1.1`, 2.2`, 3.6`))");
    assertEquals(expr.toString(), //
        "\n{{1.0,2.1,3.5},\n" + " {1.1,2.2,3.6}}");
  }

  public void test005() {
    IExpr expr = F.symjify(new boolean[][] {{true, false}, {false, true}});
    assertEquals(expr.fullFormString(), "List(List(True, False), List(False, True))");
    assertEquals(expr.toString(), "{{True,False},{False,True}}");
  }
}

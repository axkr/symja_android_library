package org.matheclipse.io.system;

import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sec;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sow;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.expression.S.y;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/** */
public class ExpandTestCase extends AbstractTestCase {
  public ExpandTestCase(String name) {
    super(name);
  }

  public void testExpand001() {
    IAST ast = Times(x, x);
    IExpr temp = Algebra.expandAll(ast, null, false, false, false,EvalEngine.get());
    assertEquals(temp.toString(), "x^2");
  }

  public void testExpand002() {
    IAST ast = Times(C1D2, x, x);
    IExpr temp = Algebra.expand(ast, null, false, false, true);
    assertEquals(temp.toString(), "NIL");
  }

  public void testExpand003() {
    IAST ast = Power(Plus(x, y), C3);
    IExpr temp = Algebra.expandAll(ast, null, false, false, false,EvalEngine.get());
    assertEquals(temp.toString(), "x^3+y^3+3*x^2*y+3*x*y^2");
  }

  public void testExpand004() {
    IAST ast = Plus(Sow(Power(a, 2)), C1);
    IExpr temp = Algebra.expandAll(ast, null, false, false, false,EvalEngine.get());
    if (!temp.isPresent()) {
      temp = ast;
    }
    assertEquals(temp.toString(), "1+Sow[a^2]");
  }

  public void testExpand005() {
    // x / y
    IAST ast = Times(x, Power(y, -1));
    IExpr temp = Algebra.expandAll(ast, null, true, false,false, EvalEngine.get());
    // because of sorting and flattening flags:
    assertEquals(temp, F.NIL);

    // temp = ExpandAll.expandAll((IAST)temp, null, true, false);
    // assertNull(temp);
  }

  public void testExpand006() {
    // (3*x^2+2)^2
    IAST ast = Power(Plus(C2, Times(C3, Power(x, 2))), C2);
    IExpr temp = Algebra.expand(ast, null, true, false, true);
    if (temp == null) {
      temp = ast;
    }
    assertEquals(temp.toString(), "2^2+3^2*(x^2)^2+2*2*3*x^2");
  }

  public void testExpand007() {
    // Sec(x)^2*Sin(x)^2
    IAST ast = Times(Power(Sec(x), C2), Power(Sin(x), 2));
    IExpr temp = Algebra.expand(ast, null, true, false, true);
    if (!temp.isPresent()) {
      assertEquals(ast.toString(), "Sec[x]^2*Sin[x]^2");
      return;
    }
    assertEquals(temp.toString(), "Tan[x]^2");
  }

  //  public void testExpandPerformance() {
  //    IAST ast = Power(Plus(F.Times(C2, w), x, y, F.Times(F.C3, z)), F.ZZ(400));
  //    IExpr temp = Algebra.expandAll(ast, null, false, false, false, EvalEngine.get());
  //    //    assertEquals(temp.toString(), " ");
  //  }

  public void testRationalFunction001() {
    check("PolynomialQ(x^2*(a+b*x^3)^16,x)", "True");
  }

  protected void setUp() {
    super.setUp();
    Config.MAX_AST_SIZE = Integer.MAX_VALUE;
  }
}

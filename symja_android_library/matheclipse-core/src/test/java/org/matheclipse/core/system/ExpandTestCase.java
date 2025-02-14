package org.matheclipse.core.system;

import static org.junit.Assert.assertEquals;
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
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.expression.S.y;
import static org.matheclipse.core.expression.S.z;
import org.junit.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class ExpandTestCase extends ExprEvaluatorTestCase {

  @Test
  public void testExpandTrig001() {
    // TODO
    check("Expand(Sin(x+y),Trig->True)", //
        "Sin(x+y)");

  }

  @Test
  public void testExpand001() {
    IAST ast = Times(x, x);
    IExpr temp = Algebra.expandAll(ast, null, false, false, false, EvalEngine.get());
    assertEquals(temp.toString(), "x^2");
  }

  @Test
  public void testExpand002() {
    IAST ast = Times(C1D2, x, x);
    IExpr temp = Algebra.expand(ast, null, false, false, true);
    assertEquals(temp.toString(), "x^2/2");
  }

  @Test
  public void testExpand003() {
    IAST ast = Power(Plus(x, y), C3);
    IExpr temp = Algebra.expandAll(ast, null, false, false, false, EvalEngine.get());
    assertEquals(temp.toString(), "x^3+y^3+3*x^2*y+3*x*y^2");
  }

  @Test
  public void testExpand004() {
    IAST ast = Plus(Sow(Power(a, 2)), C1);
    IExpr temp = Algebra.expandAll(ast, null, false, false, false, EvalEngine.get());
    if (temp.isNIL()) {
      temp = ast;
    }
    assertEquals(temp.toString(), "1+Sow(a^2)");
  }

  @Test
  public void testExpand005() {
    // x / y
    IAST ast = Times(x, Power(y, -1));
    IExpr temp = Algebra.expandAll(ast, null, true, false, false, EvalEngine.get());
    // because of sorting and flattening flags:
    assertEquals(temp, F.NIL);

    // temp = ExpandAll.expandAll((IAST)temp, null, true, false);
    // assertNull(temp);
  }

  @Test
  public void testExpand006() {
    // (3*x^2+2)^2
    IAST ast = Power(Plus(C2, Times(C3, Power(x, 2))), C2);
    IExpr temp = Algebra.expand(ast, null, true, false, true);
    if (temp == null) {
      temp = ast;
    }
    assertEquals(temp.toString(), "4+12*x^2+9*x^4");
  }

  @Test
  public void testExpand007() {
    // Sec(x)^2*Sin(x)^2
    IAST ast = Times(Power(Sec(x), C2), Power(Sin(x), 2));
    IExpr temp = Algebra.expand(ast, null, true, false, true);
    if (temp.isNIL()) {
      assertEquals(ast.toString(), "Sec(x)^2*Sin(x)^2");
      return;
    }
    assertEquals(temp.toString(), "Tan[x]^2");
  }

  @Test
  public void testExpandPerformance001() {
    // ExpandAll((a+b+2*c+x+y+3*z)^6)
    IAST ast = Power(Plus(a, b, F.Times(C2, c), x, y, F.Times(F.C3, z)), F.ZZ(6));
    IAST temp = (IAST) Algebra.expand(ast, null, false, false, false);
    EvalEngine engine = EvalEngine.get();
    temp = (IAST) engine.evaluate(temp);
    // number of terms
    assertEquals(temp.argSize(), 462);
    assertEquals(temp.get(462).toString(), "729*z^6");
  }

  @Test
  public void testExpandPerformance002() {
    // Expand((a+b+2*c+x+y+3*z)^12)
    IAST ast = Power(Plus(a, b, F.Times(C2, c), x, y, F.Times(F.C3, z)), F.ZZ(12));
    IAST temp = (IAST) Algebra.expand(ast, null, false, false, false);
    EvalEngine engine = EvalEngine.get();
    temp = (IAST) engine.evaluate(temp);
    // number of terms
    assertEquals(temp.argSize(), 6188);
    assertEquals(temp.get(6188).toString(), "531441*z^12");
    assertEquals(temp.get(462).toString(), "5544*a^5*b^6*y");
  }

  @Test
  public void testExpandPerformance003() {
    if (Config.EXPENSIVE_JUNIT_TESTS) {
      // best time 8.8 s
      // ExpandAll((a+b+2*c+x+y+3*z)^24)
      IAST ast = Power(Plus(a, b, F.Times(C2, c), x, y, F.Times(F.C3, z)), F.ZZ(24));
      IAST temp = (IAST) Algebra.expandAll(ast, null, false, false, false, EvalEngine.get());
      EvalEngine engine = EvalEngine.get();
      temp = (IAST) engine.evaluate(temp);
      // number of terms
      assertEquals(temp.argSize(), 118755);
      assertEquals(temp.get(118755).toString(), "282429536481*z^24");
    }
  }

  @Test
  public void testExpandPerformance004() {
    // https://stackoverflow.com/q/70373472/24819
    if (Config.EXPENSIVE_JUNIT_TESTS) {
      // best time 4.8 s
      String str =
          "k1 = -2*u2*u3*(2*u3*(2*u1 - 2*u2)*(-u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) - u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))^2*(u2*(u1 - u2)^2 - sqrt(3)*u3^3 + u3^2*(2*u1 - 3*u2) + sqrt(3)*u3*(u1 - 3*u2)*(u1 - u2)) - 2*(-u3^2 + (u1 - u2)^2)*(-u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) - u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))*(u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) + u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))*(-sqrt(3)*u2*(u1 - u2)^2 - u3^3 - sqrt(3)*u3^2*(2*u1 - 3*u2) + u3*(u1 - 3*u2)*(u1 - u2)))*(-u1*u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2)*(u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) + u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))*(sqrt(3)*u2^2*(u1 - u2) + u2*u3*(2*u1 - 3*u2) + u3^3 - sqrt(3)*u3^2*(u1 - 3*u2)) - u1*u3^2*(-3*u2^2 + u3^2)*(u3^2 - 3*(u1 - u2)^2)*(-u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) - u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))*(u2^2*(u1 - u2) - sqrt(3)*u2*u3*(2*u1 - 3*u2) - sqrt(3)*u3^3 - u3^2*(u1 - 3*u2))) - (-2*u2*u3*(-u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) - u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))^2*(u2^2*(u1 - u2) - sqrt(3)*u2*u3*(2*u1 - 3*u2) - sqrt(3)*u3^3 - u3^2*(u1 - 3*u2)) - (u2^2 - u3^2)*(-u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) - u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))*(u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) + u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))*(sqrt(3)*u2^2*(u1 - u2) + u2*u3*(2*u1 - 3*u2) + u3^3 - sqrt(3)*u3^2*(u1 - 3*u2)))*(-u1*u3*(2*u1 - 2*u2)*(-u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) - u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))*(u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) + u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))*(-sqrt(3)*u2*(u1 - u2)^2 - u3^3 - sqrt(3)*u3^2*(2*u1 - 3*u2) + u3*(u1 - 3*u2)*(u1 - u2)) + u3*(2*u1 - 2*u2)*(-u1*u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2)*(u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) + u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))*(-sqrt(3)*u2*(u1 - u2)^2 - u3^3 - sqrt(3)*u3^2*(2*u1 - 3*u2) + u3*(u1 - 3*u2)*(u1 - u2)) - u1*u3^2*(-3*u2^2 + u3^2)*(u3^2 - 3*(u1 - u2)^2)*(-u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) - u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))*(u2*(u1 - u2)^2 - sqrt(3)*u3^3 + u3^2*(2*u1 - 3*u2) + sqrt(3)*u3*(u1 - 3*u2)*(u1 - u2))) + u3*(u3*(2*u1 - 2*u2)*(-u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) - u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))^2*(u2*(u1 - u2)^2 - sqrt(3)*u3^3 + u3^2*(2*u1 - 3*u2) + sqrt(3)*u3*(u1 - 3*u2)*(u1 - u2)) - (-u3^2 + (u1 - u2)^2)*(-u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) - u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))*(u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) + u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))*(-sqrt(3)*u2*(u1 - u2)^2 - u3^3 - sqrt(3)*u3^2*(2*u1 - 3*u2) + u3*(u1 - 3*u2)*(u1 - u2))));\n"
              + "k2 = -(u2^2 - u3^2)*(2*u3*(2*u1 - 2*u2)*(-u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) - u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))^2*(u2*(u1 - u2)^2 - sqrt(3)*u3^3 + u3^2*(2*u1 - 3*u2) + sqrt(3)*u3*(u1 - 3*u2)*(u1 - u2)) - 2*(-u3^2 + (u1 - u2)^2)*(-u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) - u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))*(u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) + u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))*(-sqrt(3)*u2*(u1 - u2)^2 - u3^3 - sqrt(3)*u3^2*(2*u1 - 3*u2) + u3*(u1 - 3*u2)*(u1 - u2)))*(-u1*u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2)*(u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) + u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))*(sqrt(3)*u2^2*(u1 - u2) + u2*u3*(2*u1 - 3*u2) + u3^3 - sqrt(3)*u3^2*(u1 - 3*u2)) - u1*u3^2*(-3*u2^2 + u3^2)*(u3^2 - 3*(u1 - u2)^2)*(-u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) - u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))*(u2^2*(u1 - u2) - sqrt(3)*u2*u3*(2*u1 - 3*u2) - sqrt(3)*u3^3 - u3^2*(u1 - 3*u2))) - (-2*u2*u3*(-u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) - u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))^2*(u2^2*(u1 - u2) - sqrt(3)*u2*u3*(2*u1 - 3*u2) - sqrt(3)*u3^3 - u3^2*(u1 - 3*u2)) - (u2^2 - u3^2)*(-u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) - u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))*(u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) + u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))*(sqrt(3)*u2^2*(u1 - u2) + u2*u3*(2*u1 - 3*u2) + u3^3 - sqrt(3)*u3^2*(u1 - 3*u2)))*(u1*u3*(2*u1 - 2*u2)*(-u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) - u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))^2*(u2*(u1 - u2)^2 - sqrt(3)*u3^3 + u3^2*(2*u1 - 3*u2) + sqrt(3)*u3*(u1 - 3*u2)*(u1 - u2)) + u2*(u3*(2*u1 - 2*u2)*(-u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) - u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))^2*(u2*(u1 - u2)^2 - sqrt(3)*u3^3 + u3^2*(2*u1 - 3*u2) + sqrt(3)*u3*(u1 - 3*u2)*(u1 - u2)) - (-u3^2 + (u1 - u2)^2)*(-u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) - u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))*(u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) + u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))*(-sqrt(3)*u2*(u1 - u2)^2 - u3^3 - sqrt(3)*u3^2*(2*u1 - 3*u2) + u3*(u1 - 3*u2)*(u1 - u2))) - (-u3^2 + (u1 - u2)^2)*(-u1*u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2)*(u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) + u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))*(-sqrt(3)*u2*(u1 - u2)^2 - u3^3 - sqrt(3)*u3^2*(2*u1 - 3*u2) + u3*(u1 - 3*u2)*(u1 - u2)) - u1*u3^2*(-3*u2^2 + u3^2)*(u3^2 - 3*(u1 - u2)^2)*(-u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) - u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))*(u2*(u1 - u2)^2 - sqrt(3)*u3^3 + u3^2*(2*u1 - 3*u2) + sqrt(3)*u3*(u1 - 3*u2)*(u1 - u2))));\n"
              + "k3 = -u1*u3*(2*u1 - 2*u2)*(-u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) - u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))*(u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) + u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))*(-sqrt(3)*u2*(u1 - u2)^2 - u3^3 - sqrt(3)*u3^2*(2*u1 - 3*u2) + u3*(u1 - 3*u2)*(u1 - u2)) + u3*(2*u1 - 2*u2)*(-u1*u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2)*(u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) + u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))*(-sqrt(3)*u2*(u1 - u2)^2 - u3^3 - sqrt(3)*u3^2*(2*u1 - 3*u2) + u3*(u1 - 3*u2)*(u1 - u2)) - u1*u3^2*(-3*u2^2 + u3^2)*(u3^2 - 3*(u1 - u2)^2)*(-u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) - u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))*(u2*(u1 - u2)^2 - sqrt(3)*u3^3 + u3^2*(2*u1 - 3*u2) + sqrt(3)*u3*(u1 - 3*u2)*(u1 - u2))) - u3*(u3*(2*u1 - 2*u2)*(-u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) - u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))^2*(u2*(u1 - u2)^2 - sqrt(3)*u3^3 + u3^2*(2*u1 - 3*u2) + sqrt(3)*u3*(u1 - 3*u2)*(u1 - u2)) - (-u3^2 + (u1 - u2)^2)*(-u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) - u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))*(u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) + u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))*(-sqrt(3)*u2*(u1 - u2)^2 - u3^3 - sqrt(3)*u3^2*(2*u1 - 3*u2) + u3*(u1 - 3*u2)*(u1 - u2)));\n"
              + "k4 = u1*u3*(2*u1 - 2*u2)*(-u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) - u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))^2*(u2*(u1 - u2)^2 - sqrt(3)*u3^3 + u3^2*(2*u1 - 3*u2) + sqrt(3)*u3*(u1 - 3*u2)*(u1 - u2)) - u2*(u3*(2*u1 - 2*u2)*(-u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) - u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))^2*(u2*(u1 - u2)^2 - sqrt(3)*u3^3 + u3^2*(2*u1 - 3*u2) + sqrt(3)*u3*(u1 - 3*u2)*(u1 - u2)) - (-u3^2 + (u1 - u2)^2)*(-u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) - u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))*(u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) + u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))*(-sqrt(3)*u2*(u1 - u2)^2 - u3^3 - sqrt(3)*u3^2*(2*u1 - 3*u2) + u3*(u1 - 3*u2)*(u1 - u2))) - (-u3^2 + (u1 - u2)^2)*(-u1*u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2)*(u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) + u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))*(-sqrt(3)*u2*(u1 - u2)^2 - u3^3 - sqrt(3)*u3^2*(2*u1 - 3*u2) + u3*(u1 - 3*u2)*(u1 - u2)) - u1*u3^2*(-3*u2^2 + u3^2)*(u3^2 - 3*(u1 - u2)^2)*(-u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) - u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))*(u2*(u1 - u2)^2 - sqrt(3)*u3^3 + u3^2*(2*u1 - 3*u2) + sqrt(3)*u3*(u1 - 3*u2)*(u1 - u2)));";
      check(str, //
          "");
      check("xp=Expand(k1*k3+k2*k4)", //
          "0");
    }
  }

  @Test
  public void testExpandPerformance005() {
    if (Config.EXPENSIVE_JUNIT_TESTS) {
      // best time 2.5 s
      String str =
          "k1 = -2*u2*u3*(2*u3*(2*u1 - 2*u2)* ((-u2)*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) -  u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))^2* (u2*(u1 - u2)^2 - Sqrt(3)*u3^3));\n"
              + "k2 = -(u2^2 - u3^2)*(2* u3*(2*u1 - 2*u2)*(-u2*u3*(-u2^2 + 3*u3^2)*(u3^2 - 3*(u1 - u2)^2) - u3*(u1 - u2)*(-3*u2^2 + u3^2)*(3*u3^2 - (u1 - u2)^2))^2*(u2*(u1 - u2)^2 - Sqrt(3)*u3^3 + u3^2*(2*u1 - 3*u2)));";
      check(str, //
          "");
      check("xp=Expand(k1*k2)", //
          "2592*u1^18*u2^13*u3^7-36288*u1^17*u2^14*u3^7+235872*u1^16*u2^15*u3^7-943488*u1^\n"//
              + "15*u2^16*u3^7+2594592*u1^14*u2^17*u3^7-5189184*u1^13*u2^18*u3^7+7783776*u1^12*u2^\n"
              + "19*u3^7-8895744*u1^11*u2^20*u3^7+7783776*u1^10*u2^21*u3^7-5189184*u1^9*u2^22*u3^\n"
              + "7+2594592*u1^8*u2^23*u3^7-943488*u1^7*u2^24*u3^7+235872*u1^6*u2^25*u3^7-36288*u1^\n"
              + "5*u2^26*u3^7+2592*u1^4*u2^27*u3^7-6048*u1^18*u2^11*u3^9+62208*u1^17*u2^12*u3^9-\n"
              + "264384*u1^16*u2^13*u3^9+521856*u1^15*u2^14*u3^9-28512*u1^14*u2^15*u3^9-2623104*u1^\n"
              + "13*u2^16*u3^9+7755264*u1^12*u2^17*u3^9-13001472*u1^11*u2^18*u3^9+14797728*u1^10*u2^\n"
              + "19*u3^9-12013056*u1^9*u2^20*u3^9+7013952*u1^8*u2^21*u3^9-2892672*u1^7*u2^22*u3^9+\n"
              + "802656*u1^6*u2^23*u3^9-134784*u1^5*u2^24*u3^9+10368*u1^4*u2^25*u3^9-5184*Sqrt(3)*u1^\n"
              + "16*u2^12*u3^10+62208*Sqrt(3)*u1^15*u2^13*u3^10-342144*Sqrt(3)*u1^14*u2^14*u3^10+\n"
              + "1140480*Sqrt(3)*u1^13*u2^15*u3^10-2566080*Sqrt(3)*u1^12*u2^16*u3^10+4105728*Sqrt(\n"
              + "3)*u1^11*u2^17*u3^10-4790016*Sqrt(3)*u1^10*u2^18*u3^10+4105728*Sqrt(3)*u1^9*u2^\n"
              + "19*u3^10-2566080*Sqrt(3)*u1^8*u2^20*u3^10+1140480*Sqrt(3)*u1^7*u2^21*u3^10-\n"
              + "342144*Sqrt(3)*u1^6*u2^22*u3^10+62208*Sqrt(3)*u1^5*u2^23*u3^10-5184*Sqrt(3)*u1^4*u2^\n"
              + "24*u3^10+5184*u1^18*u2^9*u3^11-29376*u1^17*u2^10*u3^11-11232*u1^16*u2^11*u3^11+\n"
              + "587520*u1^15*u2^12*u3^11-2503872*u1^14*u2^13*u3^11+5821632*u1^13*u2^14*u3^11-\n"
              + "8862048*u1^12*u2^15*u3^11+9414144*u1^11*u2^16*u3^11-7179840*u1^10*u2^17*u3^11+\n"
              + "3996864*u1^9*u2^18*u3^11-1658016*u1^8*u2^19*u3^11+532224*u1^7*u2^20*u3^11-136512*u1^\n"
              + "6*u2^21*u3^11+25920*u1^5*u2^22*u3^11-2592*u1^4*u2^23*u3^11+12096*Sqrt(3)*u1^16*u2^\n"
              + "10*u3^12-95040*Sqrt(3)*u1^15*u2^11*u3^12+256608*Sqrt(3)*u1^14*u2^12*u3^12-\n"
              + "1853280*Sqrt(3)*u1^12*u2^14*u3^12+5816448*Sqrt(3)*u1^11*u2^15*u3^12-9979200*Sqrt(\n"
              + "3)*u1^10*u2^16*u3^12+11176704*Sqrt(3)*u1^9*u2^17*u3^12-8553600*Sqrt(3)*u1^8*u2^\n"
              + "18*u3^12+4466880*Sqrt(3)*u1^7*u2^19*u3^12-1530144*Sqrt(3)*u1^6*u2^20*u3^12+\n"
              + "311040*Sqrt(3)*u1^5*u2^21*u3^12-28512*Sqrt(3)*u1^4*u2^22*u3^12-2112*u1^18*u2^7*u3^\n"
              + "13+3072*u1^17*u2^8*u3^13+48000*u1^16*u2^9*u3^13-172032*u1^15*u2^10*u3^13-191904*u1^\n"
              + "14*u2^11*u3^13+2819136*u1^13*u2^12*u3^13-9724704*u1^12*u2^13*u3^13+20068608*u1^\n"
              + "11*u2^14*u3^13-28498560*u1^10*u2^15*u3^13+29020032*u1^9*u2^16*u3^13-21276864*u1^\n"
              + "8*u2^17*u3^13+10996992*u1^7*u2^18*u3^13-3807648*u1^6*u2^19*u3^13+793152*u1^5*u2^\n"
              + "20*u3^13-75168*u1^4*u2^21*u3^13-10368*Sqrt(3)*u1^16*u2^8*u3^14+25920*Sqrt(3)*u1^\n"
              + "15*u2^9*u3^14+192672*Sqrt(3)*u1^14*u2^10*u3^14-1147392*Sqrt(3)*u1^13*u2^11*u3^14+\n"
              + "2504736*Sqrt(3)*u1^12*u2^12*u3^14-2194560*Sqrt(3)*u1^11*u2^13*u3^14-1245888*Sqrt(\n"
              + "3)*u1^10*u2^14*u3^14+5550336*Sqrt(3)*u1^9*u2^15*u3^14-6896448*Sqrt(3)*u1^8*u2^16*u3^\n"
              + "14+4809024*Sqrt(3)*u1^7*u2^17*u3^14-2015712*Sqrt(3)*u1^6*u2^18*u3^14+476928*Sqrt(\n"
              + "3)*u1^5*u2^19*u3^14-49248*Sqrt(3)*u1^4*u2^20*u3^14+416*u1^18*u2^5*u3^15+192*u1^\n"
              + "17*u2^6*u3^15-10848*u1^16*u2^7*u3^15+3200*u1^15*u2^8*u3^15+234432*u1^14*u2^9*u3^\n"
              + "15-902016*u1^13*u2^10*u3^15+988544*u1^12*u2^11*u3^15+2220288*u1^11*u2^12*u3^15-\n"
              + "9715776*u1^10*u2^13*u3^15+16920576*u1^9*u2^14*u3^15-17591040*u1^8*u2^15*u3^15+\n"
              + "11695104*u1^7*u2^16*u3^15-4903200*u1^6*u2^17*u3^15+1187136*u1^5*u2^18*u3^15-\n"
              + "127008*u1^4*u2^19*u3^15+4224*Sqrt(3)*u1^16*u2^6*u3^16+12672*Sqrt(3)*u1^15*u2^7*u3^\n"
              + "16-104256*Sqrt(3)*u1^14*u2^8*u3^16-132864*Sqrt(3)*u1^13*u2^9*u3^16+1952064*Sqrt(\n"
              + "3)*u1^12*u2^10*u3^16-5807232*Sqrt(3)*u1^11*u2^11*u3^16+9292224*Sqrt(3)*u1^10*u2^\n"
              + "12*u3^16-9303552*Sqrt(3)*u1^9*u2^13*u3^16+6105024*Sqrt(3)*u1^8*u2^14*u3^16-\n"
              + "2640384*Sqrt(3)*u1^7*u2^15*u3^16+736128*Sqrt(3)*u1^6*u2^16*u3^16-124416*Sqrt(3)*u1^\n"
              + "5*u2^17*u3^16+10368*Sqrt(3)*u1^4*u2^18*u3^16-32*u1^18*u2^3*u3^17+256*u1^17*u2^4*u3^\n"
              + "17+3392*u1^16*u2^5*u3^17+9856*u1^15*u2^6*u3^17-100000*u1^14*u2^7*u3^17-115712*u1^\n"
              + "13*u2^8*u3^17+2066752*u1^12*u2^9*u3^17-6664704*u1^11*u2^10*u3^17+11463360*u1^10*u2^\n"
              + "11*u3^17-12286080*u1^9*u2^12*u3^17+8569152*u1^8*u2^13*u3^17-3870720*u1^7*u2^14*u3^\n"
              + "17+1080000*u1^6*u2^15*u3^17-165888*u1^5*u2^16*u3^17+10368*u1^4*u2^17*u3^17-832*Sqrt(\n"
              + "3)*u1^16*u2^4*u3^18-6272*Sqrt(3)*u1^15*u2^5*u3^18-6720*Sqrt(3)*u1^14*u2^6*u3^18+\n"
              + "133888*Sqrt(3)*u1^13*u2^7*u3^18+16256*Sqrt(3)*u1^12*u2^8*u3^18-1832832*Sqrt(3)*u1^\n"
              + "11*u2^9*u3^18+6104640*Sqrt(3)*u1^10*u2^10*u3^18-10679040*Sqrt(3)*u1^9*u2^11*u3^\n"
              + "18+11859264*Sqrt(3)*u1^8*u2^12*u3^18-8722944*Sqrt(3)*u1^7*u2^13*u3^18+4150656*Sqrt(\n"
              + "3)*u1^6*u2^14*u3^18-1161216*Sqrt(3)*u1^5*u2^15*u3^18+145152*Sqrt(3)*u1^4*u2^16*u3^\n"
              + "18-64*u1^17*u2^2*u3^19-800*u1^16*u2^3*u3^19-7680*u1^15*u2^4*u3^19-13760*u1^14*u2^\n"
              + "5*u3^19+160448*u1^13*u2^6*u3^19+142368*u1^12*u2^7*u3^19-2706432*u1^11*u2^8*u3^19+\n"
              + "8589696*u1^10*u2^9*u3^19-15178752*u1^9*u2^10*u3^19+17456256*u1^8*u2^11*u3^19-\n"
              + "13443840*u1^7*u2^12*u3^19+6708096*u1^6*u2^13*u3^19-1959552*u1^5*u2^14*u3^19+\n"
              + "254016*u1^4*u2^15*u3^19+64*Sqrt(3)*u1^16*u2^2*u3^20+448*Sqrt(3)*u1^15*u2^3*u3^20+\n"
              + "3296*Sqrt(3)*u1^14*u2^4*u3^20+2560*Sqrt(3)*u1^13*u2^5*u3^20-50144*Sqrt(3)*u1^12*u2^\n"
              + "6*u3^20+11136*Sqrt(3)*u1^11*u2^7*u3^20+573504*Sqrt(3)*u1^10*u2^8*u3^20-2156544*Sqrt(\n"
              + "3)*u1^9*u2^9*u3^20+4121280*Sqrt(3)*u1^8*u2^10*u3^20-4669056*Sqrt(3)*u1^7*u2^11*u3^\n"
              + "20+3143232*Sqrt(3)*u1^6*u2^12*u3^20-1161216*Sqrt(3)*u1^5*u2^13*u3^20+181440*Sqrt(\n"
              + "3)*u1^4*u2^14*u3^20+768*u1^15*u2^2*u3^21+9120*u1^14*u2^3*u3^21+34368*u1^13*u2^4*u3^\n"
              + "21-106656*u1^12*u2^5*u3^21-427776*u1^11*u2^6*u3^21+2136960*u1^10*u2^7*u3^21-\n"
              + "4482432*u1^9*u2^8*u3^21+6343488*u1^8*u2^9*u3^21-6448896*u1^7*u2^10*u3^21+4335552*u1^\n"
              + "6*u2^11*u3^21-1669248*u1^5*u2^12*u3^21+274752*u1^4*u2^13*u3^21+64*Sqrt(3)*u1^15*u2*u3^\n"
              + "22+544*Sqrt(3)*u1^14*u2^2*u3^22+4096*Sqrt(3)*u1^13*u2^3*u3^22+4128*Sqrt(3)*u1^12*u2^\n"
              + "4*u3^22-81792*Sqrt(3)*u1^11*u2^5*u3^22-91200*Sqrt(3)*u1^10*u2^6*u3^22+1057536*Sqrt(\n"
              + "3)*u1^9*u2^7*u3^22-2082240*Sqrt(3)*u1^8*u2^8*u3^22+1779840*Sqrt(3)*u1^7*u2^9*u3^\n"
              + "22-627264*Sqrt(3)*u1^6*u2^10*u3^22+36288*Sqrt(3)*u1^4*u2^12*u3^22-96*u1^14*u2*u3^\n"
              + "23-5568*u1^13*u2^2*u3^23-44448*u1^12*u2^3*u3^23-32256*u1^11*u2^4*u3^23+527328*u1^\n"
              + "10*u2^5*u3^23-492480*u1^9*u2^6*u3^23-783648*u1^8*u2^7*u3^23+1579392*u1^7*u2^8*u3^\n"
              + "23-895968*u1^6*u2^9*u3^23+119232*u1^5*u2^10*u3^23+28512*u1^4*u2^11*u3^23-768*Sqrt(\n"
              + "3)*u1^13*u2*u3^24-7680*Sqrt(3)*u1^12*u2^2*u3^24-20352*Sqrt(3)*u1^11*u2^3*u3^24+\n"
              + "113472*Sqrt(3)*u1^10*u2^4*u3^24+290304*Sqrt(3)*u1^9*u2^5*u3^24-1660608*Sqrt(3)*u1^\n"
              + "8*u2^6*u3^24+2709504*Sqrt(3)*u1^7*u2^7*u3^24-2097792*Sqrt(3)*u1^6*u2^8*u3^24+\n"
              + "787968*Sqrt(3)*u1^5*u2^9*u3^24-114048*Sqrt(3)*u1^4*u2^10*u3^24+1152*u1^12*u2*u3^\n"
              + "25+25344*u1^11*u2^2*u3^25+100512*u1^10*u2^3*u3^25-238464*u1^9*u2^4*u3^25-660096*u1^\n"
              + "8*u2^5*u3^25+2173824*u1^7*u2^6*u3^25-2242080*u1^6*u2^7*u3^25+1005696*u1^5*u2^8*u3^\n"
              + "25-165888*u1^4*u2^9*u3^25+3456*Sqrt(3)*u1^11*u2*u3^26+22464*Sqrt(3)*u1^10*u2^2*u3^\n"
              + "26-34560*Sqrt(3)*u1^9*u2^3*u3^26-314496*Sqrt(3)*u1^8*u2^4*u3^26+988416*Sqrt(3)*u1^\n"
              + "7*u2^5*u3^26-1147392*Sqrt(3)*u1^6*u2^6*u3^26+601344*Sqrt(3)*u1^5*u2^7*u3^26-\n"
              + "119232*Sqrt(3)*u1^4*u2^8*u3^26-5184*u1^10*u2*u3^27-57024*u1^9*u2^2*u3^27-18144*u1^\n"
              + "8*u2^3*u3^27+580608*u1^7*u2^4*u3^27-1000512*u1^6*u2^5*u3^27+648000*u1^5*u2^6*u3^\n"
              + "27-147744*u1^4*u2^7*u3^27-6912*Sqrt(3)*u1^9*u2*u3^28-12096*Sqrt(3)*u1^8*u2^2*u3^\n"
              + "28+133056*Sqrt(3)*u1^7*u2^3*u3^28-251424*Sqrt(3)*u1^6*u2^4*u3^28+186624*Sqrt(3)*u1^\n"
              + "5*u2^5*u3^28-49248*Sqrt(3)*u1^4*u2^6*u3^28+10368*u1^8*u2*u3^29+41472*u1^7*u2^2*u3^\n"
              + "29-168480*u1^6*u2^3*u3^29+171072*u1^5*u2^4*u3^29-54432*u1^4*u2^5*u3^29+5184*Sqrt(\n"
              + "3)*u1^7*u2*u3^30-18144*Sqrt(3)*u1^6*u2^2*u3^30+20736*Sqrt(3)*u1^5*u2^3*u3^30-\n"
              + "7776*Sqrt(3)*u1^4*u2^4*u3^30-7776*u1^6*u2*u3^31+15552*u1^5*u2^2*u3^31-7776*u1^4*u2^\n"
              + "3*u3^31");
    }
  }

  @Test
  public void testExpandPerformance006() {
    String str = "k1 = (3*u3^2 - (u1 - u2)^2)^2 ;";
    check(str, //
        "");
    check("xp=Expand(k1 )", //
        "u1^4-4*u1^3*u2+6*u1^2*u2^2-4*u1*u2^3+u2^4-6*u1^2*u3^2+12*u1*u2*u3^2-6*u2^2*u3^2+\n"
            + "9*u3^4");
  }

  @Test
  public void testExpandPattern() {
    check("FullForm(Hold((a(1) + a(2))(x(1) + x(2))^2))", //
        "Hold(Times(Plus(a(1), a(2)), Power(Plus(x(1), x(2)), 2)))");
    check("FullForm(Hold((a(1) + a(2))*(x(1) + x(2))^2))", //
        "Hold(Times(Plus(a(1), a(2)), Power(Plus(x(1), x(2)), 2)))");
    check("Expand((a(1) + a(2))*(x(1) + x(2))^2, x(_))", //
        "(a(1)+a(2))*x(1)^2+2*(a(1)+a(2))*x(1)*x(2)+(a(1)+a(2))*x(2)^2");
  }

  // @Test
  // public void testExpandDenominator() {
  // check("ExpandDenominator((x+y)*(x-y)/((x+1)*y))", //
  // "((x-y)*(x+y))/(y+x*y)");
  // check("ExpandDenominator((x - 1) (x - 2)/((x - 3) (x - 4)))", //
  // "((-2+x)*(-1+x))/(12-7*x+x^2)");
  // check("ExpandDenominator(1/(x + 1) + 2/(x + 1)^2 + 3/(x + 1)^3)", //
  // "1/(1+x)+2/(1+2*x+x^2)+3/(1+3*x+3*x^2+x^3)");
  // check("ExpandDenominator(x == c/(a + b)^2 && y >= c/(a - b)^2)", //
  // "x==c/(a^2+2*a*b+b^2)&&y>=c/(a^2-2*a*b+b^2)");
  // }
  //
  // @Test
  // public void testExpandNumerator() {
  // check("ExpandNumerator((x+y)*(x-y)/((x+1)*y))", //
  // "(x^2-y^2)/((1+x)*y)");
  // check("ExpandNumerator((x - 1) (x - 2)/((x - 3) (x - 4)))", //
  // "(2-3*x+x^2)/((-4+x)*(-3+x))");
  // check("ExpandNumerator((a + b)^2/x + (c + d) (c - d)/y)", //
  // "(a^2+2*a*b+b^2)/x+(c^2-d^2)/y");
  // check("ExpandNumerator(x == (a + b)^2/c && y >= (a - b)^2/c)", //
  // "x==(a^2+2*a*b+b^2)/c&&y>=(a^2-2*a*b+b^2)/c");
  // }

  @Test
  public void testRationalFunction001() {
    check("PolynomialQ(x^2*(a+b*x^3)^16,x)", //
        "True");
  }

  @Override
  public void setUp() {
    super.setUp();
    if (Config.EXPENSIVE_JUNIT_TESTS) {
      Config.MAX_AST_SIZE = Integer.MAX_VALUE;
    }
  }
}

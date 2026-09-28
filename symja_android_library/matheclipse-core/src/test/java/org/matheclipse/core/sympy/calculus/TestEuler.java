package org.matheclipse.core.sympy.calculus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.sympy.exception.ValueError;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

public class TestEuler extends ExprEvaluatorTestCase {

  private IExpr parse(String str) {
    return evaluator.getEvalEngine().parse(str);
  }

  private IExpr eval(String str) {
    return evaluator.getEvalEngine().evaluate(str);
  }

  @Test
  public void testEulerInterface() {
    // https://github.com/sympy/sympy/blob/master/sympy/calculus/tests/test_euler.py
    // raises(ValueError, lambda: euler(D(x(t), t)*y(t), [x(t), y]))
    assertThrows(ValueError.class,
        () -> Euler.eulerEquations(eval("D(x(t), t)*y(t)"), F.List(parse("x(t)"), parse("y")),
            (IAST) parse("{t}")));
    // raises(ValueError, lambda: euler(D(x(t), t)*x(y), [x(t), x(y)]))
    assertThrows(ValueError.class, () -> Euler.eulerEquations(eval("D(x(t), t)*x(y)"),
        F.List(parse("x(t)"), parse("x(y)")), (IAST) parse("{t}")));
    // raises(TypeError, lambda: euler(D(x(t), t)**2, x(0)))
    assertThrows(ValueError.class, () -> Euler.eulerEquations(eval("D(x(t), t)^2"),
        F.List(parse("x(0)")), (IAST) parse("{t}")));
    // raises(TypeError, lambda: euler(D(x(t), t)*y(t), [t]))
    assertThrows(ValueError.class, () -> Euler.eulerEquations(eval("D(x(t), t)*y(t)"),
        F.List(parse("t")), (IAST) parse("{t}")));
    // assert euler(D(x(t), t)**2/2, {x(t)}) == [Eq(-D(x(t), t, t), 0)]
    assertEquals(
        Euler.eulerEquations(eval("D(x(t), t)^2/2"), F.List(parse("x(t)")), (IAST) parse("{t}"))
            .toString(), //
        "{x''(t)==0}"); // Equal normalizes the sign
  }

  @Test
  public void testEulerPendulum() {
    // L = D(x(t), t)**2/2 + cos(x(t))
    // assert euler(L, x(t), t) == [Eq(-sin(x(t)) - D(x(t), t, t), 0)]
    assertEquals(
        Euler.eulerEquations(eval("D(x(t), t)^2/2 + Cos(x(t))"), F.List(parse("x(t)")),
            (IAST) parse("{t}")).toString(), //
        "{-Sin(x(t))-x''(t)==0}");
  }

  @Test
  public void testEulerHenonheiles() {
    // L = sum(D(z(t), t)**2/2 - z(t)**2/2 for z in [x, y])
    // L += -x(t)**2*y(t) + y(t)**3/3
    // assert euler(L, [x(t), y(t)], t) == [Eq(-2*x(t)*y(t) - x(t) - D(x(t), t, t), 0),
    // Eq(-x(t)**2 + y(t)**2 - y(t) - D(y(t), t, t), 0)]
    IExpr L = eval("D(x(t),t)^2/2 - x(t)^2/2 + D(y(t),t)^2/2 - y(t)^2/2 - x(t)^2*y(t) + y(t)^3/3");
    assertEquals(
        Euler.eulerEquations(L, F.List(parse("x(t)"), parse("y(t)")), (IAST) parse("{t}")).toString(), //
        "{-x(t)-2*x(t)*y(t)-x''(t)==0,-x(t)^2-y(t)+y(t)^2-y''(t)==0}");
  }

  @Test
  public void testEulerSineg() {
    // psi = Function('psi')
    // L = D(psi(t, x), t)**2/2 - D(psi(t, x), x)**2/2 + cos(psi(t, x))
    // assert euler(L, psi(t, x), [t, x]) == [Eq(-sin(psi(t, x)) -
    // D(psi(t, x), t, t) + D(psi(t, x), x, x), 0)]
    IExpr L = eval("D(psi(t, x), t)^2/2 - D(psi(t, x), x)^2/2 + Cos(psi(t, x))");
    assertEquals(
        Euler.eulerEquations(L, F.List(parse("psi(t,x)")), (IAST) parse("{t,x}")).toString(), //
        "{-Sin(psi(t,x))+Derivative(0,2)[psi][t,x]-Derivative(2,0)[psi][t,x]==0}");
  }

  @Test
  public void testEulerHighOrder() {
    // an example from hep-th/0309038
    // L = (m*D(x(t), t)**2/2 + m*D(y(t), t)**2/2 -
    // k*D(x(t), t)*D(y(t), t, t) + k*D(y(t), t)*D(x(t), t, t))
    // assert euler(L, [x(t), y(t)]) == [Eq(2*k*D(y(t), t, t, t) - m*D(x(t), t, t), 0),
    // Eq(-2*k*D(x(t), t, t, t) - m*D(y(t), t, t), 0)]
    IExpr L = eval(
        "m*D(x(t), t)^2/2 + m*D(y(t), t)^2/2 - k*D(x(t), t)*D(y(t), {t,2}) + k*D(y(t), t)*D(x(t), {t,2})");
    assertEquals(
        Euler.eulerEquations(L, F.List(parse("x(t)"), parse("y(t)")), (IAST) parse("{t}")).toString(), //
        "{-m*x''(t)+2*k*Derivative(3)[y][t]==0,-m*y''(t)-2*k*Derivative(3)[x][t]==0}");

    // w = Symbol('w')
    // L = D(x(t, w), t, w)**2/2
    // assert euler(L) == [Eq(D(x(t, w), t, t, w, w), 0)]
    L = eval("D(x(t, w), t, w)^2/2");
    assertEquals(
        Euler.eulerEquations(L, F.List(parse("x(t,w)")), (IAST) parse("{t,w}")).toString(), //
        "{Derivative(2,2)[x][t,w]==0}");
  }

  @Test
  public void testIssue18653() {
    // f, g, h = symbols("f g h", cls=Function, args=(x, y))
    // expr2 = f.diff(x)*h.diff(z)
    // assert euler(expr2, (f,), (x, y)) == []
    IExpr expr2 = eval("D(f(x,y), x)*D(h(x,y), z)");
    assertEquals(
        Euler.eulerEquations(expr2, F.List(parse("f(x,y)")), (IAST) parse("{x,y}")).toString(), //
        "{}");
  }
}

package org.matheclipse.core.sympy.core;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

public class TestBasic extends ExprEvaluatorTestCase {

  @Test
  public void test_subs() {
    ISymbol a = F.a;
    ISymbol b = F.b;
    ISymbol c = F.c;
    ISymbol d = F.d;
    ISymbol e = F.e;
    ISymbol x = F.x;
    ISymbol y = F.y;
    
    IAST A = F.Rule(F.Sqrt(F.Sin(F.Times(2, x))), a);
    IAST B = F.Rule(F.Sin(F.Times(2, x)), b);
    IAST C = F.Rule(F.Cos(F.Times(2, x)), c);
    IAST D = F.Rule(x, d);
    IAST E = F.Rule(F.Exp(x), e);

    IExpr expr = F.Subtract(F.Power(x, 3), F.Times(3, x));
    IExpr res = expr.subs(F.List(F.Rule(x, F.oo)));
    assertEquals(res.toString(), //
        "Indeterminate");
    
    // (x**2 + x**4).subs(x**2, y)
    expr = F.Plus(F.Power(x, 2), F.Power(x, 4));
    res = expr.subs(F.List(F.Rule(F.Power(x, 2), y)));
    assertEquals(res.toString(), //
        "y+y^2");

    // (x**2 + x**5).subs(x**2, y)
    expr = F.Plus(F.Power(x, 2), F.Power(x, 5));
    res = expr.subs(F.List(F.Rule(F.Power(x, 2), y)));
    assertEquals(res.toString(), //
        "x^5+y");

    // (x**2 + x**4).xreplace(x**2, y)
    expr = F.Plus(F.Power(x, 2), F.Power(x, 4));
    res = expr.xreplace(F.List(F.Rule(F.Power(x, 2), y)));
    assertEquals(res.toString(), //
        "y+x^4");

    expr =
        F.Plus(F.Times(F.Sqrt(F.Sin(F.Times(2, x))), F.Sin(F.Times(F.Exp(x), x)),
            F.Cos(F.Times(2, x))), F.Sin(F.Times(2, x)));
    res = expr.subs(F.List(A, B, C, D, E));
    assertEquals(res.toString(), //
        "b+a*c*Sin(d*e)");
    // assertEquals(F.num(3.0).asCoeffAdd().toString(), "{0,Plus(3.0)}");
    // assertEquals(F.num(-3.0).asCoeffAdd().toString(), "{0,Plus(-3.0)}");
    // assertEquals(x.asCoeffAdd().toString(), "{0,Plus(x)}");
    // assertEquals(x.subtract(F.C1).asCoeffAdd().toString(), "{-1,Plus(x)}");
    // assertEquals(x.plus(F.C1).asCoeffAdd().toString(), "{1,Plus(x)}");
    // assertEquals(x.plus(F.C2).asCoeffAdd().toString(), "{2,Plus(x)}");
    // assertEquals(x.plus(y).asCoeffAdd(y).toString(), "{x,Plus(y)}");
    // assertEquals(F.C3.times(x).asCoeffAdd(y).toString(), "{3*x,Plus()}");
    // IAST e2 = F.Power(F.Plus(x, y), F.C2);
    // assertEquals(e2.asCoeffAdd(y).toString(), "{0,Plus((x+y)^2)}");
 
  }

}

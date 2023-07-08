package org.matheclipse.core.system;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Test {@link F#cseAsJava(org.matheclipse.core.interfaces.IExpr, StringBuilder)} common
 * subexpression elimination to Java converter
 */
public class CommonSubexpressionEliminationTestCase extends ExprEvaluatorTestCase {
  public CommonSubexpressionEliminationTestCase(String name) {
    super(name);
  }


  public void testCSE001() {
    ISymbol x = F.Dummy("x");
    ISymbol y = F.Dummy("y");
    IAST ast = F.Plus(
        F.Times(F.CN1D2,
            F.Arg(F.Plus(F.C1, F.Times(F.CN1, F.Plus(F.Times(F.CI, x), F.Times(F.CN1, y)))))),
        F.Times(F.C1D2, F.Arg(F.Plus(F.C1, F.Plus(F.Times(F.CI, x), F.Times(F.CN1, y))))),
        F.Times(F.CI, F.Plus(F.Times(F.CN1D4, F.Log(F.Plus(F.Sqr(x), F.Sqr(F.Subtract(F.C1, y))))),
            F.Times(F.C1D4, F.Log(F.Plus(F.Sqr(x), F.Sqr(F.Plus(F.C1, y))))))));
    assertEquals("-Arg(1-(I*x-y))/2+Arg(1+I*x-y)/2+I*(-Log(x^2+(1-y)^2)/4+Log(x^2+(1+y)^2)/4)", //
        ast.toString());
    StringBuilder buf = new StringBuilder();
    F.cseAsJava(ast, buf);
    assertEquals("IExpr v1 = F.Negate(y);\n" //
        + "IExpr v2 = F.Times(F.CI,x);\n" //
        + "IExpr v3 = F.Sqr(x);\n" //
        + "return F.Plus(F.Times(F.CN1D2,F.Arg(F.Plus(F.C1,F.Negate(v1),F.Negate(v2)))),F.Times(F.C1D2,F.Arg(F.Plus(F.C1,v1,v2))),F.Times(F.CI,F.Plus(F.Times(F.CN1D4,F.Log(F.Plus(F.Sqr(F.Plus(F.C1,v1)),v3))),F.Times(F.C1D4,F.Log(F.Plus(v3,F.Sqr(F.Plus(F.C1,y))))))));\n",
        buf.toString());
  }

  public void testCSE002() {
    ISymbol x = F.Dummy("x");
    ISymbol y = F.Dummy("y");
    IAST ast = F.Plus(F.Times(F.C2, F.CI, F.Cosh(x),
        F.Power(F.Subtract(F.Cos(F.Times(F.C2, y)), F.Cosh(F.Times(F.C2, x))), F.CN1), F.Sin(y)),
        F.Times(F.CN1, F.C2, F.Cos(y),
            F.Power(F.Subtract(F.Cos(F.Times(F.C2, y)), F.Cosh(F.Times(F.C2, x))), F.CN1),
            F.Sinh(x)));
    assertEquals(
        "(2*I*Cosh(x)*Sin(y))/(Cos(2*y)-Cosh(2*x))+((-1)*2*Cos(y)*Sinh(x))/(Cos(2*y)-Cosh(\n"
            + "2*x))", //
        ast.toString());
    StringBuilder buf = new StringBuilder();
    F.cseAsJava(ast, buf);
    assertEquals(
        "IExpr v1 = F.Power(F.Subtract(F.Cos(F.Times(F.C2,y)),F.Cosh(F.Times(F.C2,x))),F.CN1);\n" //
            + "return F.Plus(F.Times(F.CC(0L,1L,2L,1L),v1,F.Cosh(x),F.Sin(y)),F.Times(F.CN2,v1,F.Cos(y),F.Sinh(x)));\n", //
        buf.toString());
  }

  public void testCSE003() {
    ISymbol x = F.Dummy("x");
    ISymbol y = F.Dummy("y");
    IAST ast = F.Plus(
        F.Times(F.CI, F.Power(F.Plus(F.Cos(F.Times(F.C2, y)), F.Cosh(F.Times(F.C2, x))), F.CN1),
            F.Sin(F.Times(F.C2, y))),
        F.Times(F.Power(F.Plus(F.Cos(F.Times(F.C2, y)), F.Cosh(F.Times(F.C2, x))), F.CN1),
            F.Sinh(F.Times(F.C2, x))));
    assertEquals("(I*Sin(2*y))/(Cos(2*y)+Cosh(2*x))+Sinh(2*x)/(Cos(2*y)+Cosh(2*x))", //
        ast.toString());

    StringBuilder buf = new StringBuilder();
    F.cseAsJava(ast, buf);
    assertEquals("IExpr v1 = F.Times(F.C2,x);\n" //
        + "IExpr v2 = F.Times(F.C2,y);\n" //
        + "IExpr v3 = F.Power(F.Plus(F.Cos(v2),F.Cosh(v1)),F.CN1);\n" //
        + "return F.Plus(F.Times(F.CI,v3,F.Sin(v2)),F.Times(v3,F.Sinh(v1)));\n", //
        buf.toString());
  }
}

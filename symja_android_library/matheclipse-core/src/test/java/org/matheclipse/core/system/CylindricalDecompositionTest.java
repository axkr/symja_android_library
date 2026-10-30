package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/** CylindricalDecomposition and the real algebraic decomposition behind Reduce. */
public class CylindricalDecompositionTest extends ExprEvaluatorTestCase {

  @Test
  public void testOneVariable() {
    check("CylindricalDecomposition(t^2 > 9, {t})", //
        "t<-3||t>3");
    check("CylindricalDecomposition(t^2 == 16, {t})", //
        "t==-4||t==4");
    check("CylindricalDecomposition(t^2 == -9, {t})", //
        "False");
    check("CylindricalDecomposition(t^2 > 9, t)", //
        "t<-3||t>3");
  }

  @Test
  public void testDisks() {
    check("CylindricalDecomposition(u^2 + v^2 < 4, {u, v})", //
        "-2<u<2&&-Sqrt(4-u^2)<v<Sqrt(4-u^2)");
    // the sections at the ends have one point, not the interval of the sector
    check("CylindricalDecomposition(u^2 + v^2 <= 4, {u, v})", //
        "(u==-2&&v==0)||(-2<u<2&&-Sqrt(4-u^2)<=v<=Sqrt(4-u^2))||(u==2&&v==0)");
    // an algebraic boundary of the outer variable
    check("CylindricalDecomposition(u^2 + v^2 <= 3, {u, v})", //
        "(u==-Sqrt(3)&&v==0)||(-Sqrt(3)<u<Sqrt(3)&&-Sqrt(3-u^2)<=v<=Sqrt(3-u^2))||(u==Sqrt(\n"
            + "3)&&v==0)");
    check("CylindricalDecomposition(u^2 + v^2 == 4, {u, v})", //
        "(u==-2&&v==0)||(-2<u<2&&(v==-Sqrt(4-u^2)||v==Sqrt(4-u^2)))||(u==2&&v==0)");
  }

  @Test
  public void testSectionJoinsTheSimplerSector() {
    // the section where the curves meet has the interval of both sectors
    check("CylindricalDecomposition(u^2/9 + v^2 < 1 && u^2 + v^2/9 < 1, {u, v})", //
        "(-1<u<=-3/Sqrt(10)&&-Sqrt(9-9*u^2)<v<Sqrt(9-9*u^2))||(-3/Sqrt(10)<u<3/Sqrt(10)&&-Sqrt(\n"
            + "9-u^2)/3<v<Sqrt(9-u^2)/3)||(3/Sqrt(10)<=u<1&&-Sqrt(9-9*u^2)<v<Sqrt(9-9*u^2))");
  }

  @Test
  public void testEquationInThreeVariables() {
    check("CylindricalDecomposition(u^2 + v^2 + w^2 == 4 && u > 0 && v > 0 && w > 0, {u, v, w})", //
        "0<u<2&&0<v<Sqrt(4-u^2)&&w==Sqrt(4-u^2-v^2)");
  }

  @Test
  public void testParameters() {
    check("CylindricalDecomposition(a*t + b == 0, {a, b, t})", //
        "(a<0&&t==-b/a)||(a==0&&b==0)||(a>0&&t==-b/a)");
  }

  @Test
  public void testConjunctionsAndLists() {
    check("CylindricalDecomposition({u^2 + v^2 < 4, u > 0}, {u, v})", //
        "0<u<2&&-Sqrt(4-u^2)<v<Sqrt(4-u^2)");
    check("CylindricalDecomposition(u^2 + v^2 < 4 && u > 0, {u, v})", //
        "0<u<2&&-Sqrt(4-u^2)<v<Sqrt(4-u^2)");
    check("CylindricalDecomposition(u^2 + v^2 < 1 && u^2 + v^2 > 4, {u, v})", //
        "False");
  }

  @Test
  public void testThreeVariables() {
    check("CylindricalDecomposition(u^2 + v^2 + w^2 < 9, {u, v, w})", //
        "-3<u<3&&-Sqrt(9-u^2)<v<Sqrt(9-u^2)&&-Sqrt(9-u^2-v^2)<w<Sqrt(9-u^2-v^2)");
  }

  @Test
  public void testRootBounds() {
    check("CylindricalDecomposition(u^3 + v^3 < 8, {u, v})", //
        "v<Root(-8+u^3+#1^3&,1,0)");
  }

  @Test
  public void testRootWithAParameter() {
    // the variable of the factorization inside Root must not be taken for a parameter of that name
    check("Root(#^6 + (x^2 - 4)*#^4 + x^4 &, 1)", //
        "Root(#1^6+x^4+#1^4*(-4+x^2)&,1,0)");
    check("Root(#^6 + (x^2 - 4)*#^4 + x^4 &, 1) /. x -> 1/2 // N", //
        "-1.93534");
  }

  @Test
  public void testDeclines() {
    // no third argument of this kind, also not the domain of the solutions
    check("CylindricalDecomposition(u^2 + v^2 < 1, {u, v}, Reals)", //
        "CylindricalDecomposition(u^2+v^2<1,{u,v},Reals)");
    check("CylindricalDecomposition(u^2 + v^2 < 1, {u, v}, Complexes)", //
        "CylindricalDecomposition(u^2+v^2<1,{u,v},Complexes)");
    // a variable of the formula which isn't given
    check("CylindricalDecomposition(u^2 + v^2 < 1, u)", //
        "CylindricalDecomposition(u^2+v^2<1,u)");
    check("CylindricalDecomposition(u^2 + v^2 < 1, {u, 2})", //
        "CylindricalDecomposition(u^2+v^2<1,{u,2})");
    // a coefficient which isn't a rational number
    check("CylindricalDecomposition(Sin(u) + v^2 < 1, {u, v})", //
        "CylindricalDecomposition(v^2+Sin(u)<1,{u,v})");
  }

  @Test
  public void testReduceGainsTheDecomposition() {
    check("Reduce(u^2 + v^2 < 4, {u, v}, Reals)", //
        "-2<u<2&&-Sqrt(4-u^2)<v<Sqrt(4-u^2)");
    // the earlier decompositions keep their forms
    check("Reduce(u*v > 1, {u, v}, Reals)", //
        "(u<0&&v<1/u)||(u>0&&v>1/u)");
  }

  /** The formula against the statement, at exact random points. */
  private void assertSameSolutions(String statement, String variables, int dimension) {
    EvalEngine engine = EvalEngine.get();
    IExpr decomposition =
        engine.evaluate(engine.parse("CylindricalDecomposition(" + statement + ", " + variables + ")"));
    assertEquals(false, decomposition.isAST(S.CylindricalDecomposition), statement);
    IExpr original = engine.parse(statement);
    IExpr vars = engine.evaluate(engine.parse(variables));
    Random random = new Random(statement.hashCode());
    for (int i = 0; i < 300; i++) {
      IASTAppendable rules = F.ListAlloc(dimension);
      for (int j = 1; j <= dimension; j++) {
        rules.append(F.Rule(vars.getAt(j), F.QQ(random.nextInt(1601) - 800, 197L)));
      }
      IExpr expected = engine.evaluate(original.replaceAll(rules).orElse(original));
      IExpr actual = engine.evaluate(decomposition.replaceAll(rules).orElse(decomposition));
      assertEquals(expected.isTrue(), engine.evalTrue(actual),
          statement + " at " + rules + ": " + decomposition);
    }
  }

  @Test
  public void testSolutionsAgreeWithTheStatement() {
    assertSameSolutions("u^2/4 + v^2 < 1 && u^2 + v^2/4 < 1", "{u, v}", 2);
    assertSameSolutions("u^2 + v^2 < 1 || (u - 3)^2 + v^2 < 1", "{u, v}", 2);
    assertSameSolutions("u^4 + v^4 - 3*u*v + u < 1", "{u, v}", 2);
    assertSameSolutions("v^2 > u^3 - u", "{u, v}", 2);
    assertSameSolutions("u^2 + v^2 > 1 && u*v < 1", "{u, v}", 2);
    assertSameSolutions("v^3 + u*v + 1 > 0", "{u, v}", 2);
    assertSameSolutions("u^2 + v^2 + w^2 < 4 && w > u*v", "{u, v, w}", 3);
  }
}

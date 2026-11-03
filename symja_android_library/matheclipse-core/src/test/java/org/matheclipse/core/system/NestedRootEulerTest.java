package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.integrate.AntiderivativeCheck;
import org.matheclipse.core.interfaces.IExpr;

/**
 * A root nested on <code>Sqrt(quadratic)</code>: the Euler substitution rationalizes the conic, and
 * the rules integrate the root which is left. An answer is compared with the integrand after
 * differentiation, because its form isn't the point.
 */
public class NestedRootEulerTest extends ExprEvaluatorTestCase {

  /** <code>f</code> has an antiderivative, right at the given points. */
  private void checkAntiderivative(String f, String points) {
    check("Module({g=Integrate(" + f + ",x),f=" + f + "}, {FreeQ(g,Integrate), "
        + "Union(Table(Chop(N(D(g,x)-f/.x->p,30),10^(-15)), {p," + points + "}))})", //
        "{True,{0}}");
  }

  @Test
  public void testSquareLeadingCoefficient() {
    checkAntiderivative("Sqrt(9*x^2+7)/Sqrt(3*x^2+x*Sqrt(9*x^2+7))", "{1/4,5/4,7/2}");
    checkAntiderivative("Sqrt(3*x+Sqrt(9*x^2+2*x+7))/((2*x+7)*Sqrt(9*x^2+2*x+7))",
        "{1/4,5/4,7/2}");
    checkAntiderivative("(x^2+x-3)/Sqrt(3*x+Sqrt(9*x^2+2*x+7))", "{1/4,5/4,7/2}");
  }

  @Test
  public void testRootOnRootOnConic() {
    checkAntiderivative("Sqrt(Sqrt(Sqrt(9*x^2+7)+3*x)+2)", "{1/4,5/4,-7/2}");
  }

  @Test
  public void testSquareConstantCoefficient() {
    checkAntiderivative("Sqrt(Sqrt(3*x^2+16)+4)/Sqrt(3*x^2+16)", "{1/4,5/4,-7/2}");
    checkAntiderivative("Sqrt(1-x^2)/Sqrt(1-Sqrt(1-x^2))", "{1/4,1/2,-3/4,3/2*I-1/4}");
  }

  @Test
  public void testParametricCoefficients() {
    check(
        "Module({g=Integrate(Sqrt(Sqrt(p^2*x^2+q)+p*x)/Sqrt(p^2*x^2+q),x)}, "
            + "{FreeQ(g,Integrate), Chop(N(D(g,x)-Sqrt(Sqrt(p^2*x^2+q)+p*x)/Sqrt(p^2*x^2+q)"
            + "/.{p->2,q->5,x->3/4},30),10^(-15))})", //
        "{True,0}");
  }

  @Test
  public void testConicInRootOfVariable() {
    checkAntiderivative("Sqrt(5+2*Sqrt(x)+Sqrt(3*Sqrt(x)+4*x+7))", "{1/4,5/4,7/2}");
  }

  @Test
  public void testConicInRootOfVariableAtNegativeVariable() {
    // Sqrt(x) is imaginary for x<0, where the answer in x has to hold as well
    checkAntiderivative("Sqrt(5+2*Sqrt(x)+Sqrt(3*Sqrt(x)+4*x+7))", "{-1/4,-5/4,-7/2}");
  }

  @Test
  public void testNoSingularityAtZero() {
    // the substitution variable (y-c)/x is 0/0 at x=0, written differently it is not
    check("Module({g=Integrate(Sqrt(Sqrt(3*x^2+16)+4)/Sqrt(3*x^2+16),x)}, "
        + "{FreeQ(g,Indeterminate), NumberQ(N(g/.x->0))})", //
        "{True,True}");
  }

  @Test
  public void testCheckOffTheRealLine() {
    EvalEngine engine = EvalEngine.get();
    IExpr x = F.symbol("x");
    // the same values on the real line, but not off it
    IExpr onlyReal = engine.parse("1/(1+Abs(x)^2)");
    assertFalse(AntiderivativeCheck.agreesOffTheRealLine(F.ArcTan(x), onlyReal, x, 4, engine));
    assertTrue(AntiderivativeCheck.agreesOffTheRealLine(F.ArcTan(x), engine.parse("1/(1+x^2)"), x,
        4, engine));
    // an integrand without values says nothing, and that is not a yes
    assertFalse(
        AntiderivativeCheck.agreesOffTheRealLine(F.ArcTan(x), engine.parse("g(x)"), x, 4, engine));
  }

  @Test
  public void testNoSquareCoefficientStaysUnevaluated() {
    // neither coefficient of the quadratic is a square: no rational point to start from
    check("FreeQ(Integrate(Sqrt(x+Sqrt(2*x^2+3*x+5))/x,x),Integrate)", //
        "False");
  }
}

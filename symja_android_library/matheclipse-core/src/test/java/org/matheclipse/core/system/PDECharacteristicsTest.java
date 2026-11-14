package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Linear partial differential equations of first order by their characteristics: a characteristic
 * with several branches, the branch which meets a condition, three variables; and the zero test
 * of an expression with unknown functions which checks such answers.
 */
public class PDECharacteristicsTest extends ExprEvaluatorTestCase {

  @Test
  public void testCharacteristicWithTwoBranches() {
    // t(x) of t' == 1/(5*t) has two branches, x(t) one: E^(2*t) for every t, not E^(-2*Sqrt(t^2))
    check("DSolve(D(w(x, t), t) + 5*t*D(w(x, t), x) == 2*w(x, t), w(x, t), {x, t})", //
        "{{w(x,t)->E^(2*t)*C(1)[1/2*(-5*t^2+2*x)]}}");
    check("DSolve({D(w(x, t), t) + 5*t*D(w(x, t), x) == 2*w(x, t), w(x, 0) == g(x)}, w(x, t), {x, t})", //
        "{{w(x,t)->E^(2*t)*g(-5/2*t^2+x)}}");
  }

  @Test
  public void testBranchOfCondition() {
    // both roots are solutions
    check("Length(DSolve(u(x, y)*D(u(x, y), x) - u(x, y)*D(u(x, y), y) == u(x, y)^2 + 4*(x + y)^2, "
        + "u(x, y), {x, y}))", //
        "2");
    // and the condition chooses one
    check("DSolve({u(x, y)*D(u(x, y), x) - u(x, y)*D(u(x, y), y) == u(x, y)^2 + 4*(x + y)^2, "
        + "u(x, 0) == 3}, u(x, y), {x, y})", //
        "{{u(x,y)->Sqrt(-4*(x+y)^2+(9+4*(x+y)^2)/E^(2*y))}}");
    check("DSolve({u(x, y)*D(u(x, y), x) - u(x, y)*D(u(x, y), y) == u(x, y)^2 + 4*(x + y)^2, "
        + "u(x, 0) == -3}, u(x, y), {x, y})", //
        "{{u(x,y)->-Sqrt(-4*(x+y)^2+(9+4*(x+y)^2)/E^(2*y))}}");
  }

  @Test
  public void testDerivativeAsOneElementList() {
    check("DSolve(D(u(x, t), {t}) + 3*D(u(x, t), {x}) == 0, u(x, t), {x, t})", //
        "{{u(x,t)->C(1)[1/3*(3*t-x)]}}");
  }

  @Test
  public void testThreeVariables() {
    check("DSolve(2*D(w(x, y, z), x) + 3*D(w(x, y, z), y) - 5*D(w(x, y, z), z) == 0, w(x, y, z), {x, y, z})", //
        "{{w(x,y,z)->C(1)[-3/2*x+y,5/2*x+z]}}");
    check("DSolve(D(w(x, y, z), x) + p*x*D(w(x, y, z), y) + q*y*D(w(x, y, z), z) == 0, w(x, y, z), {x, y, z})", //
        "{{w(x,y,z)->C(1)[-1/2*p*x^2+y,1/3*p*q*x^3-q*x*y+z]}}");
    check("DSolve(x*D(w(x, y, z), x) + y*D(w(x, y, z), y) + z*D(w(x, y, z), z) == 0, w, {x, y, z})", //
        "{{w->Function({x,y,z},C(1)[y/x,z/x])}}");
  }

  @Test
  public void testThreeVariablesRightHandSide() {
    check("DSolve(x*D(w(x, y, z), x) + 2*y*D(w(x, y, z), y) + 3*z*D(w(x, y, z), z) == 4*w(x, y, z), "
        + "w(x, y, z), {x, y, z})", //
        "{{w(x,y,z)->x^4*C(1)[y/x^2,z/x^3]}}");
    check("DSolve(3*D(w(x, y, z), x) + D(w(x, y, z), y) - 2*D(w(x, y, z), z) == x*y, w(x, y, z), {x, y, z})", //
        "{{w(x,y,z)->1/54*x^2*(-x+9*y)+C(1)[-x/3+y,2/3*x+z]}}");
    check("DSolve(D(w(x, y, z), x) + y*D(w(x, y, z), y) + z*D(w(x, y, z), z) == w(x, y, z)^2, "
        + "w(x, y, z), {x, y, z})", //
        "{{w(x,y,z)->1/(-x-C(1)[y/E^x,z/E^x])}}");
    // not linear in the derivatives
    check("FreeQ(DSolve(D(w(x, y, z), x) + D(w(x, y, z), y)*D(w(x, y, z), z) == 0, w(x, y, z), "
        + "{x, y, z}), Rule)", //
        "True");
  }

  @Test
  public void testPossibleZeroQUnknownFunctions() {
    check("PossibleZeroQ(Csc(h(y))^2 - Cot(h(y))^2 - 1)", //
        "True");
    check("PossibleZeroQ(h(y) - h(z))", //
        "False");
    check("PossibleZeroQ(Sqrt(1 + 2*C(1) + C(1)^2)^2 - (1 + C(1))^2)", //
        "True");
    check("PossibleZeroQ(3*Derivative(1)[C(1)][t + x])", //
        "False");
  }

  @Test
  public void testPossibleZeroQPiecewiseConstant() {
    // the derivative is 0 and the value at 0 is 0, but not the value at -1
    check("Table(PossibleZeroQ(ArcTan(x) + ArcTan(1/x) - Pi/2), {8})//Union", //
        "{False}");
    check("Table(PossibleZeroQ(ArcTan(x) + ArcCot(x) - Pi/2), {8})//Union", //
        "{False}");
    // nor the value at 2
    check("Table(PossibleZeroQ(ArcTan(Tan(x)) - x), {8})//Union", //
        "{False}");
    // nor the value at 17
    check("Table(PossibleZeroQ(ArcTan(Tan(x/10)) - x/10), {8})//Union", //
        "{False}");
    check("{PossibleZeroQ(ArcSin(x) + ArcCos(x) - Pi/2), PossibleZeroQ(Sin(x)^2 + Cos(x)^2 - 1)}", //
        "{True,True}");
  }

  @Test
  public void testPossibleZeroQPreciseSamples() {
    // every variable with the same value is a pole of 1/(a - b)
    check("PossibleZeroQ(E^(a*x + b*x)/(a - b) - E^(a*x)*E^(b*x)/(a - b) + q/(a - b) - q/(a - b))", //
        "True");
    check("PossibleZeroQ(Gamma(x + 1) - x*Gamma(x))", //
        "True");
    // true on a part of the plane only
    check("PossibleZeroQ(Sqrt(x*y) - Sqrt(x)*Sqrt(y))", //
        "False");
    check("PossibleZeroQ(Log(x*y) - Log(x) - Log(y))", //
        "False");
    check("PossibleZeroQ((y^3)^(1/3) - y)", //
        "False");
  }
}

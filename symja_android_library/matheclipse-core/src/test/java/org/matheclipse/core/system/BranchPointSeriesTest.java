package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * <code>Series</code> and <code>Limit</code> of <code>Log</code>, <code>ArcTanh</code> and
 * <code>ArcCoth</code> at a point where the argument of a logarithm vanishes.
 */
public class BranchPointSeriesTest extends ExprEvaluatorTestCase {

  @Test
  public void testArcTanhAtItsBranchPoint() {
    // the argument tends to the branch point 1: (Log(1+u)-Log(1-u))/2, where Log(1-u) is Log(a)
    check("Series(ArcTanh(1-a), {a,0,2})", //
        "Log(2)/2-Log(a)/2-a/4-a^2/16+O(a)^3");
    check("Series(ArcTanh(Sqrt(1-a^2)), {a,0,2})", //
        "Log(2)/2-Log(a^2/2)/2-a^2/4+O(a)^3");
    check("N(Normal(Series(ArcTanh(Sqrt(1-a^2)), {a,0,2})) /. a->1/100)", //
        "5.29829");
    check("N(ArcTanh(Sqrt(1-a^2)) /. a->1/100)", //
        "5.29829");
    check("Series(ArcCoth(1+a^2), {a,0,2})", //
        "Log(2)/2-Log(a^2)/2+a^2/4+O(a)^3");
    check("Series(ArcTanh(Cos(x)), {x,0,2})", //
        "Log(2)/2-Log(x^2/2)/2-x^2/12+O(x)^3");
  }

  @Test
  public void testLogOfAVanishingArgument() {
    // the argument of the logarithm vanishes: Log(c*x^r)+Log(1+h)
    check("Series(Log(1-Sqrt(1-a^2)), {a,0,2})", //
        "Log(a^2/2)+a^2/4+O(a)^3");
    check("Series(Log(Sin(x)), {x,0,4})", //
        "Log(x)-x^2/6-x^4/180+O(x)^5");
    check("Series(Log(1-Cos(x)), {x,0,4})", //
        "Log(x^2/2)-x^2/12-x^4/1440+O(x)^5");
    check("Series(Log(x^2+x^3), {x,0,3})", //
        "Log(x^2)+x-x^2/2+x^3/3+O(x)^4");
    check("Series(Log(Sqrt(x)+x), {x,0,2})", //
        "Log(x)/2+Sqrt(x)-x/2+x^(3/2)/3-x^2/4+O(x)^(5/2)");
    check("Series(Log(2*x), {x,0,2})", //
        "Log(2)+Log(x)+O(x)^3");
    check("Series(Log(Sin(x))/x, {x,0,2})", //
        "Log(x)/x-x/6+O(x)^3");
    check("N(Normal(Series(Log(1-Cos(x)), {x,0,4})) /. x->1/10)", //
        "-5.29915");
    check("N(Log(1-Cos(x)) /. x->1/10)", //
        "-5.29915");
  }

  @Test
  public void testBothSidesOfThePoint() {
    // Log(x^2) is not 2*Log(x) for a negative x
    check("Series(Log(x^2), {x,0,1})", //
        "Log(x^2)+O(x)^2");
    check("Series(Log(-x), {x,0,1})", //
        "Log(-x)+O(x)^2");
    check("N(Normal(Series(ArcTanh(Sqrt(1-a^2)), {a,0,2})) /. a->-1/100)", //
        "5.29829");
    check("N(ArcTanh(Sqrt(1-a^2)) /. a->-1/100)", //
        "5.29829");
    // the limit was ConditionalExpression(0, a>1&&a<1): a condition which never holds
    check("Limit(Sin(x)^(a-1)*(Sin(x)^2)^((1-a)/2), x->0)", //
        "Limit((Sin(x)^2)^(1/2*(1-a))/Sin(x)^(1-a),x->0)");
  }

  @Test
  public void testRegularPoints() {
    check("Series(Log(x), {x,0,2})", //
        "Log(x)+O(x)^3");
    check("Series(Log(1+x), {x,0,2})", //
        "x-x^2/2+O(x)^3");
    check("Series(Log(x), {x,1,2})", //
        "(-1+x)-(1-x)^2/2+O(-1+x)^3");
    check("Series(ArcTanh(x), {x,0,3})", //
        "x+x^3/3+O(x)^4");
  }

  @Test
  public void testLimit() {
    // the two logarithms cancel
    check("Limit(Log(a)+ArcTanh(Sqrt(1-a^2)), a->0, Direction->-1)", //
        "Log(2)");
    check("N(Log(a)+ArcTanh(Sqrt(1-a^2)) /. a->10^-6)", //
        "0.693214");
    check("Limit(ArcTanh(1-x)+Log(x)/2, x->0, Direction->-1)", //
        "Log(2)/2");
    check("Limit(ArcCoth(1+x^2)+Log(x), x->0, Direction->-1)", //
        "Log(2)/2");
    check("Limit(ArcTanh(Cos(x))+Log(x), x->0, Direction->-1)", //
        "Log(2)");
    check("Limit(ArcTanh(1-x)/Log(x), x->0, Direction->-1)", //
        "-1/2");
    check("Limit(ArcTanh(Sqrt(1-a^2)), a->0)", //
        "Infinity");
    // from the left Log(a) has the imaginary part Pi: the two sides differ
    check("Limit(Log(a)+ArcTanh(Sqrt(1-a^2)), a->0)", //
        "Indeterminate");
  }
}

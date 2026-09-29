package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** NMinimize/NMaximize with constraints, and FindMinimum when MaxIterations runs out (WMA). */
public class NumericOptimizationTest extends ExprEvaluatorTestCase {

  @Test
  public void testNMinimizeConstraints() {
    // the constraints were ignored: the unconstrained optimum {x->1,y->2} violates x+y<=2
    check("{f, r} = NMaximize({-(x-1)^2-(y-2)^2, x+y<=2, x>=0}, {x,y}); "
        + "{Round(f, 10^-6), Round({x,y} /. r, 10^-6)}", //
        "{-1/2,{1/2,3/2}}");
    check("{f, r} = NMaximize({-(x-1)^2-(y-2)^2, x+y<=2 && x>=0}, {x,y}); Round(f, 10^-6)", //
        "-1/2");
    check("{f, r} = NMinimize({(x-1)^2+(y-2)^2, x+y<=2, x>=0}, {x,y}); Round(f, 10^-6)", //
        "1/2");
    // (5*Sqrt(2)-1)^2 on the unit disk
    check("{f, r} = NMinimize({(x-5)^2+(y-5)^2, x^2+y^2<=1}, {x,y}); "
        + "Abs(f - (5*Sqrt(2)-1)^2) < 10^-5", //
        "True");
  }

  @Test
  public void testNMinimizeLinearProgram() {
    // the variables are free, not non-negative: -3, not 0
    check("NMinimize({x+y, x>=-1 && y>=-2}, {x,y})", //
        "{-3.0,{x->-1.0,y->-2.0}}");
    check("NMinimize({x+y, x>=-1, y>=-2}, {x,y})", //
        "{-3.0,{x->-1.0,y->-2.0}}");
    check("NMaximize({x+2*y, x+y<=4 && x>=0 && y>=0}, {x,y})", //
        "{8.0,{x->0.0,y->4.0}}");
  }

  @Test
  public void testFindMinimumMaxIterations() {
    // WMA: FindMinimum::cvmit and the point reached, not the call unevaluated
    check("Head(FindMaximum(-(1-x)^2-100*(y-x^2)^2, {{x,-1.2},{y,1}}, MaxIterations->2))", //
        "List");
    // a search running off to infinity has no point to give
    check("FindMinimum(x, {x, 1})", //
        "FindMinimum(x,{x,1})");
    // whatever a constrained search answers satisfies the constraint
    check("r = FindMinimum({(x-1)^2+(y-2)^2, x+y<=2}, {{x,0},{y,0}}, MaxIterations->1); "
        + "If(Head(r) === List, (x + y /. r[[2]]) <= 2 + 10^-6, True)", //
        "True");
  }
}

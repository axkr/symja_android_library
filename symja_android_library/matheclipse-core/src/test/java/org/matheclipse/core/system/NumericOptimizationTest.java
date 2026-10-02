package org.matheclipse.core.system;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** NMinimize/NMaximize with constraints, and FindMinimum when MaxIterations runs out . */
public class NumericOptimizationTest extends ExprEvaluatorTestCase {

  @Test
  public void testNMinimizeConstraints() {
    // the constraints were ignored: the unconstrained optimum {x->1,y->2} violates x+y<=2
    check(
        "{f, r} = NMaximize({-(x-1)^2-(y-2)^2, x+y<=2, x>=0}, {x,y}); "
            + "{Round(f, 10^-6), Round({x,y} /. r, 10^-6)}", //
        "{-1/2,{1/2,3/2}}");
    check("{f, r} = NMaximize({-(x-1)^2-(y-2)^2, x+y<=2 && x>=0}, {x,y}); Round(f, 10^-6)", //
        "-1/2");
    check("{f, r} = NMinimize({(x-1)^2+(y-2)^2, x+y<=2, x>=0}, {x,y}); Round(f, 10^-6)", //
        "1/2");
    // (5*Sqrt(2)-1)^2 on the unit disk
    check(
        "{f, r} = NMinimize({(x-5)^2+(y-5)^2, x^2+y^2<=1}, {x,y}); "
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
    // FindMinimum::cvmit and the point reached, not the call unevaluated
    check("Head(FindMaximum(-(1-x)^2-100*(y-x^2)^2, {{x,-1.2},{y,1}}, MaxIterations->2))", //
        "List");
    // a search running off to infinity has no point to give
    check("FindMinimum(x, {x, 1})", //
        "FindMinimum(x,{x,1})");
    // whatever a constrained search answers satisfies the constraint
    check(
        "r = FindMinimum({(x-1)^2+(y-2)^2, x+y<=2}, {{x,0},{y,0}}, MaxIterations->1); "
            + "If(Head(r) === List, (x + y /. r[[2]]) <= 2 + 10^-6, True)", //
        "True");
  }

  @Test
  public void testFindMinimumObjectiveInASymbol() {
    // {0., {x -> 1., y -> 1.}} - the held objective is a symbol which stands for it
    check(
        "rosen = (1 - x)^2 + 100*(y - x^2)^2; "
            + "FindMinimum(rosen, {{x, -1.2}, {y, 1}})[[2]] // Chop", //
        "{x->1.0,y->1.0}");
    check("FindMinimum(rosen + x, {{x, -1.2}, {y, 1}})", //
        "{0.75,{x->0.5,y->0.25}}");
    check("Clear(rosen)", //
        "");
  }

  @Test
  public void testFindMinimumMethodNamesOf() {
    // "Newton", "QuasiNewton", "PrincipalAxis" and "InteriorPoint" are answered by the method
    // which is nearest to them
    check(
        "rosen = (1 - x)^2 + 100*(y - x^2)^2; Table(Max(Abs(({x, y} /. "
            + "FindMinimum(rosen, {{x, -1.2}, {y, 1}}, Method -> m)[[2]]) - 1)) < 10^-4, "
            + "{m, {\"Newton\", \"QuasiNewton\", \"PrincipalAxis\"}})", //
        "{True,True,True}");
    check(
        "First(FindMinimum({x + y, 3*x + 2*y >= 7 && x >= 0 && y >= 0}, {x, y}, "
            + "Method -> \"InteriorPoint\"))", //
        "2.33333");
    check("Clear(rosen)", //
        "");
  }

  @Test
  public void testFindMinimumNextToAPole() {
    // {0.885603, {x -> 1.46163}} - the line search jumped across the pole at 0
    check("FindMinimum(Gamma(x), {x, 1.5})", //
        "{0.885603,{x->1.46163}}");
  }

  @Test
  public void testFindMinimumDefaultStartInABox() {
    // {5., {x -> 1., y -> 2.}} - the default start value is outside of the box
    check("FindMinimum({(x - 3)^2 + (y - 3)^2, 0 <= x <= 1 && 0 <= y <= 2}, {x, y})", //
        "{5.0,{x->1.0,y->2.0}}");
  }

  @Test
  public void testFindMinimumEvaluationMonitor() {
    // the expression is evaluated at every evaluation of the function, the variable has its value
    check("n = 0; FindMinimum(Cos(x) + x/5, {x, 3}, EvaluationMonitor :> n++); n > 3", //
        "True");
    check(
        "pts = {}; FindMinimum((x - 2)^2, {x, 0}, EvaluationMonitor :> AppendTo(pts, x)); "
            + "{Head(First(pts)), x}", //
        "{Real,x}");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testWorkingPrecision() {
    // {x -> 1.461632144968362341262659542325721328468}
    check("FindRoot(PolyGamma(x), {x, 1.5}, WorkingPrecision -> 40)", //
        "{x->1.461632144968362341262659542325721328468}");
    // 0.8856031944108887002788159005825887332080
    check(
        "Abs(First(FindMinimum(Gamma(x), {x, 1.5}, WorkingPrecision -> 40)) - "
            + "0.8856031944108887002788159005825887332080`40) < 10^-38", //
        "True");
    check("FindRoot({x^2 + y^2 == 4, x == y}, {{x, 1}, {y, 1}}, WorkingPrecision -> 30)", //
        "{x->1.4142135623730950488016887242,y->1.4142135623730950488016887242}");
    // without a derivative the machine precision result is the answer
    check("FindRoot(Cos(x) == x, {x, 1})", //
        "{x->0.739085}");
  }

  @Test
  public void testNMaximizeOnAnInterval() {
    // {12.6059, {x -> 12.6453}} - the global maximum, not the first one next to the origin
    check("NMaximize({x*Cos(x), 0 <= x <= 16}, x)", //
        "{12.60593,{x->12.64529}}");
    // Rastrigin's function on a box which isn't centred: the minimum 0 at the origin
    check(
        "r = NMinimize({20 + x^2 - 10*Cos(2*Pi*x) + y^2 - 10*Cos(2*Pi*y), "
            + "-3 <= x <= 5.12 && -4 <= y <= 5.12}, {x, y}); "
            + "{First(r) < 10^-6, Max(Abs({x, y} /. r[[2]])) < 10^-4}", //
        "{True,True}");
  }

  @Test
  public void testNMinimizeNoPoints() {
    // NMinimize::nsol and {Infinity, {x -> Indeterminate, y -> Indeterminate}}
    check("NMinimize({x + y, x^2 + y^2 <= 1 && x + y >= 3}, {x, y})", //
        "{Infinity,{x->Indeterminate,y->Indeterminate}}", //
        "NMinimize: There are no points that satisfy the constraints {x^2+y^2<=1,x+y>=3}.");
    check("NMaximize({x + y, x >= 1 && x <= 0}, {x, y})", //
        "{-Infinity,{x->Indeterminate,y->Indeterminate}}", //
        "NMaximize: There are no points that satisfy the constraints {x>=1,x<=0}.");
  }

  @Test
  public void testNMinimizeAlternatives() {
    // {4., {x -> -2.}} - the first of two equal optima
    check("NMinimize({x^2, x <= -2 || x >= 2}, x)", //
        "{4.0,{x->-2.0}}");
  }

  @Test
  public void testNMinimizeIntegers() {
    // {3., {x -> 1, y -> 2}}
    check(
        "NMaximize({x + y, -2*x + 2*y >= 1 && -8*x + 10*y <= 13 && x >= 0 && y >= 0 && "
            + "Element({x, y}, Integers)}, {x, y})", //
        "{3.0,{x->1,y->2}}");
    // {0., {x -> 15., y -> 3}}
    check("NMinimize({(x - 15)^2 + (y - 3)^2, Element(y, Integers)}, {x, y})", //
        "{0.0,{x->15.0,y->3}}");
    // a knapsack
    check(
        "NMaximize({60*a + 100*b + 120*c, 10*a + 20*b + 30*c <= 50 && 0 <= a <= 1 && "
            + "0 <= b <= 1 && 0 <= c <= 1 && Element({a, b, c}, Integers)}, {a, b, c})", //
        "{220.0,{a->0,b->1,c->1}}");
  }

  @Test
  public void testNMinimizeOptionsAndManyVariables() {
    // a Method option is accepted
    check(
        "NMinimize(20 + x^2 - 10*Cos(2*Pi*x) + y^2 - 10*Cos(2*Pi*y), {x, y}, "
            + "Method -> \"SimulatedAnnealing\")", //
        "{0.0,{x->0.0,y->0.0}}");
    // {1.20568*10^-23, {x[1] -> 1., ..., x[5] -> 1.}}
    check(
        "NMinimize(Sum(100*(x(i + 1) - x(i)^2)^2 + (1 - x(i))^2, {i, 1, 4}), "
            + "Array(x, 5))[[2]] // Chop", //
        "{x(1)->1.0,x(2)->1.0,x(3)->1.0,x(4)->1.0,x(5)->1.0}");
  }
}

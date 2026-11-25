package org.matheclipse.core.system;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** NMinimize/NMaximize with constraints, and FindMinimum when MaxIterations runs out . */
public class NumericOptimizationTest extends ExprEvaluatorTestCase {

  @Test
  public void testFindRootStopsAtTheEdgeOfTheRegion() {
    // the root Sqrt(3) lies left of the region [2, 5]: the search ends on its left edge
    check("FindRoot(x^2 - 3, {x, 4, 2, 5})", //
        "{x->2.0}");
    check("FindRoot(x^3 - 20, {x, 1, 0, 2})", //
        "{x->2.0}");
    // a root inside the region
    check("FindRoot(x^2 - 3, {x, 4, 1, 5})", //
        "{x->1.73205}");
  }

  @Test
  public void testFindRootLineSearchInTheRegion() {
    // the Newton step from 0.2 overshoots the region [0, 2]; the search stays inside and finds
    // the root 1 of the function with a pole at 3/2
    check("FindRoot((x - 1)/(3/2 - x) - 0, {x, 1/5, 0, 13/10})", //
        "{x->1.0}");
    check("Reap(FindRoot(ArcTan(x - 1), {x, 3, 0, 4}, StepMonitor :> Sow(x)))[[1]]", //
        "{x->1.0}");
  }

  @Test
  public void testFindRootSteepNearAPole() {
    // near the pole at 1 the Newton steps are tiny while the root is still far away
    check("FindRoot(1/(1 - x) - 3, {x, 999999/1000000})", //
        "{x->0.666667}");
    check("FindRoot(1/(1 - x)^2 - 4, {x, 9999999/10000000}, MaxIterations -> 200)", //
        "{x->0.5}");
  }

  @Test
  public void testFindRootOfInterpolationWithDescendingAbscissas() {
    check("g = Interpolation({{4., 16.}, {3., 9.}, {2., 4.}, {1., 1.}, {0., 0.}, {-1., 1.}});"
        + "{g(2.5), g(0.5), g(-0.5)}", //
        "{6.25,0.25,0.25}");
    check("g = Interpolation({{4., 16.}, {3., 9.}, {2., 4.}, {1., 1.}, {0., 0.}, {-1., 1.}});"
        + "FindRoot(g(x) == 5, {x, 3.5, 1, 4})", //
        "{x->2.23607}");
  }

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
    // with bounds {x, x0, xmin, xmax}, which are passed to the optimizer as SimpleBounds
    check("Round({#[[1]], {x, y} /. #[[2]]} &[FindMinimum((x - 3)^2 + (y + 1)^2, "
        + "{{x, 0, -2, 3/2}, {y, 0, -1/2, 2}}, Method -> \"InteriorPoint\")], 10^-6)", //
        "{5/2,{3/2,-1/2}}");
    check("Round({#[[1]], {x, y} /. #[[2]]} &[FindMinimum({(x - 3)^2 + (y + 1)^2, x + y <= 0}, "
        + "{{x, 0, -2, 3/2}, {y, 0}}, Method -> \"InteriorPoint\")], 10^-6)", //
        "{5/2,{3/2,-3/2}}");
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
        "{x->1.41421356237309504880168872421,y->1.41421356237309504880168872421}");
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
  public void testNMaximizeOnAOneVariableConstraint() {
    // the origin is a stationary point of the objective: the maxima are on the boundary
    check("NMaximize({x^4, x^2 <= 9}, x)", //
        "{81.0,{x->3.0}}");
    check("NMinimize({-x^2 - 1, x^2 <= 1}, x)", //
        "{-2.0,{x->1.0}}");
    check("NMaximize({(x - 2)^2, x^2 <= 16}, x)", //
        "{36.0,{x->-4.0}}");
  }

  @Test
  public void testFindMinimumToMachinePrecision() {
    // the stationary point is {3.0000730576798784, -1.0471255378468650}, a search which stops on
    // the change of the function value ends at {3.0000473, -1.0471345}
    check("g = (x - 3)^2 + (y + 1)^4 + Cos(x*y); "
        + "Round(10^6*{x, y} /. FindMinimum(g, {{x, 0}, {y, 0}})[[2]])", //
        "{3000073,-1047126}");
  }

  @Test
  public void testFindMinimumPolishStaysInTheBasin() {
    // stationary points every Pi/200: the polished point is the minimum the search ended next to
    check("g = Cos(200*x) + x^2/1000; "
        + "a = x /. FindMinimum(g, {x, 0.02})[[2]]; "
        + "b = x /. FindMinimum(g, {x, 0.02}, Method -> \"Powell\")[[2]]; "
        + "{Abs(a - b) < 10^-6, Abs(D(g, x) /. x -> a) < 10^-8}", //
        "{True,True}");
    check("h = Sin(300*x)*Sin(300*y) + (x^2 + y^2)/10^4; "
        + "a = {x, y} /. FindMinimum(h, {{x, 0.01}, {y, 0.01}})[[2]]; "
        + "b = {x, y} /. FindMinimum(h, {{x, 0.01}, {y, 0.01}}, Method -> \"Powell\")[[2]]; "
        + "Max(Abs(a - b)) < 10^-6", //
        "True");
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
  public void testFindMinimumDomainConstraints() {
    // a region is the inequalities of RegionMember
    check("FindMinimum({x - y, Element({x, y}, Disk({1, 1}, 2))}, {x, y})", //
        "{-2.82843,{x->-0.414214,y->2.41421}}");
    check("FindMaximum(x*y, Element({x, y}, Rectangle({-1, -2}, {3, 4})))", //
        "{12.0,{x->3.0,y->4.0}}");
    // a variable with a value is no start value of the region specification
    check("b = 5; FindMinimum(a^2 + b^2, Element({a, b}, Disk({2, 2}, 1)))[[1]]", //
        "3.34315");
    check("FindMinimum({(x - 4)^2 + y^2, " //
        + "Element({x, y}, Triangle({{0, 0}, {2, 0}, {0, 3}}))}, {x, y})", //
        "{4.0,{x->2.0,y->0.0}}");
    // a vector inequality compares the components; a scalar side every component
    check("FindMaximum({2*x + y, VectorLessEqual({{x, y}, {5, 7}})}, {x, y})", //
        "{17.0,{x->5.0,y->7.0}}");
    check("FindMinimum({x^2 + y^2 + z^2, VectorGreater({{x, y, z}, 2})}, {x, y, z})", //
        "{12.0,{x->2.0,y->2.0,z->2.0}}");
    // the cone of the componentwise order; another cone is no list of inequalities
    check("FindMinimum({x + y, " //
        + "VectorGreaterEqual({{x, y}, {4, -1}}, \"NonNegativeCone\")}, {x, y})", //
        "{3.0,{x->4.0,y->-1.0}}");
    check("Check(FindMinimum({x + y, VectorGreaterEqual({x, 1}, \"NormCone\")}, {x, y}), " //
        + "eqineq, FindMinimum::eqineq)", //
        "eqineq");
    // Interval and IntervalData of a single variable, also as the search specification
    check("FindMinimum(Exp(x) - 3*x, Element(x, Interval({2, 5})))", //
        "{1.38906,{x->2.0}}");
    check("FindMaximum({Sin(x), Element(x, IntervalData({0, Less, LessEqual, 1}))}, {x, 1/2})", //
        "{0.841471,{x->1.0}}");
    check("FindMinimum({(x + 2)^2, " //
        + "Element(x, IntervalData({-1, LessEqual, Less, Infinity}))}, {x, 3})", //
        "{1.0,{x->-1.0}}");
    // Element(..., Reals) is no restriction
    check("FindMinimum({(x - 1)^2 + (y - 2)^2, Element(x | y, Reals) && x == y}, {x, y})", //
        "{0.5,{x->1.5,y->1.5}}");
  }

  @Test
  public void testFindMinimumConstraintMessages() {
    // FindMinimum::eqineq names the constraint which is no relation:
    // Constraints in {x!=y} are not all equality or inequality constraints. ...
    check("FindMinimum({x^2 + y^2, x + y >= 1 && x != y}, {x, y})", //
        "FindMinimum({x^2+y^2,x+y>=1&&x!=y},{x,y})");
    check("Check(FindMinimum({x^2 + y^2, x + y >= 1 && x != y}, {x, y}), " //
        + "eqineq, FindMinimum::eqineq)", //
        "eqineq");
    // relations which are no real numbers at the start point are FindMinimum::nrnum, not eqineq:
    // a symbol which is no variable of the search - The Function value {0.75-w^2} ...
    check("Check(FindMinimum({x + y, x^2 + w^2 <= 1}, {{x, 1/2}, {y, 1/2}}), " //
        + "nrnum, FindMinimum::nrnum)", //
        "nrnum");
    check("Check(FindMinimum({x + y, x^2 + w^2 <= 1}, {{x, 1/2}, {y, 1/2}}), " //
        + "eqineq, FindMinimum::eqineq)", //
        "FindMinimum({x+y,x^2+w^2<=1},{{x,1/2},{y,1/2}})");
    // the default start {2., 2.} is outside the real domain of Sqrt
    check("Check(FindMinimum(x - y, " //
        + "Element({x, y}, ImplicitRegion(Sqrt(4 - x^2 - y^2) >= 1, {x, y}))), " //
        + "nrnum, FindMinimum::nrnum)", //
        "nrnum");
    check("Check(FindMinimum({x + y, Log(x) >= -1 && y >= 0}, {{x, -2}, {y, 1}}), " //
        + "nrnum, FindMinimum::nrnum)", //
        "nrnum");
  }

  @Test
  public void testFindMinimumInequalityChains() {
    // a chain of different relations is Inequality(a, LessEqual, y, Less, b)
    check("FindMaximum({x + 3*y, -1 <= x <= 2 && 0 <= y < 1}, {x, y})", //
        "{5.0,{x->2.0,y->1.0}}");
    check("FindMinimum({(x - 3)^2 + y^2, 0 < x <= 2 && -1 <= y < 1}, {x, y})", //
        "{1.0,{x->2.0,y->0.0}}");
    check("FindMinimum({(x - 1)^2 + (y - 2)^2, 0 <= x < y <= 1}, {x, y})", //
        "{1.0,{x->1.0,y->1.0}}");
    check("FindMaximum({Sin(x), 0 < x <= 1}, {x, 1/2})", //
        "{0.841471,{x->1.0}}");
    check("FindMinimum({x^2 + y^2, x + y == 1 < 2*y}, {x, y})", //
        "{0.5,{x->0.5,y->0.5}}");
    // the WorkingPrecision refinement and NMinimize read the chain too:
    // {x, y} = {8, 2}/Sqrt(17), the value 21 - 4*Sqrt(17)
    check("FindMinimum({(x - 4)^2 + (y - 1)^2, 1 < x^2 + y^2 <= 4}, {x, y}, " //
        + "WorkingPrecision -> 20)", //
        "{4.5075774975293578007,{x->1.9402850002906637882,y->0.48507125007266594704}}");
    check("NMinimize({(x - 4)^2 + (y - 1)^2, 1 <= x^2 + y^2 <= 4}, {x, y})", //
        "{4.50758,{x->1.94029,y->0.485071}}");
  }

  @Test
  public void testStrictConstraintRefinedOnItsBoundary() {
    // a strict inequality is read as the non-strict one, also by the WorkingPrecision refinement:
    // {x, y} = {8, 2}/Sqrt(17), the value 21 - 4*Sqrt(17)
    check("FindMinimum({(x - 4)^2 + (y - 1)^2, x^2 + y^2 < 4}, {x, y}, WorkingPrecision -> 20)", //
        "{4.5075774975293578007,{x->1.9402850002906637882,y->0.48507125007266594704}}");
    // {x, y} = -{1, 2}*Sqrt(3/5), the value -Sqrt(15)
    check("NMinimize({x + 2*y, x^2 + y^2 < 3}, {x, y}, WorkingPrecision -> 20)", //
        "{-3.8729833462074168852,{x->-0.77459666924148337704,y->-1.5491933384829667541}}");
  }

  @Test
  public void testRegionConstraintsRefinedAndGlobal() {
    // WorkingPrecision refines the machine point on the boundary of the region:
    // {x, y} = {8, 2}/Sqrt(17), the value 21 - 4*Sqrt(17)
    check("FindMinimum((x - 4)^2 + (y - 1)^2, Element({x, y}, Disk({0, 0}, 2)), " //
        + "WorkingPrecision -> 25)", //
        "{4.507577497529357800714361,{x->1.940285000290663788151252,"
            + "y->0.4850712500726659470378129}}");
    // x = y = z = Sqrt(3), the value 3*Sqrt(3)
    check("FindMaximum({x + y + z, Element({x, y, z}, Ball({0, 0, 0}, 3))}, {x, y, z}, " //
        + "WorkingPrecision -> 20)", //
        "{5.1961524227066318806,{x->1.7320508075688772935,y->1.7320508075688772935,"
            + "z->1.7320508075688772935}}");
    // NMinimize and NMaximize hand the region to the local search:
    // {x, y} = {1 - 3/Sqrt(5), 6/Sqrt(5)}, the value 1 - 3*Sqrt(5)
    check("NMinimize({x - 2*y, Element({x, y}, Disk({1, 0}, 3))}, {x, y})", //
        "{-5.7082,{x->-0.341641,y->2.68328}}");
    check("NMinimize({x - 2*y, Element({x, y}, Disk({1, 0}, 3))}, {x, y}, " //
        + "WorkingPrecision -> 20)", //
        "{-5.7082039324993690892,{x->-0.34164078649987381785,y->2.6832815729997476357}}");
    check("NMaximize({x*y*z, Element({x, y, z}, Cuboid({0, 0, 0}, {1, 2, 3}))}, {x, y, z})", //
        "{6.0,{x->1.0,y->2.0,z->3.0}}");
    check("FindArgMin((x + 1)^2 + y^2, Element({x, y}, Rectangle({0, -1}, {2, 1})))", //
        "{0.0,0.0}");
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
  @Tag(TestTags.SLOW)
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

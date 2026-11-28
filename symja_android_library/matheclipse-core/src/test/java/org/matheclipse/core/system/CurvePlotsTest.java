package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Tests for the constructed curves KochCurve, HilbertCurve, PeanoCurve, SierpinskiCurve and
 * AnglePath3D, and for RulePlot.
 */
public class CurvePlotsTest extends ExprEvaluatorTestCase {

  @Test
  public void testHilbertCurve() {
    check("HilbertCurve(1)", //
        "Line({{0,0},{0,1},{1,1},{1,0}})");
    // every point of the 4x4 grid once, each next to the one before
    check("With({p = First(HilbertCurve(2))}, {Length(Union(p)), Union(Norm /@ Differences(p))})", //
        "{16,{1}}");
    check("Last(First(HilbertCurve(3)))", //
        "{7,0}");
    check("HilbertCurve(1, DataRange -> {{-2, 2}, {0, 1}})", //
        "Line({{-2.0,0.0},{-2.0,1.0},{2.0,1.0},{2.0,0.0}})");
    check("HilbertCurve(0)", //
        "HilbertCurve(0)");
    check("HilbertCurve(u)", //
        "HilbertCurve(u)");
    check("HilbertCurve(2.0)", //
        "HilbertCurve(2.0)");
    // no argument, or an order above the largest one computed
    check("HilbertCurve()", //
        "HilbertCurve()");
    check("HilbertCurve(12)", //
        "HilbertCurve(12)");
  }

  @Test
  public void testPeanoCurve() {
    check("PeanoCurve(1)", //
        "Line({{0,0},{1,0},{2,0},{2,1},{1,1},{0,1},{0,2},{1,2},{2,2}})");
    check("With({p = First(PeanoCurve(2))}, {Length(Union(p)), Union(Norm /@ Differences(p)), Last(p)})", //
        "{81,{1},{8,8}}");
    check("PeanoCurve(-1)", //
        "PeanoCurve(-1)");
  }

  @Test
  public void testSierpinskiCurve() {
    check("Take(First(SierpinskiCurve(1)), 4)", //
        "{{0,0},{32,-32},{96,-32},{128,0}}");
    // a closed curve of diagonal and straight steps
    check("With({p = First(SierpinskiCurve(2))}, {First(p) == Last(p), Union(Abs /@ Flatten(Differences(p)))})", //
        "{True,{0,32,64}}");
    check("Length(First(SierpinskiCurve(2)))", //
        "65");
    check("With({p = First(SierpinskiCurve(2))}, {MinMax(p[[All, 1]]), MinMax(p[[All, 2]])})", //
        "{{-32,416},{-448,0}}");
    // an option other than DataRange keeps the curve at the origin
    check("First(SierpinskiCurve(1, PlotStyle -> Red))[[{1, -1}]]", //
        "{{0,0},{0,0}}");
    check("SierpinskiCurve()", //
        "SierpinskiCurve()");
    // DataRange maps the bounding square
    check("MinMax /@ Transpose(First(SierpinskiCurve(2, DataRange -> {{0, 2}, {0, 1}})))", //
        "{{0.0,2.0},{0.0,1.0}}");
  }

  @Test
  public void testKochCurve() {
    check("KochCurve(0)", //
        "Line({{0.0,0.0},{1.0,0.0}})");
    check("KochCurve(1)", //
        "Line({{0.0,0.0},{0.333333,0.0},{0.5,0.288675},{0.666667,0.0},{1.0,0.0}})");
    check("Length(First(KochCurve(3)))", //
        "65");
    // a square bump as generator
    check("KochCurve(1, {0, Pi/2, -Pi/2, -Pi/2, Pi/2})", //
        "Line({{0.0,0.0},{0.333333,0.0},{0.333333,0.333333},{0.666667,0.333333},{0.666667,0.0},{1.0,0.0}})");
    // a generator which returns to its start has no end to scale to
    check("KochCurve(1, {0, Pi})", //
        "KochCurve(1,{0,Pi})");
    check("KochCurve(-1)", //
        "KochCurve(-1)");
    // at most about a million points: order 9 for the four steps of the default generator, order
    // 4 for twenty steps
    check("KochCurve(10)", //
        "KochCurve(10)");
    check("KochCurve(5, Table(0, {20}))", //
        "KochCurve(5,{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0})");
    check("Length(First(KochCurve(2, Table(0, {20}))))", //
        "401");
    check("Length(First(KochCurve(2, {0, Pi/3, -2*Pi/3, Pi/3})))", //
        "17");
  }

  @Test
  public void testAnglePath3D() {
    check("AnglePath3D({{Pi/2, 0, 0}, {0, Pi/2, 0}})", //
        "{{0,0,0},{0,1,0},{0,1,-1}}");
    check("AnglePath3D({{2, {0, 0, 0}}, {1, {Pi, 0, 0}}})", //
        "{{0,0,0},{2,0,0},{1,0,0}}");
    // a turn about the x axis keeps the direction
    check("AnglePath3D({{0, 0, Pi/3}, {0, 0, Pi/5}})", //
        "{{0,0,0},{1,0,0},{2,0,0}}");
    check("AnglePath3D({{Pi/4, Pi/3, 0}})[[2]] // Simplify", //
        "{1/(2*Sqrt(2)),1/(2*Sqrt(2)),-Sqrt(3)/2}");
    check("AnglePath3D(x)", //
        "AnglePath3D(x)");
  }

  @Test
  public void testRulePlot() {
    // eight icons of four cells for an elementary rule
    check("Length(Cases(RulePlot(CellularAutomaton(30)), _Rectangle, Infinity))", //
        "32");
    // the new cells of rule 30 = 2^^00011110, from the neighbourhood 111 down to 000: the lowest
    // cell of each icon
    check("Cases(RulePlot(CellularAutomaton(30)), {g_GrayLevel, Rectangle({_, y_ /; y < 0.2}, _)} :> g, Infinity)", //
        "{GrayLevel(1.0),GrayLevel(1.0),GrayLevel(1.0),GrayLevel(0.0),GrayLevel(0.0),GrayLevel(0.0),GrayLevel(0.0),GrayLevel(1.0)}");
    // the first icon in the first grid cell: the cells 4/13 high, 1/4 cell apart
    check("First(Cases(RulePlot(CellularAutomaton(90)), Rectangle(a_, b_) :> Round({a, b}, 0.001), Infinity))", //
        "{{0.154,0.538},{0.462,0.846}}");
    // a totalistic rule: one bar per sum, gray by the sum, above the new cell
    check("Cases(RulePlot(CellularAutomaton({10, {3, 1}})), {GrayLevel(g_), Rectangle({_, y_ /; y > 0.5}, _)} :> g, Infinity)", //
        "{0.0,0.166667,0.333333,0.5,0.666667,0.833333,1.0}");
    // three colours: 27 icons in three rows of 9 cells
    check("Union(Cases(RulePlot(CellularAutomaton({1000, 3})), Line({{0, y_}, _}) :> y, Infinity))", //
        "{0,1,2,3}");
    // the rules of a substitution system: A white, B black, the left side above the right side
    check("Cases(RulePlot(SubstitutionSystem({\"A\" -> \"AB\", \"B\" -> \"A\"})), {GrayLevel(g_), _Rectangle} :> g, Infinity)", //
        "{1.0,1.0,0.0,0.0,1.0}");
    check("Length(Cases(RulePlot(SubstitutionSystem({0 -> {0, 1, 0}, 1 -> {1, 1, 1}})), _Rectangle, Infinity))", //
        "8");
    check("SubstitutionSystem({\"A\" -> \"AB\"})", //
        "SubstitutionSystem({A->AB})");
    check("RulePlot(30)", //
        "RulePlot(30)");
  }
}

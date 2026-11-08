package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** The bars, curves and axes of the paired (back to back) charts. */
public class PairedChartsTest extends ExprEvaluatorTestCase {

  @Test
  public void testPairedBarChartAxes() {
    // the magnitude axes under both halves are labelled
    check("Cases(PairedBarChart({2, 6, 1}, {3, 1, 4}), Text(Style(t_, ___), ___) :> t, Infinity)", //
        "{0,2.5,5,0,2.5,5}");
  }

  @Test
  public void testPairedHistogram() {
    // the gap is sized for the bin labels between the halves
    check("g = PairedHistogram({1, 1, 2, 2, 2, 3}, {2, 3, 3, 4}); "
        + "{Union(Round(Abs(Cases(g, Rectangle(Offset(_, a_), Offset(_, b_), ___) :> b[[1]], "
        + "Infinity)), 10^-4)), Cases(g, Text(Style(Pane(v_), ___), ___) :> v, Infinity)}", //
        "{{2239/10000},{1,2,3,4}}");
  }

  @Test
  public void testPairedSmoothHistogram() {
    check("p = PairedSmoothHistogram({0, 1, 1, 2, 5}, {3, 4, 4, 6}); "
        + "{Head(p), Length /@ Cases(p, Line(l_) /; Length(l) > 20 :> l, Infinity), "
        + "Sign(Mean /@ Cases(p, Line(l_) /; Length(l) > 20 :> l[[All, 1]], Infinity))}", //
        "{Graphics,{401,401},{-1,1}}");
    check("Cases(PairedSmoothHistogram({0, 1, 1, 2, 5}, {3, 4, 4, 6}, 0.5), "
        + "Text(Style(v_, ___), Offset({3., 0.}, _), ___) :> v, Infinity)", //
        "{0,2,4,6}");
    check("PairedSmoothHistogram(a, {1, 2})", //
        "PairedSmoothHistogram(a,{1,2})");
  }

  @Test
  public void testTextOffsetMovesTheLabel() {
    // Offset({0, 30}, pos) draws the label 30 printer's points above pos
    check("StringCases(ExportString(Graphics({Text(\"zz\", Offset({0, 30}, {0, 0})), "
        + "Text(\"ww\", {0, 0})}, PlotRange -> {{-1, 1}, {-1, 1}}), \"SVG\"), "
        + "RegularExpression(\"<text x=\\\"[0-9.]+\\\" y=\\\"([0-9.]+)\\\"\") -> \"$1\")", //
        "{153.840,183.840}");
  }
}

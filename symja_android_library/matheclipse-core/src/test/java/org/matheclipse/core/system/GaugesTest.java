package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Tests for AngularGauge, HorizontalGauge and ThermometerGauge. */
public class GaugesTest extends ExprEvaluatorTestCase {

  @Test
  public void testGaugeDefinitions() {
    check("Attributes(AngularGauge)", //
        "{Protected,ReadProtected}");
    check("Options(AngularGauge)", //
        "{GaugeLabels->None,GaugeStyle->Automatic,ScaleDivisions->Automatic,ScaleRanges->Automatic}");
  }

  @Test
  public void testAngularGauge() {
    check("Head(AngularGauge(3.5, {0, 5}))", //
        "Graphics");
    // one needle per value; a value which is no number draws none
    check("Length(Cases(AngularGauge({1, 4}, {0, 5}), _Polygon, Infinity))", //
        "2");
    check("Length(Cases(AngularGauge(u, {0, 5}), _Polygon, Infinity))", //
        "0");
    // the needles are red, then blue
    check("Cases(AngularGauge({1, 4}, {0, 5}), {c_RGBColor, _Polygon} :> c, Infinity)", //
        "{RGBColor(0.73,0.245,0.197),RGBColor(0.196,0.502,0.729)}");
    // the round values of the scale, with one number of decimals
    check("Cases(AngularGauge(0.25), Text(s_, ___) :> s, Infinity)", //
        "{0,0.2,0.4,0.6,0.8,1.0}");
    // about three steps of a round width
    check("Cases(AngularGauge(7, {0, 12}, ScaleDivisions -> 3), Text(s_, ___) :> s, Infinity)", //
        "{0,5,10}");
    // the minimum at 240 degrees, the maximum at -60 degrees
    check("Cases(AngularGauge(7, {0, 10}, ScaleDivisions -> 1), Text(_, p_, ___) :> Round(ArcTan @@ p / Degree), Infinity)", //
        "{-120,-60}");
    // the first value in a box
    check("Cases(AngularGauge({3, 7}, {0, 10}, GaugeLabels -> \"Value\"), Text(s_, ___) :> s, Infinity)[[-1]]", //
        "3.0");
    // a decreasing range is sorted
    check("Cases(AngularGauge(1, {3, 2}, ScaleDivisions -> 2), Text(s_, ___) :> s, Infinity)", //
        "{2.0,2.5,3.0}");
    check("AngularGauge(1, {2, 2})", //
        "AngularGauge(1,{2,2})");
    // more divisions than can be told apart give the automatic ticks
    check("Cases(AngularGauge(0.5, ScaleDivisions -> 10^9), Text(s_, ___) :> s, Infinity)", //
        "{0,0.2,0.4,0.6,0.8,1.0}");
  }

  @Test
  public void testHorizontalGauge() {
    // a triangle marker with its tip at the value, as a share of the range
    check("Cases(HorizontalGauge(6, {0, 8}), Polygon(p_) :> p[[1, 1]], Infinity)", //
        "{0.75}");
    check("Length(Cases(HorizontalGauge(0.5, ScaleDivisions -> None), _Text, Infinity))", //
        "0");
    // a scale range is a bar along the line
    check("Cases(HorizontalGauge(0.5, ScaleRanges -> {{0.6, 0.9}}), Rectangle(a_, b_) :> {a[[1]], b[[1]]}, Infinity)", //
        "{{0.6,0.9}}");
  }

  @Test
  public void testThermometerGauge() {
    check("Cases(ThermometerGauge(37, {30, 45}), Text(s_, ___) :> s, Infinity)", //
        "{30,35,40,45}");
    // the liquid rises to the value
    check("Max(Cases(ThermometerGauge(36, {30, 45}), {_RGBColor, Rectangle(a_, b_)} :> b[[2]], Infinity))", //
        "0.4");
    check("Cases(ThermometerGauge(4, {0, 10}, GaugeLabels -> \"Value\", ScaleDivisions -> 5), Text(s_, ___) :> s, Infinity)", //
        "{4.0,0,2,4,6,8,10}");
    check("Head(ThermometerGauge(w))", //
        "Graphics");
    // a label without a value sits at the bottom of the scale
    check("Cases(ThermometerGauge(w, GaugeLabels -> \"Temp\"), Text(\"Temp\", p_, ___) :> p, Infinity)", //
        "{{0.17,0.0}}");
  }
}

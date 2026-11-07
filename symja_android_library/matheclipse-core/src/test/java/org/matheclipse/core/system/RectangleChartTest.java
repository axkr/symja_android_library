package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** The colours of the bars of a rectangle chart. */
public class RectangleChartTest extends ExprEvaluatorTestCase {

  @Test
  public void testNamedSchemeIsSampledEvenlyOverTheBars() {
    // the default chart colour comes first, then the bars at 0, 1/3, 2/3 and 1 of the scheme
    check("c = Cases(RectangleChart({{2, 5}, {1, 1}, {3, 4}, {1, 2}}, "
        + "ChartStyle -> \"TemperatureMap\"), _RGBColor, Infinity); "
        + "{Length(c), Length(Union(c)), First(c), "
        + "Round(Take(List @@@ c[[{2, 5}]], All, 3), 0.001) == "
        + "Round(Take(List @@@ {ColorData(\"TemperatureMap\")[0], "
        + "ColorData(\"TemperatureMap\")[1]}, All, 3), 0.001)}", //
        "{5,5,RGBColor(1.0,0.835,0.25),True}");
    // a lone bar takes the start of the scheme
    check("Round(Take(List @@@ Cases(RectangleChart({{2, 5}}, ChartStyle -> \"SunsetColors\"), "
        + "{c_RGBColor, _Rectangle} :> c, Infinity), All, 3), 0.001) == "
        + "Round(Take(List @@@ {ColorData(\"SunsetColors\")[0]}, All, 3), 0.001)", //
        "True");
    // without a ChartStyle one dataset is drawn in one colour
    check("Length(Union(Cases(RectangleChart({{2, 5}, {1, 1}, {3, 4}}), _RGBColor, Infinity)))", //
        "1");
  }

  @Test
  public void testBaselineAndDatasetEndTicks() {
    // a baseline at height 0 and a tick at either end of the dataset
    check("Cases(RectangleChart({{3, 1}, {1, 4}, {2, 2}, {1, -3}}), _Line, Infinity)[[1]]", //
        "Line({{-0.14576,0},{7.43668,0}})");
    check("Cases(RectangleChart({{3, 1}, {1, 4}, {2, 2}, {1, -3}}), "
        + "Line({p_, Offset({0, -6}, p_)}) :> p, Infinity)", //
        "{{0.0,0},{7.288,0}}");
    // two ticks per dataset, drawn in thin gray before the bars
    check("Module({rc = RectangleChart({{{2, 3}, {1, 1}}, {{4, 2}}, {{1, 5}, {2, 1}, {1, 1}}})}, "
        + "{Length(Cases(rc, _Line, Infinity)), Length(Cases(rc, Offset({0, -6}, _), Infinity)), "
        + "Cases(rc, {GrayLevel(gl_), AbsoluteThickness(th_), __Line} :> {gl, th}, Infinity), "
        + "Position(First(rc), _Rectangle, Infinity)[[1, 1]] > 2})", //
        "{7,6,{{0.5,0.5}},True}");
    // a frame or switched off axes leave the baseline out
    check("Table(Length(Cases(RectangleChart({{3, 1}, {1, 4}}, opt), _Line, Infinity)), "
        + "{opt, {Frame -> True, Axes -> None, Axes -> {None, False}, Axes -> {False, True}}})", //
        "{0,0,0,3}");
  }
}

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
}

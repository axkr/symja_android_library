package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** The colour cycle of plots, the colours of a PlotTheme and a PlotStyle colour. */
public class PlotPaletteTest extends ExprEvaluatorTestCase {

  @Test
  public void testDefaultPaletteDecimals() {
    // the colours carry the decimals they are written with, not widened floats
    check("Cases(ListLinePlot({{2, 1, 5}, {0, 4, 2}, {3, 3, 1}, {1, 1, 4}}), _RGBColor, "
        + "Infinity)", //
        "{RGBColor(0.24,0.6,0.8),RGBColor(0.95,0.627,0.1425),RGBColor(0.455,0.7,0.21),"
            + "RGBColor(0.922526,0.385626,0.209179)}");
    check("Cases(Plot({Sin(t), Cos(t)}, {t, 0, 2}), _RGBColor, Infinity)", //
        "{RGBColor(0.24,0.6,0.8),RGBColor(0.95,0.627,0.1425)}");
  }

  @Test
  public void testThemePalette() {
    check("Cases(ListLinePlot({{1, 5, 2}, {2, 2, 7}, {4, 4, 1}}, PlotTheme -> \"Business\"), "
        + "_RGBColor, Infinity)", //
        "{RGBColor(0.34398,0.49112,0.89936),RGBColor(0.97,0.606,0.081),"
            + "RGBColor(0.91,0.318,0.243)}");
    check("Cases(Plot({t^2, t^3, t}, {t, -1, 1}, PlotTheme -> \"Scientific\"), _RGBColor, "
        + "Infinity)", //
        "{RGBColor(0.9,0.36,0.054),RGBColor(0.365248,0.427802,0.758297),"
            + "RGBColor(0.945109,0.593901,0.0)}");
    check("First(Cases(ListPlot({3, 1, 2}, PlotTheme -> \"Marketing\"), _RGBColor, Infinity))", //
        "RGBColor(1.0,0.4,0.0)");
    // a theme without a palette of its own keeps the default colours
    check("First(Cases(ListPlot({3, 1, 2}, PlotTheme -> \"Minimal\"), _RGBColor, Infinity))", //
        "RGBColor(0.24,0.6,0.8)");
  }

  @Test
  public void testPlotStyleColorReplacesPalette() {
    // a colour of the style takes the place of the palette colour; an explicit style wins over
    // the theme
    check("Plot(Sin(t), {t, 0, 1}, PlotStyle -> Blue, PlotTheme -> \"Business\")[[1, 1]]", //
        "Directive(RGBColor(0,0,1),AbsoluteThickness(2.0))");
    check("ListLinePlot({{1, 2, 0}, {3, 1, 2}}, PlotStyle -> {GrayLevel(0.25), Hue(0.7)})"
        + "[[1, {1, 3}]]", //
        "{Directive(GrayLevel(0.25),AbsoluteThickness(2.0)),"
            + "Directive(Hue(0.7),AbsoluteThickness(2.0))}");
    // an alpha value becomes an Opacity in front of the plain colour
    check("Plot(Sin(t), {t, 0, 1}, PlotStyle -> RGBColor(0, 0.5, 1, 0.25))[[1, 1]]", //
        "Directive(Opacity(0.25),RGBColor(0,0.5,1),AbsoluteThickness(2.0))");
    // a thickness in a styling directive takes the place of the default one
    check("Plot(Sin(t), {t, 0, 1}, PlotStyle -> Directive(Green, AbsoluteThickness(5)))"
        + "[[1, 1]]", //
        "Directive(RGBColor(0,1,0),AbsoluteThickness(5))");
    check("Plot(Sin(t), {t, 0, 1}, PlotStyle -> Directive(Blue, Dashed))[[1, 1]]", //
        "Directive(RGBColor(0,0,1),Dashing({Small,Small}),AbsoluteThickness(2.0))");
    // a style without a colour keeps the palette colour
    check("Cases(Plot(Sin(t), {t, 0, 1}, PlotStyle -> Dotted), _RGBColor, Infinity)", //
        "{RGBColor(0.24,0.6,0.8)}");
  }

  @Test
  public void testAlphaOfEveryColorFormIsOpacity() {
    check("Plot(Cos(t), {t, 0, 1}, PlotStyle -> GrayLevel(0.75, 0.5))[[1, 1]]", //
        "Directive(Opacity(0.5),GrayLevel(0.75),AbsoluteThickness(2.0))");
    check("Plot(Cos(t), {t, 0, 1}, PlotStyle -> Hue(0.25, 0.5, 1, 0.75))[[1, 1]]", //
        "Directive(Opacity(0.75),Hue(0.25,0.5,1),AbsoluteThickness(2.0))");
    check("Plot(Cos(t), {t, 0, 1}, PlotStyle -> CMYKColor(0, 1, 1, 0, 0.5))[[1, 1]]", //
        "Directive(Opacity(0.5),CMYKColor(0,1,1,0),AbsoluteThickness(2.0))");
  }

  @Test
  public void testLegendTakesThemeColors() {
    // the legend shows the colours of the theme the curves are drawn with
    check("Last(ListLinePlot({{3, 1, 2}, {1, 3, 2}}, PlotTheme -> \"Web\", "
        + "PlotLegends -> {u, v}))[[1, 1]] === "
        + "Cases(ListLinePlot({{3, 1, 2}, {1, 3, 2}}, PlotTheme -> \"Web\"), _RGBColor, Infinity)", //
        "True");
    check("Last(ListLinePlot({{3, 1, 2}, {1, 3, 2}}, PlotTheme -> \"Web\", "
        + "PlotLegends -> {u, v}))[[1, 1]]", //
        "{RGBColor(0.790588,0.201176,0.0),RGBColor(0.192157,0.388235,0.807843)}");
    check("Head(Last(ListPlot({{3, 1}, {1, 3}}, PlotTheme -> \"Marketing\", "
        + "PlotLegends -> {u, v}))[[1]])", //
        "PointLegend");
    // an explicit style still colours the legend
    check("Last(ListLinePlot({{3, 1}, {1, 3}}, PlotTheme -> \"Web\", PlotStyle -> {Red, Blue}, "
        + "PlotLegends -> {u, v}))[[1, 1]]", //
        "{RGBColor(1.0,0.0,0.0),RGBColor(0.0,0.0,1.0)}");
  }
}

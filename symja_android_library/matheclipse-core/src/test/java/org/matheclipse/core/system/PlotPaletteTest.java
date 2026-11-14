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
    // a colour of the style takes the place of the palette colour and follows the remaining
    // default parts; an explicit style wins over the theme
    check("Plot(Sin(t), {t, 0, 1}, PlotStyle -> Blue, PlotTheme -> \"Business\")[[1, 1]]", //
        "Directive(Opacity(1.0),AbsoluteThickness(2),RGBColor(0,0,1))");
    check("ListLinePlot({{1, 2, 0}, {3, 1, 2}}, PlotStyle -> {GrayLevel(0.25), Hue(0.7)})"
        + "[[1, 1]]", //
        "Directive(PointSize(7/360),AbsoluteThickness(2),GrayLevel(0.25))");
    check("ListLinePlot({{1, 2, 0}, {3, 1, 2}}, PlotStyle -> {GrayLevel(0.25), Hue(0.7)})"
        + "[[1, 3]]", //
        "Directive(PointSize(7/360),AbsoluteThickness(2),Hue(0.7))");
    // an alpha value becomes an Opacity after the plain colour, in place of the default Opacity
    check("Plot(Sin(t), {t, 0, 1}, PlotStyle -> RGBColor(0, 0.5, 1, 0.25))[[1, 1]]", //
        "Directive(AbsoluteThickness(2),RGBColor(0,0.5,1),Opacity(0.25))");
    // a thickness in a styling directive takes the place of the default one
    check("Plot(Sin(t), {t, 0, 1}, PlotStyle -> Directive(Green, AbsoluteThickness(5)))"
        + "[[1, 1]]", //
        "Directive(Opacity(1.0),RGBColor(0,1,0),AbsoluteThickness(5))");
    check("Plot(Sin(t), {t, 0, 1}, PlotStyle -> Directive(Blue, Dashed))[[1, 1]]", //
        "Directive(Opacity(1.0),AbsoluteThickness(2),RGBColor(0,0,1),Dashing({Small,Small}))");
    // a style without a colour keeps the palette colour
    check("Cases(Plot(Sin(t), {t, 0, 1}, PlotStyle -> Dotted), _RGBColor, Infinity)", //
        "{RGBColor(0.24,0.6,0.8)}");
  }

  @Test
  public void testAlphaOfEveryColorFormIsOpacity() {
    check("Plot(Cos(t), {t, 0, 1}, PlotStyle -> GrayLevel(0.75, 0.5))[[1, 1]]", //
        "Directive(AbsoluteThickness(2),GrayLevel(0.75),Opacity(0.5))");
    check("Plot(Cos(t), {t, 0, 1}, PlotStyle -> Hue(0.25, 0.5, 1, 0.75))[[1, 1]]", //
        "Directive(AbsoluteThickness(2),Hue(0.25,0.5,1),Opacity(0.75))");
    check("Plot(Cos(t), {t, 0, 1}, PlotStyle -> CMYKColor(0, 1, 1, 0, 0.5))[[1, 1]]", //
        "Directive(AbsoluteThickness(2),CMYKColor(0,1,1,0),Opacity(0.5))");
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

  @Test
  public void testPlotStyleMergesIntoTheDefaultStyle() {
    // a part of the same head replaces the default part; the parts given follow the rest
    check("Plot(Cos(t), {t, 0, 2}, PlotStyle -> AbsoluteThickness(7))[[1, 1]]", //
        "Directive(Opacity(1.0),RGBColor(0.24,0.6,0.8),AbsoluteThickness(7))");
    // a relative thickness has another head, so the default absolute one stays in front of it
    check("Plot(Cos(t), {t, 0, 2}, PlotStyle -> Thickness(0.01))[[1, 1]]", //
        "Directive(Opacity(1.0),RGBColor(0.24,0.6,0.8),AbsoluteThickness(2),Thickness(0.01))");
    // the parts given keep their order
    check("Plot(Cos(t), {t, 0, 2}, PlotStyle -> Directive(AbsoluteThickness(1), Green))[[1, 1]]", //
        "Directive(Opacity(1.0),AbsoluteThickness(1),RGBColor(0,1,0))");
    // nested directives are flattened, never stacked on the default one
    check("Rest(Plot(Cos(t), {t, 0, 2}, PlotStyle -> Directive(Directive(Orange), "
        + "AbsoluteDashing({2, 3})))[[1, 1]])", //
        "Directive(AbsoluteThickness(2),RGBColor(1.0,0.5,0.0),AbsoluteDashing({2,3}))");
    check("Count(Plot(Cos(t), {t, 0, 2}, PlotStyle -> Dotted), "
        + "Directive(___, _Directive, ___), {0, Infinity})", //
        "0");
    // an Opacity of the style takes the place of the default one
    check("Plot(Cos(t), {t, 0, 2}, PlotStyle -> Opacity(0.4))[[1, 1]]", //
        "Directive(RGBColor(0.24,0.6,0.8),AbsoluteThickness(2),Opacity(0.4))");
    // for a single curve a list is the style of that curve, the last colour winning
    check("Plot(Cos(t), {t, 0, 2}, PlotStyle -> {Green, AbsoluteThickness(4)})[[1, 1]]", //
        "Directive(Opacity(1.0),RGBColor(0,1,0),AbsoluteThickness(4))");
    check("Plot(Cos(t), {t, 0, 2}, PlotStyle -> {Green, GrayLevel(0.3)})[[1, 1]]", //
        "Directive(Opacity(1.0),AbsoluteThickness(2),GrayLevel(0.3))");
    // a colour superseded by a later one still leaves the Opacity of its alpha value
    check("Plot(Cos(t), {t, 0, 2}, PlotStyle -> {GrayLevel(0.2, 0.6), Green})[[1, 1]]", //
        "Directive(AbsoluteThickness(2),Opacity(0.6),RGBColor(0,1,0))");
    check("Plot(Cos(t), {t, 0, 2}, PlotStyle -> {GrayLevel(0.2, 0.6), Opacity(0.9), Green})"
        + "[[1, 1]]", //
        "Directive(AbsoluteThickness(2),Opacity(0.9),RGBColor(0,1,0))");
    // a single dataset of a list plot takes the whole list as its style as well
    check("ListLinePlot({4, 1, 3}, PlotStyle -> {Green, AbsoluteThickness(5)})[[1, 1]]", //
        "Directive(PointSize(7/360),RGBColor(0,1,0),AbsoluteThickness(5))");
    check("ListStepPlot({4, 1, 3}, PlotStyle -> {Green, AbsoluteThickness(5)})[[1, 1, 1]]", //
        "Directive(RGBColor(0,1,0),AbsoluteThickness(5))");
    // a function without any real value keeps its place in the list of styles
    check("Cases(Plot({Sqrt(-2 - t^2), Cos(t)}, {t, 0, 2}, PlotStyle -> {Green, Gray}), "
        + "_Directive, Infinity)", //
        "{Directive(Opacity(1.0),AbsoluteThickness(2),GrayLevel(0.5))}");
    check("Cases(Plot({Cos(t), Sqrt(-2 - t^2)}, {t, 0, 2}, PlotStyle -> {Green, Gray}), "
        + "_Directive, Infinity)", //
        "{Directive(Opacity(1.0),AbsoluteThickness(2),RGBColor(0,1,0))}");
    // a style which is not a list is the style of every curve
    check("Union(Cases(Plot({Cos(t), t}, {t, 0, 2}, PlotStyle -> Green), _Directive, "
        + "Infinity))", //
        "{Directive(Opacity(1.0),AbsoluteThickness(2),RGBColor(0,1,0))}");
    // for several curves a list gives one entry per curve
    check("Cases(Plot({Cos(t), t}, {t, 0, 2}, PlotStyle -> {AbsoluteThickness(6), Thin}), "
        + "_Directive, Infinity)[[1]]", //
        "Directive(Opacity(1.0),RGBColor(0.24,0.6,0.8),AbsoluteThickness(6))");
    check("Cases(Plot({Cos(t), t}, {t, 0, 2}, PlotStyle -> {AbsoluteThickness(6), Thin}), "
        + "_Directive, Infinity)[[2, 3 ;;]]", //
        "Directive(AbsoluteThickness(2),Thickness(Tiny))");
  }

  @Test
  public void testDefaultStyleOfEachKindOfPlot() {
    check("ParametricPlot({Cos(3*t), Sin(2*t)}, {t, 0, 1}, PlotStyle -> AbsoluteThickness(6))"
        + "[[1, 1, {1, 2, 4}]]", //
        "Directive(Opacity(1.0),FaceForm(Opacity(0.3)),AbsoluteThickness(6))");
    check("ListPlot({2, 5, 1, 4}, PlotStyle -> AbsoluteThickness(1))[[1, 1]]", //
        "Directive(PointSize(0.0128333),RGBColor(0.24,0.6,0.8),AbsoluteThickness(1))");
    check("ListLinePlot({2, 5, 1, 4}, PlotStyle -> AbsoluteThickness(1))[[1, 1]]", //
        "Directive(PointSize(7/360),RGBColor(0.24,0.6,0.8),AbsoluteThickness(1))");
    check("Cases(First(Plot(Cos(t), {t, 0, 2}, PlotStyle -> AbsoluteThickness(7))), "
        + "_AbsoluteThickness, Infinity)", //
        "{AbsoluteThickness(7)}");
  }

  @Test
  public void testPlotStyleThicknessIsTheDrawnStrokeWidth() {
    check("Union(StringCases(ExportString(Plot(Cos(t), {t, 0, 2}, "
        + "PlotStyle -> AbsoluteThickness(7)), \"SVG\"), "
        + "RegularExpression(\"stroke-width=.[0-9.]+.\")))", //
        "{stroke-width=\"7\"}");
  }
}

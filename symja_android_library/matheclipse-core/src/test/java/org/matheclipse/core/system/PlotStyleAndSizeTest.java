package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Contour styles given as lists of directives, the width of a picture sized by its height, grid
 * lines at every tick and ticks turned off with <code>False</code>.
 */
public class PlotStyleAndSizeTest extends ExprEvaluatorTestCase {

  @Override
  public void setUp() {
    super.setUp();
    evaluator.eval("svgSize(g_) := StringCases(ExportString(g, \"SVG\"), "
        + "RegularExpression(\"width=\\\"[0-9.]+\\\" height=\\\"[0-9.]+\\\"\"), 1)");
  }

  @Test
  public void testContourStyleListBecomesDirective() {
    // every per-contour list of directives becomes one flat Directive behind the half opacity and
    // the butt caps of the contour lines, the styles taken in turn
    check("Union(Cases(ContourPlot(x*y, {x, -1, 1}, {y, -1, 1}, " //
        + "ContourStyle -> {{Dashed, Green}, {Thin, Orange}})[[1]], _Directive, Infinity))", //
        "{Directive(Opacity(0.5),CapForm(Butt),Dashing({Small,Small}),RGBColor(0,1,0)),"
            + "Directive(Opacity(0.5),CapForm(Butt),Thickness(Tiny),RGBColor(1.0,0.5,0.0))}");
    check("Take(Cases(ContourPlot(x-2*y, {x, 0, 2}, {y, 0, 2}, Contours -> 4, " //
        + "ContourStyle -> {{Gray}, {Thick, Purple}, {Dotted}})[[1]], _Directive, Infinity), 4) " //
        + "=== Map(Directive(Opacity(0.5), CapForm(\"Butt\"), Sequence @@ #) &, " //
        + "{{Gray}, {Thick, Purple}, {Dotted}, {Gray}})", //
        "True");
    // a nested list of directives is flattened, a Directive opened
    check("Union(Cases(ContourPlot(x+2*y, {x, 0, 1}, {y, 0, 1}, " //
        + "ContourStyle -> {{Red, {Dashed, Thick}}})[[1]], _Directive, Infinity))", //
        "{Directive(Opacity(0.5),CapForm(Butt),RGBColor(1,0,0),Dashing({Small,Small}),"
            + "Thickness(Large))}");
    check("Union(Cases(ContourPlot(x+2*y, {x, 0, 1}, {y, 0, 1}, " //
        + "ContourStyle -> Directive(Blue, Thin))[[1]], _Directive, Infinity))", //
        "{Directive(Opacity(0.5),CapForm(Butt),RGBColor(0,0,1),Thickness(Tiny))}");
    // the default style
    check("Union(Cases(ContourPlot(x-y, {x, 0, 3}, {y, 0, 3})[[1]], _Directive, Infinity))", //
        "{Directive(Opacity(0.5),CapForm(Butt),GrayLevel(0.5))}");
  }

  @Test
  public void testContourPlotAutomaticLevels() {
    // round levels strictly between the least and the greatest value: 0.5, 1, ..., 5.5
    check("Length(Cases(ContourPlot(x+y, {x, 0, 3}, {y, 0, 3}, " //
        + "ContourStyle -> {{Red}, {Blue}})[[1]], _Directive, Infinity))", //
        "11");
    // -0.8, -0.6, ..., 0.8
    check("Length(Cases(ContourPlot(x*y, {x, -1, 1}, {y, -1, 1}, " //
        + "ContourStyle -> {{Red}})[[1]], _Directive, Infinity))", //
        "9");
    // an explicit number of levels is kept
    check("Length(Cases(ContourPlot(x*y, {x, -1, 1}, {y, -1, 1}, Contours -> 4, " //
        + "ContourStyle -> {{Red}})[[1]], _Directive, Infinity))", //
        "4");
  }

  @Test
  public void testListContourStyleListBecomesDirective() {
    // the level lines are half transparent with butt ends, as in ContourPlot
    check("Union(Cases(ListContourPlot(Table(x+y, {x, 0, 4}, {y, 0, 4}), " //
        + "ContourStyle -> {{Opacity(0.3), Blue}}), _Directive, Infinity))", //
        "{Directive(Opacity(0.5),CapForm(Butt),Opacity(0.3),RGBColor(0,0,1))}");
  }

  @Test
  public void testImageSizeAutomaticWidth() {
    // the height is kept and the width follows from the explicit aspect ratio of the whole
    // picture, half a pixel left on each side
    check("svgSize(Graphics(Circle(), AspectRatio -> 1/2, ImageSize -> {Automatic, 70}))", //
        "{width=\"139\" height=\"70\"}");
    // or from the ratio of the ranges of the data, padded on every side by 2 percent of the
    // larger one
    check("svgSize(Graphics(Line({{0, 0}, {5, 1}}), ImageSize -> {Automatic, 60}))", //
        "{width=\"256.667\" height=\"60\"}");
    // a drawing as high as it is wide stays square
    check("svgSize(Graphics(Rectangle({0, 0}, {2, 2}), ImageSize -> {Automatic, 70}))", //
        "{width=\"70\" height=\"70\"}");
    // the width given alone, the height follows from the aspect ratio in the same way
    check("svgSize(Graphics(Circle(), AspectRatio -> 1/2, ImageSize -> 130))", //
        "{width=\"130\" height=\"65.500\"}");
    // both given: both kept
    check("svgSize(Graphics(Circle(), AspectRatio -> 1/2, ImageSize -> {90, 50}))", //
        "{width=\"90\" height=\"50\"}");
  }

  @Test
  public void testGridLinesFull() {
    // majors every 2 on 0..10 and minors every 0.5: 21 lines across each axis
    check("StringCount(ExportString(Graphics(Line({{0, 0}, {10, 10}}), GridLines -> Full), " //
        + "\"SVG\"), \"<line\")", //
        "42");
    check("StringCount(ExportString(Graphics(Line({{0, 0}, {10, 10}}), GridLines -> Automatic), " //
        + "\"SVG\"), \"<line\")", //
        "12");
    // majors every 5 on 0..20 and minors every 1, on the x-axis only
    check("StringCount(ExportString(Graphics(Line({{0, 0}, {20, 20}}), " //
        + "GridLines -> {Full, None}), \"SVG\"), \"<line\")", //
        "21");
    // majors every 0.2 on 0..1 and minors every 0.05
    check("StringCount(ExportString(Graphics(Line({{0, 0}, {1, 1}}), " //
        + "GridLines -> {None, Full}), \"SVG\"), \"<line\")", //
        "21");
  }

  @Test
  public void testListPlotTicksFalse() {
    // the option becomes empty tick lists
    check("Options(ListPlot({2, -1, 5, 3}, Ticks -> False), Ticks)", //
        "{Ticks->{{},{}}}");
    check("ExportString(ListPlot({2, -1, 5, 3}, Ticks -> False), \"SVG\") === " //
        + "ExportString(ListPlot({2, -1, 5, 3}, Ticks -> None), \"SVG\")", //
        "True");
    check("StringCount(ExportString(ListPlot({2, -1, 5, 3}, Ticks -> False), \"SVG\"), " //
        + "\"<text\")", //
        "0");
  }

  @Test
  public void testContourLevelsThroughSamplePoints() {
    // the level 0 of x^2 - y^2 runs through the grid points on the diagonals: no line piece
    // shrinks to a point and no polygon to a line
    check("Cases(ContourPlot(x^2 - y^2, {x, -2, 2}, {y, -2, 2}, PlotPoints -> 9), " //
        + "Line({p_, p_}), Infinity)", //
        "{}");
    check("Select(Cases(ContourPlot(x^2 - y^2, {x, -2, 2}, {y, -2, 2}, PlotPoints -> 9), " //
        + "Polygon(p_) :> Length(Union(p)), Infinity), # < 3 &)", //
        "{}");
    // a negative number of contours draws none
    check("Cases(ContourPlot(x + y, {x, 0, 1}, {y, 0, 1}, Contours -> -3), _Line, Infinity)", //
        "{}");
  }
}

package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Tests for the graphics directives and primitives of {@code GraphicsDirectiveFunctions}: ColorQ,
 * DropShadowing, LinearGradientFilling, FullGraphics and the inert heads.
 */
public class GraphicsDirectiveFunctionsTest extends ExprEvaluatorTestCase {

  @Test
  public void testColorQ() {
    check("ColorQ(RGBColor(0.2, 0.7, 0.1))", //
        "True");
    check("ColorQ(Blue)", //
        "True");
    check("ColorQ(Hue(0.65))", //
        "True");
    check("ColorQ(Hue(0.25, 0.75))", //
        "True");
    // channels outside 0 ... 1 and exact numbers are channels too
    check("ColorQ(RGBColor(3, 0, 1/3))", //
        "True");
    check("ColorQ(RGBColor(1/2, Pi/5, 0))", //
        "True");
    check("ColorQ(GrayLevel(0.3, 0.8))", //
        "True");
    check("ColorQ(CMYKColor(0.5, 0.1, 0.2, 0.0))", //
        "True");
    check("ColorQ(LUVColor(0.6, 0.1, 0.3))", //
        "True");
    check("ColorQ(RGBColor({0.9, 0.5, 0.25}))", //
        "True");
    check("ColorQ(Transparent)", //
        "True");
    // symbolic channels make no colour
    check("ColorQ(RGBColor(u, 0, 1))", //
        "False");
    check("ColorQ(17)", //
        "False");
    check("ColorQ(\"orange\")", //
        "False");
    check("ColorQ(Opacity(0.4, Green))", //
        "False");
    // the colours which depend on the background aren't colours of their own
    check("ColorQ(LightDarkSwitched(Green, Yellow))", //
        "False");
    check("ColorQ(ThemeColor(\"Accent3\"))", //
        "False");
    check("ColorQ(SystemColor(\"Menu\"))", //
        "False");
  }

  @Test
  public void testDropShadowing() {
    check("DropShadowing()", //
        "DropShadowing({-3,-3},2,Opacity(1/3,ThemeColor(Foreground)))");
    check("DropShadowing({2, -1})", //
        "DropShadowing({2,-1},2,Opacity(1/3,ThemeColor(Foreground)))");
    check("DropShadowing(7)", //
        "DropShadowing({-3,-3},7,Opacity(1/3,ThemeColor(Foreground)))");
    check("DropShadowing(Green)", //
        "DropShadowing({-3,-3},2,RGBColor(0,1,0))");
    check("DropShadowing({4, 4}, Opacity(0.25))", //
        "DropShadowing({4,4},2,Opacity(0.25))");
    check("DropShadowing(None)", //
        "DropShadowing({-3,-3},2,None)");
    check("DropShadowing({1, 1}, 3, Gray)", //
        "DropShadowing({1,1},3,GrayLevel(0.5))");
    // arguments out of order stay unevaluated
    check("DropShadowing(3, {1, 1})", //
        "DropShadowing(3,{1,1})");
    check("DropShadowing(q)", //
        "DropShadowing(q)");
  }

  @Test
  public void testLinearGradientFilling() {
    check("LinearGradientFilling()", //
        "LinearGradientFilling({0,1}->{GrayLevel(0),GrayLevel(1)},0,Fixed)");
    check("LinearGradientFilling({Yellow, Blue})", //
        "LinearGradientFilling({0,1}->{RGBColor(1,1,0),RGBColor(0,0,1)},0,Fixed)");
    // four colours are spread evenly
    check("LinearGradientFilling({Red, White, Green, Black})[[1]]", //
        "{0,1/3,2/3,1}->{RGBColor(1,0,0),GrayLevel(1),RGBColor(0,1,0),GrayLevel(0)}");
    check("LinearGradientFilling({Orange, Purple}, Pi/3)", //
        "LinearGradientFilling({0,1}->{RGBColor(1.0,0.5,0.0),RGBColor(0.5,0.0,0.5)},Pi/3,Fixed)");
    check("LinearGradientFilling({{0.2, Red}, {0.9, Blue}})", //
        "LinearGradientFilling({0.2,0.9}->{RGBColor(1,0,0),RGBColor(0,0,1)},0,Fixed)");
    check("LinearGradientFilling({Cyan})", //
        "LinearGradientFilling({0,1}->{RGBColor(0,1,1),RGBColor(0,1,1)},0,Fixed)");
    // the normal form stays as it is
    check("LinearGradientFilling({0,1}->{Red,Blue},0,\"Fixed\")", //
        "LinearGradientFilling({0,1}->{RGBColor(1,0,0),RGBColor(0,0,1)},0,Fixed)");
  }

  @Test
  public void testInertHeads() {
    check("Haloing(Blue)", //
        "Haloing(RGBColor(0,0,1))");
    check("Haloing()", //
        "Haloing()");
    check("HatchFilling(Pi/6, 2, 5)", //
        "HatchFilling(Pi/6,2,5)");
    check("LightDarkSwitched(Black, White)", //
        "LightDarkSwitched(GrayLevel(0),GrayLevel(1))");
    check("ThemeColor(\"Accent4\")", //
        "ThemeColor(Accent4)");
    check("SystemColor(\"Tooltip\")", //
        "SystemColor(Tooltip)");
    check("ImageMarker(m, 12)", //
        "ImageMarker(m,12)");
    check("PolarCurve(2 + Sin(3*t), {t, 0, 2*Pi})", //
        "PolarCurve(2+Sin(3*t),{t,0,2*Pi})");
    // FilledPolarCurve takes the radius and the range itself
    check("FilledPolarCurve(PolarCurve(Cos(t)^2, {t, 0, Pi}))", //
        "FilledPolarCurve(PolarCurve(Cos(t)^2,{t,0,Pi}))");
    check("FilledPolarCurve(Cos(t)^2, {t, 0, Pi})", //
        "FilledPolarCurve(Cos(t)^2,{t,0,Pi})");
    // the variable must be a symbol
    check("PolarCurve(1 + u, {4, 0, Pi})", //
        "PolarCurve(1+u,{4,0,Pi})");
    check("Attributes(PolarCurve)", //
        "{Protected,ReadProtected}");
    check("Attributes(AxisObject)", //
        "{Protected}");
    check("FilledPolarCurve(1 + Sin(t)/2, t)", //
        "FilledPolarCurve(1+Sin(t)/2,t)");
    check("AxisObject(Line({{0, 1}, {5, 1}}), {0, 50})", //
        "AxisObject(Line({{0,1},{5,1}}),{0,50})");
    check("Raster3D({{{0.5}}})", //
        "Raster3D({{{0.5}}})");
  }

  @Test
  public void testFullGraphics() {
    // not a picture: unevaluated
    check("FullGraphics(17)", //
        "FullGraphics(17)");
    check("Head(FullGraphics(Graphics(Line({{0, 0}, {3, 2}}), Axes -> True)))", //
        "Graphics");
    // the axes are turned off and drawn as primitives
    check("Options(FullGraphics(Graphics(Line({{0, 0}, {3, 2}}), Axes -> True)), Axes)", //
        "{}");
    // nothing is left for the options to draw
    check("Options(FullGraphics(Graphics(Line({{0, 0}, {3, 2}}), Axes -> True, ImageSize -> 99)))", //
        "{}");
    // the labels of the major ticks; 0 where the axes cross stays unlabelled
    check(
        "Cases(FullGraphics(Graphics(Line({{0, 0}, {3, 2}}), Axes -> True)), Text(s_String, __) :> s, Infinity)", //
        "{0.5,1,1.5,2,2.5,3,0.5,1,1.5,2}");
    check(
        "Cases(FullGraphics(Graphics(Line({{0, 0}, {4, 1}}), Frame -> True, FrameLabel -> {\"u\", \"v\"})), Text(s_String, __) :> s, Infinity)", //
        "{1,2,3,4,u,0.2,0.4,0.6,0.8,1,v}");
    // without axes or frame there is nothing to add
    check(
        "Length(Cases(FullGraphics(Graphics(Circle())), _Line, Infinity))", //
        "0");
  }

  @Test
  public void testPlotEvaluated() {
    // Plot holds its arguments; Evaluated -> True differentiates before sampling
    check("Length(Cases(Plot(D(x^3, x), {x, 0, 1}, Evaluated -> True), _Line, Infinity)) > 0", //
        "True");
  }

  @Test
  public void testListPlotLayoutStacked() {
    check(
        "Cases(ListLinePlot({{1, 2, 3}, {2, 2, 1}}, PlotLayout -> \"Stacked\"), Line(p_) :> p[[All, 2]], Infinity)", //
        "{{1.0,2.0,3.0},{3.0,4.0,4.0}}");
    check(
        "Cases(ListLinePlot({{1, 3, 2}, {3, 1, 2}}, PlotLayout -> \"Percentile\"), Line(p_) :> p[[All, 2]], Infinity)", //
        "{{25.0,75.0,50.0},{100.0,100.0,100.0}}");
    check("Head(ListPlot({{1, 2, 3}, {3, 2, 1}}, PlotLayout -> \"Row\"))", //
        "GraphicsRow");
    check("Head(Plot({x, x^2, x^3}, {x, 0, 1}, PlotLayout -> {\"Column\", 2}))", //
        "GraphicsGrid");
  }
}

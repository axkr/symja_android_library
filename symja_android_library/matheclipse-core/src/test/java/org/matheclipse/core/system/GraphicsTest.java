package org.matheclipse.core.system;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;

/** Tests for graphics functions */
public class GraphicsTest extends ExprEvaluatorTestCase {

  @Test
  public void testBernsteinBasis() {
    check("BernsteinBasis(3,2,0.3)", //
        "0.189");
  }

  @Test
  public void testCircle() {
    check("Circle( )", //
        "Circle({0,0})");
  }

  @Test
  public void testDisk() {
    check("Disk( )", //
        "Disk({0,0})");
  }

  @Test
  public void testSphere() {
    // the radius stays implied, so the WLJS demo's Sphere -> Cuboid gives a unit cube and not the
    // malformed Cuboid({0,0,0},1)
    check("Sphere( )", //
        "Sphere({0,0,0})");
    check("Sphere({1,2,3})", //
        "Sphere({1,2,3})");
    check("Sphere( ) /. Sphere -> Cuboid", //
        "Cuboid({0,0,0},{1,1,1})");
    check("{Area(Sphere({a,b,c})), RegionMeasure(Sphere( ))}", //
        "{4*Pi,4*Pi}");
  }

  @Test
  public void testRectangle() {
    check("Rectangle( )", //
        "Rectangle({0,0})");
  }

  @Test
  public void testRGBColor() {
    // the pure colors are exact, as in WMA: Black is RGBColor(0,0,0)
    check("Black", //
        "RGBColor(0,0,0)");
    check("Blue", //
        "RGBColor(0,0,1)");
  }

  /**
   * The JSON these produced fed the JSXGraph and mathics-threejs renderers, which the SVG and WebGL
   * renderers replaced; the symbols stay declared but no longer evaluate.
   */
  @Test
  public void testGraphicsJSONUnevaluated() {
    check("GraphicsJSON(Graphics(Circle()))", //
        "GraphicsJSON(-Graphics-)");
    check("Graphics3DJSON(Graphics3D(Sphere()))", //
        "Graphics3DJSON(-Graphics3D-)");
  }

  /** The JUnit setup method */
  @Override
  public void setUp() {
    super.setUp();
    Config.SHORTEN_STRING_LENGTH = 1024;
    Config.MAX_AST_SIZE = 1000000;
    EvalEngine.get().setIterationLimit(50000);
  }

  @AfterEach
  public void tearDown() throws Exception {
    // super.tearDown();
    Config.SHORTEN_STRING_LENGTH = 80;
  }

  @Test
  public void testFontSize() {
    check("FontSize", "FontSize");
    check("Head(FontSize)", "Symbol");
    check("FontSize -> 14", "FontSize->14");
    // Style only changes how an expression looks, so OutputForm shows the expression itself
    check("Style(\"hello\", FontSize -> 24)", "hello");
    check("{Style(\"green\", Italic, Green), Style(\"red\", Bold, Red)}", "{green,red}");
    check("Style(\"hello\", Bold, Red)", "hello");
    // ... while InputForm keeps the directives
    check("ToString(Style(\"green\", Italic, Green), InputForm)",
        "Style(\"green\",Italic,RGBColor(0,1,0))");
    check("ToString(Style(NumberForm(50., {3, 1}), 18, Bold))", "50.0");
    // Row places its elements side by side, keeping the spaces its strings carry
    check(
        "ToString(Row({Style(NumberForm(50., {3, 1}), 18, Bold), Style(\"% shaded\", 18, Bold)}))",
        "50.0% shaded");
    check("ToString(Row({NumberForm(50., {3, 1}), \" pct\"}))", "50.0 pct");
  }

  @Test
  public void testFontFamily() {
    check("FontFamily", "FontFamily");
    check("Head(FontFamily)", "Symbol");
    check("FontFamily -> \"Helvetica\"", "FontFamily->Helvetica");
    check("Style(\"hello\", FontFamily -> \"Arial\")", "hello");
  }

  @Test
  public void testThick() {
    check("Thick", "Thickness(Large)");
    check("Head(Thick)", "Thickness");
    check("Thin", "Thickness(Tiny)");
    // a picture has no text representation - OutputForm shows a placeholder
    check("Graphics({Thick, Line({{0, 0}, {1, 1}})})", "-Graphics-");
    check("ToString(Graphics({Thick, Line({{0, 0}, {1, 1}})}), InputForm)",
        "Graphics({Thickness(Large),Line({{0,0},{1,1}})})");
    check("Plot(Sin(x), {x, 0, 1}, PlotStyle -> Thick)", "-Graphics-");
  }

  @Test
  public void testDashed() {
    check("Dashed", "Dashing({Small,Small})");
    check("Dotted", "Dashing({0,Small})");
    check("DotDashed", "Dashing({0,Small,Small,Small})");
    check("Head(Dashed)", "Dashing");
    check("ToString(Graphics({Dashed, Line({{0, 0}, {1, 1}})}), InputForm)",
        "Graphics({Dashing({Small,Small}),Line({{0,0},{1,1}})})");
    check("ToString(Graphics({Dotted, Line({{0, 0}, {1, 1}})}), InputForm)",
        "Graphics({Dashing({0,Small}),Line({{0,0},{1,1}})})");
    check("ToString(Graphics({DotDashed, Line({{0, 0}, {1, 1}})}), InputForm)",
        "Graphics({Dashing({0,Small,Small,Small}),Line({{0,0},{1,1}})})");
    check("Dashing({Small, Small})", "Dashing({Small,Small})");
  }
}

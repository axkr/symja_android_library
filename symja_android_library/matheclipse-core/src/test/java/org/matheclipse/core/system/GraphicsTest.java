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

  @Test
  public void testGraphicsJSON() {
    // check("GraphicsJSON(Graphics(Polygon({{1, -1}, {0, Sqrt(7)}, {-1, -1}})))", //
    // "{\"elements\":[{\"type\":\"polygon\",\"color\":[0.0,0.0,0.0],\"opacity\":1.0,\"coords\":[[[1.0,-1.0]],[[0.0,2.6457513110645907]],[[-1.0,-1.0]]]}],\"extent\":{\"xmin\":-1.0,\"xmax\":1.0,\"ymin\":-1.0,\"ymax\":2.6457513110645907},\"axes\":{\"hasaxes\":false}}");
    //
    // check("GraphicsJSON(Graphics({Red, Rectangle({0, 0}), Purple, Rectangle({0.75, 0.75})}))", //
    // "{\"elements\":[{\"option\":\"color\",\"value\":[1.0,0.0,0.0]},{\"type\":\"rectangle\",\"color\":[1.0,0.0,0.0],\"opacity\":1.0,\"coords\":[[[0.0,0.0]],[[1.0,1.0]]]},{\"option\":\"color\",\"value\":[0.5,0.0,0.5]},{\"type\":\"rectangle\",\"color\":[0.5,0.0,0.5],\"opacity\":1.0,\"coords\":[[[0.75,0.75]],[[1.0,1.0]]]}],\"extent\":{\"xmin\":0.0,\"xmax\":1.0,\"ymin\":0.0,\"ymax\":1.0},\"axes\":{\"hasaxes\":false}}");
    //
    // check("GraphicsJSON(Graphics({Red, Disk({1, 1}, 1, {0, Pi/4})}))", //
    // "{\"elements\":[{\"option\":\"color\",\"value\":[1.0,0.0,0.0]},{\"type\":\"disk\",\"color\":[1.0,0.0,0.0],\"opacity\":1.0,\"radius1\":1.0,\"radius2\":1.0,\"angle1\":0.0,\"angle2\":0.7853981633974483,\"coords\":[[[1.0,1.0]]]}],\"extent\":{\"xmin\":0.0,\"xmax\":2.0,\"ymin\":0.0,\"ymax\":2.0},\"axes\":{\"hasaxes\":false}}");
    //
    // check(
    // "GraphicsJSON(Graphics({Blue, Circle({2,0}), Yellow, Polygon( {{2, 0}, {4, 1}, {4, -1}})}))",
    // //
    // "{\"elements\":[{\"option\":\"color\",\"value\":[0.0,0.0,1.0]},{\"type\":\"circle\",\"color\":[0.0,0.0,1.0],\"opacity\":1.0,\"radius1\":1.0,\"radius2\":1.0,\"coords\":[[[2.0,0.0]]]},{\"option\":\"color\",\"value\":[1.0,1.0,0.0]},{\"type\":\"polygon\",\"color\":[1.0,1.0,0.0],\"opacity\":1.0,\"coords\":[[[2.0,0.0]],[[4.0,1.0]],[[4.0,-1.0]]]}],\"extent\":{\"xmin\":1.0,\"xmax\":4.0,\"ymin\":-1.0,\"ymax\":1.0},\"axes\":{\"hasaxes\":false}}");
    //
    // check("GraphicsJSON(Graphics(Point( Table({t, Sin(t)}, {t, 0, 2 Pi, 2*Pi/10}) )))", //
    // "{\"elements\":[{\"type\":\"point\",\"pointSize\":0.0013,\"color\":[0.0,0.0,0.0],\"opacity\":1.0,\"coords\":[[[0.0,0.0]],[[0.6283185307179586,0.5877852522924731]],[[1.2566370614359172,0.9510565162951535]],[[1.8849555921538759,0.9510565162951535]],[[2.5132741228718345,0.5877852522924731]],[[3.141592653589793,0.0]],[[3.7699111843077517,-0.5877852522924731]],[[4.39822971502571,-0.9510565162951535]],[[5.026548245743669,-0.9510565162951535]],[[5.654866776461628,-0.5877852522924731]],[[6.283185307179586,0.0]]]}],\"extent\":{\"xmin\":0.0,\"xmax\":6.283185307179586,\"ymin\":-0.9510565162951535,\"ymax\":0.9510565162951535},\"axes\":{\"hasaxes\":false}}");
    //
    // check("GraphicsJSON(Graphics(Line({{1, 1}, {3, 1}, {4, 3}, {4, 7}})))", //
    // "{\"elements\":[{\"type\":\"line\",\"color\":[0.0,0.0,0.0],\"opacity\":1.0,\"coords\":[[[1.0,1.0]],[[3.0,1.0]],[[4.0,3.0]],[[4.0,7.0]]]}],\"extent\":{\"xmin\":1.0,\"xmax\":4.0,\"ymin\":1.0,\"ymax\":7.0},\"axes\":{\"hasaxes\":false}}");
    //
    // check("GraphicsJSON(Graphics(Circle({1, 1}, {3, 4})))", //
    // "{\"elements\":[{\"type\":\"circle\",\"color\":[0.0,0.0,0.0],\"opacity\":1.0,\"radius1\":3.0,\"radius2\":4.0,\"coords\":[[[1.0,1.0]]]}],\"extent\":{\"xmin\":-2.0,\"xmax\":4.0,\"ymin\":-3.0,\"ymax\":5.0},\"axes\":{\"hasaxes\":false}}");
    //
    // check("GraphicsJSON(Graphics(Arrow({{1, 0}, {2, 3}, {3, 7}, {4, 2}})))", //
    // "{\"elements\":[{\"type\":\"arrow\",\"color\":[0.0,0.0,0.0],\"opacity\":1.0,\"coords\":[[[1.0,0.0]],[[2.0,3.0]],[[3.0,7.0]],[[4.0,2.0]]]}],\"extent\":{\"xmin\":1.0,\"xmax\":4.0,\"ymin\":0.0,\"ymax\":7.0},\"axes\":{\"hasaxes\":false}}");
  }

  @Test
  public void testGraphicsJSONColor() {
    // check("GraphicsJSON(Graphics(Red))", //
    // "{\"elements\":[{\"option\":\"color\",\"value\":[1.0,0.0,0.0]}]}");
  }

  @Test
  public void testGraphicsJSONPointSize() {
    // check("GraphicsJSON(Graphics(PointSize(1/2)))", //
    // "{\"elements\":[{\"option\":\"pointSize\",\"value\":0.5}]}");
  }

  @Test
  public void testGraphicsJSONOpacity() {
    // check("GraphicsJSON(Graphics(Opacity(7/8)))", //
    // "{\"elements\":[{\"option\":\"opacity\",\"value\":0.875}]}");
  }

  @Test
  public void testGraphicsJSONWithOptions() {
    // check(
    // "GraphicsJSON(Graphics({Green, Rectangle()}, PlotRangePadding -> {{0.5, 0.4}, {0.3, 0.3}},
    // Frame -> True))", //
    // "{\"elements\":[{\"option\":\"color\",\"value\":[0.0,1.0,0.0]},{\"type\":\"rectangle\",\"color\":[0.0,1.0,0.0],\"opacity\":1.0,\"coords\":[[[0.0,0.0]],[[1.0,1.0]]]}],\"extent\":{\"xmin\":0.0,\"xmax\":1.0,\"ymin\":0.0,\"ymax\":1.0},\"axes\":{\"hasaxes\":false}}");
    //
    // check("GraphicsJSON(Graphics(Circle(), Axes -> True ))", //
    // "{\"elements\":[{\"type\":\"circle\",\"color\":[0.0,0.0,0.0],\"opacity\":1.0,\"radius1\":1.0,\"radius2\":1.0,\"coords\":[[[0.0,0.0]]]}],\"extent\":{\"xmin\":-1.0,\"xmax\":1.0,\"ymin\":-1.0,\"ymax\":1.0},\"axes\":{\"hasaxes\":true}}");
    //
    // check("GraphicsJSON(Graphics(Circle(), Axes -> {False, True}))", //
    // "{\"elements\":[{\"type\":\"circle\",\"color\":[0.0,0.0,0.0],\"opacity\":1.0,\"radius1\":1.0,\"radius2\":1.0,\"coords\":[[[0.0,0.0]]]}],\"extent\":{\"xmin\":-1.0,\"xmax\":1.0,\"ymin\":-1.0,\"ymax\":1.0},\"axes\":{\"hasaxes\":[false,true]}}");

  }

  @Test
  public void testGraphicsJSONText() {
    // check("GraphicsJSON(Graphics({Circle(), Text(x^2 + y^2 < 1, {0, 0})}))", //
    // "{\"elements\":[{\"type\":\"circle\",\"color\":[0.0,0.0,0.0],\"opacity\":1.0,\"radius1\":1.0,\"radius2\":1.0,\"coords\":[[[0.0,0.0]]]},{\"type\":\"text\",\"coords\":[[[0.0,0.0]]],\"texts\":[\"x^2+y^2<1\"]}],\"extent\":{\"xmin\":-1.0,\"xmax\":1.0,\"ymin\":-1.0,\"ymax\":1.0},\"axes\":{\"hasaxes\":false}}");
  }

  @Test
  public void testGraphics3DJSON() {
    check("Graphics3DJSON(Graphics3D(Arrow({{1, 1, -1}, {2, 3, 0}, {3, 7, -1}, {4, 2, 0}})))", //
        "{\"elements\":[{\"type\":\"arrow\",\"color\":[1.0,0.5,0.0],\"opacity\":1.0,\"coords\":[[[1.0,1.0,-1.0]],[[2.0,3.0,0.0]],[[3.0,7.0,-1.0]],[[4.0,2.0,0.0]]]}],\"lighting\":[{\"type\":\"ambient\",\"color\":[0.4,0.2,0.2]},{\"type\":\"directional\",\"color\":[0.0,0.18,0.5],\"coords\":[[2.0,0.0,2.0]]},{\"type\":\"directional\",\"color\":[0.18,0.5,0.18],\"coords\":[[2.0,2.0,3.0]]},{\"type\":\"directional\",\"color\":[0.5,0.18,0.0],\"coords\":[[0.0,2.0,2.0]]},{\"type\":\"directional\",\"color\":[0.0,0.0,0.18],\"coords\":[[0.0,0.0,2.0]]}],\"viewpoint\":[1.3,-2.4,2.0]}");
  }

  @Test
  public void testGraphics3DJSONText() {
    check("Graphics3DJSON(Graphics3D({Sphere(), Text(x^2 + y^2 + z^2 < 1, {0, 0,0})}))", //
        "{\"elements\":[{\"type\":\"sphere\",\"color\":[1.0,0.5,0.0],\"opacity\":1.0,\"radius\":1.0,\"coords\":[[[0.0,0.0,0.0]]]},{\"type\":\"text\",\"color\":[1.0,0.5,0.0],\"opacity\":1.0,\"coords\":[[null,[0.0,0.0,0.0]]],\"texts\":[\"x^2+y^2+z^2<1\"]}],\"lighting\":[{\"type\":\"ambient\",\"color\":[0.4,0.2,0.2]},{\"type\":\"directional\",\"color\":[0.0,0.18,0.5],\"coords\":[[2.0,0.0,2.0]]},{\"type\":\"directional\",\"color\":[0.18,0.5,0.18],\"coords\":[[2.0,2.0,3.0]]},{\"type\":\"directional\",\"color\":[0.5,0.18,0.0],\"coords\":[[0.0,2.0,2.0]]},{\"type\":\"directional\",\"color\":[0.0,0.0,0.18],\"coords\":[[0.0,0.0,2.0]]}],\"viewpoint\":[1.3,-2.4,2.0]}");
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

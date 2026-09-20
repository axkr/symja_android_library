package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * A boundary mesh region as a picture: what <code>Show</code> makes of it, and what the renderers
 * draw. Both used to drop it without a trace.
 *
 * <p>
 * The picture is the one the reference implementation draws, measured in Mathematica on 2026-09-19
 * with <code>Show[ConvexHullMesh[...]] // InputForm</code>.
 */
public class MeshRegionGraphicsTest {

  private static final String SQUARE = "{{0,0},{2,0},{2,2},{0,2}}";
  private static final String TETRAHEDRON = "{{0,0,0},{1,0,0},{0,1,0},{0,0,1}}";

  private static ExprEvaluator evaluator;

  @BeforeAll
  public static void setUpEngine() {
    Locale.setDefault(Locale.US);
    Config.SERVER_MODE = false;
    try {
      F.await();
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(ie);
    }
    EvalEngine engine = new EvalEngine(true);
    EvalEngine.set(engine);
    engine.init();
    evaluator = new ExprEvaluator(engine, false, (short) 100);
  }

  private static String inputForm(String input) {
    return evaluator.eval("ToString(" + input + ", InputForm)").toString();
  }

  private static String svg(String input) {
    IExpr result = evaluator.eval("ExportString(" + input + ", \"SVG\")");
    assertTrue(result.isString(), input + " did not export: " + result);
    return result.toString();
  }

  /** The colours an attribute takes across the whole picture. */
  private static String colours(String svg, String attribute) {
    java.util.TreeSet<String> found = new java.util.TreeSet<>();
    Matcher m = Pattern.compile(attribute + "=\"(rgb\\([^)]*\\))\"").matcher(svg);
    while (m.find()) {
      found.add(m.group(1));
    }
    return found.toString();
  }

  @Test
  public void showOfATwoDimensionalMesh() {
    assertEquals("Graphics(GraphicsComplex({{0.0`,0.0`},{2.0`,0.0`},{2.0`,2.0`},{0.0`,2.0`}},"
        + "{Directive({RGBColor(0.6260033081763745`,0.8359330492764128`,0.9185378316380052`),"
        + "EdgeForm({RGBColor(0.372575209344428`,0.6124949134587575`,0.706900379014863`)})}),"
        + "{Annotation(Polygon({{1,2,3,4}}),\"Geometry\")}}))", //
        inputForm("Show(ConvexHullMesh(" + SQUARE + "))"));
  }

  /** Styled cells follow the geometry as a group of their own, as the reference draws them. */
  @Test
  public void showOfStyledEdges() {
    assertEquals("Graphics(GraphicsComplex({{0.0`,0.0`},{2.0`,0.0`},{2.0`,2.0`},{0.0`,2.0`}},"
        + "{Directive({RGBColor(0.6260033081763745`,0.8359330492764128`,0.9185378316380052`),"
        + "EdgeForm({RGBColor(0.372575209344428`,0.6124949134587575`,0.706900379014863`)})}),"
        + "{Annotation(Polygon({{1,2,3,4}}),\"Geometry\")},"
        + "{{Directive(RGBColor(1,0,0)),Line({{1,2},{2,3},{3,4},{4,1}})}}}))", //
        inputForm("Show(ConvexHullMesh(" + SQUARE + ", MeshCellStyle->{{1,All}->Red}))"));
  }

  /**
   * A three dimensional mesh is its faces, unboxed and lit by four lights. The faces are in
   * Symja's order, which is not the reference's; only the order differs.
   */
  @Test
  public void showOfAThreeDimensionalMesh() {
    assertEquals("Graphics3D(GraphicsComplex({{0.0`,0.0`,0.0`},{1.0`,0.0`,0.0`},{0.0`,1.0`,0.0`},"
        + "{0.0`,0.0`,1.0`}},{Directive({RGBColor(0.465719011680535`,0.7656186418234469`,"
        + "0.8836254737685788`),EdgeForm({RGBColor(0.465719011680535`,0.7656186418234469`,"
        + "0.8836254737685788`)})}),{Annotation(Polygon({{1,2,4},{1,3,2},{1,4,3},{2,3,4}}),"
        + "\"Geometry\")}}),{Boxed->False,Lighting->{{\"Ambient\",GrayLevel(0.45`)},"
        + "{\"Directional\",GrayLevel(0.3`),ImageScaled({2,0,2})},{\"Directional\","
        + "GrayLevel(0.33`),ImageScaled({2,2,2})},{\"Directional\",GrayLevel(0.3`),"
        + "ImageScaled({0,2,2})}}})", //
        inputForm("Show(ConvexHullMesh(" + TETRAHEDRON + "))"));
  }

  @Test
  public void aTwoDimensionalMeshIsDrawnInTheReferenceColours() {
    // 0.626/0.836/0.919 and 0.373/0.612/0.707 in eight bits
    String svg = svg("ConvexHullMesh(" + SQUARE + ")");
    assertEquals("[rgb(160,213,234)]", colours(svg, "fill"));
    assertEquals("[rgb(95,156,180)]", colours(svg, "stroke"));
    // the same region among other primitives is drawn the same way
    assertEquals(svg, svg("Graphics({ConvexHullMesh(" + SQUARE + ")})"));
  }

  @Test
  public void styledEdgesAreDrawnInTheirStyle() {
    String svg = svg("Show(ConvexHullMesh(" + SQUARE + ", MeshCellStyle->{{1,All}->Red}))");
    assertTrue(colours(svg, "stroke").contains("rgb(255,0,0)"), colours(svg, "stroke"));
  }

  /**
   * A translucent face reaches the drawing - and is not drawn over an opaque copy of itself, which
   * would hide the transparency.
   */
  @Test
  public void aTranslucentFaceStaysTranslucent() {
    String opaque = svg("ConvexHullMesh(" + TETRAHEDRON + ")");
    String translucent =
        svg("ConvexHullMesh(" + TETRAHEDRON + ", MeshCellStyle->{{2,All}->Opacity(0.5,LightBlue)})");
    assertTrue(!opaque.contains("opacity=\"0.502\""), "the default faces are opaque");
    assertTrue(translucent.contains("opacity=\"0.502\""), "the styled faces are half transparent");
    assertEquals(4, translucent.split("<polygon", -1).length - 1, "four faces, drawn once each");
  }

  /**
   * A list inside a <code>Directive</code> is only a way of writing several parts, and every part
   * applies. It used to be collected in a scope of its own and thrown away, which drew the polygon
   * black and without an edge - and a mesh region is drawn with exactly this form.
   */
  @Test
  public void aListInsideADirectiveApplies() {
    String svg = svg("Graphics({Directive({Red, EdgeForm({Blue})}), Polygon({{0,0},{1,0},{0,1}})})");
    assertEquals("[rgb(255,0,0)]", colours(svg, "fill"));
    assertEquals("[rgb(0,0,255)]", colours(svg, "stroke"));
  }

  /** Show puts several graphics together, keeping each one's directives to itself. */
  @Test
  public void showCombinesGraphics() {
    assertEquals(
        "Graphics({{RGBColor(1,0,0),Disk({0,0})},{RGBColor(0,0,1),Point({0,0})}},Axes->True)",
        inputForm("Show(Graphics({Red,Disk()}), Graphics({Blue,Point({0,0})}), Axes->True)"));
    // the first setting of an option wins, and Show's own come first
    assertEquals("Graphics({{Point({0,0})},{Point({1,1})}},Axes->False,Frame->True)",
        inputForm("Show(Graphics({Point({0,0})}, Frame->True), Graphics({Point({1,1})}, "
            + "Frame->False), Axes->False)"));
    assertEquals("Show(x)", inputForm("Show(x)"));
  }

  /**
   * The ways a boundary mesh region's cells may be written. A cell may stand on its own instead of
   * in a list, and a <code>Line</code> may be a walk along the boundary rather than its edges one
   * by one - <code>BoundaryMeshRegion[{{0,0},{1,0},{0,1}}, Line[{1,2,3,1}]]</code> is both at once.
   * Neither form was read at all: the region was not a region, so it had no area and no picture.
   */
  @Test
  public void theCellsMayBeWrittenInAnyOfTheThreeForms() {
    String triangle = "{{0,0},{1,0},{0,1}}";
    String[] cells = {"Line({1,2,3,1})", "{Line({1,2,3,1})}", "{Line({{1,2},{2,3},{3,1}})}"};
    for (String cell : cells) {
      String mesh = "BoundaryMeshRegion(" + triangle + ", " + cell + ")";
      assertEquals("1/2", evaluator.eval("Area(" + mesh + ")").toString(), cell);
      assertEquals("{Line({1,2}),Line({2,3}),Line({3,1})}",
          evaluator.eval("MeshCells(" + mesh + ", 1)").toString(), cell);
      // every form draws the same picture
      assertEquals(inputForm("Show(BoundaryMeshRegion(" + triangle + ", " + cells[2] + "))"),
          inputForm("Show(" + mesh + ")"), cell);
      assertTrue(svg(mesh).contains("rgb(160,213,234)"), cell);
    }
  }
}

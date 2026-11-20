package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

/** A TransformationFunction applied to a graphics primitive or region. */
public class RegionTransformationTest extends ExprEvaluatorTestCase {

  @Test
  public void testPointListsUnderAffineMaps() {
    check("TranslationTransform({2, -1})[Polygon({{0, 0}, {2, 0}, {0, 2}})]", //
        "Polygon({{2,-1},{4,-1},{2,1}})");
    check("ShearingTransform(Pi/4, {1, 0}, {0, 1})[Line({{0, 0}, {0, 2}})]", //
        "Line({{0,0},{2,2}})");
    check("ScalingTransform({2, 3})[Point({{1, 1}, {2, 0}})]", //
        "Point({{2,3},{4,0}})");
    check("RotationTransform(s)[Point({0, 2})]", //
        "Point({-2*Sin(s),2*Cos(s)})");
    check("TranslationTransform({1, 1, 1})[Tetrahedron({{0, 0, 0}, {1, 0, 0}, {0, 1, 0}, "
        + "{0, 0, 1}})]", //
        "Tetrahedron({{1,1,1},{2,1,1},{1,2,1},{1,1,2}})");
  }

  @Test
  public void testRoundPrimitivesUnderSimilarities() {
    check("RotationTransform(Pi/2, {0, 0, 1})[Cylinder({{1, 0, 0}, {1, 0, 2}}, 1/2)]", //
        "Cylinder({{0,1,0},{0,1,2}},1/2)");
    check("ScalingTransform({1/2, 1/2})[Circle({4, 2}, 6)]", //
        "Circle({2,1},3)");
    // a reflection keeps a disk round; the default radius is written out
    check("ScalingTransform({-1, 1})[Disk({3, 1})]", //
        "Disk({-3,1},1)");
    check("TranslationTransform({0, 0, 5})[Cone({{0, 0, 0}, {0, 0, 1}}, w)]", //
        "Cone({{0,0,5},{0,0,6}},w)");
    check("RotationTransform(s, {1, 0, 0})[Sphere({0, 0, 0}, 2)]", //
        "Sphere({0,0,0},2)");
  }

  @Test
  public void testFilledEllipses() {
    // a non-uniform scaling turns a disk into an ellipse
    check("ScalingTransform({2, 5})[Disk({1, 0}, 3)]", //
        "Ellipsoid({2,0},{{36,0},{0,225}})");
    check("RotationTransform(Pi/2)[Disk({0, 0}, {2, 1})]", //
        "Ellipsoid({0,0},{{1,0},{0,4}})");
    // a symbolic radius gives no Ball, even under a uniform scaling
    check("ScalingTransform({3, 3, 3})[Ball({0, 0, 1}, w)]", //
        "Ellipsoid({0,0,3},{{9*w^2,0,0},{0,9*w^2,0},{0,0,9*w^2}})");
    // a translation keeps the semi-axes and a symbolic radius
    check("TranslationTransform({1, -1})[Disk({0, 0}, {2, 1})]", //
        "Disk({1,-1},{2,1})");
    check("TranslationTransform({1, -1})[Disk({0, 0}, w)]", //
        "Disk({1,-1},w)");
    // a symbolic uniform scaling keeps a disk of numeric radius
    check("ScalingTransform({k, k})[Disk({1, 1}, 2)]", //
        "Disk({k,k},2*Sqrt(k^2))");
  }

  @Test
  public void testRectangles() {
    check("TranslationTransform({-1, 2})[Rectangle({1, 1}, {3, 2})]", //
        "Rectangle({0,3},{2,4})");
    // a negative factor swaps the sides
    check("ScalingTransform({-1, 2})[Rectangle({1, 1}, {3, 2})]", //
        "Rectangle({-3,2},{-1,4})");
    // the corners of a rectangle which is no longer one, starting at the lower left
    check("ShearingTransform(Pi/4, {1, 0}, {0, 1})[Rectangle({0, 0}, {2, 1})]", //
        "Polygon({{0,0},{1,1},{3,1},{2,0}})");
  }

  @Test
  public void testImagesOfAnotherKindStayUnevaluated() {
    // a circle is only the boundary of an ellipse
    check("ScalingTransform({2, 3})[Circle({0, 0}, 1)]", //
        "TransformationFunction({{2,0,0},{0,3,0},{0,0,1}})[Circle({0,0},1)]");
    // a projective map
    check("TransformationFunction({{1, 0, 0}, {0, 1, 0}, {1, 0, 1}})[Line({{0, 0}, {1, 1}})]", //
        "TransformationFunction({{1,0,0},{0,1,0},{1,0,1}})[Line({{0,0},{1,1}})]");
    // a primitive of another dimension
    check("TranslationTransform({1, 1})[Ball({0, 0, 1}, 2)]", //
        "TransformationFunction({{1,0,1},{0,1,1},{0,0,1}})[Ball({0,0,1},2)]");
  }

  @Test
  public void testExactRadiusStaysRound() {
    // an exact positive radius is a number as well: the rotated disk and circle stay round
    check("RotationTransform(Pi/6)[Disk({0, 0}, Sqrt(5))]", //
        "Disk({0,0},Sqrt(5))");
    check("RotationTransform(Pi/3)[Circle({0, 0}, E)]", //
        "Circle({0,0},E)");
    check("ScalingTransform({3, 3})[Sphere({0, 0}, Pi)]", //
        "Sphere({0,0},3*Pi)");
  }

  @Test
  public void testComposedTransformationFunctions() {
    // Composition applies the last function first, RightComposition the first one
    check("ScalingTransform({2, 3}) /* TranslationTransform({1, -1})", //
        "TransformationFunction({{2,0,1},{0,3,-1},{0,0,1}})");
    check("TranslationTransform({1, -1}) @* ScalingTransform({2, 3})", //
        "TransformationFunction({{2,0,1},{0,3,-1},{0,0,1}})");
    check("RightComposition(TranslationTransform({1, -1}), ScalingTransform({2, 3}))", //
        "TransformationFunction({{2,0,2},{0,3,-3},{0,0,1}})");
    check("(TranslationTransform({3, 1}) /* RotationTransform(Pi/2))[{1, 2}] == "
        + "RotationTransform(Pi/2)[TranslationTransform({3, 1})[{1, 2}]]", //
        "True");
    // a run of transformations between other functions is fused
    check("f /* ScalingTransform({2, 2}) /* TranslationTransform({0, 1}) /* g", //
        "f/*TransformationFunction({{2,0,0},{0,2,1},{0,0,1}})/*g");
    // Identity drops out, a composition of one function is that function
    check("{Composition(h), RightComposition(Identity, h), Composition(Identity, h, Identity), "
        + "RightComposition(h), Composition(Identity)}", //
        "{h,h,h,h,Identity}");
    // transformations of different dimensions are not multiplied
    check("Head(RotationTransform(Pi/2, {0, 0, 1}) /* TranslationTransform({1, 0}))", //
        "RightComposition");
    assertEquals(
        "TransformationFunction: TransformationFunction({{2,0,0},{0,2,0},{0,0,1}}) and "
            + "TransformationFunction({{1,0,0,1},{0,1,0,0},{0,0,1,0},{0,0,0,1}}) in Composition "
            + "have incompatible dimensions.\n", //
        messagesOf("ScalingTransform({2, 2}) @* TranslationTransform({1, 0, 0})"));
  }

  private String messagesOf(String input) {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    PrintStream old = evaluator.getEvalEngine().getErrorPrintStream();
    evaluator.getEvalEngine().setErrorPrintStream(new PrintStream(buffer, true));
    try {
      evaluator.eval(input);
    } finally {
      evaluator.getEvalEngine().setErrorPrintStream(old);
    }
    return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
  }
}

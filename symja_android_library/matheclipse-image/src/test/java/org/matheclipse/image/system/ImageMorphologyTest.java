package org.matheclipse.image.system;

import org.junit.jupiter.api.Test;

/**
 * <code>Dilation</code>, <code>Erosion</code>, <code>Opening</code>, <code>Closing</code>,
 * <code>MorphologicalComponents</code>, <code>DistanceTransform</code>,
 * <code>DerivativeFilter</code> and <code>GradientFilter</code>.
 */
public class ImageMorphologyTest extends AbstractTestCase {

  @Test
  public void testDilationErosion() {
    check("spot = Image(ReplacePart(Table(0., {5}, {5}), {3, 3} -> 1.)); "
        + "ImageData(Dilation(spot, 1)) == {{0,0,0,0,0}, {0,1,1,1,0}, {0,1,1,1,0}, {0,1,1,1,0}, "
        + "{0,0,0,0,0}}", //
        "True");
    // the non-zero elements of a matrix are the structuring element
    check(
        "ImageData(Dilation(spot, {{0, 1, 0}, {1, 1, 1}, {0, 1, 0}})) == "
            + "{{0,0,0,0,0}, {0,0,1,0,0}, {0,1,1,1,0}, {0,0,1,0,0}, {0,0,0,0,0}}", //
        "True");
    // an element which isn't symmetric: the element ker[[b]] lies on the pixel x + b - centre
    check("ImageData(Dilation(Image({{0., 0., 1., 0., 0.}}), {{1, 1, 0}})) == {{0, 0, 1, 1, 0}}", //
        "True");
    check("ImageData(Erosion(Image({{0., 0., 1., 1., 1.}}), {{1, 1, 0}})) == {{0, 0, 0, 1, 1}}", //
        "True");
    // the part of the element outside of the image doesn't count
    check("Union(Flatten(ImageData(Erosion(Image(Table(1., {3}, {4})), 1))))", //
        "{1.0}");
  }

  @Test
  public void testOpeningClosing() {
    check(
        "g = Image({{0.1, 0.5, 0.3}, {0.9, 0.2, 0.7}, {0.4, 0.8, 0.6}}); "
            + "ImageData(Dilation(g, 1)) == {{0.9, 0.9, 0.7}, {0.9, 0.9, 0.8}, {0.9, 0.9, 0.8}}", //
        "True");
    check("ImageData(Erosion(g, 1)) == {{0.1, 0.1, 0.2}, {0.1, 0.1, 0.2}, {0.2, 0.2, 0.2}}", //
        "True");
    check("ImageData(Opening(g, 1)) == {{0.1, 0.2, 0.2}, {0.2, 0.2, 0.2}, {0.2, 0.2, 0.2}}", //
        "True");
    check("ImageData(Closing(g, 1)) == {{0.9, 0.7, 0.7}, {0.9, 0.7, 0.7}, {0.9, 0.8, 0.8}}", //
        "True");
  }

  @Test
  public void testMorphologyKeepsType() {
    check("b = Image({{0, 128, 255}, {64, 192, 32}}, \"Byte\"); "
        + "ImageType /@ {Dilation(b, 1), Erosion(b, 1), Dilation(Image({{0, 1, 0}}, \"Bit\"), 1)}", //
        "{Byte,Byte,Bit}");
    check("ImageData(Dilation(b, 1), \"Byte\") == {{192, 255, 255}, {192, 255, 255}}", //
        "True");
  }

  @Test
  public void testMorphologicalComponents() {
    // pixels which touch at a corner are connected
    check("MorphologicalComponents(Image({{1., 0.}, {0., 1.}})) == {{1, 0}, {0, 1}}", //
        "True");
    check(
        "MorphologicalComponents(Image({{1., 0.}, {0., 1.}}), CornerNeighbors -> False) == "
            + "{{1, 0}, {0, 2}}", //
        "True");
    check(
        "MorphologicalComponents(Image({{1, 0, 1}, {1, 0, 1}, {1, 1, 1}}), "
            + "CornerNeighbors -> False) == {{1, 0, 1}, {1, 0, 1}, {1, 1, 1}}", //
        "True");
    // the components are numbered in the order they are met row by row
    check(
        "MorphologicalComponents(Image({{0, 1, 0, 1}, {0, 0, 0, 0}, {1, 0, 0, 1}})) == "
            + "{{0, 1, 0, 2}, {0, 0, 0, 0}, {3, 0, 0, 4}}", //
        "True");
    check(
        "MorphologicalComponents({{0, 1, 0, 1}, {0, 0, 0, 0}, {1, 0, 0, 1}}) == "
            + "{{0, 1, 0, 2}, {0, 0, 0, 0}, {3, 0, 0, 4}}", //
        "True");
    // every pixel greater than the threshold is foreground, and the threshold is 0 if not given
    check("MorphologicalComponents(Image({{0.2, 0.9}, {0.6, 0.1}})) == {{1, 1}, {1, 1}}", //
        "True");
    check("MorphologicalComponents(Image({{0.2, 0.9}, {0.6, 0.1}}), 0.5) == {{0, 1}, {1, 0}}", //
        "True");
  }

  @Test
  public void testDistanceTransform() {
    // the distances are in pixels, not scaled to 0...1
    check("dist = DistanceTransform(Image(ReplacePart(Table(1, {5}, {6}), {1, 1} -> 0))); "
        + "{ImageType(dist), Max(Abs(ImageData(dist) - " + "{{0., 1., 2., 3., 4., 5.}, "
        + "{1., 1.4142135381698608, 2.2360680103302, 3.1622776985168457, 4.123105525970459, "
        + "5.099019527435303}, "
        + "{2., 2.2360680103302, 2.8284270763397217, 3.605551242828369, 4.4721360206604, "
        + "5.385164737701416}, "
        + "{3., 3.1622776985168457, 3.605551242828369, 4.242640495300293, 5., 5.830951690673828}, "
        + "{4., 4.123105525970459, 4.4721360206604, 5., 5.656854152679443, 6.4031243324279785}})) "
        + "< 10^-6}", //
        "{Real32,True}");
    check(
        "Max(Abs(ImageData(DistanceTransform(Image({{0, 1, 1}, {1, 1, 1}}))) - "
            + "{{0., 1., 2.}, {1., 1.4142135381698608, 2.2360680103302}})) < 10^-6", //
        "True");
    // without a background pixel
    check(
        "ImageData(DistanceTransform(Image({{1, 1, 1}, {1, 1, 1}}))) == "
            + "{{1., 1., 1.}, {1., 1., 1.}}", //
        "True");
    check(
        "ImageData(DistanceTransform(Image({{0.2, 0.9, 0.9}, {0.6, 0.9, 0.1}}))) == "
            + "{{1., 1., 1.}, {1., 1., 1.}}", //
        "True");
  }

  @Test
  public void testDerivativeFilter() {
    // the derivative of the cubic spline through the samples, not a finite difference
    check(
        "slope = Image(Table(0.1*x, {y, 3}, {x, 6})); "
            + "Max(Abs(ImageData(DerivativeFilter(slope, {0, 1}))[[2]] - "
            + "{0.050069063901901245, 0.11313973367214203, 0.09737206995487213, "
            + "0.09737205505371094, 0.11313973367214203, 0.05006907880306244})) < 10^-7", //
        "True");
    check("Max(Abs(ImageData(DerivativeFilter(slope, {1, 0})))) < 10^-7", //
        "True");
    // a third argument is the standard deviation of a Gaussian
    check(
        "Max(Abs(ImageData(DerivativeFilter(slope, {0, 1}, 1))[[2]] - "
            + "{0.04999897629022598, 0.08620456606149673, 0.09859683364629745, "
            + "0.09859683364629745, 0.08620458096265793, 0.04999900236725807})) < 10^-7", //
        "True");
    check(
        "step = Image(Table(If(x > 4, 1., 0.), {y, 3}, {x, 8})); "
            + "Max(Abs(ImageData(DerivativeFilter(step, {0, 1}))[[2]] - "
            + "{-0.012196330353617668, 0.04551732540130615, -0.16987298429012299, "
            + "0.633974552154541, 0.633974552154541, -0.16987299919128418, 0.04551732540130615, "
            + "-0.01219630241394043})) < 10^-7", //
        "True");
  }

  @Test
  public void testGradientFilter() {
    check(
        "step = Image(Table(If(x > 4, 1., 0.), {y, 3}, {x, 8})); "
            + "Max(Abs(ImageData(GradientFilter(step, 1))[[2]] - "
            + "{0., 0., 0., 0.5, 0.5, 0., 0., 0.})) < 10^-7", //
        "True");
    check(
        "slope = Image(Table(0.1*x, {y, 3}, {x, 6})); "
            + "Max(Abs(ImageData(GradientFilter(slope, 1))[[2]] - "
            + "{0.05, 0.1, 0.1, 0.1, 0.1, 0.05})) < 10^-7", //
        "True");
    check(
        "b = Image({{0, 128, 255}, {64, 192, 32}}, \"Byte\"); "
            + "ImageType /@ {DerivativeFilter(b, {0, 1}), GradientFilter(b, 1)}", //
        "{Real32,Real32}");
  }
}

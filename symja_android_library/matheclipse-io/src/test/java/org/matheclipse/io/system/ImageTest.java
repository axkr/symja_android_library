package org.matheclipse.io.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.api.TensorUnaryOperator;
import org.matheclipse.core.tensor.img.ColorDataGradients;
import org.matheclipse.core.tensor.img.ImageCrop;

public class ImageTest extends AbstractTestCase {

  /**
   * The plots come from <code>matheclipse-core</code> even with this module on the classpath.
   *
   * <p>
   * They used to be replaced here by versions that returned a bitmap, so the same call gave a
   * different picture depending on the classpath. Anything that installs a second evaluator for a
   * plot symbol again turns these back into <code>Image(...)</code>.
   */
  @Test
  public void testPlotsAreGraphicsNotBitmaps() {
    check("Head(ArrayPlot({{1, 2}, {3, 4}}))", //
        "Graphics");
    check("Head(ListDensityPlot({{1, 2}, {3, 4}}))", //
        "Graphics");
  }

  @Test
  public void testArrayPlot() {
    check("Head(ArrayPlot(SparseArray({{1, 1} -> 0, {2, 2} -> 0} )))", //
        "Graphics");
    check("i=Image(ArrayPlot({{1, 0}, {0, 1}}))", //
        "Image(Dimensions: 600,600 Transparency: 3)");
    check("ImageDimensions(i)", //
        "{600,600}");
  }

  @Test
  public void testImageGray001() {
    check("i=Image({{0.1,0.2,0.3},{0.4,0.5,0.6},{0.7,0.8,0.9}})", //
        "Image(Dimensions: 3,3 Transparency: 1)");
    check("ImageDimensions(i)", //
        "{3,3}");
    check("ImageData(i)", //
        "{{0.1,0.2,0.3},\n" //
            + " {0.4,0.5,0.6},\n" //
            + " {0.7,0.8,0.9}}");
  }

  @Test
  public void testImageGrayNormalDistribution() {
    check("Image(RandomVariate(NormalDistribution(.5, .1), {100, 100}))", //
        "Image(Dimensions: 100,100 Transparency: 1)");
  }

  @Test
  public void testImageRGB001() {
    check(
        "i=Image({{{0.1,0.6,0.0},{0.4,0.1,0.8},{0.7,0.9,0.7}}, {{1.0,0.0,0.9},{0.6,0.6,1.0},{1.0,0.8,0.3}}},ColorSpace->\"RGB\")",
        // an RGB image without an alpha channel is opaque - it used to be stored as TYPE_INT_ARGB
        // whatever the data said, which made ImageChannels report 4 channels for a 3 channel matrix
        "Image(Dimensions: 3,2 Transparency: 1)");
    check("ImageDimensions(i)", //
        "{3,2}");
    check("d=ImageData(i);Dimensions(d)", //
        "{2,3,3}");
    check("d", //
        "{{{0.1,0.6,0.0},{0.4,0.1,0.8},{0.7,0.9,0.7}},{{1.0,0.0,0.9},{0.6,0.6,1.0},{1.0,0.8,0.3}}}");
  }

  @Test
  public void testImageRGB002() {
    check("Image(RandomReal(1, {4, 5, 3}))", //
        "Image(Dimensions: 5,4 Transparency: 1)");
  }

  @Test
  public void testColor() throws ClassNotFoundException {
    IAST image = F.List(F.List(0, 0, 0), F.List(0, 1, 0), F.List(0, 0, 0));
    image = image.mapLeaf(S.List, ColorDataGradients.CLASSIC);
    TensorUnaryOperator tensorUnaryOperator = ImageCrop.eq(image.getPart(1, 1));
    IAST result = tensorUnaryOperator.apply(image);
    assertEquals(result, F.List(F.List(F.List(255, 237, 237, 255))));
  }

  @Test
  public void testImageCrop() {
    EvalEngine engine = EvalEngine.get();
    IAST tensor = F.List(F.List(0, 0, 0), F.List(0, 1, 0), F.List(0, 0, 0));
    tensor = tensor.mapLeaf(S.List, ColorDataGradients.CLASSIC);
    IExpr image = S.Image.of(engine, tensor);
    IExpr croppedImage = S.ImageCrop.of(image, tensor.getPart(1, 1));
    // F.show(croppedImage);
    assertEquals("Image(Dimensions: 3,3 Transparency: 3)", croppedImage.toString());
  }
}

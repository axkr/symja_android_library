package org.matheclipse.core.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.api.TensorUnaryOperator;
import org.matheclipse.core.tensor.img.ColorDataGradients;
import org.matheclipse.core.tensor.img.ImageCrop;

public class ImageTest extends ExprEvaluatorTestCase {

  public ImageTest(String name) {
    super(name);
  }

  public void testImageGray001() {
    check("Image({{0.1,0.2,0.3},{0.4,0.5,0.6},{0.7,0.8,0.9}})", //
        "Image(Dimensions: 3,3 Transparency: 1)");
  }

  public void testImageGrayNormalDistribution() {
    check("Image(RandomVariate(NormalDistribution(.5, .1), {100, 100}))", //
        "Image(Dimensions: 100,100 Transparency: 1)");
  }

  public void testImageRGB001() {
    check("Image({{{0.1,0.6,0.0},{0.4,0.1,0.8},{0.7,0.9,0.7}}," //
        + "{{1.0,0.0,0.9},{0.6,0.6,1.0},{1.0,0.8,0.3}}}," + "ColorSpace->RGB)",
        "Image(Dimensions: 3,2 Transparency: 3)");
  }

  public void testImageRGB002() {
    check("Image(RandomReal(1, {4, 5, 3}))", //
        "Image(Dimensions: 5,4 Transparency: 3)");
  }

  public void testColor() throws ClassNotFoundException {
    IAST image = F.List(F.List(0, 0, 0), F.List(0, 1, 0), F.List(0, 0, 0));
    image = image.mapLeaf(S.List, ColorDataGradients.CLASSIC);
    TensorUnaryOperator tensorUnaryOperator = ImageCrop.eq(image.getPart(1, 1));
    IAST result = tensorUnaryOperator.apply(image);
    assertEquals(result, F.List(F.List(F.List(255, 237, 237, 255))));
  }

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
